package com.daliborhes.weatherwizz.Fragments;


import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.daliborhes.weatherwizz.Common.Common;
import com.daliborhes.weatherwizz.Common.Retrofit.IOpenWeatherMap;
import com.daliborhes.weatherwizz.Common.Retrofit.RetrofitClient;
import com.daliborhes.weatherwizz.Model.forecast5DayWeather.WeatherForecastResult;
import com.daliborhes.weatherwizz.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;

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

    @BindView(R.id.graph_pressure)
    LineChart lineChartPressure;
    @BindView(R.id.graph_temperature)
    LineChart lineChartTemp;
    @BindView(R.id.graph_wind)
    LineChart lineChartWind;
    @BindView(R.id.graph_temperature_txt)
    TextView graph_title;

    private CompositeDisposable compositeDisposable;
    private IOpenWeatherMap mService;

    private static WeatherGraphFragment instance;
    private ArrayList<Entry> lineValuesTemp = new ArrayList<>();
    private ArrayList<Entry> lineValuesWind = new ArrayList<>();
    private ArrayList<Entry> lineValuesPressure = new ArrayList<>();

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
                .subscribe(weatherResult -> displayGraphInfo(weatherResult)
                        , throwable -> {
                            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
                            Log.d("GraphFragment", "" + throwable.getMessage());
                        })
        );
    }

    private void displayGraphInfo(WeatherForecastResult weatherResult) {
        // Load information
        for (int i = 0; i < weatherResult.getList().size(); i++) {
            double temp = weatherResult.getList().get(i).getMain().getTemp();
            float tempFloat = (float) temp;
            int hour = weatherResult.getList().get(i).getDt();
            double windSpeed = weatherResult.getList().get(i).getWind().getSpeed();
            float windSpeedFloat = (float) windSpeed;
            double pressure = weatherResult.getList().get(i).getMain().getPressure();
            float pressureFloat = (float) pressure;
            Log.d("JSON temp", "getWeatherInfo: " + temp + " " + i + " " + hour + " " + windSpeedFloat);

            lineValuesTemp.add(new Entry(hour, tempFloat));
            lineValuesWind.add(new Entry(hour, windSpeedFloat));
            lineValuesPressure.add(new Entry(hour, pressureFloat));

        }

        // Hourly temperature line chart
        displayLineTemperatureChart();

        // Hourly wind speed line chart
        displayLineWindChart();

        // Hourly pressure line chart
        displayLinePressureChart();
    }

    private void displayLineTemperatureChart() {
        LineDataSet setTemp = new LineDataSet(lineValuesTemp, "Temperature");
        setTemp.setColor(R.color.colorPrimaryDark);
        setTemp.setDrawFilled(true);
        if (Utils.getSDKInt() >= 18) {
            // fill drawable only supported on api level 18 and above
            Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.fade_red);
            setTemp.setFillDrawable(drawable);
        } else {
            setTemp.setFillColor(Color.BLACK);
        }
        setTemp.setCircleColor(R.color.colorPrimaryDark);
        setTemp.setValueTextSize(12);

        LineData dataTemp = new LineData(setTemp);
        lineChartTemp.getXAxis().setLabelCount(50);
        lineChartTemp.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChartTemp.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return Common.convertUnixToDay((int) value);
            }
        });
        lineChartTemp.setDragEnabled(false);
        lineChartTemp.getLegend().setEnabled(false);
        lineChartTemp.getDescription().setEnabled(false);
        lineChartTemp.animateXY(0, 2000);
        lineChartTemp.setPinchZoom(false);
        lineChartTemp.getAxisLeft().setEnabled(false);
        lineChartTemp.getAxisRight().setEnabled(false);
        lineChartTemp.setDrawGridBackground(false);
        lineChartTemp.setData(dataTemp);
        lineChartTemp.invalidate();
    }

    private void displayLineWindChart() {
        LineDataSet setWind = new LineDataSet(lineValuesWind, "Wind");
        setWind.setColor(R.color.colorPrimaryDark);
        setWind.setCircleColor(R.color.colorPrimaryDark);
        setWind.setDrawFilled(true);
        if (Utils.getSDKInt() >= 18) {
            // fill drawable only supported on api level 18 and above
            Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.fade_blue);
            setWind.setFillDrawable(drawable);
        } else {
            setWind.setFillColor(Color.BLACK);
        }
        setWind.setValueTextSize(12);

        LineData datawind = new LineData(setWind);
        lineChartWind.getXAxis().setLabelCount(50);
        lineChartWind.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChartWind.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return Common.convertUnixToDay((int) value);
            }
        });
        lineChartWind.getLegend().setEnabled(false);
        lineChartWind.getDescription().setEnabled(false);
        lineChartWind.getAxisLeft().setEnabled(false);
        lineChartWind.getAxisRight().setEnabled(false);
        lineChartWind.animateXY(0, 2000);
        lineChartWind.setPinchZoom(false);
        lineChartWind.setDrawGridBackground(false);
        lineChartWind.setData(datawind);
        lineChartWind.invalidate();
    }

    private void displayLinePressureChart() {
        LineDataSet setPressure = new LineDataSet(lineValuesPressure, "Pressure");
        setPressure.setColor(R.color.colorPrimaryDark);
        setPressure.setDrawFilled(true);
        if (Utils.getSDKInt() >= 18) {
            // fill drawable only supported on api level 18 and above
            Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.fade_green);
            setPressure.setFillDrawable(drawable);
        } else {
            setPressure.setFillColor(Color.BLACK);
        }
        setPressure.setCircleColor(R.color.colorPrimaryDark);
        setPressure.setValueTextSize(10);

        LineData dataPressure = new LineData(setPressure);
        lineChartPressure.getXAxis().setLabelCount(50);
        lineChartPressure.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChartPressure.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return Common.convertUnixToDay((int) value);
            }
        });
        lineChartPressure.setDragEnabled(false);
        lineChartPressure.getLegend().setEnabled(false);
        lineChartPressure.getDescription().setEnabled(false);
        lineChartPressure.animateXY(0, 2000);
        lineChartPressure.setPinchZoom(false);
        lineChartPressure.getAxisLeft().setEnabled(false);
        lineChartPressure.getAxisRight().setEnabled(false);
        lineChartPressure.setDrawGridBackground(false);
        lineChartPressure.setData(dataPressure);
        lineChartPressure.invalidate();
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
