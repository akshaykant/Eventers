package com.akshaykant.com.eventers;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.sql.Time;
import java.util.Date;

/**
 * Created by Akshay Kant on 28-11-2016.
 */

public class Events implements Serializable {

    public Events() {
    }

    public Events(String eventType, String location, String date, String time, String dressCode, String weather, String organiser, double latitude, double longitude) {
        this.eventType = eventType;
        this.location = location;
        this.date = date;
        this.time = time;
        this.dressCode = dressCode;
        this.weather = weather;
        this.organiser = organiser;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    private String eventType;

    private String location;

    private String date;

    private String time;

    private String dressCode;

    private String weather;

    private String organiser;

    private double latitude;

    private double longitude;

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDressCode() {
        return dressCode;
    }

    public void setDressCode(String dressCode) {
        this.dressCode = dressCode;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getOrganiser() {
        return organiser;
    }

    public void setOrganiser(String organiser) {
        this.organiser = organiser;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
