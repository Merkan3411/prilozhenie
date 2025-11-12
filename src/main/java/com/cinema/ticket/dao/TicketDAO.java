package com.cinema.ticket.dao;

import com.cinema.ticket.DatabaseConnection;
import com.cinema.ticket.Ticket;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketDAO {

    public boolean bookTicket(Ticket ticket) {
        String sql = "INSERT INTO tickets (session_id, user_id, row_number, seat_number, total_price) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, ticket.getSessionId());
            pstmt.setInt(2, ticket.getUserId());
            pstmt.setInt(3, ticket.getRowNumber());
            pstmt.setInt(4, ticket.getSeatNumber());
            pstmt.setDouble(5, ticket.getTotalPrice());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error booking ticket: " + e.getMessage());
            return false;
        }
    }

    public List<Ticket> getUserTickets(int userId) {
        List<Ticket> tickets = new ArrayList<>();
        String sql = """
            SELECT t.*, m.title as movie_title, s.session_time 
            FROM tickets t 
            JOIN sessions s ON t.session_id = s.id 
            JOIN movies m ON s.movie_id = m.id 
            WHERE t.user_id = ? 
            ORDER BY t.purchase_date DESC
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Ticket ticket = new Ticket();
                ticket.setId(rs.getInt("id"));
                ticket.setSessionId(rs.getInt("session_id"));
                ticket.setUserId(rs.getInt("user_id"));
                ticket.setRowNumber(rs.getInt("row_number"));
                ticket.setSeatNumber(rs.getInt("seat_number"));
                ticket.setPurchaseDate(rs.getTimestamp("purchase_date").toLocalDateTime());
                ticket.setStatus(rs.getString("status"));
                ticket.setTotalPrice(rs.getDouble("total_price"));
                ticket.setMovieTitle(rs.getString("movie_title"));
                ticket.setSessionTime(rs.getTime("session_time").toString());
                tickets.add(ticket);
            }

        } catch (SQLException e) {
            System.err.println("Error getting user tickets: " + e.getMessage());
        }
        return tickets;
    }

    public boolean isSeatAvailable(int sessionId, int row, int seat) {
        String sql = "SELECT id FROM tickets WHERE session_id = ? AND row_number = ? AND seat_number = ? AND status != 'CANCELLED'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, sessionId);
            pstmt.setInt(2, row);
            pstmt.setInt(3, seat);
            ResultSet rs = pstmt.executeQuery();

            return !rs.next(); // Если нет записей - место свободно

        } catch (SQLException e) {
            System.err.println("Error checking seat availability: " + e.getMessage());
            return false;
        }
    }

    public boolean cancelTicket(int ticketId) {
        String sql = "UPDATE tickets SET status = 'CANCELLED' WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, ticketId);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error cancelling ticket: " + e.getMessage());
            return false;
        }
    }
}