package ui;

import model.Medicine;
import service.MedicineService;
import util.DateUtil;
import util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Add Medicine modal frame allowing administrators to register new medicine inventory.
 */
public class AddMedicineFrame extends JFrame {

    private final MedicineService medicineService;
    private final Runnable onSuccessCallback;

    private JTextField idField;
    private JTextField nameField;
    private JTextField genericField;
    private JComboBox<String> categoryCombo;
    private JTextField manufacturerField;
    private JTextField dosageField;
    private JComboBox<String> dosageFormCombo;
    private JTextArea descriptionArea;
    private JTextArea usesArea;
    private JCheckBox rxCheckBox;
    private JTextField priceField;
    private JTextField stockField;
    private JTextField mfgDateField;
    private JTextField expDateField;
    private JTextField batchField;
    private JTextField storageField;

    private JButton saveButton;
    private JButton cancelButton;

    public AddMedicineFrame(Runnable onSuccessCallback) {
        this.medicineService = new MedicineService();
        this.onSuccessCallback = onSuccessCallback;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Medicine Repository - Add New Medicine");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(720, 750);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(18, 24, 18, 24));
        mainPanel.setBackground(new Color(248, 250, 252));

        // Header
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 2, 2));
        headerPanel.setBackground(new Color(248, 250, 252));
        JLabel titleLabel = new JLabel("Register New Medicine Record");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(15, 23, 42));

        JLabel subLabel = new JLabel("All fields marked with an asterisk (*) are mandatory");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subLabel.setForeground(new Color(100, 116, 139));

        headerPanel.add(titleLabel);
        headerPanel.add(subLabel);

        // Form Panel in ScrollPane
        JPanel formContainer = new JPanel();
        formContainer.setLayout(new BoxLayout(formContainer, BoxLayout.Y_AXIS));
        formContainer.setBackground(new Color(248, 250, 252));

        // Grid for short fields
        JPanel grid = new JPanel(new GridLayout(8, 2, 14, 12));
        grid.setBackground(new Color(248, 250, 252));

        idField = new JTextField();
        nameField = new JTextField();
        genericField = new JTextField();

        String[] categories = {
                "Pain Relief", "Antibiotics", "Antihistamines", "Antacids",
                "Diabetes", "Blood Pressure", "Vitamins", "Respiratory",
                "Gastrointestinal", "Cardiovascular"
        };
        categoryCombo = new JComboBox<>(categories);
        categoryCombo.setEditable(true);

        manufacturerField = new JTextField();
        dosageField = new JTextField();

        String[] dosageForms = {"Tablet", "Capsule", "Oral Suspension", "Inhaler", "Gel", "Injection", "Nasal Spray", "Chewable Tablet", "Softgel", "Oral Powder"};
        dosageFormCombo = new JComboBox<>(dosageForms);
        dosageFormCombo.setEditable(true);

        priceField = new JTextField("0.00");
        stockField = new JTextField("10");

        mfgDateField = new JTextField(DateUtil.formatIso(LocalDate.now().minusMonths(3)));
        expDateField = new JTextField(DateUtil.formatIso(LocalDate.now().plusYears(2)));
        batchField = new JTextField("BN-");
        storageField = new JTextField("Store at room temperature 15°C to 25°C.");

        rxCheckBox = new JCheckBox("Prescription Required (Rx Only)");
        rxCheckBox.setFont(new Font("Segoe UI", Font.BOLD, 12));
        rxCheckBox.setBackground(new Color(248, 250, 252));

        addGridField(grid, "Medicine ID * (e.g. MED-1050)", idField);
        addGridField(grid, "Medicine Name *", nameField);
        addGridField(grid, "Generic Name *", genericField);
        addGridField(grid, "Category *", categoryCombo);
        addGridField(grid, "Manufacturer *", manufacturerField);
        addGridField(grid, "Dosage * (e.g. 500 mg)", dosageField);
        addGridField(grid, "Dosage Form *", dosageFormCombo);
        addGridField(grid, "Price ($) *", priceField);
        addGridField(grid, "Stock Quantity *", stockField);
        addGridField(grid, "Manufacturing Date * (yyyy-MM-dd)", mfgDateField);
        addGridField(grid, "Expiry Date * (yyyy-MM-dd)", expDateField);
        addGridField(grid, "Batch Number *", batchField);
        addGridField(grid, "Storage Instructions *", storageField);
        addGridField(grid, "Regulatory Classification", rxCheckBox);

        formContainer.add(grid);
        formContainer.add(Box.createVerticalStrut(10));

        // Text Areas
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        formContainer.add(createLabeledArea("Description / Pharmacological Action *", descriptionArea));
        formContainer.add(Box.createVerticalStrut(8));

        usesArea = new JTextArea(2, 20);
        usesArea.setLineWrap(true);
        usesArea.setWrapStyleWord(true);
        formContainer.add(createLabeledArea("Indications / Medical Uses *", usesArea));

        JScrollPane scrollPane = new JScrollPane(formContainer);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        scrollPane.getVerticalScrollBar().setUnitIncrement(14);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(new Color(248, 250, 252));

        cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cancelButton.setBackground(new Color(241, 245, 249));
        cancelButton.setFocusPainted(false);
        cancelButton.addActionListener(e -> dispose());

        saveButton = new JButton("Save Medicine");
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        saveButton.setBackground(new Color(14, 116, 144));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setPreferredSize(new Dimension(140, 36));
        saveButton.addActionListener(e -> saveMedicine());

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        setContentPane(mainPanel);
    }

    private void addGridField(JPanel panel, String label, JComponent field) {
        JPanel box = new JPanel(new BorderLayout(4, 2));
        box.setBackground(new Color(248, 250, 252));
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(51, 65, 85));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        box.add(lbl, BorderLayout.NORTH);
        box.add(field, BorderLayout.CENTER);
        panel.add(box);
    }

    private JPanel createLabeledArea(String label, JTextArea area) {
        JPanel panel = new JPanel(new BorderLayout(4, 2));
        panel.setBackground(new Color(248, 250, 252));
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(51, 65, 85));
        area.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JScrollPane sp = new JScrollPane(area);
        panel.add(lbl, BorderLayout.NORTH);
        panel.add(sp, BorderLayout.CENTER);
        return panel;
    }

    private void saveMedicine() {
        try {
            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            String generic = genericField.getText().trim();
            String category = (String) categoryCombo.getSelectedItem();
            String manufacturer = manufacturerField.getText().trim();
            String dosage = dosageField.getText().trim();
            String dosageForm = (String) dosageFormCombo.getSelectedItem();
            String description = descriptionArea.getText().trim();
            String uses = usesArea.getText().trim();
            boolean rx = rxCheckBox.isSelected();
            String batch = batchField.getText().trim();
            String storage = storageField.getText().trim();

            if (ValidationUtil.isEmpty(id)) throw new IllegalArgumentException("Medicine ID is required.");
            if (ValidationUtil.isEmpty(name)) throw new IllegalArgumentException("Medicine Name is required.");
            if (ValidationUtil.isEmpty(generic)) throw new IllegalArgumentException("Generic Name is required.");
            if (ValidationUtil.isEmpty(category)) throw new IllegalArgumentException("Category is required.");
            if (ValidationUtil.isEmpty(manufacturer)) throw new IllegalArgumentException("Manufacturer is required.");
            if (ValidationUtil.isEmpty(dosage)) throw new IllegalArgumentException("Dosage is required.");
            if (ValidationUtil.isEmpty(dosageForm)) throw new IllegalArgumentException("Dosage Form is required.");
            if (ValidationUtil.isEmpty(batch)) throw new IllegalArgumentException("Batch Number is required.");
            if (ValidationUtil.isEmpty(storage)) throw new IllegalArgumentException("Storage instructions are required.");

            if (!ValidationUtil.isValidPrice(priceField.getText())) {
                throw new IllegalArgumentException("Price must be a non-negative number.");
            }
            double price = Double.parseDouble(priceField.getText().trim());

            if (!ValidationUtil.isValidStock(stockField.getText())) {
                throw new IllegalArgumentException("Stock quantity must be a non-negative whole integer.");
            }
            int stock = Integer.parseInt(stockField.getText().trim());

            LocalDate mfgDate = DateUtil.parseDate(mfgDateField.getText());
            if (mfgDate == null) {
                throw new IllegalArgumentException("Invalid manufacturing date format (please use yyyy-MM-dd).");
            }

            LocalDate expDate = DateUtil.parseDate(expDateField.getText());
            if (expDate == null) {
                throw new IllegalArgumentException("Invalid expiry date format (please use yyyy-MM-dd).");
            }

            if (!expDate.isAfter(mfgDate)) {
                throw new IllegalArgumentException("Expiry date must be strictly after the manufacturing date.");
            }

            Medicine medicine = new Medicine(id, name, generic, category, manufacturer, dosage, dosageForm,
                    description, uses, rx, price, stock, mfgDate, expDate, batch, storage);

            medicineService.addMedicine(medicine);

            JOptionPane.showMessageDialog(this,
                    "Medicine '" + name + "' added successfully to the repository!",
                    "Medicine Added",
                    JOptionPane.INFORMATION_MESSAGE);

            dispose();
            if (onSuccessCallback != null) {
                onSuccessCallback.run();
            }

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database error while saving medicine:\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Unexpected error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
