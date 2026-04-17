module ticket.system {
    requires java.sql;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.media;
    requires com.google.gson;
    requires java.net.http;
    requires java.desktop;

    opens com.cinema.ticket to javafx.fxml;
    opens com.cinema.ticket.controllers to javafx.fxml;
    opens com.cinema.ticket.models to javafx.fxml, com.google.gson;
    opens com.cinema.ticket.dao to javafx.fxml;

    exports com.cinema.ticket;
    exports com.cinema.ticket.controllers;
    exports com.cinema.ticket.models;
    exports com.cinema.ticket.dao;
}

