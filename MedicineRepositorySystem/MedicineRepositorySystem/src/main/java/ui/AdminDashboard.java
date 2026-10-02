package ui;

import model.Medicine;
import model.User;
import service.MedicineService;
import service.UserService;
import util.DateUtil;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Admin Dashboard providing real-time system metrics, executive inventory controls,
 * expiry surveillance, user auditing, and direct stock intervention.
 */
public class AdminDashboard extends JFrame {

    private final User adminUser;
    private final MedicineService medicineService;
    private final UserService userService;

    // Stat Value Labels
    private JLabel totalMedValue;
    private JLabel availMedValue;
    private JLabel lowStockValue;
    private JLabel outOfStockValue;
    private JLabel expiredValue;
    private JLabel usersValue;

    public AdminDashboard(User adminUser) {
        this.adminUser = adminUser;
        this.medicineService = new MedicineService();
        this.userService = new UserService();
        initializeUI();
        refreshStatistics();
    }

    private void initializeUI() {
        setTitle("Medicine Repository System - Administrator Executive Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 720);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(16, 16));
        mainPanel.setBorder(new EmptyBorder(20, 24, 20, 24));
        mainPanel.setBackground(new Color(248, 250, 252));

        // Top Navigation Header
        JPanel topHeader = new JPanel(new BorderLayout());
        topHeader.setBackground(new Color(248, 250, 252));

        JPanel welcomeBox = new JPanel(new GridLayout(2, 1, 2, 2));
        welcomeBox.setBackground(new Color(248, 250, 252));

        JLabel titleLbl = new JLabel("System Administration & Inventory Surveillance");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLbl.setForeground(new Color(15, 23, 42));

        JLabel userLbl = new JLabel("Logged in as: " + adminUser.getName() + " (" + adminUser.getUsername() + ") | Role: " + adminUser.getRole());
        userLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        userLbl.setForeground(new Color(100, 116, 139));

        welcomeBox.add(titleLbl);
        welcomeBox.add(userLbl);

        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        headerRight.setBackground(new Color(248, 250, 252));

        JButton refreshStatsBtn = new JButton("Refresh Statistics");
        refreshStatsBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        refreshStatsBtn.setBackground(new Color(241, 245, 249));
        refreshStatsBtn.setFocusPainted(false);
        refreshStatsBtn.addActionListener(e -> refreshStatistics());

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logoutBtn.setBackground(new Color(239, 68, 68));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.addActionListener(e -> performLogout());

        headerRight.add(refreshStatsBtn);
        headerRight.add(logoutBtn);

        topHeader.add(welcomeBox, BorderLayout.WEST);
        topHeader.add(headerRight, BorderLayout.EAST);

        // Center Content: Cards + Actions
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(248, 250, 252));

        // Section Title: Real-Time Metrics
        JLabel metricsTitle = new JLabel("Live Repository Statistics (MySQL Database)");
        metricsTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        metricsTitle.setForeground(new Color(30, 41, 59));
        metricsTitle.setBorder(new EmptyBorder(10, 0, 8, 0));
        centerPanel.add(metricsTitle);

        // 6 Statistic Cards Grid (2 rows x 3 columns)
        JPanel statsGrid = new JPanel(new GridLayout(2, 3, 14, 14));
        statsGrid.setBackground(new Color(248, 250, 252));

        totalMedValue = new JLabel("0", SwingConstants.CENTER);
        availMedValue = new JLabel("0", SwingConstants.CENTER);
        lowStockValue = new JLabel("0", SwingConstants.CENTER);
        outOfStockValue = new JLabel("0", SwingConstants.CENTER);
        expiredValue = new JLabel("0", SwingConstants.CENTER);
        usersValue = new JLabel("0", SwingConstants.CENTER);

        statsGrid.add(createStatCard("Total Medicines", totalMedValue, new Color(15, 23, 42), "All active repository items"));
        statsGrid.add(createStatCard("Available Medicines", availMedValue, new Color(21, 128, 61), "Stock ≥ 10 & unexpired"));
        statsGrid.add(createStatCard("Low Stock Alerts", lowStockValue, new Color(180, 83, 9), "Stock between 1 and 9"));
        statsGrid.add(createStatCard("Out of Stock", outOfStockValue, new Color(225, 29, 72), "Immediate replenishment needed"));
        statsGrid.add(createStatCard("Expired Medicines", expiredValue, new Color(147, 51, 234), "Passed expiry date"));
        statsGrid.add(createStatCard("Registered Users", usersValue, new Color(3, 105, 161), "User & Admin accounts"));

        centerPanel.add(statsGrid);
        centerPanel.add(Box.createVerticalStrut(24));

        // Section Title: Management Operations
        JLabel actionsTitle = new JLabel("Administrative Control Center");
        actionsTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        actionsTitle.setForeground(new Color(30, 41, 59));
        actionsTitle.setBorder(new EmptyBorder(10, 0, 8, 0));
        centerPanel.add(actionsTitle);

        // Operations Buttons Grid
        JPanel operationsGrid = new JPanel(new GridLayout(2, 3, 14, 14));
        operationsGrid.setBackground(new Color(248, 250, 252));

        JButton manageMedBtn = createActionBtn("Manage Medicines", "Search, edit, delete, and inspect all records", new Color(14, 116, 144));
        JButton addMedBtn = createActionBtn("Add New Medicine", "Register a new medicine item with specifications", new Color(15, 23, 42));
        JButton viewLowStockBtn = createActionBtn("View Low Stock", "Inspect medicines that require urgent reordering", new Color(180, 83, 9));
        JButton viewExpiredBtn = createActionBtn("View Expired Medicines", "List pharmaceuticals that have passed expiry", new Color(185, 28, 28));
        JButton viewExpiringSoonBtn = createActionBtn("View Expiring Soon", "View medicines expiring within the next 60 days", new Color(194, 65, 12));
        JButton viewUsersBtn = createActionBtn("View Registered Users", "Audit all registered user and administrator accounts", new Color(67, 56, 202));

        operationsGrid.add(manageMedBtn);
        operationsGrid.add(addMedBtn);
        operationsGrid.add(viewLowStockBtn);
        operationsGrid.add(viewExpiredBtn);
        operationsGrid.add(viewExpiringSoonBtn);
        operationsGrid.add(viewUsersBtn);

        centerPanel.add(operationsGrid);

        mainPanel.add(topHeader, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);

        // Button Listeners
        manageMedBtn.addActionListener(e -> {
            MedicineManagementFrame frame = new MedicineManagementFrame(this::refreshStatistics);
            frame.setVisible(true);
        });

        addMedBtn.addActionListener(e -> {
            AddMedicineFrame frame = new AddMedicineFrame(this::refreshStatistics);
            frame.setVisible(true);
        });

        viewLowStockBtn.addActionListener(e -> openFilteredMedicineView("Low Stock Medicines (Stock < 10)", () -> {
            try {
                return medicineService.getLowStockMedicines();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }));

        viewExpiredBtn.addActionListener(e -> openFilteredMedicineView("Expired Medicines (Past Expiry Date)", () -> {
            try {
                return medicineService.getExpiredMedicines();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }));

        viewExpiringSoonBtn.addActionListener(e -> openFilteredMedicineView("Medicines Expiring Soon (Within 60 Days)", () -> {
            try {
                return medicineService.getExpiringSoonMedicines();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }));

        viewUsersBtn.addActionListener(e -> openUsersDialog());
    }

    private JPanel createStatCard(String label, JLabel valueLabel, Color accentColor, String subtitle) {
        JPanel card = new JPanel(new BorderLayout(6, 6));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(14, 18, 14, 18)
        ));

        JLabel titleLbl = new JLabel(label);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLbl.setForeground(new Color(100, 116, 139));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        valueLabel.setForeground(accentColor);

        JLabel subLbl = new JLabel(subtitle);
        subLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subLbl.setForeground(new Color(148, 163, 184));

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(subLbl, BorderLayout.SOUTH);
        return card;
    }

    private JButton createActionBtn(String title, String description, Color baseColor) {
        JButton btn = new JButton();
        btn.setLayout(new BorderLayout(4, 4));
        btn.setBackground(Color.WHITE);
        btn.setBorder(new CompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(12, 16, 12, 16)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLbl.setForeground(baseColor);

        JLabel descLbl = new JLabel("<html>" + description + "</html>");
        descLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        descLbl.setForeground(new Color(100, 116, 139));

        btn.add(titleLbl, BorderLayout.NORTH);
        btn.add(descLbl, BorderLayout.CENTER);

        // Hover effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(241, 245, 249));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(Color.WHITE);
            }
        });

