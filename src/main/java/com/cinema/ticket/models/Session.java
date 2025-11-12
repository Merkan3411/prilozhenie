package com.cinema.ticket;

import java.time.LocalDate;
import java.time.LocalTime;

public class Session {
    private int id;
    private int movieId;
    private int hallId;
    private LocalDate sessionDate;
    private LocalTime sessionTime;
    private double price;
    private int availableSeats;
    private String movieTitle;
    private String hallName;

    public Session() {}

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getMovieId() { return movieId; }
    public void setMovieId(int movieId) { this.movieId = movieId; }
    public int getHallId() { return hallId; }
    public void setHallId(int hallId) { this.hallId = hallId; }
    public LocalDate getSessionDate() { return sessionDate; }
    public void setSessionDate(LocalDate sessionDate) { this.sessionDate = sessionDate; }
    public LocalTime getSessionTime() { return sessionTime; }
    public void setSessionTime(LocalTime sessionTime) { this.sessionTime = sessionTime; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public int getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }
    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }
    public String getHallName() { return hallName; }
    public void setHallName(String hallName) { this.hallName = hallName; }
}