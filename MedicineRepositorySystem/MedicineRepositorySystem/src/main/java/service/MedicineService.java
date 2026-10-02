package service;

import dao.MedicineDAO;
import dao.UserDAO;
import model.Medicine;
import util.ValidationUtil;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service layer coordinating medicine inventory logic, validation rules,
 * search querying, stock adjustments, and dashboard metrics.
 */
public class MedicineService {

    private final MedicineDAO medicineDAO;
    private final UserDAO userDAO;

    public MedicineService() {
        this.medicineDAO = new MedicineDAO();
        this.userDAO = new UserDAO();
    }

    public MedicineService(MedicineDAO medicineDAO, UserDAO userDAO) {
        this.medicineDAO = medicineDAO;
        this.userDAO = userDAO;
    }

    /**
     * Validates and inserts a new medicine into the repository.
     */
    public void addMedicine(Medicine medicine) throws SQLException {
        validateMedicineFields(medicine, true);

        if (medicineDAO.medicineIdExists(medicine.getMedicineId())) {
            throw new IllegalArgumentException("A medicine with ID '" + medicine.getMedicineId() + "' already exists.");
        }

        boolean success = medicineDAO.addMedicine(medicine);
        if (!success) {
            throw new SQLException("Failed to insert medicine into database.");
        }
    }

    /**
     * Validates and updates an existing medicine.
     */
    public void updateMedicine(Medicine medicine) throws SQLException {
        validateMedicineFields(medicine, false);

        if (!medicineDAO.medicineIdExists(medicine.getMedicineId())) {
            throw new IllegalArgumentException("Medicine with ID '" + medicine.getMedicineId() + "' does not exist.");
        }

        boolean success = medicineDAO.updateMedicine(medicine);
        if (!success) {
            throw new SQLException("Failed to update medicine in database.");
        }
    }

    /**
     * Deletes a medicine by ID.
     */
    public void deleteMedicine(String medicineId) throws SQLException {
        if (ValidationUtil.isEmpty(medicineId)) {
            throw new IllegalArgumentException("Please select a medicine record first.");
        }

        boolean success = medicineDAO.deleteMedicine(medicineId);
        if (!success) {
            throw new SQLException("Could not delete medicine with ID: " + medicineId);
        }
    }

    /**
     * Retrieves all medicines.
     */
    public List<Medicine> getAllMedicines() throws SQLException {
        return medicineDAO.getAllMedicines();
    }

    /**
     * Finds a medicine by ID.
     */
    public Medicine getMedicineById(String medicineId) throws SQLException {
        if (ValidationUtil.isEmpty(medicineId)) {
            throw new IllegalArgumentException("Medicine ID cannot be empty.");
        }
        Medicine medicine = medicineDAO.getMedicineById(medicineId);
        if (medicine == null) {
            throw new IllegalArgumentException("Medicine not found. Please try another medicine ID.");
        }
        return medicine;
    }

    /**
     * Searches medicines by name, generic name, category, or manufacturer.
     */
    public List<Medicine> searchMedicines(String query, String category) throws SQLException {
        return medicineDAO.searchMedicines(query, category);
    }

    /**
     * Retrieves medicines by category.
     */
    public List<Medicine> getMedicinesByCategory(String category) throws SQLException {
        if (ValidationUtil.isEmpty(category)) {
            return getAllMedicines();
        }
        return medicineDAO.getMedicinesByCategory(category);
    }

    /**
     * Retrieves low stock medicines (stock > 0 and stock < 10).
     */
    public List<Medicine> getLowStockMedicines() throws SQLException {
        return medicineDAO.getLowStockMedicines();
    }

    /**
     * Retrieves expired medicines.
     */
    public List<Medicine> getExpiredMedicines() throws SQLException {
        return medicineDAO.getExpiredMedicines();
    }

    /**
     * Retrieves medicines expiring soon (within next 60 days).
     */
    public List<Medicine> getExpiringSoonMedicines() throws SQLException {
        return medicineDAO.getExpiringSoonMedicines();
    }

    /**
     * Adjusts the stock quantity for a medicine.
     */
    public void updateStock(String medicineId, int newQuantity) throws SQLException {
        if (ValidationUtil.isEmpty(medicineId)) {
            throw new IllegalArgumentException("Please select a medicine record first.");
        }
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative.");
        }

        boolean success = medicineDAO.updateStock(medicineId, newQuantity);
        if (!success) {
            throw new SQLException("Failed to update stock quantity.");
        }
    }

    /**
     * Retrieves distinct categories for filtering dropdowns.
     */
    public List<String> getAllCategories() throws SQLException {
        return medicineDAO.getAllCategories();
    }

    /**
     * Compiles live calculated dashboard statistics directly from the database.
     */
    public Map<String, Integer> getDashboardStatistics() throws SQLException {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("totalMedicines", medicineDAO.getTotalMedicineCount());
        stats.put("availableMedicines", medicineDAO.getAvailableMedicineCount());
        stats.put("outOfStockMedicines", medicineDAO.getOutOfStockCount());
        stats.put("lowStockMedicines", medicineDAO.getLowStockCount());
        stats.put("expiredMedicines", medicineDAO.getExpiredMedicineCount());
        stats.put("registeredUsers", userDAO.getUserCount());
        return stats;
    }

    /**
     * Comprehensive validation routine for medicine entities.
     */
    private void validateMedicineFields(Medicine m, boolean isNew) {
        if (m == null) {
            throw new IllegalArgumentException("Medicine data cannot be null.");
        }
        if (ValidationUtil.isEmpty(m.getMedicineId())) {
            throw new IllegalArgumentException("Medicine ID cannot be empty.");
        }
        if (ValidationUtil.isEmpty(m.getMedicineName())) {
            throw new IllegalArgumentException("Medicine name cannot be empty.");
        }
        if (ValidationUtil.isEmpty(m.getGenericName())) {
            throw new IllegalArgumentException("Generic name cannot be empty.");
        }
        if (ValidationUtil.isEmpty(m.getCategory())) {
            throw new IllegalArgumentException("Category cannot be empty.");
        }
        if (ValidationUtil.isEmpty(m.getManufacturer())) {
            throw new IllegalArgumentException("Manufacturer cannot be empty.");
        }
        if (ValidationUtil.isEmpty(m.getDosage())) {
            throw new IllegalArgumentException("Dosage cannot be empty.");
        }
        if (ValidationUtil.isEmpty(m.getDosageForm())) {
            throw new IllegalArgumentException("Dosage form cannot be empty.");
        }
        if (m.getPrice() < 0) {
            throw new IllegalArgumentException("Price cannot be negative.");
        }
        if (m.getStockQuantity() < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative.");
        }
        if (m.getManufacturingDate() == null) {
            throw new IllegalArgumentException("Manufacturing date must be a valid date.");
        }
        if (m.getExpiryDate() == null) {
            throw new IllegalArgumentException("Expiry date must be a valid date.");
        }
        if (!m.getExpiryDate().isAfter(m.getManufacturingDate())) {
            throw new IllegalArgumentException("Expiry date must be strictly after the manufacturing date.");
        }
        if (ValidationUtil.isEmpty(m.getBatchNumber())) {
            throw new IllegalArgumentException("Batch number cannot be empty.");
        }
    }
}
