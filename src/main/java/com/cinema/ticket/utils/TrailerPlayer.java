package com.cinema.ticket.utils;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.net.URI;

public class TrailerPlayer {

    public static void show(String trailerUrl, String movieTitle) {
        if (trailerUrl == null || trailerUrl.trim().isEmpty()) {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Трейлер");
            a.setHeaderText(null);
            a.setContentText("Трейлер для этого фильма не добавлен.");
            a.showAndWait();
            return;
        }

        String url = trailerUrl.trim();

        if (isDirectMedia(url)) {
            showMediaPlayer(url, movieTitle);
        } else {
            openInSystemBrowser(url, movieTitle);
        }
    }


    private static void showMediaPlayer(String url, String movieTitle) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Трейлер: " + (movieTitle != null ? movieTitle : ""));

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #000000;");

        HBox topBar = new HBox(10);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(10, 15, 10, 15));
        topBar.setStyle("-fx-background-color: #16213e;");
        Label title = new Label("▶  " + (movieTitle != null ? movieTitle : "Трейлер"));
        title.setStyle("-fx-text-fill: #ffffff; -fx-font-weight: bold; -fx-font-size: 14;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button closeBtn = new Button("✕ Закрыть");
        closeBtn.setStyle("-fx-background-color: #e50914; -fx-text-fill: white;" +
                "-fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 6 14;" +
                "-fx-cursor: hand;");
        topBar.getChildren().addAll(title, spacer, closeBtn);
        root.setTop(topBar);

        try {
            Media media = new Media(url);
            MediaPlayer player = new MediaPlayer(media);
            MediaView view = new MediaView(player);
            view.setPreserveRatio(true);
            view.setFitWidth(960);
            view.setFitHeight(540);
            player.setAutoPlay(true);

            Runnable closeAndDispose = () -> {
                player.dispose();
                stage.close();
            };
            stage.setOnCloseRequest(e -> player.dispose());
            closeBtn.setOnAction(e -> closeAndDispose.run());

            player.setOnError(() -> javafx.application.Platform.runLater(() -> {
                player.dispose();
                stage.close();
                openInSystemBrowser(url, movieTitle);
            }));

            root.setCenter(view);
        } catch (Exception ex) {
            stage.close();
            openInSystemBrowser(url, movieTitle);
            return;
        }

        Scene scene = new Scene(root, 980, 620);
        stage.setScene(scene);
        stage.show();
    }

    private static void openInSystemBrowser(String url, String movieTitle) {
        boolean opened = tryOpenBrowser(url);

        if (!opened) {
            showLinkFallback(url, movieTitle);
            return;
        }

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Трейлер: " + (movieTitle != null ? movieTitle : ""));

        VBox root = new VBox(14);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: #0f0f1a;");

        Label title = new Label("▶  Трейлер открыт в браузере");
        title.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 15; -fx-font-weight: bold;");

        Label subtitle = new Label(movieTitle != null ? movieTitle : "");
        subtitle.setStyle("-fx-text-fill: #c0c0c0; -fx-font-size: 13;");

        Label hint = new Label("Встроенный плеер JavaFX не поддерживает\n" +
                "видеостриминг RuTube, поэтому трейлер\n" +
                "открывается в системном браузере.");
        hint.setStyle("-fx-text-fill: #7a7a9a; -fx-font-size: 12;" +
                "-fx-text-alignment: center;");
        hint.setWrapText(true);

        Button reopenBtn = new Button("Открыть снова");
        reopenBtn.setStyle("-fx-background-color: #1db954; -fx-text-fill: white;" +
                "-fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 18;" +
                "-fx-cursor: hand;");
        reopenBtn.setOnAction(e -> tryOpenBrowser(url));

        Button closeBtn = new Button("Закрыть");
        closeBtn.setStyle("-fx-background-color: #e50914; -fx-text-fill: white;" +
                "-fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 18;" +
                "-fx-cursor: hand;");
        closeBtn.setOnAction(e -> stage.close());

        HBox btns = new HBox(10, reopenBtn, closeBtn);
        btns.setAlignment(Pos.CENTER);

        root.getChildren().addAll(title, subtitle, hint, btns);

        Scene scene = new Scene(root, 420, 240);
        stage.setScene(scene);
        stage.show();
    }

    private static boolean tryOpenBrowser(String url) {
        try {
            if (Desktop.isDesktopSupported()
                    && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(URI.create(url));
                return true;
            }
        } catch (Exception ignored) {}

        try {
            String os = System.getProperty("os.name", "").toLowerCase();
            ProcessBuilder pb;
            if (os.contains("win")) {
                pb = new ProcessBuilder("cmd", "/c", "start", "", url);
            } else if (os.contains("mac")) {
                pb = new ProcessBuilder("open", url);
            } else {
                pb = new ProcessBuilder("xdg-open", url);
            }
            pb.start();
            return true;
        } catch (Exception ignored) {}

        return false;
    }

    private static void showLinkFallback(String url, String movieTitle) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Трейлер");
        a.setHeaderText(movieTitle != null ? movieTitle : "Трейлер");
        a.setContentText("Не удалось открыть браузер.\nСкопируйте ссылку:\n\n" + url);
        a.showAndWait();
    }

    private static boolean isDirectMedia(String url) {
        String lower = url.toLowerCase();
        return lower.endsWith(".mp4") || lower.endsWith(".m4v") ||
                lower.endsWith(".flv");
    }
}
