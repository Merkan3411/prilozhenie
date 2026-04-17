package com.cinema.ticket.controllers;

import com.cinema.ticket.CinemaApp;
import com.cinema.ticket.models.User;
import com.cinema.ticket.dao.UserDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private UserDAO userDAO = new UserDAO();

    @FXML
    protected void onLoginClick(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Ошибка", "Пожалуйста, заполните все поля");
            return;
        }

        User user = userDAO.getUserByUsername(username);

        if (user != null && user.getPassword().equals(password) && user.isActive()) {
            try {
                if (user.isAdmin()) {
                    goToAdminPanel(event, user);
                } else {
                    goToMovies(event, user);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Неверное имя пользователя или пароль");
        }
    }

    @FXML
    protected void onGuestLoginClick(ActionEvent event) {
        try {
            goToMovies(event, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onRegisterLinkClick(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(CinemaApp.class.getResource("register.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.setTitle("CineMax - Регистрация");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    protected void onBackClick(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(CinemaApp.class.getResource("main.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.setTitle("CineMax - Главная");
        stage.setScene(scene);
        stage.show();
    }

    private void goToMovies(ActionEvent event, User user) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(CinemaApp.class.getResource("movies.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        MoviesController controller = fxmlLoader.getController();

        if (user != null) {
            controller.setCurrentUser(user);
        } else {
        }

        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.setTitle("CineMax - Фильмы");
        stage.setScene(scene);
        stage.show();
    }

    private void goToAdminPanel(ActionEvent event, User user) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(CinemaApp.class.getResource("admin-panel.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        AdminPanelController controller = fxmlLoader.getController();
        controller.setCurrentAdmin(user);
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.setTitle("CineMax - Панель администратора");
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