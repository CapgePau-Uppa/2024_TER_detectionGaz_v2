package com.example.gazdetectorapplication.pathfinding;

public class Pair {
    private int x;
    private int y;

    public Pair(int x, int y){
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "Coordinate{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    public void addX(int x){
        this.x += x;
    }

    public void addY(int y){
        this.y += y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isPartOf(int n){
        return (n==this.x || n==this.y);
    }



}
