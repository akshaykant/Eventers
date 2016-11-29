package com.akshaykant.com.eventers.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.akshaykant.com.eventers.R;

/**
 * Created by Akshay Kant on 29-11-2016.
 */

public class DetailWidgetProvider extends AppWidgetProvider {

    public static final String TOAST_ACTION = "com.akshaykant.com.eventers.TOAST_ACTION";
    public static final String EXTRA_ITEM = "com.akshaykant.com.eventers.EXTRA_ITEM";


    // Called when the BroadcastReceiver receives an Intent broadcast.
    // Checks to see whether the intent's action is TOAST_ACTION. If it is, the app widget
    // displays a Toast message for the current item.
    @Override
    public void onReceive(Context context, Intent intent) {
        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        if (intent.getAction().equals(TOAST_ACTION)) {
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            int viewIndex = intent.getIntExtra(EXTRA_ITEM, 0);
            Toast.makeText(context, "Touched view " + viewIndex, Toast.LENGTH_SHORT).show();
        }
        super.onReceive(context, intent);
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        final int N = appWidgetIds.length;
    /*int[] appWidgetIds holds ids of multiple instance of your widget
     * meaning you are placing more than one widgets on your homescreen*/
        for (int i = 0; i < N; ++i) {

            // Set up the intent that starts the DetailWidgetRemoteViewsService, which will
            // provide the views for this collection.
            Intent intent = new Intent(context, DetailWidgetRemoteViewsService.class);
            // Add the app widget ID to the intent extras.
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews remoteViews = updateWidgetListView(context,
                    appWidgetIds[i]);

            // This section makes it possible for items to have individualized behavior.
            // It does this by setting up a pending intent template. Individuals items of a collection
            // cannot set up their own pending intents. Instead, the collection as a whole sets
            // up a pending intent template, and the individual items set a fillInIntent
            // to create unique behavior on an item-by-item basis.
            Intent toastIntent = new Intent(context, DetailWidgetProvider.class);
            // Set the action for the intent.
            // When the user touches a particular view, it will have the effect of
            // broadcasting TOAST_ACTION.
            toastIntent.setAction(DetailWidgetProvider.TOAST_ACTION);
            toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            PendingIntent toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            remoteViews.setPendingIntentTemplate(R.id.widget_list, toastPendingIntent);
            appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private RemoteViews updateWidgetListView(Context context, int appWidgetId) {

        //which layout to show on widget
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.widget_detail);

        //RemoteViews Service needed to provide adapter for ListView
        Intent svcIntent = new Intent(context, DetailWidgetRemoteViewsService.class);
        //passing app widget id to that RemoteViews Service
        svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        //setting a unique Uri to the intent
        svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
        //setting adapter to listview of the widget
        remoteViews.setRemoteAdapter(appWidgetId, R.id.widget_list,
                svcIntent);
        //setting an empty view in case of no data
        //remoteViews.setEmptyView(R.id.widget_list, R.id.widget_empty);
        return remoteViews;
    }
}
