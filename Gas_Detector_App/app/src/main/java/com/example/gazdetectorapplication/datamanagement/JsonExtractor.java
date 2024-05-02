package com.example.gazdetectorapplication.datamanagement;

import android.content.res.Resources;

import com.example.gazdetectorapplication.pathfinding.Floor;
import com.example.gazdetectorapplication.pathfinding.Pair;
import com.example.gazdetectorapplication.pathfinding.Room;
import com.example.gazdetectorapplication.pathfinding.ZoneData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class JsonExtractor {
    Resources resources;

    public JsonExtractor( Resources resources) {
        this.resources = resources;
    }



    public ZoneData extractDatas(int data) {
        ArrayList<Floor> floors = new ArrayList<Floor>();
        try {
            JSONObject js = new JSONObject(loadJson(data));
            JSONObject floor = (JSONObject) js.get("floors");
            floors = getFloors(floor);
        } catch (JSONException | IOException e) {
            throw new RuntimeException(e);
        }
        return new ZoneData("0000","0000", new ArrayList<String>(), new ArrayList<String>(), floors);
    }

    private String loadJson(int data) throws IOException {
        InputStream stream = resources.openRawResource(data);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } finally {
            stream.close();
        }

        return writer.toString();
    }

    private ArrayList<Floor> getFloors(JSONObject floor) throws JSONException {//TODO
        ArrayList<Floor> floors = new ArrayList<>();
        Iterator<?> keys = floor.keys();
        while(keys.hasNext()){
            String key = (String)keys.next();
            if(floor.get(key) instanceof JSONObject){
                int x = (int) ((JSONObject) floor.get(key)).getJSONArray("imageSize").get(0);
                int y = (int) ((JSONObject) floor.get(key)).getJSONArray("imageSize").get(1);
                Pair imageSize = new Pair(x,y);
                String imageName = ((JSONObject) floor.get(key)).getString("mapName");
                ArrayList<Room> rooms = new ArrayList<>();
                ArrayList<Integer> HPEnd = new ArrayList<>();
                ArrayList<Integer> MPEnd = new ArrayList<>();
                ArrayList<Integer> LPEnd = new ArrayList<>();
                ArrayList<Pair> fireDoors = new ArrayList<>();//TODO
                rooms = getRoomsJson((JSONObject) ((JSONObject) floor.get(key)).getJSONObject("rooms"));
                JSONArray highPriorityEnds = ((JSONObject) floor.get(key)).getJSONArray("HighPriorityEnds");
                for(int i = 0; i<highPriorityEnds.length();i++){
                    HPEnd.add((Integer) highPriorityEnds.get(i));
                }
                JSONArray mediumPriorityEnds = ((JSONObject) floor.get(key)).getJSONArray("MediumPriorityEnds");
                for(int i = 0; i<mediumPriorityEnds.length();i++){
                    MPEnd.add((Integer) mediumPriorityEnds.get(i));
                }
                JSONArray lowPriorityEnds = ((JSONObject) floor.get(key)).getJSONArray("LowPriorityEnds");
                for(int i = 0; i<lowPriorityEnds.length();i++){
                    LPEnd.add((Integer) lowPriorityEnds.get(i));
                }
                JSONObject fireDoorsJson = ((JSONObject) floor.get(key)).getJSONObject("fireDoor");
                Iterator<?> fireDoorIterator = fireDoorsJson.keys();
                while(fireDoorIterator.hasNext()){
                    String keyFireDoor = (String) fireDoorIterator.next();
                    if(fireDoorsJson.get(keyFireDoor) instanceof JSONArray){
                        int fireDoorX = Integer.parseInt(fireDoorsJson.getJSONArray(keyFireDoor).get(0).toString());
                        int fireDoorY = Integer.parseInt(fireDoorsJson.getJSONArray(keyFireDoor).get(1).toString());
                        fireDoors.add(new Pair(fireDoorX, fireDoorY));
                    }

                }

                floors.add(new Floor(rooms,imageName,imageSize,HPEnd,MPEnd,LPEnd,fireDoors,new ArrayList<Integer>()));
            }
        }
        return floors;
    }

    private ArrayList<Room> getRoomsJson(JSONObject rooms) throws JSONException {
        ArrayList<Room> toReturn = new ArrayList<>();
        Iterator<?> keys = rooms.keys();
        while(keys.hasNext() ) {
            String key = (String)keys.next();
            if ( rooms.get(key) instanceof JSONObject ) {
                int x = (int) ((JSONObject) rooms.get(key)).getJSONArray("xy1").get(0);
                int y = (int) ((JSONObject) rooms.get(key)).getJSONArray("xy1").get(1);
                boolean isPMRRoom = ((JSONObject) rooms.get(key)).getBoolean("isPMRRoom");
                Pair c0 = new Pair(x,y);
                x = (int) ((JSONObject) rooms.get(key)).getJSONArray("xy2").get(0);
                y = (int) ((JSONObject) rooms.get(key)).getJSONArray("xy2").get(1);
                Pair c1 = new Pair(x,y);
                JSONObject doorsJson = (JSONObject) ((JSONObject) rooms.get(key)).get("doors");
                HashMap<Integer, Pair> doors = new HashMap<>();
                Iterator<?> keysDoor = doorsJson.keys();
                while(keysDoor.hasNext()){
                    String keyDoor = (String) keysDoor.next();
                    if(doorsJson.get(keyDoor) instanceof JSONArray){
                        Pair c = new Pair(Integer.parseInt(doorsJson.getJSONArray(keyDoor).get(0).toString()),
                                Integer.parseInt(doorsJson.getJSONArray(keyDoor).get(1).toString()));
                        doors.put(Integer.parseInt(keyDoor),c);
                    }
                }
                JSONObject ends = (JSONObject) ((JSONObject) rooms.get(key)).get("end");
                ArrayList<Pair> end = new ArrayList<>();
                Iterator<?> endIterator = ends.keys();
                while(endIterator.hasNext()){
                    String keyEnd = (String) endIterator.next();
                    if(ends.get(keyEnd)instanceof JSONArray){
                        Pair c = new Pair(Integer.parseInt(ends.getJSONArray(keyEnd).get(0).toString()),
                                Integer.parseInt(ends.getJSONArray(keyEnd).get(1).toString()));
                        end.add(c);
                    }
                }
                Room r = new Room(Integer.parseInt(key),c0,c1,end,doors, new HashMap<Room, Integer>(), isPMRRoom);
                toReturn.add(r);
            }
        }
        return toReturn;
    }

}
