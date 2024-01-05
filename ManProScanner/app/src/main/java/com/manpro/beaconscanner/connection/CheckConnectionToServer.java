package com.manpro.beaconscanner.connection;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
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
import com.manpro.beaconscanner.database.DatabaseHelperBeacon;
import com.manpro.beaconscanner.dati.Costanti;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CheckConnectionToServer {
    private static final String TAG = "CheckConnectionToServer";
    //DatabaseHelperBeacon dbHelper;
    //SQLiteDatabase db;
    SharedPreferences preferences;

    public CheckConnectionToServer() {
        //dbHelper = new DatabaseHelperBeacon(MonitoringActivity.monitoringActivity);
        //db = dbHelper.getWritableDatabase();
        preferences = PreferenceManager.getDefaultSharedPreferences(MonitoringActivity.monitoringActivity);
        String Login = preferences.getString("pref_db_login", "");
        String Password = preferences.getString("pref_db_password", "");
        //String indirizzoInvio = preferences.getString("pref_indirizzoInvio", "");
        //String db_connect = preferences.getString("pref_db_string", "");
        String pref_stringa_connessione = preferences.getString("pref_stringa_connessione", "");
        Log.d(TAG,"- " + pref_stringa_connessione);
        //String url = Costanti.host +  Costanti.attracco_android;
        //Log.d(TAG,"url -> " + url);
        String addsComplete = Costanti.host +  Costanti.attracco_android;
        //addsComplete = Costanti.checkPortalAddress(url,MonitoringActivity.monitoringActivity) + Costanti.download_beacon_ambienti;
        //addsComplete = url + Costanti.download_beacon_ambienti;
        Log.d(TAG,"addsComplete -> " + addsComplete);

        RequestQueue reQueue = Volley.newRequestQueue(MonitoringActivity.monitoringActivity);

        StringRequest request=new StringRequest(com.android.volley.Request.Method.POST,
                addsComplete,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        //db.delete(DatabaseHelperBeacon.TBL_BEACON_AMBIENTI,null,null);

                        Log.d(TAG, "Cosa ricevo: "+response);

                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            //for (int i = 0; i < jsonArray.length(); i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                //Devo aggiungere i dati nel DB
                                //ContentValues record = new ContentValues();

                                Log.d(TAG,jsonObject.toString());

                                String indirizzoPortale = jsonObject.getString("indirizzoPortale");
                                String indirizzoPortaleEncoding = jsonObject.getString("indirizzoPortaleEncoding");
                                String NameOdbc = jsonObject.getString("NameOdbc");

                                Log.d(TAG,indirizzoPortale + " - " + indirizzoPortaleEncoding + " - " + NameOdbc);

                                //Devo Salvare Dominio di indirizzoPortale in pref_indirizzoInvio
                                String dominio = Costanti.extractDomain(indirizzoPortale);

                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("pref_indirizzoInvio", dominio);
                                editor.putString("pref_db_string", NameOdbc);
                                editor.commit();

                                String indirizzoInvio = preferences.getString("pref_indirizzoInvio", dominio);
                                //Devo Salvare NameOdbc in
                                String db_connect = preferences.getString("pref_db_string", NameOdbc);

                                Log.d(TAG,indirizzoInvio  + " - " + indirizzoPortaleEncoding + " - " + db_connect);
/*
                                int IDAmbiente = jsonObject.getInt("IDAmbiente");
                                //Log.d(TAG, "Cosa ricevo: "+IDAmbiente);
                                String CodiceAmbiente = jsonObject.getString("CodiceAmbiente");
                                //Log.d(TAG, "Cosa ricevo: "+CodiceAmbiente);
                                String CodiceAssAmbiente = jsonObject.getString("CodiceAssAmbiente");
                                //Log.d(TAG, "Cosa ricevo: "+CodiceAssAmbiente);
                                String Destinazione = jsonObject.getString("Destinazione");
                                //Log.d(TAG, "Cosa ricevo: "+Destinazione);
                                String Piano = jsonObject.getString("Piano");
                                Log.d(TAG, "Cosa ricevo: "+Piano);
                                String BeaconMac = jsonObject.getString("BeaconMac");
                                //Log.d(TAG, "Cosa ricevo: "+BeaconMac);
                                String BeaconMacIos = jsonObject.getString("BeaconMacIos");
                                //Log.d(TAG, "Cosa ricevo: "+BeaconMacIos);
                                String IdUtilizzatore = jsonObject.getString("IdUtilizzatore");
                                //Log.d(TAG, "Cosa ricevo: "+IdUtilizzatore);
                                String Descrizione = jsonObject.getString("Descrizione");
                                //Log.d(TAG, "Cosa ricevo: "+Descrizione);

                                record.put(DatabaseHelperBeacon.COLUMN_IDAmbiente, IDAmbiente);
                                record.put(DatabaseHelperBeacon.COLUMN_CODICEAMBIENTE, CodiceAmbiente);
                                record.put(DatabaseHelperBeacon.COLUMN_CODICEASSAMBIENTE, CodiceAssAmbiente);
                                record.put(DatabaseHelperBeacon.COLUMN_MAC_BEACON, BeaconMac);
                                record.put(DatabaseHelperBeacon.COLUMN_UUID_BEACON, BeaconMacIos);
                                record.put(DatabaseHelperBeacon.COLUMN_DESTINAZIONEDUSO, Destinazione);
                                record.put(DatabaseHelperBeacon.COLUMN_PIANO, Piano);
                                record.put(DatabaseHelperBeacon.COLUMN_IDUTILIZZATORE, IdUtilizzatore);
                                record.put(DatabaseHelperBeacon.COLUMN_DESCUTILIZZATORE, Descrizione);

                                db.insert(	DatabaseHelperBeacon.TBL_BEACON_AMBIENTI, "", record);
 */
                            //}
                        } catch (JSONException e) {
                            e.printStackTrace();
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
                //params.put("Content-Type", "application/json; charset=ansi");
                params.put( "Content-Type", "application/x-www-form-urlencoded");
                params.put( "charset", "utf-8");
/*
                String Login = preferences.getString("pref_db_login", "");
                String Password = preferences.getString("pref_db_password", "");
                String db_connect = preferences.getString("pref_db_string", "");
*/
                String StringaAccesso = preferences.getString("pref_stringa_connessione", "");

                //Log.d(TAG,Costanti.getIndirizzoPortale(MonitoringActivity.monitoringActivity) + " - " + Login + " - " + Password + " - " + db_connect);
/*
                params.put("Login", ""+Login);
                params.put("Password", ""+Password);
                params.put("db_connect", ""+db_connect);
*/
                params.put("StringaAccesso", ""+StringaAccesso);

                return params;
            }
        };
        try{ reQueue.add(request); }
        catch(Exception e){ Log.e(TAG, ""+e); }
    }
}