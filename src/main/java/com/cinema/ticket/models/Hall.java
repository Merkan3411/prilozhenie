package com.cinema.ticket.models;

public class Hall {
    private int id;
    private String name;
    private int capacity;
    private int rows;
    private int seatsPerRow;

    public Hall() {}

    public Hall(String name, int capacity, int rows, int seatsPerRow) {
        this.name = name;
        this.capacity = capacity;
        this.rows = rows;
        this.seatsPerRow = seatsPerRow;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public int getRows() { return rows; }
    public void setRows(int rows) { this.rows = rows; }
    public int getSeatsPerRow() { return seatsPerRow; }
    public void setSeatsPerRow(int seatsPerRow) { this.seatsPerRow = seatsPerRow; }
}