package com.example.gazdetectorapplication.ui.map;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.gazdetectorapplication.R;
import com.example.gazdetectorapplication.databinding.FragmentMapBinding;
import com.example.gazdetectorapplication.util.Pair;
import com.example.gazdetectorapplication.util.Floor;
import com.example.gazdetectorapplication.util.Room;
import com.example.gazdetectorapplication.util.ZoneData;

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

public class MapFragment extends Fragment {
    private FragmentMapBinding binding;
    private boolean canDraw = true;
    private int weigth = 1000000;
    private ZoneData zoneData = null;
    private int currentFloor = 0;
    private ArrayList<Room> pathRooms = new ArrayList<>();
    private int imageId = 0;

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        //RESOURCES
        binding = FragmentMapBinding.inflate(inflater, container, false);
        imageId = R.drawable.t_floor_0;
        binding.imageMap.setImageResource(imageId);


        //DATA EXTRACTION
        zoneData = extractDatas(R.raw.zonedata);
        binding.zoneDataText.setText(zoneData.getFloors().get(currentFloor).getMapName());

        for(Room r : zoneData.getFloors().get(currentFloor).getRooms()){
            HashMap<Room, Integer> nearestRooms = new HashMap<>();
            for(Room r2 : zoneData.getFloors().get(currentFloor).getRooms()){
                if(r.getDoors().containsKey(r2.getId())){
                    nearestRooms.put(r2,getDistanceBetweenRoom(r, r2));
                }
            }
            r.setNearestRooms(nearestRooms);
        }
        binding.rightButtonMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentFloor+=1;
                currentFloor=currentFloor%zoneData.getFloors().size();
                binding.zoneDataText.setText(zoneData.getFloors().get(currentFloor).getMapName());
                imageId = getResources().getIdentifier(zoneData.getFloors().get(currentFloor).getMapName(), "drawable", getContext().getPackageName());//R.drawable.t_floor_1;
                binding.imageMap.setImageResource(imageId);
                for(Room r : zoneData.getFloors().get(currentFloor).getRooms()){//TODO -> le faire dans le constructeur de Room plus tard
                    HashMap<Room, Integer> nearestRooms = new HashMap<>();
                    for(Room r2 : zoneData.getFloors().get(currentFloor).getRooms()){
                        if(r.getDoors().containsKey(r2.getId())){
                            nearestRooms.put(r2,getDistanceBetweenRoom(r, r2));
                        }
                    }
                    r.setNearestRooms(nearestRooms);
                }
            }
        });

        binding.leftButtonMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentFloor-=1;
                currentFloor=currentFloor%zoneData.getFloors().size();
                binding.zoneDataText.setText(zoneData.getFloors().get(currentFloor).getMapName());
                imageId = getResources().getIdentifier(zoneData.getFloors().get(currentFloor).getMapName(), "drawable", getContext().getPackageName());//R.drawable.t_floor_1;
                binding.imageMap.setImageResource(imageId);
                for(Room r : zoneData.getFloors().get(currentFloor).getRooms()){//TODO -> le faire dans le constructeur de Room plus tard
                    HashMap<Room, Integer> nearestRooms = new HashMap<>();
                    for(Room r2 : zoneData.getFloors().get(currentFloor).getRooms()){
                        if(r.getDoors().containsKey(r2.getId())){
                            nearestRooms.put(r2,getDistanceBetweenRoom(r, r2));
                        }
                    }
                    r.setNearestRooms(nearestRooms);
                }
            }
        });


        //PATH FINDING
        binding.imageMap.setOnTouchListener((v, event) -> {
            if(canDraw){
                canDraw=false;
                float ratio = (float) binding.imageMap.getDrawable().getIntrinsicWidth() /binding.imageMap.getWidth();
                int pictureHeight = (int) (binding.imageMap.getHeight()/ratio);
                float ratioCoordinate = (float) pictureHeight /zoneData.getFloors().get(currentFloor).getImageSize().getY();
                float imageToPxRatio = (float) binding.imageMap.getDrawable().getIntrinsicWidth() /zoneData.getFloors().get(currentFloor).getImageSize().getX();
                float x = event.getX()/ratioCoordinate;
                float y = event.getY() -(((float) binding.imageMap.getHeight()/2) - ((float) pictureHeight /2));
                y/=ratioCoordinate;
                Room roomBegin = null;
                for(Room r : zoneData.getFloors().get(currentFloor).getRooms()){
                    if(r.getC0().getX()<x && r.getC1().getX()>x && r.getC0().getY()<y && r.getC1().getY()>y){
                        roomBegin=r;
                    }
                }
                if(roomBegin!=null){
                    ArrayList<Room> path = getShortestPathFrom(roomBegin);
                    binding.imageMap.drawOnImage(imageToPxRatio,path,imageId);
                }
            }
            canDraw=true;
            return true;
        });
        return binding.getRoot();
    }

    public ArrayList<Room> getShortestPathFrom(Room roomBegin) {
        ArrayList<Room> toReturn = new ArrayList<Room>();
        toReturn.add(roomBegin);
        int poids=0;//TOOD -> on le supprime??? et on met 0 direct dans la fonction?
        toReturn = getPath((ArrayList) toReturn.clone(), roomBegin, poids);
        weigth=1000000;
        return toReturn;
    }

    private ArrayList<Room> getPath(ArrayList<Room> path, Room actualRoom, int poids) {
        HashMap<ArrayList<Room>, Integer> allPaths = new HashMap<>();
        if(actualRoom.getEnd().size()>0){
            if(!path.contains(actualRoom)){
                path.add(actualRoom);
            }
            return path;

        }
        for(Room r : actualRoom.getNearestRooms().keySet()){
            if(!path.contains(r)){
                ArrayList<Room> newArray = (ArrayList<Room>) path.clone();
                newArray.add(r);
                getPath(newArray,r,poids+actualRoom.getNearestRooms().get(r));
                allPaths.put((ArrayList<Room>) newArray.clone(),poids+actualRoom.getNearestRooms().get(r));
            }
        }
        HashMap<ArrayList<Room>, Integer> other = new HashMap<>();
        //TODO traitement de tous les types de paths
        for(ArrayList<Room> rooms : allPaths.keySet()){
            boolean goThroughFire = false;
            for(int i =0;i<rooms.size();i++){
                for(Pair p : zoneData.getFloors().get(currentFloor).getFireDoors()){
                    if(i!=0){
                        if(p.isPartOf(rooms.get(i).getId()) && p.isPartOf(rooms.get(i-1).getId())){
                            goThroughFire = true;
                        }
                    }

                }
                if(zoneData.getFloors().get(currentFloor).getMPEnd().contains(rooms.get(i).getId())){
                    if(!zoneData.getFloors().get(currentFloor).getLPEnd().contains(rooms.get(i).getId())) {
                        if(goThroughFire){
                            other.put(rooms, allPaths.get(rooms) * 500);
                        }else{
                            other.put(rooms, allPaths.get(rooms) * 10);
                        }
                    }
                }
                if(zoneData.getFloors().get(currentFloor).getHPEnd().contains(rooms.get(i).getId())) {
                    if(goThroughFire){
                        other.put(rooms, allPaths.get(rooms) * 50);
                    }else{
                        other.put(rooms, allPaths.get(rooms));
                    }
                }
            }
        }
        for(ArrayList<Room> rooms : other.keySet()){

            if(other.get(rooms)<weigth && !rooms.get(rooms.size()-1).getEnd().isEmpty()){
                    weigth = other.get(rooms);
                    pathRooms = rooms;
            }
        }
        //binding.textMap.setText(String.valueOf(weigth));
        return pathRooms;
    }

    private Integer getDistanceBetweenRoom(Room r, Room r2) {//TODO refaire pour les fire door
        Pair centerR1 = r.getMiddle();
        Pair centerR2 = r2.getMiddle();
        Pair door = r.getDoors().get(r2.getId());
        return getHypothenus(centerR1, door) + getHypothenus(door, centerR2);
    }

    private Integer getHypothenus(Pair c1, Pair c2) {
        int dist1X = c1.getX()-c2.getX();
        int dist1Y = c1.getY()-c2.getY();
        dist1X*=dist1X;
        dist1Y*=dist1Y;
        return (int) Math.sqrt((dist1Y+dist1X));
    }

    private ZoneData extractDatas(int test) {
        ArrayList<Floor> floors = new ArrayList<Floor>();
        try {
            JSONObject js = new JSONObject(loadJson(test));
            JSONObject floor = (JSONObject) js.get("floors");
            floors = getFloors(floor);
        } catch (JSONException | IOException e) {
            throw new RuntimeException(e);
        }
        return new ZoneData("0000","0000", new ArrayList<String>(), new ArrayList<String>(), floors);
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

                floors.add(new Floor(rooms,imageName,imageSize,HPEnd,MPEnd,LPEnd,fireDoors));
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
                Room r = new Room(Integer.parseInt(key),c0,c1,end,doors, new HashMap<Room, Integer>());
                toReturn.add(r);
            }
        }
        return toReturn;
    }

    public String loadJson(int data) throws IOException {
        InputStream stream = getResources().openRawResource(data);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}