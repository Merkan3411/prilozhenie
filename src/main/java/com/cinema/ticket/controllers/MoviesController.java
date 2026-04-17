package com.cinema.ticket.controllers;

import com.cinema.ticket.CinemaApp;
import com.cinema.ticket.dao.MovieDAO;
import com.cinema.ticket.models.Movie;
import com.cinema.ticket.models.User;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class MoviesController {

    @FXML private Label welcomeLabel;
    @FXML private TextField searchField;
    @FXML private GridPane moviesGrid;

    private User currentUser;
    private final MovieDAO movieDAO = new MovieDAO();
    private List<Movie> allMovies;  // хранит все фильмы из БД

    private static final int CARDS_PER_ROW = 4;

    @FXML
    public void initialize() {
        loadMovies();

        // Авто-фильтрация по вводу текста
        searchField.textProperty().addListener((obs, oldText, newText) -> filterMovies(newText));
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;

        if (user != null && !user.isGuest()) {
            welcomeLabel.setText("🎬 Фильмы, " + user.getFullName());
        } else {
            welcomeLabel.setText("🎬 Фильмы");
        }

        loadMovies();
    }

    private void loadMovies() {
        Task<List<Movie>> task = new Task<>() {
            @Override
            protected List<Movie> call() {
                return movieDAO.getAllMovies();
            }
        };

        task.setOnSucceeded(e -> {
            allMovies = task.getValue();
            Platform.runLater(() -> displayMovies(allMovies));
        });

        task.setOnFailed(e -> {
            task.getException().printStackTrace();
            Platform.runLater(() -> {
                Label error = new Label("Ошибка загрузки фильмов");
                moviesGrid.add(error, 0, 0);
            });
        });

        new Thread(task).start();
    }

    private void filterMovies(String query) {
        if (allMovies == null) return;

        if (query == null || query.isBlank()) {
            displayMovies(allMovies);
            return;
        }

        String lowerQuery = query.toLowerCase();

        List<Movie> filtered = allMovies.stream()
                .filter(m -> m.getTitle().toLowerCase().contains(lowerQuery))
                .toList();

        displayMovies(filtered);
    }

    private void displayMovies(List<Movie> movies) {
        moviesGrid.getChildren().clear();
        moviesGrid.getColumnConstraints().clear();

        for (int i = 0; i < CARDS_PER_ROW; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(100.0 / CARDS_PER_ROW);
            col.setHgrow(Priority.ALWAYS);
            col.setMinWidth(220);
            col.setPrefWidth(260);
            moviesGrid.getColumnConstraints().add(col);
        }

        if (movies == null || movies.isEmpty()) {
            Label empty = new Label("Нет доступных фильмов");
            empty.setStyle("-fx-font-size: 18; -fx-text-fill: #64748b; -fx-font-weight: bold;");
            empty.setAlignment(Pos.CENTER);
            moviesGrid.add(empty, 0, 0);
            GridPane.setColumnSpan(empty, CARDS_PER_ROW);
            return;
        }

        int col = 0;
        int row = 0;

        for (Movie movie : movies) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/cinema/ticket/movie_card.fxml")
                );

                VBox card = loader.load();
                MovieCardController controller = loader.getController();
                controller.setData(movie, currentUser, this);

                GridPane.setHgrow(card, Priority.ALWAYS);
                GridPane.setFillWidth(card, true);
                GridPane.setMargin(card, new Insets(10));

                moviesGrid.add(card, col, row);

                col++;
                if (col >= CARDS_PER_ROW) {
                    col = 0;
                    row++;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        moviesGrid.setAlignment(Pos.TOP_CENTER);
        moviesGrid.setHgap(20);
        moviesGrid.setVgap(24);
    }

    @FXML
    protected void onSearchClick(ActionEvent event) {
        filterMovies(searchField.getText());
    }

    @FXML
    protected void onSessionsClick(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(CinemaApp.class.getResource("sessions.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) moviesGrid.getScene().getWindow();
        stage.setTitle("CineMax - Сеансы");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    protected void onProfileClick(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(CinemaApp.class.getResource("profile.fxml"));
        Scene scene = new Scene(loader.load());
        ProfileController controller = loader.getController();
        controller.setCurrentUser(currentUser);
        Stage stage = (Stage) moviesGrid.getScene().getWindow();
        stage.setTitle("CineMax - Профиль");
        stage.setScene(scene);
        stage.show();
    }

    void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Уведомление");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    void onBookMovie(Movie movie) {
        if (currentUser == null || currentUser.isGuest()) {
            showWarning("Гостям запрещено бронировать билеты.\n\nПожалуйста, войдите или зарегистрируйтесь.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(CinemaApp.class.getResource("booking.fxml"));
            Scene scene = new Scene(loader.load());
            BookingController controller = loader.getController();
            controller.setMovie(movie);
            controller.setCurrentUser(currentUser);
            Stage stage = (Stage) moviesGrid.getScene().getWindow();
            stage.setTitle("CineMax - Бронирование: " + movie.getTitle());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}