package com.cinema.ticket.controllers;

import com.cinema.ticket.dao.MovieDAO;
import com.cinema.ticket.dao.SessionDAO;
import com.cinema.ticket.models.Movie;
import com.cinema.ticket.models.Session;
import com.cinema.ticket.models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class MoviesController {

    @FXML private TextField searchField;
    @FXML private GridPane moviesGrid;
    @FXML private ScrollPane newMoviesPane;
    @FXML private HBox newMoviesBox;
    @FXML private ScrollPane recommendedPane;
    @FXML private HBox recommendedBox;
    @FXML private Label welcomeLabel;

    private MovieDAO movieDAO = new MovieDAO();
    private SessionDAO sessionDAO = new SessionDAO();
    private User currentUser;

    @FXML
    public void initialize() {
        loadMovies();
        loadNewMovies();
        loadRecommended();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (user != null) {
            welcomeLabel.setText("Добро пожаловать, " + user.getFullName() + "!");
        } else {
            welcomeLabel.setText("Добро пожаловать, Гость!");
        }
    }

    private void loadMovies() {
        moviesGrid.getChildren().clear();
        List<Movie> movies = movieDAO.getAllMovies();

        int column = 0;
        int row = 0;
        int columns = 3;

        for (Movie movie : movies) {
            VBox movieCard = createMovieCard(movie);
            moviesGrid.add(movieCard, column, row);

            column++;
            if (column >= columns) {
                column = 0;
                row++;
            }
        }
    }

    private void loadNewMovies() {
        newMoviesBox.getChildren().clear();
        List<Movie> newMovies = movieDAO.getNewMovies();

        for (Movie movie : newMovies) {
            VBox movieCard = createMovieCard(movie);
            newMoviesBox.getChildren().add(movieCard);
        }
    }

    private void loadRecommended() {
        recommendedBox.getChildren().clear();
        List<Movie> recommended = movieDAO.getNewMovies();

        for (Movie movie : recommended) {
            VBox movieCard = createMovieCard(movie);
            recommendedBox.getChildren().add(movieCard);
        }
    }

    private VBox createMovieCard(Movie movie) {
        VBox card = new VBox();
        card.setSpacing(10);
        card.setStyle("-fx-padding: 15; -fx-border-color: #ddd; -fx-border-radius: 5; -fx-background-radius: 5;");
        card.setPrefWidth(250);

        ImageView poster = new ImageView();
        poster.setFitWidth(200);
        poster.setFitHeight(300);
        poster.setPreserveRatio(true);

        try {
            if (movie.getPosterUrl() != null && !movie.getPosterUrl().isEmpty()) {
                poster.setImage(new Image(movie.getPosterUrl()));
            } else {
                poster.setImage(new Image("https://via.placeholder.com/200x300/cccccc/969696?text=No+Image"));
            }
        } catch (Exception e) {
            poster.setImage(new Image("https://via.placeholder.com/200x300/cccccc/969696?text=Error"));
        }

        Label titleLabel = new Label(movie.getTitle());
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(200);

        Label detailsLabel = new Label(movie.getGenre() + " • " + movie.getRating() + " ★");
        detailsLabel.setStyle("-fx-text-fill: #666;");

        Label ageLabel = new Label(movie.getAgeRating());
        ageLabel.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white; -fx-padding: 2 5; -fx-background-radius: 3;");

        Button selectButton = new Button("Выбрать сеанс");
        selectButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        selectButton.setOnAction(e -> onMovieSelect(movie));

        card.getChildren().addAll(poster, titleLabel, detailsLabel, ageLabel, selectButton);
        return card;
    }

    private void onMovieSelect(Movie movie) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("sessions.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1000, 700);

            SessionsController controller = fxmlLoader.getController();
            controller.setCurrentMovie(movie);
            controller.setCurrentUser(currentUser);

            Stage stage = (Stage) searchField.getScene().getWindow();
            stage.setTitle("Сеансы - " + movie.getTitle());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось загрузить страницу сеансов");
        }
    }

    @FXML
    protected void onSearchClick() {
        String query = searchField.getText().trim().toLowerCase();

        if (query.isEmpty()) {
            loadMovies();
            return;
        }

        moviesGrid.getChildren().clear();
        List<Movie> allMovies = movieDAO.getAllMovies();

        int column = 0;
        int row = 0;
        int columns = 3;

        for (Movie movie : allMovies) {
            if (movie.getTitle().toLowerCase().contains(query) ||
                    movie.getGenre().toLowerCase().contains(query)) {

                VBox movieCard = createMovieCard(movie);
                moviesGrid.add(movieCard, column, row);

                column++;
                if (column >= columns) {
                    column = 0;
                    row++;
                }
            }
        }
    }

    @FXML
    protected void onProfileClick(ActionEvent event) throws IOException {
        if (currentUser != null && !currentUser.isGuest()) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("profile.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1000, 700);

            ProfileController controller = fxmlLoader.getController();
            controller.setCurrentUser(currentUser);

            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setTitle("Мой профиль");
            stage.setScene(scene);
            stage.show();
        } else {
            showAlert("Ошибка", "Эта функция доступна только зарегистрированным пользователям");
        }
    }

    @FXML
    protected void onLogoutClick(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.setTitle("CineMax - Кинотеатр");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    protected void onSessionsClick(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("sessions.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 700);

        SessionsController controller = fxmlLoader.getController();
        controller.setCurrentUser(currentUser);

        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.setTitle("Все сеансы");
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