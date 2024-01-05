package com.manpro.beaconscanner;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.RemoteException;
import androidx.core.app.NotificationCompat;

import android.preference.PreferenceManager;
import android.util.Log;

import com.manpro.beaconscanner.dati.Costanti;
import com.manpro.beaconscanner.provider.BeaconProvider;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.startup.RegionBootstrap;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import com.manpro.beaconscanner.database.DatabaseHelperBeacon;

import org.altbeacon.bluetooth.BluetoothCrashResolver;
import org.altbeacon.bluetooth.BluetoothMedic;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * @author Lorenzo Malferrari
 */

public class BeaconReferenceApplication extends Application implements BootstrapNotifier {
    private static final String TAG = "BeaconReferenceApp";
    //private BackgroundPowerSaver backgroundPowerSaver;
    private boolean haveDetectedBeaconsSinceBoot = false;
    private MonitoringActivity monitoringActivity = null;

    public static BluetoothMedic medic;
    @SuppressLint("StaticFieldLeak")
    public static BluetoothCrashResolver bluetoothCrashResolver;
    BeaconManager beaconManager;
    BeaconReferenceApplication beaconReferenceApplication;
    public void onCreate() {
Log.d("debug___" + TAG, "onCreate");
        super.onCreate();
        beaconReferenceApplication = this;
        beaconManager = org.altbeacon.beacon.BeaconManager.getInstanceForApplication(this);

        beaconManager.getBeaconParsers().clear();
        // By default the AndroidBeaconLibrary will only find AltBeacons.  If you wish to make it
        // find a different type of beacon, you must specify the byte layout for that beacon's
        // advertisement with a line like below.  The example shows how to find a beacon with the
        // same byte layout as AltBeacon but with a beaconTypeCode of 0xaabb.  To find the proper
        // layout expression for other beacon types, do a web search for "setBeaconLayout"
        // including the quotes.
        //

        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25")); // ALTBEACON
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19")); // EDDYSTONE UID
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("x,s:0-1=feaa,m:2-2=20,d:3-3,d:4-5,d:6-7,d:8-11,d:12-15")); // EDDYSTONE TLM
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("s:0-1=feaa,m:2-2=10,p:3-3:-41,i:4-20v")); // EDDYSTONE URL
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24")); // IBEACON
        //beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));

        //beaconManager.setDebug(true);

        // Uncomment the code below to use a foreground service to scan for beacons. This unlocks
        // the ability to continually scan for long periods of time in the background on Andorid 8+
        // in exchange for showing an icon at the top of the screen and a always-on notification to
        // communicate to users that your app is using resources in the background.
        //
        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.drawable.ic_loop_scanner);
        builder.setContentTitle(getString(R.string.mexScanAttiva));
        Intent intent = new Intent(this, MonitoringActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("BeaconsChannel",
                    "My Notification Name", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("My Notification Channel Description");
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(channel.getId());
        }

        beaconManager.enableForegroundServiceScanning(builder.build(), 456);
        beaconManager.setEnableScheduledScanJobs(false);
        //beaconManager.setRegionStatePersistenceEnabled(false);

        // For the above foreground scanning service to be useful, you need to disable
        // JobScheduler-based scans (used on Android 8+) and set a fast background scan
        // cycle that would otherwise be disallowed by the operating system.
        //

        //SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(beaconReferenceApplication);
        //int sec;
        //int time_scan = preferences.getInt("pref_time_scan", Costanti.timeScanSec);
        //Log.d("AAAAAAAAAA","sec -> " + time_scan);

        int secondi = Costanti.timeScanSec * 1000;
        //In secondo piano
        beaconManager.setBackgroundBetweenScanPeriod(0);
        beaconManager.setBackgroundScanPeriod(secondi);
        //In primo piano
        beaconManager.setForegroundBetweenScanPeriod(0);
        beaconManager.setForegroundScanPeriod(secondi);

        Log.d(TAG, "setting up background monitoring for beacons and power saving");
        // wake up the app when a beacon is seen
        Region region = new Region("backgroundRegion",null, null, null);
        //RegionBootstrap regionBootstrap =
        new RegionBootstrap(this, region);

        //BluetoothDevice bd = new BluetoothDevice();
        bluetoothCrashResolver = new BluetoothCrashResolver(this.getApplicationContext());
        bluetoothCrashResolver.start();
        //bluetoothCrashResolver.notifyScannedDevice();

        medic = BluetoothMedic.getInstance();
        medic.enablePowerCycleOnFailures(this);
        medic.enablePeriodicTests(this, BluetoothMedic.SCAN_TEST | BluetoothMedic.TRANSMIT_TEST);
    }


    /*
    public void disableMonitoring() {
        if (regionBootstrap != null) {
            regionBootstrap.disable();
            regionBootstrap = null;
        }
    }
    */

    /*
    public void enableMonitoring() {
        Region region = new Region("backgroundRegion",null, null, null);
        regionBootstrap = new RegionBootstrap(this, region);
    }
    */

    @Override
    public void didEnterRegion(Region arg0) {
        // In this example, this class sends a notification to the user whenever a Beacon
        // matching a Region (defined above) are first seen.
        Log.d(TAG, "did enter region.");
        if (!haveDetectedBeaconsSinceBoot) {
            Log.d(TAG, "auto launching MainActivity");

            // The very first time since boot that we detect an beacon, we launch the
            // MainActivity
            Intent intent = new Intent(this, MonitoringActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // Important:  make sure to add android:launchMode="singleInstance" in the manifest
            // to keep multiple copies of this activity from getting created if the user has
            // already manually launched the app.
            this.startActivity(intent);
            haveDetectedBeaconsSinceBoot = true;
        } else {
            if (monitoringActivity != null) {
                // If the Monitoring Activity is visible, we log info about the beacons we have
                // seen on its display
                logToDisplay(getString(R.string.mexVedoBeacon),new ArrayList<Beacon>());
            } else {
                // If we have already seen beacons before, but the monitoring activity is not in
                // the foreground, we send a notification to the user on subsequent detections.
                //Log.d(TAG, "Sending notification.");
                sendNotification();
            }
        }
    }

    @Override
    public void didExitRegion(Region region) {
        logToDisplay(getString(R.string.mexFuoriRegione),new ArrayList<Beacon>());
    }

    @Override
    public void didDetermineStateForRegion(int state, Region region) {
        try { beaconManager.startRangingBeaconsInRegion(region); }
        catch (RemoteException e) { e.printStackTrace(); }

        RangeNotifier rangeNotifier = new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                String dataN = getDateNow();
                //Log.i("CICLO 30 SECONDI",dataN);
                Log.i("rangedBeacons",dataN + " --- N. Beacons visti -> "+beacons.size());
                //Toast.makeText(getBaseContext(), dataN + " --- N. Beacons visti -> "+beacons.size(), Toast.LENGTH_LONG).show();
                Log.d(TAG, "didRangeBeaconsInRegion called with beacon count:  "+beacons.size());

                ArrayList<Beacon> listaB = new ArrayList<>();

                if (beacons.size() > 0) {
                    Log.i("rangedBeacons","Devo effettuare Tot Cicli -> "+beacons.size());
                    int i = 0;

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                    String indirizzoInvio = preferences.getString("pref_indirizzoInvio", "");
                    Log.i("rangedBeacons","indirizzoInvio -> "+indirizzoInvio);
                    String Login = preferences.getString("pref_db_login", "");
                    Log.i("rangedBeacons","Login -> "+Login);
                    String Password = preferences.getString("pref_db_password", "");
                    Log.i("rangedBeacons","Password -> "+ Password);
                    String db_connect = preferences.getString("pref_db_string", "");
                    Log.i("rangedBeacons","db_connect -> "+ db_connect);

                    //La lista dei Beacons la
                    for(Beacon beacon : beacons){
                        listaB.add(beacon);
                        double distance = beacon.getDistance();

                        String where = "" +
                                DatabaseHelperBeacon.COLUMN_DB_CONNECT + " = '" + db_connect + "' AND " +
                                DatabaseHelperBeacon.COLUMN_UTENTE + " = '" + Login + "' AND " +
                                DatabaseHelperBeacon.COLUMN_PASSWORD + " = '" + Password + "' AND " +

                                DatabaseHelperBeacon.COLUMN_MODEL + " = '" + android.os.Build.MODEL + "' AND " +
                                DatabaseHelperBeacon.COLUMN_DATACAMPIONAMENTO + " = '" + dataN + "' AND " +
                                DatabaseHelperBeacon.COLUMN_ANDROID + " = " + android.os.Build.VERSION.SDK_INT + " AND " +
                                DatabaseHelperBeacon.COLUMN_LIVELLOBATTERIA + " = " + getBatteryLevel() + " AND " +
                                DatabaseHelperBeacon.COLUMN_TEMPOCAMPIONAMENTO + " = " + Costanti.timeScanSec + " AND " +
                                DatabaseHelperBeacon.COLUMN_NUMEROVERSIONE + " = " + Costanti.versionCode + " AND " +
                                DatabaseHelperBeacon.COLUMN_BEACON_MAC + " = '" + beacon.getBluetoothAddress() + "' AND " +
                                DatabaseHelperBeacon.COLUMN_BEACON_DISTANCE + " = " + distance + " AND " +
                                DatabaseHelperBeacon.COLUMN_BEACON_TXPOWER + " = " + beacon.getTxPower() + " AND " +
                                DatabaseHelperBeacon.COLUMN_BEACON_RSSI + " = " + beacon.getRssi() + " AND " +
                                DatabaseHelperBeacon.COLUMN_BIT + " = 0";
                        Log.d("rangedBeacons","where -> " + where);
                        Cursor cursor = getContentResolver().query(BeaconProvider.CONTENT_URI,
                                new String[] {DatabaseHelperBeacon.COLUMN_ID},where,null,null);
                        Log.d("rangedBeacons","Cursor -> " + cursor.getCount());
                        //Se Record già presente in DB non lo rimetto
                        if(cursor.getCount() <= 5){//Inserisco record
                            Log.d("rangedBeacons_check","Inserisco valore nel DB ->" + beacon.getBluetoothAddress());
                            ContentValues values = new ContentValues();

                            values.put(DatabaseHelperBeacon.COLUMN_DB_CONNECT, db_connect);
                            values.put(DatabaseHelperBeacon.COLUMN_UTENTE, Login);
                            values.put(DatabaseHelperBeacon.COLUMN_PASSWORD, Password);
                            values.put(DatabaseHelperBeacon.COLUMN_MODEL, android.os.Build.MODEL);
                            values.put(DatabaseHelperBeacon.COLUMN_DATACAMPIONAMENTO, dataN);
                            values.put(DatabaseHelperBeacon.COLUMN_ANDROID, android.os.Build.VERSION.SDK_INT);
                            values.put(DatabaseHelperBeacon.COLUMN_LIVELLOBATTERIA, getBatteryLevel());
                            values.put(DatabaseHelperBeacon.COLUMN_TEMPOCAMPIONAMENTO, Costanti.timeScanSec);
                            values.put(DatabaseHelperBeacon.COLUMN_NUMEROVERSIONE, Costanti.versionCode);
                            values.put(DatabaseHelperBeacon.COLUMN_BEACON_MAC, beacon.getBluetoothAddress());
                            values.put(DatabaseHelperBeacon.COLUMN_BEACON_DISTANCE, distance);
                            values.put(DatabaseHelperBeacon.COLUMN_BEACON_TXPOWER, beacon.getTxPower());
                            values.put(DatabaseHelperBeacon.COLUMN_BEACON_RSSI, beacon.getRssi());
                            values.put(DatabaseHelperBeacon.COLUMN_BIT, 0);
                            values.put(DatabaseHelperBeacon.COLUMN_ANDROID_ID, "");

                            Log.d("rangedBeacons","Sto per inserire -> " + values.toString());

                            Uri uri = getContentResolver().insert(BeaconProvider.CONTENT_URI, values);
                            //Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
                            assert uri != null;
                            Log.i("rangedBeacons",uri.toString());
                            i++;
                        }
                        else{
                            //Non faccio niente
                            Log.d("rangedBeacons_check","Valore già nel DB");
                        }
                        cursor.close();
                    }

                    Log.i("rangedBeacons","Quanti Cicli Ho Effettuato -> "+i);
                }
                else{
                    //Se beacons.size() = 0, 'BLOCCO'
                    Log.i("Blocco","Implementazione del file reportBlocchi.lm a data -> " + dataN);
                    MonitoringActivity.createReportBlocchi(dataN);
                }

                //Inserisco RECORD FINTO x FINE CICLATA
                ContentValues record = new ContentValues();
                record.put(DatabaseHelperBeacon.COLUMN_DB_CONNECT, "");
                record.put(DatabaseHelperBeacon.COLUMN_UTENTE, "");
                record.put(DatabaseHelperBeacon.COLUMN_PASSWORD, "");
                record.put(DatabaseHelperBeacon.COLUMN_MODEL, android.os.Build.MODEL);
                record.put(DatabaseHelperBeacon.COLUMN_DATACAMPIONAMENTO, dataN);
                record.put(DatabaseHelperBeacon.COLUMN_ANDROID, android.os.Build.VERSION.SDK_INT);
                record.put(DatabaseHelperBeacon.COLUMN_LIVELLOBATTERIA, getBatteryLevel());
                record.put(DatabaseHelperBeacon.COLUMN_TEMPOCAMPIONAMENTO, Costanti.timeScanSec);
                record.put(DatabaseHelperBeacon.COLUMN_NUMEROVERSIONE, Costanti.versionCode);
                record.put(DatabaseHelperBeacon.COLUMN_BEACON_MAC,"XXXXXXXXXXXXXXXXX");
                record.put(DatabaseHelperBeacon.COLUMN_BEACON_DISTANCE,"");
                record.put(DatabaseHelperBeacon.COLUMN_BEACON_TXPOWER,beacons.size());//Numero Beacon visti nel valore tarocco
                record.put(DatabaseHelperBeacon.COLUMN_BEACON_RSSI,0);
                record.put(DatabaseHelperBeacon.COLUMN_BIT, 0);
                record.put(DatabaseHelperBeacon.COLUMN_ANDROID_ID, "");

                getContentResolver().insert(BeaconProvider.CONTENT_URI,record);

                logToDisplay(getString(R.string.dataScansione)+": " + dataN, listaB);
                //Toast.makeText(getBaseContext(), "Scanner -> " + dataN + " - " + beacons.size(), Toast.LENGTH_LONG).show();
            }
        };

        try {
            beaconManager.addRangeNotifier(rangeNotifier);
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) { Log.e(TAG, "startRangingBeaconsInRegion: "+e);  }
    }

    private void sendNotification() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setContentTitle(String.valueOf(getString(R.string.avvBeaconVicini)))
                        .setContentText("")
                        .setSmallIcon(R.mipmap.ic_launcher);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(new Intent(this, MonitoringActivity.class));
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.notify(1, builder.build());
    }

    public void setMonitoringActivity(MonitoringActivity activity) {
        this.monitoringActivity = activity;
    }

    public void logToDisplay(String dataScansione, ArrayList<Beacon> listaBeacon) {
        if (this.monitoringActivity != null) {this.monitoringActivity.updateLog(dataScansione, listaBeacon);}
    }

    private String getDateNow(){
        String currentTime;
        Date dt = new Date();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        currentTime = sdf.format(dt);
        return  currentTime;
    }

    public int getBatteryLevel(){
        BatteryManager bm = (BatteryManager)getSystemService(BATTERY_SERVICE);
        assert bm != null;
        return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
    }

}