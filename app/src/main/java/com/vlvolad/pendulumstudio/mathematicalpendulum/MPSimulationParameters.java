package com.vlvolad.pendulumstudio.mathematicalpendulum;

import android.content.SharedPreferences;

public class MPSimulationParameters {
	public static final String PREFS_NAME = "MPPrefsFile";
	
	public static MPSimulationParameters simParams = new MPSimulationParameters(1.f, 1.f, 981.f, 0.020f, true, 
			true, (float)(45. * Math.PI / 180.), (float)(0. * Math.PI / 180.), 
			3000, false);
	public volatile float l, m, g, k;
	public volatile boolean initRandom, showTrajectory, infiniteTrajectory;
	public volatile float th0, thv0;
	public volatile int traceLength;
    public volatile int pendulumColor;
	MPSimulationParameters(float set_l, float set_m, float set_g, float set_k,
			boolean set_random, boolean set_trajectory, float set_th0, float set_thv0,
			int trLength, boolean infinite_trajectory)
		{
			l = set_l;
			m = set_m;
			g = set_g;
			k = set_k;
			initRandom = set_random;
			showTrajectory = set_trajectory;
			th0 = set_th0;
			thv0 = set_thv0;
			traceLength = trLength;
			infiniteTrajectory = infinite_trajectory;
		}
	MPSimulationParameters()
		{
			initRandom = true;
		}
	
	public void readSettings(SharedPreferences settings) {
		
		l = settings.getFloat("l", 1.f);
		m = settings.getFloat("m", 1.f);
		g = settings.getFloat("g", 981.f);
		k = settings.getFloat("k", 0.020f);

        if (l <= 0.)  l  = 1.f;
        if (m <= 0.)  m  = 1.f;
        if (g  < 0.)  g  = 981.f;
        if (k  < 0.)  k  = 0.020f;
		
		initRandom = settings.getBoolean("initRandom", true);
		showTrajectory = settings.getBoolean("showTrajectory", true);
		infiniteTrajectory = settings.getBoolean("infiniteTrajectory", false);
		
		th0 = settings.getFloat("th0", (float)(45. * Math.PI / 180.));
		thv0 = settings.getFloat("thv0", (float)(0. * Math.PI / 180.));
		
		traceLength = settings.getInt("traceLength", 100);
        if (traceLength <= 0) traceLength = 100;
        if (traceLength > 100000) traceLength = 100000;

        pendulumColor = settings.getInt("pendulumColor", 0xFFFF0000);
		
	}
	
	public void writeSettings(SharedPreferences settings) {
		SharedPreferences.Editor editor = settings.edit();
		editor.putFloat("l", l);
		editor.putFloat("m", m);
		editor.putFloat("g", g);
		editor.putFloat("k", k);
		editor.putBoolean("initRandom", initRandom);
		editor.putBoolean("showTrajectory", showTrajectory);
		editor.putBoolean("infiniteTrajectory", infiniteTrajectory);
		editor.putFloat("th0", th0);
		editor.putFloat("thv0", thv0);
		editor.putInt("traceLength", traceLength);
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
