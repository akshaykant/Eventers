package com.akshaykant.com.eventers.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.R.attr.version;

/**
 * Created by Akshay Kant on 29-11-2016.
 */

public class EventDBHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = EventDBHelper.class.getSimpleName();

    /**
     * Name of the database file
     */
    private static final String DATABASE_NAME = "events.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;
    /**
     * Constructs a new instance of {@link EventDBHelper}.
     *
     * @param context of the app
     */
    public EventDBHelper(Context context) {
        super(context, DATABASE_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create a String that contains the SQL statement to create the events table
        String SQL_CREATE_EVENTS_TABLE = "CREATE TABLE " + EventContract.EventsEntry.TABLE_NAME + " ("
                + EventContract.EventsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + EventContract.EventsEntry.COLUMN_EVENT_TYPE + " TEXT NOT NULL, "
                + EventContract.EventsEntry.COLUMN_EVENT_LOCATION + " TEXT NOT NULL, "
                + EventContract.EventsEntry.COLUMN_EVENT_DATE + " TEXT NOT NULL, "
                + EventContract.EventsEntry.COLUMN_EVENT_TIME + " TEXT NOT NULL, "
                + EventContract.EventsEntry.COLUMN_EVENT_DRESS + " TEXT NOT NULL, "
                + EventContract.EventsEntry.COLUMN_EVENT_WEATHER + " TEXT NOT NULL, "
                + EventContract.EventsEntry.COLUMN_EVENT_ORGANISER + " TEXT NOT NULL, "
                + EventContract.EventsEntry.COLUMN_EVENT_LATITUDE + " DOUBLE NOT NULL, "
                + EventContract.EventsEntry.COLUMN_EVENT_LONGITUDE + " DOUBLE NOT NULL);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_EVENTS_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
