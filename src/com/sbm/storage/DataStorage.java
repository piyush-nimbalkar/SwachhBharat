package com.sbm.storage;

import android.database.Cursor;
import android.util.Log;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import com.sbm.model.Spotfix;
import com.sbm.model.SpotfixBuilder;

import java.text.ParseException;
import java.util.ArrayList;

public class DataStorage extends SQLiteOpenHelper {

    private static final String TAG = "DATABASE";

    private static final String DB_NAME = "swachh_bharat.db";
    private static final String TABLE_SPOTFIX = "spotfix";

    private static final String COLUMN_SPOTFIX_ID = "spotfix_id";
    private static final String COLUMN_OWNER_ID = "owner_id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_STATUS = "status";
    private static final String COLUMN_ESTIMATED_HOURS = "estimated_hours";
    private static final String COLUMN_ESTIMATED_PEOPLE = "estimated_people";
    private static final String COLUMN_LATITUDE = "latitude";
    private static final String COLUMN_LONGITUDE = "longitude";
    private static final String COLUMN_FIX_DATE = "fix_date";

    private static final String CREATE_SPOTFIX_TABLE = "CREATE TABLE IF NOT EXISTS " +
            TABLE_SPOTFIX + " (" +
            COLUMN_SPOTFIX_ID + " INTEGER, " +
            COLUMN_OWNER_ID + " INTEGER, " +
            COLUMN_TITLE + " VARCHAR(150), " +
            COLUMN_DESCRIPTION + " TEXT, " +
            COLUMN_STATUS + " VARCHAR(50), " +
            COLUMN_ESTIMATED_HOURS + " INTEGER, " +
            COLUMN_ESTIMATED_PEOPLE + " INTEGER, " +
            COLUMN_LATITUDE + " TEXT, " +
            COLUMN_LONGITUDE + " TEXT, " +
            COLUMN_FIX_DATE + " TEXT );";

    private static final String INSERT_SPOTFIX = "INSERT INTO " +
            TABLE_SPOTFIX + " (" +
            COLUMN_SPOTFIX_ID + "," +
            COLUMN_OWNER_ID + "," +
            COLUMN_TITLE + "," +
            COLUMN_DESCRIPTION + "," +
            COLUMN_STATUS + "," +
            COLUMN_ESTIMATED_HOURS + "," +
            COLUMN_ESTIMATED_PEOPLE + "," +
            COLUMN_LATITUDE + "," +
            COLUMN_LONGITUDE + "," +
            COLUMN_FIX_DATE + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";


    public DataStorage(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SPOTFIX_TABLE);
    }

    public void createSpotfix(Spotfix spotfix) {
        SQLiteDatabase db = getWritableDatabase();
        assert db != null;
        SQLiteStatement statement = db.compileStatement(INSERT_SPOTFIX);
        assert statement != null;
        statement.bindLong(1, spotfix.getId());
        statement.bindLong(2, spotfix.getOwnerId());
        statement.bindString(3, spotfix.getTitle());
        statement.bindString(4, spotfix.getDescription());
        statement.bindString(5, spotfix.getStatus());
        statement.bindLong(6, spotfix.getEstimatedHours());
        statement.bindLong(7, spotfix.getEstimatedPeople());
        statement.bindDouble(8, spotfix.getLatitude());
        statement.bindDouble(9, spotfix.getLongitude());
        statement.bindString(10, spotfix.getFixDateInString());
        statement.executeInsert();
        Log.d(TAG, "Spotfix created.");
    }

    public Spotfix getSpotfix(long id) throws ParseException {
        SQLiteDatabase db = getReadableDatabase();
        assert db != null;
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_SPOTFIX + " WHERE " + COLUMN_SPOTFIX_ID + "=" + id, null);
        Spotfix spotfix = null;

        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    spotfix = SpotfixBuilder.spotfix()
                            .setId(c.getLong(c.getColumnIndex(COLUMN_SPOTFIX_ID)))
                            .setOwnerId(c.getLong(c.getColumnIndex(COLUMN_OWNER_ID)))
                            .setTitle(c.getString(c.getColumnIndex(COLUMN_TITLE)))
                            .setDescription(c.getString(c.getColumnIndex(COLUMN_DESCRIPTION)))
                            .setStatus(c.getString(c.getColumnIndex(COLUMN_STATUS)))
                            .setEstimatedHours(c.getLong(c.getColumnIndex(COLUMN_ESTIMATED_HOURS)))
                            .setEstimatedPeople(c.getLong(c.getColumnIndex(COLUMN_ESTIMATED_PEOPLE)))
                            .setLatitude(c.getDouble(c.getColumnIndex(COLUMN_LATITUDE)))
                            .setLongitude(c.getDouble(c.getColumnIndex(COLUMN_LONGITUDE)))
                            .setFixDate(c.getString(c.getColumnIndex(COLUMN_FIX_DATE))).build();
                } while (c.moveToNext());
            }
        }
        return spotfix;
    }

    public ArrayList<Spotfix> getSpotfixes() throws ParseException {
        SQLiteDatabase db = getReadableDatabase();
        assert db != null;
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_SPOTFIX, null);
        ArrayList<Spotfix> spotfixes = new ArrayList<Spotfix>();

        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    Spotfix spotfix = SpotfixBuilder.spotfix()
                            .setId(c.getLong(c.getColumnIndex(COLUMN_SPOTFIX_ID)))
                            .setOwnerId(c.getLong(c.getColumnIndex(COLUMN_OWNER_ID)))
                            .setTitle(c.getString(c.getColumnIndex(COLUMN_TITLE)))
                            .setDescription(c.getString(c.getColumnIndex(COLUMN_DESCRIPTION)))
                            .setStatus(c.getString(c.getColumnIndex(COLUMN_STATUS)))
                            .setEstimatedHours(c.getLong(c.getColumnIndex(COLUMN_ESTIMATED_HOURS)))
                            .setEstimatedPeople(c.getLong(c.getColumnIndex(COLUMN_ESTIMATED_PEOPLE)))
                            .setLatitude(c.getDouble(c.getColumnIndex(COLUMN_LATITUDE)))
                            .setLongitude(c.getDouble(c.getColumnIndex(COLUMN_LONGITUDE)))
                            .setFixDate(c.getString(c.getColumnIndex(COLUMN_FIX_DATE))).build();
                    spotfixes.add(spotfix);
                } while (c.moveToNext());
            }
        }
        return spotfixes;
    }

    public void deleteSpotfixes() {
        SQLiteDatabase db = getWritableDatabase();
        assert db != null;
        db.execSQL("DELETE FROM " + TABLE_SPOTFIX);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
