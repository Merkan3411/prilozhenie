package com.cinema.ticket.dao;

import com.cinema.ticket.SupabaseClient;
import com.cinema.ticket.models.Review;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReviewDAO {

    public List<Review> getReviewsByMovieId(int movieId) {
        List<Review> reviews = new ArrayList<>();
        try {
            String query = "movie_id=eq." + movieId +
                    "&select=*,users(full_name,username)&order=created_at.desc";
            JsonArray result = SupabaseClient.select("reviews", query);
            for (int i = 0; i < result.size(); i++) {
                reviews.add(mapJsonToReview(result.get(i).getAsJsonObject()));
            }
        } catch (Exception e) {
            System.err.println("Error getting reviews by movie: " + e.getMessage());
        }
        return reviews;
    }

    public Review getReviewByTicketId(int ticketId) {
        try {
            JsonArray result = SupabaseClient.select("reviews", "ticket_id=eq." + ticketId);
            if (result.size() > 0) {
                return mapJsonToReview(result.get(0).getAsJsonObject());
            }
        } catch (Exception e) {
            System.err.println("Error getting review by ticket: " + e.getMessage());
        }
        return null;
    }

    public double getAverageRating(int movieId) {
        try {
            JsonArray result = SupabaseClient.select("reviews",
                    "movie_id=eq." + movieId + "&select=rating");
            if (result.size() == 0) return 0.0;
            double sum = 0.0;
            int count = 0;
            for (int i = 0; i < result.size(); i++) {
                JsonObject o = result.get(i).getAsJsonObject();
                if (o.has("rating") && !o.get("rating").isJsonNull()) {
                    sum += o.get("rating").getAsDouble();
                    count++;
                }
            }
            return count > 0 ? sum / count : 0.0;
        } catch (Exception e) {
            return 0.0;
        }
    }

    public int getReviewsCount(int movieId) {
        try {
            JsonArray result = SupabaseClient.select("reviews",
                    "movie_id=eq." + movieId + "&select=id");
            return result.size();
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean createReview(Review review) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("ticket_id", review.getTicketId());
            data.put("movie_id", review.getMovieId());
            data.put("user_id", review.getUserId());
            data.put("rating", review.getRating());
            data.put("review_text", review.getReviewText());
            SupabaseClient.insert("reviews", data);
            return true;
        } catch (Exception e) {
            System.err.println("Error creating review: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteReviewByTicketId(int ticketId) {
        try {
            SupabaseClient.delete("reviews", "ticket_id=eq." + ticketId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Review mapJsonToReview(JsonObject json) {
        Review r = new Review();
        r.setId(json.get("id").getAsInt());
        r.setTicketId(json.get("ticket_id").getAsInt());
        r.setMovieId(json.get("movie_id").getAsInt());
        r.setUserId(json.get("user_id").getAsInt());
        r.setRating(json.get("rating").getAsDouble());
        if (json.has("review_text") && !json.get("review_text").isJsonNull()) {
            r.setReviewText(json.get("review_text").getAsString());
        }
        if (json.has("created_at") && !json.get("created_at").isJsonNull()) {
            try {
                String ts = json.get("created_at").getAsString();
                if (ts.length() > 19) ts = ts.substring(0, 19);
                r.setCreatedAt(LocalDateTime.parse(ts));
            } catch (Exception ignored) {}
        }
        if (json.has("users") && !json.get("users").isJsonNull()) {
            JsonElement u = json.get("users");
            JsonObject userObj = null;
            if (u.isJsonObject()) userObj = u.getAsJsonObject();
            else if (u.isJsonArray() && u.getAsJsonArray().size() > 0)
                userObj = u.getAsJsonArray().get(0).getAsJsonObject();
            if (userObj != null) {
                if (userObj.has("full_name") && !userObj.get("full_name").isJsonNull()) {
                    r.setUserName(userObj.get("full_name").getAsString());
                } else if (userObj.has("username") && !userObj.get("username").isJsonNull()) {
                    r.setUserName(userObj.get("username").getAsString());
                }
            }
        }
        return r;
    }
}
