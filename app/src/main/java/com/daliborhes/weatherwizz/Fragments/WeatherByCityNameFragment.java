package com.daliborhes.weatherwizz.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.daliborhes.weatherwizz.Common.Common;
import com.daliborhes.weatherwizz.Common.Retrofit.IOpenWeatherMap;
import com.daliborhes.weatherwizz.Common.Retrofit.RetrofitClient;
import com.daliborhes.weatherwizz.Model.currentWeather.WeatherResult;
import com.daliborhes.weatherwizz.R;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

/**
 * Created by Dalibor J. Stanković on 14.05.2019.
 */

public class WeatherByCityNameFragment extends Fragment {

    @BindView(R.id.search_city_et)
    EditText searchCityET;
    @BindView(R.id.search_time_txt)
    TextView cityTimeTxt;
    @BindView(R.id.search_city_btn)
    Button searchCityBtn;
    @BindView(R.id.city_name_txt)
    TextView cityNameTxt;
    @BindView(R.id.temp_city_txt)
    TextView cityTempTxt;
    @BindView(R.id.weather_desc_city_txt)
    TextView cityWeatherDesc;
    @BindView(R.id.humidity_city_txt)
    TextView cityHumidityTxt;
    @BindView(R.id.pressure_city_txt)
    TextView cityPressureTxt;
    @BindView(R.id.wind_city_txt)
    TextView cityWindTxt;
    @BindView(R.id.search_city_layout)
    ConstraintLayout searchLayout;
    @BindView(R.id.search_icon)
    ImageView searchIcon;

    public static WeatherByCityNameFragment instance;
    private final CompositeDisposable compositeDisposable;
    private final IOpenWeatherMap mService;
    private Unbinder unbinder;

    public static WeatherByCityNameFragment getInstance() {
        if (instance == null) {
            instance = new WeatherByCityNameFragment();
        }
        return instance;
    }

    public WeatherByCityNameFragment() {
        // Required empty public constructor
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getInstance();
        mService = retrofit.create(IOpenWeatherMap.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View itemView = inflater.inflate(R.layout.fragment_city, container, false);

        unbinder = ButterKnife.bind(this, itemView);

        searchCityBtn.setOnClickListener(v -> {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            String cityName = searchCityET.getText().toString();
            getWeatherInfo(cityName);
            searchCityET.getText().clear();
        });
        return itemView;
    }

    private void getWeatherInfo(String weatherByCity) {
        compositeDisposable.add(mService.getWeatherByCityName(weatherByCity,
                Common.APP_ID,
                "metric")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::loadSearchInfo,
                        throwable -> {
                            searchLayout.setVisibility(View.INVISIBLE);
                            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
                            Log.d("CityFragment", "" + throwable.getMessage());
                        })
        );
    }

    private void loadSearchInfo(WeatherResult weatherResult) {

        searchLayout.setVisibility(View.VISIBLE);

        // Replace icons
        replaceIcons(weatherResult);

        // Load information
        displaySearchInfo(weatherResult);

    }

    private void displaySearchInfo(WeatherResult weatherResult) {
        int time = weatherResult.getDt();
        cityTimeTxt.setText(Common.convertUnixToDay(time) + "h");
        int temperatureByCityName = (int) Math.round(weatherResult.getMain().getTemp());
        cityTempTxt.setText(String.valueOf(temperatureByCityName));
        cityNameTxt.setText(weatherResult.getName());
        cityWeatherDesc.setText(weatherResult.getWeather().get(0).getDescription());
        cityHumidityTxt.setText(getString(R.string.humidity) + " " + weatherResult.getMain().getHumidity() + "%");
        cityPressureTxt.setText(getString(R.string.pressure) + " " + weatherResult.getMain().getPressure() + " hPa");
        cityWindTxt.setText(getString(R.string.wind) + " " + weatherResult.getWind().getSpeed() + " m/s");
    }

    private void replaceIcons(WeatherResult weatherResult) {
        String serverIcon = weatherResult.getWeather().get(0).getIcon();
//        searchIcon.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_transition_from_left));

        switch (serverIcon) {
            case "01d":
                searchIcon.setImageResource(R.drawable.day_sunny);
                break;
            case "02d":
                searchIcon.setImageResource(R.drawable.day_cloudy);
                break;
            case "03d":
                searchIcon.setImageResource(R.drawable.day_cloud);
                break;
            case "04d":
                searchIcon.setImageResource(R.drawable.cloudy);
                break;
            case "09d":
                searchIcon.setImageResource(R.drawable.showers);
                break;
            case "10d":
                searchIcon.setImageResource(R.drawable.day_rain);
                break;
            case "11d":
                searchIcon.setImageResource(R.drawable.thunderstorm);
                break;
            case "13d":
                searchIcon.setImageResource(R.drawable.day_snow);
                break;
            case "50d":
                searchIcon.setImageResource(R.drawable.day_fog);
                break;
            case "01n":
                searchIcon.setImageResource(R.drawable.night_clear);
                break;
            case "02n":
                searchIcon.setImageResource(R.drawable.night_cloudy);
                break;
            case "03n":
                searchIcon.setImageResource(R.drawable.day_cloud);
                break;
            case "04n":
                searchIcon.setImageResource(R.drawable.cloudy);
                break;
            case "09n":
                searchIcon.setImageResource(R.drawable.showers);
                break;
            case "10n":
                searchIcon.setImageResource(R.drawable.night_rain);
                break;
            case "11n":
                searchIcon.setImageResource(R.drawable.thunderstorm);
                break;
            case "13n":
                searchIcon.setImageResource(R.drawable.night_snow);
                break;
            case "50n":
                searchIcon.setImageResource(R.drawable.night_fog);
                break;
            default:
                Picasso.get().load("https://openweathermap.org/img/w/" + serverIcon + ".png")
                        .into(searchIcon);
                break;
        }
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
