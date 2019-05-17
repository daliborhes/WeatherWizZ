package com.daliborhes.weatherwizz;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.daliborhes.weatherwizz.Adapter.ViewPagerAdapter;
import com.daliborhes.weatherwizz.Common.Common;
import com.daliborhes.weatherwizz.Common.Retrofit.IOpenWeatherMap;
import com.daliborhes.weatherwizz.Common.Retrofit.RetrofitClient;
import com.daliborhes.weatherwizz.Fragments.Weather5DayFragment;
import com.daliborhes.weatherwizz.Fragments.WeatherByCityNameFragment;
import com.daliborhes.weatherwizz.Fragments.WeatherGraphFragment;
import com.daliborhes.weatherwizz.Model.currentWeather.WeatherResult;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;

    @BindView(R.id.scrolling_temp_txt)
    TextView scrollingTempTxt;
    @BindView(R.id.scrolling_celsius_txt)
    TextView scrollingCelsiusTxt;
    @BindView(R.id.scrolling_minmax_temp_txt)
    TextView scrollingMinMaxTempTxt;
    @BindView(R.id.scrolling_desc_txt)
    TextView scrollingDescTxt;
    @BindView(R.id.scrolling_date_txt)
    TextView scrollingDateTxt;
    @BindView(R.id.scrolling_humidity_txt)
    TextView scrollingHumidityTxt;
    @BindView(R.id.scrolling_pressure_txt)
    TextView scrollingPressureTxt;
    @BindView(R.id.scrolling_wind_txt)
    TextView scrollingWindTxt;
    @BindView(R.id.scrolling_sunrise_txt)
    TextView scrollingSunriseTxt;
    @BindView(R.id.scrolling_sunset_txt)
    TextView scrollingSunsetTxt;
    @BindView(R.id.scrolling_image_view)
    ImageView scrollingImageView;
    @BindView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.root_view)
    CoordinatorLayout rootLayout;
    @BindView(R.id.collapsing_toolbar_layout)
    CollapsingToolbarLayout collapsingToolbarLayout;

    private IOpenWeatherMap mService;
    private CompositeDisposable compositeDisposable;
    private String serverIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            buildLocationRequest();
            buildLocationCallback();
            getWeatherInfo();
            swipeRefreshLayout.setRefreshing(false);
        });

        appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            // Only allow pull to refresh when scrolled to top
            swipeRefreshLayout.setEnabled(verticalOffset == 0);
        });


        // Request permission
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            buildLocationRequest();
                            buildLocationCallback();

                            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
                            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        Snackbar.make(rootLayout, "Permission denied", Snackbar.LENGTH_LONG).show();
                    }
                }).check();
    }

    private void buildLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                Common.current_location = locationResult.getLastLocation();

                getWeatherInfo();

                viewPager = findViewById(R.id.view_pager);
                setupViewPager(viewPager);
                tabLayout = findViewById(R.id.tabs);
                tabLayout.setupWithViewPager(viewPager);

                Log.d("Location", locationResult.getLastLocation().getLatitude() + "/" + locationResult.getLastLocation().getLongitude());
            }
        };

    }

    public MainActivity() {
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getInstance();
        mService = retrofit.create(IOpenWeatherMap.class);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(Weather5DayFragment.getInstance(), "5-Day Forecast");
        adapter.addFragment(WeatherGraphFragment.getInstance(), "Graphs");
        adapter.addFragment(WeatherByCityNameFragment.getInstance(), "Cities");

        viewPager.setAdapter(adapter);
    }

    private void buildLocationRequest() {

        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10.0f);
    }

    public void getWeatherInfo() {
        compositeDisposable.add(mService.getWeatherByLatLng(String.valueOf(Common.current_location.getLatitude()),
                String.valueOf(Common.current_location.getLongitude()),
                Common.APP_ID,
                "metric")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::loadInfo,
                        throwable -> {
                            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                            Log.d("Today", "" + throwable.getMessage());
                        })
        );
    }

    private void loadInfo(WeatherResult weatherResult) {

        // Load images
        replaceIcons(weatherResult);

        // Load information
        displayCurrentWeatherInfo(weatherResult);
    }

    private void displayCurrentWeatherInfo(WeatherResult weatherResult) {
        collapsingToolbarLayout.setTitle(weatherResult.getName() + ", " + weatherResult.getSys().getCountry());
        scrollingCelsiusTxt.setText("°C");
        scrollingCelsiusTxt.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_transition_from_right));
        scrollingDescTxt.setText(weatherResult.getWeather().get(0).getDescription());
        scrollingDescTxt.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_transition_from_left));
        int temp = (int) Math.round(weatherResult.getMain().getTemp());
        scrollingTempTxt.setText(String.valueOf(temp));
        scrollingTempTxt.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_transition_from_right));
        scrollingDateTxt.setText(Common.convertUnixToDate(weatherResult.getDt()));
        scrollingDateTxt.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_transition_from_right));
        scrollingPressureTxt.setText(getString(R.string.pressure) + " " + weatherResult.getMain().getPressure() + getString(R.string.pressure_unit));
        scrollingPressureTxt.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_transition_from_left));
        scrollingHumidityTxt.setText(getString(R.string.humidity) + " " + weatherResult.getMain().getHumidity() + getString(R.string.percent));
        scrollingHumidityTxt.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_transition_from_left));
        scrollingWindTxt.setText(getString(R.string.wind) + " " + weatherResult.getWind().getSpeed() + " " + getString(R.string.wind_speed_unit)
                + ", " + Common.convertDegreeToCardinalDirection(weatherResult.getWind().getDeg()));
        scrollingWindTxt.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_transition_from_left));
        scrollingSunriseTxt.setText(getString(R.string.sunrise) + " " + Common.convertUnixToHour(weatherResult.getSys().getSunrise()) + getString(R.string.hour_unit));
        scrollingSunriseTxt.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_transition_from_right));
        scrollingSunsetTxt.setText(getString(R.string.sunset) + " " + Common.convertUnixToHour(weatherResult.getSys().getSunset()) + getString(R.string.hour_unit));
        scrollingSunsetTxt.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_transition_from_right));


        /** TODO: Displaying only current Temp, not Min and Max values
         *  the problem could be in JSON response
         * */
