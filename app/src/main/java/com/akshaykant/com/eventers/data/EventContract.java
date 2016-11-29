package com.akshaykant.com.eventers.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Akshay Kant on 29-11-2016.
 */

@SuppressWarnings("HardCodedStringLiteral")
public class EventContract {
    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    @SuppressWarnings("HardCodedStringLiteral")
    public static final String CONTENT_AUTHORITY = "com.akshaykant.com.eventers";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    @SuppressWarnings("HardCodedStringLiteral")
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.android.events/events/ is a valid path for
     * looking at event data. content://com.example.android.events/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    public static final String PATH_EVENTS = "events";

    /**
     * // To prevent someone from accidentally instantiating the contract class,
     * // give it an empty constructor.
     * private EventContract() {}
     * <p>
     * /**
     * Inner class that defines constant values for the events database table.
     * Each entry in the table represents a single event.
     */
    public static final class EventsEntry implements BaseColumns {

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of events.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EVENTS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single event.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EVENTS;

        /**
         * The content URI to access the event data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_EVENTS);

        /**
         * Name of database table for evnts
         */
        public final static String TABLE_NAME = "events";

        /**
         * Unique ID number for the event (only for use in the database table).
         * <p>
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Type of the event.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_EVENT_TYPE = "type";

        /**
         * Location of the event.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_EVENT_LOCATION = "location";

        /**
         * date of the event.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_EVENT_DATE = "date";

        /**
         * time of the event.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_EVENT_TIME = "time";

        /**
         * dress in event.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_EVENT_DRESS = "dress";

        /**
         * weather at event.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_EVENT_WEATHER = "weather";

        /**
         * Organiser of the event.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_EVENT_ORGANISER = "organiser";

        /**
         * latitude of the event.
         * <p>
         * Type: DOUBLE
         */
        public final static String COLUMN_EVENT_LATITUDE = "latitude";

        /**
         * longitude of the event.
         * <p>
         * Type: DOUBLE
         */
        public final static String COLUMN_EVENT_LONGITUDE = "longitude";
    }

}
