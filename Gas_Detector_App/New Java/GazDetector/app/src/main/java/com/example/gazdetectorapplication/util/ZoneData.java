package com.example.gazdetectorapplication.util;

import java.util.ArrayList;

public class ZoneData {
    private String zoneId;
    private String zoneName;
    private ArrayList<String> zoneAdministrator;
    private ArrayList<String> zoneWorker;
    private ArrayList<Floor> floors;

    public ZoneData(String zoneId, String zoneName, ArrayList<String> zoneAdministrator, ArrayList<String> zoneWorker, ArrayList<Floor> floors) {
        this.zoneId = zoneId;
        this.zoneName = zoneName;
        this.zoneAdministrator = zoneAdministrator;
        this.zoneWorker = zoneWorker;
        this.floors = floors;
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

    public ArrayList<Floor> getFloors() {
        return floors;
    }

}
