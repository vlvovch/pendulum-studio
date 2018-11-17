package com.vlvolad.pendulumstudio;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by Volodymyr on 28.05.2017.
 */
public class SettingsActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.settings);
        addPreferencesFromResource(R.xml.preferences);
    }
}
