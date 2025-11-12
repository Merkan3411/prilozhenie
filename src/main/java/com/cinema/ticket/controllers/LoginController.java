package com.cinema.ticket.controllers;

import com.cinema.ticket.dao.UserDAO;
import com.cinema.ticket.models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    private UserDAO userDAO = new UserDAO();

    @FXML
    protected void onLoginClick(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Ошибка", "Заполните все поля");
            return;
        }

        if (userDAO.validateUser(username, password)) {
            try {
                User user = userDAO.getUserByUsername(username);
                loadMoviesScene(event, user);
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Ошибка", "Ошибка при загрузке приложения");
            }
        } else {
            showAlert("Ошибка", "Неверное имя пользователя или пароль");
        }
    }

    @FXML
    protected void onRegisterLinkClick(ActionEvent event) throws IOException {
        loadScene("register.fxml", "Регистрация", event);
    }

    @FXML
    protected void onGuestLoginClick(ActionEvent event) throws IOException {
        User guest = new User("guest", "", "guest@cinema.com", "Гость", "GUEST");
        loadMoviesScene(event, guest);
    }

    @FXML
    protected void onBackClick(ActionEvent event) throws IOException {
        loadScene("main.fxml", "CineMax - Кинотеатр", event);
    }

    private void loadMoviesScene(ActionEvent event, User user) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("movies.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 700);

        MoviesController controller = fxmlLoader.getController();
        controller.setCurrentUser(user);

        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.setTitle("Фильмы - " + user.getFullName());
        stage.setScene(scene);
        stage.show();
    }

    private void loadScene(String fxmlFile, String title, ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}