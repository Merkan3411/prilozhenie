package com.cinema.ticket;

import java.sql.*;

public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/cinema_db";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "1111";

    private static boolean driverLoaded = false;

    static {
        loadDriver();
    }

    public static Connection getConnection() throws SQLException {
        if (!driverLoaded) {
            loadDriver();
        }
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    private static void loadDriver() {
        try {
            Class.forName("org.postgresql.Driver");
            driverLoaded = true;
            System.out.println("PostgreSQL Driver loaded successfully");
        } catch (ClassNotFoundException e) {
            System.err.println("Failed to load PostgreSQL driver: " + e.getMessage());
        }
    }

    public static void initializeDatabase() {
        String createUsersTable = """
            CREATE TABLE IF NOT EXISTS users (
                id SERIAL PRIMARY KEY,
                username VARCHAR(50) UNIQUE NOT NULL,
                password VARCHAR(100) NOT NULL,
                email VARCHAR(100) NOT NULL,
                full_name VARCHAR(100) NOT NULL,
                role VARCHAR(20) DEFAULT 'CLIENT',
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """;

        String createMoviesTable = """
            CREATE TABLE IF NOT EXISTS movies (
                id SERIAL PRIMARY KEY,
                title VARCHAR(200) NOT NULL,
                description TEXT,
                genre VARCHAR(100),
                duration INTEGER,
                age_rating VARCHAR(10),
                rating DECIMAL(3,1) DEFAULT 0.0,
                poster_url VARCHAR(300),
                trailer_url VARCHAR(300),
                is_active BOOLEAN DEFAULT true,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """;

        String createHallsTable = """
            CREATE TABLE IF NOT EXISTS halls (
                id SERIAL PRIMARY KEY,
                name VARCHAR(50) NOT NULL,
                capacity INTEGER NOT NULL,
                rows INTEGER NOT NULL,
                seats_per_row INTEGER NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """;

        String createSessionsTable = """
            CREATE TABLE IF NOT EXISTS sessions (
                id SERIAL PRIMARY KEY,
                movie_id INTEGER REFERENCES movies(id),
                hall_id INTEGER REFERENCES halls(id),
                session_date DATE NOT NULL,
                session_time TIME NOT NULL,
                price DECIMAL(8,2) NOT NULL,
                available_seats INTEGER NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """;

        String createTicketsTable = """
            CREATE TABLE IF NOT EXISTS tickets (
                id SERIAL PRIMARY KEY,
                session_id INTEGER REFERENCES sessions(id),
                user_id INTEGER REFERENCES users(id),
                row_number INTEGER NOT NULL,
                seat_number INTEGER NOT NULL,
                purchase_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                status VARCHAR(20) DEFAULT 'BOOKED',
                qr_code VARCHAR(500),
                total_price DECIMAL(8,2) NOT NULL
            )
            """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createUsersTable);
            stmt.execute(createMoviesTable);
            stmt.execute(createHallsTable);
            stmt.execute(createSessionsTable);
            stmt.execute(createTicketsTable);

            System.out.println("Cinema database tables created successfully");

            // Добавляем тестовые данные
            insertTestData(conn);

        } catch (SQLException e) {
            System.err.println("Error creating cinema database tables: " + e.getMessage());
        }
    }

    private static void insertTestData(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            // Добавляем тестового администратора
            stmt.execute("""
                INSERT INTO users (username, password, email, full_name, role) 
                VALUES ('admin', 'admin123', 'admin@cinema.com', 'Администратор', 'ADMIN')
                ON CONFLICT (username) DO NOTHING
                """);

            // Добавляем тестовые залы
            stmt.execute("""
                INSERT INTO halls (name, capacity, rows, seats_per_row) 
                VALUES 
                ('Зал 1', 100, 10, 10),
                ('Зал 2', 80, 8, 10),
                ('Зал 3', 120, 12, 10)
                ON CONFLICT DO NOTHING
                """);

            System.out.println("Test data inserted successfully");
        } catch (SQLException e) {
            System.err.println("Error inserting test data: " + e.getMessage());
        }
    }
}