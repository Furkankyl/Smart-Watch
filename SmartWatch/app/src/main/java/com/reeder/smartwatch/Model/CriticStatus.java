package com.reeder.smartwatch.Model;

import java.util.Date;

public class CriticStatus {
    private String id;
    private int totalHeartBeat;
    private int totalAnomallyHeartBeat;
    private Date date;

    public CriticStatus() {
    }

    public CriticStatus(String id, int totalHeartBeat, int totalAnomallyHeartBeat, Date date) {
        this.id = id;
        this.totalHeartBeat = totalHeartBeat;
        this.totalAnomallyHeartBeat = totalAnomallyHeartBeat;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTotalHeartBeat() {
        return totalHeartBeat;
    }

    public void setTotalHeartBeat(int totalHeartBeat) {
        this.totalHeartBeat = totalHeartBeat;
    }

    public int getTotalAnomallyHeartBeat() {
        return totalAnomallyHeartBeat;
    }

    public void setTotalAnomallyHeartBeat(int totalAnomallyHeartBeat) {
        this.totalAnomallyHeartBeat = totalAnomallyHeartBeat;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
