package com.cinema.ticket.controllers;

import com.cinema.ticket.CinemaApp;
import com.cinema.ticket.models.User;
import com.cinema.ticket.models.Movie;
import com.cinema.ticket.models.Session;
import com.cinema.ticket.models.Ticket;
import com.cinema.ticket.dao.SessionDAO;
import com.cinema.ticket.dao.TicketDAO;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BookingController {

    @FXML private Label movieTitleLabel;
    @FXML private Label stepLabel;
    @FXML private VBox seatsContainer;
    @FXML private Label selectedSeatsLabel;
    @FXML private Label totalPriceLabel;
    @FXML private Button bookButton;

    private User currentUser;
    private Movie selectedMovie;
    private Session selectedSession;
    private final SessionDAO sessionDAO = new SessionDAO();
    private final TicketDAO ticketDAO = new TicketDAO();
    private final List<Integer> selectedSeats = new ArrayList<>();

    private static final double TICKET_PRICE = 250.0;
    private static final int ROWS = 9;
    private static final int SEATS_PER_ROW = 10;

    @FXML
    public void initialize() {}

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public void setMovie(Movie movie) {
        this.selectedMovie = movie;
        if (movieTitleLabel != null) {
            movieTitleLabel.setText("🎬 " + movie.getTitle());
        }
        showSessionStep();
    }

    // Выбор сеанса
    private void showSessionStep() {
        seatsContainer.getChildren().clear();
        selectedSeats.clear();
        updateBottomBar();
        stepLabel.setText("Выберите сеанс");

        List<Session> sessions = sessionDAO.getSessionsByMovieId(selectedMovie.getId());

        if (sessions.isEmpty()) {
            Label noSessions = new Label("Нет доступных сеансов для этого фильма");
            noSessions.setStyle(
                    "-fx-font-size: 14;" +
                            "-fx-text-fill: #e50914;" +
                            "-fx-padding: 30;"
            );
            seatsContainer.getChildren().add(noSessions);
            return;
        }

        VBox sessionList = new VBox(14);
        sessionList.setPadding(new Insets(15));
        sessionList.setAlignment(Pos.TOP_CENTER);

        Label hint = new Label("Выберите удобный сеанс:");
        hint.setStyle(
                "-fx-font-size: 14;" +
                        "-fx-text-fill: #d0d0d0;" +
                        "-fx-padding: 0 0 10 0;"
        );
        sessionList.getChildren().add(hint);

        for (Session session : sessions) {
            Button sessionBtn = new Button();
            sessionBtn.setPrefWidth(460);
            sessionBtn.setPrefHeight(62);

            String dateStr = session.getSessionDate() != null ? session.getSessionDate() : "—";
            String timeStr = session.getSessionTime() != null ? session.getSessionTime() : "—";
            sessionBtn.setText("📅  " + dateStr + "     🕐  " + timeStr +
                    "     |     Зал №" + session.getHallNumber());

            String normalStyle =
                    "-fx-background-color: #16213e;" +
                            "-fx-border-color: #e50914;" +
                            "-fx-border-width: 2;" +
                            "-fx-border-radius: 10;" +
                            "-fx-background-radius: 10;" +
                            "-fx-font-size: 14;" +
                            "-fx-font-weight: bold;" +
                            "-fx-text-fill: #ffffff;" +
                            "-fx-cursor: hand;" +
                            "-fx-effect: dropshadow(gaussian, rgba(229,9,20,0.2), 10, 0, 0, 2);";

            String hoverStyle =
                    "-fx-background-color: #e50914;" +
                            "-fx-border-color: #e50914;" +
                            "-fx-border-width: 2;" +
                            "-fx-border-radius: 10;" +
                            "-fx-background-radius: 10;" +
                            "-fx-font-size: 14;" +
                            "-fx-font-weight: bold;" +
                            "-fx-text-fill: #ffffff;" +
                            "-fx-cursor: hand;" +
                            "-fx-effect: dropshadow(gaussian, rgba(229,9,20,0.5), 15, 0, 0, 4);";

            sessionBtn.setStyle(normalStyle);
            sessionBtn.setOnMouseEntered(e -> sessionBtn.setStyle(hoverStyle));
            sessionBtn.setOnMouseExited(e -> sessionBtn.setStyle(normalStyle));
            sessionBtn.setOnAction(e -> {
                this.selectedSession = session;
                showSeatsStep();
            });

            sessionList.getChildren().add(sessionBtn);
        }

        seatsContainer.getChildren().add(sessionList);
    }

    // Выбор мест
    private void showSeatsStep() {
        seatsContainer.getChildren().clear();
        selectedSeats.clear();
        updateBottomBar();

        String dateStr = selectedSession.getSessionDate() != null
                ? selectedSession.getSessionDate() : "—";
        String timeStr = selectedSession.getSessionTime() != null
                ? selectedSession.getSessionTime() : "—";

        stepLabel.setText("Выберите места  |  📅 " + dateStr +
                "  🕐 " + timeStr + "  | Зал №" + selectedSession.getHallNumber());

        // Кнопка назад
        Button backBtn = new Button("← Назад к сеансам");
        String backNormal =
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #e50914;" +
                        "-fx-border-color: #e50914;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 7 16;" +
                        "-fx-cursor: hand;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 13;";
        String backHover =
                "-fx-background-color: #e50914;" +
                        "-fx-text-fill: #ffffff;" +
                        "-fx-border-color: #e50914;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 7 16;" +
                        "-fx-cursor: hand;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 13;";
        backBtn.setStyle(backNormal);
        backBtn.setOnMouseEntered(e -> backBtn.setStyle(backHover));
        backBtn.setOnMouseExited(e -> backBtn.setStyle(backNormal));
        backBtn.setOnAction(e -> showSessionStep());
        seatsContainer.getChildren().add(backBtn);

        seatsContainer.getChildren().add(createScreenBox());

        // Места
        VBox seatsBox = new VBox(8);
        seatsBox.setAlignment(Pos.CENTER);

        for (int row = 1; row <= ROWS; row++) {
            HBox rowBox = new HBox(6);
            rowBox.setAlignment(Pos.CENTER);

            Label leftNum = new Label(String.valueOf(row));
            leftNum.setStyle("-fx-font-weight: bold; -fx-font-size: 12; -fx-text-fill: #7a7a9a;");
            leftNum.setPrefWidth(30);
            leftNum.setAlignment(Pos.CENTER);
            rowBox.getChildren().add(leftNum);

            for (int seat = 1; seat <= SEATS_PER_ROW; seat++) {
                rowBox.getChildren().add(createSeatButton(row, seat, selectedSession.getId()));
            }

            Label rightNum = new Label(String.valueOf(row));
            rightNum.setStyle("-fx-font-weight: bold; -fx-font-size: 12; -fx-text-fill: #7a7a9a;");
            rightNum.setPrefWidth(30);
            rightNum.setAlignment(Pos.CENTER);
            rowBox.getChildren().add(rightNum);

            seatsBox.getChildren().add(rowBox);
        }

        seatsContainer.getChildren().add(seatsBox);
        seatsContainer.getChildren().add(createLegendBox());
    }

    // UI компоненты
    private VBox createScreenBox() {
        VBox box = new VBox(6);
        box.setAlignment(Pos.CENTER);
        box.setStyle(
                "-fx-padding: 18;" +
                        "-fx-border-color: #2a2a5a;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-color: #16213e;" +
                        "-fx-background-radius: 8;"
        );

        Label screen = new Label("🎥  ЭКРАН");
        screen.setStyle(
                "-fx-font-size: 15;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #ffffff;" +
                        "-fx-letter-spacing: 4;"
        );

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #2a2a5a;");

        Label info = new Label("← Смотреть отсюда →");
        info.setStyle("-fx-font-size: 12; -fx-text-fill: #7a7a9a; -fx-padding: 6 0 0 0;");

        box.getChildren().addAll(screen, sep, info);
        return box;
    }

    private Button createSeatButton(int row, int seat, int sessionId) {
        Button btn = new Button(row + "-" + seat);
        btn.setPrefWidth(36);
        btn.setPrefHeight(36);

        int seatNumber = row * 100 + seat;
        final boolean[] selected = {false};

        String freeStyle =
                "-fx-font-size: 8; -fx-padding: 2;" +
                        "-fx-background-color: #1db954;" +
                        "-fx-text-fill: white;" +
                        "-fx-border-radius: 5; -fx-background-radius: 5;" +
                        "-fx-cursor: hand;";

        String selectedStyle =
                "-fx-font-size: 8; -fx-padding: 2;" +
                        "-fx-background-color: #4a9eff;" +
                        "-fx-text-fill: white;" +
                        "-fx-border-radius: 5; -fx-background-radius: 5;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(74,158,255,0.6), 8, 0, 0, 0);";

        String occupiedStyle =
                "-fx-font-size: 8; -fx-padding: 2;" +
                        "-fx-background-color: #e50914;" +
                        "-fx-text-fill: white;" +
                        "-fx-border-radius: 5; -fx-background-radius: 5;";

        if (ticketDAO.isSeatOccupied(sessionId, seatNumber)) {
            btn.setStyle(occupiedStyle);
            btn.setDisable(true);
        } else {
            btn.setStyle(freeStyle);
            btn.setOnAction(e -> {
                if (!selected[0]) {
                    btn.setStyle(selectedStyle);
                    selectedSeats.add(seatNumber);
                    selected[0] = true;
                } else {
                    btn.setStyle(freeStyle);
                    selectedSeats.remove(Integer.valueOf(seatNumber));
                    selected[0] = false;
                }
                updateBottomBar();
            });
        }
        return btn;
    }

    private VBox createLegendBox() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(16));
        box.setStyle(
                "-fx-border-color: #2a2a5a;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-color: #16213e;" +
                        "-fx-background-radius: 10;"
        );

        Label title = new Label("Легенда:");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 12; -fx-text-fill: #d0d0d0;");

        HBox items = new HBox(30);
        items.setAlignment(Pos.CENTER_LEFT);
        items.getChildren().addAll(
                legendItem("#1db954", "Свободное"),
                legendItem("#4a9eff", "Выбранное"),
                legendItem("#e50914", "Занято")
        );

        box.getChildren().addAll(title, items);
        return box;
    }

    private HBox legendItem(String color, String text) {
        HBox hbox = new HBox(8);
        hbox.setAlignment(Pos.CENTER_LEFT);

        Button btn = new Button();
        btn.setPrefSize(26, 26);
        btn.setStyle(
                "-fx-background-color: " + color + ";" +
                        "-fx-border-radius: 4; -fx-background-radius: 4;"
        );
        btn.setDisable(true);

        Label label = new Label(text);
        label.setStyle("-fx-font-size: 11; -fx-text-fill: #d0d0d0;");

        hbox.getChildren().addAll(btn, label);
        return hbox;
    }

    // нижняя панель
    private void updateBottomBar() {
        if (selectedSeatsLabel == null || totalPriceLabel == null) return;

        if (selectedSeats.isEmpty()) {
            selectedSeatsLabel.setText("Выбранные места: нет");
            totalPriceLabel.setText("Общая стоимость: 0 руб.");
            bookButton.setDisable(true);
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (Integer s : selectedSeats) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(s / 100).append("-").append(s % 100);
        }

        double total = selectedSeats.size() * TICKET_PRICE;
        selectedSeatsLabel.setText("Выбранные места: " + sb);
        totalPriceLabel.setText("Общая стоимость: " + total + " руб.");
        bookButton.setDisable(false);
    }

    // бронирование
    @FXML
    protected void onBookClick(ActionEvent event) {
        if (currentUser == null || currentUser.isGuest()) { showAuthError(); return; }
        if (selectedSeats.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Ошибка", "Выберите места для бронирования");
            return;
        }
        try {
            for (int seat : selectedSeats) {
                Ticket t = new Ticket();
                t.setUserId(currentUser.getId());
                t.setSessionId(selectedSession.getId());
                t.setSeatNumber(seat);
                t.setTotalPrice(TICKET_PRICE);
                t.setStatus("BOOKED");
                ticketDAO.createTicket(t);
            }
            showAlert(Alert.AlertType.INFORMATION, "Успех",
                    "Билеты забронированы!\nМест: " + selectedSeats.size() +
                            "\nСумма: " + (selectedSeats.size() * TICKET_PRICE) + " руб.");
            goBackToMovies();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "❌ Ошибка", "Не удалось забронировать билеты");
            e.printStackTrace();
        }
    }

    @FXML
    protected void onCancelClick(ActionEvent event) {
        goBackToMovies();
    }

    // назад
    private void goBackToMovies() {
        try {
            FXMLLoader loader = new FXMLLoader(CinemaApp.class.getResource("movies.fxml"));
            Scene scene = new Scene(loader.load());
            MoviesController controller = loader.getController();
            controller.setCurrentUser(currentUser);
            Stage stage = (Stage) seatsContainer.getScene().getWindow();
            stage.setTitle("CineMax - Фильмы");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAuthError() {
        showAlert(Alert.AlertType.WARNING, "⚠️ Ошибка",
                "Гостям запрещено бронировать билеты\nАвторизуйтесь.");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}