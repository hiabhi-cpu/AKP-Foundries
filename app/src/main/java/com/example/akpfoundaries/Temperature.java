package com.example.akpfoundaries;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Temperature {

    private long time;
    private float temperature;

    public Temperature(long time, float temperature) {
        this.time = time;
        this.temperature = temperature;
    }

    public Temperature() {
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }
}
