package com.vlvolad.pendulumstudio.springsphericalpendulum;

import android.content.SharedPreferences;

public class SSPSimulationParameters {
	public static final String PREFS_NAME = "SSPPrefsFile";
	
	public static SSPSimulationParameters simParams = new SSPSimulationParameters(0.50f, 0.75f, 1.f, 1.f, 981.f, 40.f, 0.020f, true, 
			true, 0.f, 0.f, 0.5f,
			0.f, 0.f, 0.f,
			(float)(45. * Math.PI / 180.), (float)(0. * Math.PI / 180.), 
			(float)(90. * Math.PI / 180.), (float)(0. * Math.PI / 180.), 100, false);
	public volatile float aa, m1, l, m2, g, k, gam;
	public volatile boolean initRandom, showTrajectory, infiniteTrajectory;
	public volatile float x, y, z, xv, yv, zv;
	public volatile float th1, thv1, ph1, phv1;
	public volatile int traceLength;
    public volatile int pendulumColor, pendulumColor2;
	SSPSimulationParameters(float set_aa, float set_l, float set_m1, float set_m2, float set_g, float set_k, float set_gam,
			boolean set_random, boolean set_trajectory, 
			float set_x, float set_y, float set_z,
			float set_xv, float set_yv, float set_zv,
			float set_th, float set_thv,
			float set_ph, float set_phv, int trLength, boolean infinite_trajectory)
		{
			aa = set_aa;
			m1 = set_m1;
			l = set_l;
			m2 = set_m2;
			gam = set_gam;
			g = set_g;
			k = set_k;
			initRandom = set_random;
			showTrajectory = set_trajectory;
			x = set_x;
			y = set_y;
			z = set_z;
			xv = set_xv;
			yv = set_yv;
			zv = set_zv;
			th1 = set_th;
			thv1 = set_thv;
			ph1 = set_ph;
			phv1 = set_phv;
			traceLength = trLength;
			infiniteTrajectory = infinite_trajectory;
		}
	SSPSimulationParameters()
		{
			initRandom = true;
		}
	
	public void readSettings(SharedPreferences settings) {
		
		aa = settings.getFloat("aa", 0.50f);
		k = settings.getFloat("k", 40.f);
		l = settings.getFloat("l", 0.75f);
		m1 = settings.getFloat("m1", 1.f);
		m2 = settings.getFloat("m2", 1.f);
		g = settings.getFloat("g", 981.f);
		gam = settings.getFloat("gam", 0.020f);

        if (aa < 0.)  aa   = 0.50f;
        if (k  < 0.)  k    = 40.f;
        if (l  <= 0.) l    = 0.75f;
        if (m1 <= 0.) m1   = 1.f;
        if (m2 <= 0.) m2   = 1.f;
        if (g  < 0.)  g    = 981.f;
        if (gam< 0.)  gam  = 0.020f;
		
		initRandom = settings.getBoolean("initRandom", true);
		showTrajectory = settings.getBoolean("showTrajectory", true);
		infiniteTrajectory = settings.getBoolean("infiniteTrajectory", false);
		
		x = settings.getFloat("x", 0.0f);
		xv = settings.getFloat("xv", 0.f);
		y = settings.getFloat("y", 0.0f);
		yv = settings.getFloat("yv", 0.f);
		z = settings.getFloat("z", 0.50f);
		zv = settings.getFloat("zv", 0.f);
		th1 = settings.getFloat("th1", (float)(45. * Math.PI / 180.));
		thv1 = settings.getFloat("thv1", (float)(0. * Math.PI / 180.));
		ph1 = settings.getFloat("ph1", (float)(90. * Math.PI / 180.));
		phv1 = settings.getFloat("phv1", (float)(0. * Math.PI / 180.));
		
		traceLength = settings.getInt("traceLength", 100);
        if (traceLength <= 0) traceLength = 100;
        if (traceLength > 100000) traceLength = 100000;

        pendulumColor = settings.getInt("pendulumColor", 0xFFFF0000);
        pendulumColor2 = settings.getInt("pendulumColor2", 0xFF0000FF);
		
	}
	
	public void writeSettings(SharedPreferences settings) {
		SharedPreferences.Editor editor = settings.edit();
		editor.putFloat("a", aa);
		editor.putFloat("k", k);
		editor.putFloat("l", l);
		editor.putFloat("m1", m1);
		editor.putFloat("m2", m2);
		editor.putFloat("g", g);
		editor.putFloat("gam", gam);
		editor.putBoolean("initRandom", initRandom);
		editor.putBoolean("showTrajectory", showTrajectory);
		editor.putBoolean("infiniteTrajectory", infiniteTrajectory);
		editor.putFloat("x", x);
		editor.putFloat("xv", xv);
		editor.putFloat("y", y);
		editor.putFloat("yv", yv);
		editor.putFloat("z", z);
		editor.putFloat("zv", zv);
		editor.putFloat("th1", th1);
		editor.putFloat("thv1", thv1);
		editor.putFloat("ph1", ph1);
		editor.putFloat("phv1", phv1);
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
