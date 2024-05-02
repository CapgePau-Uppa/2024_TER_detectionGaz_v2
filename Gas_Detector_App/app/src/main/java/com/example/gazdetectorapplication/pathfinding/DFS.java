package com.example.gazdetectorapplication.pathfinding;

import java.util.ArrayList;
import java.util.HashMap;

public class DFS {
    private int weight;
    private ArrayList<Room> pathRooms;
    private ZoneData zoneData;
    private int currentFloor;

    public DFS(int weight, ZoneData zoneData, int currentFloor){
        this.weight = weight;
        this.zoneData = zoneData;
        this.currentFloor = currentFloor;
        this.pathRooms = new ArrayList<Room>();

    }

    public ArrayList<Room> getPMRPath(ArrayList<Room> path, Room actualRoom, int poids){
        HashMap<ArrayList<Room>, Integer> allPaths = new HashMap<>();
        if(actualRoom.isPMRRoom() || actualRoom.getEnd().size()>0){
            if(!path.contains(actualRoom)){
                path.add(actualRoom);
                return path;
            }else{
                if(path.size()==1){
                    allPaths.put(path,30);
                }
            }
        }
        for(Room r : actualRoom.getNearestRooms().keySet()){
            if(!path.contains(r)){
                ArrayList<Room> newArray = (ArrayList<Room>) path.clone();
                newArray.add(r);
                getPMRPath(newArray,r,poids+actualRoom.getNearestRooms().get(r));
                allPaths.put(newArray,poids+actualRoom.getNearestRooms().get(r));
            }
        }

        HashMap<ArrayList<Room>, Integer> pathFound = new HashMap<>();
        for(ArrayList<Room> rooms : allPaths.keySet()){
            int goThroughFire = 0;
            if(!hasANonPassableObstacle(rooms)){
                int obstacleWeight = getObstacleWeight(rooms);
                for(int i =0;i<rooms.size();i++){
                    for(Pair p : zoneData.getFloors().get(currentFloor).getFireDoors()){
                        if(i!=0){
                            if(p.isPartOf(rooms.get(i).getId()) && p.isPartOf(rooms.get(i-1).getId())){
                                goThroughFire +=1000;
                            }
                        }
                    }
                    if(currentFloor==0){
                        if(zoneData.getFloors().get(currentFloor).getMPEnd().contains(rooms.get(i).getId())){
                            pathFound.put(rooms, allPaths.get(rooms) * (1 + obstacleWeight + goThroughFire));
                        }
                        if(zoneData.getFloors().get(currentFloor).getHPEnd().contains(rooms.get(i).getId())) {
                            pathFound.put(rooms, allPaths.get(rooms) * (1+obstacleWeight + goThroughFire)/10);
                        }
                    }
                    if(zoneData.getFloors().get(currentFloor).getLPEnd().contains(rooms.get(i).getId())){
                        pathFound.put(rooms, allPaths.get(rooms) * (7500 + obstacleWeight + goThroughFire));
                    }
                    if(zoneData.getFloors().get(currentFloor).getPMRRoom().contains(rooms.get(i).getId())){
                        pathFound.put(rooms, allPaths.get(rooms) * (1 + obstacleWeight + goThroughFire)/10);
                    }
                }
            }

        }
        for(ArrayList<Room> rooms : pathFound.keySet()){
            if(pathFound.get(rooms)< weight){
                if(isSafeForPMR(rooms) || !rooms.get(rooms.size()-1).getEnd().isEmpty()){
                    weight = pathFound.get(rooms);
                    pathRooms = rooms;
                }

            }
        }
        return pathRooms;
    }



    public ArrayList<Room> getPath(ArrayList<Room> path, Room actualRoom, int poids) {
        HashMap<ArrayList<Room>, Integer> allPaths = new HashMap<>();
        if(actualRoom.getEnd().size()>0){
            if(!path.contains(actualRoom)){
                path.add(actualRoom);
                return path;
            }else{
                if(path.size()==1){
                    allPaths.put(path,30);
                }
            }
        }
        for(Room r : actualRoom.getNearestRooms().keySet()){
            if(!path.contains(r)){
                ArrayList<Room> newArray = (ArrayList<Room>) path.clone();
                newArray.add(r);
                getPath(newArray,r,poids+actualRoom.getNearestRooms().get(r));
                allPaths.put(newArray,poids+actualRoom.getNearestRooms().get(r));
            }
        }

        HashMap<ArrayList<Room>, Integer> pathFound = new HashMap<>();
        for(ArrayList<Room> rooms : allPaths.keySet()){
            int goThroughFire = 0;
            if(!hasANonPassableObstacle(rooms)){
                int obstacleWeight = getObstacleWeight(rooms);
                for(int i =0;i<rooms.size();i++){
                    for(Pair p : zoneData.getFloors().get(currentFloor).getFireDoors()){
                        if(i!=0){
                            if(p.isPartOf(rooms.get(i).getId()) && p.isPartOf(rooms.get(i-1).getId())){
                                goThroughFire +=1000;
                            }
                        }
                    }
                    if(zoneData.getFloors().get(currentFloor).getLPEnd().contains(rooms.get(i).getId())){
                        pathFound.put(rooms, allPaths.get(rooms) * (15000 + obstacleWeight + goThroughFire));
                    }
                    if(zoneData.getFloors().get(currentFloor).getMPEnd().contains(rooms.get(i).getId())){
                        pathFound.put(rooms, allPaths.get(rooms) * (50 + obstacleWeight + goThroughFire));
                    }
                    if(zoneData.getFloors().get(currentFloor).getHPEnd().contains(rooms.get(i).getId())) {
                        pathFound.put(rooms, allPaths.get(rooms) * (1+obstacleWeight + goThroughFire)/10);
                    }
                }
            }

        }
        for(ArrayList<Room> rooms : pathFound.keySet()){
            if(pathFound.get(rooms)< weight && !rooms.get(rooms.size()-1).getEnd().isEmpty()){
                weight = pathFound.get(rooms);
                pathRooms = rooms;
            }
        }
        return pathRooms;
    }

    private boolean isSafeForPMR(ArrayList<Room> rooms) {
        for(Room r : rooms){
            if(zoneData.getFloors().get(currentFloor).getPMRRoom().contains(r.getId())){
                return true;
            }
        }
        return false;
    }

    private int getObstacleWeight(ArrayList<Room> rooms) {
        int obstacleWeight = 0;
        for(Room r : rooms){
            obstacleWeight+=(r.getObstacleLevel()*400);
        }
        return obstacleWeight;
    }

    private boolean hasANonPassableObstacle(ArrayList<Room> rooms) {
        for(Room r : rooms){
            if(r.getObstacleLevel()==3){
                return true;
            }
        }
        return false;
    }

}
