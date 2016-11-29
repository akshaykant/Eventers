package com.akshaykant.com.eventers.widget;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.akshaykant.com.eventers.R;
import com.akshaykant.com.eventers.data.EventContract;
import com.facebook.internal.Utility;

import static android.R.attr.description;
import static com.google.android.gms.cast.internal.zzl.sR;

/**
 * Created by Akshay Kant on 29-11-2016.
 */

public class DetailWidgetRemoteViewsService extends RemoteViewsService {

    public final String LOG_TAG = DetailWidgetRemoteViewsService.class.getSimpleName();
    private static final String[] EVENT_COLUMNS = {
            EventContract.EventsEntry.TABLE_NAME + "." + EventContract.EventsEntry._ID,
            EventContract.EventsEntry.COLUMN_EVENT_TYPE,
            EventContract.EventsEntry.COLUMN_EVENT_LOCATION,
            EventContract.EventsEntry.COLUMN_EVENT_DATE,
            EventContract.EventsEntry.COLUMN_EVENT_TIME,
            EventContract.EventsEntry.COLUMN_EVENT_WEATHER
    };
    // these indices must match the projection
    static final int INDEX_EVENT_ID = 0;
    static final int INDEX_EVENT_TYPE = 1;
    static final int INDEX_EVENT_LOCATION = 2;
    static final int INDEX_EVENT_DATE = 3;
    static final int INDEX_EVENT_TIME = 4;
    static final int INDEX_EVENT_WEATHER = 6;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {

            private Cursor data = null;

            @Override
            public void onCreate() {

            }

            @Override
            public void onDataSetChanged() {

                if (data != null) {
                    data.close();
                }
                // This method is called by the app hosting the widget (e.g., the launcher)
                // However, our ContentProvider is not exported so it doesn't have access to the
                // data. Therefore we need to clear (and finally restore) the calling identity so
                // that calls use our process and permission
                final long identityToken = Binder.clearCallingIdentity();

                Uri uri = EventContract.EventsEntry.CONTENT_URI;
                data = getContentResolver().query(uri,
                        EVENT_COLUMNS,
                        null,
                        null,
                        null);
                Binder.restoreCallingIdentity(identityToken);

            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.widget_detail_list_item);

                String event_type = data.getString(INDEX_EVENT_TYPE);
                String event_location = data.getString(INDEX_EVENT_LOCATION);
                String event_date = data.getString(INDEX_EVENT_DATE);
                String event_time = data.getString(INDEX_EVENT_TIME);
                String event_weather_img = data.getString(INDEX_EVENT_WEATHER);


                views.setTextViewText(R.id.widget_event_type, event_type);
                views.setTextViewText(R.id.widget_location, event_location);
                views.setTextViewText(R.id.widget_date, event_date);
                views.setTextViewText(R.id.widget_time, event_time);

                final Intent fillInIntent = new Intent();
                Uri uri = EventContract.EventsEntry.CONTENT_URI;
                fillInIntent.setData(uri);
                views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);
                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_detail_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return data.getLong(INDEX_EVENT_ID);
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
