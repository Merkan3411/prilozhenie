package com.cinema.ticket.controllers;

import com.cinema.ticket.models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    @FXML
    protected void onLoginClick(ActionEvent event) throws IOException {
        loadScene("login.fxml", "Вход в систему", event);
    }

    @FXML
    protected void onRegisterClick(ActionEvent event) throws IOException {
        loadScene("register.fxml", "Регистрация", event);
    }

    @FXML
    protected void onGuestClick(ActionEvent event) throws IOException {
        // Создаем гостевого пользователя и переходим к фильмам
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("movies.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1000, 700);

            MoviesController controller = fxmlLoader.getController();
            User guest = User.createGuest();
            controller.setCurrentUser(guest);

            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setTitle("Фильмы - Гость");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось загрузить страницу фильмов");
        }
    }

    @FXML
    protected void onMoviesClick(ActionEvent event) throws IOException {
        // Переход к фильмам как гость
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("movies.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1000, 700);

            MoviesController controller = fxmlLoader.getController();
            User guest = User.createGuest();
            controller.setCurrentUser(guest);

            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setTitle("Фильмы - Гость");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось загрузить страницу фильмов");
        }
    }

    @FXML
    protected void onSessionsClick(ActionEvent event) throws IOException {
        // Переход к сеансам как гость
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("sessions.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1000, 700);

            SessionsController controller = fxmlLoader.getController();
            User guest = User.createGuest();
            controller.setCurrentUser(guest);

            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setTitle("Сеансы - Гость");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось загрузить страницу сеансов");
        }
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