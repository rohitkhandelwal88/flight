package com.rohit.flights.model;

public class Flight {
    private String flightNo;
    private String fromAirportCode;
    private String toAirportCode;
    private int startTime;
    private int endTime;

    // Getters and setters

    public String getFlightNo() {
        return flightNo;
    }

    public void setFlightNo(String flightNo) {
        this.flightNo = flightNo;
    }

    public String getFromAirportCode() {
        return fromAirportCode;
    }

    public void setFromAirportCode(String fromAirportCode) {
        this.fromAirportCode = fromAirportCode;
    }

    public String getToAirportCode() {
        return toAirportCode;
    }

    public void setToAirportCode(String toAirportCode) {
        this.toAirportCode = toAirportCode;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    // Calculate duration in minutes
    public int getDurationInMinutes() {
        int startHours = startTime / 100;
        int startMinutes = startTime % 100;
        int endHours = endTime / 100;
        int endMinutes = endTime % 100;

        int startTotalMinutes = startHours * 60 + startMinutes;
        int endTotalMinutes = endHours * 60 + endMinutes;

        if (endTotalMinutes < startTotalMinutes) {
            endTotalMinutes += 24 * 60; // adjust if end time is the next day
        }

        return endTotalMinutes - startTotalMinutes;
    }
}

