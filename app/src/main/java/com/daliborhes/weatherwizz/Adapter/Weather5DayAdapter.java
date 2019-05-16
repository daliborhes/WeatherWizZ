package com.daliborhes.weatherwizz.Adapter;

import android.content.Context;
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
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Dalibor J. Stanković on 23.04.2019.
 */

public class Weather5DayAdapter extends RecyclerView.Adapter<Weather5DayAdapter.MyViewHolder> {

    private Context mContext;
    private WeatherForecastResult weatherForecastResult;

    public Weather5DayAdapter(Context mContext, WeatherForecastResult weatherForecastResult) {
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

        // Replace icons
        replaceIcons(myViewHolder, position);

        // Load information
        displayForecastInfo(myViewHolder, position);
    }

    private void displayForecastInfo(MyViewHolder myViewHolder, int position) {
        myViewHolder.forecastDay.setText(Common.convertUnixToDay(weatherForecastResult.getList().get(position).getDt()));
        int temp = (int) Math.round(weatherForecastResult.getList().get(position).getMain().getTemp());
        myViewHolder.forecastTemp.setText(String.valueOf(temp));
        myViewHolder.forecastHumidity.setText(weatherForecastResult.getList().get(position).getMain().getHumidity() + " %");
        myViewHolder.forecastPressure.setText(weatherForecastResult.getList().get(position).getMain().getPressure() + " hPa");

        myViewHolder.forecastWind.setText(weatherForecastResult.getList().get(position).getWind().getSpeed() + " m/s,");

        int drawableId = Common.convertDegreeToCardinalDirectionImg(weatherForecastResult.getList().get(position).getWind().getDeg());
        myViewHolder.forecastWindDirection.setImageResource(drawableId);

//        myViewHolder.forecastMinMaxTemp.setText(Math.round(weatherForecastResult.getList().get(position).getMain().getTempMin()) + "/" +
//                Math.round(weatherForecastResult.getList().get(position).getMain().getTempMax()));

        if ((position % 2) == 0) {
            myViewHolder.container.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_transition_from_left));
        } else {
            myViewHolder.container.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_transition_from_right));
        }

    }

    private void replaceIcons(MyViewHolder myViewHolder, int position) {

        String serverIcon = weatherForecastResult.getList().get(position).getWeather().get(0).getIcon();

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
    }

    @Override
    public int getItemCount() {
        return weatherForecastResult.getList().size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.day_icon)
        ImageView forecastIcon;
        @BindView(R.id.wind_direction_iv)
        ImageView forecastWindDirection;
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

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
