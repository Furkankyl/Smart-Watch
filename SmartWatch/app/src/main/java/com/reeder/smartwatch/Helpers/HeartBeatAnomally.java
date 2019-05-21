package com.reeder.smartwatch.Helpers;

import com.reeder.smartwatch.Model.HeartBeat;

import java.util.ArrayList;
import java.util.List;

public class HeartBeatAnomally {

    private final int HEART_BEAT_LIST_SIZE = 50;
    private final int ANOMALLY_TOLERANCE = 30;

    private  List<HeartBeat> heartBeatList;
    private  double sum;

    public HeartBeatAnomally() {
        heartBeatList = new ArrayList<>();
    }

    public double getAvarage(){
        return sum/HEART_BEAT_LIST_SIZE;
    }
    public boolean addHeartBeat(HeartBeat heartBeat){
        if(heartBeatList.size() == HEART_BEAT_LIST_SIZE)
            heartBeatList.remove(0);

            heartBeatList.add(heartBeat);
            calSum();

            return isAnomally(heartBeat);
    }
    private void calSum(){
        this.sum = 0;
        for(HeartBeat heartBeat: heartBeatList){
            this.sum += heartBeat.getHeartBeat();
        }
    }
    private boolean isAnomally(HeartBeat heartBeat) {
        return heartBeat.getHeartBeat() - getAvarage() > ANOMALLY_TOLERANCE
                || heartBeat.getHeartBeat() - getAvarage() < -ANOMALLY_TOLERANCE;
    }



}
