package com.akshaykant.com.eventers;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

/**
 * Created by Akshay Kant on 28-11-2016.
 */

public class EventAdapter extends ArrayAdapter<Events> {

    @SuppressWarnings("HardCodedStringLiteral")
    private static final String ORGANISER = "ORGANISER";
    @SuppressWarnings("HardCodedStringLiteral")
    private static final String INVITEE = "INVITEE";

    public EventAdapter(Context context, int resource, List<Events> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_event, parent, false);
        }

        TextView eventType = (TextView) convertView.findViewById(R.id.article_event_type);
        TextView eventLocation = (TextView) convertView.findViewById(R.id.article_event_location);
        TextView eventDate = (TextView) convertView.findViewById(R.id.article_event_date);
        TextView eventTime = (TextView) convertView.findViewById(R.id.article_event_time);
        TextView isOrganiser = (TextView) convertView.findViewById(R.id.article_is_organiser);

        Events events = getItem(position);

        eventType.setText(events.getEventType());
        eventLocation.setText(events.getLocation());
        eventDate.setText(events.getDate());
        eventTime.setText(events.getTime());

        if (events.getOrganiser() != null && events.getOrganiser().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            isOrganiser.setText(ORGANISER);
        } else {
            isOrganiser.setText(INVITEE);
        }

        return convertView;
    }
}
