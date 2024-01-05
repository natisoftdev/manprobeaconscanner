package com.manpro.beaconscanner.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
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
import com.manpro.beaconscanner.provider.BeaconProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DownloadBeaconAmbienti{

    private static final String TAG = "DownloadBeaconAmbienti";

    DatabaseHelperBeacon dbHelper;
    //SQLiteDatabase db;
    SharedPreferences preferences;

    public DownloadBeaconAmbienti() {
        //dbHelper = new DatabaseHelperBeacon(MonitoringActivity.monitoringActivity);
        dbHelper = DatabaseHelperBeacon.getInstance(MonitoringActivity.monitoringActivity);
        //db = dbHelper.getWritableDatabase();
        preferences = PreferenceManager.getDefaultSharedPreferences(MonitoringActivity.monitoringActivity);

        String indirizzoInvio = preferences.getString("pref_indirizzoInvio", "");
        String Login = preferences.getString("pref_db_login", "");
        String Password = preferences.getString("pref_db_password", "");
        String db_connect = preferences.getString("pref_db_string", "");

        //Log.d(TAG,"- " + indirizzoInvio + " - " + Login + " - " + Password + " - " + db_connect);

        String url = indirizzoInvio;
        Log.d(TAG,"url -> " + url);
        String addsComplete;

        //addsComplete = Costanti.checkPortalAddress(url,MonitoringActivity.monitoringActivity) + Costanti.download_beacon_ambienti;
        addsComplete = url + Costanti.port + Costanti.download_beacon_ambienti;
        Log.d(TAG,"addsComplete -> " + addsComplete);

        RequestQueue reQueue = Volley.newRequestQueue(MonitoringActivity.monitoringActivity);

        StringRequest request=new StringRequest(com.android.volley.Request.Method.POST,
                addsComplete,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        //DatabaseHelperBeacon.getInstance(MonitoringActivity.monitoringActivity).deleteAllDB();
                        dbHelper.deleteAllDB();
                        //db.delete(DatabaseHelperBeacon.TBL_BEACON_AMBIENTI,null,null);

                        Log.d(TAG, "Cosa ricevo: "+response);

                        try {
                            JSONObject jsonObjectResponse = new JSONObject(response);
                            String RuoloJson = jsonObjectResponse.getString("RuoloJson");
                            String BeaconAmbientiJson = jsonObjectResponse.getString("BeaconAmbienteJson");
                            //Log.d(TAG, "Cosa ricevo -> RuoloJson: "+RuoloJson);
                            //Log.d(TAG, "Cosa ricevo -> BeaconAmbientiJson: "+BeaconAmbientiJson);

                            JSONObject jsonObjRuolo = new JSONObject(RuoloJson);
                            //JSONObject jsonObjBeaconAmbientiJson = new JSONObject(BeaconAmbientiJson);
                            JSONArray jsonArrayBeaconAmbienti = new JSONArray(BeaconAmbientiJson);
                            //Log.d(TAG, "JSONArray -> jsonArrayRuolo: "+jsonObjRuolo.toString());
                            //Log.d(TAG, "JSONArray -> jsonArrayBeaconAmbienti: "+jsonArrayBeaconAmbienti.toString());

                            int ruolo = jsonObjRuolo.getInt("ruolo");
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putInt("ruolo", ruolo);
                            editor.commit();
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
                            //Log.d(TAG, "Devo effettuare il ciclo per : " + jsonArrayBeaconAmbienti.length());
                            for (int i = 0; i < jsonArrayBeaconAmbienti.length(); i++){
                                JSONObject jsonObject = jsonArrayBeaconAmbienti.getJSONObject(i);
                                //Devo aggiungere i dati nel DB
                                ContentValues record = new ContentValues();

                                //Log.d(TAG,"-> " + jsonObject.toString());

                                int IDAmbiente = jsonObject.getInt("IDAmbiente");
                                //Log.d(TAG, "Cosa ricevo - IDAmbiente: "+IDAmbiente);
                                String CodiceAmbiente = jsonObject.getString("CodiceAmbiente");
                                //Log.d(TAG, "Cosa ricevo - CodiceAmbiente: "+CodiceAmbiente);
                                String CodiceAssAmbiente = jsonObject.getString("CodiceAssAmbiente");
                                //Log.d(TAG, "Cosa ricevo - CodiceAssAmbiente: "+CodiceAssAmbiente);
                                String DesAmbiente = jsonObject.getString("DesAmbiente");
                                //Log.d(TAG, "Cosa ricevo - DesAmbiente: "+DesAmbiente);
                                String Destinazione = jsonObject.getString("Destinazione");
                                //Log.d(TAG, "Cosa ricevo - Destinazione: "+Destinazione);
                                String Piano = jsonObject.getString("Piano");
                                //Log.d(TAG, "Cosa ricevo - Piano: "+Piano);
                                String BeaconMac = jsonObject.getString("BeaconMac");
                                //Log.d(TAG, "Cosa ricevo - BeaconMac: "+BeaconMac);
                                String BeaconMacIos = jsonObject.getString("BeaconMacIos");
                                //Log.d(TAG, "Cosa ricevo - BeaconMacIos: "+BeaconMacIos);
                                String IdUtilizzatore = jsonObject.getString("IdUtilizzatore");
                                //Log.d(TAG, "Cosa ricevo - IdUtilizzatore: "+IdUtilizzatore);
                                String Descrizione = jsonObject.getString("Descrizione");
                                //Log.d(TAG, "Cosa ricevo - Descrizione: "+Descrizione);

                                record.put(DatabaseHelperBeacon.COLUMN_IDAmbiente, IDAmbiente);
                                record.put(DatabaseHelperBeacon.COLUMN_CODICEAMBIENTE, CodiceAmbiente);
                                record.put(DatabaseHelperBeacon.COLUMN_CODICEASSAMBIENTE, CodiceAssAmbiente);
                                record.put(DatabaseHelperBeacon.COLUMN_DESAMBIENTE, DesAmbiente);
                                record.put(DatabaseHelperBeacon.COLUMN_MAC_BEACON, BeaconMac);
                                record.put(DatabaseHelperBeacon.COLUMN_UUID_BEACON, BeaconMacIos);
                                record.put(DatabaseHelperBeacon.COLUMN_DESTINAZIONEDUSO, Destinazione);
                                record.put(DatabaseHelperBeacon.COLUMN_PIANO, Piano);
                                record.put(DatabaseHelperBeacon.COLUMN_IDUTILIZZATORE, IdUtilizzatore);
                                record.put(DatabaseHelperBeacon.COLUMN_DESCUTILIZZATORE, Descrizione);

                                //db.insert(	DatabaseHelperBeacon.TBL_BEACON_AMBIENTI, "", record);
                                DatabaseHelperBeacon.getInstance(MonitoringActivity.monitoringActivity).insertRecord(record);
                            }

                            //db.close();
                            dbHelper.close();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MonitoringActivity.monitoringActivity, "Attenzione!! Controlla le Credenziali, login fallito", Toast.LENGTH_SHORT).show();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(
                            VolleyError error) {
                        Log.e(TAG, ""+error);
                        error.printStackTrace();
                        Toast.makeText(MonitoringActivity.monitoringActivity, MonitoringActivity.monitoringActivity.getResources().getString(R.string.copy_err), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json; charset=ansi");

                //Date dt = new Date();

                //@SuppressLint("SimpleDateFormat")
                //SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

                //String currentTime = sdf.format(dt);
                //params.put("DataInvio", ""+  currentTime);

                String Login = preferences.getString("pref_db_login", "");
                String Password = preferences.getString("pref_db_password", "");
                String db_connect = preferences.getString("pref_db_string", "");

                Log.d(TAG, Login + " - " + Password + " - " + db_connect);

                params.put("Login", ""+Login);
                params.put("Password", ""+Password);
                params.put("db_connect", ""+db_connect);

                return params;
            }
        };

        try{ reQueue.add(request); }
        catch(Exception e){ Log.e(TAG, ""+e); }
    }
}