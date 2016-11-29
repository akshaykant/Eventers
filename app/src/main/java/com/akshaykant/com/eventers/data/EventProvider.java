package com.akshaykant.com.eventers.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import org.abego.treelayout.internal.util.Contract;

import java.security.Provider;
import java.util.Date;

import static com.akshaykant.com.eventers.R.string.organiser;

/**
 * Created by Akshay Kant on 29-11-2016.
 */

public class EventProvider extends ContentProvider {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = EventProvider.class.getSimpleName();

    /**
     * URI matcher code for the content URI for the events table
     */
    private static final int EVENTS = 100;

    /**
     * URI matcher code for the content URI for a single event in the events table
     */
    private static final int EVENT_ID = 101;


    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // The content URI of the form "content://com.example.android.events/events" will map to the
        // integer code {@link #EVENTS}. This URI is used to provide access to MULTIPLE rows
        // of the events table.
        sUriMatcher.addURI(EventContract.CONTENT_AUTHORITY, EventContract.PATH_EVENTS, EVENTS);

        // The content URI of the form "content://com.example.android.events/events/#" will map to the
        // integer code {@link #EVENT_ID}. This URI is used to provide access to ONE single row
        // of the events table.
        //
        // In this case, the "#" wildcard is used where "#" can be substituted for an integer.
        // For example, "content://com.example.android.events/events/3" matches, but
        // "content://com.example.android.events/events" (without a number at the end) doesn't match.
        sUriMatcher.addURI(EventContract.CONTENT_AUTHORITY, EventContract.PATH_EVENTS + "/#", EVENT_ID);
    }

    /**
     * Database helper object
     */
    private EventDBHelper mDbHelper;


    @Override
    public boolean onCreate() {
        mDbHelper = new EventDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);

        switch (match) {
            case EVENTS:
                // For the EVENTS code, query the events table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the events table.
                cursor = database.query(EventContract.EventsEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case EVENT_ID:
                // For the EVENT_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.events/events/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = EventContract.EventsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the events table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(EventContract.EventsEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the cursor
        return cursor;

    }

    @Nullable
    @Override
    public String getType(Uri uri) {


        final int match = sUriMatcher.match(uri);
        switch (match) {
            case EVENTS:
                return EventContract.EventsEntry.CONTENT_LIST_TYPE;
            case EVENT_ID:
                return EventContract.EventsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }

    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case EVENTS:
                return insertEvent(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }

    }

    /**
     * Insert a event into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertEvent(Uri uri, ContentValues values) {

        // Check that the name is not null
        String type = values.getAsString(EventContract.EventsEntry.COLUMN_EVENT_TYPE);
        if (type == null) {
            throw new IllegalArgumentException("Event requires a type");
        }

        // Check that the location is not null
        String location = values.getAsString(EventContract.EventsEntry.COLUMN_EVENT_LOCATION);
        if (location == null) {
            throw new IllegalArgumentException("Event requires a location");
        }

        // Check that the date is not null
        String date = values.getAsString(EventContract.EventsEntry.COLUMN_EVENT_DATE);
        if (date == null) {
            throw new IllegalArgumentException("Event requires a date");
        }

        // Check that the time is not null
        String time = values.getAsString(EventContract.EventsEntry.COLUMN_EVENT_TIME);
        if (time == null) {
            throw new IllegalArgumentException("Event requires a time");
        }

        // Check that the dress is not null
        String dress = values.getAsString(EventContract.EventsEntry.COLUMN_EVENT_DRESS);
        if (dress == null) {
            throw new IllegalArgumentException("Event requires a dress");
        }

        // Check that the weather is not null
        String weather = values.getAsString(EventContract.EventsEntry.COLUMN_EVENT_WEATHER);
        if (weather == null) {
            throw new IllegalArgumentException("Event requires a weather");
        }

        // Check that the organiser is not null
        String organiser = values.getAsString(EventContract.EventsEntry.COLUMN_EVENT_ORGANISER);
        if (organiser == null) {
            throw new IllegalArgumentException("Event requires a organiser");
        }

        // Check that the latitude is not null
        Double latitude = values.getAsDouble(EventContract.EventsEntry.COLUMN_EVENT_LATITUDE);
        if (latitude == null) {
            throw new IllegalArgumentException("Event requires a latitude");
        }

        // Check that the longitude is not null
        Double longitude = values.getAsDouble(EventContract.EventsEntry.COLUMN_EVENT_LONGITUDE);
        if (longitude == null) {
            throw new IllegalArgumentException("Event requires a longitude");
        }

        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new event with the given values
        long id = database.insert(EventContract.EventsEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the Event content URI
        getContext().getContentResolver().notifyChange(uri, null);
        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);

    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
