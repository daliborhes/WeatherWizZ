package com.daliborhes.weatherwizz.Fragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daliborhes.weatherwizz.Adapter.RecyclerForecastAdapter;
import com.daliborhes.weatherwizz.Common.Common;
import com.daliborhes.weatherwizz.Common.Retrofit.IOpenWeatherMap;
import com.daliborhes.weatherwizz.Common.Retrofit.RetrofitClient;
import com.daliborhes.weatherwizz.Model.WeatherForecastResult;
import com.daliborhes.weatherwizz.R;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 */
public class Weather5DayFragment extends Fragment {

    static Weather5DayFragment instance;

    CompositeDisposable compositeDisposable;
    IOpenWeatherMap mService;
    private RecyclerView forecastRecyclerView;
    private RecyclerForecastAdapter adapter;

    public static Weather5DayFragment getInstance() {
        if (instance == null) {
            instance = new Weather5DayFragment();
        }
        return instance;
    }

    public Weather5DayFragment() {
        // Required empty public constructor
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getInstance();
        mService = retrofit.create(IOpenWeatherMap.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_weather16_day, container, false);

        forecastRecyclerView = itemView.findViewById(R.id.forecast_recyclerview);

        getForecastInfo();

        return itemView;
    }

    @Override
    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    private void getForecastInfo() {
        compositeDisposable.add(mService.get5DayForecastByLatLng(
                String.valueOf(Common.current_location.getLatitude()),
                String.valueOf(Common.current_location.getLongitude()),
                Common.APP_ID,
                "metric")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeatherForecastResult>() {
                    @Override
                    public void accept(WeatherForecastResult weatherForecastResult) throws Exception {
                        displayForecast16Day(weatherForecastResult);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d("Forecast", "Error: " + throwable.getMessage());
                    }
                }));
    }

    private void displayForecast16Day(WeatherForecastResult weatherForecastResult) {

        forecastRecyclerView.setHasFixedSize(true);
        forecastRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        RecyclerForecastAdapter adapter = new RecyclerForecastAdapter(getContext(), weatherForecastResult);
        forecastRecyclerView.setAdapter(adapter);
    }

}
