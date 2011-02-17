package com.github.qrgen;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
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
        source.setOnPreferenceChangeListener(
                new Preference.OnPreferenceChangeListener() {
                    public boolean onPreferenceChange(Preference p, Object o) {
                        updateSource((String)o);
                        return true;
                    }
                });
        updateSource(source.getValue());
    }

    private void updateSource(String source) {
        Log.i(TAG, "update: " + source);
        if (source.equals("sourceManual")) {
            manualData.setEnabled(true);
            contactData.setEnabled(false);
        } else {
            manualData.setEnabled(false);
            contactData.setEnabled(true);
        }
    }

}
