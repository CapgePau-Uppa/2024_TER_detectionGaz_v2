package com.example.gazdetectorapplication.ui.map;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.gazdetectorapplication.R;
import com.example.gazdetectorapplication.databinding.FragmentMapBinding;
import com.example.gazdetectorapplication.jsongestion.JsonExtractor;
import com.example.gazdetectorapplication.pathfinding.DFS;
import com.example.gazdetectorapplication.pathfinding.Pair;
import com.example.gazdetectorapplication.pathfinding.Room;
import com.example.gazdetectorapplication.pathfinding.ZoneData;

import java.util.ArrayList;
import java.util.HashMap;

public class MapFragment extends Fragment {
    private FragmentMapBinding binding;
    private boolean canDraw = true;
    private boolean drawAlert = false;
    private ZoneData zoneData = null;
    private int currentFloor = 0;
    private int imageId = 0;
    private int alertLevel = 1;
    private int alertType = 1;

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //RESOURCES
        binding = FragmentMapBinding.inflate(inflater, container, false);
        imageId = R.drawable.t_floor_0;//TODO -> avoir un fichier de config avec le nom des floors
        binding.imageMap.setImageResource(imageId);
        binding.buttonObstacleMap.setBackgroundColor(Color.GRAY);
        binding.buttonHelpMap.setBackgroundColor(Color.GRAY);
        binding.buttonPathMap.setBackgroundColor(Color.CYAN);
        binding.alertDegreeLayout.setVisibility(View.GONE);
        binding.buttonAlertDegree1.setBackgroundColor(Color.YELLOW);



        //DATA EXTRACTION
        JsonExtractor jsonExtractor = new JsonExtractor(getResources());
        zoneData = jsonExtractor.extractDatas(R.raw.zonedata);
        prepareContent();


        //OnClick Functions boutons de niveau d'alerte (jaune/orange/rouge)
        binding.buttonAlertDegree1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertLevel = 1;
            }
        });
        binding.buttonAlertDegree2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertLevel = 2;
            }
        });
        binding.buttonAlertDegree3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertLevel = 3;
            }
        });

        //onClick chemin le plus court vers la sortie
        binding.buttonPathMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canDraw=true;
                drawAlert=false;
                binding.buttonPathMap.setBackgroundColor(Color.CYAN);
                binding.buttonObstacleMap.setBackgroundColor(Color.GRAY);
                binding.buttonHelpMap.setBackgroundColor(Color.GRAY);
                binding.textMap.setText("Cliquer sur votre position");
                binding.alertDegreeLayout.setVisibility(View.GONE);
                alertType = 3;
            }
        });

        //onClick ajouter un obstacle
        binding.buttonObstacleMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canDraw=false;
                drawAlert=true;
                binding.buttonPathMap.setBackgroundColor(Color.GRAY);
                binding.buttonObstacleMap.setBackgroundColor(Color.CYAN);
                binding.buttonHelpMap.setBackgroundColor(Color.GRAY);
                binding.textMap.setText("signaler un obstacle");
                binding.alertDegreeLayout.setVisibility(View.VISIBLE);
                alertType = 1;
            }
        });

        //onClick ajouter une personne en danger
        binding.buttonHelpMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canDraw=false;
                drawAlert=true;
                binding.buttonPathMap.setBackgroundColor(Color.GRAY);
                binding.buttonObstacleMap.setBackgroundColor(Color.GRAY);
                binding.buttonHelpMap.setBackgroundColor(Color.CYAN);
                binding.textMap.setText("signaler une personne en danger");
                binding.alertDegreeLayout.setVisibility(View.VISIBLE);
                alertType = 2;
            }
        });

        //onclick changer d'étage droite
        binding.rightButtonMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentFloor+=1;
                currentFloor=currentFloor%zoneData.getFloors().size();
                imageId = getResources().getIdentifier(zoneData.getFloors().get(currentFloor).getMapName(), "drawable", getContext().getPackageName());//R.drawable.t_floor_1;
                prepareContent();
            }
        });

        //onclick changer d'étage gauche
        binding.leftButtonMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentFloor-=1;
                if(currentFloor<0){
                    currentFloor=zoneData.getFloors().size()-1;
                }
                imageId = getResources().getIdentifier(zoneData.getFloors().get(currentFloor).getMapName(), "drawable", getContext().getPackageName());
                prepareContent();
            }
        });

        //onClick sur image pour soit ajouter une personne en danger, ajouter un obstacle ou trouver le chemin le plus court
        binding.imageMap.setOnTouchListener((v, event) -> {
            float ratioX = (float) zoneData.getFloors().get(currentFloor).getImageSize().getX() /binding.imageMap.getWidth();
            float ratioY = (float) binding.imageMap.getHeight() /zoneData.getFloors().get(currentFloor).getImageSize().getY();
            float imageToPxRatio = (float) binding.imageMap.getDrawable().getIntrinsicWidth() /zoneData.getFloors().get(currentFloor).getImageSize().getX();
            float x = event.getX()*ratioX;
            float y = event.getY()/ratioY;
            Room roomTouched = null;
            for(Room r : zoneData.getFloors().get(currentFloor).getRooms()){
                if(r.getC0().getX()<x && r.getC1().getX()>x && r.getC0().getY()<y && r.getC1().getY()>y){
                    roomTouched =r;
                }
            }
            if(canDraw){
                canDraw=false;
                if(roomTouched !=null){
                    ArrayList<Room> path = new ArrayList<Room>();
                    path.add(roomTouched);
                    DFS dfs = new DFS(10000000,zoneData, currentFloor);
                    binding.imageMap.drawOnImage(imageToPxRatio,dfs.getPath(path, roomTouched, 0),imageId, zoneData.getFloors().get(currentFloor).getRooms());
                }
                canDraw=true;
            }
            if(drawAlert){
                drawAlert = false;
                if(roomTouched !=null){
                    if(alertType==1){
                        for(Room r : zoneData.getFloors().get(currentFloor).getRooms()){
                            if(r.getId()== roomTouched.getId()){
                                r.setObstacleLevel(alertLevel);
                                binding.textMap.setText("obstacle : "+alertLevel+"/3 "+r.getId());
                            }
                        }
                        binding.imageMap.drawAlert(zoneData.getFloors().get(currentFloor).getRooms(),imageToPxRatio,imageId);
                    }else{
                        binding.textMap.setText("Personne en danger : "+alertLevel+"/3");
                    }
                }
                drawAlert = true;
            }
            return true;
        });
        return binding.getRoot();
    }

    private void prepareContent() {
        binding.zoneDataText.setText("Étage n°"+currentFloor);
        binding.imageMap.setImageResource(imageId);
        for(Room r : zoneData.getFloors().get(currentFloor).getRooms()){
            HashMap<Room, Integer> nearestRooms = new HashMap<>();
            for(Room r2 : zoneData.getFloors().get(currentFloor).getRooms()){
                if(r.getDoors().containsKey(r2.getId())){
                    nearestRooms.put(r2,getDistanceBetweenRoom(r, r2));
                }
            }
            r.setNearestRooms(nearestRooms);
        }
    }


    public Integer getDistanceBetweenRoom(Room r, Room r2) {//TODO refaire pour faire de door à door plutôt que milieu à door
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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}