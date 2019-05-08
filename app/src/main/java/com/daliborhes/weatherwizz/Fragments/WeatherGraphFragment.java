package com.daliborhes.weatherwizz.Fragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.daliborhes.weatherwizz.Common.Common;
import com.daliborhes.weatherwizz.Common.Retrofit.IOpenWeatherMap;
import com.daliborhes.weatherwizz.Common.Retrofit.RetrofitClient;
import com.daliborhes.weatherwizz.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 */
public class WeatherGraphFragment extends Fragment {

    Unbinder unbinder;

    @BindView(R.id.graph_temperature)
    LineChart lineChartTemp;
    @BindView(R.id.graph_wind)
    LineChart lineChartWind;
    @BindView(R.id.graph_temperature_txt)
    TextView graph_title;

    CompositeDisposable compositeDisposable;
    IOpenWeatherMap mService;

    static WeatherGraphFragment instance;
    private ArrayList<Entry> lineValuesTemp = new ArrayList<>();
    private ArrayList<Entry> lineValuesWind = new ArrayList<>();


    public static WeatherGraphFragment getInstance() {
        if (instance == null) {
            instance = new WeatherGraphFragment();
        }
        return instance;
    }

    public WeatherGraphFragment() {
        // Required empty public constructor
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getInstance();
        mService = retrofit.create(IOpenWeatherMap.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_graph, container, false);

        unbinder = ButterKnife.bind(this, itemView);

        getWeatherInfo();

        return itemView;
    }

    public void getWeatherInfo() {
        compositeDisposable.add(mService.get5DayForecastByLatLng(String.valueOf(Common.current_location.getLatitude()),
                String.valueOf(Common.current_location.getLongitude()),
                Common.APP_ID,
                "metric")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(weatherResult -> {

                    // Load information
                    for (int i = 0; i < weatherResult.getList().size(); i++) {
                        int temp = (int) Math.round(weatherResult.getList().get(i).getMain().getTemp());
                        int hour = weatherResult.getList().get(i).getDt();
                        double windSpeed = weatherResult.getList().get(i).getWind().getSpeed();
                        float windSpeedFloat = (float) windSpeed;
                        Log.d("JSON temp", "getWeatherInfo: " + temp + " " + i + " " + hour + " " + windSpeedFloat);

                        lineValuesTemp.add(new Entry(hour, temp));
                        lineValuesWind.add(new Entry(hour, windSpeedFloat));

                    }

                    LineDataSet setTemp = new LineDataSet(lineValuesTemp, "Temperature in °C");
                    setTemp.setColor(R.color.colorPrimary);
                    setTemp.setCircleColor(R.color.colorPrimary);

                    LineDataSet setWind = new LineDataSet(lineValuesWind, "Wind in m/s");
                    setWind.setColor(R.color.colorPrimary);
                    setWind.setCircleColor(R.color.colorPrimary);


                    LineData dataTemp = new LineData(setTemp);
                    lineChartTemp.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                    lineChartTemp.animateXY(0, 3000);
                    lineChartTemp.setMaxVisibleValueCount(50);
                    lineChartTemp.setPinchZoom(false);
                    lineChartTemp.setDrawGridBackground(true);
                    lineChartTemp.getLegend().setEnabled(false);
                    lineChartTemp.setData(dataTemp);

                    LineData datawind = new LineData(setWind);
                    lineChartWind.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                    lineChartWind.animateXY(0, 3000);
                    lineChartWind.setMaxVisibleValueCount(50);
                    lineChartWind.setPinchZoom(false);
                    lineChartWind.setDrawGridBackground(true);
                    lineChartWind.getLegend().setEnabled(false);
                    lineChartWind.setData(datawind);

                }, throwable -> {
                    Toast.makeText(getActivity(), "" + throwable.getMessage(), Toast.LENGTH_LONG).show();
                    Log.d("GraphFragment", "" + throwable.getMessage());
                })
        );
    }

    @Override
    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
