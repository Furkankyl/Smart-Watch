package com.reeder.smartwatch.Model;

import java.util.Date;

public class HeartBeat {
    private double heartBeat;
    private double o2;
    private Date date;
    private boolean isNormal;

    public HeartBeat() {
    }

    public HeartBeat(double heartBeat, double o2, Date date, boolean isNormal) {
        this.heartBeat = heartBeat;
        this.o2 = o2;
        this.date = date;
        this.isNormal = isNormal;
    }

    public double getHeartBeat() {
        return heartBeat;
    }

    public void setHeartBeat(double heartBeat) {
        this.heartBeat = heartBeat;
    }

    public double getO2() {
        return o2;
    }

    public void setO2(double o2) {
        this.o2 = o2;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isNormal() {
        return isNormal;
    }

    public void setNormal(boolean normal) {
        isNormal = normal;
    }

    @Override
    public String toString() {
        return "HeartBeat{" +
                "heartBeat=" + heartBeat +
                ", o2=" + o2 +
                ", date=" + date +
                ", isNormal=" + isNormal +
                '}';
    }
}
