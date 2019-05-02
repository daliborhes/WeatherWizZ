package com.daliborhes.weatherwizz.Fragments;


import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daliborhes.weatherwizz.Common.Common;
import com.daliborhes.weatherwizz.Common.Retrofit.IOpenWeatherMap;
import com.daliborhes.weatherwizz.Common.Retrofit.RetrofitClient;
import com.daliborhes.weatherwizz.Model.WeatherResult;
import com.daliborhes.weatherwizz.R;
import com.squareup.picasso.Picasso;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 */
public class WeatherTodayFragment extends Fragment {

    Unbinder unbinder;

    @BindView(R.id.weather_image) ImageView img_weather;
    //@BindView(R.id.city_txt) TextView city_name_txt;
    @BindView(R.id.humidity_txt) TextView humidity_txt;
    @BindView(R.id.sunrise_txt) TextView sunrise_txt;
    @BindView(R.id.sunset_txt) TextView sunset_txt;
    @BindView(R.id.time_txt) TextView datetime_txt;
    @BindView(R.id.pressure_txt) TextView pressure_txt;
    @BindView(R.id.temperature_txt) TextView temperature_txt;
    @BindView(R.id.description_txt) TextView description_txt;
    @BindView(R.id.wind_txt) TextView wind_txt;

    @BindView(R.id.progress_bar) ProgressBar progressBar;
    @BindView(R.id.weather_panel)
    ConstraintLayout weather_panel;

    CompositeDisposable compositeDisposable;
    IOpenWeatherMap mService;

    static WeatherTodayFragment instance;

    public static WeatherTodayFragment getInstance() {
        if (instance == null) {
            instance = new WeatherTodayFragment();
        }
        return instance;
    }

    public WeatherTodayFragment() {
        // Required empty public constructor
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getInstance();
        mService = retrofit.create(IOpenWeatherMap.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_today, container, false);

        unbinder = ButterKnife.bind(this, itemView);

        getWeatherInfo();

        return itemView;
    }

    public void getWeatherInfo() {
        compositeDisposable.add(mService.getWeatherByLatLng(String.valueOf(Common.current_location.getLatitude()),
                String.valueOf(Common.current_location.getLongitude()),
                Common.APP_ID,
                "metric")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeatherResult>() {
                    @Override
                    public void accept(WeatherResult weatherResult) throws Exception {
                        // Load images
                        Picasso.get().load("https://openweathermap.org/img/w/" +
                                weatherResult.getWeather().get(0).getIcon() +
                                ".png").into(img_weather);

                        // Load information
                        //city_name_txt.setText(weatherResult.getName());
                        description_txt.setText(weatherResult.getWeather().get(0).getDescription());
                        Integer temp = weatherResult.getMain().getTemp().intValue();
                        temperature_txt.setText(String.valueOf(temp));
                        datetime_txt.setText(Common.convertUnixToDate(weatherResult.getDt()));
                        pressure_txt.setText(String.valueOf(weatherResult.getMain().getPressure()) + " hpa");
                        humidity_txt.setText(String.valueOf(weatherResult.getMain().getHumidity()) + " %");
                        sunrise_txt.setText(Common.convertUnixToHour(weatherResult.getSys().getSunrise()));
                        sunset_txt.setText(Common.convertUnixToHour(weatherResult.getSys().getSunset()));

                        wind_txt.setText(String.valueOf(weatherResult.getWind().getSpeed()) + " m/s");

                        // Display information
                        weather_panel.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(getActivity(), "" + throwable.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("Today", "" + throwable.getMessage().toString());
                    }
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
