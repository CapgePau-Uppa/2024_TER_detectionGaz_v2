package com.example.gazdetectorapplication.util;

import java.util.ArrayList;

public class ZoneData {
    private String zoneId;
    private String zoneName;
    private ArrayList<String> zoneAdministrator;
    private ArrayList<String> zoneWorker;
    private Coordinate imageSize;

    public ZoneData(String zoneId, String zoneName, ArrayList<String> zoneAdministrator, ArrayList<String> zoneWorker, Coordinate imageSize) {
        this.zoneId = zoneId;
        this.zoneName = zoneName;
        this.zoneAdministrator = zoneAdministrator;
        this.zoneWorker = zoneWorker;
        this.imageSize = imageSize;
    }

    public String getZoneId() {
        return zoneId;
    }

    public String getZoneName() {
        return zoneName;
    }

    public ArrayList<String> getZoneAdministrator() {
        return zoneAdministrator;
    }

    public ArrayList<String> getZoneWorker() {
        return zoneWorker;
    }

    public Coordinate getImageSize() {
        return imageSize;
    }

}
