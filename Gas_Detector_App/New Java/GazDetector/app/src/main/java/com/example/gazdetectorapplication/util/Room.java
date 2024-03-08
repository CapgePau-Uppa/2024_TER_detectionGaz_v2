package com.example.gazdetectorapplication.util;

import java.util.ArrayList;
import java.util.HashMap;

public class Room {
    private int id;
    private Coordinate c0;
    private Coordinate c1;
    private ArrayList<Coordinate> end;
    private HashMap<Integer,Coordinate> doors;

    public Room(int id, Coordinate c0, Coordinate c1, ArrayList<Coordinate> end, HashMap<Integer,Coordinate> doors){
        this.id = id;
        this.c0 = c0;
        this.c1 = c1;
        this.end = end;
        this.doors = doors;
    }



}
