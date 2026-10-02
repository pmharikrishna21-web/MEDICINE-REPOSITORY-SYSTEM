package service;

import dao.UserDAO;
import model.User;
import util.ValidationUtil;

import java.sql.SQLException;
import java.util.List;

/**
 * Service layer orchestrating user authentication, registration validations,
 * and user management logic.
 */
public class UserService {

    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Validates and registers a new normal user.
     *
     * @param name Full name
     * @param email Valid email
     * @param phone Phone number
     * @param username Unique username
     * @param password Password
     * @param confirmPassword Password confirmation
     * @return Registered User object upon success
     * @throws IllegalArgumentException if validation fails
     * @throws SQLException if database error occurs
     */
    public User registerUser(String name, String email, String phone, String username,
                             String password, String confirmPassword) throws SQLException {
        // 1. Check for empty fields
        if (ValidationUtil.isEmpty(name)) {
            throw new IllegalArgumentException("Full name is required.");
        }
        if (ValidationUtil.isEmpty(email)) {
            throw new IllegalArgumentException("Email address is required.");
        }
        if (!ValidationUtil.isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format (e.g. user@example.com).");
        }
        if (ValidationUtil.isEmpty(phone)) {
            throw new IllegalArgumentException("Phone number is required.");
        }
        if (!ValidationUtil.isValidPhone(phone)) {
            throw new IllegalArgumentException("Invalid phone number format.");
        }
        if (ValidationUtil.isEmpty(username)) {
            throw new IllegalArgumentException("Username is required.");
        }
        if (!ValidationUtil.isValidUsername(username)) {
            throw new IllegalArgumentException("Username must be 3-30 alphanumeric characters.");
        }
        if (ValidationUtil.isEmpty(password)) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }
        if (password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long.");
        }
        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Passwords do not match.");
        }

        // 2. Uniqueness checks
        if (userDAO.usernameExists(username)) {
            throw new IllegalArgumentException("Username '" + username + "' is already registered. Please choose another.");
        }
        if (userDAO.emailExists(email)) {
            throw new IllegalArgumentException("Email address is already in use by another account.");
        }

        // 3. Create user entity with default USER role
        User newUser = new User(name.trim(), email.trim(), phone.trim(), username.trim(), password, "USER");
        boolean success = userDAO.registerUser(newUser);
        if (!success) {
            throw new SQLException("Failed to save user account to the database.");
        }

        return newUser;
    }

    /**
     * Authenticates a user with username and password.
     *
     * @param username Username
     * @param password Password
     * @return User object if credentials are valid
     * @throws IllegalArgumentException if fields are empty
     * @throws SecurityException if credentials are invalid
     * @throws SQLException if database access fails
     */
    public User login(String username, String password) throws SQLException {
        if (ValidationUtil.isEmpty(username) || ValidationUtil.isEmpty(password)) {
            throw new IllegalArgumentException("Please enter both username and password.");
        }

        User user = userDAO.loginUser(username.trim(), password);
        if (user == null) {
            throw new SecurityException("Invalid username or password.");
        }

        return user;
    }

    /**
     * Retrieves all registered users in the system.
     */
    public List<User> getAllUsers() throws SQLException {
        return userDAO.getAllUsers();
    }

    /**
     * Returns total registered user count.
     */
    public int getUserCount() throws SQLException {
        return userDAO.getUserCount();
    }
}
