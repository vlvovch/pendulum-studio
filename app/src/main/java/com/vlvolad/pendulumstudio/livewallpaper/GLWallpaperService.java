package com.vlvolad.pendulumstudio.livewallpaper;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Build;
import android.service.wallpaper.WallpaperService;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import android.preference.PreferenceManager;

import com.vlvolad.pendulumstudio.common.GenericPendulum;
import com.vlvolad.pendulumstudio.pendulumwave.PendulumWave;

public abstract class GLWallpaperService extends WallpaperService implements SensorEventListener {

    private GenericPendulum mPendulum;
    private SensorManager mSensorManager;
    private Sensor mGravity;
    private boolean useDynGravity;
    private Display display;

    GenericPendulum getCurrentPendulum() {
        String pendulum = PreferenceManager.getDefaultSharedPreferences(
                this).getString("pendulum_selection", "5");
        if (pendulum.equals("0")) return (GenericPendulum) PendulumRenderer.mPendulumMP;
        else if (pendulum.equals("1")) return (GenericPendulum) PendulumRenderer.mPendulumSP;
        else if (pendulum.equals("2")) return (GenericPendulum) PendulumRenderer.mPendulumSP2D;
        else if (pendulum.equals("3")) return (GenericPendulum) PendulumRenderer.mPendulumSP3D;
        else if (pendulum.equals("4")) return (GenericPendulum) PendulumRenderer.mPendulumDP;
        else if (pendulum.equals("5")) return (GenericPendulum) PendulumRenderer.mPendulumDSP;
        else if (pendulum.equals("6")) return (GenericPendulum) PendulumRenderer.mPendulumSMP;
        else if (pendulum.equals("7")) return (GenericPendulum) PendulumRenderer.mPendulumSSP;
        else return (GenericPendulum) PendulumRenderer.mPendulumPW;
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        // The light sensor returns a single value.
        // Many sensors return 3 values, one for each axis.
        int rotmode = display.getRotation();
        if (rotmode==Surface.ROTATION_0) mPendulum.setGravity(event.values[0], event.values[1], event.values[2]);
        else if (rotmode==Surface.ROTATION_90) mPendulum.setGravity(-event.values[1], event.values[0], event.values[2]);
        else if (rotmode==Surface.ROTATION_180) mPendulum.setGravity(event.values[0], -event.values[1], event.values[2]);
        else if (rotmode==Surface.ROTATION_270) mPendulum.setGravity(event.values[1], -event.values[0], event.values[2]);
        // Do something with this sensor value.
    }

	public class GLEngine extends Engine {
        private PendulumRenderer mRenderer;

		class WallpaperGLSurfaceView extends GLSurfaceView {
			private static final String TAG = "WallpaperGLSurfaceView";

			WallpaperGLSurfaceView(Context context) {
				super(context);

//				if (LoggerConfig.ON) {
//					Log.d(TAG, "WallpaperGLSurfaceView(" + context + ")");
//				}
			}

			@Override
			public SurfaceHolder getHolder() {
//				if (LoggerConfig.ON) {
//					Log.d(TAG, "getHolder(): returning " + getSurfaceHolder());
//				}

				return getSurfaceHolder();
			}

			public void onDestroy() {
//				if (LoggerConfig.ON) {
//					Log.d(TAG, "onDestroy()");
//				}

				super.onDetachedFromWindow();
                mSensorManager.unregisterListener(GLWallpaperService.this);
			}
		}

		private static final String TAG = "GLEngine";

		private WallpaperGLSurfaceView glSurfaceView;
		private boolean rendererHasBeenSet;



