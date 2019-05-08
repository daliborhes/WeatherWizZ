package com.daliborhes.weatherwizz.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.daliborhes.weatherwizz.Common.Common;
import com.daliborhes.weatherwizz.Model.WeatherForecastResult;
import com.daliborhes.weatherwizz.R;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Dalibor J. Stanković on 23.04.2019.
 */

public class RecyclerForecastAdapter extends RecyclerView.Adapter<RecyclerForecastAdapter.MyViewHolder> {

    private Context mContext;
    private WeatherForecastResult weatherForecastResult;

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

        Picasso.get().load("https://openweathermap.org/img/w/" +
                weatherForecastResult.getList().get(position).getWeather().get(0).getIcon() +
                ".png").into(myViewHolder.forecastIcon);

        myViewHolder.forecastDay.setText(Common.convertUnixToDay(weatherForecastResult.getList().get(position).getDt()));
        int temp = (int) Math.round(weatherForecastResult.getList().get(position).getMain().getTemp());
        myViewHolder.forecastTemp.setText(new StringBuilder(String.valueOf(temp)).append(" °C"));
        myViewHolder.forecastHumidity.setText(new StringBuilder("Hum: " + weatherForecastResult.getList().get(position).getMain().getHumidity()).append(" %"));
        myViewHolder.forecastPressure.setText(new StringBuilder("Press: " + weatherForecastResult.getList().get(position).getMain().getPressure()).append(" hpa"));
        myViewHolder.forecastWind.setText("Wind: " + weatherForecastResult.getList().get(position).getWind().getSpeed() + " m/s");
//        myViewHolder.forecastDay.setText(weatherForecastResult.getList().get(0).getMain().getTempMax().intValue() + "/" +
//                weatherForecastResult.getList().get(0).getMain().getTempMin().intValue());

        myViewHolder.container.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_transition_from_left));
    }

    @Override
    public int getItemCount() {
        return weatherForecastResult.getList().size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView forecastIcon;
        private TextView forecastDay, forecastTemp, forecastHumidity, forecastPressure, forecastWind;
        private CardView container;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            forecastIcon = itemView.findViewById(R.id.day_icon);
            forecastDay = itemView.findViewById(R.id.days_txt);
            forecastTemp = itemView.findViewById(R.id.temperature_day_txt);
            forecastHumidity = itemView.findViewById(R.id.forecast_humidity_item_txt);
            forecastPressure = itemView.findViewById(R.id.forecast_pressure_item_txt);
            forecastWind = itemView.findViewById(R.id.forecast_wind_item_txt);
            container = itemView.findViewById(R.id.container);

        }
    }
}
