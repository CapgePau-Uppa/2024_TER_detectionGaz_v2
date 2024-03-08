package com.example.gazdetectorapplication.ui.map;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.gazdetectorapplication.R;
import com.example.gazdetectorapplication.databinding.FragmentMapBinding;
import com.example.gazdetectorapplication.util.Coordinate;
import com.example.gazdetectorapplication.util.Dijkstra;
import com.example.gazdetectorapplication.util.Room;

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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class MapFragment extends Fragment {
    private JSONObject js;
    private FragmentMapBinding binding;
    private ArrayList<Room> rooms = new ArrayList<Room>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentMapBinding.inflate(inflater, container, false);
        //DATA PARSING
        try {
            js = new JSONObject(loadJson());
            JSONObject floor = (JSONObject) js.get("floors");
            floor = (JSONObject) floor.get("0");
            JSONArray array = (JSONArray) floor.get("dijkstraArray");
            int[][] dijkstraArray = getArray(array);
            Dijkstra d = new Dijkstra();
            LinkedList<Integer> path = d.dijkstra(dijkstraArray,28,14);
            binding.textMap.setText(path.toString());
            rooms = getRoom((JSONObject) floor.get("rooms"));
        } catch (JSONException | IOException e) {
            throw new RuntimeException(e);
        }
        binding.imageMap.setImageResource(R.drawable.floor_0);
        Bitmap bm=((BitmapDrawable)binding.imageMap.getDrawable()).getBitmap();
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setStrokeWidth(10);
        p.setColor(Color.GREEN);
        Bitmap bm2 = Bitmap.createBitmap(2844, 1077, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bm2);
        c.drawBitmap(bm, 0,0,null);
        c.drawLine(0,0,50,50,p);
        binding.imageMap.setImageDrawable(new BitmapDrawable(getResources(),bm2));
        //binding.textMap.setText(String.valueOf(rooms.size()));



        View root = binding.getRoot();
        return root;
    }

    private ArrayList<Room> getRoom(JSONObject rooms) throws JSONException {
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
                Room r = new Room(Integer.parseInt(key),c0,c1,new ArrayList<Coordinate>(),doors);
                toReturn.add(r);
            }
        }
        return toReturn;
    }

    private int[][] getArray(JSONArray array) throws JSONException {
        int toReturn[][] = new int[array.length()][array.length()];
        for(int i = 0; i<array.length();i++){
            for(int j = 0; j<array.getJSONArray(i).length();j++){
                toReturn[i][j] = (int) array.getJSONArray(i).get(j);
            }
        }
        return toReturn;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public String loadJson() throws IOException {
        InputStream stream = getResources().openRawResource(R.raw.data);
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
}