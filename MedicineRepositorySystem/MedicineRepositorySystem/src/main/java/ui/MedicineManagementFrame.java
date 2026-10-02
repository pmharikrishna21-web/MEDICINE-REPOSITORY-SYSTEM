package ui;

import model.Medicine;
import service.MedicineService;
import util.DateUtil;
import util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Medicine Management Screen providing full administrative controls:
 * viewing records, searching, adding, editing, deleting with confirmation,
 * quick stock updates, and viewing full clinical profiles.
 */
public class MedicineManagementFrame extends JFrame {

    private final MedicineService medicineService;
    private final Runnable onDataChangedCallback;

    private JTextField searchField;
    private JComboBox<String> categoryComboBox;
    private JButton searchButton;
    private JButton resetButton;

    private JTable medicineTable;
    private DefaultTableModel tableModel;
    private List<Medicine> currentList = new ArrayList<>();

    private JButton addBtn;
    private JButton updateBtn;
    private JButton deleteBtn;
    private JButton updateStockBtn;
    private JButton viewDetailsBtn;
    private JButton refreshBtn;
    private JButton closeBtn;
    private JLabel countLabel;

    public MedicineManagementFrame(Runnable onDataChangedCallback) {
        this.medicineService = new MedicineService();
        this.onDataChangedCallback = onDataChangedCallback;
        initializeUI();
        loadCategories();
        loadData(null, "All Categories");
    }

