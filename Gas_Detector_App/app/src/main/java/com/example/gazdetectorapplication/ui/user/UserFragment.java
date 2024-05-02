package com.example.gazdetectorapplication.ui.user;

import static android.content.Context.MODE_PRIVATE;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.gazdetectorapplication.NavigationActivity;
import com.example.gazdetectorapplication.databinding.FragmentUserBinding;
import com.example.gazdetectorapplication.datamanagement.Account;

public class UserFragment extends Fragment {

    private FragmentUserBinding binding;
    private Account currentAccount;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUserBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        currentAccount = NavigationActivity.getCurrentAccount();
        binding.nameText.setText("Prénom : "+currentAccount.getName());
        binding.surnameText.setText("Nom : "+currentAccount.getSurname());
        binding.userPMRCheckbox.setChecked(currentAccount.isPMR());
        binding.userCBCheckbox.setChecked(currentAccount.isColorBlind());

        binding.userPMRCheckbox.setOnClickListener(v -> {
            binding.userButtonSaveDatas.setVisibility(View.VISIBLE);
        });

        binding.userCBCheckbox.setOnClickListener(v -> {
            binding.userButtonSaveDatas.setVisibility(View.VISIBLE);
        });

        binding.userButtonSaveDatas.setOnClickListener(v->{
            binding.feedbackSaveDatas.setVisibility(View.VISIBLE);
            SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("userPreferences", MODE_PRIVATE);
            SharedPreferences.Editor editor;
            editor = sharedPreferences.edit();
            editor.putBoolean("isPMR", binding.userPMRCheckbox.isChecked());
            editor.putBoolean("isCB", binding.userCBCheckbox.isChecked());
            editor.apply();
            currentAccount.setPMR(binding.userPMRCheckbox.isChecked());
            currentAccount.setColorBlind(binding.userCBCheckbox.isChecked());

            NavigationActivity.setCurrentAccount(currentAccount);
        });

        if(NavigationActivity.isAlertOccuring()){
            binding.userHeader.setBackgroundColor(Color.RED);
            binding.userHeader.setPadding(15,35,0,35);
            binding.userHeader.setText("Alerte Reçue, Evacuer la zone");
            binding.userHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder( getContext() )
                            .setTitle("Consignes d'Evacuation")
                            .setMessage("\n- Dirigez vous vers la sortie la plus proche" +"\n\n"+
                                    "- Aidez-vous de l'application pour vous tenir informé" +"\n\n"+
                                    "- Signalez tout individu inconscient" +"\n\n"+
                                    "- Signalez tout obstacle\n")
                            .setPositiveButton("Compris", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            } )
                            .show();
                }
            });
        }else{
            binding.userHeader.setBackgroundColor(Color.TRANSPARENT);
            binding.userHeader.setPadding(15,15,0,15);
            binding.userHeader.setText("");

        }

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}