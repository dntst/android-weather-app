package com.weather.app.utils.location;

public class Location {
    private double lat;
    private double lng;
    private float accuracy;
    private String name;

    public Location(double lat, double lng, float accuracy, String name) {
        this.lat = lat;
        this.lng = lng;
        this.accuracy = accuracy;
        this.name = name;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public String getName() {
        return name;
    }
}
