package com.manpro.beaconscanner.processi;

import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import com.manpro.beaconscanner.MonitoringActivity;
import com.manpro.beaconscanner.notifica.CreazioneNotifica;
import com.manpro.beaconscanner.servizio.NotificationService;

import java.util.Timer;
import java.util.TimerTask;

public class SendNotification extends Thread{

    private static final String TAG = "SendNotification";
    // run on another Thread to avoid crash
    private Handler mHandler = new Handler();
    // timer handling
    private Timer mTimer = null;
    ContentResolver crT = null;

    public SendNotification() {
        //crT = NotificationService.notificationService.getContentResolver();
        // cancel if already existed
        if(mTimer != null) { mTimer.cancel(); }
        else { mTimer = new Timer(); }
    }

    public void start(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MonitoringActivity.monitoringActivity);
        String timeSendSec = preferences.getString("pref_controllo_notifiche","");// 5 Minuti di base
        //Log.i(TAG,"timeSendSec -> " + timeSendSec);
        int secondi;

        if(timeSendSec != ""){ secondi = Integer.parseInt(timeSendSec) * 60; }
        else { secondi = (5 * 60); }
        //Log.i(TAG,"secondi -> " + secondi);
        mTimer.scheduleAtFixedRate(new SendNotification.TimeDisplayTimerTask(), 30*1000, secondi * 1000);
    }

    class TimeDisplayTimerTask extends TimerTask {
        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    execute();
                }
            });
        }
    }

    private void execute(){
        //Interrogo DB
        Log.i(TAG,"Interrogo DB");
        Log.i(TAG,"-> " + mTimer.toString());
        new CreazioneNotifica();
    }

    public void interruptThread () {
        this.interrupt();
    }

    public String getIdTimer(){
        return  mTimer.toString();
    }

    public void deleteTimer (){
        mTimer.cancel();
    }
}