package com.cinema.ticket.controllers;

import com.cinema.ticket.CinemaApp;
import com.cinema.ticket.models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    @FXML
    protected void onLoginClick(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(CinemaApp.class.getResource("login.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setTitle("CineMax - Вход");
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onRegisterClick(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(CinemaApp.class.getResource("register.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setTitle("CineMax - Регистрация");
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onGuestClick(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(CinemaApp.class.getResource("movies.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            MoviesController controller = fxmlLoader.getController();
            controller.setCurrentUser(User.createGuest());
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setTitle("CineMax - Фильмы (Гость)");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}