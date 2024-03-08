package com.example.gazdetectorapplication.ui.plug_in;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.gazdetectorapplication.R;
import com.example.gazdetectorapplication.databinding.FragmentPlugInBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlugInFragment extends Fragment {


    private FragmentPlugInBinding binding;
    Handler taskHandler = new Handler();
    LineDataSet ld1,ld2,ld3;
    ArrayList<ILineDataSet> datasets = new ArrayList<ILineDataSet>();
    float num = 0;


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
        ld2 = new LineDataSet(new ArrayList<Entry>(),"LPG");
        ld3 = new LineDataSet(new ArrayList<Entry>(), "Smoke");
        ld1.setColor(Color.CYAN);
        ld2.setColor(Color.RED);
        ld3.setColor(Color.GREEN);
        datasets.add(ld1);
        datasets.add(ld2);
        datasets.add(ld3);
        LineData d = new LineData(datasets);
        binding.gasChart.setData(d);
        taskHandler.postDelayed(getDataValue,0);
        return root;
    }

    private void modifyDataSet() {
        ld1.addEntry(new Entry( num, (float) (Math.random()*100)));
        ld2.addEntry(new Entry( num, (float) (Math.random()*100)));
        ld3.addEntry(new Entry( num, (float) (Math.random()*100)));
        binding.gasChart.setVisibleXRangeMinimum(num-1);
        binding.gasChart.setVisibleXRangeMaximum(num-1);
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
            taskHandler.postDelayed(getDataValue, 1000);
        }
    };


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        taskHandler.removeCallbacks(getDataValue);

    }
}