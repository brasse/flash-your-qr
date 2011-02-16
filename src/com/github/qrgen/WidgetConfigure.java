package com.github.qrgen;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;

public class WidgetConfigure extends PreferenceActivity {

    private static final String TAG = "qrgen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.widget_configure);
        Preference manual = findPreference("qrManual");
        Preference contact = findPreference("qrContact");
        manual.setOnPreferenceChangeListener(
                new Preference.OnPreferenceChangeListener() {
                    public boolean onPreferenceChange(Preference p, Object o) {
                        Log.i(TAG, "change: " + o);
                        return true;
                    }
                });
    }

}
