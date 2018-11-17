package com.vlvolad.pendulumstudio.livewallpaper;

import android.opengl.GLSurfaceView.Renderer;

import android.preference.PreferenceManager;
import android.util.Log;

import com.vlvolad.pendulumstudio.mathematicalpendulum.MPGLRenderer;
import com.vlvolad.pendulumstudio.sphericalpendulum.SPGLRenderer;
import com.vlvolad.pendulumstudio.springmathematicalpendulum.SMPGLRenderer;
import com.vlvolad.pendulumstudio.springpendulum2d.SP2DGLRenderer;
import com.vlvolad.pendulumstudio.springpendulum3d.SP3DGLRenderer;
import com.vlvolad.pendulumstudio.springsphericalpendulum.SSPGLRenderer;
import com.vlvolad.pendulumstudio.doublependulum.DPGLRenderer;
import com.vlvolad.pendulumstudio.doublesphericalpendulum.DSPGLRenderer;

public class PendulumStudioWallpaperService extends OpenGLES2WallpaperService {
    private String currentPendulum;
	@Override
	Renderer getNewRenderer() {
        currentPendulum = PreferenceManager.getDefaultSharedPreferences(
                this).getString("pendulum_selection", "5");
//        Log.d("getNewRenderer()", currentPendulum);
        if (currentPendulum.equals("0")) return new PendulumRenderer(PendulumRenderer.mPendulumMP);
        else if (currentPendulum.equals("1")) return new PendulumRenderer(PendulumRenderer.mPendulumSP);
        else if (currentPendulum.equals("2")) return new PendulumRenderer(PendulumRenderer.mPendulumSP2D);
        else if (currentPendulum.equals("3")) return new PendulumRenderer(PendulumRenderer.mPendulumSP3D);
        else if (currentPendulum.equals("4")) return new PendulumRenderer(PendulumRenderer.mPendulumDP);
        else if (currentPendulum.equals("5")) return new PendulumRenderer(PendulumRenderer.mPendulumDSP);
        else if (currentPendulum.equals("6")) return new PendulumRenderer(PendulumRenderer.mPendulumSMP);
        else if (currentPendulum.equals("7")) return new PendulumRenderer(PendulumRenderer.mPendulumSSP);
        else return new PendulumRenderer(PendulumRenderer.mPendulumPW);
	}

    @Override
    boolean needNewPendulum() {
        String pendulum = PreferenceManager.getDefaultSharedPreferences(
                this).getString("pendulum_selection", "5");
//        Log.v("Wallpaper", "Current pendulum: " + currentPendulum);
//        Log.v("Wallpaper", "New pendulum: " + pendulum);
        return !pendulum.equals(currentPendulum);
    }
}
