package com.github.qrgen;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.provider.Contacts;
import android.util.Log;

public class WidgetConfigure extends PreferenceActivity {

    private static final String TAG = "qrgen";

    private Preference manualData;
    private Preference contactData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.widget_configure);

        manualData = findPreference("qrManualData");
        contactData = findPreference("qrContactData");

        ListPreference source = (ListPreference)findPreference("qrSource");
        updateSource(source.getValue());
        source.setOnPreferenceChangeListener(
                new Preference.OnPreferenceChangeListener() {
                    public boolean onPreferenceChange(Preference p, Object o) {
                        updateSource((String)o);
                        return true;
                    }
                });

        contactData.setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
                    public boolean onPreferenceClick (Preference preference) {
                        Intent intent = new Intent(Intent.ACTION_PICK, 
                               Contacts.People.CONTENT_URI);
                        startActivityForResult(intent, 0);
                        return true;
                    }
                });
    }

    private void updateSource(String source) {
        if (source.equals(getResources().getString(R.string.sourceManual))) {
            manualData.setEnabled(true);
            contactData.setEnabled(false);
        } else {
            manualData.setEnabled(false);
            contactData.setEnabled(true);
        }
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri contactData = data.getData();
            Log.i(TAG, "uri: " + contactData);
            Cursor c =  managedQuery(contactData, null, null, null, null);
            if (c.moveToFirst()) {
                String name = c.getString(c.getColumnIndexOrThrow(Contacts.People.NAME));
                Log.i(TAG, "name: " + name);
            }
        }
    }

}