//                    int tempMax = (int) Math.round(weatherResult.getMain().getTempMax());
//                    int tempMin = (int) Math.round(weatherResult.getMain().getTempMin());
//                    scrollingMinMaxTempTxt.setText(tempMin + "/" + tempMax);
//                    Log.d("WeatherInfo", String.valueOf(weatherResult.getMain().getHumidity()));
    }

    private void replaceIcons(WeatherResult weatherResult) {

        serverIcon = weatherResult.getWeather().get(0).getIcon();
        scrollingImageView.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_transition_from_left));

        switch (serverIcon) {
            case "01d":
                scrollingImageView.setImageResource(R.drawable.day_sunny_white);
                break;
            case "02d":
                scrollingImageView.setImageResource(R.drawable.day_cloudy_white);
                break;
            case "03d":
                scrollingImageView.setImageResource(R.drawable.day_cloud_white);
                break;
            case "04d":
                scrollingImageView.setImageResource(R.drawable.cloudy_white);
                break;
            case "09d":
                scrollingImageView.setImageResource(R.drawable.showers_white);
                break;
            case "10d":
                scrollingImageView.setImageResource(R.drawable.day_rain_white);
                break;
            case "11d":
                scrollingImageView.setImageResource(R.drawable.thunderstorm_white);
                break;
            case "13d":
                scrollingImageView.setImageResource(R.drawable.day_snow_white);
                break;
            case "50d":
                scrollingImageView.setImageResource(R.drawable.day_fog_white);
                break;
            case "01n":
                scrollingImageView.setImageResource(R.drawable.night_clear_white);
                break;
            case "02n":
                scrollingImageView.setImageResource(R.drawable.night_cloudy_white);
                break;
            case "03n":
                scrollingImageView.setImageResource(R.drawable.day_cloud_white);
                break;
            case "04n":
                scrollingImageView.setImageResource(R.drawable.cloudy_white);
                break;
            case "09n":
                scrollingImageView.setImageResource(R.drawable.showers_white);
                break;
            case "10n":
                scrollingImageView.setImageResource(R.drawable.night_rain_white);
                break;
            case "11n":
                scrollingImageView.setImageResource(R.drawable.thunderstorm_white);
                break;
            case "13n":
                scrollingImageView.setImageResource(R.drawable.night_snow_white);
                break;
            case "50n":
                scrollingImageView.setImageResource(R.drawable.night_fog_white);
                break;
            default:
                Picasso.get().load("https://openweathermap.org/img/w/" + serverIcon + ".png")
                        .into(scrollingImageView);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
