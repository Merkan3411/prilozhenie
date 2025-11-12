package com.cinema.ticket.dao;

import com.cinema.ticket.Session;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class SessionDAO {

    public List<Session> getAllSessions() {
        List<Session> sessions = new ArrayList<>();
        String sql = """
            SELECT s.*, m.title as movie_title, h.name as hall_name 
            FROM sessions s 
            JOIN movies m ON s.movie_id = m.id 
            JOIN halls h ON s.hall_id = h.id 
            WHERE s.session_date >= CURRENT_DATE 
            ORDER BY s.session_date, s.session_time
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Session session = new Session();
                session.setId(rs.getInt("id"));
                session.setMovieId(rs.getInt("movie_id"));
                session.setHallId(rs.getInt("hall_id"));
                session.setSessionDate(rs.getDate("session_date").toLocalDate());
                session.setSessionTime(rs.getTime("session_time").toLocalTime());
                session.setPrice(rs.getDouble("price"));
                session.setAvailableSeats(rs.getInt("available_seats"));
                session.setMovieTitle(rs.getString("movie_title"));
                session.setHallName(rs.getString("hall_name"));
                sessions.add(session);
            }

        } catch (SQLException e) {
            System.err.println("Error getting sessions: " + e.getMessage());
        }
        return sessions;
    }

    public List<Session> getSessionsByMovie(int movieId) {
        List<Session> sessions = new ArrayList<>();
        String sql = """
            SELECT s.*, m.title as movie_title, h.name as hall_name 
            FROM sessions s 
            JOIN movies m ON s.movie_id = m.id 
            JOIN halls h ON s.hall_id = h.id 
            WHERE s.movie_id = ? AND s.session_date >= CURRENT_DATE 
            ORDER BY s.session_date, s.session_time
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, movieId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Session session = new Session();
                session.setId(rs.getInt("id"));
                session.setMovieId(rs.getInt("movie_id"));
                session.setHallId(rs.getInt("hall_id"));
                session.setSessionDate(rs.getDate("session_date").toLocalDate());
                session.setSessionTime(rs.getTime("session_time").toLocalTime());
                session.setPrice(rs.getDouble("price"));
                session.setAvailableSeats(rs.getInt("available_seats"));
                session.setMovieTitle(rs.getString("movie_title"));
                session.setHallName(rs.getString("hall_name"));
                sessions.add(session);
            }

        } catch (SQLException e) {
            System.err.println("Error getting sessions by movie: " + e.getMessage());
        }
        return sessions;
    }

    public Session getSessionById(int id) {
        String sql = """
            SELECT s.*, m.title as movie_title, h.name as hall_name 
            FROM sessions s 
            JOIN movies m ON s.movie_id = m.id 
            JOIN halls h ON s.hall_id = h.id 
            WHERE s.id = ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Session session = new Session();
                session.setId(rs.getInt("id"));
                session.setMovieId(rs.getInt("movie_id"));
                session.setHallId(rs.getInt("hall_id"));
                session.setSessionDate(rs.getDate("session_date").toLocalDate());
                session.setSessionTime(rs.getTime("session_time").toLocalTime());
                session.setPrice(rs.getDouble("price"));
                session.setAvailableSeats(rs.getInt("available_seats"));
                session.setMovieTitle(rs.getString("movie_title"));
                session.setHallName(rs.getString("hall_name"));
                return session;
            }

        } catch (SQLException e) {
            System.err.println("Error getting session by ID: " + e.getMessage());
        }
        return null;
    }

    public boolean addSession(Session session) {
        String sql = "INSERT INTO sessions (movie_id, hall_id, session_date, session_time, price, available_seats) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, session.getMovieId());
            pstmt.setInt(2, session.getHallId());
            pstmt.setDate(3, Date.valueOf(session.getSessionDate()));
            pstmt.setTime(4, Time.valueOf(session.getSessionTime()));
            pstmt.setDouble(5, session.getPrice());
            pstmt.setInt(6, session.getAvailableSeats());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error adding session: " + e.getMessage());
            return false;
        }
    }
}