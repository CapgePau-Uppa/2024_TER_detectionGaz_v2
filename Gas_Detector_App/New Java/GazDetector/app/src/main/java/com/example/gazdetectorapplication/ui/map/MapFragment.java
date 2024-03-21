package com.example.gazdetectorapplication.ui.map;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.gazdetectorapplication.R;
import com.example.gazdetectorapplication.databinding.FragmentMapBinding;
import com.example.gazdetectorapplication.util.Coordinate;
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
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class MapFragment extends Fragment {
    private FragmentMapBinding binding;
    private boolean canDraw = true;
    private int weigth = 1000000;
    private ZoneData zoneData = null;
    private ArrayList<Room> pathRooms = new ArrayList<>();
    private ArrayList<Room> extractedRooms = new ArrayList<>();
    private int dataId = 0;
    private int imageId = 0;

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentMapBinding.inflate(inflater, container, false);

        //DATA EXTRACTION
        dataId = R.raw.test;
        imageId = R.drawable.test_image;
        extractedRooms = extractDatas(dataId);
        //ArrayList<Room> rooms = extractDatas(dataId);
        for(Room r : extractedRooms){
            HashMap<Room, Integer> nearestRooms = new HashMap<>();
            for(Room r2 : extractedRooms){
                if(r.getDoors().containsKey(r2.getId())){
                    nearestRooms.put(r2,getDistanceBetweenRoom(r, r2));
                }
            }
            r.setNearestRooms(nearestRooms);
        }
        //IMAGE
        binding.imageMap.setImageResource(imageId);

        binding.switchMap.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    dataId = R.raw.cap_gemini;
                    imageId = R.drawable.cg_floor_0;
                }else{
                    dataId = R.raw.test;
                    imageId = R.drawable.test_image;
                }
                binding.imageMap.setImageResource(imageId);
                extractedRooms = extractDatas(dataId);
                for(Room r : extractedRooms){
                    HashMap<Room, Integer> nearestRooms = new HashMap<>();
                    for(Room r2 : extractedRooms){
                        if(r.getDoors().containsKey(r2.getId())){
                            nearestRooms.put(r2,getDistanceBetweenRoom(r, r2));
                        }
                    }
                    r.setNearestRooms(nearestRooms);
                }
            }
        });

        binding.imageMap.setOnTouchListener((v, event) -> {
            if(canDraw){
                canDraw=false;
                float ratio = (float) binding.imageMap.getDrawable().getIntrinsicWidth() /binding.imageMap.getWidth();
                int pictureHeight = (int) (binding.imageMap.getHeight()/ratio);
                float ratioCoordinate = (float) pictureHeight /zoneData.getImageSize().getY();
                float imageToPxRatio = (float) binding.imageMap.getDrawable().getIntrinsicWidth() /zoneData.getImageSize().getX();
                float x = event.getX()/ratioCoordinate;
                float y = event.getY() -(((float) binding.imageMap.getHeight()/2) - ((float) pictureHeight /2));
                y/=ratioCoordinate;
                Room roomBegin = null;
                for(Room r : extractedRooms){
                    if(r.getC0().getX()<x && r.getC1().getX()>x && r.getC0().getY()<y && r.getC1().getY()>y){
                        roomBegin=r;
                    }
                }
                if(roomBegin!=null){
                    ArrayList<Room> path = getShortestPathFrom(roomBegin);
                    binding.imageMap.drawOnImage(imageToPxRatio,path,imageId);
                    binding.textMap.setText(String.valueOf(roomBegin.getId()));
                }
            }
            canDraw=true;
            return true;
        });
        View root = binding.getRoot();
        return root;
    }

    private ArrayList<Room> extractDatas(int test) {
        ArrayList<Room> rooms = new ArrayList<>();
        try {
            JSONObject js = new JSONObject(loadJson(test));
            JSONObject floor = (JSONObject) js.get("floors");
            JSONArray dimension = (JSONArray) js.get("imageSize") ;
            Coordinate zoneDimensation = new Coordinate(Integer.parseInt(dimension.get(0).toString()),Integer.parseInt(dimension.get(1).toString()));
            zoneData = new ZoneData("0","capGemini",new ArrayList<String>(),new ArrayList<String>(),zoneDimensation);
            floor = (JSONObject) floor.get("0");
            rooms = getRoomsJson((JSONObject) floor.get("rooms"));
        } catch (JSONException | IOException e) {
            throw new RuntimeException(e);
        }
        return rooms;
    }


    public ArrayList<Room> getShortestPathFrom(Room roomBegin) {
        ArrayList<Room> toReturn = new ArrayList<Room>();
        toReturn.add(roomBegin);
        int poids=0;
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
        for(ArrayList<Room> rooms : allPaths.keySet()){
            if(allPaths.get(rooms)<weigth && !rooms.get(rooms.size()-1).getEnd().isEmpty()){
                weigth = allPaths.get(rooms);
                pathRooms = rooms;
            }
        }
        return pathRooms;
    }

    private Integer getDistanceBetweenRoom(Room r, Room r2) {
        Coordinate centerR1 = new Coordinate(r.getC0().getX()-r.getC1().getX(),r.getC0().getY()-r.getC1().getY());
        Coordinate centerR2 = new Coordinate(r2.getC0().getX()-r2.getC1().getX(),r2.getC0().getY()-r2.getC1().getY());
        Coordinate door = r.getDoors().get(r2.getId());
        return getHypothenus(centerR1, door) + getHypothenus(door, centerR2);
    }

    private Integer getHypothenus(Coordinate c1, Coordinate c2) {
        int dist1X = c1.getX()-c2.getX();
        int dist1Y = c1.getY()-c2.getY();
        dist1X*=dist1X;
        dist1Y*=dist1Y;
        return (int) Math.sqrt((dist1Y+dist1X));

    }

    private ArrayList<Room> getRoomsJson(JSONObject rooms) throws JSONException {
        ArrayList<Room> toReturn = new ArrayList<>();
        Iterator<?> keys = rooms.keys();
        while(keys.hasNext() ) {
            String key = (String)keys.next();
            if ( rooms.get(key) instanceof JSONObject ) {
                int x = (int) ((JSONObject) rooms.get(key)).getJSONArray("xy1").get(0);
                int y = (int) ((JSONObject) rooms.get(key)).getJSONArray("xy1").get(1);
                Coordinate c0 = new Coordinate(x,y);
                x = (int) ((JSONObject) rooms.get(key)).getJSONArray("xy2").get(0);
                y = (int) ((JSONObject) rooms.get(key)).getJSONArray("xy2").get(1);
                Coordinate c1 = new Coordinate(x,y);
                JSONObject doorsJson = (JSONObject) ((JSONObject) rooms.get(key)).get("doors");
                HashMap<Integer, Coordinate> doors = new HashMap<>();
                Iterator<?> keysDoor = doorsJson.keys();
                while(keysDoor.hasNext()){
                    String keyDoor = (String) keysDoor.next();
                    if(doorsJson.get(keyDoor) instanceof JSONArray){
                        Coordinate c = new Coordinate(Integer.parseInt(doorsJson.getJSONArray(keyDoor).get(0).toString()),
                                Integer.parseInt(doorsJson.getJSONArray(keyDoor).get(1).toString()));
                        doors.put(Integer.parseInt(keyDoor),c);
                    }
                }
                JSONObject ends = (JSONObject) ((JSONObject) rooms.get(key)).get("end");
                ArrayList<Coordinate> end = new ArrayList<>();
                Iterator<?> endIterator = ends.keys();
                while(endIterator.hasNext()){
                    String keyEnd = (String) endIterator.next();
                    if(ends.get(keyEnd)instanceof JSONArray){
                        Coordinate c = new Coordinate(Integer.parseInt(ends.getJSONArray(keyEnd).get(0).toString()),
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

//    private int[][] getArray(JSONArray array) throws JSONException {
//        int[][] toReturn = new int[array.length()][array.length()];
//        for(int i = 0; i<array.length();i++){
//            for(int j = 0; j<array.getJSONArray(i).length();j++){
//                toReturn[i][j] = (int) array.getJSONArray(i).get(j);
//            }
//        }
//        return toReturn;
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}