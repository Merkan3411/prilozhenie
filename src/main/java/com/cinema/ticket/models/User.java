package com.cinema.ticket.models;

public class User {
    private int id;
    private String username;
    private String password;
    private String email;
    private String fullName;
    private String role;

    // Конструктор по умолчанию (для гостей и TableView)
    public User() {
        this.username = "guest";
        this.fullName = "Гость";
        this.role = "GUEST";
        this.email = "guest@cinema.com";
        this.password = "";
    }

    // Конструктор для регистрации пользователей
    public User(String username, String password, String email, String fullName, String role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
    }

    // Конструктор для гостя
    public static User createGuest() {
        User guest = new User();
        guest.setUsername("guest");
        guest.setFullName("Гость");
        guest.setRole("GUEST");
        guest.setEmail("guest@cinema.com");
        return guest;
    }

    // Геттеры и сеттеры
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // Вспомогательные методы
    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }

    public boolean isClient() {
        return "CLIENT".equals(role);
    }

    public boolean isGuest() {
        return "GUEST".equals(role);
    }

    public String getRoleDisplayName() {
        switch (role) {
            case "ADMIN": return "Администратор";
            case "CLIENT": return "Клиент";
            case "GUEST": return "Гость";
            default: return role;
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", role='" + role + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && username.equals(user.username);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, username);
    }
}