package com.cinema.ticket.controllers;

import com.cinema.ticket.dao.TicketDAO;
import com.cinema.ticket.models.Ses sion;
import com.cinema.ticket.models.Ticket;
import com.cinema.ticket.models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BookingController {

    @FXML private Label movieTitleLabel;
    @FXML private Label sessionInfoLabel;
    @FXML private Label hallLabel;
    @FXML private Label priceLabel;
    @FXML private GridPane seatsGrid;
    @FXML private Label selectedSeatsLabel;
    @FXML private Label totalPriceLabel;
    @FXML private Button bookButton;
    @FXML private Button buyButton;

    private Session currentSession;
    private User currentUser;
    private TicketDAO ticketDAO = new TicketDAO();
    private List<String> selectedSeats = new ArrayList<>();
    private int rows = 8;
    private int seatsPerRow = 10;

    @FXML
    public void initialize() {
        initializeSeatGrid();
    }

    public void setCurrentSession(Session session) {
        this.currentSession = session;
        updateSessionInfo();
        updateSeatAvailability();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    private void updateSessionInfo() {
        if (currentSession != null) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

            movieTitleLabel.setText(currentSession.getMovieTitle());
            sessionInfoLabel.setText("📅 " + currentSession.getSessionDate().format(dateFormatter) +
                    "  🕒 " + currentSession.getSessionTime().format(timeFormatter));
            hallLabel.setText("🎭 " + currentSession.getHallName());
            priceLabel.setText("💵 " + currentSession.getPrice() + " руб. за место");
        }
    }

    private void initializeSeatGrid() {
        seatsGrid.getChildren().clear();
        seatsGrid.setHgap(5);
        seatsGrid.setVgap(5);

        for (int row = 0; row < rows; row++) {
            Label rowLabel = new Label("Ряд " + (row + 1));
            rowLabel.setStyle("-fx-font-weight: bold;");
            seatsGrid.add(rowLabel, 0, row);
        }

        for (int row = 0; row < rows; row++) {
            for (int seat = 0; seat < seatsPerRow; seat++) {
                Button seatButton = createSeatButton(row + 1, seat + 1);
                seatsGrid.add(seatButton, seat + 1, row);
            }
        }

        HBox legend = createLegend();
        seatsGrid.add(legend, 0, rows, seatsPerRow + 1, 1);
    }

    private Button createSeatButton(int row, int seat) {
        Button button = new Button(seat + "");
        button.setPrefSize(30, 30);
        button.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

        button.setOnAction(e -> onSeatSelect(row, seat, button));

        return button;
    }

    private HBox createLegend() {
        HBox legend = new HBox(10);
        legend.setStyle("-fx-padding: 10; -fx-alignment: center;");

        VBox availableBox = new VBox(5);
        Rectangle availableRect = new Rectangle(20, 20, Color.GREEN);
        Label availableLabel = new Label("Свободно");
        availableBox.getChildren().addAll(availableRect, availableLabel);

        VBox selectedBox = new VBox(5);
        Rectangle selectedRect = new Rectangle(20, 20, Color.BLUE);
        Label selectedLabel = new Label("Выбрано");
        selectedBox.getChildren().addAll(selectedRect, selectedLabel);

        VBox occupiedBox = new VBox(5);
        Rectangle occupiedRect = new Rectangle(20, 20, Color.RED);
        Label occupiedLabel = new Label("Занято");
        occupiedBox.getChildren().addAll(occupiedRect, occupiedLabel);

        legend.getChildren().addAll(availableBox, selectedBox, occupiedBox);
        return legend;
    }

    private void updateSeatAvailability() {
        if (currentSession == null) return;

        for (int row = 1; row <= rows; row++) {
            for (int seat = 1; seat <= seatsPerRow; seat++) {
                Button seatButton = getSeatButton(row, seat);
                if (seatButton != null) {
                    boolean isAvailable = ticketDAO.isSeatAvailable(currentSession.getId(), row, seat);
                    if (!isAvailable) {
                        seatButton.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white;");
                        seatButton.setDisable(true);
                    } else {
                        seatButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                        seatButton.setDisable(false);
                    }
                }
            }
        }
    }

    private Button getSeatButton(int row, int seat) {
        for (javafx.scene.Node node : seatsGrid.getChildren()) {
            if (node instanceof Button) {
                Integer rowIndex = GridPane.getRowIndex(node);
                Integer colIndex = GridPane.getColumnIndex(node);
                if (rowIndex != null && colIndex != null &&
                        rowIndex == row - 1 && colIndex == seat) {
                    return (Button) node;
                }
            }
        }
        return null;
    }

    private void onSeatSelect(int row, int seat, Button button) {
        String seatKey = row + "-" + seat;

        if (selectedSeats.contains(seatKey)) {
            selectedSeats.remove(seatKey);
            button.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        } else {
            selectedSeats.add(seatKey);
            button.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        }

        updateSelectionInfo();
    }

    private void updateSelectionInfo() {
        if (selectedSeats.isEmpty()) {
            selectedSeatsLabel.setText("Выбранные места: нет");
            totalPriceLabel.setText("Общая стоимость: 0 руб.");
            bookButton.setDisable(true);
            buyButton.setDisable(true);
        } else {
            selectedSeatsLabel.setText("Выбранные места: " + String.join(", ", selectedSeats));
            double totalPrice = selectedSeats.size() * currentSession.getPrice();
            totalPriceLabel.setText("Общая стоимость: " + totalPrice + " руб.");
            bookButton.setDisable(false);
            buyButton.setDisable(false);
        }
    }

    @FXML
    protected void onBookClick() {
        if (selectedSeats.isEmpty()) {
            showAlert("Ошибка", "Выберите хотя бы одно место");
            return;
        }

        boolean success = true;
        for (String seat : selectedSeats) {
            String[] parts = seat.split("-");
            int row = Integer.parseInt(parts[0]);
            int seatNum = Integer.parseInt(parts[1]);

            Ticket ticket = new Ticket(currentSession.getId(), currentUser.getId(),
                    row, seatNum, currentSession.getPrice());

            if (!ticketDAO.bookTicket(ticket)) {
                success = false;
                break;
            }
        }

        if (success) {
            showSuccessAlert("Успех", "Билеты успешно забронированы! Номера мест: " +
                    String.join(", ", selectedSeats));
            clearSelection();
            updateSeatAvailability();
        } else {
            showAlert("Ошибка", "Не удалось забронировать некоторые места");
        }
    }

    @FXML
    protected void onBuyOnlineClick() {
        if (selectedSeats.isEmpty()) {
            showAlert("Ошибка", "Выберите хотя бы одно место");
            return;
        }

        showSuccessAlert("Онлайн оплата",
                "Переход на страницу оплаты...\nВыбранные места: " + String.join(", ", selectedSeats) +
                        "\nОбщая сумма: " + (selectedSeats.size() * currentSession.getPrice()) + " руб.");

        clearSelection();
        updateSeatAvailability();
    }

    @FXML
    protected void onCancelClick(ActionEvent event) throws IOException {
        loadScene("sessions.fxml", "Сеансы", event);
    }

    private void clearSelection() {
        for (String seat : selectedSeats) {
            String[] parts = seat.split("-");
            int row = Integer.parseInt(parts[0]);
            int seatNum = Integer.parseInt(parts[1]);

            Button seatButton = getSeatButton(row, seatNum);
            if (seatButton != null) {
                seatButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
            }
        }
        selectedSeats.clear();
        updateSelectionInfo();
    }

    private void loadScene(String fxmlFile, String title, ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 700);

        SessionsController controller = fxmlLoader.getController();
        controller.setCurrentUser(currentUser);

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

    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}