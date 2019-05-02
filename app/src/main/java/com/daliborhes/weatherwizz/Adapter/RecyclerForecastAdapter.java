package com.daliborhes.weatherwizz.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daliborhes.weatherwizz.Common.Common;
import com.daliborhes.weatherwizz.Model.WeatherForecastResult;
import com.daliborhes.weatherwizz.R;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
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

        Picasso.get().load(new StringBuilder("https://openweathermap.org/img/w/")
                .append(weatherForecastResult.getList().get(position).getWeather().get(0).getIcon())
                .append(".png").toString()).into(myViewHolder.forecastIcon);

        myViewHolder.forecastDay.setText(Common.convertUnixToDay(weatherForecastResult.getList().get(position).getDt()));
        Integer temp = weatherForecastResult.getList().get(position).getMain().getTemp().intValue();
        myViewHolder.forecastTemp.setText(new StringBuilder(String.valueOf(temp)).append(" °C"));

    }

    @Override
    public int getItemCount() {
        return weatherForecastResult.getList().size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView forecastIcon;
        private TextView forecastDay, forecastTemp;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            forecastIcon = itemView.findViewById(R.id.day_icon);
            forecastDay = itemView.findViewById(R.id.days_txt);
            forecastTemp = itemView.findViewById(R.id.temperature_day_txt);

        }
    }
}
