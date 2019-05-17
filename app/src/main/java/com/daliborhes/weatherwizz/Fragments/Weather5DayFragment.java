package com.daliborhes.weatherwizz.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daliborhes.weatherwizz.Adapter.Weather5DayAdapter;
import com.daliborhes.weatherwizz.Common.Common;
import com.daliborhes.weatherwizz.Common.Retrofit.IOpenWeatherMap;
import com.daliborhes.weatherwizz.Common.Retrofit.RetrofitClient;
import com.daliborhes.weatherwizz.Model.forecast5DayWeather.WeatherForecastResult;
import com.daliborhes.weatherwizz.R;

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
public class Weather5DayFragment extends Fragment {

    @BindView(R.id.forecast_recyclerview)
    RecyclerView forecastRecyclerView;

    private static Weather5DayFragment instance;
    private CompositeDisposable compositeDisposable;
    private IOpenWeatherMap mService;

    private Unbinder unbinder;

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_weather_5_day, container, false);

        unbinder = ButterKnife.bind(this, itemView);

        getForecastInfo();

        return itemView;
    }

    private void getForecastInfo() {
        compositeDisposable.add(mService.get5DayForecastByLatLng(
                String.valueOf(Common.current_location.getLatitude()),
                String.valueOf(Common.current_location.getLongitude()),
                Common.APP_ID,
                "metric")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::displayForecast5Day,
                        throwable -> {
                            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
                            Log.d("CityFragment", "" + throwable.getMessage());
                        })
        );
    }

    private void displayForecast5Day(WeatherForecastResult weatherForecastResult) {

        forecastRecyclerView.setHasFixedSize(true);
        forecastRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Weather5DayAdapter adapter = new Weather5DayAdapter(getContext(), weatherForecastResult);
        forecastRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
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
