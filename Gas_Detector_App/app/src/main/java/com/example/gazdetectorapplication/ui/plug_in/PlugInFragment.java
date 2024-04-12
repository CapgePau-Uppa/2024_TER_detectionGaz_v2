package com.example.gazdetectorapplication.ui.plug_in;
import java.util.List;
import okhttp3.*;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.io.IOException;
import android.widget.Button;
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
        ld1.setColor(Color.CYAN);
        ld2.setColor(Color.RED);
        ld3.setColor(Color.GREEN);
        datasets.add(ld1);
        datasets.add(ld2);
        datasets.add(ld3);
        LineData d = new LineData(datasets);
        binding.gasChart.setData(d);
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

    private void sendPostRequest(String selectedGas, String gasValue) {
        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("valeurgaz", gasValue)
                .build();
        Request request = new Request.Builder()
                .url("http://127.0.0.1:27017/gazDB/addAlert")
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

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, itemsToShow);
        dialogBinding.spinnerGasLeak.setAdapter(adapter);

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
                sendPostRequest(selectedGas, gasValue);
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