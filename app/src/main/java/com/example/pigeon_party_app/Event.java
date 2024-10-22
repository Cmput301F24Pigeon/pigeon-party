package com.example.pigeon_party_app;
import java.time.LocalTime;
import java.time.LocalDate;


public class Event {
    private LocalDate date;
    private LocalTime time;
    private String details;
    private int capacity;
    private String name;
    private String location;

    public Event(LocalDate date, LocalTime time, String details, int capacity, String name, String location) {
        this.date = date;
        this.time = time;
        this.details = details;
        this.capacity = capacity;
        this.name = name;
        this.location = location;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
