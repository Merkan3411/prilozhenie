package com.cinema.ticket.models;

public class Session {
    private int id;
    private int movieId;
    private String movieTitle;
    private int hallNumber;
    private String hallName;
    private String sessionTime;
    private String sessionDate;

    public Session() {}

    public Session(int movieId, int hallNumber, String sessionTime, String sessionDate) {
        this.movieId = movieId;
        this.hallNumber = hallNumber;
        this.sessionTime = sessionTime;
        this.sessionDate = sessionDate;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getMovieId() { return movieId; }
    public void setMovieId(int movieId) { this.movieId = movieId; }

    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

    public int getHallNumber() { return hallNumber; }
    public void setHallNumber(int hallNumber) { this.hallNumber = hallNumber; }

    public String getHallName() { return hallName; }
    public void setHallName(String hallName) { this.hallName = hallName; }

    public String getSessionTime() { return sessionTime; }
    public void setSessionTime(String sessionTime) { this.sessionTime = sessionTime; }

    public String getSessionDate() { return sessionDate; }
    public void setSessionDate(String sessionDate) { this.sessionDate = sessionDate; }

    @Override
    public String toString() {
        return movieTitle + " - " + sessionDate + " " + sessionTime + " (Зал: " + hallNumber + ")";
    }
}





