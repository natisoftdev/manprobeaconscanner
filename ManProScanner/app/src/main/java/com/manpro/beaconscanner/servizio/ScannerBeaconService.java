package com.manpro.beaconscanner.servizio;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.manpro.beaconscanner.BeaconReferenceApplication;
import com.manpro.beaconscanner.MonitoringActivity;

public class ScannerBeaconService extends Service {
    private static final String TAG = "ScannerBeaconService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onCreate() {
        super.onCreate();

        BeaconReferenceApplication application = ((BeaconReferenceApplication) this.getApplicationContext());
        application.setMonitoringActivity(MonitoringActivity.monitoringActivity);
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
