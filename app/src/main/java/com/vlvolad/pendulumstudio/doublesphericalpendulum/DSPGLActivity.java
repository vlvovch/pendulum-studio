package com.vlvolad.pendulumstudio.doublesphericalpendulum;

import com.vlvolad.pendulumstudio.InformationActivity;
import com.vlvolad.pendulumstudio.R;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.view.WindowManager;

import java.lang.reflect.Method;

public class DSPGLActivity extends Activity implements SensorEventListener {

    private static final String TAG = "DSPGLActivity";
	private DSPGLSurfaceView mGLView;
	private SensorManager mSensorManager;
	private Sensor mGravity;
	private boolean useDynGravity;
	private boolean useDamping;
    private boolean isRunning;
	private Display display;

    static int frequency = 1000;
    static int buttonsFadeOutTime = 4000;
    static int buttonsFadeAnimationTime = 300;
    private boolean paused;
    private long deltaT;
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            deltaT = System.currentTimeMillis() - deltaT;
            //Log.d("PWGLActivity", "dT: " + deltaT);
            //Log.d("PWGLActivity", "Frames: " + PWGLRenderer.mPendulum.frames);
            float fps = DSPGLRenderer.mPendulum.frames / (float)(deltaT) * 1.e3f;
            ((TextView)findViewById(R.id.fps)).setText("FPS: " + String.format("%.0f", fps));
            DSPGLRenderer.mPendulum.frames = 0;
            deltaT = System.currentTimeMillis();
            if (isRunning && !paused) timerHandler.postDelayed(this, frequency);
        }
    };

    boolean buttonsAreOff;
    Runnable timerButtonsOff = new Runnable() {
        @Override
        public void run() {
            if (paused || !PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("pref_buttons_fade", true)) return;

            if(Build.VERSION.SDK_INT >= 12) {

                findViewById(R.id.DSP_buttons).animate()
                        .alpha(0f)
                        .setDuration(buttonsFadeAnimationTime)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                findViewById(R.id.DSP_buttons).setVisibility(View.GONE);
                                buttonsAreOff = true;
                            }
                        });
            }
            else {
                findViewById(R.id.DSP_buttons).setVisibility(View.GONE);
                buttonsAreOff = true;
            }

        }
    };

    Runnable timerButtonsOn = new Runnable() {
        @Override
        public void run() {
            //Log.d("Act","ButtonsOn");
            if(Build.VERSION.SDK_INT >= 12) {

                findViewById(R.id.DSP_buttons).setAlpha(0f);
                findViewById(R.id.DSP_buttons).setVisibility(View.VISIBLE);
                findViewById(R.id.DSP_buttons).animate()
                        .alpha(1f)
                        .setDuration(buttonsFadeAnimationTime)
                        .setListener(null);
            }
            else
                findViewById(R.id.DSP_buttons).setVisibility(View.VISIBLE);

            buttonsAreOff = false;

            if (!paused) {
                timerHandler.removeCallbacks(timerButtonsOff);
                timerHandler.postDelayed(timerButtonsOff, buttonsFadeOutTime);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getResources().getBoolean(R.bool.portrait_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.doublesphericalpendulum_gl);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setFullScreenMode();
        setFpsMode();
        
        mGLView = (DSPGLSurfaceView)findViewById(R.id.DSP_gl_surface_view);
        
        
        
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //mGravity = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        mGravity = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        useDynGravity = DSPGLRenderer.mPendulum.dynamicGravity;
        
        display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        
        if (Math.abs(DSPGLRenderer.mPendulum.k)>1.e-7) useDamping = true;
        else useDamping = false;


        isRunning = !DSPGLRenderer.mPendulum.paused;
        if (!isRunning) ((ImageButton)findViewById(R.id.button_playpause)).setImageResource(R.drawable.ic_action_play);
        else ((ImageButton)findViewById(R.id.button_playpause)).setImageResource(R.drawable.ic_action_pause);
        findViewById(R.id.button_playpause).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRunning) ((ImageButton)findViewById(R.id.button_playpause)).setImageResource(R.drawable.ic_action_play);
                else ((ImageButton)findViewById(R.id.button_playpause)).setImageResource(R.drawable.ic_action_pause);
                isRunning = !isRunning;
                mGLView.queueEvent(new Runnable() {
                    // This method will be called on the rendering
                    // thread:
                    public void run() {
                        //MPGLRenderer.mPendulum.restart();
                        if (!isRunning) DSPGLRenderer.mPendulum.paused = true;
                        else DSPGLRenderer.mPendulum.paused = false;
                    }});
                if (isRunning) {
                    DSPGLRenderer.mPendulum.frames = 0;
                    deltaT = System.currentTimeMillis();
                    timerHandler.postDelayed(timerRunnable, frequency);
                }

            }
        });
        
        findViewById(R.id.button_restart).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mGLView.queueEvent(new Runnable() {
                    // This method will be called on the rendering
                    // thread:
                    public void run() {
                    	DSPGLRenderer.mPendulum.restart();
                        DSPGLRenderer.resetAccumBuffer();
                    	if (useDamping) DSPGLRenderer.mPendulum.k = DSPSimulationParameters.simParams.k;
                        else DSPGLRenderer.mPendulum.k = 0.;
                    }});
			}
		});

        findViewById(R.id.button_settings).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mGLView.queueEvent(new Runnable() {
                    // This method will be called on the rendering
                    // thread:
                    public void run() {
                        Intent intentParam = new Intent(DSPGLActivity.this, DSPParametersActivity.class);
                        startActivity(intentParam);
                    }});
            }
        });
        
        findViewById(R.id.togglebutton_sensor_gravity).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DSPGLRenderer.mPendulum.toggleGravity();
                useDynGravity = !useDynGravity;
                if (useDynGravity) mSensorManager.registerListener(DSPGLActivity.this, mGravity, SensorManager.SENSOR_DELAY_GAME);
                else mSensorManager.unregisterListener(DSPGLActivity.this);