    private void initializeUI() {
        setTitle("Medicine Repository - Inventory & Medicine Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1150, 720);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(14, 14));
        mainPanel.setBorder(new EmptyBorder(18, 22, 18, 22));
        mainPanel.setBackground(new Color(248, 250, 252));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(248, 250, 252));

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 2, 2));
        titleBox.setBackground(new Color(248, 250, 252));
        JLabel titleLbl = new JLabel("Medicine Inventory Management");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLbl.setForeground(new Color(15, 23, 42));

        JLabel subLbl = new JLabel("Manage stock, update records, and maintain clinical pharmaceutical specifications");
        subLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subLbl.setForeground(new Color(100, 116, 139));
        titleBox.add(titleLbl);
        titleBox.add(subLbl);

        refreshBtn = new JButton("Refresh Table");
        refreshBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        refreshBtn.setBackground(new Color(241, 245, 249));
        refreshBtn.setFocusPainted(false);
        refreshBtn.addActionListener(e -> reloadCurrentView());

        headerPanel.add(titleBox, BorderLayout.WEST);
        headerPanel.add(refreshBtn, BorderLayout.EAST);

        // Search & Filter Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        filterPanel.setBackground(new Color(241, 245, 249));
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                new EmptyBorder(6, 10, 6, 10)
        ));

        JLabel searchLbl = new JLabel("Search:");
        searchLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        searchField = new JTextField(22);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JLabel catLbl = new JLabel("Category:");
        catLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        categoryComboBox = new JComboBox<>(new String[]{"All Categories"});
        categoryComboBox.setPreferredSize(new Dimension(170, 30));

        searchButton = new JButton("Search");
        searchButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        searchButton.setBackground(new Color(14, 116, 144));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);

        resetButton = new JButton("Reset");
        resetButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        resetButton.setBackground(Color.WHITE);
        resetButton.setFocusPainted(false);

        filterPanel.add(searchLbl);
        filterPanel.add(searchField);
        filterPanel.add(catLbl);
        filterPanel.add(categoryComboBox);
        filterPanel.add(searchButton);
        filterPanel.add(resetButton);

        JPanel northGroup = new JPanel(new BorderLayout(8, 12));
        northGroup.setBackground(new Color(248, 250, 252));
        northGroup.add(headerPanel, BorderLayout.NORTH);
        northGroup.add(filterPanel, BorderLayout.SOUTH);

        // Table
        String[] columns = {
                "Medicine ID", "Medicine Name", "Generic Name", "Category",
                "Manufacturer", "Dosage", "Price ($)", "Stock", "Status", "Expiry Date"
        };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        medicineTable = new JTable(tableModel);
        medicineTable.setRowHeight(30);
        medicineTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        medicineTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        medicineTable.getTableHeader().setBackground(new Color(241, 245, 249));
        medicineTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Status column styling
        medicineTable.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value != null && !isSelected) {
                    String status = value.toString();
                    if (status.equalsIgnoreCase("OUT OF STOCK") || status.equalsIgnoreCase("EXPIRED")) {
                        setForeground(new Color(185, 28, 28));
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else if (status.equalsIgnoreCase("LOW STOCK")) {
                        setForeground(new Color(180, 83, 9));
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else {
                        setForeground(new Color(21, 128, 61));
                        setFont(getFont().deriveFont(Font.PLAIN));
                    }
                } else if (!isSelected) {
                    setForeground(Color.BLACK);
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(medicineTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));

        medicineTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && medicineTable.getSelectedRow() != -1) {
                    openDetails();
                }
            }
        });

        // Bottom Operations Bar
        JPanel southPanel = new JPanel(new BorderLayout(10, 10));
        southPanel.setBackground(new Color(248, 250, 252));

        countLabel = new JLabel("Total records: 0");
        countLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        countLabel.setForeground(new Color(100, 116, 139));

        JPanel actionBtnGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionBtnGroup.setBackground(new Color(248, 250, 252));

        addBtn = new JButton("+ Add Medicine");
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addBtn.setBackground(new Color(14, 116, 144));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFocusPainted(false);

        updateBtn = new JButton("Edit / Update");
        updateBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        updateBtn.setBackground(new Color(241, 245, 249));
        updateBtn.setFocusPainted(false);

        updateStockBtn = new JButton("Update Stock");
        updateStockBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        updateStockBtn.setBackground(new Color(241, 245, 249));
        updateStockBtn.setFocusPainted(false);

        viewDetailsBtn = new JButton("View Details");
        viewDetailsBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        viewDetailsBtn.setBackground(new Color(241, 245, 249));
        viewDetailsBtn.setFocusPainted(false);

        deleteBtn = new JButton("Delete Record");
        deleteBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        deleteBtn.setBackground(new Color(239, 68, 68));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setFocusPainted(false);

        closeBtn = new JButton("Close");
        closeBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        closeBtn.setFocusPainted(false);

        actionBtnGroup.add(addBtn);
        actionBtnGroup.add(updateBtn);
        actionBtnGroup.add(updateStockBtn);
        actionBtnGroup.add(viewDetailsBtn);
        actionBtnGroup.add(deleteBtn);
        actionBtnGroup.add(closeBtn);

        southPanel.add(countLabel, BorderLayout.WEST);
        southPanel.add(actionBtnGroup, BorderLayout.EAST);

        mainPanel.add(northGroup, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(southPanel, BorderLayout.SOUTH);
        setContentPane(mainPanel);

        // Handlers
        searchButton.addActionListener(e -> performSearch());
        searchField.addActionListener(e -> performSearch());
        resetButton.addActionListener(e -> {
            searchField.setText("");
            categoryComboBox.setSelectedIndex(0);
            loadData(null, "All Categories");
        });
        categoryComboBox.addActionListener(e -> performSearch());

        addBtn.addActionListener(e -> openAddMedicine());
        updateBtn.addActionListener(e -> openUpdateMedicine());
        updateStockBtn.addActionListener(e -> performQuickStockUpdate());
        viewDetailsBtn.addActionListener(e -> openDetails());
        deleteBtn.addActionListener(e -> performDelete());
        closeBtn.addActionListener(e -> dispose());
    }

    private void loadCategories() {
        try {
            List<String> categories = medicineService.getAllCategories();
            categoryComboBox.removeAllItems();
            categoryComboBox.addItem("All Categories");
            for (String cat : categories) {
                categoryComboBox.addItem(cat);
            }
        } catch (SQLException e) {
            System.err.println("Could not load categories: " + e.getMessage());
        }
    }

    private void loadData(String query, String category) {
        try {
            currentList = medicineService.searchMedicines(query, category);
            tableModel.setRowCount(0);

            for (Medicine m : currentList) {
                tableModel.addRow(new Object[]{
                        m.getMedicineId(),
                        m.getMedicineName(),
                        m.getGenericName(),
                        m.getCategory(),
                        m.getManufacturer(),
                        m.getDosage(),
                        String.format("%.2f", m.getPrice()),
                        m.getStockQuantity(),
                        m.getEffectiveStatus(),
                        DateUtil.formatDisplay(m.getExpiryDate())
                });
            }

            countLabel.setText("Total matching records: " + currentList.size());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to query medicine records:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void reloadCurrentView() {
        String query = searchField.getText().trim();
        String selectedCategory = (String) categoryComboBox.getSelectedItem();
        loadData(query, selectedCategory);
        if (onDataChangedCallback != null) {
            onDataChangedCallback.run();
        }
    }

    private void performSearch() {
        String query = searchField.getText().trim();
        String selectedCategory = (String) categoryComboBox.getSelectedItem();
        loadData(query, selectedCategory);
    }

    private Medicine getSelectedMedicine() {
        int row = medicineTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a medicine record first.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return currentList.get(row);
    }

    private void openAddMedicine() {
        AddMedicineFrame addFrame = new AddMedicineFrame(() -> {
            loadCategories();
            reloadCurrentView();
        });
        addFrame.setVisible(true);
    }

    private void openUpdateMedicine() {
        Medicine selected = getSelectedMedicine();
        if (selected == null) return;

        UpdateMedicineFrame updateFrame = new UpdateMedicineFrame(selected, () -> {
            loadCategories();
            reloadCurrentView();
        });
        updateFrame.setVisible(true);
    }

    private void openDetails() {
        Medicine selected = getSelectedMedicine();
        if (selected == null) return;

        MedicineDetailsFrame details = new MedicineDetailsFrame(selected);
        details.setVisible(true);
    }

    private void performDelete() {
        Medicine selected = getSelectedMedicine();
        if (selected == null) return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to permanently delete this medicine record?\n\n" +
                        "Medicine ID: " + selected.getMedicineId() + "\n" +
                        "Name: " + selected.getMedicineName() + "\n" +
                        "Category: " + selected.getCategory() + "\n\n" +
                        "This action cannot be undone.",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                medicineService.deleteMedicine(selected.getMedicineId());
                JOptionPane.showMessageDialog(this,
                        "Medicine '" + selected.getMedicineName() + "' was successfully deleted.",
                        "Deleted",
                        JOptionPane.INFORMATION_MESSAGE);
                reloadCurrentView();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                        "Failed to delete medicine:\n" + e.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void performQuickStockUpdate() {
        Medicine selected = getSelectedMedicine();
        if (selected == null) return;

        String input = JOptionPane.showInputDialog(this,
                "Update stock quantity for:\n" + selected.getMedicineName() + " (" + selected.getMedicineId() + ")\n\n" +
                        "Current stock: " + selected.getStockQuantity() + "\n" +
                        "Enter new stock quantity:",
                selected.getStockQuantity());

        if (input == null) return; // cancelled

        if (!ValidationUtil.isValidStock(input)) {
            JOptionPane.showMessageDialog(this,
                    "Stock quantity cannot be negative and must be a valid whole number.",
                    "Invalid Stock Input",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int newQty = Integer.parseInt(input.trim());
        try {
            medicineService.updateStock(selected.getMedicineId(), newQty);
            JOptionPane.showMessageDialog(this,
                    "Stock quantity updated to " + newQty + " units.",
                    "Stock Updated",
                    JOptionPane.INFORMATION_MESSAGE);
            reloadCurrentView();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to update stock:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
