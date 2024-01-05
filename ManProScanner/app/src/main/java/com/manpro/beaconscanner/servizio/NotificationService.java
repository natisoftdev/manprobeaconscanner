package com.manpro.beaconscanner.servizio;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.manpro.beaconscanner.processi.SendNotification;

public class NotificationService extends Service {

    private static final String TAG = "NotificationService";
    public static NotificationService notificationService;
    public static SendNotification sendNotification;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationService = this;
/*
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Login = preferences.getString("pref_db_login", "");
        Password = preferences.getString("pref_db_password", "");
        db_connect = preferences.getString("pref_db_string", "");
        Log.i("SharedPreferences",Login + " - " + Password + " - " + db_connect);
*/
        sendNotification = new SendNotification();
        sendNotification.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG,"onStartCommand -> START_STICKY");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG,"onDestroy -> Servizio distrutto");
        super.onDestroy();
    }
}