package com.github.qrgen;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.provider.ContactsContract.Contacts;
import android.util.Log;

import java.io.InputStreamReader;
import java.io.BufferedReader;

public class WidgetConfigure extends PreferenceActivity {

    private static final String TAG = "qrgen";

    private static final String SOURCE = "qrSource";
    private static final String MANUAL = "qrManualData";
    private static final String CONTACT = "qrContactData";
    private static final String LABEL = "qrLabel";
    private static final String SOURCE_MANUAL = "sourceManual";
    private static final String SOURCE_CONTACT = "sourceContact";

    private int mWidgetId;
    private Preference manualData;
    private Preference contactData;

    private static String prefsName(int widgetId) {
        return "qrwidget_" + widgetId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Find the widget id from the intent. 
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        int widgetId = 0;
        if (extras != null) {
            mWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                                      AppWidgetManager.INVALID_APPWIDGET_ID);
        } else {
            Log.e(TAG, "We should have received extras here.");
            throw new RuntimeException("Missing data in configure activity.");
        }

        getPreferenceManager().setSharedPreferencesName(prefsName(mWidgetId));
        addPreferencesFromResource(R.xml.widget_configure);

        manualData = findPreference(MANUAL);
        manualData.setOnPreferenceChangeListener(setSummary);

        Preference label = findPreference(LABEL);
        label.setOnPreferenceChangeListener(setSummary);

        contactData = findPreference(CONTACT);
        contactData.setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
                    public boolean onPreferenceClick (Preference preference) {
                        Intent intent = new Intent(Intent.ACTION_PICK, 
                               Contacts.CONTENT_URI);
                        startActivityForResult(intent, 0);
                        return true;
                    }
                });

        ListPreference source = (ListPreference)findPreference(SOURCE);
        updateSource(source.getValue());
        source.setOnPreferenceChangeListener(
                new Preference.OnPreferenceChangeListener() {
                    public boolean onPreferenceChange(Preference p, Object o) {
                        updateSource((String)o);
                        return true;
                    }
                });
    }

    private Preference.OnPreferenceChangeListener setSummary = 
            new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference p, Object o) {
                    p.setSummary((String)o);
                    return true;
                }
            };

    private void updateSource(String source) {
        if (source.equals(SOURCE_MANUAL)) {
            manualData.setEnabled(true);
            contactData.setEnabled(false);
        } else {
            manualData.setEnabled(false);
            contactData.setEnabled(true);
        }
    }

    private void getVCard(Uri uri) {
        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(
                    getContentResolver().openAssetFileDescriptor(uri, "r")
                    .createInputStream()));
            String l;
            do {
                l = r.readLine();
                if (l != null) {
                    Log.i(TAG, "getVCard(): " + l);
                }
            } while (l != null);
        } catch (Exception e) {
            Log.i(TAG, "Exception...", e);
        }
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri contactUri = data.getData();
            Log.i(TAG, "uri: " + contactUri);
            String key = null;
            Cursor c =  managedQuery(contactUri, null, null, null, null);
            if (c.moveToFirst()) {
                key = c.getString(
                        c.getColumnIndexOrThrow(Contacts.LOOKUP_KEY));
                Log.i(TAG, "key: " + key);
                SharedPreferences.Editor prefs =
                        getPreferenceManager().getSharedPreferences().edit();
                prefs.putString("qrContactData", key);
                prefs.commit();
                String displayName = c.getString(
                        c.getColumnIndexOrThrow(Contacts.DISPLAY_NAME));
                Log.i(TAG, "display name: " + displayName);
                contactData.setSummary(displayName);
            }
            Log.i(TAG, "vcard " + Contacts.CONTENT_VCARD_URI);
            Uri vCardUri = Uri.withAppendedPath(Contacts.CONTENT_VCARD_URI, key);
            Log.i(TAG, "uri   " + vCardUri.toString());
            getVCard(vCardUri);
        }
    }

    @Override
    public void onBackPressed() {
        Context context = this;

        QrData qrData = loadConf(context, mWidgetId);

        if (qrData.data == null) {
            Log.i(TAG, "No data provided for QR code.");
            setResult(RESULT_CANCELED);
            super.onBackPressed();
            return;
        }

        AppWidgetManager appWidgetManager = 
                AppWidgetManager.getInstance(context);
        QrAppWidgetProvider.updateAppWidget(context, appWidgetManager,
                                            mWidgetId, qrData);
        // Make sure we pass back the original appWidgetId
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mWidgetId);
        setResult(RESULT_OK, resultValue);

        super.onBackPressed();
    }

    static QrData loadConf(Context context, int widgetId) {
        SharedPreferences prefs = 
                context.getSharedPreferences(prefsName(widgetId), 0);
        String data;
        if (SOURCE_MANUAL.equals(prefs.getString(SOURCE, null))) {
            data = prefs.getString(MANUAL, null);
        } else {
            data = prefs.getString(CONTACT, null);
        }

        return new QrData(data, prefs.getString(LABEL, ""));
    }

}
