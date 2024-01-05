package com.manpro.beaconscanner.recyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.manpro.beaconscanner.R;
import com.manpro.beaconscanner.database.CopyInDatabase;
import com.manpro.beaconscanner.database.DatabaseHelperBeacon;
import com.manpro.beaconscanner.provider.BeaconProvider;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder> {

    private ArrayList<Beacon> listaBeacon;
    private Context context;

    DatabaseHelperBeacon dbHelper;
    SQLiteDatabase db;
    SharedPreferences preferences;

    public MyRecyclerViewAdapter(Context ct, ArrayList<Beacon> listaB){
        context = ct;
        listaBeacon = listaB;

        //dbHelper = new DatabaseHelperBeacon(context);
        dbHelper = DatabaseHelperBeacon.getInstance(context);

        if(dbHelper != null ){
            dbHelper.close();
        }

        db = dbHelper.getWritableDatabase();

        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //Costruisco la CardView da visualizzare nella Activity
        final String macB = listaBeacon.get(position).getBluetoothAddress();
        boolean flagBeaconNoti = preferences.getBoolean("pref_beacon_noti",false);
        //Log.d("CardView","flagBeaconNoti -> " + flagBeaconNoti);
        //Se il macB percepito è un Beacon Associato ad un Vano
        //Log.d("CardView",macB + " -> getInfoAmbiente" + getInfoAmbiente(macB));

        if(!getInfoAmbiente(macB).isEmpty()){/*Costruisco Interfaccia BeaconAssociato*/ layoutBeaconAssociato(holder,position); }
        else if(!flagBeaconNoti){/*Costruisco Interfaccia BeaconNonAssociato*/ layoutBeaconNonAssociato(holder,position); }
        else { holder.cardView.setVisibility(View.INVISIBLE); }
    }

    @SuppressLint("ResourceAsColor")
    private void setColorCardView(MyViewHolder holder/*, String idAmb*/) {
        holder.constraintLayout.setBackgroundColor(Color.rgb(145,184,15));//Verde -> 91B80F
    }

    @Override
    public int getItemCount() {
        return listaBeacon.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        //Dati Beacon
        TextView datiBeacon;
        //Bottone Copia
        ImageButton btnCopy;

        CardView cardView;
        ConstraintLayout constraintLayout;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            //ConstraintLayout
                constraintLayout = itemView.findViewById(R.id.constraintLayout);
            //CardView
                cardView = itemView.findViewById(R.id.cardView);
            //TextView
                datiBeacon = itemView.findViewById(R.id.dati_beacon);
            //init Button
                btnCopy = itemView.findViewById(R.id.myButton);
        }
    }

    private String getInfoAmbiente(String macB) {
        String datoBeacon = "";
        //Log.d("BeaconAmbienti","Beacon letto = " + macB);

        String[] tableColumns = new String[] {
                DatabaseHelperBeacon.COLUMN_IDAmbiente,
                DatabaseHelperBeacon.COLUMN_CODICEAMBIENTE,
                DatabaseHelperBeacon.COLUMN_CODICEASSAMBIENTE,
                DatabaseHelperBeacon.COLUMN_DESAMBIENTE,
                DatabaseHelperBeacon.COLUMN_PIANO,
                DatabaseHelperBeacon.COLUMN_DESTINAZIONEDUSO,
                DatabaseHelperBeacon.COLUMN_IDUTILIZZATORE,
                DatabaseHelperBeacon.COLUMN_DESCUTILIZZATORE
        };
        String whereClause = DatabaseHelperBeacon.COLUMN_MAC_BEACON + " like ?";
        String[] whereArgs = new String[] {macB};
        db = dbHelper.getWritableDatabase();
        Cursor c = db.query(DatabaseHelperBeacon.TBL_BEACON_AMBIENTI, tableColumns, whereClause, whereArgs,null,null,null);

        if (c.moveToFirst()){
            do{
                datoBeacon = "Ambiente" +
                        "\t\tId: " + c.getString(c.getColumnIndex(DatabaseHelperBeacon.COLUMN_IDAmbiente)) + "\n" +
                        "\t\tCodiceAmbiente: " + c.getString(c.getColumnIndex(DatabaseHelperBeacon.COLUMN_CODICEAMBIENTE)) + "\n" +
                        "\t\tCodiceAssAmbiente: " + c.getString(c.getColumnIndex(DatabaseHelperBeacon.COLUMN_CODICEASSAMBIENTE)) + "\n" +
                        "\t\tDesAmbiente: " + c.getString(c.getColumnIndex(DatabaseHelperBeacon.COLUMN_DESAMBIENTE)) + "\n" +
                        "\t\tPiano: " + c.getString(c.getColumnIndex(DatabaseHelperBeacon.COLUMN_PIANO)) + "\n" +
                        "\t\tDestinazione d'uso: " + c.getString(c.getColumnIndex(DatabaseHelperBeacon.COLUMN_DESTINAZIONEDUSO)) + "\n" +
                        "\t\tIdUtilizzatore: " + c.getString(c.getColumnIndex(DatabaseHelperBeacon.COLUMN_IDUTILIZZATORE)) + " - " +
                        "Descrizione: " + c.getString(c.getColumnIndex(DatabaseHelperBeacon.COLUMN_DESCUTILIZZATORE)) + "\n\n";
            }while(c.moveToNext());
        }
        c.close();
        db.close();
        dbHelper.close();

        return datoBeacon;
    }

    private void layoutBeaconAssociato(MyViewHolder holder, int position){
        Log.d("CardView_layoutBeaconAssociato","position -> " + position);
        final String macB = listaBeacon.get(position).getBluetoothAddress();
        //Testo
        String ambiente = getInfoAmbiente(macB);

        String beacon = "Beacon" + "\n" +
                "\t\t" + context.getResources().getString(R.string.txtMac) + ": " + listaBeacon.get(position).getBluetoothAddress() + "\n" +
                "\t\t" + context.getResources().getString(R.string.txtDistance) + ": " + listaBeacon.get(position).getDistance();

        holder.datiBeacon.setText((ambiente + beacon));
        //Setto lo sfondo
        this.setColorCardView(holder);
        //Bottone
        holder.btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Devo effettuare la Copia del MAC Beacon sul DB, sfruttando Volley
                Log.i("Copia","Ho cliccato il Bottone Copia Mac Beacon -> " + macB);
                //Mostro Messaggio usando Toast
                Toast.makeText(context, context.getResources().getString(R.string.txtCopiaToast_1) + " " + macB + " " + context.getResources().getString(R.string.txtCopiaToast_2), Toast.LENGTH_LONG).show();
                //Effettuo l'invio del valore
                new CopyInDatabase(context,"mac_beacon",macB);
            }
        });
    }

    private void layoutBeaconNonAssociato(MyViewHolder holder, int position){
        final String macB = listaBeacon.get(position).getBluetoothAddress();

        String beacon = "Beacon" + "\n" +
                "\t" + "UUID : " + listaBeacon.get(position).getId1() + "\n" +
                "\t" + context.getResources().getString(R.string.txtMac) + ": " + listaBeacon.get(position).getBluetoothAddress() + "\n" +
                "\t" + context.getResources().getString(R.string.txtDistance) + ": " + listaBeacon.get(position).getDistance() + "\n" +
                "\t" + context.getResources().getString(R.string.txtTxPower) + ": " + listaBeacon.get(position).getTxPower() + "\n" +
                "\t" + context.getResources().getString(R.string.txtRssi) + ": " + listaBeacon.get(position).getRssi();

        //Testi
        holder.datiBeacon.setText(beacon);
        //Bottone
        holder.btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Devo effettuare la Copia del MAC Beacon sul DB, sfruttando Volley
                Log.i("Copia","Ho cliccato il Bottone Copia Mac Beacon -> " + macB);
                //Mostro Messaggio usando Toast
                Toast.makeText(context, context.getResources().getString(R.string.txtCopiaToast_1) + " " + macB + " " + context.getResources().getString(R.string.txtCopiaToast_2), Toast.LENGTH_LONG).show();
                //Effettuo l'invio del valore
                new CopyInDatabase(context,"mac_beacon",macB);
            }
        });
    }
}