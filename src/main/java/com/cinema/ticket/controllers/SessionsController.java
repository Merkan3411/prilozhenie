package com.cinema.ticket.controllers;

import com.cinema.ticket.CinemaApp;
import com.cinema.ticket.dao.SessionDAO;
import com.cinema.ticket.models.Session;
import com.cinema.ticket.models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.List;

public class SessionsController {
    @FXML
    private Label titleLabel;
    @FXML
    private ComboBox<String> dateFilter;
    @FXML
    private ComboBox<String> timeFilter;
    @FXML
    private VBox sessionsContainer;

    private SessionDAO sessionDAO = new SessionDAO();
    private User currentUser;

    @FXML
    public void initialize() {
        loadSessions();
        initializeFilters();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    private void loadSessions() {
        sessionsContainer.getChildren().clear();
    }

    private void initializeFilters() {
        if (dateFilter != null) {
            dateFilter.getItems().addAll("Все даты", "Сегодня", "Завтра", "На неделю");
            dateFilter.setValue("Все даты");
        }
        if (timeFilter != null) {
            timeFilter.getItems().addAll("Все время", "Утро (06:00-12:00)", "День (12:00-18:00)", "Вечер (18:00-00:00)");
            timeFilter.setValue("Все время");
        }
    }

    @FXML
    protected void onFilterChange(ActionEvent event) {
    }

    @FXML
    protected void onBackClick(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(CinemaApp.class.getResource("movies.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        MoviesController controller = fxmlLoader.getController();
        controller.setCurrentUser(currentUser);
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.setTitle("Фильмы");
        stage.setScene(scene);
        stage.show();
    }
}