		@Override
		public void onCreate(SurfaceHolder surfaceHolder) {
//			if (LoggerConfig.ON) {
//				Log.d(TAG, "onCreate(" + surfaceHolder + ")");
//			}

			super.onCreate(surfaceHolder);

            mPendulum = getCurrentPendulum();

			glSurfaceView = new WallpaperGLSurfaceView(GLWallpaperService.this);

            mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            //mGravity = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
            mGravity = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            useDynGravity = PreferenceManager.getDefaultSharedPreferences(
                    GLWallpaperService.this).getBoolean("use_accelerometer", true);
            boolean PWP = PreferenceManager.getDefaultSharedPreferences(
                    GLWallpaperService.this).getString("pendulum_selection", "5").equals("8");
            if (PWP) useDynGravity = false;
            mPendulum.setGravityMode(useDynGravity);
            if (mPendulum.firsttime) {
                if (PreferenceManager.getDefaultSharedPreferences(GLWallpaperService.this).getString("pendulum_selection", "5").equals("8"))
                ((PendulumWave) mPendulum).restartPW(Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(GLWallpaperService.this).getString("pref_NP", "12")),
                        Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(GLWallpaperService.this).getString("pref_NT", "40")),
                        30./180.*Math.PI);
                else mPendulum.restart();
            }
            mPendulum.setColorPendulum1(PreferenceManager.getDefaultSharedPreferences(GLWallpaperService.this).getInt("wallpaper_pendulum_color_1", 0xFFFF0000));
            mPendulum.setColorPendulum2(PreferenceManager.getDefaultSharedPreferences(GLWallpaperService.this).getInt("wallpaper_pendulum_color_2", 0xFF0000FF));
            if (PreferenceManager.getDefaultSharedPreferences(GLWallpaperService.this).getString("pendulum_selection", "5").equals("8")) mPendulum.setColorPendulum1(PreferenceManager.getDefaultSharedPreferences(GLWallpaperService.this).getInt("wallpaper_pendulum_color_wave", 0xFF0000FF));
            display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		}

		@Override
		public void onVisibilityChanged(boolean visible) {
			super.onVisibilityChanged(visible);

			if (rendererHasBeenSet) {
				if (visible) {
                        glSurfaceView.queueEvent(new Runnable() {
                            // This method will be called on the rendering
                            // thread:
                            public void run() {
                                String oname = mRenderer.mPendulum.name;
                                mRenderer.switchPendulum(PreferenceManager.getDefaultSharedPreferences(
                                        GLWallpaperService.this).getString("pendulum_selection", "5"),
                                        PreferenceManager.getDefaultSharedPreferences(
                                                GLWallpaperService.this).getBoolean("use_damping", true),
                                        PreferenceManager.getDefaultSharedPreferences(
                                                GLWallpaperService.this).getBoolean("show_trace", true));
                                if (mRenderer.mPendulum.firsttime && !PreferenceManager.getDefaultSharedPreferences(GLWallpaperService.this).getString("pendulum_selection", "5").equals("8")) mRenderer.mPendulum.restart();
                                if (mRenderer.mPendulum.firsttime && PreferenceManager.getDefaultSharedPreferences(GLWallpaperService.this).getString("pendulum_selection", "5").equals("8"))
                                    ((PendulumWave) mPendulum).restartPW(Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(GLWallpaperService.this).getString("pref_NP", "12")),
                                            Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(GLWallpaperService.this).getString("pref_NT", "40")),
                                            30./180.*Math.PI);
                                if ((mRenderer.mPendulum.firsttime || !oname.equals(mRenderer.mPendulum.name)) && PreferenceManager.getDefaultSharedPreferences(GLWallpaperService.this).getString("pendulum_selection", "5").equals("8"))
                                    ((PendulumWave) mPendulum).restartPW(Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(GLWallpaperService.this).getString("pref_NP", "12")),
                                            Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(GLWallpaperService.this).getString("pref_NT", "40")),
                                            30./180.*Math.PI);
                                else if (PreferenceManager.getDefaultSharedPreferences(GLWallpaperService.this).getString("pendulum_selection", "5").equals("8")
                                        && (Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(GLWallpaperService.this).getString("pref_NP", "12"))!=((PendulumWave)mRenderer.mPendulum).NP || Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(GLWallpaperService.this).getString("pref_NT", "40"))!=((PendulumWave)mRenderer.mPendulum).NT))
                                        ((PendulumWave) mPendulum).restartPW(Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(GLWallpaperService.this).getString("pref_NP", "12")),
                                            Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(GLWallpaperService.this).getString("pref_NT", "40")),
                                            30./180.*Math.PI);
                                mRenderer.mPendulum.setColorPendulum1(PreferenceManager.getDefaultSharedPreferences(GLWallpaperService.this).getInt("wallpaper_pendulum_color_1", 0xFFFF0000));
                                mRenderer.mPendulum.setColorPendulum2(PreferenceManager.getDefaultSharedPreferences(GLWallpaperService.this).getInt("wallpaper_pendulum_color_2", 0xFF0000FF));
                                if (PreferenceManager.getDefaultSharedPreferences(GLWallpaperService.this).getString("pendulum_selection", "5").equals("8")) mPendulum.setColorPendulum1(PreferenceManager.getDefaultSharedPreferences(GLWallpaperService.this).getInt("wallpaper_pendulum_color_wave", 0xFF0000FF));
                                mRenderer.recompileProgram();
                            }});
//                        Log.v("Wallpaper renderer", "After: rendering pendulum is " + mRenderer.mPendulum.name);
                        mPendulum = getCurrentPendulum();
                        useDynGravity = PreferenceManager.getDefaultSharedPreferences(
                            GLWallpaperService.this).getBoolean("use_accelerometer", true);
//                        Log.v("Wallpaper:", "Changing");
                    //}
                    boolean PWP = PreferenceManager.getDefaultSharedPreferences(
                            GLWallpaperService.this).getString("pendulum_selection", "5").equals("8");
                    if (PWP) useDynGravity = false;
					glSurfaceView.onResume();
                    mPendulum.setGravityMode(useDynGravity);

                    if (useDynGravity) mSensorManager.registerListener(GLWallpaperService.this, mGravity, SensorManager.SENSOR_DELAY_GAME);
				} else {					
					glSurfaceView.onPause();
                    if (useDynGravity) mSensorManager.unregisterListener(GLWallpaperService.this);
				}
			}
		}		

		@Override
		public void onDestroy() {
//			if (LoggerConfig.ON) {
//				Log.d(TAG, "onDestroy()");
//			}

			super.onDestroy();
			glSurfaceView.onDestroy();
		}
		
		protected void setRenderer(Renderer renderer) {
//			if (LoggerConfig.ON) {
//				Log.d(TAG, "setRenderer(" + renderer + ")");
//			}
            mRenderer = (PendulumRenderer) renderer;
			glSurfaceView.setRenderer(renderer);
			rendererHasBeenSet = true;
		}
		
		protected void setPreserveEGLContextOnPause(boolean preserve) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//				if (LoggerConfig.ON) {
//					Log.d(TAG, "setPreserveEGLContextOnPause(" + preserve + ")");
//				}
	
				glSurfaceView.setPreserveEGLContextOnPause(preserve);
			}
		}		

		protected void setEGLContextClientVersion(int version) {
//			if (LoggerConfig.ON) {
//				Log.d(TAG, "setEGLContextClientVersion(" + version + ")");
//			}

			glSurfaceView.setEGLContextClientVersion(version);
		}

	}

    abstract boolean needNewPendulum();
}
