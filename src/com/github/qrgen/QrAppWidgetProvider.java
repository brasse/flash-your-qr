package com.github.qrgen;

import com.google.zxing.WriterException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.google.zxing.qrcode.encoder.Encoder;
import com.google.zxing.qrcode.encoder.QRCode;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
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
            String url = QrAppWidgetConfigure.loadConf(context, appWidgetId,
                    QrAppWidgetConfigure.QR_DATA,
                    context.getString(R.string.default_qrdata));
            String label = QrAppWidgetConfigure.loadConf(context, appWidgetId,
                    QrAppWidgetConfigure.LABEL,
                    context.getString(R.string.default_label));

            updateAppWidget(context, appWidgetManager, appWidgetId, url, label);
        }
    }

    private static void drawIcon(Context context, RemoteViews views,
            ByteMatrix qrCode) {
        Bitmap bitmap = Bitmap.createBitmap(50, 50, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        QrDraw.draw(canvas, qrCode, 0, 0, 0, 0);
        views.setImageViewBitmap(R.id.button, bitmap);
    }

    static void updateAppWidget(Context context,
                                AppWidgetManager appWidgetManager,
                                int appWidgetId, 
                                String qrData,
                                String label) {
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
        views.setTextViewText(R.id.label, label);
        views.setOnClickPendingIntent(R.id.button, pendingIntent);

        try {
            QRCode code = new QRCode();
            Encoder.encode(qrData, ErrorCorrectionLevel.L, code);
            drawIcon(context, views, code.getMatrix());
        } catch (WriterException e) {
            Log.e(TAG, "Failed to generate QR.", e);
        }

        // Tell the widget manager
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}