package com.cinema.ticket.dao;

import com.cinema.ticket.Movie;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovieDAO {

    public List<Movie> getAllMovies() {
        List<Movie> movies = new ArrayList<>();
        String sql = "SELECT * FROM movies WHERE is_active = true ORDER BY title";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Movie movie = new Movie();
                movie.setId(rs.getInt("id"));
                movie.setTitle(rs.getString("title"));
                movie.setDescription(rs.getString("description"));
                movie.setGenre(rs.getString("genre"));
                movie.setDuration(rs.getInt("duration"));
                movie.setAgeRating(rs.getString("age_rating"));
                movie.setRating(rs.getDouble("rating"));
                movie.setPosterUrl(rs.getString("poster_url"));
                movie.setTrailerUrl(rs.getString("trailer_url"));
                movies.add(movie);
            }

        } catch (SQLException e) {
            System.err.println("Error getting movies: " + e.getMessage());
        }
        return movies;
    }

    public List<Movie> getNewMovies() {
        List<Movie> movies = new ArrayList<>();
        String sql = "SELECT * FROM movies WHERE is_active = true ORDER BY created_at DESC LIMIT 5";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Movie movie = new Movie();
                movie.setId(rs.getInt("id"));
                movie.setTitle(rs.getString("title"));
                movie.setDescription(rs.getString("description"));
                movie.setGenre(rs.getString("genre"));
                movie.setDuration(rs.getInt("duration"));
                movie.setAgeRating(rs.getString("age_rating"));
                movie.setRating(rs.getDouble("rating"));
                movie.setPosterUrl(rs.getString("poster_url"));
                movies.add(movie);
            }

        } catch (SQLException e) {
            System.err.println("Error getting new movies: " + e.getMessage());
        }
        return movies;
    }

    public Movie getMovieById(int id) {
        String sql = "SELECT * FROM movies WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Movie movie = new Movie();
                movie.setId(rs.getInt("id"));
                movie.setTitle(rs.getString("title"));
                movie.setDescription(rs.getString("description"));
                movie.setGenre(rs.getString("genre"));
                movie.setDuration(rs.getInt("duration"));
                movie.setAgeRating(rs.getString("age_rating"));
                movie.setRating(rs.getDouble("rating"));
                movie.setPosterUrl(rs.getString("poster_url"));
                movie.setTrailerUrl(rs.getString("trailer_url"));
                return movie;
            }

        } catch (SQLException e) {
            System.err.println("Error getting movie by ID: " + e.getMessage());
        }
        return null;
    }

    public boolean addMovie(Movie movie) {
        String sql = "INSERT INTO movies (title, description, genre, duration, age_rating, rating, poster_url, trailer_url) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, movie.getTitle());
            pstmt.setString(2, movie.getDescription());
            pstmt.setString(3, movie.getGenre());
            pstmt.setInt(4, movie.getDuration());
            pstmt.setString(5, movie.getAgeRating());
            pstmt.setDouble(6, movie.getRating());
            pstmt.setString(7, movie.getPosterUrl());
            pstmt.setString(8, movie.getTrailerUrl());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error adding movie: " + e.getMessage());
            return false;
        }
    }
}