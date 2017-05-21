package com.example.ubuntu_master.googlemaps;

/**
 * Created by ubuntu-master on 21.05.17.
 */

public class CityInfo {
    private String name;

    public String getName() {
        return name;
    }

    public Double getCoordinateX() {
        return coordinateX;
    }

    public Double getCoordinateY() {
        return coordinateY;
    }

    private Double coordinateX;

    public CityInfo(String name, Double coordinateX, Double coordinateY) {
        this.name = name;
        this.coordinateX = coordinateX;
        this.coordinateY = coordinateY;
    }

    private Double coordinateY;
}
