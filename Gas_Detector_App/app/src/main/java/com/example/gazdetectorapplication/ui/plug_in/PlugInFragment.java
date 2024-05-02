package com.example.gazdetectorapplication.ui.plug_in;
import java.util.List;
import okhttp3.*;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.io.IOException;
import android.widget.Button;

import com.example.gazdetectorapplication.NavigationActivity;
import com.example.gazdetectorapplication.databinding.DialogContentBinding;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.gazdetectorapplication.databinding.FragmentPlugInBinding;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import java.util.ArrayList;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;



public class PlugInFragment extends Fragment {

    private FragmentPlugInBinding binding;
    Handler taskHandler = new Handler();
    String url = "http://192.168.43.172:3000/addAlert";
    LineDataSet ld1, ld2, ld3;
    ArrayList<ILineDataSet> datasets = new ArrayList<ILineDataSet>();
    float num = 0;
    Button alertButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPlugInBinding.inflate(inflater, container, false);
        binding.gasChart.clear();
        num = 0;
        Thread.currentThread().interrupt();
        View root = binding.getRoot();
        LineChart chart = binding.gasChart;
        chart.getAxisRight().setDrawLabels(true);
        YAxis yAxis = chart.getAxisLeft();
        yAxis.setAxisMaximum(100f);
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisLineWidth(3f);
        YAxis yAxis2 = chart.getAxisRight();
        yAxis2.setEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        ld1 = new LineDataSet(new ArrayList<Entry>(), "Co");
        ld2 = new LineDataSet(new ArrayList<Entry>(), "LPG");
        ld3 = new LineDataSet(new ArrayList<Entry>(), "Smoke");
        if(NavigationActivity.getCurrentAccount().isColorBlind()){
            ld1.setColor(Color.BLACK);
            ld2.setColor(Color.CYAN);
            ld3.setColor(Color.rgb(255, 165, 0));
        }else{
            ld1.setColor(Color.CYAN);
            ld2.setColor(Color.RED);
            ld3.setColor(Color.GREEN);
        }
        datasets.add(ld1);
        datasets.add(ld2);
        datasets.add(ld3);
        LineData d = new LineData(datasets);
        binding.gasChart.setData(d);

        if(NavigationActivity.isAlertOccuring()){
            binding.plugInHeader.setBackgroundColor(Color.RED);
            binding.plugInHeader.setPadding(15,35,0,35);
            binding.plugInHeader.setText("Alerte Reçue, Evacuer la zone");
            binding.plugInHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new androidx.appcompat.app.AlertDialog.Builder( getContext() )
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
            binding.plugInHeader.setBackgroundColor(Color.TRANSPARENT);
            binding.plugInHeader.setPadding(15,15,0,15);
            binding.plugInHeader.setText("");
        }

        taskHandler.postDelayed(getDataValue, 0);
        alertButton = binding.alertButton;
        alertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup();
            }
        });
        return root;
    }

    private void modifyDataSet() {
        ld1.addEntry(new Entry(num, (float) (Math.random() * 100)));
        ld2.addEntry(new Entry(num, (float) (Math.random() * 100)));
        ld3.addEntry(new Entry(num, 9));
        binding.gasChart.setVisibleXRangeMinimum(num - 1);
        binding.gasChart.setVisibleXRangeMaximum(num - 1);
        num++;
        datasets.clear();
        binding.gasChart.getLineData().getDataSets().add(ld1);
        datasets.add(ld2);
        datasets.add(ld3);
        LineData d2 = new LineData(datasets);
        binding.gasChart.setData(d2);
    }

    private final Runnable getDataValue = new Runnable() {
        @Override
        public void run() {
            modifyDataSet();
            binding.gasChart.notifyDataSetChanged();
            binding.gasChart.invalidate();
            taskHandler.postDelayed(getDataValue, 10000);
        }
    };

    private void sendPostRequest(String selectedGas, String gasValue, String zoneId) {

        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("gazValue", gasValue)
                .add("idZone", zoneId)
                .add("typeAlert", selectedGas)
                .add("alerter", NavigationActivity.getCurrentAccount().getId())
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // GÃ©rer la rÃ©ponse du serveur si nÃ©cessaire
                }
            }
        });
    }

    private void showPopup() {

        DialogContentBinding dialogBinding = DialogContentBinding.inflate(LayoutInflater.from(getContext()));
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogBinding.getRoot());
        builder.setTitle("Alerter:");

        List<String> itemsToShow = new ArrayList<>();
        itemsToShow.add("Au sujet d'une fuite de gaz:");
        String[] gasLeakSubjects = {"CO", "LPG", "Smoke"};

        for (String subject : gasLeakSubjects) {
            switch (subject) {
                case "CO":
                    if (ld1.getEntryForIndex(ld1.getEntryCount() - 1).getY() >= 10) {
                        itemsToShow.add(subject);
                    }
                    break;
                case "LPG":
                    if (ld2.getEntryForIndex(ld2.getEntryCount() - 1).getY() >= 10) {
                        itemsToShow.add(subject);
                    }
                    break;
                case "Smoke":
                    if (ld3.getEntryForIndex(ld3.getEntryCount() - 1).getY() >= 10) {
                        itemsToShow.add(subject);
                    }
                    break;
            }
        }

        ArrayAdapter<String> GazAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, itemsToShow);
        dialogBinding.spinnerGasLeak.setAdapter(GazAdapter);
        ArrayList<String> arrayZoneIds = new ArrayList<>();
        arrayZoneIds.add("Prévenir la zone :");
        arrayZoneIds.add(NavigationActivity.getCurrentAccount().getZoneId());
        ArrayAdapter<String> idsZoneAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, arrayZoneIds);
        dialogBinding.spinnerZoneId.setAdapter(idsZoneAdapter);
        dialogBinding.spinnerGasLeak.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedGas = itemsToShow.get(position);
                switch (selectedGas) {
                    case "CO":
                        dialogBinding.editTextgasValue.setText(String.valueOf(ld1.getEntryForIndex(ld1.getEntryCount() - 1).getY()));
                        break;
                    case "LPG":
                        dialogBinding.editTextgasValue.setText(String.valueOf(ld2.getEntryForIndex(ld2.getEntryCount() - 1).getY()));
                        break;
                    case "Smoke":
                        dialogBinding.editTextgasValue.setText(String.valueOf(ld3.getEntryForIndex(ld3.getEntryCount() - 1).getY()));
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Ne rien faire si rien n'est sÃ©lectionnÃ©
            }
        });


        //valider
        builder.setPositiveButton("Valider", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedGas = dialogBinding.spinnerGasLeak.getSelectedItem().toString();
                String gasValue = dialogBinding.editTextgasValue.getText().toString();
                String zoneValue = dialogBinding.spinnerZoneId.getSelectedItem().toString();
                if(selectedGas!="Au sujet d'une fuite de gaz:" && zoneValue !="Prévenir la zone :" ){
                    sendPostRequest(selectedGas, gasValue, zoneValue);
                    if(dialogBinding.checkBoxSamu.isChecked()){
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:15"));
                        startActivity(intent);
                    }
                    if(dialogBinding.checkBoxPompier.isChecked()){
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:18"));
                        startActivity(intent);
                    }
                }


            }
        });

        //annuler
        builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        taskHandler.removeCallbacks(getDataValue);

    }
}