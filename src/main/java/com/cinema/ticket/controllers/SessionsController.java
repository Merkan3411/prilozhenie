package com.cinema.ticket.controllers;

import com.cinema.ticket.dao.SessionDAO;
import com.cinema.ticket.models.Movie;
import com.cinema.ticket.models.Session;
import com.cinema.ticket.models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SessionsController {

    @FXML private Label titleLabel;
    @FXML private VBox sessionsContainer;
    @FXML private ComboBox<String> dateFilter;
    @FXML private ComboBox<String> timeFilter;

    private SessionDAO sessionDAO = new SessionDAO();
    private Movie currentMovie;
    private User currentUser;

    @FXML
    public void initialize() {
        dateFilter.getItems().addAll("Все даты", "Сегодня", "Завтра", "Эта неделя");
        timeFilter.getItems().addAll("Все время", "Утро (09:00-12:00)", "День (12:00-18:00)", "Вечер (18:00-23:00)");

        dateFilter.setValue("Все даты");
        timeFilter.setValue("Все время");

        loadSessions();
    }

    public void setCurrentMovie(Movie movie) {
        this.currentMovie = movie;
        titleLabel.setText("Сеансы: " + movie.getTitle());
        loadSessions();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (currentMovie == null) {
            titleLabel.setText("Все сеансы");
        }
    }

    private void loadSessions() {
        sessionsContainer.getChildren().clear();

        List<Session> sessions;
        if (currentMovie != null) {
            sessions = sessionDAO.getSessionsByMovie(currentMovie.getId());
        } else {
            sessions = sessionDAO.getAllSessions();
        }

        if (sessions.isEmpty()) {
            Label noSessionsLabel = new Label("Нет доступных сеансов");
            noSessionsLabel.setStyle("-fx-font-size: 16; -fx-text-fill: #666;");
            sessionsContainer.getChildren().add(noSessionsLabel);
            return;
        }

        for (Session session : sessions) {
            VBox sessionCard = createSessionCard(session);
            sessionsContainer.getChildren().add(sessionCard);
        }
    }

    private VBox createSessionCard(Session session) {
        VBox card = new VBox();
        card.setSpacing(10);
        card.setStyle("-fx-padding: 15; -fx-border-color: #ddd; -fx-border-radius: 5; -fx-background-radius: 5; -fx-margin: 5;");
        card.setPrefWidth(600);

        Label movieLabel = new Label(session.getMovieTitle() + " - " + session.getHallName());
        movieLabel.setFont(Font.font("System", FontWeight.BOLD, 16));

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        Label timeLabel = new Label("📅 " + session.getSessionDate().format(dateFormatter) +
                "  🕒 " + session.getSessionTime().format(timeFormatter));
        timeLabel.setStyle("-fx-font-size: 14;");

        Label priceLabel = new Label("💵 Цена: " + session.getPrice() + " руб.");
        Label seatsLabel = new Label("🎫 Доступно мест: " + session.getAvailableSeats());

        if (session.getAvailableSeats() < 10) {
            seatsLabel.setStyle("-fx-text-fill: #ff4444; -fx-font-weight: bold;");
        } else {
            seatsLabel.setStyle("-fx-text-fill: #4CAF50;");
        }

        Button bookButton = new Button("Забронировать билеты");
        bookButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        bookButton.setOnAction(e -> onBookSession(session));

        if (session.getAvailableSeats() <= 0) {
            bookButton.setText("Нет мест");
            bookButton.setDisable(true);
            bookButton.setStyle("-fx-background-color: #cccccc;");
        }

        card.getChildren().addAll(movieLabel, timeLabel, priceLabel, seatsLabel, bookButton);
        return card;
    }

    private void onBookSession(Session session) {
        if (currentUser == null || "GUEST".equals(currentUser.getRole())) {
            showAlert("Ошибка", "Для бронирования билетов необходимо войти в систему");
            return;
        }

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("booking.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1000, 700);

            BookingController controller = fxmlLoader.getController();
            controller.setCurrentSession(session);
            controller.setCurrentUser(currentUser);

            Stage stage = (Stage) titleLabel.getScene().getWindow();
            stage.setTitle("Бронирование - " + session.getMovieTitle());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось загрузить страницу бронирования");
        }
    }

    @FXML
    protected void onFilterChange() {
        loadSessions();
    }

    @FXML
    protected void onBackClick(ActionEvent event) throws IOException {
        if (currentMovie != null) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("movies.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1000, 700);

            MoviesController controller = fxmlLoader.getController();
            controller.setCurrentUser(currentUser);

            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setTitle("Фильмы");
            stage.setScene(scene);
            stage.show();
        } else {
            loadScene("movies.fxml", "Фильмы", event);
        }
    }

    @FXML
    protected void onMoviesClick(ActionEvent event) throws IOException {
        loadScene("movies.fxml", "Фильмы", event);
    }

    private void loadScene(String fxmlFile, String title, ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 700);

        if (fxmlFile.equals("movies.fxml")) {
            MoviesController controller = fxmlLoader.getController();
            controller.setCurrentUser(currentUser);
        }

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