package com.daliborhes.weatherwizz.Common.Retrofit;

import com.daliborhes.weatherwizz.Model.WeatherForecastResult;
import com.daliborhes.weatherwizz.Model.WeatherResult;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Dalibor J. Stanković on 23.04.2019.
 */

public interface IOpenWeatherMap {

    @GET("weather")
    Observable<WeatherResult> getWeatherByLatLng(@Query("lat") String lat,
                                                          @Query("lon") String lng,
                                                          @Query("appid") String appId,
                                                          @Query("units") String unit);

    @GET("forecast")
    Observable<WeatherForecastResult> get5DayForecastByLatLng(@Query("lat") String lat,
                                                              @Query("lon") String lng,
                                                              @Query("appid") String appId,
                                                              @Query("units") String unit);

}
