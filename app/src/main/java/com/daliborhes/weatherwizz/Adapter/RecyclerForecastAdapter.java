package com.daliborhes.weatherwizz.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.daliborhes.weatherwizz.Common.Common;
import com.daliborhes.weatherwizz.Model.forecast5DayWeather.WeatherForecastResult;
import com.daliborhes.weatherwizz.R;
import com.daliborhes.weatherwizz.application.AppHelp;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Dalibor J. Stanković on 23.04.2019.
 */

public class RecyclerForecastAdapter extends RecyclerView.Adapter<RecyclerForecastAdapter.MyViewHolder> {

    private Context mContext;
    private WeatherForecastResult weatherForecastResult;
    private String serverIcon;
    private String localIcon = "something";

    public RecyclerForecastAdapter(Context mContext, WeatherForecastResult weatherForecastResult) {
        this.mContext = mContext;
        this.weatherForecastResult = weatherForecastResult;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final View layoutView = LayoutInflater.from(mContext)
                .inflate(R.layout.day_weather_item, viewGroup, false);
        return new MyViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {

        serverIcon = weatherForecastResult.getList().get(position).getWeather().get(0).getIcon();

        switch (serverIcon) {
            case "01d":
                myViewHolder.forecastIcon.setImageResource(R.drawable.day_sunny);
                break;
            case "02d":
                myViewHolder.forecastIcon.setImageResource(R.drawable.day_cloudy);
                break;
            case "03d":
                myViewHolder.forecastIcon.setImageResource(R.drawable.day_cloud);
                break;
            case "04d":
                myViewHolder.forecastIcon.setImageResource(R.drawable.cloudy);
                break;
            case "09d":
                myViewHolder.forecastIcon.setImageResource(R.drawable.showers);
                break;
            case "10d":
                myViewHolder.forecastIcon.setImageResource(R.drawable.day_rain);
                break;
            case "11d":
                myViewHolder.forecastIcon.setImageResource(R.drawable.thunderstorm);
                break;
            case "13d":
                myViewHolder.forecastIcon.setImageResource(R.drawable.day_snow);
                break;
            case "50d":
                myViewHolder.forecastIcon.setImageResource(R.drawable.day_fog);
                break;
            case "01n":
                myViewHolder.forecastIcon.setImageResource(R.drawable.night_clear);
                break;
            case "02n":
                myViewHolder.forecastIcon.setImageResource(R.drawable.night_cloudy);
                break;
            case "03n":
                myViewHolder.forecastIcon.setImageResource(R.drawable.day_cloud);
                break;
            case "04n":
                myViewHolder.forecastIcon.setImageResource(R.drawable.cloudy);
                break;
            case "09n":
                myViewHolder.forecastIcon.setImageResource(R.drawable.showers);
                break;
            case "10n":
                myViewHolder.forecastIcon.setImageResource(R.drawable.night_rain);
                break;
            case "11n":
                myViewHolder.forecastIcon.setImageResource(R.drawable.thunderstorm);
                break;
            case "13n":
                myViewHolder.forecastIcon.setImageResource(R.drawable.night_snow);
                break;
            case "50n":
                myViewHolder.forecastIcon.setImageResource(R.drawable.night_fog);
                break;
            default:
                Picasso.get().load("https://openweathermap.org/img/w/" +
                        weatherForecastResult.getList().get(position).getWeather().get(0).getIcon() +
                        ".png").into(myViewHolder.forecastIcon);
                break;
        }


        myViewHolder.forecastDay.setText(Common.convertUnixToDay(weatherForecastResult.getList().get(position).getDt()));
        int temp = (int) Math.round(weatherForecastResult.getList().get(position).getMain().getTemp());
        myViewHolder.forecastTemp.setText((temp) + " °C");
        myViewHolder.forecastHumidity.setText(new StringBuilder("Humidity: " + weatherForecastResult.getList().get(position).getMain().getHumidity()).append("%"));
        myViewHolder.forecastPressure.setText(new StringBuilder("Pressure: " + weatherForecastResult.getList().get(position).getMain().getPressure()).append(" hPa"));
        myViewHolder.forecastWind.setText("Wind: " + weatherForecastResult.getList().get(position).getWind().getSpeed() + " m/s, " +
                AppHelp.convertDegreeToCardinalDirection(weatherForecastResult.getList().get(position).getWind().getDeg()));
//        myViewHolder.forecastDay.setText(weatherForecastResult.getList().get(0).getMain().getTempMax().intValue() + "/" +
//                weatherForecastResult.getList().get(0).getMain().getTempMin().intValue());

        myViewHolder.container.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_transition_from_left));
    }

    @Override
    public int getItemCount() {
        return weatherForecastResult.getList().size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.day_icon)
        ImageView forecastIcon;
        @BindView(R.id.days_txt)
        TextView forecastDay;
        @BindView(R.id.temperature_day_txt)
        TextView forecastTemp;
        @BindView(R.id.forecast_humidity_item_txt)
        TextView forecastHumidity;
        @BindView(R.id.forecast_pressure_item_txt)
        TextView forecastPressure;
        @BindView(R.id.forecast_wind_item_txt)
        TextView forecastWind;
        @BindView(R.id.container)
        CardView container;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
