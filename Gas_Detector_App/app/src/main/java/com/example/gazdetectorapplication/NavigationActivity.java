package com.example.gazdetectorapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.gazdetectorapplication.databinding.ActivityNavigationBinding;
import com.example.gazdetectorapplication.datamanagement.Account;
import com.example.gazdetectorapplication.ui.map.MapFragment;
import com.example.gazdetectorapplication.ui.plug_in.PlugInFragment;
import com.example.gazdetectorapplication.ui.user.UserFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NavigationActivity extends AppCompatActivity {

    private ActivityNavigationBinding binding;

    static String urlCheckAlert = "http://192.168.1.85:3000/checkCurrentAlert";
    static boolean alertOccuring = false;
    MapFragment mapFragment = new MapFragment();
    PlugInFragment plugInFragment = new PlugInFragment();
    UserFragment userFragment = new UserFragment();
    static Account currentAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentAccount = new Account();
        setContentView(R.layout.activity_navigation);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.navigation_plug_in);
        setCurrentAccount(getIntent().getExtras());
        checkAlert();
        navView.setOnItemSelectedListener(item -> {
            checkAlert();
            if (item.getItemId() == R.id.navigation_plug_in) {
                getSupportFragmentManager()
                        .beginTransaction().replace(R.id.nav_host_fragment_activity_main, plugInFragment)
                        .commit();
                return true;
            }
            if (item.getItemId() == R.id.navigation_map) {
                getSupportFragmentManager()
                        .beginTransaction().replace(R.id.nav_host_fragment_activity_main, mapFragment)
                        .commit();
                return true;
            }
            if (item.getItemId() == R.id.navigation_user) {
                getSupportFragmentManager()
                        .beginTransaction().replace(R.id.nav_host_fragment_activity_main, userFragment)
                        .commit();

                return true;
            }
            return false;
        });


    }



    public static void checkAlert() {
        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("zoneId", currentAccount.getZoneId())
                .build();
        Request request = new Request.Builder()
                .url(urlCheckAlert)
                .post(formBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {}

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    alertOccuring = response.code() == 200;
                }
            }

        });
    }

    public static boolean isAlertOccuring(){
        return alertOccuring;
    }




    private void setCurrentAccount(Bundle extras) {
        currentAccount.setName(extras.getString("name"));
        currentAccount.setSurname(extras.getString("surname"));
        currentAccount.setAdmin(extras.getBoolean("isAdmin"));
        currentAccount.setId(extras.getString("id"));
        currentAccount.setZoneId(extras.getString("zone"));
        SharedPreferences sharedPreferences = getSharedPreferences("userPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor;
        if(!sharedPreferences.contains("isPMR")){
            editor = sharedPreferences.edit();
            editor.putBoolean("isPMR", false);
            editor.apply();
        }else{
            currentAccount.setPMR(sharedPreferences.getBoolean("isPMR", false));
        }
        if(!sharedPreferences.contains("isCB")){
            editor = sharedPreferences.edit();
            editor.putBoolean("isCB", false);
            editor.apply();
        }else{
            currentAccount.setColorBlind(sharedPreferences.getBoolean("isCB", false));
        }
    }

    public static void setCurrentAccount(Account c){
        currentAccount = c;
    }

    public static Account getCurrentAccount(){
        return currentAccount;
    }


}