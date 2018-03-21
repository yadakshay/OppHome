package com.openhome.BackgroundService;

/**
 * Created by user on 26-10-2017.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class locationDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "location.db";
    private static final int DATABASE_VERSION = 5;

    public locationDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_LOCATION_TABLE = "CREATE TABLE " +
                locationsContract.locationsEntry.TABLE_NAME + " (" +
                locationsContract.locationsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                locationsContract.locationsEntry.COLUMN_LATITUDE + " TEXT NOT NULL, " +
                locationsContract.locationsEntry.COLUMN_LONGITUDE + " TEXT NOT NULL" +
                ");";

        db.execSQL(SQL_CREATE_LOCATION_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + locationsContract.locationsEntry.TABLE_NAME);
        onCreate(db);
    }
}
