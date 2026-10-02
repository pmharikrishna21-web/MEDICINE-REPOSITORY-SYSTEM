package ui;

import service.UserService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

/**
 * Registration GUI frame allowing new users to create an account.
 */
public class RegisterFrame extends JFrame {

    private final UserService userService;
    private final JFrame parentFrame;

    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton registerButton;
    private JButton backButton;

    public RegisterFrame(JFrame parentFrame) {
        this.userService = new UserService();
        this.parentFrame = parentFrame;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Medicine Repository System - Account Registration");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(480, 560);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(20, 30, 25, 30));
        mainPanel.setBackground(new Color(248, 250, 252));

        // Header Panel
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 4, 4));
        headerPanel.setBackground(new Color(248, 250, 252));

        JLabel titleLabel = new JLabel("Create a New Account", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(15, 23, 42));

        JLabel subtitleLabel = new JLabel("Fill in your details to register as a system user", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(100, 116, 139));

        headerPanel.add(titleLabel);
        headerPanel.add(subtitleLabel);

        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 14));
        formPanel.setBackground(new Color(248, 250, 252));

        nameField = new JTextField();
        emailField = new JTextField();
        phoneField = new JTextField();
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        confirmPasswordField = new JPasswordField();

        addFormField(formPanel, "Full Name *", nameField);
        addFormField(formPanel, "Email Address *", emailField);
        addFormField(formPanel, "Phone Number *", phoneField);
        addFormField(formPanel, "Username *", usernameField);
        addFormField(formPanel, "Password *", passwordField);
        addFormField(formPanel, "Confirm Password *", confirmPasswordField);

        // Button Panel
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 8, 8));
        buttonPanel.setBackground(new Color(248, 250, 252));

        registerButton = new JButton("Register");
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        registerButton.setBackground(new Color(14, 116, 144));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setPreferredSize(new Dimension(380, 38));

        backButton = new JButton("Back to Login");
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        backButton.setBackground(new Color(241, 245, 249));
        backButton.setForeground(new Color(30, 41, 59));
        backButton.setFocusPainted(false);

        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        setContentPane(mainPanel);

        // Handlers
        registerButton.addActionListener(e -> performRegistration());
        backButton.addActionListener(e -> returnToLogin());

        // Handle window close to restore login
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                returnToLogin();
            }
        });
    }

    private void addFormField(JPanel panel, String labelText, JComponent field) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(new Color(51, 65, 85));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(label);
        panel.add(field);
    }

    private void performRegistration() {
        String name = nameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        try {
            userService.registerUser(name, email, phone, username, password, confirmPassword);

            JOptionPane.showMessageDialog(this,
                    "Account registered successfully! You can now log in with your credentials.",
                    "Registration Successful",
                    JOptionPane.INFORMATION_MESSAGE);

            returnToLogin();

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database error during registration:\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "An unexpected error occurred: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void returnToLogin() {
        dispose();
        if (parentFrame != null) {
            parentFrame.setVisible(true);
        }
    }
}
