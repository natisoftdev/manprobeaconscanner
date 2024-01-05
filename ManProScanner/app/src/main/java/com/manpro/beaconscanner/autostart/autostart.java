package com.manpro.beaconscanner.autostart;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.manpro.beaconscanner.servizio.NotificationService;
import com.manpro.beaconscanner.servizio.ScannerBeaconService;

public class autostart extends BroadcastReceiver {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    public void onReceive(Context arg0, Intent arg1) {
        Intent intent = new Intent(arg0, ScannerBeaconService.class);
        //Intent intent2 = new Intent(arg0, NotificationService.class);
        arg0.startService(intent);
        //arg0.startService(intent2);
        Log.i("Autostart", "started");
    }
}