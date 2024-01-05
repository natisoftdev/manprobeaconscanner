package com.manpro.beaconscanner.dati;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class Costanti {
    public static int timeScanSec = 20; // 30 secondi //Scansione dei Beacons
    public static int timeErrorSec = 180;// 3 minuti = 180 secondi || per controllom reportBlocchi
    public static int versionCode; //Codice della versione dell'applicazione
    public static String versionName;
    public static String messaggioNotifica;
    public  static int maxSizeFile = 22;// Massima dimensione del file da allegare, espresso in MB
    public static String host_sviluppo = "https://sviluppo.manpronet.com"; //185.63.229.15
    public static String host = "https://manpronet.com";
    public static String port = ":8089";
    public static String copia_appunto = "/app_mobile/copia_appunto.php";
    public static String download_beacon_ambienti = "/app_mobile/downloadBeaconAmbienti.php";
    public static String send_notification_request = "/app_mobile/notifica_app_scanner/send_notification_request.php";
    public static String attracco_android = "/attracco_android/risolutore_indirizzo/json-events-login.php";
    public static String https = "https://"; //prefisso da aagiungere 'eventualmente' alla voce Indirizzo Portale

    public static String extractDomain(String url){
        //String d = "";
        String[] parts = url.split("/mobile/");
        if(parts[0].contains("www.")){
            return parts[0].replace("www.","");
        }
        else{ return parts[0]; }
    }
}