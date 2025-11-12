package com.cinema.ticket.controllers;

import com.cinema.ticket.dao.UserDAO;
import com.cinema.ticket.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;

public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField emailField;
    @FXML private TextField fullNameField;

    private UserDAO userDAO = new UserDAO();

    @FXML
    protected void onRegisterClick(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String email = emailField.getText().trim();
        String fullName = fullNameField.getText().trim();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() ||
                email.isEmpty() || fullName.isEmpty()) {
            showAlert("Ошибка", "Заполните все поля");
            return;
        }

        if (username.length() < 3) {
            showAlert("Ошибка", "Имя пользователя должно содержать минимум 3 символа");
            return;
        }

        if (password.length() < 4) {
            showAlert("Ошибка", "Пароль должен содержать минимум 4 символа");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert("Ошибка", "Пароли не совпадают");
            return;
        }

        if (userDAO.userExists(username)) {
            showAlert("Ошибка", "Пользователь с таким именем уже существует");
            return;
        }

        User newUser = new User(username, password, email, fullName, "CLIENT");
        if (userDAO.createUser(newUser)) {
            showSuccessAlert("Успех", "Регистрация завершена! Теперь вы можете войти в систему.");
            clearFields();

            try {
                loadScene("login.fxml", "Вход в систему", event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showAlert("Ошибка", "Ошибка при регистрации пользователя");
        }
    }

    @FXML
    protected void onBackClick(ActionEvent event) throws IOException {
        loadScene("main.fxml", "CineMax - Кинотеатр", event);
    }

    private void loadScene(String fxmlFile, String title, ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }

    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        emailField.clear();
        fullNameField.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}