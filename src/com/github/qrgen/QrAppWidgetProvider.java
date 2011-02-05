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
            Log.i(TAG, "widget onUpdate: " + appWidgetId);

            // Create an Intent to launch ExampleActivity
            Intent intent = new Intent(context, QrGen.class);
            String url = QrAppWidgetConfigure.loadQrData(context, appWidgetId);

            updateAppWidget(context, appWidgetManager, appWidgetId, url);
        }
    }

    static void updateAppWidget(Context context,
                                AppWidgetManager appWidgetManager,
                                int appWidgetId, 
                                String qrData) {
        Log.i(TAG, "widget updateAppWidget: " + appWidgetId + " " + qrData);

        // Create the intent that the widget will launch.
        Intent intent = new Intent(context, QrGen.class);
        intent.putExtra("qrdata", qrData);
        PendingIntent pendingIntent = 
        PendingIntent.getActivity(context, appWidgetId, intent, 
                                  PendingIntent.FLAG_UPDATE_CURRENT);

        // Create the view.
        RemoteViews views = new RemoteViews(context.getPackageName(),
                                            R.layout.qr_appwidget);
        views.setTextViewText(R.id.button, String.valueOf(appWidgetId));
        views.setOnClickPendingIntent(R.id.button, pendingIntent);

        // Tell the widget manager
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}