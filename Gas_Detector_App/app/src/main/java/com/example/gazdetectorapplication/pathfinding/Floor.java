package com.example.gazdetectorapplication.pathfinding;

import java.util.ArrayList;

public class Floor {
    private ArrayList<Room> rooms;
    private String mapName;
    private Pair imageSize;
    private ArrayList<Integer> HPEnd;
    private ArrayList<Integer> MPEnd;
    private ArrayList<Integer> LPEnd;
    private ArrayList<Integer> PMRRoom;
    private ArrayList<Pair> fireDoors;


    public Floor(ArrayList<Room> rooms, String mapName, Pair imageSize, ArrayList<Integer> HPEnd, ArrayList<Integer> MPEnd, ArrayList<Integer> LPEnd, ArrayList<Pair> fireDoors, ArrayList<Integer> PMRRoom) {
        this.rooms = rooms;
        this.mapName = mapName;
        this.imageSize = imageSize;
        this.HPEnd = HPEnd;
        this.MPEnd = MPEnd;
        this.LPEnd = LPEnd;
        this.PMRRoom = PMRRoom;
        this.fireDoors = fireDoors;
    }

    public ArrayList<Room> getRooms() {
        return rooms;
    }

    public String getMapName() {
        return mapName;
    }

    public Pair getImageSize() {
        return imageSize;
    }

    public ArrayList<Integer> getHPEnd() {
        return HPEnd;
    }

    public ArrayList<Integer> getMPEnd() {
        return MPEnd;
    }

    public ArrayList<Integer> getLPEnd() {
        return LPEnd;
    }

    public ArrayList<Pair> getFireDoors() {
        return fireDoors;
    }

    public ArrayList<Integer> getPMRRoom(){return this.PMRRoom;}

    public void setPMRRoom(ArrayList<Integer> pmrRoom){
        this.PMRRoom = pmrRoom;
    }

}