        return btn;
    }

    public void refreshStatistics() {
        try {
            Map<String, Integer> stats = medicineService.getDashboardStatistics();
            totalMedValue.setText(String.valueOf(stats.getOrDefault("totalMedicines", 0)));
            availMedValue.setText(String.valueOf(stats.getOrDefault("availableMedicines", 0)));
            lowStockValue.setText(String.valueOf(stats.getOrDefault("lowStockMedicines", 0)));
            outOfStockValue.setText(String.valueOf(stats.getOrDefault("outOfStockMedicines", 0)));
            expiredValue.setText(String.valueOf(stats.getOrDefault("expiredMedicines", 0)));
            usersValue.setText(String.valueOf(stats.getOrDefault("registeredUsers", 0)));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to refresh dashboard metrics from MySQL:\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    @FunctionalInterface
    private interface MedicineListSupplier {
        List<Medicine> get() throws Exception;
    }

    private void openFilteredMedicineView(String title, MedicineListSupplier supplier) {
        try {
            List<Medicine> list = supplier.get();

            JDialog dialog = new JDialog(this, title, true);
            dialog.setSize(950, 520);
            dialog.setLocationRelativeTo(this);

            JPanel panel = new JPanel(new BorderLayout(10, 10));
            panel.setBorder(new EmptyBorder(16, 18, 16, 18));
            panel.setBackground(new Color(248, 250, 252));

            JLabel titleLbl = new JLabel(title + " — " + list.size() + " records found");
            titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
            panel.add(titleLbl, BorderLayout.NORTH);

            String[] columns = {"Medicine ID", "Medicine Name", "Category", "Stock", "Status", "Expiry Date", "Unit Price"};
            DefaultTableModel model = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int col) {
                    return false;
                }
            };

            for (Medicine m : list) {
                model.addRow(new Object[]{
                        m.getMedicineId(),
                        m.getMedicineName(),
                        m.getCategory(),
                        m.getStockQuantity(),
                        m.getEffectiveStatus(),
                        DateUtil.formatDisplay(m.getExpiryDate()),
                        String.format("$%.2f", m.getPrice())
                });
            }

            JTable table = new JTable(model);
            table.setRowHeight(28);
            table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

            panel.add(new JScrollPane(table), BorderLayout.CENTER);

            JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            bottom.setBackground(new Color(248, 250, 252));

            JButton detailsBtn = new JButton("View Selected Details");
            detailsBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            detailsBtn.addActionListener(e -> {
                int r = table.getSelectedRow();
                if (r != -1) {
                    new MedicineDetailsFrame(list.get(r)).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(dialog, "Please select a row first.", "No Selection", JOptionPane.WARNING_MESSAGE);
                }
            });

            JButton closeBtn = new JButton("Close");
            closeBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            closeBtn.addActionListener(e -> dialog.dispose());

            bottom.add(detailsBtn);
            bottom.add(closeBtn);
            panel.add(bottom, BorderLayout.SOUTH);

            dialog.setContentPane(panel);
            dialog.setVisible(true);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error querying records: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openUsersDialog() {
        try {
            List<User> userList = userService.getAllUsers();

            JDialog dialog = new JDialog(this, "Registered User Accounts Directory", true);
            dialog.setSize(850, 480);
            dialog.setLocationRelativeTo(this);

            JPanel panel = new JPanel(new BorderLayout(10, 10));
            panel.setBorder(new EmptyBorder(16, 18, 16, 18));
            panel.setBackground(new Color(248, 250, 252));

            JLabel titleLbl = new JLabel("Registered System Accounts (" + userList.size() + " Total)");
            titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
            panel.add(titleLbl, BorderLayout.NORTH);

            String[] columns = {"User ID", "Full Name", "Email Address", "Phone", "Username", "Role", "Registered At"};
            DefaultTableModel model = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int col) {
                    return false;
                }
            };

            for (User u : userList) {
                model.addRow(new Object[]{
                        u.getUserId(),
                        u.getName(),
                        u.getEmail(),
                        u.getPhone(),
                        u.getUsername(),
                        u.getRole(),
                        u.getCreatedAt() != null ? u.getCreatedAt().toString().replace('T', ' ') : "N/A"
                });
            }

            JTable table = new JTable(model);
            table.setRowHeight(28);
            table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

            panel.add(new JScrollPane(table), BorderLayout.CENTER);

            JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            bottom.setBackground(new Color(248, 250, 252));
            JButton closeBtn = new JButton("Close");
            closeBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            closeBtn.addActionListener(e -> dialog.dispose());
            bottom.add(closeBtn);
            panel.add(bottom, BorderLayout.SOUTH);

            dialog.setContentPane(panel);
            dialog.setVisible(true);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database error while querying users:\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to log out of the Administrator Dashboard?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        }
    }
}
