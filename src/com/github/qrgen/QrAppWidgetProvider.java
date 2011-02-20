/*
 * Copyright 2011 Mattias Svala
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
            QrData qrData = WidgetConfigure.loadConf(context, appWidgetId);
            updateAppWidget(context, appWidgetManager, appWidgetId, qrData);
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
                                int appWidgetId, QrData qrData) {
        if (qrData.data == null) {
            qrData.data = "notsetnotset";
            qrData.label = "foo";
        }

        Log.i(TAG, "widget updateAppWidget: " + appWidgetId + " " +
                qrData.data);

        // Create the intent that the widget will launch.
        Intent intent = new Intent(context, QrGen.class);
        intent.putExtra("qrdata", qrData.data);
        PendingIntent pendingIntent = 
        PendingIntent.getActivity(context, appWidgetId, intent, 
                                  PendingIntent.FLAG_UPDATE_CURRENT);

        // Create the view.
        RemoteViews views = new RemoteViews(context.getPackageName(),
                                            R.layout.qr_appwidget);
        if (qrData.label != null) {
            views.setTextViewText(R.id.label, qrData.label);
        }
        views.setOnClickPendingIntent(R.id.button, pendingIntent);

        try {
            Log.i(TAG, "Draw QR code.");
            QRCode code = new QRCode();
            if (qrData.data != null) {
                Encoder.encode(qrData.data, ErrorCorrectionLevel.L, code);
                drawIcon(context, views, code.getMatrix());
            }
        } catch (WriterException e) {
            Log.e(TAG, "Failed to generate QR.", e);
        }

        // Tell the widget manager
        Log.i(TAG, "Done updating.");
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}
