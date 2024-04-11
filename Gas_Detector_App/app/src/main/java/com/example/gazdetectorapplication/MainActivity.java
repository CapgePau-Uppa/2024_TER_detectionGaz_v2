package com.example.gazdetectorapplication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.gazdetectorapplication.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setContentView(binding.getRoot());
        replaceFragment(new PlugInFragment());

        binding.navView.setOnItemSelectedListener(item -> {
            switch(item.getItemId()){
                case 1000001:
                    replaceFragment(new PlugInFragment());
                    break;
                case 1000003:
                    replaceFragment(new MapFragment());
                    break;
                case 1000006:
                    replaceFragment(new UserFragment());
                    break;
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame_layout,fragment);
        ft.commit();

    }

}
