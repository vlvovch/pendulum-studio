package com.vlvolad.pendulumstudio.springmathematicalpendulum;

import android.content.SharedPreferences;

public class SMPSimulationParameters {
	public static final String PREFS_NAME = "SMPPrefsFile";
	
	public static SMPSimulationParameters simParams = new SMPSimulationParameters(0.50f, 40.f, 0.75f, 
			1.f, 1.f, 981.f, 0.020f, true, 
			true, 
			0.50f, 0.f,
			(float)(45. * Math.PI / 180.), (float)(0. * Math.PI / 180.), 
			(float)(5. * Math.PI / 8.), (float)(90. * Math.PI / 180.), 
			100, false);
	public volatile float aa, m1, l, m2, g, k, gam;
	public volatile boolean initRandom, showTrajectory, infiniteTrajectory;
	public volatile float s, sv;
	public volatile float th1, thv1;
	public volatile float th2, thv2;
	public volatile int traceLength;
    public volatile int pendulumColor, pendulumColor2;
	SMPSimulationParameters(float set_aa, float set_k, float set_l, float set_m1, float set_m2, 
			float set_g, float set_gam,
			boolean set_random, boolean set_trajectory, 
			float set_s, float set_sv,
			float set_th1, float set_thv1,
			float set_th2, float set_thv2,
			int trLength, boolean infinite_trajectory)
		{
			aa = set_aa;
			m1 = set_m1;
			l = set_l;
			m2 = set_m2;
			g = set_g;
			k = set_k;
			gam = set_gam;
			initRandom = set_random;
			showTrajectory = set_trajectory;
			s = set_s;
			sv = set_sv;
			th1 = set_th1;
			thv1 = set_thv1;
			th2 = set_th2;
			thv2 = set_thv2;
			traceLength = trLength;
			infiniteTrajectory = infinite_trajectory;
		}
	SMPSimulationParameters()
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
        if (l  <= 0.) l    = 1.f;
        if (m1 <= 0.) m1   = 1.f;
        if (m2 <= 0.) m2   = 1.f;
        if (g  < 0.)  g    = 981.f;
        if (gam< 0.)  gam  = 0.020f;
		
		initRandom = settings.getBoolean("initRandom", true);
		showTrajectory = settings.getBoolean("showTrajectory", true);
		infiniteTrajectory = settings.getBoolean("infiniteTrajectory", false);
		
		s = settings.getFloat("s", 0.50f);
		sv = settings.getFloat("sv", 0.f);
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
		editor.putFloat("s", s);
		editor.putFloat("sv", sv);
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
