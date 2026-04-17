package com.cinema.ticket;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CinemaApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cinema/ticket/main.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1200, 800);

        var cssUrl = getClass().getResource("/com/cinema/ticket/dark-table.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        } else {
        }

        primaryStage.setTitle("CineMax - Кинотеатр");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

