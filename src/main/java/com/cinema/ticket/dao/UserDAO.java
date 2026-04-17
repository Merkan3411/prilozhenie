package com.cinema.ticket.dao;

import com.cinema.ticket.SupabaseClient;
import com.cinema.ticket.models.User;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDAO {

    public User getUserByUsername(String username) {
        try {
            JsonArray results = SupabaseClient.select("users",
                    "username=eq." + username + "&select=*");
            if (results.size() > 0) {
                return mapUser(results.get(0).getAsJsonObject());
            }
        } catch (Exception e) {
            System.err.println("Error getting user by username: " + e.getMessage());
        }
        return null;
    }


    public User getUserById(int id) {
        try {
            JsonArray results = SupabaseClient.select("users",
                    "id=eq." + id + "&select=*");
            if (results.size() > 0) {
                return mapUser(results.get(0).getAsJsonObject());
            }
        } catch (Exception e) {
            System.err.println("Error getting user by ID: " + e.getMessage());
        }
        return null;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try {
            JsonArray results = SupabaseClient.select("users", "select=*&order=id.desc");
            for (JsonElement el : results) {
                users.add(mapUser(el.getAsJsonObject()));
            }
        } catch (Exception e) {
            System.err.println("Error getting all users: " + e.getMessage());
        }
        return users;
    }

    public boolean createUser(User user) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("full_name", user.getFullName());
            data.put("email", user.getEmail());
            data.put("username", user.getUsername());
            data.put("password", user.getPassword());
            data.put("role", user.getRole() != null ? user.getRole() : "USER");
            SupabaseClient.insert("users", data);
            return true;
        } catch (Exception e) {
            System.err.println("Error creating user: " + e.getMessage());
            return false;
        }
    }

    public boolean updateUser(User user) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("full_name", user.getFullName());
            data.put("email", user.getEmail());
            data.put("role", user.getRole());
            SupabaseClient.update("users", "id=eq." + user.getId(), data);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean updatePassword(User user) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("password", user.getPassword());
            SupabaseClient.update("users", "id=eq." + user.getId(), data);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteUser(int id) {
        try {
            TicketDAO ticketDAO = new TicketDAO();
            ticketDAO.deleteAllTicketsByUserId(id);
            SupabaseClient.delete("users", "id=eq." + id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public int getTotalUsersCount() {
        try {
            JsonArray results = SupabaseClient.select("users", "select=id");
            return results.size();
        } catch (Exception e) {
            return 0;
        }
    }

    private User mapUser(JsonObject json) {
        User user = new User();
        user.setId(json.get("id").getAsInt());
        user.setFullName(json.get("full_name").getAsString());
        user.setEmail(json.get("email").getAsString());
        user.setUsername(json.get("username").getAsString());
        user.setPassword(json.get("password").getAsString());
        user.setRole(json.get("role").getAsString());
        user.setActive(true);
        return user;
    }
}
