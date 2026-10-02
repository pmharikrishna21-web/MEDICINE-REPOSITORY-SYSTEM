package ui;

import model.User;
import service.UserService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;

/**
 * Login GUI frame providing authentication entry point for both Normal Users and Administrators.
 */
public class LoginFrame extends JFrame {

    private final UserService userService;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JButton exitButton;

    public LoginFrame() {
        this.userService = new UserService();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Medicine Repository System - Secure Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(460, 420);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main Container Panel
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(25, 30, 25, 30));
        mainPanel.setBackground(new Color(248, 250, 252));

        // Header Panel
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 4, 4));
        headerPanel.setBackground(new Color(248, 250, 252));

        JLabel titleLabel = new JLabel("Medicine Repository System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(15, 23, 42));

        JLabel subtitleLabel = new JLabel("Please enter your credentials to continue", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(new Color(100, 116, 139));

        headerPanel.add(titleLabel);
        headerPanel.add(subtitleLabel);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(248, 250, 252));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);

        // Username Row
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        userLabel.setForeground(new Color(51, 65, 85));
        formPanel.add(userLabel, gbc);

        gbc.gridy = 1;
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.setPreferredSize(new Dimension(380, 36));
        formPanel.add(usernameField, gbc);

        // Password Row
        gbc.gridy = 2;
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        passLabel.setForeground(new Color(51, 65, 85));
        formPanel.add(passLabel, gbc);

        gbc.gridy = 3;
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setPreferredSize(new Dimension(380, 36));
        formPanel.add(passwordField, gbc);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 8, 8));
        buttonPanel.setBackground(new Color(248, 250, 252));

        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setBackground(new Color(14, 116, 144));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setPreferredSize(new Dimension(380, 38));

        registerButton = new JButton("Register New Account");
        registerButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        registerButton.setBackground(new Color(241, 245, 249));
        registerButton.setForeground(new Color(30, 41, 59));
        registerButton.setFocusPainted(false);

        exitButton = new JButton("Exit Application");
        exitButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        exitButton.setForeground(new Color(148, 163, 184));
        exitButton.setBorderPainted(false);
        exitButton.setContentAreaFilled(false);

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        buttonPanel.add(exitButton);

        // Assemble Main Frame
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        setContentPane(mainPanel);

        // Event Listeners
        loginButton.addActionListener(e -> performLogin());
        registerButton.addActionListener(e -> openRegistration());
        exitButton.addActionListener(e -> System.exit(0));

        // Enter key triggers login
        KeyAdapter enterKeyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        };
        usernameField.addKeyListener(enterKeyAdapter);
        passwordField.addKeyListener(enterKeyAdapter);
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both username and password.",
                    "Input Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        loginButton.setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        SwingUtilities.invokeLater(() -> {
            try {
                User user = userService.login(username, password);

                JOptionPane.showMessageDialog(this,
                        "Welcome, " + user.getName() + "!\nLogged in as: " + user.getRole(),
                        "Login Successful",
                        JOptionPane.INFORMATION_MESSAGE);

                dispose(); // Close login window

                // Open appropriate dashboard based on database role
                if (user.isAdmin()) {
                    AdminDashboard adminDashboard = new AdminDashboard(user);
                    adminDashboard.setVisible(true);
                } else {
                    UserDashboard userDashboard = new UserDashboard(user);
                    userDashboard.setVisible(true);
                }

            } catch (SecurityException ex) {
                JOptionPane.showMessageDialog(this,
                        ex.getMessage(),
                        "Authentication Failed",
                        JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "Database connection error:\n" + ex.getMessage() +
                                "\nPlease ensure MySQL server is running and database is configured.",
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "An unexpected error occurred: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            } finally {
                loginButton.setEnabled(true);
                setCursor(Cursor.getDefaultCursor());
            }
        });
    }

    private void openRegistration() {
        RegisterFrame registerFrame = new RegisterFrame(this);
        registerFrame.setVisible(true);
        setVisible(false);
    }
}
