package ui;

import model.Medicine;
import model.User;
import service.MedicineService;
import util.DateUtil;

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
 * User Dashboard allowing regular users to search, filter by category,
 * view matching medicine tables, and inspect clinical specifications.
 */
public class UserDashboard extends JFrame {

    private final User currentUser;
    private final MedicineService medicineService;

    private JTextField searchField;
    private JComboBox<String> categoryComboBox;
    private JButton searchButton;
    private JButton resetButton;
    private JButton viewDetailsButton;
    private JButton logoutButton;
    private JTable medicineTable;
    private DefaultTableModel tableModel;
    private JLabel statusCountLabel;

    private List<Medicine> currentList = new ArrayList<>();

    public UserDashboard(User user) {
        this.currentUser = user;
        this.medicineService = new MedicineService();
        initializeUI();
        loadCategories();
        loadMedicineData(null, "All Categories");
    }

    private void initializeUI() {
        setTitle("Medicine Repository System - User Dashboard (" + currentUser.getName() + ")");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1050, 680);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(18, 22, 18, 22));
        mainPanel.setBackground(new Color(248, 250, 252));

        // Top Header
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(248, 250, 252));

        JPanel welcomeBox = new JPanel(new GridLayout(2, 1, 2, 2));
        welcomeBox.setBackground(new Color(248, 250, 252));
        JLabel welcomeLabel = new JLabel("Welcome back, " + currentUser.getName());
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        welcomeLabel.setForeground(new Color(15, 23, 42));

        JLabel roleLabel = new JLabel("Medicine Catalog & Real-Time Stock Availability Explorer");
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        roleLabel.setForeground(new Color(100, 116, 139));
        welcomeBox.add(welcomeLabel);
        welcomeBox.add(roleLabel);

        logoutButton = new JButton("Log Out");
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logoutButton.setBackground(new Color(239, 68, 68));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(e -> performLogout());

        topPanel.add(welcomeBox, BorderLayout.WEST);
        topPanel.add(logoutButton, BorderLayout.EAST);

        // Filter / Search Toolbar
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
        searchField.setToolTipText("Enter medicine name, generic name, category, or manufacturer");

        JLabel catLbl = new JLabel("Category:");
        catLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        categoryComboBox = new JComboBox<>(new String[]{"All Categories"});
        categoryComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        categoryComboBox.setPreferredSize(new Dimension(180, 30));

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

        // Combine Header & Filter
        JPanel northContainer = new JPanel(new BorderLayout(8, 12));
        northContainer.setBackground(new Color(248, 250, 252));
        northContainer.add(topPanel, BorderLayout.NORTH);
        northContainer.add(filterPanel, BorderLayout.SOUTH);

        // Center Table
        String[] columnNames = {
                "Medicine ID", "Medicine Name", "Category", "Manufacturer",
                "Dosage", "Price", "Stock Status", "Expiry Date"
        };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // read-only
            }
        };

        medicineTable = new JTable(tableModel);
        medicineTable.setRowHeight(30);
        medicineTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        medicineTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        medicineTable.getTableHeader().setBackground(new Color(241, 245, 249));
        medicineTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Custom Cell Renderer for Stock Status
        medicineTable.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value != null && !isSelected) {
                    String status = value.toString();
                    if (status.equalsIgnoreCase("OUT OF STOCK") || status.equalsIgnoreCase("EXPIRED")) {
                        setForeground(new Color(185, 28, 28)); // Red
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else if (status.equalsIgnoreCase("LOW STOCK")) {
                        setForeground(new Color(180, 83, 9)); // Amber
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else {
                        setForeground(new Color(21, 128, 61)); // Green
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

        // Double click to view details
        medicineTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && medicineTable.getSelectedRow() != -1) {
                    openSelectedMedicineDetails();
                }
            }
        });

        // Bottom Action Panel
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBackground(new Color(248, 250, 252));

        statusCountLabel = new JLabel("Showing medicines");
        statusCountLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        statusCountLabel.setForeground(new Color(100, 116, 139));

        viewDetailsButton = new JButton("View Complete Details");
        viewDetailsButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        viewDetailsButton.setBackground(new Color(15, 23, 42));
        viewDetailsButton.setForeground(Color.WHITE);
        viewDetailsButton.setFocusPainted(false);
        viewDetailsButton.setPreferredSize(new Dimension(190, 36));
        viewDetailsButton.addActionListener(e -> openSelectedMedicineDetails());

        southPanel.add(statusCountLabel, BorderLayout.WEST);
        southPanel.add(viewDetailsButton, BorderLayout.EAST);

        // Assembly
        mainPanel.add(northContainer, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(southPanel, BorderLayout.SOUTH);
        setContentPane(mainPanel);

        // Event Listeners
        searchButton.addActionListener(e -> performSearch());
        searchField.addActionListener(e -> performSearch());
        resetButton.addActionListener(e -> {
            searchField.setText("");
            categoryComboBox.setSelectedIndex(0);
            loadMedicineData(null, "All Categories");
        });
        categoryComboBox.addActionListener(e -> performSearch());
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

    private void loadMedicineData(String query, String category) {
        try {
            currentList = medicineService.searchMedicines(query, category);
            tableModel.setRowCount(0);

            for (Medicine m : currentList) {
                tableModel.addRow(new Object[]{
                        m.getMedicineId(),
                        m.getMedicineName(),
                        m.getCategory(),
                        m.getManufacturer(),
                        m.getDosage(),
                        String.format("$%.2f", m.getPrice()),
                        m.getEffectiveStatus(),
                        DateUtil.formatDisplay(m.getExpiryDate())
                });
            }

            statusCountLabel.setText("Showing " + currentList.size() + " medicine record(s)");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to load medicine data:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performSearch() {
        String query = searchField.getText().trim();
        String selectedCategory = (String) categoryComboBox.getSelectedItem();
        loadMedicineData(query, selectedCategory);
    }

    private void openSelectedMedicineDetails() {
        int selectedRow = medicineTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a medicine record first.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Medicine selectedMedicine = currentList.get(selectedRow);
        MedicineDetailsFrame detailsFrame = new MedicineDetailsFrame(selectedMedicine);
        detailsFrame.setVisible(true);
    }

    private void performLogout() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to log out?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            dispose();
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        }
    }
}
