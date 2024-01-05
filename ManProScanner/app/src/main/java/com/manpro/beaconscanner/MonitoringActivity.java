package com.manpro.beaconscanner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.icu.util.LocaleData;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.manpro.beaconscanner.database.DownloadBeaconAmbienti;
import com.manpro.beaconscanner.dati.Costanti;
import com.manpro.beaconscanner.processi.SendNotification;
import com.manpro.beaconscanner.recyclerView.MyRecyclerViewAdapter;
import com.manpro.beaconscanner.servizio.NotificationService;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static androidx.core.app.ServiceCompat.START_STICKY;

/**
 * @author Lorenzo Malferrari
 */
public class MonitoringActivity extends Activity /*implements BeaconConsumer*/ {

	protected static final String TAG = "MonitoringActivity";
	//private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
	private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
	public static final String REFRESH_IBEACON_INTENT = "REFRESH_IBEACON_INTENT";
	public static MonitoringActivity monitoringActivity;
	public static SendNotification sendNotification;

	private int PERMISSION_ALL = 1;
	private String[] PERMISSIONS = {
			Manifest.permission.ACCESS_COARSE_LOCATION,
			Manifest.permission.ACCESS_FINE_LOCATION,
			Manifest.permission.ACCESS_BACKGROUND_LOCATION,
			Manifest.permission.READ_PHONE_STATE,
			Manifest.permission.BLUETOOTH,
			Manifest.permission.BLUETOOTH_ADMIN,
			Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.READ_EXTERNAL_STORAGE,
			//Manifest.permission.STORAGE,
			Manifest.permission.RECEIVE_BOOT_COMPLETED,
			Manifest.permission.WAKE_LOCK,
			Manifest.permission.FOREGROUND_SERVICE
	};

	private static File myDir;
	public static final String nameFileBlocchi = "reportBlocchi.lm"; //nome del file contenente i dati di reportistica
	public static File fileReportB;
	public static String dataLast = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d("debug___" + TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_monitoring);

		monitoringActivity = this;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			// Android M Permission check
			if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.richiestaPosizione));
                builder.setMessage(getString(R.string.mexRichiestaPosizione));
                builder.setPositiveButton(getString(android.R.string.ok), null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                    @TargetApi(23)
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                PERMISSION_REQUEST_COARSE_LOCATION);

						if(!hasPermissions(monitoringActivity, PERMISSIONS)){
							ActivityCompat.requestPermissions(monitoringActivity, PERMISSIONS, PERMISSION_ALL);
						}
                    }

                });
                builder.show();
            }
/*
			if(!hasPermissions(this, PERMISSIONS)){			ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL); }
*/		}

		RecyclerView recyclerView;
		recyclerView = findViewById(R.id.listaBeacon);

		LinearLayoutManager llm = new LinearLayoutManager(this);
		llm.setOrientation(LinearLayoutManager.VERTICAL);
		recyclerView.setLayoutManager(llm);
		recyclerView.setAdapter(null);

		verifyBluetooth();

	//	if(!hasPermissions(this, PERMISSIONS)){
	//		ActivityCompat.requestPermissions(monitoringActivity, PERMISSIONS, PERMISSION_ALL);}

		Costanti.versionCode = BuildConfig.VERSION_CODE;
		Costanti.versionName = BuildConfig.VERSION_NAME;

		//Implementazione di alcuni Oggetti presenti nella activity_monitoring.xml
		//TextView textView = findViewById(R.id.textVersion);
		//textView.setText(getString(R.string.versione)+": " + Costanti.versionName);

		ImageButton btnSettings = findViewById(R.id.img_settings);
		btnSettings.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d("TEST","Ho cliccato Impostazioni");
				//Devo Aprire la SettingsActivity
				startActivity(new Intent(monitoringActivity,SettingsActivity.class));
			}
		});

		//Implementazione del percorso con cartella dove verranno salvati i file
		//String root = Environment.getExternalStorageDirectory().toString();
		String root = monitoringActivity.getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString();
		myDir = new File(root + "/log_beacon");

		Log.i("myDir",myDir.toString());

		if (!myDir.exists()) { myDir.mkdirs(); }
		createReportBlocchi(getDateNow());
		//readListFiles(myDir);

		//Log.d(TAG,"onCreate - pre DownloadBeaconAmbienti");
		//Scarico dati
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
			//Mostro Messaggio usando Toast
			//Toast.makeText(getApplicationContext() , "Download in corso dei Dati degli Ambienti", Toast.LENGTH_SHORT).show();
			//Do something after 100ms
			new DownloadBeaconAmbienti();
			}
		}, 100);
		//Log.d(TAG,"onCreate - post DownloadBeaconAmbienti");
		//
		sendNotification = new SendNotification();
		sendNotification.start();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
