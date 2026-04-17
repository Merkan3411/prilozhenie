package com.cinema.ticket.controllers;

import com.cinema.ticket.CinemaApp;
import com.cinema.ticket.SupabaseClient;
import com.cinema.ticket.models.User;
import com.cinema.ticket.models.Movie;
import com.cinema.ticket.models.Session;
import com.cinema.ticket.dao.UserDAO;
import com.cinema.ticket.dao.MovieDAO;
import com.cinema.ticket.dao.SessionDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class AdminPanelController {

    @FXML private Label adminNameLabel;
    @FXML private TabPane adminTabPane;

    // Фильмы
    @FXML private TableView<Movie> moviesTable;
    @FXML private TableColumn<Movie, String> movieTitleColumn;
    @FXML private TableColumn<Movie, String> movieGenreColumn;
    @FXML private TextField addMovieTitleField;
    @FXML private TextArea addMovieDescriptionArea;
    @FXML private TextField addMovieGenreField;
    @FXML private TextField addMovieDurationField;
    @FXML private TextField addMovieTrailerField;
    @FXML private Button addMovieButton;
    @FXML private Button deleteMovieButton;
    @FXML private Button editMovieButton;
    @FXML private Button uploadPosterButton;

    private Integer editingMovieId = null;

    // Пользователи
    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, String> userNameColumn;
    @FXML private TableColumn<User, String> userEmailColumn;
    @FXML private TableColumn<User, String> userRoleColumn;

    // Сеансы
    @FXML private TableView<Session> sessionsTable;
    @FXML private TableColumn<Session, Integer> sessionMovieColumn;
    @FXML private TableColumn<Session, String> sessionTimeColumn;
    @FXML private TableColumn<Session, String> sessionDateColumn;
    @FXML private TableColumn<Session, Integer> sessionHallColumn;
    @FXML private ComboBox<Movie> movieComboBox;
    @FXML private TextField sessionTimeField;
    @FXML private TextField sessionDateField;
    @FXML private TextField hallNumberField;
    @FXML private Button addSessionButton;
    @FXML private Button deleteSessionButton;

    private User currentAdmin;
    private MovieDAO movieDAO = new MovieDAO();
    private UserDAO userDAO = new UserDAO();
    private SessionDAO sessionDAO = new SessionDAO();

    private static final int WINDOW_WIDTH = 1920;
    private static final int WINDOW_HEIGHT = 1080;

    @FXML
    public void initialize() {
        try {
            movieTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
            movieGenreColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));
        } catch (Exception e) {
        }

        try {
            userNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
            userEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
            userRoleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        } catch (Exception e) {
        }

        try {
            sessionMovieColumn.setCellValueFactory(new PropertyValueFactory<>("movieId"));
            sessionTimeColumn.setCellValueFactory(new PropertyValueFactory<>("sessionTime"));
            sessionDateColumn.setCellValueFactory(new PropertyValueFactory<>("sessionDate"));
            sessionHallColumn.setCellValueFactory(new PropertyValueFactory<>("hallNumber"));
        } catch (Exception e) {
        }

        loadMovieComboBox();
    }

    public void setCurrentAdmin(User admin) {
        this.currentAdmin = admin;
        adminNameLabel.setText("⚙ Администратор: " + admin.getFullName());
        loadMovies();
        loadUsers();
        loadSessions();
    }

    // управление пользователями
    public void onSetAdminClick() {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert(Alert.AlertType.WARNING, "⚠️ Ошибка", "Выберите пользователя для назначения админом");
            return;
        }
        if ("ADMIN".equals(selectedUser.getRole())) {
            showAlert(Alert.AlertType.INFORMATION, "ℹ️ Информация", "Этот пользователь уже админ");
            return;
        }
        if (selectedUser.getId() == currentAdmin.getId()) {
            showAlert(Alert.AlertType.WARNING, "⚠️ Ошибка", "Вы уже администратор");
            return;
        }

        Optional<ButtonType> result = showConfirm(
                "Назначить админом?",
                "Вы уверены, что хотите назначить " + selectedUser.getFullName() + " администратором?"
        );

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                selectedUser.setRole("ADMIN");
                if (userDAO.updateUser(selectedUser)) {
                    showAlert(Alert.AlertType.INFORMATION, "Успех",
                            "✓ " + selectedUser.getFullName() + " получил права администратора");
                    loadUsers();
                } else {
                    showAlert(Alert.AlertType.ERROR, "❌ Ошибка", "Не удалось обновить роль пользователя");
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "❌ Ошибка", "Ошибка при назначении админом: " + e.getMessage());
            }
        }
    }

    public void onRemoveAdminClick() {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert(Alert.AlertType.WARNING, "⚠️ Ошибка", "Выберите пользователя для снятия прав");
            return;
        }
        if (!"ADMIN".equals(selectedUser.getRole())) {
            showAlert(Alert.AlertType.INFORMATION, "ℹ️ Информация", "Этот пользователь не является админом");
            return;
        }
        if (selectedUser.getId() == currentAdmin.getId()) {
            showAlert(Alert.AlertType.WARNING, "⚠️ Ошибка", "Вы не можете лишить себя прав администратора");
            return;
        }

        Optional<ButtonType> result = showConfirm(
                "⚠️ Снять права администратора?",
                "Вы уверены, что хотите снять права администратора у " + selectedUser.getFullName() + "?"
        );

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                selectedUser.setRole("USER");
                if (userDAO.updateUser(selectedUser)) {
                    showAlert(Alert.AlertType.INFORMATION, "Успех",
                            "Права администратора сняты у " + selectedUser.getFullName());
                    loadUsers();
                } else {
                    showAlert(Alert.AlertType.ERROR, "❌ Ошибка", "Не удалось обновить роль пользователя");
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "❌ Ошибка", "Ошибка при снятии прав: " + e.getMessage());
            }
        }
    }

    public void onDeleteAccountClick() {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert(Alert.AlertType.WARNING, "⚠️ Ошибка", "Выберите пользователя для удаления");
            return;
        }
        if (selectedUser.getId() == currentAdmin.getId()) {
            showAlert(Alert.AlertType.WARNING, "⚠️ Ошибка", "Вы не можете удалить свой аккаунт");
            return;
        }

        Optional<ButtonType> result = showConfirm(
                "🗑️ Удалить аккаунт?",
                "Это действие необратимо! Удалить аккаунт пользователя:\n\n" +
                        "👤 " + selectedUser.getFullName() + "\n" +
                        "📧 " + selectedUser.getEmail() + "\n\n" +
                        "Все данные и бронирования будут удалены из базы данных"
        );

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                if (userDAO.deleteUser(selectedUser.getId())) {
                    showAlert(Alert.AlertType.INFORMATION, "Успех",
                            "Аккаунт " + selectedUser.getFullName() + " удален из БД");
                    loadUsers();
                } else {
                    showAlert(Alert.AlertType.ERROR, "❌ Ошибка", "Не удалось удалить аккаунт");
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "❌ Ошибка", "Ошибка при удалении аккаунта: " + e.getMessage());
            }
        }
    }

    // управление фильмами
    @FXML
    protected void onAddMovieClick(ActionEvent event) {
        String title = addMovieTitleField.getText().trim();
        String description = addMovieDescriptionArea.getText().trim();
        String genre = addMovieGenreField.getText().trim();
        String durationStr = addMovieDurationField.getText().trim();
        String trailerUrl = addMovieTrailerField != null
                ? addMovieTrailerField.getText().trim() : "";

        if (title.isEmpty() || description.isEmpty() || genre.isEmpty() || durationStr.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Ошибка", "Заполните все поля");
            return;
        }

        try {
            int duration = Integer.parseInt(durationStr);

            if (editingMovieId != null) {
                Movie existing = movieDAO.getMovieById(editingMovieId);
                if (existing == null) {
                    showAlert(Alert.AlertType.ERROR, "Ошибка", "Фильм не найден");
                    clearMovieFields();
                    return;
                }
                existing.setTitle(title);
                existing.setDescription(description);
                existing.setGenre(genre);
                existing.setDuration(duration);
                existing.setTrailerUrl(trailerUrl.isEmpty() ? null : trailerUrl);
                if (movieDAO.updateMovie(existing)) {
                    showAlert(Alert.AlertType.INFORMATION, "Успех", "Фильм обновлён");
                    clearMovieFields();
                    loadMovies();
                    loadMovieComboBox();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось обновить фильм");
                }
                return;
            }

            Movie movie = new Movie();
            movie.setTitle(title);
            movie.setDescription(description);
            movie.setGenre(genre);
            movie.setDuration(duration);
            movie.setTrailerUrl(trailerUrl.isEmpty() ? null : trailerUrl);
            movie.setActive(true);

            if (movieDAO.createMovie(movie)) {
                showAlert(Alert.AlertType.INFORMATION, "Успех", "Фильм добавлен");
                clearMovieFields();
                loadMovies();
                loadMovieComboBox();
            } else {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось добавить фильм");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Ошибка", "Длительность должна быть числом");
        }
    }

    @FXML
    protected void onDeleteMovieClick(ActionEvent event) {
        Movie selectedMovie = moviesTable.getSelectionModel().getSelectedItem();
        if (selectedMovie == null) {
            showAlert(Alert.AlertType.WARNING, "Ошибка", "Выберите фильм для удаления");
            return;
        }

        Optional<ButtonType> result = showConfirm("Удаление",
                "Вы уверены, что хотите удалить фильм: " + selectedMovie.getTitle() + "?");

        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (movieDAO.deleteMovie(selectedMovie.getId())) {
                showAlert(Alert.AlertType.INFORMATION, "Успех", "Фильм удален");
                loadMovies();
                loadMovieComboBox();
            }
        }
    }

    @FXML
    protected void onEditMovieClick(ActionEvent event) {
        Movie selectedMovie = moviesTable.getSelectionModel().getSelectedItem();
        if (selectedMovie == null) {
            showAlert(Alert.AlertType.WARNING, "Ошибка", "Выберите фильм для редактирования");
            return;
        }
        editingMovieId = selectedMovie.getId();
        addMovieTitleField.setText(selectedMovie.getTitle());
        addMovieDescriptionArea.setText(selectedMovie.getDescription());
        addMovieGenreField.setText(selectedMovie.getGenre());
        addMovieDurationField.setText(String.valueOf(selectedMovie.getDuration()));
        if (addMovieTrailerField != null) {
            addMovieTrailerField.setText(selectedMovie.getTrailerUrl() != null
                    ? selectedMovie.getTrailerUrl() : "");
        }
        addMovieButton.setText("✎ Обновить фильм");
    }

    @FXML
    protected void onUploadPosterClick(ActionEvent event) {
        Movie selectedMovie = moviesTable.getSelectionModel().getSelectedItem();
        if (selectedMovie == null) {
            showAlert(Alert.AlertType.WARNING, "⚠️ Ошибка",
                    "Сначала выберите фильм из таблицы");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите постер для: " + selectedMovie.getTitle());
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Изображения", "*.jpg", "*.jpeg", "*.png", "*.webp")
        );

        Stage stage = (Stage) adminNameLabel.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file == null) return;

        if (file.length() > 5 * 1024 * 1024) {
            showAlert(Alert.AlertType.WARNING, "⚠️ Ошибка",
                    "Файл слишком большой, Максимум 5 МБ.");
            return;
        }

        String originalName = file.getName();
        String ext = originalName.substring(originalName.lastIndexOf('.')).toLowerCase();
        String safeName = selectedMovie.getTitle()
                .replaceAll("[^a-zA-Z0-9]", "_")
                .toLowerCase();
        String fileName = selectedMovie.getId() + "_" + safeName + ext;

        String contentType = switch (ext) {
            case ".png"  -> "image/png";
            case ".webp" -> "image/webp";
            default      -> "image/jpeg";
        };

        try {
            byte[] fileBytes = java.nio.file.Files.readAllBytes(file.toPath());
            String publicUrl = SupabaseClient.uploadFile("movies", fileName, fileBytes, contentType);

            selectedMovie.setPosterUrl(publicUrl);
            if (movieDAO.updateMovie(selectedMovie)) {
                showAlert(Alert.AlertType.INFORMATION, "Успех",
                        "Постер для фильма \"" + selectedMovie.getTitle() + "\" успешно загружен");
                loadMovies();
                loadMovieComboBox();
            } else {
                showAlert(Alert.AlertType.ERROR, "❌ Ошибка",
                        "Файл загружен в Storage, но не удалось обновить БД");
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "❌ Ошибка загрузки",
                    "Не удалось загрузить постер:\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadMovies() {
        try {
            List<Movie> movies = movieDAO.getAllMovies();
            moviesTable.getItems().setAll(movies);
        } catch (Exception e) {
        }
    }

    private void loadMovieComboBox() {
        try {
            List<Movie> movies = movieDAO.getAllMovies();
            movieComboBox.getItems().setAll(movies);
            if (!movies.isEmpty()) movieComboBox.getSelectionModel().selectFirst();
        } catch (Exception e) {
        }
    }

    private void clearMovieFields() {
        addMovieTitleField.clear();
        addMovieDescriptionArea.clear();
        addMovieGenreField.clear();
        addMovieDurationField.clear();
        if (addMovieTrailerField != null) addMovieTrailerField.clear();
        addMovieButton.setText("➕ Добавить фильм");
        editingMovieId = null;
    }

    private void loadUsers() {
        try {
            List<User> users = userDAO.getAllUsers();
            usersTable.getItems().setAll(users);
        } catch (Exception e) {
        }
    }

    // управление сеансами
    @FXML
    protected void onAddSessionClick(ActionEvent event) {
        Movie selectedMovie = movieComboBox.getSelectionModel().getSelectedItem();
        String sessionTime = sessionTimeField.getText().trim();
        String sessionDate = sessionDateField.getText().trim();
        String hallStr = hallNumberField.getText().trim();

        if (selectedMovie == null || sessionTime.isEmpty() || sessionDate.isEmpty() || hallStr.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Ошибка", "Заполните все поля");
            return;
        }

        try {
            int hallNumber = Integer.parseInt(hallStr);
            Session session = new Session();
            session.setMovieId(selectedMovie.getId());
            session.setSessionTime(sessionTime);
            session.setSessionDate(sessionDate);
            session.setHallNumber(hallNumber);

            if (sessionDAO.createSession(session)) {
                showAlert(Alert.AlertType.INFORMATION, "Успех", "✓ Сеанс добавлен");
                sessionTimeField.clear();
                sessionDateField.clear();
                hallNumberField.clear();
                loadSessions();
            } else {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось добавить сеанс");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Ошибка", "Номер зала должен быть числом");
        }
    }

    @FXML
    protected void onDeleteSessionClick(ActionEvent event) {
        Session selectedSession = sessionsTable.getSelectionModel().getSelectedItem();
        if (selectedSession == null) {
            showAlert(Alert.AlertType.WARNING, "Ошибка", "Выберите сеанс для удаления");
            return;
        }

        Optional<ButtonType> result = showConfirm("Удаление", "Вы уверены, что хотите удалить сеанс?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (sessionDAO.deleteSession(selectedSession.getId())) {
                showAlert(Alert.AlertType.INFORMATION, "Успех", "Сеанс удален");
                loadSessions();
            }
        }
    }

    private void loadSessions() {
        try {
            List<Session> sessions = sessionDAO.getAllSessions();
            sessionsTable.getItems().setAll(sessions);
        } catch (Exception e) {
        }
    }

    @FXML
    protected void onLogoutClick(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(CinemaApp.class.getResource("main.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), WINDOW_WIDTH, WINDOW_HEIGHT);
        Stage stage = (Stage) adminNameLabel.getScene().getWindow();
        stage.setTitle("CineMax - Главная");
        stage.setScene(scene);
        stage.show();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private Optional<ButtonType> showConfirm(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait();
    }
}

