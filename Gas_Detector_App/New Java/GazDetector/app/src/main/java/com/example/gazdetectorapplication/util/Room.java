package com.example.gazdetectorapplication.util;

import java.util.ArrayList;
import java.util.HashMap;

public class Room {
    public int getId() {
        return id;
    }

    public Coordinate getC0() {
        return c0;
    }

    public Coordinate getC1() {
        return c1;
    }

    public ArrayList<Coordinate> getEnd() {
        return end;
    }

    public HashMap<Integer, Coordinate> getDoors() {
        return doors;
    }

    public HashMap<Room, Integer> getNearestRooms() {
        return nearestRooms;
    }

    private int id;
    private Coordinate c0;
    private Coordinate c1;

    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                '}';
    }

    private ArrayList<Coordinate> end;

    public void setNearestRooms(HashMap<Room, Integer> nearestRooms) {
        this.nearestRooms = nearestRooms;
    }

    private HashMap<Integer,Coordinate> doors;
    private HashMap<Room, Integer> nearestRooms;

    public Room(int id, Coordinate c0, Coordinate c1, ArrayList<Coordinate> end, HashMap<Integer,Coordinate> doors, HashMap<Room, Integer> nearestRooms){
        this.id = id;
        this.c0 = c0;
        this.c1 = c1;
        this.end = end;
        this.doors = doors;
        this.nearestRooms = nearestRooms;
    }





}