/*
	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		if (requestCode == PERMISSION_REQUEST_COARSE_LOCATION) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				Log.d(TAG, "coarse location permission granted");
			} else {
				final AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle(getString(R.string.permessiLimitati));
				builder.setMessage(getString(R.string.mexPermessiLimitati));
				builder.setPositiveButton(getString(android.R.string.ok), null);
				builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

					@Override
					public void onDismiss(DialogInterface dialog) {
					}

				});
				builder.show();
			}
		}
	}
*/
    @Override
    public void onResume() {
        super.onResume();
        BeaconReferenceApplication application = ((BeaconReferenceApplication) this.getApplicationContext());
        application.setMonitoringActivity(this);
		//Log.d(TAG,"onResume() - pre DownloadBeaconAmbienti");
		new DownloadBeaconAmbienti();
		//Log.d(TAG,"onResume() - post DownloadBeaconAmbienti");
    }

    @Override
    public void onPause() {
        super.onPause();
        ((BeaconReferenceApplication) this.getApplicationContext()).setMonitoringActivity(null);
    }

	private void verifyBluetooth() {

		try {
			if (!BeaconManager.getInstanceForApplication(this).checkAvailability()) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle(getString(R.string.avvBluetoothSpento));
				builder.setMessage(getString(R.string.mexAvvBlueSpento));
				builder.setPositiveButton(getString(android.R.string.ok), null);
				builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialog) {
						//finish();
			            //System.exit(0);
					}					
				});
				builder.show();
			}			
		}
		catch (RuntimeException e) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(getString(R.string.avvBluetoothAcceso));
			builder.setMessage(getString(R.string.mexAvvBlueAcceso));
			builder.setPositiveButton(getString(android.R.string.ok), null);
			builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface dialog) {
					//finish();
		            //System.exit(0);
				}
				
			});
			builder.show();
			
		}
		
	}	

    public synchronized void updateLog(final String log,final ArrayList<Beacon> listBeacon) {
    	runOnUiThread(new Runnable() {
    	    public void run() {
				TextView editText = MonitoringActivity.this.findViewById(R.id.monitoringText);
				//editText.getText().clear();
				editText.setText(log);

				RecyclerView recyclerView;
				recyclerView = findViewById(R.id.listaBeacon);

				/*
				for(Beacon beacon : listBeacon){
					Log.i("myAdapter",
							"MAC: " + beacon.getBluetoothAddress() +
							"\nDistance: " + beacon.getDistance() +
							"\nTxPower: " + beacon.getTxPower() +
							"\nRssi: " + beacon.getRssi());
				}
				 */

				MyRecyclerViewAdapter myAdapter = new MyRecyclerViewAdapter(monitoringActivity,listBeacon);
				recyclerView.setAdapter(myAdapter);
				recyclerView.setLayoutManager(new LinearLayoutManager(monitoringActivity));

    	    }
    	});
    }

	public static boolean hasPermissions(Context context, String... permissions) {
		if (context != null && permissions != null) {
			for (String permission : permissions) { if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) { return false;} }
		}
		return true;
	}

	public static synchronized void createReportBlocchi(String dateN){
		//Log.i("createReportBlocchi","DateNow -> "+dateN);
		//Log.i("createReportBlocchi","DateLast -> "+dataLast);
		//se dataLast è nulla
		if(dataLast.isEmpty()){ dataLast = dateN; }
		//Log.i("createReportBlocchi","DENTRO");
		String today = dateN.split(" ")[0]; //prendo la prima parte della stringa ovvero dd-mm-yyyy
		//Log.i("createReportBlocchi","DATA OGGI -> "+today);

		//File filesDir = monitoringActivity.getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
		fileReportB = new File( myDir, today + "_" + nameFileBlocchi );
		//fileReportB = new File(filesDir, today + "_" + nameFileBlocchi);

		//Log.i("createReportBlocchi","Nome del file -> "+fileReportB);
		//Log.i("createReportBlocchi","File esiste già -> "+(fileReportB.length() == 0 || !fileReportB.exists()));
		if ( !fileReportB.exists() ){//If file è vuoto || non esistente
			try {
//				if (!fileReportB.exists()) {
//					if (!fileReportB.createNewFile()) {
//						throw new IOException("Cant able to create file");
//					}
//				}
				OutputStream os = new FileOutputStream(fileReportB);
				String text = dateN+"\n";
				Log.d("createReportBlocchi", "Testo inserito -> " + text);
				byte[] data = text.getBytes();
				os.write(data);
				os.close();
				Log.d("createReportBlocchi", "File Path= " + fileReportB.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}

			/*

			//Log.i("createReportBlocchi","File vuoto o non esistente inserisco il valore -> "+dateN);
			//Creo file e aggiungo dateNow
			try {
				fileReportB.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			writeToFile(fileReportB,dateN+"\n");

			*/
		}
		else{//Else file esiste
			//String dateLastReportBlocchi = getLastDate(fileReportB); //Ultima data inserita nel file reportBlocchi.lm
			//Log.i("createReportBlocchi","File esistente --- DateNow -> "+dateN);
			//Log.i("createReportBlocchi","File esistente --- DateLast -> "+dataLast);
			//Log.i("createReportBlocchi","Differenza tra le due date -> "+diffBetweenTwoDates(dateN,dataLast));
			//Log.i("createReportBlocchi","Differenza tra le due date maggiore di 3 minuti-> "+(diffBetweenTwoDates(dateN,dataLast) >= (Costanti.timeErrorSec)));
			if(diffBetweenTwoDates(dateN,dataLast) >= (Costanti.timeErrorSec)){//Se la differenza tra dateNow e dateLast è >= 3 minuti, scatta l'errore
				long maxSize = (Costanti.maxSizeFile * (1024L * 1024L)); // Corrisponde al valore 22 MB
				//Log.i("createReportBlocchi", "Dimensione del file MAX -> "+maxSize+" B"); //Espresso in B
				//Log.i("createReportBlocchi", "Dimensione del file MAX -> "+String.valueOf((maxSize / (1024L * 1024L)))+" MB"); //Espresso in MB
				//Log.i("createReportBlocchi", "Dimensione del file ATTUALMENTE -> "+fileReportB.length()+" B"); //Espresso in B
				//Log.i("createReportBlocchi", "Dimensione del file ATTUALMENTE -> "+String.valueOf((fileReportB.length() / (1024L * 1024L)))+" MB"); //Espresso in B
				//Verifico la dimensione del file reportBlocchi -> MAX 22 MB per poter essere inviato via EMAIL
				if(fileReportB.length() > maxSize){ //Se valore è maggiore di 22 MB
					//Cancello il file
					boolean success = fileReportB.delete();
					//Controllo se il file è stato cancellato correttamente
					if (!success) { //Se il valore booleano è false mando errore
						Log.e("createReportBlocchi", "Attenzione!! Cancellazione fallita");
						throw new IllegalArgumentException("Attenzione!! Cancellazione fallita");
					}
					else {
						//Log.i("createReportBlocchi", "Cancellazione eseguita");
						//Ricreo il file e aggiungo dateNow
						writeToFile(fileReportB,dateN+"\n");
						dataLast = dateN;
					}
				}
				else{
					//Aggiungo dateNow
					writeToFile(fileReportB,dateN+"\n");
					dataLast = dateN;
					Log.i("createReportBlocchi", "Data aggiunta al file reportBlocchi");
				}
			}
			else{
				Log.i("createReportBlocchi","Continua il ciclo --- DateLast -> "+dataLast);
			}
		}
	}

	private String getDateNow(){
		String currentTime;
		Date dt = new Date();
		@SuppressLint("SimpleDateFormat")
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		currentTime = sdf.format(dt);
		return currentTime;
	}

	private static synchronized void writeToFile(File fileName, String message){
		try {
			Log.i("writeToFile","Data inserita");
			FileOutputStream out = new FileOutputStream(fileName, true);
			out.write(message.getBytes());
			out.flush();
			out.close();
		} catch (Exception e) {
			Log.e("writeToFile","File "+fileName+" non creato");
			e.printStackTrace();
		}
	}

	private static int diffBetweenTwoDates(String dN, String dL){
		int seconds = Integer.MAX_VALUE;
		//Calcolo la differenza
		@SuppressLint("SimpleDateFormat")
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		// Parses the two strings.
		try {
			Date d1 = sdf.parse(dN);
			//Log.i("DBSQL_diffBTDates","Data Now "+dN);
			Date d2 = sdf.parse(dL);
			//Log.i("DBSQL_diffBTDates","Data Last "+dL);

			// Calculates the difference in milliseconds.
			assert d1 != null;
			assert d2 != null;
			long millisDiff = d1.getTime() - d2.getTime();
			//Log.i("DBSQL_diffBTDates","Differenza: " + millisDiff+" Millisecondi");
			seconds = (int) (millisDiff / 1000);
			//Log.i("DBSQL_diffBTDates","Differenza: " + seconds+" Secondi");
		} catch (ParseException e) {
			Log.e("DBSQL_diffBTDates","Impossibile fare la conversione "+e);
			e.printStackTrace();
		}
		return seconds;
	}
/*
	private static void readListFiles(File listaFile){
		//Log.i("readListFiles","Leggo CARTELLA");
		File[] listOfFiles = listaFile.listFiles();
		if(listOfFiles != null) {
			for (int i = 0; i < listOfFiles.length; i++) {
				//Log.i("readListFiles","Nome Path -> "+listOfFiles[i].getAbsolutePath());
				//Log.i("readListFiles","Nome FIlE -> "+listOfFiles[i].getName());
				//readFile(listOfFiles[i]);
			}
		}
	}

	private static void readFile(File file){
		//String date = "";
		FileReader f;
		try {
			f = new FileReader(file);
			BufferedReader b = new BufferedReader(f);
			String s = "";

			while(s != null) {
				try {
					s = b.readLine();
					//Log.i("Lettura FILE",file.getName()+" -> "+s);
					//if(s != null) date = s;
				} catch (IOException e) {e.printStackTrace();}
			}
		} catch (FileNotFoundException e) {e.printStackTrace();}
	}
*/
}