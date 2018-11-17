package com.vlvolad.pendulumstudio.springpendulum3d;

import android.content.SharedPreferences;

public class SP3DSimulationParameters {
	public static final String PREFS_NAME = "SP3DPrefsFile";
	
	public static SP3DSimulationParameters simParams = new SP3DSimulationParameters(0.50f, 40.f,
			1.f, 981.f, 0.020f, true, 
			true, 
			0.50f, 0.f,
			0.50f, 0.f,
			0.50f, 0.f,
			100, false);
	public volatile float aa, m, g, k, gam;
	public volatile boolean initRandom, showTrajectory, infiniteTrajectory;
	public volatile float x, xv;
	public volatile float y, yv;
	public volatile float z, zv;
	public volatile int traceLength;
    public volatile int pendulumColor;
	SP3DSimulationParameters(float set_aa, float set_k, float set_m,
			float set_g, float set_gam,
			boolean set_random, boolean set_trajectory, 
			float set_x, float set_xv,
			float set_y, float set_yv,
			float set_z, float set_zv,
			int trLength, boolean infinite_trajectory)
		{
			aa = set_aa;
			m = set_m;
			g = set_g;
			k = set_k;
			gam = set_gam;
			initRandom = set_random;
			showTrajectory = set_trajectory;
			x = set_x;
			xv = set_xv;
			y = set_y;
			yv = set_yv;
			z = set_z;
			zv = set_zv;
			traceLength = trLength;
			infiniteTrajectory = infinite_trajectory;
		}
	SP3DSimulationParameters()
		{
			initRandom = true;
		}
	
	public void readSettings(SharedPreferences settings) {
		
		aa = settings.getFloat("aa", 0.50f);
		k = settings.getFloat("k", 40.f);
		m = settings.getFloat("m", 1.f);
		g = settings.getFloat("g", 981.f);
		gam = settings.getFloat("gam", 0.020f);

        if (aa <  0.)  aa   = 0.50f;
        if (k  <  0.)  k    = 40.f;
        if (m  <= 0.)  m    = 1.f;
        if (g  <  0.)  g    = 981.f;
        if (gam<  0.)  gam  = 0.020f;
		
		initRandom = settings.getBoolean("initRandom", true);
		showTrajectory = settings.getBoolean("showTrajectory", true);
		infiniteTrajectory = settings.getBoolean("infiniteTrajectory", false);
		
		x = settings.getFloat("x", 0.50f);
		xv = settings.getFloat("xv", 0.f);
		y = settings.getFloat("y", 0.50f);
		yv = settings.getFloat("yv", 0.f);
		z = settings.getFloat("z", 0.50f);
		zv = settings.getFloat("zv", 0.f);
		
		traceLength = settings.getInt("traceLength", 100);
        if (traceLength <= 0) traceLength = 100;
        if (traceLength > 100000) traceLength = 100000;

        pendulumColor = settings.getInt("pendulumColor", 0xFFFF0000);
		
	}
	
	public void writeSettings(SharedPreferences settings) {
		SharedPreferences.Editor editor = settings.edit();
		editor.putFloat("a", aa);
		editor.putFloat("k", k);
		editor.putFloat("m", m);
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
