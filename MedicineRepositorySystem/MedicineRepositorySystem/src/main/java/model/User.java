package model;

import java.time.LocalDateTime;

/**
 * Represents a user in the Medicine Repository System.
 * Encapsulates authentication credentials, contact details, and role permissions.
 */
public class User {
    private int userId;
    private String name;
    private String email;
    private String phone;
    private String username;
    private String password;
    private String role; // "USER" or "ADMIN"
    private LocalDateTime createdAt;

    // Default Constructor
    public User() {
    }

    // Parameterized Constructor for registration
    public User(String name, String email, String phone, String username, String password, String role) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.username = username;
        this.password = password;
        this.role = role != null ? role.toUpperCase() : "USER";
    }

    // Full Parameterized Constructor
    public User(int userId, String name, String email, String phone, String username, String password, String role, LocalDateTime createdAt) {
        this(name, email, phone, username, password, role);
        this.userId = userId;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role != null ? role.toUpperCase() : "USER";
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(this.role);
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
