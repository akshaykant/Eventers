package com.akshaykant.com.eventers;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.akshaykant.com.eventers.data.EventContract;

/**
 * Created by Akshay Kant on 29-11-2016.
 */
public class EventCursorAdapter extends CursorAdapter {
    public EventCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.activity_detail_event, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // Find individual views that we want to modify in the list item layout
        TextView latLong = (TextView) view.findViewById(R.id.article_loader);

        // Find the columns of pet attributes that we're interested in
        int latColumnIndex = cursor.getColumnIndex(EventContract.EventsEntry.COLUMN_EVENT_LATITUDE);
        int longColumnIndex = cursor.getColumnIndex(EventContract.EventsEntry.COLUMN_EVENT_LONGITUDE);

        // Read the pet attributes from the Cursor for the current pet
        Double latitude = cursor.getDouble(latColumnIndex);
        Double longitude = cursor.getDouble(longColumnIndex);

        if (latitude != null && longitude != null) {
            latLong.setText("Latitude: " + latitude + "Longitude" + longitude);
        }


    }
}
