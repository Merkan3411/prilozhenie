package com.cinema.ticket.dao;

import com.cinema.ticket.models.Ticket;
import com.cinema.ticket.SupabaseClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TicketDAO {

    public List<Ticket> getAllTickets() {
        List<Ticket> tickets = new ArrayList<>();
        try {
            JsonElement result = SupabaseClient.selectRaw("tickets", "order=id.desc");
            if (result.isJsonArray()) {
                JsonArray array = result.getAsJsonArray();
                for (int i = 0; i < array.size(); i++) {
                    tickets.add(mapJsonToTicket(array.get(i).getAsJsonObject()));
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting tickets: " + e.getMessage());
        }
        return tickets;
    }

    public List<Ticket> getTicketsByUserId(int userId) {
        List<Ticket> tickets = new ArrayList<>();
        try {
            String query = "user_id=eq." + userId + "&select=*,sessions(session_time,session_date,movie_id,movies(title))&order=purchase_date.desc";

            JsonElement result = SupabaseClient.selectRaw("tickets", query);

            if (result.isJsonArray()) {
                JsonArray array = result.getAsJsonArray();
                for (int i = 0; i < array.size(); i++) {
                    Ticket ticket = mapJsonToTicketWithMovie(array.get(i).getAsJsonObject());
                    if (ticket != null) {
                        tickets.add(ticket);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting tickets by user: " + e.getMessage());
            e.printStackTrace();
        }
        return tickets;
    }

    public List<Ticket> getTicketsBySessionId(int sessionId) {
        List<Ticket> tickets = new ArrayList<>();
        try {
            JsonElement result = SupabaseClient.selectRaw("tickets", "session_id=eq." + sessionId + "&order=id.desc");

            if (result.isJsonArray()) {
                JsonArray array = result.getAsJsonArray();
                for (int i = 0; i < array.size(); i++) {
                    tickets.add(mapJsonToTicket(array.get(i).getAsJsonObject()));
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting tickets by session: " + e.getMessage());
        }
        return tickets;
    }

    public boolean isSeatOccupied(int sessionId, int seatNumber) {
        try {
            String query = "session_id=eq." + sessionId + "&seat_number=eq." + seatNumber;
            JsonElement result = SupabaseClient.selectRaw("tickets", query);

            if (result.isJsonArray()) {
                JsonArray array = result.getAsJsonArray();
                return array.size() > 0;
            }
        } catch (Exception e) {
            System.err.println("Error checking seat: " + e.getMessage());
        }
        return false;
    }

    public boolean createTicket(Ticket ticket) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("session_id", ticket.getSessionId());
            data.put("user_id", ticket.getUserId());
            data.put("row_number", ticket.getRowNumber());
            data.put("seat_number", ticket.getSeatNumber());
            data.put("total_price", ticket.getTotalPrice());
            data.put("status", ticket.getStatus());
            data.put("purchase_date", LocalDateTime.now().toString());

            SupabaseClient.insert("tickets", data);
            return true;
        } catch (Exception e) {
            System.err.println("Error creating ticket: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean addTicket(Ticket ticket) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("session_id", ticket.getSessionId());
            data.put("user_id", ticket.getUserId());
            data.put("row_number", ticket.getRowNumber());
            data.put("seat_number", ticket.getSeatNumber());
            data.put("total_price", ticket.getTotalPrice());
            data.put("status", ticket.getStatus());
            data.put("purchase_date", LocalDateTime.now().toString());

            SupabaseClient.insert("tickets", data);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteTicket(int ticketId) {
        try {
            SupabaseClient.delete("tickets", "id=eq." + ticketId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public boolean updateTicketStatus(int ticketId, String status) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("status", status);
            SupabaseClient.update("tickets", "id=eq." + ticketId, data);
            return true;
        } catch (Exception e) {
            System.err.println("Error updating ticket status: " + e.getMessage());
            return false;
        }
    }

    private Ticket mapJsonToTicket(JsonObject json) {
        Ticket ticket = new Ticket();
        ticket.setId(json.get("id").getAsInt());
        ticket.setSessionId(json.get("session_id").getAsInt());
        ticket.setUserId(json.get("user_id").getAsInt());
        ticket.setRowNumber(json.get("row_number").getAsInt());
        ticket.setSeatNumber(json.get("seat_number").getAsInt());
        ticket.setPurchaseDate(LocalDateTime.parse(json.get("purchase_date").getAsString()));
        ticket.setStatus(json.get("status").getAsString());
        ticket.setTotalPrice(json.get("total_price").getAsDouble());
        return ticket;
    }

    private Ticket mapJsonToTicketWithMovie(JsonObject json) {
        try {
            Ticket ticket = new Ticket();
            ticket.setId(json.get("id").getAsInt());
            ticket.setSessionId(json.get("session_id").getAsInt());
            ticket.setUserId(json.get("user_id").getAsInt());
            ticket.setRowNumber(json.get("row_number").getAsInt());
            ticket.setSeatNumber(json.get("seat_number").getAsInt());
            ticket.setPurchaseDate(LocalDateTime.parse(json.get("purchase_date").getAsString()));
            ticket.setStatus(json.get("status").getAsString());
            ticket.setTotalPrice(json.get("total_price").getAsDouble());

            if (json.has("sessions") && !json.get("sessions").isJsonNull()) {
                JsonElement sessionsElement = json.get("sessions");
                JsonObject sessionObj = null;

                if (sessionsElement.isJsonObject()) {
                    sessionObj = sessionsElement.getAsJsonObject();
                } else if (sessionsElement.isJsonArray()) {
                    JsonArray sessionsArray = sessionsElement.getAsJsonArray();
                    if (sessionsArray.size() > 0) {
                        sessionObj = sessionsArray.get(0).getAsJsonObject();
                    }
                }

                if (sessionObj != null) {
                    if (sessionObj.has("session_time") && !sessionObj.get("session_time").isJsonNull()) {
                        ticket.setSessionTime(sessionObj.get("session_time").getAsString());
                    }

                    if (sessionObj.has("movie_id") && !sessionObj.get("movie_id").isJsonNull()) {
                        ticket.setMovieId(sessionObj.get("movie_id").getAsInt());
                    }

                    if (sessionObj.has("movies") && !sessionObj.get("movies").isJsonNull()) {
                        JsonElement moviesElement = sessionObj.get("movies");
                        JsonObject movieObj = null;

                        if (moviesElement.isJsonObject()) {
                            movieObj = moviesElement.getAsJsonObject();
                        } else if (moviesElement.isJsonArray()) {
                            JsonArray moviesArray = moviesElement.getAsJsonArray();
                            if (moviesArray.size() > 0) {
                                movieObj = moviesArray.get(0).getAsJsonObject();
                            }
                        }

                        if (movieObj != null && movieObj.has("title")) {
                            ticket.setMovieTitle(movieObj.get("title").getAsString());
                        }
                    }
                }
            }

            return ticket;
        } catch (Exception e) {
            System.err.println("Error mapping ticket with movie: " + e.getMessage());
            e.printStackTrace();
            return null;
        }

    }
    public boolean deleteAllTicketsByUserId(int userId) {
        try {
            SupabaseClient.delete("tickets", "user_id=eq." + userId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
