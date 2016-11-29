package com.akshaykant.com.eventers.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.akshaykant.com.eventers.Events;
import com.akshaykant.com.eventers.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Akshay Kant on 29-11-2016.
 */

public class ListProvider implements RemoteViewsService.RemoteViewsFactory {

    private ArrayList<Events> listItemList = new ArrayList<Events>();
    private Context context = null;
    private int appWidgetId;

    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    public ListProvider(Context context, Intent intent) {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    private void populateListItem() {

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("events");
        mDatabaseReference.addValueEventListener(new ValueEventListener() {

            Events listItem;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    listItem = new Events();
                    Events events = postSnapshot.getValue(Events.class);

                    listItem.setEventType(events.getEventType());
                    listItem.setLocation(events.getLocation());
                    listItem.setDate(events.getDate());
                    listItem.setTime(events.getTime());
                    listItemList.add(listItem);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public int getCount() {
        return listItemList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /*
     *Similar to getView of Adapter where instead of View
     *we return RemoteViews
     *
     */
    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews remoteView = new RemoteViews(
                context.getPackageName(), R.layout.widget_detail_list_item);
        Events listItem = listItemList.get(position);
        remoteView.setTextViewText(R.id.widget_event_type, listItem.getEventType());
        remoteView.setTextViewText(R.id.widget_location, listItem.getLocation());
        remoteView.setTextViewText(R.id.widget_date, listItem.getDate());
        remoteView.setTextViewText(R.id.widget_time, listItem.getTime());

        return remoteView;
    }


    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onCreate() {
        populateListItem();
    }

    @Override
    public void onDataSetChanged() {
        populateListItem();
    }

    @Override
    public void onDestroy() {

    }
}
