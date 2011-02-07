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

    int mAppWidgetId;
    EditText mQrData;

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
            saveQrData(context, mAppWidgetId, qrData);

            // Push widget update to surface with newly set prefix
            AppWidgetManager appWidgetManager = 
                AppWidgetManager.getInstance(context);
            QrAppWidgetProvider.updateAppWidget(context, appWidgetManager,
                                                mAppWidgetId, qrData);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 
                                 mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    static void saveQrData(Context context, int appWidgetId, String qrData) {
        SharedPreferences.Editor prefs = 
            context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, qrData);
        prefs.commit();
    }

    static String loadQrData(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String qrData = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
        if (qrData != null) {
            return qrData;
        } else {
            return context.getString(R.string.default_qrdata);
        }
    }
}
