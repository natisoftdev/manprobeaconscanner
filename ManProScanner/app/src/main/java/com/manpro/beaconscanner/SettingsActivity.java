package com.manpro.beaconscanner;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.manpro.beaconscanner.connection.CheckConnectionToServer;
import com.manpro.beaconscanner.database.DatabaseHelperBeacon;
import com.manpro.beaconscanner.database.DownloadBeaconAmbienti;
import com.manpro.beaconscanner.dati.Beacon;
import com.manpro.beaconscanner.processi.SendNotification;
import com.manpro.beaconscanner.servizio.NotificationService;

import java.util.List;

public class SettingsActivity extends PreferenceActivity {

    SettingsActivity settingsActivity;
    //SendNotification sendNotification;
    String set_Login;
    String set_Password;
    String set_db_connect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.activity_settings);

        PreferenceManager pm = getPreferenceManager();

        settingsActivity = this;
        String versionName = "";
        try { versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName; }
        catch (PackageManager.NameNotFoundException e) { Log.e("Preferenze", ""+e); }

        Preference editTextPref = pm.findPreference("versionNumberUser");
        assert editTextPref != null;
        editTextPref.setSummary(versionName);
/*
        final EditTextPreference pref_indirizzoInvio = (EditTextPreference) pm.findPreference("pref_indirizzoInvio");
        assert pref_indirizzoInvio != null;
        pref_indirizzoInvio.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100ms
                        new DownloadBeaconAmbienti();
                    }
                }, 100);
                return true;
            }
        });
*/

        final EditTextPreference etp1 = (EditTextPreference) pm.findPreference("pref_stringa_connessione");
        final EditTextPreference etp2 = (EditTextPreference) pm.findPreference("pref_db_login");
        final EditTextPreference etp3 = (EditTextPreference) pm.findPreference("pref_db_password");

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MonitoringActivity.monitoringActivity);

        //String indirizzoInvio = preferences.getString("pref_indirizzoInvio", "");
        //Log.i("settingsActivity","indirizzoInvio -> "+indirizzoInvio);
        set_Login = preferences.getString("pref_db_login", "");
        set_Password = preferences.getString("pref_db_password", "");
        set_db_connect = preferences.getString("pref_db_string", "");

        assert etp1 != null;
        etp1.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100ms
                        new CheckConnectionToServer();
                        Log.i("settingsActivity","Login -> "+ set_Login);
                        Log.i("settingsActivity","Password -> "+ set_Password);
                        Log.i("settingsActivity","db_connect -> "+ set_db_connect);
                        if((set_Login.length() > 0) && (set_Password.length() > 0) && (set_Password.length() > 0)) {
                            new DownloadBeaconAmbienti();
                        }
                    }
                }, 100);
                return true;
            }
        });

