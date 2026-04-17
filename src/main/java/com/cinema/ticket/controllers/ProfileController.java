package com.cinema.ticket.controllers;

import com.cinema.ticket.CinemaApp;
import com.cinema.ticket.dao.ReviewDAO;
import com.cinema.ticket.dao.TicketDAO;
import com.cinema.ticket.dao.UserDAO;
import com.cinema.ticket.models.Review;
import com.cinema.ticket.models.Ticket;
import com.cinema.ticket.models.User;

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
import java.util.*;

public class ProfileController {

    @FXML private Label userInfoLabel;
    @FXML private ScrollPane ticketsScrollPane;
    @FXML private VBox ticketsContainer;
    @FXML private Button logoutButton;
    @FXML private Button registerButton;
    @FXML private Button editProfileButton;
    @FXML private Button changePasswordButton;
    @FXML private Button backButton;

    private User currentUser;
    private final TicketDAO ticketDAO = new TicketDAO();
    private final UserDAO userDAO = new UserDAO();
    private final ReviewDAO reviewDAO = new ReviewDAO();

    private static final double TICKET_PRICE = 250.0;
    private static final int WINDOW_WIDTH = 1920;
    private static final int WINDOW_HEIGHT = 1080;

    // инициализация
    @FXML
    public void initialize() {
        if (registerButton != null) registerButton.setVisible(false);
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (currentUser == null || currentUser.isGuest()) {
            showGuestProfile();
        } else {
            loadUserInfo();
            loadUserTickets();
        }
    }


    // гость
    private void showGuestProfile() {
        userInfoLabel.setText("Вы не авторизованы");
        userInfoLabel.setStyle("-fx-text-fill: #e50914; -fx-font-size: 14;");
        userInfoLabel.setVisible(true);

        ticketsContainer.getChildren().clear();
        Label msg = new Label("⛔ Для просмотра бронирований авторизуйтесь.");
        msg.setStyle("-fx-text-fill: #7a7a9a; -fx-font-size: 13; -fx-padding: 20;");
        msg.setWrapText(true);
        ticketsContainer.getChildren().add(msg);

        editProfileButton.setDisable(true);
        changePasswordButton.setDisable(true);
        registerButton.setVisible(true);
    }

    // информация о пользователе
    private void loadUserInfo() {
        if (currentUser == null) return;
        userInfoLabel.setText(
                "ФИО: " + currentUser.getFullName() + "\n" +
                        "Email: " + currentUser.getEmail() + "\n" +
                        "Username: " + currentUser.getUsername()
        );
        userInfoLabel.setStyle(
                "-fx-text-fill: #e0e0e0;" +
                        "-fx-font-size: 14;" +
                        "-fx-line-spacing: 6;"
        );
        userInfoLabel.setVisible(true);
        editProfileButton.setDisable(false);
        changePasswordButton.setDisable(false);
    }

    // редактировать профиль
    @FXML
    protected void onEditProfileClick(ActionEvent event) {
        if (currentUser == null || currentUser.isGuest()) return;

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("✏️ Редактировать профиль");
        dialog.setHeaderText(null);

        ButtonType saveButton = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, cancelButton);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        TextField fullNameField = new TextField(currentUser.getFullName());
        TextField emailField = new TextField(currentUser.getEmail());
        fullNameField.setPrefWidth(280);
        emailField.setPrefWidth(280);
        fullNameField.setPromptText("Введите ФИО");
        emailField.setPromptText("Введите email");

        grid.add(new Label("ФИО:"), 0, 0);
        grid.add(fullNameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == saveButton) {
            String newName = fullNameField.getText().trim();
            String newEmail = emailField.getText().trim();

            if (newName.isEmpty() || newEmail.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "⚠️ Ошибка", "Поля не могут быть пустыми");
                return;
            }
            if (!newEmail.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                showAlert(Alert.AlertType.WARNING, "⚠️ Ошибка", "Введите корректный email");
                return;
            }

            currentUser.setFullName(newName);
            currentUser.setEmail(newEmail);

