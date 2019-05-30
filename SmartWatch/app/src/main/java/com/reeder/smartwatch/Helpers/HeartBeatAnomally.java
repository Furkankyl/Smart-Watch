package com.reeder.smartwatch.Helpers;

import android.util.Log;

import com.reeder.smartwatch.Model.HeartBeat;

import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class HeartBeatAnomally {

    private final int HEART_BEAT_LIST_SIZE = 50;
    private final int ANOMALLY_TOLERANCE = 30;
    private final int ANOMALLY_CRITIC_TOLERANCE = 75;
    private  List<HeartBeat> heartBeatList;
    private  double sum;

    public HeartBeatAnomally() {
        heartBeatList = new ArrayList<>();
    }

    public double getAvarage(){
        return sum/(double)heartBeatList.size();
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
        Log.d(TAG, "isAnomally: ort: "+getAvarage() + "heart: "+heartBeat.getHeartBeat());
        if(heartBeat.getHeartBeat() - getAvarage() > 0)
            return  !(heartBeat.getHeartBeat() - getAvarage() > ANOMALLY_TOLERANCE);
        else
            return  !((heartBeat.getHeartBeat() - getAvarage())*-1 > ANOMALLY_TOLERANCE);

    }

    public boolean isCriticStatus(){
        return (double)totalAnomallyHeartBeatCount()/(double)totalHeartBeatCount() > ANOMALLY_CRITIC_TOLERANCE;
    }

    public int totalHeartBeatCount(){
        return heartBeatList.size();
    }
    public int totalAnomallyHeartBeatCount(){
        int count = 0;
        for(HeartBeat heartBeat:heartBeatList){
            if(!heartBeat.isNormal())
                count++;
        }
        return count;
    }
}
