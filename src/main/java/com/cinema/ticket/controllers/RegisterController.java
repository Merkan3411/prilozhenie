package com.cinema.ticket.controllers;

import com.cinema.ticket.CinemaApp;
import com.cinema.ticket.models.User;
import com.cinema.ticket.dao.UserDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.regex.Pattern;

public class RegisterController {

    @FXML
    private TextField fullNameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;


    @FXML
    private Label fullNameCountLabel;

    @FXML
    private Label emailCountLabel;

    @FXML
    private Label usernameCountLabel;

    @FXML
    private Label passwordCountLabel;

    @FXML
    private Label confirmPasswordCountLabel;

    private UserDAO userDAO = new UserDAO();

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private static final int FULLNAME_MAX = 100;
    private static final int EMAIL_MAX = 126;
    private static final int USERNAME_MAX = 30;
    private static final int PASSWORD_MIN = 8;
    private static final int PASSWORD_MAX = 48;

    @FXML
    public void initialize() {
        addTextLimiter(fullNameField, FULLNAME_MAX);
        fullNameField.textProperty().addListener((obs, oldVal, newVal) ->
                fullNameCountLabel.setText("(" + newVal.length() + "/" + FULLNAME_MAX + ")")
        );

        addTextLimiter(emailField, EMAIL_MAX);
        emailField.textProperty().addListener((obs, oldVal, newVal) ->
                emailCountLabel.setText("(" + newVal.length() + "/" + EMAIL_MAX + ")")
        );

        addTextLimiter(usernameField, USERNAME_MAX);
        usernameField.textProperty().addListener((obs, oldVal, newVal) ->
                usernameCountLabel.setText("(" + newVal.length() + "/" + USERNAME_MAX + ")")
        );

        addTextLimiter(passwordField, PASSWORD_MAX);
        passwordField.textProperty().addListener((obs, oldVal, newVal) ->
                passwordCountLabel.setText("(" + newVal.length() + "/" + PASSWORD_MAX + ")")
        );

        addTextLimiter(confirmPasswordField, PASSWORD_MAX);
        confirmPasswordField.textProperty().addListener((obs, oldVal, newVal) ->
                confirmPasswordCountLabel.setText("(" + newVal.length() + "/" + PASSWORD_MAX + ")")
        );
    }

    private void addTextLimiter(TextField textField, int maxLength) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > maxLength) {
                textField.setText(oldValue);
            }
        });
    }

    private void addTextLimiter(PasswordField passwordField, int maxLength) {
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > maxLength) {
                passwordField.setText(oldValue);
            }
        });
    }

    @FXML
    protected void onRegisterClick(ActionEvent event) {
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (fullName.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Ошибка", "Пожалуйста, заполните все поля");
            return;
        }

        if (fullName.length() > FULLNAME_MAX) {
            showAlert(Alert.AlertType.WARNING, "Ошибка", "ФИО не может содержать более " + FULLNAME_MAX + " символов");
            return;
        }

        if (!isValidEmail(email)) {
            showAlert(Alert.AlertType.WARNING, "Ошибка", "Укажите корректный email адрес");
            return;
        }

        if (email.length() > EMAIL_MAX) {
            showAlert(Alert.AlertType.WARNING, "Ошибка", "Email не может содержать более " + EMAIL_MAX + " символов");
            return;
        }

        if (username.length() > USERNAME_MAX) {
            showAlert(Alert.AlertType.WARNING, "Ошибка", "Имя пользователя не может содержать более " + USERNAME_MAX + " символов");
            return;
        }

        if (password.length() < PASSWORD_MIN) {
            showAlert(Alert.AlertType.WARNING, "Ошибка", "Пароль должен содержать минимум " + PASSWORD_MIN + " символов");
            return;
        }

        if (password.length() > PASSWORD_MAX) {
            showAlert(Alert.AlertType.WARNING, "Ошибка", "Пароль не может содержать более " + PASSWORD_MAX + " символов");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.WARNING, "Ошибка", "Пароли не совпадают");
            return;
        }

        User newUser = new User();
        newUser.setFullName(fullName);
        newUser.setEmail(email);
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setRole("USER");

        if (userDAO.createUser(newUser)) {
            showAlert(Alert.AlertType.INFORMATION, "Успех", "Регистрация успешна");
            try {
                goToLogin(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Пользователь уже существует");
        }
    }

    @FXML
    protected void onLoginClick(ActionEvent event) throws IOException {
        goToLogin(event);
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    @FXML
    protected void onBackClick(ActionEvent event) throws IOException {
        goBackToMain(event);
    }

    private void goBackToMain(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(CinemaApp.class.getResource("main.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) fullNameField.getScene().getWindow();
        stage.setTitle("CineMax - Главная");
        stage.setScene(scene);
        stage.show();
    }

    private void goToLogin(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(CinemaApp.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) fullNameField.getScene().getWindow();
        stage.setTitle("CineMax - Вход");
        stage.setScene(scene);
        stage.show();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}


