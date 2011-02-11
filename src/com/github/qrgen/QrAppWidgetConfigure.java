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

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class QrAppWidgetConfigure extends Activity {

    private static final String TAG = "qrgen";
    private static final String PREFS_NAME
        = "com.github.qrgen.QrAppWidgetProvider";
    private static final String PREF_PREFIX_KEY = "qrdata_";

    public static final String QR_DATA = "QR_DATA";
    public static final String LABEL = "LABEL";

    int mAppWidgetId;
    EditText mQrData;
    EditText mLabel;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget
        // host to cancel out of the widget placement if they press
        // the back button.
        setResult(RESULT_CANCELED);

        // Set the view layout resource to use.
        setContentView(R.layout.qr_appwidget_conf);

        // Find the EditText
        mQrData = (EditText)findViewById(R.id.qr_data);
        mLabel = (EditText)findViewById(R.id.qr_label);

        // Bind the action for the save button.
        findViewById(R.id.ok_button).setOnClickListener(mOnClickListener);

        // Find the widget id from the intent. 
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                                         AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If they gave us an intent without the widget id, just bail.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = QrAppWidgetConfigure.this;

            // When the button is clicked, save the string in our
            // prefs and return that they clicked OK.
            String qrData = mQrData.getText().toString();
            String label = mLabel.getText().toString();

            saveConf(context, mAppWidgetId, QR_DATA, qrData);
            saveConf(context, mAppWidgetId, LABEL, label);

            // Push widget update to surface with newly set prefix
            AppWidgetManager appWidgetManager = 
                AppWidgetManager.getInstance(context);
            QrAppWidgetProvider.updateAppWidget(context, appWidgetManager,
                                                mAppWidgetId, qrData, label);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 
                                 mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    private static String confKey(int appWidgetId, String key) {
        return PREF_PREFIX_KEY + appWidgetId + "_" + key;
    }

    static void saveConf(Context context, int appWidgetId, String key,
                         String value) {
        SharedPreferences.Editor prefs = 
            context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(confKey(appWidgetId, key), value);
        prefs.commit();
    }

    static String loadConf(Context context, int appWidgetId, String key,
                           String defaultValue) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String value =
            prefs.getString(confKey(appWidgetId, PREF_PREFIX_KEY), null);
        if (value != null) {
            return value;
        } else {
            return defaultValue;
        }
    }
}
