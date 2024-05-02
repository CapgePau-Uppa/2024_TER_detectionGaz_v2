package com.example.gazdetectorapplication.ui.map;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.gazdetectorapplication.MainActivity;
import com.example.gazdetectorapplication.NavigationActivity;
import com.example.gazdetectorapplication.R;
import com.example.gazdetectorapplication.databinding.FragmentMapBinding;
import com.example.gazdetectorapplication.datamanagement.JsonExtractor;
import com.example.gazdetectorapplication.pathfinding.DFS;
import com.example.gazdetectorapplication.pathfinding.Pair;
import com.example.gazdetectorapplication.pathfinding.Room;
import com.example.gazdetectorapplication.pathfinding.ZoneData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MapFragment extends Fragment {
    private String urlFinishAlert = "http://192.168.1.85:3000/finishAlert";
    private String urlSendFPerson = "http://192.168.1.85:3000/sendFPerson";
    private String urlSendObstacle = "http://192.168.1.85:3000/sendObstacle";
    private String urlGetObstacles = "http://192.168.1.85:3000/getObstacles";
    private String urlGetFPersons = "http://192.168.1.85:3000/getFPersons";


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

        //DATA EXTRACTION
        JsonExtractor jsonExtractor = new JsonExtractor(getResources());
        zoneData = jsonExtractor.extractDatas(R.raw.zonedata);


        //RESOURCES
        binding = FragmentMapBinding.inflate(inflater, container, false);
        imageId = getResources().getIdentifier(zoneData.getFloors().get(currentFloor).getMapName(), "drawable", getContext().getPackageName());//TODO -> avoir un fichier de config avec le nom des floors
        binding.buttonObstacleMap.setBackgroundColor(Color.GRAY);
        binding.buttonHelpMap.setBackgroundColor(Color.GRAY);
        binding.buttonPathMap.setBackgroundColor(Color.CYAN);
        binding.alertDegreeLayout.setVisibility(View.GONE);
        if(NavigationActivity.getCurrentAccount().isColorBlind()){
            binding.buttonAlertDegree1.setBackgroundColor(Color.rgb(109,182,255));
            binding.buttonAlertDegree2.setBackgroundColor(Color.rgb(0, 109, 219));
            binding.buttonAlertDegree3.setBackgroundColor(Color.rgb(0,73,73));
        }else{
            binding.buttonAlertDegree1.setBackgroundColor(Color.YELLOW);
            binding.buttonAlertDegree2.setBackgroundColor(Color.rgb(255, 165, 0));
            binding.buttonAlertDegree3.setBackgroundColor(Color.RED);

        }
        prepareContent();

        if(!NavigationActivity.getCurrentAccount().isAdmin() || !NavigationActivity.isAlertOccuring()){
            binding.stopAlertButton.setVisibility(View.INVISIBLE);
        }else{

            binding.stopAlertButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Voulez-vous supprimer l'alerte en cours?")
                            .setPositiveButton("Valider", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    OkHttpClient client = new OkHttpClient();
                                    RequestBody formBody = new FormBody.Builder()
                                            .add("zoneId", NavigationActivity.getCurrentAccount().getZoneId())
                                            .build();
                                    Request request = new Request.Builder()
                                            .url(urlFinishAlert)
                                            .post(formBody)
                                            .build();
                                    client.newCall(request).enqueue(new Callback() {
                                        @Override
                                        public void onFailure(Call call, IOException e) {}

                                        @Override
                                        public void onResponse(Call call, Response response) throws IOException {}

                                    });
                                    binding.stopAlertButton.setVisibility(View.INVISIBLE);
                                    binding.mapHeader.setBackgroundColor(Color.TRANSPARENT);
                                    binding.mapHeader.setPadding(15,15,0,15);
                                    binding.mapHeader.setText("");
                                    //RESET zoneData
                                }

                            })
                            .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();

                }

            });
        }


        if(NavigationActivity.isAlertOccuring()){
            binding.mapHeader.setBackgroundColor(Color.RED);
            binding.mapHeader.setPadding(15,35,0,35);
            binding.mapHeader.setText("Alerte Reçue, Evacuer la zone");
            binding.mapHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder( getContext() )
                            .setTitle("Consignes d'Evacuation")
                            .setMessage("\n- Dirigez vous vers la sortie la plus proche" +"\n\n"+
                                    "- Aidez-vous de l'application pour vous tenir informé" +"\n\n"+
                                    "- Signalez tout individu inconscient" +"\n\n"+
                                    "- Signalez tout obstacle\n")
                            .setPositiveButton("Compris", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {}
                            } )
                            .show();
                }
            });
        }else{
            binding.mapHeader.setBackgroundColor(Color.TRANSPARENT);
            binding.mapHeader.setPadding(15,15,0,15);
            binding.mapHeader.setText("");
        }




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
                binding.textMap.setText("Trouver la sortie la plus proche");
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
                float imageToPxRatio = (float) binding.imageMap.getDrawable().getIntrinsicWidth() /zoneData.getFloors().get(currentFloor).getImageSize().getX();
                binding.imageMap.drawAlert(zoneData.getFloors().get(currentFloor).getRooms(),imageToPxRatio,imageId, NavigationActivity.getCurrentAccount().isColorBlind());
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
                float imageToPxRatio = (float) binding.imageMap.getDrawable().getIntrinsicWidth() /zoneData.getFloors().get(currentFloor).getImageSize().getX();
                binding.imageMap.drawAlert(zoneData.getFloors().get(currentFloor).getRooms(),imageToPxRatio,imageId, NavigationActivity.getCurrentAccount().isColorBlind());

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
                    if(NavigationActivity.getCurrentAccount().isPMR()){
                        binding.imageMap.drawOnImage(imageToPxRatio,dfs.getPMRPath(path, roomTouched, 0),imageId, zoneData.getFloors().get(currentFloor).getRooms());
                    }else{
                        binding.imageMap.drawOnImage(imageToPxRatio,dfs.getPath(path, roomTouched, 0),imageId, zoneData.getFloors().get(currentFloor).getRooms());

                    }
                }
                canDraw=true;
            }
            if(drawAlert){
                drawAlert = false;
                if(roomTouched !=null){
                    if(alertType==1){
                        for(Room r : zoneData.getFloors().get(currentFloor).getRooms()){
                            if(r.getId()== roomTouched.getId()){
                                if(event.getAction()==MotionEvent.ACTION_UP){
                                    r.setObstacleLevel(alertLevel);
                                    binding.textMap.setText("obstacle : "+alertLevel+"/3 ");
                                    sendObstacle(roomTouched);
                                }
                            }
                        }
                        binding.imageMap.drawAlert(zoneData.getFloors().get(currentFloor).getRooms(),imageToPxRatio,imageId, NavigationActivity.getCurrentAccount().isColorBlind());
                    }else{
                        if(event.getAction()== MotionEvent.ACTION_UP){
                            binding.textMap.setText("Personne en danger : "+alertLevel+"/3");
                            sendFaintedPerson(roomTouched);
                            for(Room r : zoneData.getFloors().get(currentFloor).getRooms()){
                                if(r.getId()== roomTouched.getId()){
                                    r.setFaintedPerson(true);
                                }
                            }
                            binding.imageMap.drawAlert(zoneData.getFloors().get(currentFloor).getRooms(),imageToPxRatio,imageId, NavigationActivity.getCurrentAccount().isColorBlind());
                        }
                    }
                }
                drawAlert = true;
            }
            return true;
        });
        return binding.getRoot();
    }

    private void sendObstacle(Room roomTouched) {
        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("zoneId", NavigationActivity.getCurrentAccount().getZoneId())
                .add("floor", String.valueOf(currentFloor))
                .add("roomId", String.valueOf(roomTouched.getId()))
                .add("obstacleLevel", String.valueOf(alertLevel))
                .build();
        Request request = new Request.Builder()
                .url(urlSendObstacle)
                .post(formBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {  }
            @Override
            public void onResponse(Call call, Response response) throws IOException {}

        });
        new AlertDialog.Builder(getContext())
                .setTitle("Message Envoyé")
                .setMessage("Votre message a été envoyé")
                .show();
    }

    private void sendFaintedPerson(Room roomTouched) {
        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("zoneId", NavigationActivity.getCurrentAccount().getZoneId())
                .add("floor", String.valueOf(currentFloor))
                .add("roomId", String.valueOf(roomTouched.getId()))
                .add("isFinished", String.valueOf(false))
                .build();
        Request request = new Request.Builder()
                .url(urlSendFPerson)
                .post(formBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {  }
            @Override
            public void onResponse(Call call, Response response) throws IOException {}

        });
        new AlertDialog.Builder(getContext())
                .setTitle("Message Envoyé")
                .setMessage("Votre message a été envoyé")
                .show();
    }

    private void prepareContent() {
        if(currentFloor==0){
            binding.zoneDataText.setText("Red-de-Chaussé");
        }else{
            binding.zoneDataText.setText("Étage n°"+currentFloor);
        }
        binding.imageMap.setImageResource(imageId);
        ArrayList<Integer> PMRRooms = new ArrayList<Integer>();
        for(Room r : zoneData.getFloors().get(currentFloor).getRooms()){
            if(r.isPMRRoom()){
                PMRRooms.add(r.getId());
            }
            HashMap<Room, Integer> nearestRooms = new HashMap<>();
            for(Room r2 : zoneData.getFloors().get(currentFloor).getRooms()){
                if(r.getDoors().containsKey(r2.getId())){
                    nearestRooms.put(r2,getDistanceBetweenRoom(r, r2));
                }
            }
            r.setNearestRooms(nearestRooms);
        }
        zoneData.getFloors().get(currentFloor).setPMRRoom(PMRRooms);
        getServerDatas();
    }

    private void getServerDatas() {
        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("zoneId", NavigationActivity.getCurrentAccount().getZoneId())
                .build();
        Request request = new Request.Builder()
                .url(urlGetFPersons)
                .post(formBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {  }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.code()==200){
                    JSONObject obj = null;
                    ArrayList<String> idRooms = new ArrayList<>();
                    try {
                        obj = new JSONObject(response.body().string());
                        JSONArray arr = obj.getJSONArray("array");
                        for(int i = 0; i<arr.length();i++){
                            JSONObject o = (JSONObject) arr.get(i);
                            int floor = o.getInt("floor");
                            int roomId = o.getInt("roomId");
                            for(Room r : zoneData.getFloors().get(floor).getRooms()){
                                if(r.getId()==roomId){
                                    r.setFaintedPerson(true);
                                }
                            }
                        }

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

        });
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