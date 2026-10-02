package dao;

import database.DatabaseConnection;
import model.Medicine;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Medicine records in the MySQL database.
 * Uses prepared statements and try-with-resources.
 */
public class MedicineDAO {

    /**
     * Inserts a new medicine record into the database.
     */
    public boolean addMedicine(Medicine medicine) throws SQLException {
        String sql = "INSERT INTO medicines (" +
                "medicine_id, medicine_name, generic_name, category, manufacturer, " +
                "dosage, dosage_form, description, uses, prescription_required, " +
                "price, stock_quantity, manufacturing_date, expiry_date, batch_number, storage_instructions" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            setMedicineStatementParameters(stmt, medicine);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Updates an existing medicine record.
     */
    public boolean updateMedicine(Medicine medicine) throws SQLException {
        String sql = "UPDATE medicines SET " +
                "medicine_name = ?, generic_name = ?, category = ?, manufacturer = ?, " +
                "dosage = ?, dosage_form = ?, description = ?, uses = ?, prescription_required = ?, " +
                "price = ?, stock_quantity = ?, manufacturing_date = ?, expiry_date = ?, " +
                "batch_number = ?, storage_instructions = ? " +
                "WHERE medicine_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, medicine.getMedicineName());
            stmt.setString(2, medicine.getGenericName());
            stmt.setString(3, medicine.getCategory());
            stmt.setString(4, medicine.getManufacturer());
            stmt.setString(5, medicine.getDosage());
            stmt.setString(6, medicine.getDosageForm());
            stmt.setString(7, medicine.getDescription());
            stmt.setString(8, medicine.getUses());
            stmt.setInt(9, medicine.isPrescriptionRequired() ? 1 : 0);
            stmt.setDouble(10, medicine.getPrice());
            stmt.setInt(11, medicine.getStockQuantity());
            stmt.setDate(12, Date.valueOf(medicine.getManufacturingDate()));
            stmt.setDate(13, Date.valueOf(medicine.getExpiryDate()));
            stmt.setString(14, medicine.getBatchNumber());
            stmt.setString(15, medicine.getStorageInstructions());
            stmt.setString(16, medicine.getMedicineId());

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Deletes a medicine record by ID.
     */
    public boolean deleteMedicine(String medicineId) throws SQLException {
        String sql = "DELETE FROM medicines WHERE medicine_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, medicineId);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Checks if a medicine ID already exists in the database.
     */
    public boolean medicineIdExists(String medicineId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM medicines WHERE medicine_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, medicineId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * Retrieves all medicines ordered by medicine name.
     */
    public List<Medicine> getAllMedicines() throws SQLException {
        List<Medicine> list = new ArrayList<>();
        String sql = "SELECT * FROM medicines ORDER BY medicine_name ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapRowToMedicine(rs));
            }
        }
        return list;
    }

    /**
     * Finds a single medicine by its unique ID.
     */
    public Medicine getMedicineById(String medicineId) throws SQLException {
        String sql = "SELECT * FROM medicines WHERE medicine_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, medicineId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToMedicine(rs);
                }
            }
        }
        return null;
    }

    /**
     * Searches medicines by query (medicine_name, generic_name, manufacturer, or category)
     * with optional category filtering. Supports partial, case-insensitive matches.
     */
    public List<Medicine> searchMedicines(String query, String category) throws SQLException {
        List<Medicine> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM medicines WHERE 1=1 ");

        boolean hasQuery = query != null && !query.trim().isEmpty();
        boolean hasCategory = category != null && !category.trim().isEmpty() && !category.equalsIgnoreCase("All Categories");

        if (hasQuery) {
            sql.append("AND (LOWER(medicine_name) LIKE ? OR LOWER(generic_name) LIKE ? OR LOWER(category) LIKE ? OR LOWER(manufacturer) LIKE ?) ");
        }
        if (hasCategory) {
            sql.append("AND LOWER(category) = ? ");
        }
        sql.append("ORDER BY medicine_name ASC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (hasQuery) {
                String pattern = "%" + query.trim().toLowerCase() + "%";
                stmt.setString(paramIndex++, pattern);
                stmt.setString(paramIndex++, pattern);
                stmt.setString(paramIndex++, pattern);
                stmt.setString(paramIndex++, pattern);
            }
            if (hasCategory) {
                stmt.setString(paramIndex++, category.trim().toLowerCase());
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToMedicine(rs));
                }
            }
        }
        return list;
    }

    /**
     * Retrieves medicines by category.
     */
    public List<Medicine> getMedicinesByCategory(String category) throws SQLException {
        List<Medicine> list = new ArrayList<>();
        String sql = "SELECT * FROM medicines WHERE LOWER(category) = LOWER(?) ORDER BY medicine_name ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, category);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToMedicine(rs));
                }
            }
        }
        return list;
    }

    /**
     * Retrieves low stock medicines (stock quantity > 0 and < 10).
     */
    public List<Medicine> getLowStockMedicines() throws SQLException {
        List<Medicine> list = new ArrayList<>();
        String sql = "SELECT * FROM medicines WHERE stock_quantity > 0 AND stock_quantity < 10 ORDER BY stock_quantity ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapRowToMedicine(rs));
            }
        }
        return list;
    }

    /**
     * Retrieves expired medicines (expiry_date < CURRENT_DATE).
     */
    public List<Medicine> getExpiredMedicines() throws SQLException {
        List<Medicine> list = new ArrayList<>();
        String sql = "SELECT * FROM medicines WHERE expiry_date < CURRENT_DATE() ORDER BY expiry_date ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapRowToMedicine(rs));
            }
        }
        return list;
    }

    /**
     * Retrieves medicines expiring within the next 60 days (and not already expired).
     */
    public List<Medicine> getExpiringSoonMedicines() throws SQLException {
        List<Medicine> list = new ArrayList<>();
        String sql = "SELECT * FROM medicines WHERE expiry_date >= CURRENT_DATE() AND expiry_date <= DATE_ADD(CURRENT_DATE(), INTERVAL 60 DAY) ORDER BY expiry_date ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapRowToMedicine(rs));
            }
        }
        return list;
    }

    /**
     * Updates the stock quantity of a specific medicine.
     */
    public boolean updateStock(String medicineId, int newQuantity) throws SQLException {
        String sql = "UPDATE medicines SET stock_quantity = ? WHERE medicine_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, newQuantity);
            stmt.setString(2, medicineId);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Returns total count of all medicines.
     */
    public int getTotalMedicineCount() throws SQLException {
        return getCountBySql("SELECT COUNT(*) FROM medicines");
    }

    /**
     * Returns count of available medicines (stock >= 10 and not expired).
     */
    public int getAvailableMedicineCount() throws SQLException {
        return getCountBySql("SELECT COUNT(*) FROM medicines WHERE stock_quantity >= 10 AND expiry_date >= CURRENT_DATE()");
    }

    /**
     * Returns count of out of stock medicines (stock = 0).
     */
    public int getOutOfStockCount() throws SQLException {
        return getCountBySql("SELECT COUNT(*) FROM medicines WHERE stock_quantity = 0");
    }

    /**
     * Returns count of low stock medicines (stock > 0 and stock < 10).
     */
    public int getLowStockCount() throws SQLException {
        return getCountBySql("SELECT COUNT(*) FROM medicines WHERE stock_quantity > 0 AND stock_quantity < 10");
    }

    /**
     * Returns count of expired medicines (expiry_date < CURRENT_DATE).
     */
    public int getExpiredMedicineCount() throws SQLException {
        return getCountBySql("SELECT COUNT(*) FROM medicines WHERE expiry_date < CURRENT_DATE()");
    }

    /**
     * Retrieves distinct categories present in the repository.
     */
    public List<String> getAllCategories() throws SQLException {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT DISTINCT category FROM medicines ORDER BY category ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                categories.add(rs.getString("category"));
            }
        }
        return categories;
    }

    private int getCountBySql(String sql) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    private void setMedicineStatementParameters(PreparedStatement stmt, Medicine m) throws SQLException {
        stmt.setString(1, m.getMedicineId());
        stmt.setString(2, m.getMedicineName());
        stmt.setString(3, m.getGenericName());
        stmt.setString(4, m.getCategory());
        stmt.setString(5, m.getManufacturer());
        stmt.setString(6, m.getDosage());
        stmt.setString(7, m.getDosageForm());
        stmt.setString(8, m.getDescription());
        stmt.setString(9, m.getUses());
        stmt.setInt(10, m.isPrescriptionRequired() ? 1 : 0);
        stmt.setDouble(11, m.getPrice());
        stmt.setInt(12, m.getStockQuantity());
        stmt.setDate(13, Date.valueOf(m.getManufacturingDate()));
        stmt.setDate(14, Date.valueOf(m.getExpiryDate()));
        stmt.setString(15, m.getBatchNumber());
        stmt.setString(16, m.getStorageInstructions());
    }

    private Medicine mapRowToMedicine(ResultSet rs) throws SQLException {
        String id = rs.getString("medicine_id");
        String name = rs.getString("medicine_name");
        String generic = rs.getString("generic_name");
        String category = rs.getString("category");
        String manufacturer = rs.getString("manufacturer");
        String dosage = rs.getString("dosage");
        String dosageForm = rs.getString("dosage_form");
        String description = rs.getString("description");
        String uses = rs.getString("uses");
        boolean prescriptionRequired = rs.getInt("prescription_required") == 1;
        double price = rs.getDouble("price");
        int stock = rs.getInt("stock_quantity");
        Date mfgSql = rs.getDate("manufacturing_date");
        Date expSql = rs.getDate("expiry_date");
        LocalDate mfg = (mfgSql != null) ? mfgSql.toLocalDate() : LocalDate.now();
        LocalDate exp = (expSql != null) ? expSql.toLocalDate() : LocalDate.now();
        String batchNumber = rs.getString("batch_number");
        String storageInstructions = rs.getString("storage_instructions");

        return new Medicine(id, name, generic, category, manufacturer, dosage, dosageForm,
                description, uses, prescriptionRequired, price, stock, mfg, exp,
                batchNumber, storageInstructions);
    }
}
