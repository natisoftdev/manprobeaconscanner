package com.manpro.beaconscanner.notifica;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.manpro.beaconscanner.MonitoringActivity;
import com.manpro.beaconscanner.R;
import com.manpro.beaconscanner.database.DatabaseHelperBeacon;
import com.manpro.beaconscanner.dati.Costanti;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CreazioneNotifica {

    private static final String TAG = "CreazioneNotifica";
    SharedPreferences preferences;

    public CreazioneNotifica() {
        preferences = PreferenceManager.getDefaultSharedPreferences(MonitoringActivity.monitoringActivity);
        String indirizzoInvio = preferences.getString("pref_indirizzoInvio", "");
        String Login = preferences.getString("pref_db_login", "");
        String Password = preferences.getString("pref_db_password", "");
        String db_connect = preferences.getString("pref_db_string", "");
        Log.d(TAG,"- " + indirizzoInvio + " - " + Login + " - " + Password + " - " + db_connect);
        String url = indirizzoInvio;
        Log.d(TAG,"url -> " + url);
        String addsComplete;
        //addsComplete = Costanti.checkPortalAddress(url,MonitoringActivity.monitoringActivity) + Costanti.download_beacon_ambienti;
        addsComplete = url + Costanti.port + Costanti.send_notification_request;
        Log.d(TAG,"addsComplete -> " + addsComplete);
        RequestQueue reQueue = Volley.newRequestQueue(MonitoringActivity.monitoringActivity);
        StringRequest request=new StringRequest(com.android.volley.Request.Method.POST,
                addsComplete,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Cosa ricevo -> response: "+response);
                        try {
                            JSONObject jsonObjectResponse = new JSONObject(response);
                            String Messaggio = jsonObjectResponse.getString("Messaggio");
                            Log.d(TAG, "Cosa ricevo -> Messaggio: "+Messaggio);

                            JSONObject jsonObjRuolo = new JSONObject(Messaggio);

                            Costanti.messaggioNotifica = jsonObjRuolo.getString("result");
                            Log.d(TAG, "Messaggio: " + Costanti.messaggioNotifica);
                            //SharedPreferences.Editor editor = preferences.edit();
                            //editor.putInt("ruolo", ruolo);
                            //editor.commit();

                            //Log.d(TAG, "Devo salvare nelle preferenze alla voce ruolo -> "+ruolo);

                            //JSONArray jsonArray = new JSONArray(BeaconAmbientiJson);
                           /*
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject RuoloJson = jsonArray.getJSONObject(0);
                            JSONObject BeaconAmbientiJson = jsonArray.getJSONObject(0);

                            JSONArray RuoloJson = jsonArray.getJSONArray(0);
                            JSONArray BeaconAmbientiJson = jsonArray.getJSONArray(1);
                            Log.d(TAG, "Cosa ricevo -> RuoloJson: "+RuoloJson);
                            Log.d(TAG, "Cosa ricevo -> BeaconAmbientiJson: "+BeaconAmbientiJson);

                            //JSONObject jsonObject = RuoloJson.getJSONObject(i);
                            int ruolo = RuoloJson.getJSONObject(0).getInt("ruolo");
                            Log.d(TAG, "Devo salvare nelle preferenze alla voce ruolo -> Ciao a tutti"+ruolo);


                            for (int i = 0; i < BeaconAmbientiJson.length(); i++) {
                                JSONObject jsonObject = BeaconAmbientiJson.getJSONObject(i);

                            }
*/
                        } catch (JSONException e) {
                            e.printStackTrace();
                            //Toast.makeText(MonitoringActivity.monitoringActivity, "Attenzione!! Controlla le Credenziali, login fallito", Toast.LENGTH_SHORT).show();
                        }

                        // Creo Notifica
                        Log.i(TAG,"Creo Notifica");
                        createNotifica();

                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(
                            VolleyError error) {
                        Log.e(TAG, ""+error);
                        error.printStackTrace();
                        //Toast.makeText(MonitoringActivity.monitoringActivity, MonitoringActivity.monitoringActivity.getResources().getString(R.string.copy_err), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json; charset=ansi");

                String Login = preferences.getString("pref_db_login", "");
                String Password = preferences.getString("pref_db_password", "");
                String db_connect = preferences.getString("pref_db_string", "");
                String minuti = preferences.getString("pref_controllo_notifiche", "");

                if(minuti.length() <= 0){
                    minuti = "5";
                }

                params.put("Login", ""+Login);
                params.put("Password", ""+Password);
                params.put("db_connect", ""+db_connect);
                params.put("minuti", ""+minuti);
                Log.d(TAG, "params: " + params);
                return params;
            }
        };

        try{ reQueue.add(request); }
        catch(Exception e){ Log.e(TAG, ""+e); }
    }

    private void createNotifica(){
        Log.i(TAG,"Messaggio -> " + Costanti.messaggioNotifica);

        if( (Costanti.messaggioNotifica != null) && (Costanti.messaggioNotifica.length() > 0)){
            NotificationCompat.Builder mBuilder2 = new NotificationCompat.Builder(MonitoringActivity.monitoringActivity,"AlertMessage")
                    //.setDefaults(Notification.DEFAULT_ALL)
                    .setSmallIcon(R.drawable.ic_alert)
                    .setContentTitle("Avviso Richiesta")
                    .setContentText(Costanti.messaggioNotifica)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(Costanti.messaggioNotifica))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name  = "Richiesta";
                String description = "Notifica Richiesta";

                int importance = NotificationManager.IMPORTANCE_MAX;
                @SuppressLint("WrongConstant")
                NotificationChannel channel = new NotificationChannel("AlertMessage", name, importance);

                channel.setDescription(description);
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                NotificationManager notificationManager = MonitoringActivity.monitoringActivity.getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);

            }

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MonitoringActivity.monitoringActivity);
            notificationManager.notify(232, mBuilder2.build());
            Costanti.messaggioNotifica = "";
        }
    }
}