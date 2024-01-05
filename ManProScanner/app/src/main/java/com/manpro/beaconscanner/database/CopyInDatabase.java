package com.manpro.beaconscanner.database;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.manpro.beaconscanner.MonitoringActivity;
import com.manpro.beaconscanner.R;
import com.manpro.beaconscanner.dati.Costanti;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CopyInDatabase {

    private static final String TAG = "CopyInDatabase";
    SharedPreferences preferences;
    //private static Context ctx;

    public CopyInDatabase(final Context context, final String datoTipo, final String datoValore)/*(final Beacon oneBeacon) */{
        preferences = PreferenceManager.getDefaultSharedPreferences(MonitoringActivity.monitoringActivity);
        String indirizzoInvio = preferences.getString("pref_indirizzoInvio", "");

        String url = indirizzoInvio;
        Log.d(TAG,"url -> " + url);
        String addsComplete;
        addsComplete = url + Costanti.port + Costanti.copia_appunto;
        //Log.d(TAG,"addsComplete -> " + addsComplete);
        RequestQueue reQueue = Volley.newRequestQueue(context);

        StringRequest request=new StringRequest(com.android.volley.Request.Method.POST,
                addsComplete,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        Log.d(TAG, ""+response);
                        JSONObject json;

                        try {
                            json = new JSONObject(response);
                            int val  = json.getInt("result");

                            Log.d(TAG, "valore json bottone copia MAC beacon: "+val);

                            String msg;
                            if(val==1)
                                msg = context.getResources().getString(R.string.copy_ok);
                            else
                                msg = context.getResources().getString(R.string.copy_err);

                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, ""+e);
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(
                            VolleyError error) {
                        Log.e(TAG, ""+error);
                        error.printStackTrace();
                        Toast.makeText(context, context.getResources().getString(R.string.copy_err), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json; charset=ansi");

                Date dt = new Date();

                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

                String currentTime = sdf.format(dt);
                params.put("DataInvio", ""+  currentTime);

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                String Login = preferences.getString("pref_db_login", "");
                String Password = preferences.getString("pref_db_password", "");
                String db_connect = preferences.getString("pref_db_string", "");

                //Log.d("CopyInDatabase",Costanti.getIndirizzoPortale(context) + " - " + Login + " - " + Password + " - " + db_connect + " - " + currentTime + " - " + datoTipo + " - " + datoValore);

                params.put("Login", ""+Login);
                params.put("Password", ""+Password);
                params.put("db_connect", ""+db_connect);

                params.put("datoTipo", ""+datoTipo);
                params.put("datoValore", ""+datoValore);

                return params;
            }
        };

        try{ reQueue.add(request); }
        catch(Exception e){ Log.e(TAG, ""+e); }
    }
}