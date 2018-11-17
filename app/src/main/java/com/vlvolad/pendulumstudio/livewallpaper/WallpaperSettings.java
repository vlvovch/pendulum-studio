package com.vlvolad.pendulumstudio.livewallpaper;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.widget.Toast;

import com.vlvolad.pendulumstudio.R;

public class WallpaperSettings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.wallpaper_preferences);

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
        String pendulum = PreferenceManager.getDefaultSharedPreferences(
                this).getString("pendulum_selection", "5");
        cleanAndPopulate();
        if (pendulum.equals("8")) {
            getPreferenceScreen().findPreference("pref_NP").setSummary(PreferenceManager.getDefaultSharedPreferences(
                    this).getString("pref_NP", "12"));
            getPreferenceScreen().findPreference("pref_NT").setSummary(PreferenceManager.getDefaultSharedPreferences(
                    this).getString("pref_NT", "40"));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        cleanAndPopulate();
        //getPreferenceScreen().findPreference("wallpaper_pendulum_color_2").setEnabled(twoPendulums());
    }

    boolean twoPendulums() {
        String pendulum = PreferenceManager.getDefaultSharedPreferences(
                this).getString("pendulum_selection", "5");

        if (pendulum.equals("0") || pendulum.equals("1") || pendulum.equals("2") || pendulum.equals("3") || pendulum.equals("8")) return false;
        else return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        String pendulum = PreferenceManager.getDefaultSharedPreferences(
                this).getString("pendulum_selection", "5");

        if (key.equals("pendulum_selection")) {
            cleanAndPopulate();
            if (pendulum.equals("8")) {
            }
        }

        if (pendulum.equals("8")) {
            if (key.equals("pref_NP")) {
                if (Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString("pref_NP", "12"))<=0) {
                    PreferenceManager.getDefaultSharedPreferences(this).edit().putString("pref_NP", "12").commit();
                }
                if (Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString("pref_NP", "12"))>100) {
                    PreferenceManager.getDefaultSharedPreferences(this).edit().putString("pref_NP", "100").commit();
                    Context context = getApplicationContext();
                    CharSequence text = getString(R.string.PWLimit);//"Trace too long! Setting to 100000...";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
                getPreferenceScreen().findPreference("pref_NP").setSummary(PreferenceManager.getDefaultSharedPreferences(
                        this).getString("pref_NP", "12"));

            }
            if (key.equals("pref_NT"))
                if (Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString("pref_NT", "40"))<=0) {
                    PreferenceManager.getDefaultSharedPreferences(this).edit().putString("pref_NT", "40").commit();
                }
                getPreferenceScreen().findPreference("pref_NT").setSummary(PreferenceManager.getDefaultSharedPreferences(
                        this).getString("pref_NT", "40"));
        }

    }

    private void cleanAndPopulate() {
        String pendulum = PreferenceManager.getDefaultSharedPreferences(
                this).getString("pendulum_selection", "5");
        PreferenceScreen screen = getPreferenceScreen();
        Preference pref = getPreferenceManager().findPreference("pendulum_selection");

        screen.removeAll();
        addPreferencesFromResource(R.xml.wallpaper_preferences);

        if (pendulum.equals("8")) {

            pref = getPreferenceManager().findPreference("show_trace");
            screen.removePreference(pref);
            pref = getPreferenceManager().findPreference("use_accelerometer");
            screen.removePreference(pref);
            pref = getPreferenceManager().findPreference("wallpaper_pendulum_color_1");
            screen.removePreference(pref);
            pref = getPreferenceManager().findPreference("wallpaper_pendulum_color_2");
            screen.removePreference(pref);

            getPreferenceScreen().findPreference("pref_NP").setSummary(PreferenceManager.getDefaultSharedPreferences(
                    this).getString("pref_NP", "12"));
            getPreferenceScreen().findPreference("pref_NT").setSummary(PreferenceManager.getDefaultSharedPreferences(
                    this).getString("pref_NT", "40"));
        }
        else {
            pref = getPreferenceManager().findPreference("pref_NP");
            screen.removePreference(pref);
            pref = getPreferenceManager().findPreference("pref_NT");
            screen.removePreference(pref);
            pref = getPreferenceManager().findPreference("wallpaper_pendulum_color_2");
            if (!twoPendulums()) screen.removePreference(pref);
            pref = getPreferenceManager().findPreference("wallpaper_pendulum_color_wave");
            screen.removePreference(pref);
        }


    }
}
