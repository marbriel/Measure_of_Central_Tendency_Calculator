package com.example.project1;

import android.databinding.DataBindingUtil;
import android.os.Build.VERSION_CODES;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.project1.databinding.ActivityMainBinding;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static  final DecimalFormat decimalFormat = new DecimalFormat("0.00");
    @RequiresApi(api = VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        binding.calculate.setOnClickListener(view -> {
            if(!TextUtils.isEmpty(binding.data.getText().toString())){
                List<Double> data = csvToListDouble(binding.data.getText().toString());
                Double mean = getMean(data);
                Double median = getMedian(data);
                List<Double> mode = getMode(data);
                Double variance = getVariance(data, mean);
                Double sd = getSd(variance);
                binding.meanResult.setText("Mean : " +mean.toString());
                binding.medianResult.setText("Median : "+ median.toString());
                if(mode.isEmpty()){
                    binding.modeResult.setText("Mode : No mode/s found");
                }else{
                    binding.modeResult.setText("Mode : "+ mode.toString());
                }
                binding.varianceResult.setText("Variance : "+ variance.toString());
                binding.sdResult.setText("Standard Deviation : "+ sd.toString());

            }else{
                Toast toast = Toast.makeText(this, "Data must not be null", Toast.LENGTH_SHORT);
                toast.show();
            }

        });
    }

    protected List<Double> csvToListDouble(String data){
        List<String> stringData = Arrays.asList(data.split("[\\\\s,]+"));
        List<Double> convertedData = new ArrayList<>();
        for (String singleData: stringData) {
            convertedData.add(Double.parseDouble(singleData));
        }

        return convertedData;
    }
    protected Double getMean(List<Double> data){
        double mean = 0;
        double sum = 0;
        for(Double datum: data){
            sum+=datum;
        }

        String meanFormatted = decimalFormat.format(sum/data.size());
        return Double.parseDouble(meanFormatted);
    }


    protected Double getMedian(List<Double> data){
        Collections.sort(data);
        double median = 0.0;
        if(data.size()%2 == 0){
            double midValue1 = data.get((data.size()/2)-1);
            double midValue2 = data.get(data.size()/2);
            median = (midValue1 + midValue2)/2;
        }else{
            median = data.get(data.size()/2);
        }
        return median;
    }

    @RequiresApi(api = VERSION_CODES.N)
    protected List<Double> getMode(List<Double> data){
        HashMap<Double, Integer> dataValues = new HashMap<>();
        for (Double datum: data) {
            if(!dataValues.containsKey(datum)){
                dataValues.put(datum, 1);
            }else{
                int dataValue = dataValues.get(datum) + 1;
                dataValues.replace(datum, dataValues.get(datum), dataValue);
            }
        }

        List<Double> modes = new ArrayList<>();
        ConcurrentHashMap<Double, Integer> modeMap = new ConcurrentHashMap<>();

        for(Map.Entry<Double, Integer> item: dataValues.entrySet() ){
            if(modeMap.isEmpty() && item.getValue() != 1){
                modeMap.put(item.getKey(), item.getValue());
            }else{
                for(Map.Entry<Double, Integer> modeMax: modeMap.entrySet()){
                    if(modeMax.getValue() == item.getValue()){
                        modeMap.put(item.getKey(), item.getValue());
                    }
                    if(modeMax.getValue() < item.getValue()){
                        if(item.getValue() != 1){
                            modeMap.remove(modeMax.getKey());
                            modeMap.put(item.getKey(), item.getValue());
                        }
                    }
                }
            }
        }

        for(Map.Entry<Double, Integer> mode: modeMap.entrySet()){
            modes.add(mode.getKey());
        }
        return modes;
    }

    protected Double getVariance(List<Double> data, Double mean){
        double sumVariance = 0.0;
        for(Double datum: data){
            sumVariance += Math.pow((datum - mean), 2);
        }
        double variance = sumVariance/data.size();
        return Double.parseDouble(decimalFormat.format(variance));
    }

    protected Double getSd(Double variance){
        Double sd = Math.sqrt(variance);
        return Double.parseDouble(decimalFormat.format(sd));
    }



}