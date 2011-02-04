package com.github.qrgen;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class QrAppWidgetProvider extends AppWidgetProvider {

    private static final String TAG = "qrgen";

    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        final int N = appWidgetIds.length;

        // Perform this loop procedure for each App Widget that
        // belongs to this provider
        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];
            Log.i(TAG, "widget update: " + appWidgetId);

            // Create an Intent to launch ExampleActivity
            Intent intent = new Intent(context, QrGen.class);
            String url;
            if (appWidgetId % 2 == 0) {
                url = "http://stackoverflow.com";
            } else {
                url = "http://beliber.se";
            }
            intent.putExtra("url", url);
            PendingIntent pendingIntent = 
                PendingIntent.getActivity(context, 0, intent, 
                                          PendingIntent.FLAG_UPDATE_CURRENT);

            // Get the layout for the App Widget and attach an
            // on-click listener to the button
            RemoteViews views = 
                new RemoteViews(context.getPackageName(), 
                                R.layout.qr_appwidget);
            views.setTextViewText(R.id.button, String.valueOf(appWidgetId));
            views.setOnClickPendingIntent(R.id.button, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the
            // current App Widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}