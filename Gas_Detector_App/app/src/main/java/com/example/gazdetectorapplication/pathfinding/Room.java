package com.example.gazdetectorapplication.pathfinding;

import java.util.ArrayList;
import java.util.HashMap;

public class Room {

    private int id;
    private Pair c0;
    private Pair c1;
    private HashMap<Integer, Pair> doors;
    private HashMap<Room, Integer> nearestRooms;
    private ArrayList<Pair> end;
    private int obstacleLevel;
    private boolean isPMRRoom;
    private boolean hasFaintedPerson;

    public Room(int id, Pair c0, Pair c1, ArrayList<Pair> end, HashMap<Integer, Pair> doors, HashMap<Room, Integer> nearestRooms, boolean isPMRRoom){
        this.id = id;
        this.c0 = c0;
        this.c1 = c1;
        this.end = end;
        this.doors = doors;
        this.nearestRooms = nearestRooms;
        this.obstacleLevel = 0;
        this.isPMRRoom = isPMRRoom;
        this.hasFaintedPerson=false;
    }


    public int getId() {
        return id;
    }

    public Pair getC0() {
        return c0;
    }

    public Pair getC1() {
        return c1;
    }

    public ArrayList<Pair> getEnd() {
        return end;
    }

    public HashMap<Integer, Pair> getDoors() {
        return doors;
    }

    public HashMap<Room, Integer> getNearestRooms() {
        return nearestRooms;
    }
    public int getObstacleLevel() {return obstacleLevel;}

    public boolean isPMRRoom(){return this.isPMRRoom;}
    public boolean hasFaintedPerson(){return this.hasFaintedPerson;}


    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                " / doors" + doors.toString() +
                '}';
    }

    public void setNearestRooms(HashMap<Room, Integer> nearestRooms) {
        this.nearestRooms = nearestRooms;
    }

    public void setObstacleLevel(int level){
        this.obstacleLevel = level;
    }

    public void setFaintedPerson(boolean b){
        this.hasFaintedPerson = b;
    }


    public Pair getMiddle() {
        Pair toReturn = new Pair(this.getC0().getX(), this.getC0().getY());
        toReturn.addX((this.getC1().getX()-this.getC0().getX())/2);
        toReturn.addY((this.getC1().getY()-this.getC0().getY())/2);
        return toReturn;
    }


}