//                updateGravitySensorStatus();
			}
		});
        
        findViewById(R.id.togglebutton_damping).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
                useDamping = !useDamping;
                if (useDamping) DSPGLRenderer.mPendulum.k = DSPSimulationParameters.simParams.k;
                else DSPGLRenderer.mPendulum.k = 0.;
//                if (useDynGravity) mSensorManager.registerListener(DSPGLActivity.this, mGravity, SensorManager.SENSOR_DELAY_GAME);
//                else mSensorManager.unregisterListener(DSPGLActivity.this);
//                updateDampingStatus();
			}
		});

        findViewById(R.id.togglebutton_trace).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //MPGLRenderer.mPendulum.trmd = !MPGLRenderer.mPendulum.trmd;
                DSPSimulationParameters.simParams.showTrajectory = !DSPSimulationParameters.simParams.showTrajectory;
//                if (useDynGravity) mSensorManager.registerListener(MPGLActivity.this, mGravity, SensorManager.SENSOR_DELAY_GAME);
//                else mSensorManager.unregisterListener(MPGLActivity.this);
//                updateDampingStatus();
                if (DSPSimulationParameters.simParams.showTrajectory) {
                    mGLView.queueEvent(new Runnable() {
                        // This method will be called on the rendering
                        // thread:
                        public void run() {
                            DSPGLRenderer.mPendulum.clearTrajectory();
                            DSPGLRenderer.resetAccumBuffer();
                        }
                    });
                }
            }
        });
        
        if (!useDynGravity) ((ToggleButton)findViewById(R.id.togglebutton_sensor_gravity)).setChecked(false);
        else ((ToggleButton)findViewById(R.id.togglebutton_sensor_gravity)).setChecked(true);
        
        if (!useDamping) ((ToggleButton)findViewById(R.id.togglebutton_damping)).setChecked(false);
        else ((ToggleButton)findViewById(R.id.togglebutton_damping)).setChecked(true);

        if (!DSPSimulationParameters.simParams.showTrajectory) ((ToggleButton)findViewById(R.id.togglebutton_trace)).setChecked(false);
        else ((ToggleButton)findViewById(R.id.togglebutton_trace)).setChecked(true);

        paused = false;

        buttonsAreOff = false;
        timerHandler.postDelayed(timerButtonsOff, buttonsFadeOutTime);
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
      if (rotmode==Surface.ROTATION_0) DSPGLRenderer.mPendulum.setGravity(event.values[0], event.values[1], event.values[2]);
      else if (rotmode==Surface.ROTATION_90) DSPGLRenderer.mPendulum.setGravity(-event.values[1], event.values[0], event.values[2]);
      else if (rotmode==Surface.ROTATION_180) DSPGLRenderer.mPendulum.setGravity(event.values[0], -event.values[1], event.values[2]);
      else if (rotmode==Surface.ROTATION_270) DSPGLRenderer.mPendulum.setGravity(event.values[1], -event.values[0], event.values[2]);
      // Do something with this sensor value.
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        // The following call pauses the rendering thread.
        // If your OpenGL application is memory intensive,
        // you should consider de-allocating objects that
        // consume significant memory here.
        mGLView.onPause();
        if (useDynGravity) mSensorManager.unregisterListener(this);
        paused = true;
        makeButtonsVisible();
    }
    
    @Override
    protected void onResume() {
        super.onResume();

        setFullScreenMode();
        setFpsMode();

        if (DSPSimulationParameters.simParams.showTrajectory && !((ToggleButton)findViewById(R.id.togglebutton_trace)).isChecked())
            mGLView.queueEvent(new Runnable() {
                // This method will be called on the rendering
                // thread:
                public void run() {
                    DSPGLRenderer.mPendulum.clearTrajectory();
                }
            });
        if (!DSPSimulationParameters.simParams.showTrajectory) ((ToggleButton)findViewById(R.id.togglebutton_trace)).setChecked(false);
        else ((ToggleButton)findViewById(R.id.togglebutton_trace)).setChecked(true);

        mGLView.queueEvent(new Runnable() {
            // This method will be called on the rendering
            // thread:
            public void run() {
                DSPGLRenderer.mPendulum.setColorPendulum1(DSPSimulationParameters.simParams.pendulumColor);
                DSPGLRenderer.mPendulum.setColorPendulum2(DSPSimulationParameters.simParams.pendulumColor2);
            }
        });

        // The following call resumes a paused rendering thread.
        // If you de-allocated graphic objects for onPause()
        // this is a good place to re-allocate them.
        mGLView.onResume();
        if (useDynGravity) mSensorManager.registerListener(this, mGravity, SensorManager.SENSOR_DELAY_GAME);

        makeButtonsVisible();

        paused = false;
        if (isRunning && !paused) {
            DSPGLRenderer.mPendulum.frames = 0;
            deltaT = System.currentTimeMillis();
            timerHandler.postDelayed(timerRunnable, frequency);

            if (!buttonsAreOff) {
                timerHandler.removeCallbacks(timerButtonsOff);
                timerHandler.postDelayed(timerButtonsOff, buttonsFadeOutTime);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.doublesphericalpendulum, menu);
        MenuItem item = menu.findItem(R.id.action_rate);
        item.setVisible(!PreferenceManager.getDefaultSharedPreferences(this).getBoolean("rate_clicked", false));
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.DSP_parameters:
            	Intent intentParam = new Intent(DSPGLActivity.this, DSPParametersActivity.class);
                startActivity(intentParam);
                return true;
            case R.id.action_rate:
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }

                PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("rate_clicked", true).apply();
                if(Build.VERSION.SDK_INT >= 11)
                    invalidateOptionsMenu();

                return true;
            case R.id.action_information:
                Intent intent = new Intent(DSPGLActivity.this, InformationActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu)
    {
        if(Build.VERSION.SDK_INT >= 14 && featureId == Window.FEATURE_ACTION_BAR && menu != null){
            if(menu.getClass().getSimpleName().equals("MenuBuilder")){
                try{
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                }
                catch(NoSuchMethodException e){
                    Log.e(TAG, "onMenuOpened", e);
                }
                catch(Exception e){
                    throw new RuntimeException(e);
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    protected void onStop() {
        super.onStop();
        paused = true;
    }

    protected void setFullScreenMode() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean full_screen = sharedPref.getBoolean("pref_fullscreen", false);
        if (full_screen) {
            if (Build.VERSION.SDK_INT < 16) { //old method
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
            } else { // Jellybean and up, new hotness
                View decorView = getWindow().getDecorView();
                // Hide the status bar.
                int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
                decorView.setSystemUiVisibility(uiOptions);
                // Remember that you should never show the action bar if the
                // status bar is hidden, so hide that too if necessary.
                ActionBar actionBar = getActionBar();
                actionBar.hide();
            }
        }
        else {
            if (Build.VERSION.SDK_INT < 16) { //old method
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            } else { // Jellybean and up, new hotness
                View decorView = getWindow().getDecorView();
                // Hide the status bar.
                int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
                decorView.setSystemUiVisibility(uiOptions);
                // Remember that you should never show the action bar if the
                // status bar is hidden, so hide that too if necessary.
                ActionBar actionBar = getActionBar();
                actionBar.show();
            }
        }
    }

    protected void setFpsMode() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean show_fps = sharedPref.getBoolean("pref_fps", false);
        LinearLayout view = (LinearLayout) findViewById(R.id.fps_layout);
        if (show_fps)
            view.setVisibility(View.VISIBLE);
        else
            view.setVisibility(View.INVISIBLE);
    }

    protected void makeButtonsVisible() {
        timerHandler.removeCallbacks(timerButtonsOff);
        if(Build.VERSION.SDK_INT >= 12)
            findViewById(R.id.DSP_buttons).setAlpha(1f);
        findViewById(R.id.DSP_buttons).setVisibility(View.VISIBLE);
        buttonsAreOff = false;
    }
}