            if (userDAO.updateUser(currentUser)) {
                showAlert(Alert.AlertType.INFORMATION, "Успех", "Профиль обновлён");
                loadUserInfo();
            } else {
                showAlert(Alert.AlertType.ERROR, "❌ Ошибка", "Не удалось обновить профиль");
            }
        }
    }

    //смена пароля
    @FXML
    protected void onChangePasswordClick(ActionEvent event) {
        if (currentUser == null || currentUser.isGuest()) return;

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("🔒 Смена пароля");
        dialog.setHeaderText(null);

        ButtonType saveButton = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, cancelButton);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        PasswordField oldPassField = new PasswordField();
        PasswordField newPassField = new PasswordField();
        PasswordField confirmPassField = new PasswordField();
        oldPassField.setPrefWidth(280);
        newPassField.setPrefWidth(280);
        confirmPassField.setPrefWidth(280);
        oldPassField.setPromptText("Текущий пароль");
        newPassField.setPromptText("Минимум 8 символов");
        confirmPassField.setPromptText("Повторите новый пароль");

        grid.add(new Label("Текущий пароль:"), 0, 0);
        grid.add(oldPassField, 1, 0);
        grid.add(new Label("Новый пароль:"), 0, 1);
        grid.add(newPassField, 1, 1);
        grid.add(new Label("Подтвердите:"), 0, 2);
        grid.add(confirmPassField, 1, 2);

        javafx.scene.Node saveNode = dialog.getDialogPane().lookupButton(saveButton);
        saveNode.setDisable(true);
        newPassField.textProperty().addListener((obs, o, n) ->
                saveNode.setDisable(n.trim().isEmpty() || oldPassField.getText().isEmpty()));
        oldPassField.textProperty().addListener((obs, o, n) ->
                saveNode.setDisable(n.trim().isEmpty() || newPassField.getText().isEmpty()));

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == saveButton) {
            String oldPass = oldPassField.getText();
            String newPass = newPassField.getText();
            String confirmPass = confirmPassField.getText();

            if (!currentUser.getPassword().equals(oldPass)) {
                showAlert(Alert.AlertType.ERROR, "❌ Ошибка", "Текущий пароль неверен");
                return;
            }
            if (newPass.length() < 8) {
                showAlert(Alert.AlertType.WARNING, "⚠️ Ошибка",
                        "Пароль должен содержать минимум 8 символов");
                return;
            }
            if (!newPass.equals(confirmPass)) {
                showAlert(Alert.AlertType.WARNING, "⚠️ Ошибка", "Пароли не совпадают");
                return;
            }
            if (newPass.equals(oldPass)) {
                showAlert(Alert.AlertType.WARNING, "⚠️ Ошибка",
                        "Новый пароль совпадает со старым");
                return;
            }

            currentUser.setPassword(newPass);
            if (userDAO.updatePassword(currentUser)) {
                showAlert(Alert.AlertType.INFORMATION, "Успех", "Пароль изменён");
            } else {
                showAlert(Alert.AlertType.ERROR, "❌ Ошибка", "Не удалось изменить пароль");
            }
        }
    }


    // история брони
    private void loadUserTickets() {
        try {
            ticketsContainer.getChildren().clear();

            if (currentUser == null || currentUser.getId() == 0) {
                addEmptyLabel("Нет истории бронирований.");
                return;
            }

            List<Ticket> tickets = ticketDAO.getTicketsByUserId(currentUser.getId());

            if (tickets.isEmpty()) {
                addEmptyLabel("У вас пока нет бронирований.");
                return;
            }

            Map<String, List<Ticket>> grouped = new LinkedHashMap<>();
            for (Ticket t : tickets) {
                String key = t.getMovieTitle() != null ? t.getMovieTitle() : "Неизвестный фильм";
                grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(t);
            }

            for (Map.Entry<String, List<Ticket>> entry : grouped.entrySet()) {
                ticketsContainer.getChildren().add(
                        buildTicketCard(entry.getKey(), entry.getValue())
                );
            }

        } catch (Exception e) {
            addEmptyLabel("Ошибка загрузки истории.");
            e.printStackTrace();
        }
    }

    private VBox buildTicketCard(String movieTitle, List<Ticket> tickets) {
        VBox card = new VBox(0);
        card.setStyle(
                "-fx-background-color: #16213e;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: #2a2a5a;" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-width: 1;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 3);"
        );

        // Заголовок
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle(
                "-fx-background-color: #1e2d50;" +
                        "-fx-background-radius: 12 12 0 0;" +
                        "-fx-padding: 14 18;"
        );

        Label titleLbl = new Label("🎬  " + movieTitle);
        titleLbl.setStyle(
                "-fx-text-fill: #ffffff;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 15;"
        );
        HBox.setHgrow(titleLbl, Priority.ALWAYS);

        double total = tickets.stream()
                .mapToDouble(t -> t.getTotalPrice() > 0 ? t.getTotalPrice() : TICKET_PRICE)
                .sum();
        Label totalLbl = new Label(String.format("%.0f руб.", total));
        totalLbl.setStyle(
                "-fx-text-fill: #e50914;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 15;"
        );

        header.getChildren().addAll(titleLbl, totalLbl);
        card.getChildren().add(header);

        //Инфо сеанса
        Ticket first = tickets.get(0);
        HBox sessionInfo = new HBox(12);
        sessionInfo.setStyle("-fx-padding: 10 18 8 18;");
        sessionInfo.setAlignment(Pos.CENTER_LEFT);

        if (first.getSessionTime() != null) {
            sessionInfo.getChildren().add(infoChip("🕐 " + first.getSessionTime()));
        }
        sessionInfo.getChildren().add(infoChip("🎫 Мест: " + tickets.size()));
        card.getChildren().add(sessionInfo);

        //Разделитель
        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #2a2a5a;");
        card.getChildren().add(sep);

        //Список мест
        VBox seatsList = new VBox(6);
        seatsList.setStyle("-fx-padding: 10 18 10 18;");

        for (Ticket t : tickets) {
            int seatNumber = t.getSeatNumber();
            int seatRow = seatNumber / 100;
            int seatCol = seatNumber % 100;
            double price = t.getTotalPrice() > 0 ? t.getTotalPrice() : TICKET_PRICE;

            HBox seatRow1 = new HBox(10);
            seatRow1.setAlignment(Pos.CENTER_LEFT);
            seatRow1.setStyle(
                    "-fx-background-color: #22294a;" +
                            "-fx-background-radius: 8;" +
                            "-fx-padding: 9 12;"
            );

            Label seatIcon = new Label("💺");
            seatIcon.setStyle("-fx-font-size: 13;");

            Label seatLbl = new Label("Ряд " + seatRow + ", Место " + seatCol);
            seatLbl.setStyle(
                    "-fx-text-fill: #e0e0e0;" +
                            "-fx-font-size: 13;" +
                            "-fx-font-weight: bold;"
            );
            HBox.setHgrow(seatLbl, Priority.ALWAYS);

            Label priceLbl = new Label(String.format("%.0f руб.", price));
            priceLbl.setStyle(
                    "-fx-text-fill: #1db954;" +
                            "-fx-font-size: 13;" +
                            "-fx-font-weight: bold;"
            );

            Button delBtn = new Button("✕");
            delBtn.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-text-fill: #e50914;" +
                            "-fx-border-color: #e50914;" +
                            "-fx-border-width: 1;" +
                            "-fx-border-radius: 5;" +
                            "-fx-background-radius: 5;" +
                            "-fx-padding: 3 8;" +
                            "-fx-cursor: hand;" +
                            "-fx-font-size: 11;"
            );
            delBtn.setOnAction(e -> {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Удалить место?");
                confirm.setHeaderText(null);
                confirm.setContentText("Удалить место Ряд " + seatRow +
                        ", Место " + seatCol + "?");
                Optional<ButtonType> res = confirm.showAndWait();
                if (res.isPresent() && res.get() == ButtonType.OK) {
                    try { reviewDAO.deleteReviewByTicketId(t.getId()); } catch (Exception ignored) {}
                    ticketDAO.deleteTicket(t.getId());
                    loadUserTickets();
                }
            });

            seatRow1.getChildren().addAll(seatIcon, seatLbl, priceLbl, delBtn);
            seatsList.getChildren().add(seatRow1);
        }

        card.getChildren().add(seatsList);

        // Разделитель
        Separator sep2 = new Separator();
        sep2.setStyle("-fx-background-color: #2a2a5a;");
        card.getChildren().add(sep2);

        // футер — кнопки оценить и удалить всю бронь
        HBox footer = new HBox(10);
        footer.setStyle("-fx-padding: 12 18;");
        footer.setAlignment(Pos.CENTER_LEFT);

        Ticket primaryTicket = tickets.get(0);
        Review existing = null;
        try {
            existing = reviewDAO.getReviewByTicketId(primaryTicket.getId());
        } catch (Exception ignored) {}

        Button rateBtn = new Button(existing != null
                ? "★ Мой отзыв (" + String.format("%.1f", existing.getRating()) + ")"
                : "★  Оценить фильм");
        rateBtn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #ffc107;" +
                        "-fx-border-color: #ffc107;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8 16;" +
                        "-fx-cursor: hand;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 12;"
        );
        final Review existingFinal = existing;
        rateBtn.setOnAction(e -> {
            if (existingFinal != null) {
                showExistingReview(existingFinal);
            } else {
                openRateDialog(primaryTicket);
            }
        });
        footer.getChildren().add(rateBtn);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        footer.getChildren().add(spacer);

        Button deleteAllBtn = new Button("🗑  Удалить всю бронь");
        deleteAllBtn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #e50914;" +
                        "-fx-border-color: #e50914;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8 16;" +
                        "-fx-cursor: hand;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 12;"
        );
        deleteAllBtn.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Удалить бронь?");
            confirm.setHeaderText(null);
            confirm.setContentText("Удалить ВСЮ бронь на фильм \"" + movieTitle +
                    "\"?\nОтзыв и оценка также будут удалены.");
            Optional<ButtonType> res = confirm.showAndWait();
            if (res.isPresent() && res.get() == ButtonType.OK) {
                for (Ticket t : tickets) {
                    try { reviewDAO.deleteReviewByTicketId(t.getId()); } catch (Exception ignored) {}
                    ticketDAO.deleteTicket(t.getId());
                }
                loadUserTickets();
            }
        });
        footer.getChildren().add(deleteAllBtn);
        card.getChildren().add(footer);

        return card;
    }

    private void openRateDialog(Ticket ticket) {
        if (ticket.getMovieId() <= 0) {
            showAlert(Alert.AlertType.ERROR, "❌ Ошибка",
                    "Не удалось определить фильм для оценки.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("★ Оценить фильм");
        dialog.setHeaderText("Поставьте оценку и оставьте отзыв");

        ButtonType saveBtn = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelBtn = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, cancelBtn);

        VBox box = new VBox(12);
        box.setPadding(new Insets(15));

        Label rLbl = new Label("Оценка (1–10):");
        Slider slider = new Slider(1, 10, 8);
        slider.setMajorTickUnit(1);
        slider.setMinorTickCount(0);
        slider.setSnapToTicks(true);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        Label sliderVal = new Label("★ 8.0");
        sliderVal.setStyle("-fx-text-fill: #e59014; -fx-font-weight: bold; -fx-font-size: 15;");
        slider.valueProperty().addListener((obs, o, n) ->
                sliderVal.setText(String.format("★ %.1f", n.doubleValue())));

        Label tLbl = new Label("Отзыв (необязательно):");
        TextArea textArea = new TextArea();
        textArea.setPromptText("Поделитесь впечатлениями...");
        textArea.setPrefRowCount(4);
        textArea.setWrapText(true);

        box.getChildren().addAll(rLbl, slider, sliderVal, tLbl, textArea);
        dialog.getDialogPane().setContent(box);

        Optional<ButtonType> res = dialog.showAndWait();
        if (res.isPresent() && res.get() == saveBtn) {
            double rating = Math.round(slider.getValue() * 10.0) / 10.0;
            String text = textArea.getText().trim();

            Review review = new Review();
            review.setTicketId(ticket.getId());
            review.setMovieId(ticket.getMovieId());
            review.setUserId(currentUser.getId());
            review.setRating(rating);
            review.setReviewText(text.isEmpty() ? null : text);

            if (reviewDAO.createReview(review)) {
                showAlert(Alert.AlertType.INFORMATION, "Спасибо",
                        "Ваша оценка сохранена.");
                loadUserTickets();
            } else {
                showAlert(Alert.AlertType.ERROR, "❌ Ошибка",
                        "Не удалось сохранить оценку. Возможно, отзыв для этого билета уже существует.");
            }
        }
    }

    private void showExistingReview(Review review) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Мой отзыв");
        alert.setHeaderText(String.format("★ %.1f", review.getRating()));
        String text = review.getReviewText();
        alert.setContentText(text != null && !text.isEmpty()
                ? text : "Вы поставили оценку без текста отзыва.");
        alert.showAndWait();
    }

    private Label infoChip(String text) {
        Label chip = new Label(text);
        chip.setStyle(
                "-fx-background-color: #22294a;" +
                        "-fx-text-fill: #d0d0d0;" +
                        "-fx-background-radius: 6;" +
                        "-fx-padding: 5 12;" +
                        "-fx-font-size: 12;"
        );
        return chip;
    }

    private void addEmptyLabel(String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-text-fill: #7a7a9a; -fx-font-size: 13; -fx-padding: 20;");
        ticketsContainer.getChildren().add(lbl);
    }

    // навигация
    @FXML
    protected void onRegisterClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(CinemaApp.class.getResource("register.fxml"));
            Scene scene = new Scene(loader.load(), WINDOW_WIDTH, WINDOW_HEIGHT);
            Stage stage = (Stage) registerButton.getScene().getWindow();
            stage.setTitle("CineMax - Регистрация");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onLogoutClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(CinemaApp.class.getResource("main.fxml"));
            Scene scene = new Scene(loader.load(), WINDOW_WIDTH, WINDOW_HEIGHT);
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setTitle("CineMax - Главная");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onBackClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(CinemaApp.class.getResource("movies.fxml"));
            Scene scene = new Scene(loader.load(), WINDOW_WIDTH, WINDOW_HEIGHT);
            MoviesController controller = loader.getController();
            controller.setCurrentUser(currentUser);
            Stage stage = (Stage) userInfoLabel.getScene().getWindow();
            stage.setTitle("CineMax - Фильмы");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // вспомогательные методы
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
