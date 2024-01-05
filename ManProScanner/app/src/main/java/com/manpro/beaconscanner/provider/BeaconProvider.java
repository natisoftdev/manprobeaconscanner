package com.manpro.beaconscanner.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.strictmode.SqliteObjectLeakedViolation;
import android.text.TextUtils;
import android.util.Log;

import com.manpro.beaconscanner.database.DatabaseHelperBeacon;

import java.util.HashMap;

/**
 * @author Lorenzo Malferrari
 */

public class BeaconProvider extends ContentProvider {

    private static final String TAG = "BeaconProvider";
    public static final String PROVIDER_NAME = "com.manpro.beaconscanner.provider.BeaconProvider";
    public static final String URL = "content://" + PROVIDER_NAME + "/" + DatabaseHelperBeacon.TABLE_NAME;
    public static final String URL_2 = "content://" + PROVIDER_NAME + "/" + DatabaseHelperBeacon.TBL_BEACON_AMBIENTI;
    public static final Uri CONTENT_URI = Uri.parse(URL);
    public static final Uri CONTENT_URI_2 = Uri.parse(URL_2);
    static final int Beacon_VERSION = 1;
    static final int Beacon_ID = 2;
    Context context;

    static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, DatabaseHelperBeacon.TABLE_NAME, Beacon_VERSION);
        uriMatcher.addURI(PROVIDER_NAME, DatabaseHelperBeacon.TABLE_NAME + "/#", Beacon_ID);
    }

    private SQLiteDatabase db;
    private DatabaseHelperBeacon dbHelper;

    @Override
    public boolean onCreate() {
        context = getContext();
        //dbHelper = new DatabaseHelperBeacon(context);
        dbHelper = DatabaseHelperBeacon.getInstance(context);
        db = dbHelper.getWritableDatabase();
        return db != null;
    }

    @Override
    public Cursor query( Uri uri, String[] projection, Bundle queryArgs, CancellationSignal cancellationSignal) {
        //Log.i(TAG + "Cursorquery",">= Oreo");
        HashMap<String,String> values;
        values = new HashMap<>();

        values.put(DatabaseHelperBeacon.COLUMN_ID,DatabaseHelperBeacon.COLUMN_ID);
        values.put(DatabaseHelperBeacon.COLUMN_DB_CONNECT,DatabaseHelperBeacon.COLUMN_DB_CONNECT);
        values.put(DatabaseHelperBeacon.COLUMN_UTENTE,DatabaseHelperBeacon.COLUMN_UTENTE);
        values.put(DatabaseHelperBeacon.COLUMN_PASSWORD,DatabaseHelperBeacon.COLUMN_PASSWORD);
        values.put(DatabaseHelperBeacon.COLUMN_MODEL,DatabaseHelperBeacon.COLUMN_MODEL);
        values.put(DatabaseHelperBeacon.COLUMN_DATACAMPIONAMENTO,DatabaseHelperBeacon.COLUMN_DATACAMPIONAMENTO);
        values.put(DatabaseHelperBeacon.COLUMN_ANDROID,DatabaseHelperBeacon.COLUMN_ANDROID);
        values.put(DatabaseHelperBeacon.COLUMN_LIVELLOBATTERIA,DatabaseHelperBeacon.COLUMN_LIVELLOBATTERIA);
        values.put(DatabaseHelperBeacon.COLUMN_TEMPOCAMPIONAMENTO,DatabaseHelperBeacon.COLUMN_TEMPOCAMPIONAMENTO);
        values.put(DatabaseHelperBeacon.COLUMN_NUMEROVERSIONE,DatabaseHelperBeacon.COLUMN_NUMEROVERSIONE);
        values.put(DatabaseHelperBeacon.COLUMN_BEACON_MAC,DatabaseHelperBeacon.COLUMN_BEACON_MAC);
        values.put(DatabaseHelperBeacon.COLUMN_BEACON_DISTANCE,DatabaseHelperBeacon.COLUMN_BEACON_DISTANCE);
        values.put(DatabaseHelperBeacon.COLUMN_BEACON_TXPOWER,DatabaseHelperBeacon.COLUMN_BEACON_TXPOWER);
        values.put(DatabaseHelperBeacon.COLUMN_BEACON_RSSI,DatabaseHelperBeacon.COLUMN_BEACON_RSSI);
        values.put(DatabaseHelperBeacon.COLUMN_BIT,DatabaseHelperBeacon.COLUMN_BIT);
        values.put(DatabaseHelperBeacon.COLUMN_ANDROID_ID,DatabaseHelperBeacon.COLUMN_ANDROID_ID);

        String[] selectData = {
                DatabaseHelperBeacon.COLUMN_ID,
                DatabaseHelperBeacon.COLUMN_DB_CONNECT,
                DatabaseHelperBeacon.COLUMN_UTENTE,
                DatabaseHelperBeacon.COLUMN_PASSWORD,
                DatabaseHelperBeacon.COLUMN_MODEL,
                DatabaseHelperBeacon.COLUMN_DATACAMPIONAMENTO,
                DatabaseHelperBeacon.COLUMN_ANDROID,
                DatabaseHelperBeacon.COLUMN_LIVELLOBATTERIA,
                DatabaseHelperBeacon.COLUMN_TEMPOCAMPIONAMENTO,
                DatabaseHelperBeacon.COLUMN_NUMEROVERSIONE,
                DatabaseHelperBeacon.COLUMN_BEACON_MAC,
                DatabaseHelperBeacon.COLUMN_BEACON_DISTANCE,
                DatabaseHelperBeacon.COLUMN_BEACON_TXPOWER,
                DatabaseHelperBeacon.COLUMN_BEACON_RSSI,
                DatabaseHelperBeacon.COLUMN_BIT,
                DatabaseHelperBeacon.COLUMN_ANDROID_ID
        };

        Log.i(TAG + " -> Cursorquery","Dentro");
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(DatabaseHelperBeacon.TABLE_NAME);
        qb.setStrict(true);
        qb.setProjectionMap(values);

        /*switch (uriMatcher.match(uri)) {
            case Beacon_ID:

                break;
            default:
                Log.i("Cursorquery ","URI sconosciuto");
                throw new IllegalArgumentException("URI sconosciuto" + uri);
        }*/

        String sortOrder = DatabaseHelperBeacon.COLUMN_ID;
        //Log.i(TAG + "Cursorquery",">= Oreo");
        //dbHelper = new DatabaseHelperBeacon(context);
        dbHelper = DatabaseHelperBeacon.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = qb.query(db,	selectData,	null,
               null,null, null, sortOrder);

        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public Cursor query(Uri uri, String[] projection,String selection,String[] selectionArgs, String sortOrder) {

        Log.i(TAG + "Cursorquery","< Oreo");

        HashMap<String,String> values;
        values = new HashMap<>();

        values.put(DatabaseHelperBeacon.COLUMN_ID,DatabaseHelperBeacon.COLUMN_ID);
        values.put(DatabaseHelperBeacon.COLUMN_DB_CONNECT,DatabaseHelperBeacon.COLUMN_DB_CONNECT);
        values.put(DatabaseHelperBeacon.COLUMN_UTENTE,DatabaseHelperBeacon.COLUMN_UTENTE);
        values.put(DatabaseHelperBeacon.COLUMN_PASSWORD,DatabaseHelperBeacon.COLUMN_PASSWORD);
        values.put(DatabaseHelperBeacon.COLUMN_MODEL,DatabaseHelperBeacon.COLUMN_MODEL);
        values.put(DatabaseHelperBeacon.COLUMN_DATACAMPIONAMENTO,DatabaseHelperBeacon.COLUMN_DATACAMPIONAMENTO);
        values.put(DatabaseHelperBeacon.COLUMN_ANDROID,DatabaseHelperBeacon.COLUMN_ANDROID);
        values.put(DatabaseHelperBeacon.COLUMN_LIVELLOBATTERIA,DatabaseHelperBeacon.COLUMN_LIVELLOBATTERIA);
        values.put(DatabaseHelperBeacon.COLUMN_TEMPOCAMPIONAMENTO,DatabaseHelperBeacon.COLUMN_TEMPOCAMPIONAMENTO);
        values.put(DatabaseHelperBeacon.COLUMN_NUMEROVERSIONE,DatabaseHelperBeacon.COLUMN_NUMEROVERSIONE);
        values.put(DatabaseHelperBeacon.COLUMN_BEACON_MAC,DatabaseHelperBeacon.COLUMN_BEACON_MAC);
        values.put(DatabaseHelperBeacon.COLUMN_BEACON_DISTANCE,DatabaseHelperBeacon.COLUMN_BEACON_DISTANCE);
        values.put(DatabaseHelperBeacon.COLUMN_BEACON_TXPOWER,DatabaseHelperBeacon.COLUMN_BEACON_TXPOWER);
        values.put(DatabaseHelperBeacon.COLUMN_BEACON_RSSI,DatabaseHelperBeacon.COLUMN_BEACON_RSSI);
        values.put(DatabaseHelperBeacon.COLUMN_BIT,DatabaseHelperBeacon.COLUMN_BIT);
        values.put(DatabaseHelperBeacon.COLUMN_ANDROID_ID,DatabaseHelperBeacon.COLUMN_ANDROID_ID);

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(DatabaseHelperBeacon.TABLE_NAME);
        qb.setStrict(true);

        qb.setProjectionMap(values);

        /*switch (uriMatcher.match(uri)) {
            case Beacon_ID:

                break;
            default:
                Log.i("Cursorquery","URI sconosciuto");
                throw new IllegalArgumentException("URI sconosciuto" + uri);
        }*/

        if (sortOrder == null || sortOrder.equals("")){ sortOrder = DatabaseHelperBeacon.COLUMN_ID; }
        //dbHelper = new DatabaseHelperBeacon(context);
        dbHelper = DatabaseHelperBeacon.getInstance(context);
        db = dbHelper.getWritableDatabase();
        Cursor c = qb.query(db,	projection,	selection, selectionArgs,null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        db.close();
        dbHelper.close();
        return c;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        /**
         * Add a new student record
         */
        //dbHelper = new DatabaseHelperBeacon(context);
        dbHelper = DatabaseHelperBeacon.getInstance(context);
        db = dbHelper.getWritableDatabase();
        long rowID = db.insert(	DatabaseHelperBeacon.TABLE_NAME, "", values);

        /**
         * If record is added successfully
         */
        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            db.close();
            dbHelper.close();
            return _uri;
        }

        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        //dbHelper = new DatabaseHelperBeacon(context);
        dbHelper = DatabaseHelperBeacon.getInstance(context);
        db = dbHelper.getWritableDatabase();
        int count = 0;
        switch (uriMatcher.match(uri)){
            case Beacon_VERSION:
                count = db.delete(DatabaseHelperBeacon.TABLE_NAME, selection, selectionArgs);
                break;

            case Beacon_ID:
                String id = uri.getPathSegments().get(1);
                count = db.delete( DatabaseHelperBeacon.TABLE_NAME, DatabaseHelperBeacon.COLUMN_ID +  " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        db.close();
        dbHelper.close();
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        //dbHelper = new DatabaseHelperBeacon(context);
        dbHelper = DatabaseHelperBeacon.getInstance(context);
        db = dbHelper.getWritableDatabase();
        int count;
        switch (uriMatcher.match(uri)) {
            case Beacon_VERSION:
                count = db.update( DatabaseHelperBeacon.TABLE_NAME, values, selection, selectionArgs);
                db.close();
                dbHelper.close();
                break;

            case Beacon_ID:
                count = db.update( DatabaseHelperBeacon.TABLE_NAME, values,
                        DatabaseHelperBeacon.COLUMN_ID + " = " + uri.getPathSegments().get(1) +
                                (!TextUtils.isEmpty(selection) ? " AND (" +selection + ')' : ""), selectionArgs);
                db.close();
                dbHelper.close();
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri );
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            case Beacon_VERSION:
                return "vnd.android.cursor.dir/vnd.manpro.beaconscanner.dati.Beacon";
            case Beacon_ID:
                return "vnd.android.cursor.item/vnd.manpro.beaconscanner.dati.Beacon";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }
}