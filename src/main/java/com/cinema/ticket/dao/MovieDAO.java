package com.cinema.ticket.dao;

import com.cinema.ticket.SupabaseClient;
import com.cinema.ticket.models.Movie;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovieDAO {

    public List<Movie> getAllMovies() {
        List<Movie> movies = new ArrayList<>();
        try {
            JsonArray result = SupabaseClient.select("movies", "is_active=eq.true&select=*,poster_url,trailer_url&order=title.asc");
            for (int i = 0; i < result.size(); i++) {
                movies.add(mapJsonToMovie(result.get(i).getAsJsonObject()));
            }
        } catch (Exception e) {
            System.err.println("Error getting movies: " + e.getMessage());
        }
        return movies;
    }

    public List<Movie> getNewMovies() {
        List<Movie> movies = new ArrayList<>();
        try {
            JsonArray result = SupabaseClient.select("movies", "is_active=eq.true&select=*,poster_url,trailer_url&order=id.desc&limit=5");
            for (int i = 0; i < result.size(); i++) {
                movies.add(mapJsonToMovie(result.get(i).getAsJsonObject()));
            }
        } catch (Exception e) {
            System.err.println("Error getting new movies: " + e.getMessage());
        }
        return movies;
    }

    public Movie getMovieById(int id) {
        try {
            JsonArray result = SupabaseClient.select("movies", "id=eq." + id + "&select=*,poster_url,trailer_url");
            if (result.size() > 0) {
                return mapJsonToMovie(result.get(0).getAsJsonObject());
            }
        } catch (Exception e) {
            System.err.println("Error getting movie by ID: " + e.getMessage());
        }
        return null;
    }

    public boolean createMovie(Movie movie) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("title", movie.getTitle());
            data.put("description", movie.getDescription());
            data.put("genre", movie.getGenre());
            data.put("duration", movie.getDuration());
            data.put("age_rating", movie.getAgeRating());
            data.put("rating", movie.getRating());
            data.put("poster_url", movie.getPosterUrl());  // Added poster_url
            data.put("trailer_url", movie.getTrailerUrl());  // Added trailer_url
            data.put("is_active", movie.isActive());
            SupabaseClient.insert("movies", data);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean updateMovie(Movie movie) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("title", movie.getTitle());
            data.put("description", movie.getDescription());
            data.put("genre", movie.getGenre());
            data.put("duration", movie.getDuration());
            data.put("age_rating", movie.getAgeRating());
            data.put("rating", movie.getRating());
            data.put("poster_url", movie.getPosterUrl());  // Added poster_url
            data.put("trailer_url", movie.getTrailerUrl());  // Added trailer_url
            data.put("is_active", movie.isActive());
            SupabaseClient.update("movies", "id=eq." + movie.getId(), data);
            return true;
        } catch (Exception e) {
            System.err.println("Error updating movie: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteMovie(int id) {
        try {
            SupabaseClient.delete("movies", "id=eq." + id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public int getTotalMoviesCount() {
        try {
            JsonArray result = SupabaseClient.select("movies", "select=id");
            return result.size();
        } catch (Exception e) {
            return 0;
        }
    }

    private Movie mapJsonToMovie(JsonObject json) {
        Movie movie = new Movie();
        movie.setId(json.get("id").getAsInt());
        movie.setTitle(json.get("title").getAsString());
        movie.setDescription(json.has("description") && !json.get("description").isJsonNull()
                ? json.get("description").getAsString() : "");
        movie.setGenre(json.has("genre") && !json.get("genre").isJsonNull()
                ? json.get("genre").getAsString() : "");
        movie.setDuration(json.has("duration") && !json.get("duration").isJsonNull()
                ? json.get("duration").getAsInt() : 0);

        if (json.has("age_rating") && !json.get("age_rating").isJsonNull()) {
            movie.setAgeRating(json.get("age_rating").getAsString());
        }

        if (json.has("rating") && !json.get("rating").isJsonNull()) {
            movie.setRating(json.get("rating").getAsDouble());
        }

        if (json.has("poster_url") && !json.get("poster_url").isJsonNull()) {
            String poster = json.get("poster_url").getAsString().trim();
            if (!poster.isEmpty() && !poster.startsWith("http")) {
                poster = "https://xvbjaflhuqchygvjwqoi.supabase.co/storage/v1/object/public/movies/" + poster;
            }
            movie.setPosterUrl(poster);
        } else {
            movie.setPosterUrl(null);
        }

        if (json.has("trailer_url") && !json.get("trailer_url").isJsonNull()) {
            String trailer = json.get("trailer_url").getAsString().trim();
            movie.setTrailerUrl(trailer.isEmpty() ? null : trailer);
        } else {
            movie.setTrailerUrl(null);
        }

        movie.setActive(json.get("is_active").getAsBoolean());
        return movie;
    }
    }