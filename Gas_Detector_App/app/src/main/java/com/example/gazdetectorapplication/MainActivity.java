package com.example.gazdetectorapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.example.gazdetectorapplication.datamanagement.Account;
import com.example.gazdetectorapplication.ui.map.MapFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.gazdetectorapplication.databinding.ActivityMainBinding;

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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    String url = "http://192.168.1.85:3000/getPerson";
    String port = ":3000";
    private static Account currentAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        binding.userButtonConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = binding.userUserEditText.getText().toString();
                String password = binding.userPasswordEditText.getText().toString();
                sendPostRequest(username, password);
            }
        });
        setContentView(binding.getRoot());


    }



    private void sendPostRequest(String surname, String password) {
        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("surname", surname)
                .add("password", password)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String t = e.getMessage();

            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    if(response.code()!=204){
                        try {
                            JSONObject obj = new JSONObject(response.body().string());
                            JSONArray arr = obj.getJSONArray("array");
                            currentAccount = new Account(arr.getJSONObject(0));
                            Intent intentToApp = new Intent(MainActivity.this, NavigationActivity.class);
                            intentToApp.putExtra("name",currentAccount.getName());
                            intentToApp.putExtra("surname",currentAccount.getSurname());
                            intentToApp.putExtra("isAdmin",currentAccount.isAdmin());
                            intentToApp.putExtra("zone",currentAccount.getZoneId());
                            intentToApp.putExtra("id", currentAccount.getId());
                            startActivity(intentToApp);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }else{
                        binding.userFeedbackText.setText("Identifiants invalides");
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }



}