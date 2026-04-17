package com.cinema.ticket.dao;

import com.cinema.ticket.SupabaseClient;
import com.cinema.ticket.models.Session;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SessionDAO {

    public List<Session> getAllSessions() {
        List<Session> sessions = new ArrayList<>();
        try {
            JsonElement result = SupabaseClient.selectRaw("sessions",
                    "order=session_date.asc,session_time.asc");
            if (result.isJsonArray()) {
                for (JsonElement el : result.getAsJsonArray()) {
                    sessions.add(mapJsonToSession(el.getAsJsonObject()));
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting sessions: " + e.getMessage());
        }
        return sessions;
    }

    public List<Session> getSessionsByMovieId(int movieId) {
        List<Session> sessions = new ArrayList<>();
        try {
            JsonElement result = SupabaseClient.selectRaw("sessions",
                    "movie_id=eq." + movieId + "&order=session_date.asc,session_time.asc");
            if (result.isJsonArray()) {
                for (JsonElement el : result.getAsJsonArray()) {
                    sessions.add(mapJsonToSession(el.getAsJsonObject()));
                }
            } else if (result.isJsonObject()) {
                sessions.add(mapJsonToSession(result.getAsJsonObject()));
            }
        } catch (Exception e) {
            System.err.println("Error getting sessions by movie ID: " + e.getMessage());
            e.printStackTrace();
        }
        return sessions;
    }

    public Session getSessionById(int id) {
        try {
            JsonElement result = SupabaseClient.selectRaw("sessions", "id=eq." + id);
            if (result.isJsonArray()) {
                JsonArray array = result.getAsJsonArray();
                if (array.size() > 0) return mapJsonToSession(array.get(0).getAsJsonObject());
            } else if (result.isJsonObject()) {
                return mapJsonToSession(result.getAsJsonObject());
            }
        } catch (Exception e) {
            System.err.println("Error getting session by ID: " + e.getMessage());
        }
        return null;
    }

    public boolean createSession(Session session) {
        try {
            if (!session.getSessionTime().matches("\\d{2}:\\d{2}")) {
                System.err.println("❌ Неверный формат времени. Используйте HH:mm");
                return false;
            }

            String date = (session.getSessionDate() != null && !session.getSessionDate().isEmpty())
                    ? session.getSessionDate()
                    : java.time.LocalDate.now().toString();

            Map<String, Object> data = new HashMap<>();
            data.put("movie_id", session.getMovieId());
            data.put("hall_id", session.getHallNumber());
            data.put("session_time", session.getSessionTime());
            data.put("session_date", date);
            data.put("available_seats", 100);
            data.put("price", 250.0);

            SupabaseClient.insert("sessions", data);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteSession(int id) {
        try {
            SupabaseClient.delete("sessions", "id=eq." + id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Session mapJsonToSession(JsonObject json) {
        Session session = new Session();
        session.setId(json.get("id").getAsInt());
        session.setMovieId(json.get("movie_id").getAsInt());
        session.setHallNumber(json.get("hall_id").getAsInt());
        session.setSessionTime(json.get("session_time").getAsString());

        if (json.has("session_date") && !json.get("session_date").isJsonNull()) {
            session.setSessionDate(json.get("session_date").getAsString());
        }

        return session;
    }
}
