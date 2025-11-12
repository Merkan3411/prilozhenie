package com.cinema.ticket.controllers;

import com.cinema.ticket.dao.TicketDAO;
import com.cinema.ticket.dao.UserDAO;
import com.cinema.ticket.Ticket;
import com.cinema.ticket.User;
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

public class ProfileController {

    @FXML private Label welcomeLabel;
    @FXML private Label userInfoLabel;
    @FXML private VBox ticketsContainer;
    @FXML private Button editProfileButton;
    @FXML private Button changePasswordButton;

    private User currentUser;
    private UserDAO userDAO = new UserDAO();
    private TicketDAO ticketDAO = new TicketDAO();

    @FXML
    public void initialize() {
        loadUserTickets();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        updateUserInfo();
        loadUserTickets();
    }

    private void updateUserInfo() {
        if (currentUser != null) {
            welcomeLabel.setText("Личный кабинет - " + currentUser.getFullName());
            userInfoLabel.setText(
                    "👤 Имя: " + currentUser.getFullName() + "\n" +
                            "📧 Email: " + currentUser.getEmail() + "\n" +
                            "🎫 Роль: " + getRoleDisplayName(currentUser.getRole())
            );

            // Скрываем кнопки редактирования для гостей
            boolean isGuest = "GUEST".equals(currentUser.getRole());
            editProfileButton.setVisible(!isGuest);
            changePasswordButton.setVisible(!isGuest);
        }
    }

    private String getRoleDisplayName(String role) {
        switch (role) {
            case "ADMIN": return "Администратор";
            case "CLIENT": return "Клиент";
            case "GUEST": return "Гость";
            default: return role;
        }
    }

    private void loadUserTickets() {
        ticketsContainer.getChildren().clear();

        if (currentUser == null || "GUEST".equals(currentUser.getRole())) {
            Label noTicketsLabel = new Label("История бронирований доступна только зарегистрированным пользователям");
            noTicketsLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #666;");
            ticketsContainer.getChildren().add(noTicketsLabel);
            return;
        }

        List<Ticket> tickets = ticketDAO.getUserTickets(currentUser.getId());

        if (tickets.isEmpty()) {
            Label noTicketsLabel = new Label("У вас пока нет забронированных билетов");
            noTicketsLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #666;");
            ticketsContainer.getChildren().add(noTicketsLabel);
            return;
        }

        for (Ticket ticket : tickets) {
            VBox ticketCard = createTicketCard(ticket);
            ticketsContainer.getChildren().add(ticketCard);
        }
    }

    private VBox createTicketCard(Ticket ticket) {
        VBox card = new VBox();
        card.setSpacing(8);
        card.setStyle("-fx-padding: 15; -fx-border-color: #ddd; -fx-border-radius: 5; -fx-background-radius: 5; -fx-margin: 5;");

        // Заголовок с фильмом
        Label movieLabel = new Label("🎬 " + ticket.getMovieTitle());
        movieLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

        // Информация о сеансе
        Label sessionLabel = new Label("🕒 Время: " + ticket.getSessionTime());

        // Информация о месте
        Label seatLabel = new Label("💺 Место: Ряд " + ticket.getRowNumber() + ", Место " + ticket.getSeatNumber());

        // Дата покупки
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        Label dateLabel = new Label("📅 Дата бронирования: " + ticket.getPurchaseDate().format(formatter));

        // Статус и цена
        HBox statusPriceBox = new HBox(20);
        Label statusLabel = new Label("Статус: " + getStatusDisplayName(ticket.getStatus()));
        statusLabel.setStyle(getStatusStyle(ticket.getStatus()));

        Label priceLabel = new Label("💵 " + ticket.getTotalPrice() + " руб.");

        statusPriceBox.getChildren().addAll(statusLabel, priceLabel);

        // Кнопка отмены для активных бронирований
        if ("BOOKED".equals(ticket.getStatus())) {
            Button cancelButton = new Button("Отменить бронь");
            cancelButton.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white;");
            cancelButton.setOnAction(e -> onCancelTicket(ticket));
            card.getChildren().add(cancelButton);
        }

        card.getChildren().addAll(movieLabel, sessionLabel, seatLabel, dateLabel, statusPriceBox);
        return card;
    }

    private String getStatusDisplayName(String status) {
        switch (status) {
            case "BOOKED": return "Забронирован";
            case "PAID": return "Оплачен";
            case "CANCELLED": return "Отменен";
            case "USED": return "Использован";
            default: return status;
        }
    }

    private String getStatusStyle(String status) {
        switch (status) {
            case "BOOKED": return "-fx-text-fill: #2196F3; -fx-font-weight: bold;";
            case "PAID": return "-fx-text-fill: #4CAF50; -fx-font-weight: bold;";
            case "CANCELLED": return "-fx-text-fill: #ff4444; -fx-font-weight: bold;";
            case "USED": return "-fx-text-fill: #666; -fx-font-weight: bold;";
            default: return "";
        }
    }

    private void onCancelTicket(Ticket ticket) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Подтверждение отмены");
        confirmAlert.setHeaderText("Отменить бронирование?");
        confirmAlert.setContentText("Вы действительно хотите отменить бронирование билета на " +
                ticket.getMovieTitle() + "?");

        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            if (ticketDAO.cancelTicket(ticket.getId())) {
                showSuccessAlert("Успех", "Бронирование успешно отменено");
                loadUserTickets(); // Обновляем список
            } else {
                showAlert("Ошибка", "Не удалось отменить бронирование");
            }
        }
    }

    @FXML
    protected void onEditProfileClick() {
        showAlert("Редактирование", "Функция редактирования профиля в разработке");
    }

    @FXML
    protected void onChangePasswordClick() {
        showAlert("Смена пароля", "Функция смены пароля в разработке");
    }

    @FXML
    protected void onMoviesClick(ActionEvent event) throws IOException {
        loadScene("movies.fxml", "Фильмы", event);
    }

    @FXML
    protected void onSessionsClick(ActionEvent event) throws IOException {
        loadScene("sessions.fxml", "Сеансы", event);
    }

    @FXML
    protected void onLogoutClick(ActionEvent event) throws IOException {
        loadScene("main.fxml", "CineMax - Кинотеатр", event);
    }

    private void loadScene(String fxmlFile, String title, ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 700);

        if (fxmlFile.equals("movies.fxml")) {
            MoviesController controller = fxmlLoader.getController();
            controller.setCurrentUser(currentUser);
        } else if (fxmlFile.equals("sessions.fxml")) {
            SessionsController controller = fxmlLoader.getController();
            controller.setCurrentUser(currentUser);
        }

        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
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