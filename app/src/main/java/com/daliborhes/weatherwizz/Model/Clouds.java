package com.daliborhes.weatherwizz.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Dalibor J. Stanković on 23.04.2019.
 */

public class Clouds {
    @SerializedName("all")
    @Expose
    private Integer all;

    public Clouds() {
    }

    public Integer getAll() {
        return all;
    }

    public void setAll(Integer all) {
        this.all = all;
    }
}
