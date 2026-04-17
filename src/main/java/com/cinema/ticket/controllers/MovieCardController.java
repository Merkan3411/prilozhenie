package com.cinema.ticket.controllers;

import com.cinema.ticket.dao.ReviewDAO;
import com.cinema.ticket.models.Movie;
import com.cinema.ticket.models.Review;
import com.cinema.ticket.models.User;
import com.cinema.ticket.utils.SupabaseStorage;
import com.cinema.ticket.utils.TrailerPlayer;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.InputStream;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MovieCardController {

    @FXML private VBox cardContainer;
    @FXML private ImageView posterImage;
    @FXML private Label titleLabel;
    @FXML private Label genreLabel;
    @FXML private Label durationLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label ratingLabel;
    @FXML private Button bookButton;
    @FXML private Button trailerButton;
    @FXML private Button reviewsButton;

    private Movie movie;
    private User currentUser;
    private MoviesController moviesController;
    private final ReviewDAO reviewDAO = new ReviewDAO();

    public void setData(Movie movie, User user, MoviesController controller) {
        this.movie = movie;
        this.currentUser = user;
        this.moviesController = controller;

        fillMovieData();
        loadPosterImageAsync();
        configureBookButton();
        configureTrailerButton();
        configureReviewsButton();
        loadRatingAsync();
    }

    private void fillMovieData() {
        if (movie == null) return;

        titleLabel.setText(safe(movie.getTitle()));
        genreLabel.setText(safe(movie.getGenre()));
        descriptionLabel.setText(safe(movie.getDescription()));

        durationLabel.setText(movie.getDuration() > 0 ? movie.getDuration() + " мин" : "");

        if (ratingLabel != null) {
            ratingLabel.setText("★ —");
        }
    }

    private String safe(String s) {
        return (s != null) ? s : "";
    }

    private void loadRatingAsync() {
        if (movie == null || ratingLabel == null) return;
        final int movieId = movie.getId();
        Task<double[]> task = new Task<>() {
            @Override
            protected double[] call() {
                double avg = reviewDAO.getAverageRating(movieId);
                int count = reviewDAO.getReviewsCount(movieId);
                return new double[]{avg, count};
            }
        };
        task.setOnSucceeded(e -> Platform.runLater(() -> {
            double[] v = task.getValue();
            double avg = v[0];
            int count = (int) v[1];
            if (count == 0) {
                ratingLabel.setText("★ Нет оценок");
            } else {
                ratingLabel.setText(String.format("★ %.1f (%d)", avg, count));
            }
        }));
        new Thread(task).start();
    }

    private void loadPosterImageAsync() {
        String rawPoster = movie.getPosterUrl();


        if (rawPoster == null || rawPoster.trim().isEmpty()) {
            setDefaultPoster();
            return;
        }

        String posterUrl = rawPoster.trim();
        if (!posterUrl.startsWith("http")) {
            posterUrl = SupabaseStorage.getPosterUrl(posterUrl);
        }

        final String finalUrl = posterUrl;

        Task<Image> task = new Task<>() {
            @Override
            protected Image call() throws Exception {
                try (InputStream is = new URL(finalUrl).openStream()) {
                    Image img = new Image(is, 200, 300, false, true);
                    if (img.isError() || img.getWidth() < 50 || img.getHeight() < 50) {
                        throw new Exception("Изображение битое или слишком маленькое");
                    }
                    return img;
                }
            }
        };

        task.setOnSucceeded(e -> Platform.runLater(() -> posterImage.setImage(task.getValue())));

        task.setOnFailed(e -> {
            Platform.runLater(this::setDefaultPoster);
            System.err.println("[Poster ERROR] " + movie.getTitle() + " → " + finalUrl + " | " +
                    (task.getException() != null ? task.getException().getMessage() : "неизвестно"));
        });

        new Thread(task).start();
    }

    private void setDefaultPoster() {
        Image def = getDefaultPoster();
        if (def != null) {
            posterImage.setImage(def);
        } else {
            cardContainer.setStyle("-fx-background-color: #f1f5f9; -fx-border-color: #cbd5e1;");
        }
    }

    private Image getDefaultPoster() {
        try {
            URL url = getClass().getResource("/images/no-poster.jpg");
            if (url != null) {
                return new Image(url.toExternalForm(), 200, 300, false, true);
            }
        } catch (Exception ignored) {}
        return null;
    }

    private void configureBookButton() {
        bookButton.setVisible(true);
        bookButton.setManaged(true);

        bookButton.setOnAction(e -> {
            if (currentUser == null || currentUser.isGuest()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Требуется вход");
                alert.setHeaderText(null);
                alert.setContentText("Гостям запрещено бронировать билеты.\n\nВойдите или зарегистрируйтесь.");
                alert.showAndWait();
            } else {
                if (moviesController != null) {
                    moviesController.onBookMovie(movie);
                }
            }
        });
    }

    private void configureTrailerButton() {
        if (trailerButton == null) return;
        trailerButton.setOnAction(e ->
                TrailerPlayer.show(movie.getTrailerUrl(), movie.getTitle()));
    }

    private void configureReviewsButton() {
        if (reviewsButton == null) return;
        reviewsButton.setOnAction(e -> showReviewsDialog());
    }

    private void showReviewsDialog() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Отзывы: " + movie.getTitle());

        VBox root = new VBox(10);
        root.setPadding(new Insets(18));
        root.setStyle("-fx-background-color: #0f0f1a;");

        Label header = new Label("🎬  Отзывы о фильме \"" + movie.getTitle() + "\"");
        header.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #ffffff;");
        root.getChildren().add(header);

        Label loading = new Label("Загрузка...");
        loading.setStyle("-fx-text-fill: #7a7a9a; -fx-font-size: 13;");
        root.getChildren().add(loading);

        VBox list = new VBox(10);
        ScrollPane scroll = new ScrollPane(list);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(460);
        scroll.setStyle("-fx-background: #0f0f1a; -fx-background-color: #0f0f1a;");

        Button closeBtn = new Button("Закрыть");
        closeBtn.setStyle("-fx-background-color: #e50914; -fx-text-fill: white;" +
                "-fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 18; -fx-cursor: hand;");
        closeBtn.setOnAction(e -> stage.close());

        Task<List<Review>> task = new Task<>() {
            @Override
            protected List<Review> call() {
                return reviewDAO.getReviewsByMovieId(movie.getId());
            }
        };
        task.setOnSucceeded(e -> Platform.runLater(() -> {
            root.getChildren().remove(loading);
            List<Review> reviews = task.getValue();
            if (reviews.isEmpty()) {
                Label empty = new Label("Пока нет отзывов. Станьте первым!");
                empty.setStyle("-fx-text-fill: #7a7a9a; -fx-font-size: 13; -fx-padding: 20;");
                list.getChildren().add(empty);
            } else {
                double avg = reviews.stream().mapToDouble(Review::getRating).average().orElse(0);
                Label avgLbl = new Label(String.format("Средняя оценка: ★ %.1f  |  Отзывов: %d",
                        avg, reviews.size()));
                avgLbl.setStyle("-fx-text-fill: #ffc107; -fx-font-size: 13; -fx-font-weight: bold;" +
                        "-fx-padding: 0 0 6 0;");
                list.getChildren().add(avgLbl);
                for (Review r : reviews) {
                    list.getChildren().add(buildReviewCard(r));
                }
            }
            root.getChildren().addAll(scroll, closeBtn);
        }));
        task.setOnFailed(e -> Platform.runLater(() -> {
            root.getChildren().remove(loading);
            Label err = new Label("Не удалось загрузить отзывы.");
            err.setStyle("-fx-text-fill: #e50914;");
            root.getChildren().addAll(err, closeBtn);
        }));
        new Thread(task).start();

        Scene scene = new Scene(root, 560, 620);
        stage.setScene(scene);
        stage.show();
    }

    private VBox buildReviewCard(Review r) {
        VBox card = new VBox(6);
        card.setStyle("-fx-background-color: #16213e; -fx-background-radius: 10;" +
                "-fx-border-color: #2a2a5a; -fx-border-radius: 10; -fx-padding: 12;");

        HBox top = new HBox(10);
        top.setAlignment(Pos.CENTER_LEFT);
        Label name = new Label("👤 " + (r.getUserName() != null ? r.getUserName() : "Аноним"));
        name.setStyle("-fx-text-fill: #ffffff; -fx-font-weight: bold; -fx-font-size: 13;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label rate = new Label(String.format("★ %.1f", r.getRating()));
        rate.setStyle("-fx-text-fill: #ffc107; -fx-font-weight: bold; -fx-font-size: 13;");
        top.getChildren().addAll(name, spacer, rate);
        card.getChildren().add(top);

        if (r.getCreatedAt() != null) {
            Label date = new Label(r.getCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
            date.setStyle("-fx-text-fill: #7a7a9a; -fx-font-size: 11;");
            card.getChildren().add(date);
        }

        if (r.getReviewText() != null && !r.getReviewText().isEmpty()) {
            Label text = new Label(r.getReviewText());
            text.setWrapText(true);
            text.setStyle("-fx-text-fill: #d0d0d0; -fx-font-size: 13;");
            card.getChildren().add(text);
        }
        return card;
    }
}
