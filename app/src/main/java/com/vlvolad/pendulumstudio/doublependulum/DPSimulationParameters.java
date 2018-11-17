package com.vlvolad.pendulumstudio.doublependulum;

import android.content.SharedPreferences;

public class DPSimulationParameters {
	public static final String PREFS_NAME = "DPPrefsFile";
	
	public static DPSimulationParameters simParams = new DPSimulationParameters(0.75f, 0.75f, 1.f, 1.f, 981.f, 0.020f, true, 
			true, (float)(45. * Math.PI / 180.), (float)(0. * Math.PI / 180.), 
			(float)(5. * Math.PI / 8.), (float)(90. * Math.PI / 180.), 
			100, false);
	public volatile float l1, m1, l2, m2, g, k;
	public volatile boolean initRandom, showTrajectory, infiniteTrajectory;
	public volatile float th1, thv1, ph1, phv1;
	public volatile float th2, thv2, ph2, phv2;
	public volatile int traceLength;
    public volatile int pendulumColor, pendulumColor2;
	DPSimulationParameters(float set_l1, float set_l2, float set_m1, float set_m2, float set_g, float set_k,
			boolean set_random, boolean set_trajectory, float set_th1, float set_thv1,
			float set_th2, float set_thv2,
			int trLength, boolean infinite_trajectory)
		{
			l1 = set_l1;
			m1 = set_m1;
			l2 = set_l2;
			m2 = set_m2;
			g = set_g;
			k = set_k;
			initRandom = set_random;
			showTrajectory = set_trajectory;
			th1 = set_th1;
			thv1 = set_thv1;
			th2 = set_th2;
			thv2 = set_thv2;
			traceLength = trLength;
			infiniteTrajectory = infinite_trajectory;
		}
	DPSimulationParameters()
		{
			initRandom = true;
		}
	
	public void readSettings(SharedPreferences settings) {
		
		l1 = settings.getFloat("l1", 1.f);
		m1 = settings.getFloat("m1", 1.f);
		l2 = settings.getFloat("l2", 1.f);
		m2 = settings.getFloat("m2", 1.f);
		g = settings.getFloat("g", 981.f);
		k = settings.getFloat("k", 0.020f);

        if (l1 <= 0.) l1 = 1.f;
        if (l2 <= 0.) l2 = 1.f;
        if (m1 <= 0.) m1 = 1.f;
        if (m2 <= 0.) m2 = 1.f;
        if (g  < 0.)  g  = 981.f;
        if (k  < 0.)  k  = 0.020f;

		
		initRandom = settings.getBoolean("initRandom", true);
		showTrajectory = settings.getBoolean("showTrajectory", true);
		infiniteTrajectory = settings.getBoolean("infiniteTrajectory", false);
		
		th1 = settings.getFloat("th1", (float)(45. * Math.PI / 180.));
		thv1 = settings.getFloat("thv1", (float)(0. * Math.PI / 180.));
		th2 = settings.getFloat("th2", (float)(5. * Math.PI / 8.));
		thv2 = settings.getFloat("thv2", (float)(90. * Math.PI / 180.));
		
		traceLength = settings.getInt("traceLength", 100);
        if (traceLength <= 0) traceLength = 100;
        if (traceLength > 100000) traceLength = 100000;

        pendulumColor = settings.getInt("pendulumColor", 0xFFFF0000);
        pendulumColor2 = settings.getInt("pendulumColor2", 0xFF0000FF);
		
	}
	
	public void writeSettings(SharedPreferences settings) {
		SharedPreferences.Editor editor = settings.edit();
		editor.putFloat("l1", l1);
		editor.putFloat("m1", m1);
		editor.putFloat("l2", l2);
		editor.putFloat("m2", m2);
		editor.putFloat("g", g);
		editor.putFloat("k", k);
		editor.putBoolean("initRandom", initRandom);
		editor.putBoolean("showTrajectory", showTrajectory);
		editor.putBoolean("infiniteTrajectory", infiniteTrajectory);
		editor.putFloat("th1", th1);
		editor.putFloat("thv1", thv1);
		editor.putFloat("th2", th2);
		editor.putFloat("thv2", thv2);
		editor.putInt("traceLength", traceLength);
        editor.putInt("pendulumColor", pendulumColor);
        editor.putInt("pendulumColor2", pendulumColor2);
		
		editor.commit();
	}

    public void clearSettings(SharedPreferences settings) {

        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.commit();
        readSettings(settings);

    }
}
