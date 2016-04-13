package com.example.sanja.simplealarmapp;

/**
 * Created by Hamz on 10.1.2016..
 */
public class Alarm {
    int key;
    int hour;
    int minute;

    public Alarm(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public Alarm(int key, int hour, int minute) {
        this.key = key;
        this.hour = hour;
        this.minute = minute;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }
}