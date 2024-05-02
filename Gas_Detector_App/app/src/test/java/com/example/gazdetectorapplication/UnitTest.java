package com.example.gazdetectorapplication;

import org.junit.Test;

import static org.junit.Assert.*;

import com.example.gazdetectorapplication.pathfinding.DFS;
import com.example.gazdetectorapplication.pathfinding.Floor;
import com.example.gazdetectorapplication.pathfinding.Pair;
import com.example.gazdetectorapplication.pathfinding.Room;
import com.example.gazdetectorapplication.pathfinding.ZoneData;
import com.example.gazdetectorapplication.ui.map.MapFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class UnitTest {

    @Test
    public void testSimplePath(){//version 0
        ZoneData zd = getZoneDataForTu(0);
        DFS dfs = new DFS(10000000,zd,0);
        ArrayList<Room> rooms = zd.getFloors().get(0).getRooms();
        ArrayList<Integer> supposedResult = new ArrayList<>();
        ArrayList<Integer> result = new ArrayList<>();
        ArrayList<Room> path = new ArrayList<>();
        Room roomBegin = rooms.get(1);
        supposedResult.add(1);
        supposedResult.add(2);
        supposedResult.add(4);
        supposedResult.add(3);
        path.add(roomBegin);
        for(Room r : dfs.getPath(path,roomBegin,0)){
            result.add(r.getId());
        }
        System.out.println("test 0 : "+supposedResult.toString()+" = "+ result.toString());
        assertEquals(supposedResult,result);

    }

    @Test
    public void testSimplePathWithNewDoor(){//version 1
        ZoneData zd = getZoneDataForTu(1);
        DFS dfs = new DFS(10000000,zd,0);
        ArrayList<Room> rooms = zd.getFloors().get(0).getRooms();
        ArrayList<Integer> supposedResult = new ArrayList<>();
        ArrayList<Integer> result = new ArrayList<>();
        ArrayList<Room> path = new ArrayList<>();
        Room roomBegin = rooms.get(1);
        supposedResult.add(1);
        supposedResult.add(0);
        supposedResult.add(3);
        path.add(roomBegin);
        for(Room r : dfs.getPath(path,roomBegin,0)){
            result.add(r.getId());
        }
        System.out.println("test 1 : "+supposedResult.toString()+" = "+ result.toString());
        assertEquals(supposedResult,result);
    }

    @Test
    public void testSimplePathWithFireDoor(){//version 2
        ZoneData zd = getZoneDataForTu(2);
        DFS dfs = new DFS(10000000,zd,0);
        ArrayList<Room> rooms = zd.getFloors().get(0).getRooms();
        ArrayList<Integer> supposedResult = new ArrayList<>();
        ArrayList<Integer> result = new ArrayList<>();
        ArrayList<Room> path = new ArrayList<>();
        Room roomBegin = rooms.get(4);
        supposedResult.add(4);
        supposedResult.add(2);
        supposedResult.add(1);
        supposedResult.add(0);
        supposedResult.add(3);
        path.add(roomBegin);
        for(Room r : dfs.getPath(path,roomBegin,0)){
            result.add(r.getId());
        }
        System.out.println("test 2 : "+supposedResult.toString()+" = "+ result.toString());
        assertEquals(supposedResult,result);
    }

    @Test
    public void testSimplePathWithUnpassableRoom(){//version 3
        ZoneData zd = getZoneDataForTu(3);
        DFS dfs = new DFS(10000000,zd,0);
        ArrayList<Room> rooms = zd.getFloors().get(0).getRooms();
        ArrayList<Integer> supposedResult = new ArrayList<>();
        ArrayList<Integer> result = new ArrayList<>();
        ArrayList<Room> path = new ArrayList<>();
        Room roomBegin = rooms.get(4);
        supposedResult.add(4);
        supposedResult.add(3);
        path.add(roomBegin);
        for(Room r : dfs.getPath(path,roomBegin,0)){
            result.add(r.getId());
        }
        System.out.println("test 3 : "+supposedResult.toString()+" = "+ result.toString());
        assertEquals(supposedResult,result);
    }

    @Test
    public void testSimplePathWithMpEnd(){//version 4
        ZoneData zd = getZoneDataForTu(4);
        DFS dfs = new DFS(10000000,zd,0);
        ArrayList<Room> rooms = zd.getFloors().get(0).getRooms();
        ArrayList<Integer> supposedResult = new ArrayList<>();
        ArrayList<Integer> result = new ArrayList<>();
        ArrayList<Room> path = new ArrayList<>();
        Room roomBegin = rooms.get(2);
        supposedResult.add(2);
        supposedResult.add(1);
        supposedResult.add(0);
        supposedResult.add(3);
        path.add(roomBegin);
        for(Room r : dfs.getPath(path,roomBegin,0)){
            result.add(r.getId());
        }
        System.out.println("test 4 : "+supposedResult.toString()+" = "+ result.toString());
        assertEquals(supposedResult,result);
    }

    @Test
    public void testSimplePathWithLpEnd(){//version 5
        ZoneData zd = getZoneDataForTu(5);
        DFS dfs = new DFS(10000000,zd,0);
        ArrayList<Room> rooms = zd.getFloors().get(0).getRooms();
        ArrayList<Integer> supposedResult = new ArrayList<>();
        ArrayList<Integer> result = new ArrayList<>();
        ArrayList<Room> path = new ArrayList<>();
        Room roomBegin = rooms.get(2);
        supposedResult.add(2);
        supposedResult.add(1);
        path.add(roomBegin);
        for(Room r : dfs.getPath(path,roomBegin,0)){
            result.add(r.getId());
        }
        System.out.println("test 5 : "+supposedResult.toString()+" = "+ result.toString());
        assertEquals(supposedResult,result);
    }

    @Test
    public void testSimplePathWithHpEnd(){//version 6
        ZoneData zd = getZoneDataForTu(6);
        DFS dfs = new DFS(10000000,zd,0);
        ArrayList<Room> rooms = zd.getFloors().get(0).getRooms();
        ArrayList<Integer> supposedResult = new ArrayList<>();
        ArrayList<Integer> result = new ArrayList<>();
        ArrayList<Room> path = new ArrayList<>();
        Room roomBegin = rooms.get(3);
        supposedResult.add(3);
        supposedResult.add(0);
        supposedResult.add(1);
        supposedResult.add(2);
        supposedResult.add(4);
        path.add(roomBegin);
        for(Room r : dfs.getPath(path,roomBegin,0)){
            result.add(r.getId());
        }
        System.out.println("test 6 : "+supposedResult.toString()+" = "+ result.toString());
        assertEquals(supposedResult,result);
    }

     /* Légende :   [MPE] = Medium Priority End (sortie classique)
                    [LPE] = Low Priority End (sortie à éviter mais possible)
                    [HPE] = High Priority End (sortie à prendre avant toute autre)

     version 0 : on part de 1, chemin = [1,2,4,3]
    _____________________________
   |             |     |         |
   |     0       |  1            |
   |_____  ______|_____|         |
   |                   |         |
 [MPE]       3         |    2    |
   |________  _________|         |
   |                   |         |
   |         4                   |
   |___________________|_________|


    version 1 : (ouverture entre les pièces 0/1) : on part de 1, chemin = [1,0,3]
    _____________________________
   |             |     |         |
   |     0          1            |
   |_____  ______|_____|         |
   |                   |         |
 [MPE]       3         |    2    |
   |________  _________|         |
   |                   |         |
   |         4                   |
   |___________________|_________|

     version 2 : (ouverture entre les pièces 0/1 + porte coupe feu entre 3 et 4)
     on part de 4, chemin = [4,2,1,0,3] (on évite de prendre la porte coupe feu)
    _____________________________
   |             |     |         |
   |     0          1            |
   |_____  ______|_____|         |
   |                   |         |
 [MPE]       3         |    2    |
   |________.._________|         |
   |                   |         |
   |         4                   |
   |___________________|_________|


    version 3 : (pièce 1 est condamnée -> obliger de prendre la porte coupe feu)
    on part de 4, chemin = [4,3]
    _____________________________
   |             | X X |         |
   |     0        X X X          |
   |_____  ______|_X_X_|         |
   |                   |         |
 [MPE]       3         |    2    |
   |________.._________|         |
   |                   |         |
   |         4                   |
   |___________________|_________|

     version 4 : (ouverture entre les pièces 0/1 + porte coupe feu entre 3 et 4 + pièce n°1 est une lowPriorityEnd (ascenseur))
     on part de 2, chemin = [2,1,0,3] (on a beau commencé à côté de la sortie 1, c'est la sortie de plus haute priorité qui est préférée (la 3))
    ______________[LPE]___________
   |             |     |         |
   |     0          1            |
   |_____  ______|_____|         |
   |                   |         |
 [MPE]       3         |    2    |
   |________.._________|         |
   |                   |         |
   |         4                   |
   |___________________|_________|


    version 5 : (pareil que précédemment mais la pièce 3 ne peut être emprunté -> obliger de prendre la [LPE])
     on part de 2, chemin = [2,1]
    ______________[LPE]___________
   |             |     |         |
   |     0          1            |
   |_____  ______|_____|         |
   |X  X  X   X  X  X X|         |
 [MPE]X  X   X   X   X |    2    |
   |X__X___X.._X___X__X|         |
   |                   |         |
   |         4                   |
   |___________________|_________|

  version 6 : ([HPE] dans la 4, mais on évitera la porte coupe feu pour y accéder)
     on part de 3, chemin = [3,0,1,2,4] (on a beau commencé à côté de la sortie MPE, c'est la sortie de plus haute priorité qui est préférée (la 4))
    ______________[LPE]___________
   |             |     |         |
   |     0          1            |
   |_____  ______|_____|         |
   |                   |         |
 [MPE]       3         |    2    |
   |________.._________|         |
   |                   |         |
   |         4                   |
   |_______[HPE]_______|_________|


     */

    ZoneData getZoneDataForTu(int version){
        ArrayList<Floor> floors = new ArrayList<>();
        ArrayList<Integer> hPEnd = new ArrayList<>();
        ArrayList<Integer> mPEnd = new ArrayList<>();
        ArrayList<Integer> lPEnd = new ArrayList<>();
        ArrayList<Pair> fireDoor = new ArrayList<>();
        if(version>1){
            fireDoor.add(new Pair(3,4));
        }
        mPEnd.add(3);
        if(version>3){
            lPEnd.add(1);
        }
        if(version>5){
            hPEnd.add(4);
        }
        Floor f = new Floor(getRoomsForTU(version),"testUnitMap",new Pair(150,150),
                hPEnd,mPEnd,lPEnd,fireDoor, new ArrayList<Integer>());
        floors.add(f);
        ZoneData zoneData = new ZoneData("0000","zoneUnit",new ArrayList<String>(),new ArrayList<String>(),floors);
        return zoneData;
    }

    ArrayList<Room> getRoomsForTU(int roomsVersion){
        ArrayList<Room> rooms = new ArrayList<>();
        HashMap<Integer, Pair> doorsR0 = new HashMap<>();
        doorsR0.put(3,new Pair(37,50));
        if(roomsVersion>0){
            doorsR0.put(1, new Pair(75,25));
        }
        HashMap<Integer, Pair> doorsR1 = new HashMap<>();
        doorsR1.put(2,new Pair(100,25));
        if(roomsVersion>0){
            doorsR1.put(0, new Pair(75,25));
        }
        HashMap<Integer, Pair> doorsR2 = new HashMap<>();
        doorsR2.put(1,new Pair(100,25));
        doorsR2.put(4,new Pair(100,125));
        HashMap<Integer, Pair> doorsR3 = new HashMap<>();
        doorsR3.put(0,new Pair(37,50));
        doorsR3.put(4,new Pair(50,100));
        HashMap<Integer, Pair> doorsR4 = new HashMap<>();
        doorsR4.put(3,new Pair(50,100));
        doorsR4.put(2,new Pair(100,125));
        ArrayList<Pair> endR3 = new ArrayList<>();
        ArrayList<Pair> endR1 = new ArrayList<>();
        ArrayList<Pair> endR4 = new ArrayList<>();
        endR3.add(new Pair(0,75));
        if(roomsVersion>3){
            endR1.add(new Pair(87,0));
        }
        if(roomsVersion>5){
            endR4.add(new Pair(50,150));
        }
        Room r0 = new Room(0,new Pair(0,0),new Pair(75,50),new ArrayList<>(),doorsR0,new HashMap<Room, Integer>(), false);
        Room r1 = new Room(1,new Pair(75,0),new Pair(100,50),endR1,doorsR1,new HashMap<Room, Integer>(), false);
        Room r2 = new Room(2,new Pair(100,0),new Pair(150,150),new ArrayList<>(),doorsR2,new HashMap<Room, Integer>(), false);
        Room r3 = new Room(3,new Pair(0,50),new Pair(100,100),endR3,doorsR3,new HashMap<Room, Integer>(), false);
        Room r4 = new Room(4,new Pair(0,100),new Pair(100,150),endR4,doorsR4,new HashMap<Room, Integer>(), false);
        HashMap<Room,Integer> nearestRoomsR0 = new HashMap<>();
        nearestRoomsR0.put(r3,51);
        if(roomsVersion>0){
            nearestRoomsR0.put(r1,50);
        }
        HashMap<Room,Integer> nearestRoomsR1 = new HashMap<>();
        nearestRoomsR1.put(r2,92);
        if(roomsVersion>0){
            nearestRoomsR1.put(r0,50);
        }
        HashMap<Room,Integer> nearestRoomsR2 = new HashMap<>();
        nearestRoomsR2.put(r1,92);
        nearestRoomsR2.put(r4,92);
        HashMap<Room,Integer> nearestRoomsR3 = new HashMap<>();
        nearestRoomsR3.put(r0,51);
        nearestRoomsR3.put(r4,50);
        HashMap<Room,Integer> nearestRoomsR4 = new HashMap<>();
        nearestRoomsR4.put(r3,50);
        nearestRoomsR4.put(r2,92);
        r0.setNearestRooms(nearestRoomsR0);
        r1.setNearestRooms(nearestRoomsR1);
        r2.setNearestRooms(nearestRoomsR2);
        r3.setNearestRooms(nearestRoomsR3);
        r4.setNearestRooms(nearestRoomsR4);
        if(roomsVersion==3){
            r1.setObstacleLevel(3);
        }
        if(roomsVersion==5){
            r3.setObstacleLevel(3);
        }
        rooms.add(r0);
        rooms.add(r1);
        rooms.add(r2);
        rooms.add(r3);
        rooms.add(r4);
        return rooms;
    }

}