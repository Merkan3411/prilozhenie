package com.cinema.ticket.models;

public class Movie {
    private int id;
    private String title;
    private String description;
    private String genre;
    private int duration;
    private String ageRating;
    private double rating;
    private String posterUrl;
    private String trailerUrl;
    private boolean isActive;

    public Movie() {
    }


    public Movie(int id, String title, String description, String genre, int duration,
                 String ageRating, double rating, String posterUrl, String trailerUrl, boolean isActive) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.genre = genre;
        this.duration = duration;
        this.ageRating = ageRating;
        this.rating = rating;
        this.posterUrl = posterUrl;
        this.trailerUrl = trailerUrl;
        this.isActive = isActive;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public String getAgeRating() { return ageRating; }
    public void setAgeRating(String ageRating) { this.ageRating = ageRating; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterPath) { this.posterUrl = posterPath; }

    public String getTrailerUrl() { return trailerUrl; }
    public void setTrailerUrl(String trailerUrl) { this.trailerUrl = trailerUrl; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    @Override
    public String toString() {
        return title;
    }
}