/*
        final EditTextPreference etp1 = (EditTextPreference) pm.findPreference("pref_db_string");
        assert etp1 != null;
        etp1.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100ms
                        new DownloadBeaconAmbienti();
                    }
                }, 100);
                return true;
            }
        });
*/
        assert etp2 != null;
        etp2.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100ms
                        Log.i("settingsActivity","Login -> "+ set_Login);
                        Log.i("settingsActivity","Password -> "+ set_Password);
                        Log.i("settingsActivity","db_connect -> "+ set_db_connect);
                        if((set_Login.length() > 0) && (set_Password.length() > 0) && (set_Password.length() > 0)) {
                            new DownloadBeaconAmbienti();
                        }
                    }
                }, 100);
                return true;
            }
        });

        assert etp3 != null;
        etp3.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100ms
                        //Log.i("settingsActivity","Login -> "+ set_Login);
                        //Log.i("settingsActivity","Password -> "+ set_Password);
                        //Log.i("settingsActivity","db_connect -> "+ set_db_connect);
                        if((set_Login.length() > 0) && (set_Password.length() > 0) && (set_Password.length() > 0)) {
                            new DownloadBeaconAmbienti();
                        }
                    }
                }, 100);
                return true;
            }
        });

        final EditTextPreference etp4 = (EditTextPreference) pm.findPreference("pref_time_scan");
        assert etp4 != null;
        etp4.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                //MonitoringActivity.monitoringActivity.onDestroy();
                //MonitoringActivity.monitoringActivity.recreate();
                return true;
            }
        });

        final EditTextPreference pref_controllo_notifiche = (EditTextPreference) pm.findPreference("pref_controllo_notifiche");
        assert pref_controllo_notifiche != null;
        pref_controllo_notifiche.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("pref_controllo_notifiche", newValue.toString());
                editor.commit();

                //Stoppo il servizio della Notifca
                //NotificationService.notificationService.onDestroy();
                //Riavvio il resvizio della Notifica
                //NotificationService.notificationService.onCreate();
                //sendNotification.deleteTimer();
                //Log.i("SendNotification","da settingsCl -> " +MonitoringActivity.monitoringActivity.sendNotification.getIdTimer());
                
                //Log.i("SendNotification","da settingsCl -> " +MonitoringActivity.monitoringActivity.sendNotification.getState());
                //MonitoringActivity.monitoringActivity.sendNotification = new SendNotification();
                //MonitoringActivity.monitoringActivity.sendNotification.start();
                //Log.i("SendNotification","da settingsCl -> " +MonitoringActivity.monitoringActivity.sendNotification.getState());
                //Log.i("SendNotification","da settingsCl post start -> " +MonitoringActivity.monitoringActivity.sendNotification.getIdTimer());
                return true;
            }
        });

        Preference updateBA = pm.findPreference("updateBA");
        assert updateBA != null;
        updateBA.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Mostro Messaggio usando Toast
                        Toast.makeText(getApplicationContext() , "Download in corso dei Dati degli Ambienti", Toast.LENGTH_SHORT).show();
                        //Do something after 100ms
                        new DownloadBeaconAmbienti();
                    }
                }, 100);
                return true;
            }
        });

        Preference deleteDB = pm.findPreference("deleteDB");
        assert deleteDB != null;
        deleteDB.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MonitoringActivity.monitoringActivity);
                //String Login = preferences.getString("pref_db_login", "");
                //String Password = preferences.getString("pref_db_password", "");
                int ruolo = preferences.getInt("ruolo",0);
                //final DatabaseHelperBeacon dhB = new DatabaseHelperBeacon(MonitoringActivity.monitoringActivity);
                final DatabaseHelperBeacon dhB = DatabaseHelperBeacon.getInstance(MonitoringActivity.monitoringActivity);

                List<Beacon> list = dhB.getBeacon();
                int size = list.size();
                //Log.i("Impostazioni", "Numero record in DB: "+size);

                //Log.d("Impostazioni", "Pre Cancello DB");
                //Controllo i permessi
                if(
                        //(Login.equals("monand") && Password.equals("paolamen")) ||
                                //(Login.equals("rossi") && Password.equals("1"))
                        ruolo == 1 // -> admin
                ){
                    //Log.d("Impostazioni", "Sono Utente coi permessi");

                    new AlertDialog.Builder(settingsActivity)
                            .setTitle("Database").setMessage("Sei sicuro di voler ripulire il Database")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //Log.d("Impostazioni", "Sto per cancellare DB");
                                    dhB.deleteAllDB();
                                    //Log.d("Impostazioni", "Ho cancellato DB");
                                    List<Beacon> list2 = dhB.getBeacon();
                                    int size2 = list2.size();
                                    //Log.i("Impostazioni", "Numero record in DB: "+size2);
                                    //Log.d("Impostazioni", "Scarico Dati Ambienti");
                                    new DownloadBeaconAmbienti();
                                    //Log.d("Impostazioni", "Ho scaricato Dati Ambienti");
                                }
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(R.drawable.ic_delete_db)
                            .create()
                            .show();
                }
                else{
                    //Messaggio: Non sei autorizzato
                    new AlertDialog.Builder(settingsActivity)
                            .setTitle("Database").setMessage("Non hai i permessi per svuotare il Database")
                            .setPositiveButton(android.R.string.yes, null)
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(R.drawable.ic_delete_db)
                            .create()
                            .show();
                }

                dhB.close();

                return true;
            }
        });

        Preference updateApp = pm.findPreference("updateApp");
        assert updateApp != null;
        updateApp.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(Intent.ACTION_VIEW , Uri.parse("https://play.google.com/store/apps/details?id=com.manpro.beaconscanner"));
                startActivity(intent);
                return true;
            }
        });
    }
}