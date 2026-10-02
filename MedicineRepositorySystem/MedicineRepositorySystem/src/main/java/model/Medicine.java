package model;

import java.time.LocalDate;

/**
 * Represents a medicine item stored within the Medicine Repository System.
 * Encapsulates clinical, inventory, manufacturing, and regulatory details.
 */
public class Medicine {
    private String medicineId;
    private String medicineName;
    private String genericName;
    private String category;
    private String manufacturer;
    private String dosage;
    private String dosageForm;
    private String description;
    private String uses;
    private boolean prescriptionRequired;
    private double price;
    private int stockQuantity;
    private LocalDate manufacturingDate;
    private LocalDate expiryDate;
    private String batchNumber;
    private String storageInstructions;

    // Default Constructor
    public Medicine() {
    }

    // Full Parameterized Constructor
    public Medicine(String medicineId, String medicineName, String genericName, String category,
                    String manufacturer, String dosage, String dosageForm, String description,
                    String uses, boolean prescriptionRequired, double price, int stockQuantity,
                    LocalDate manufacturingDate, LocalDate expiryDate, String batchNumber,
                    String storageInstructions) {
        this.medicineId = medicineId;
        this.medicineName = medicineName;
        this.genericName = genericName;
        this.category = category;
        this.manufacturer = manufacturer;
        this.dosage = dosage;
        this.dosageForm = dosageForm;
        this.description = description;
        this.uses = uses;
        this.prescriptionRequired = prescriptionRequired;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.manufacturingDate = manufacturingDate;
        this.expiryDate = expiryDate;
        this.batchNumber = batchNumber;
        this.storageInstructions = storageInstructions;
    }

    // Getters and Setters
    public String getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(String medicineId) {
        this.medicineId = medicineId;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public String getGenericName() {
        return genericName;
    }

    public void setGenericName(String genericName) {
        this.genericName = genericName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getDosageForm() {
        return dosageForm;
    }

    public void setDosageForm(String dosageForm) {
        this.dosageForm = dosageForm;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUses() {
        return uses;
    }

    public void setUses(String uses) {
        this.uses = uses;
    }

    public boolean isPrescriptionRequired() {
        return prescriptionRequired;
    }

    public void setPrescriptionRequired(boolean prescriptionRequired) {
        this.prescriptionRequired = prescriptionRequired;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public LocalDate getManufacturingDate() {
        return manufacturingDate;
    }

    public void setManufacturingDate(LocalDate manufacturingDate) {
        this.manufacturingDate = manufacturingDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public String getStorageInstructions() {
        return storageInstructions;
    }

    public void setStorageInstructions(String storageInstructions) {
        this.storageInstructions = storageInstructions;
    }

    /**
     * Determines the stock status according to business logic:
     * Stock Quantity = 0       -> OUT OF STOCK
     * Stock Quantity below 10  -> LOW STOCK
     * Stock Quantity 10 or more -> AVAILABLE
     */
    public String getStockStatus() {
        if (stockQuantity <= 0) {
            return "OUT OF STOCK";
        } else if (stockQuantity < 10) {
            return "LOW STOCK";
        } else {
            return "AVAILABLE";
        }
    }

    /**
     * Checks if the medicine has passed its expiration date.
     */
    public boolean isExpired() {
        if (expiryDate == null) {
            return false;
        }
        return expiryDate.isBefore(LocalDate.now());
    }

    /**
     * Checks if the medicine will expire within the given threshold in days.
     */
    public boolean isExpiringSoon(int daysThreshold) {
        if (expiryDate == null || isExpired()) {
            return false;
        }
        LocalDate thresholdDate = LocalDate.now().plusDays(daysThreshold);
        return !expiryDate.isAfter(thresholdDate);
    }

    /**
     * Default expiring soon check (within 60 days).
     */
    public boolean isExpiringSoon() {
        return isExpiringSoon(60);
    }

    /**
     * Formatted string showing effective operational status,
     * ensuring expired medicines are not presented as normally available.
     */
    public String getEffectiveStatus() {
        if (isExpired()) {
            return "EXPIRED";
        }
        return getStockStatus();
    }

    @Override
    public String toString() {
        return "Medicine{" +
                "id='" + medicineId + '\'' +
                ", name='" + medicineName + '\'' +
                ", category='" + category + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", price=" + price +
                ", stock=" + stockQuantity +
                ", status=" + getEffectiveStatus() +
                '}';
    }
}
