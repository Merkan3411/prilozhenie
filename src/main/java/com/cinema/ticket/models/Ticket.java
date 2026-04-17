package com.cinema.ticket.models;

import java.time.LocalDateTime;

public class Ticket {

    private int id;
    private int sessionId;
    private int userId;
    private int rowNumber;
    private int seatNumber;
    private LocalDateTime purchaseDate;
    private String status;
    private String qrCode;
    private double totalPrice;
    private String movieTitle;
    private String sessionTime;
    private int movieId;

    public Ticket() {}

    public Ticket(int sessionId, int userId, int rowNumber, int seatNumber, double totalPrice) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.rowNumber = rowNumber;
        this.seatNumber = seatNumber;
        this.totalPrice = totalPrice;
        this.status = "BOOKED";
        this.purchaseDate = LocalDateTime.now();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getSessionId() { return sessionId; }
    public void setSessionId(int sessionId) { this.sessionId = sessionId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getRowNumber() { return rowNumber; }
    public void setRowNumber(int rowNumber) { this.rowNumber = rowNumber; }

    public int getSeatNumber() { return seatNumber; }
    public void setSeatNumber(int seatNumber) { this.seatNumber = seatNumber; }

    public LocalDateTime getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDateTime purchaseDate) { this.purchaseDate = purchaseDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

    public String getSessionTime() { return sessionTime; }
    public void setSessionTime(String sessionTime) { this.sessionTime = sessionTime; }

    public int getMovieId() { return movieId; }
    public void setMovieId(int movieId) { this.movieId = movieId; }

    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", sessionId=" + sessionId +
                ", userId=" + userId +
                ", rowNumber=" + rowNumber +
                ", seatNumber=" + seatNumber +
                ", purchaseDate=" + purchaseDate +
                ", status='" + status + '\'' +
                ", totalPrice=" + totalPrice +
                ", movieTitle='" + movieTitle + '\'' +
                ", sessionTime='" + sessionTime + '\'' +
                '}';
    }
}
