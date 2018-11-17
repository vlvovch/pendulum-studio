package com.vlvolad.pendulumstudio.pendulumwave;

import android.content.SharedPreferences;

/**
 * Created by Volodymyr on 26.05.2015.
 */
public class PWSimulationParameters {
    public static final String PREFS_NAME = "PWPrefsFile";

    public static PWSimulationParameters simParams = new PWSimulationParameters(12, 40, 1.f, 1.f, 981.f, 0.020f, true,
            (float)(30. * Math.PI / 180.));
    public volatile int NP, NT;
    public volatile float l, m, g, k;
    public volatile boolean initRandom;
    public volatile float th0;
    public volatile int pendulumColor;
    PWSimulationParameters(int set_np, int set_nt, float set_l, float set_m, float set_g, float set_k,
                           boolean set_random, float set_th0)
    {
        NP = set_np;
        NT = set_nt;
        l = set_l;
        m = set_m;
        g = set_g;
        k = set_k;
        initRandom = set_random;
        th0 = set_th0;
    }
    PWSimulationParameters()
    {
        initRandom = true;
    }

    public void readSettings(SharedPreferences settings) {

        NP = settings.getInt("NP", 12);
        if (NP<=0)  NP = 12;
        if (NP>100) NP = 100;
        NT = settings.getInt("NT", 40);
        if (NT<=0) NT = 40;

        l = settings.getFloat("l", 1.f);
        m = settings.getFloat("m", 1.f);
        g = settings.getFloat("g", 981.f);
        k = settings.getFloat("k", 0.020f);

        if (l  <= 0.) l  = 1.f;
        if (m  <= 0.) m  = 1.f;
        if (g  < 0.)  g  = 981.f;
        if (k  < 0.)  k  = 0.020f;

        initRandom = settings.getBoolean("initRandom", true);

        th0 = settings.getFloat("th0", (float)(30. * Math.PI / 180.));

        pendulumColor = settings.getInt("pendulumColor", 0xFF0000FF);

    }

    public void writeSettings(SharedPreferences settings) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putFloat("l", l);
        editor.putInt("NP", NP);
        editor.putInt("NT", NT);
        editor.putFloat("m", m);
        editor.putFloat("g", g);
        editor.putFloat("k", k);
        editor.putBoolean("initRandom", initRandom);
        editor.putFloat("th0", th0);
        editor.putInt("pendulumColor", pendulumColor);

        editor.commit();
    }

    public void clearSettings(SharedPreferences settings) {

        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.commit();
        readSettings(settings);

    }
}
