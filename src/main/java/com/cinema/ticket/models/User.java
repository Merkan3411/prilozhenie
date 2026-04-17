package com.cinema.ticket.models;

import java.time.LocalDateTime;

public class User {
    private int id;
    private String fullName;
    private String email;
    private String username;
    private String password;
    private String role;
    private boolean isActive;
    private LocalDateTime createdAt;

    public User() {
    }

    public User(int id, String fullName, String email, String username, String password, String role, boolean isActive) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
        this.isActive = isActive;
    }

    public static User createGuest() {
        User guest = new User();
        guest.setId(0);
        guest.setFullName("Гость");
        guest.setEmail("guest@cinema.local");
        guest.setUsername("guest");
        guest.setPassword("");
        guest.setRole("GUEST");
        guest.setActive(true);
        return guest;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
        this.role = role;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }

    public boolean isUser() {
        return "USER".equals(role);
    }

    public boolean isGuest() {
        return "GUEST".equals(role);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
