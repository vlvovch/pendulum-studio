package com.vlvolad.pendulumstudio.pendulumwave;

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
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.vlvolad.pendulumstudio.InformationActivity;
import com.vlvolad.pendulumstudio.R;

import java.lang.reflect.Method;

/**
 * Created by Volodymyr on 25.05.2015.
 */
public class PWGLActivity extends Activity implements SensorEventListener {

    private static final String TAG = "PWGLActivity";
    private PWGLSurfaceView mGLView;
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
            float fps = PWGLRenderer.mPendulum.frames / (float)(deltaT) * 1.e3f;
            ((TextView)findViewById(R.id.fps)).setText("FPS: " + String.format("%.0f", fps));
            PWGLRenderer.mPendulum.frames = 0;
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

                findViewById(R.id.PW_buttons).animate()
                        .alpha(0f)
                        .setDuration(buttonsFadeAnimationTime)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                findViewById(R.id.PW_buttons).setVisibility(View.GONE);
                                buttonsAreOff = true;
                            }
                        });
            }
            else {
                findViewById(R.id.PW_buttons).setVisibility(View.GONE);
                buttonsAreOff = true;
            }

        }
    };

    Runnable timerButtonsOn = new Runnable() {
        @Override
        public void run() {
            Log.d("Act","ButtonsOn");
            if(Build.VERSION.SDK_INT >= 12) {

                findViewById(R.id.PW_buttons).setAlpha(0f);
                findViewById(R.id.PW_buttons).setVisibility(View.VISIBLE);
                findViewById(R.id.PW_buttons).animate()
                        .alpha(1f)
                        .setDuration(buttonsFadeAnimationTime)
                        .setListener(null);
            }
            else
                findViewById(R.id.PW_buttons).setVisibility(View.VISIBLE);

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
        //getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.pendulumwave_gl);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setFullScreenMode();
        setFpsMode();

        mGLView = (PWGLSurfaceView)findViewById(R.id.gl_surface_view);



        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //mGravity = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        mGravity = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        useDynGravity = PWGLRenderer.mPendulum.dynamicGravity;

        display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        if (Math.abs(PWGLRenderer.mPendulum.k)>1.e-7) useDamping = true;
        else useDamping = false;


        findViewById(R.id.button_restart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGLView.queueEvent(new Runnable() {
                    // This method will be called on the rendering
                    // thread:
                    public void run() {
                        PWGLRenderer.mPendulum.restart();
                        if (useDamping) PWGLRenderer.mPendulum.setDamping(PWSimulationParameters.simParams.k); //PWGLRenderer.mPendulum.k = MPSimulationParameters.simParams.k;
                        else PWGLRenderer.mPendulum.setDamping(0.); //PWGLRenderer.mPendulum.k = 0.;
                    }});
            }
        });

        isRunning = !PWGLRenderer.mPendulum.paused;
        if (!isRunning) ((ImageButton)findViewById(R.id.button_playpause)).setImageResource(R.drawable.ic_action_play);
        else ((ImageButton)findViewById(R.id.button_playpause)).setImageResource(R.drawable.ic_action_pause);
        findViewById(R.id.button_playpause).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRunning) ((ImageButton)findViewById(R.id.button_playpause)).setImageResource(R.drawable.ic_action_play);
                else ((ImageButton)findViewById(R.id.button_playpause)).setImageResource(R.drawable.ic_action_pause);
                isRunning = !isRunning;
                mGLView.queueEvent(new Runnable() {
                    // This method will be called on the rendering
                    // thread:
                    public void run() {
                        if (!isRunning) PWGLRenderer.mPendulum.paused = true;
                        else PWGLRenderer.mPendulum.paused = false;
                    }});
                if (isRunning) {
                    PWGLRenderer.mPendulum.frames = 0;
                    deltaT = System.currentTimeMillis();
                    timerHandler.postDelayed(timerRunnable, frequency);
                }

            }
        });

        findViewById(R.id.button_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGLView.queueEvent(new Runnable() {
                    // This method will be called on the rendering
                    // thread:
                    public void run() {
                        Intent intentParam = new Intent(PWGLActivity.this, PWParametersActivity.class);
                        startActivity(intentParam);
                    }});
            }
        });

        findViewById(R.id.togglebutton_damping).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                useDamping = !useDamping;
                if (useDamping) PWGLRenderer.mPendulum.k = PWSimulationParameters.simParams.k;
                else PWGLRenderer.mPendulum.k = 0.;
            }
        });

        if (!useDamping) ((ToggleButton)findViewById(R.id.togglebutton_damping)).setChecked(false);
        else ((ToggleButton)findViewById(R.id.togglebutton_damping)).setChecked(true);

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
        if (rotmode== Surface.ROTATION_0) PWGLRenderer.mPendulum.setGravity(event.values[0], event.values[1], event.values[2]);
        else if (rotmode==Surface.ROTATION_90) PWGLRenderer.mPendulum.setGravity(-event.values[1], event.values[0], event.values[2]);
        else if (rotmode==Surface.ROTATION_180) PWGLRenderer.mPendulum.setGravity(event.values[0], -event.values[1], event.values[2]);
        else if (rotmode==Surface.ROTATION_270) PWGLRenderer.mPendulum.setGravity(event.values[1], -event.values[0], event.values[2]);
        // Do something with this sensor value.
    }

    @Override
    protected void onPause() {
        super.onPause();
        // The following call pauses the rendering thread.
        // If your OpenGL application is memory intensive,
        // you should consider de-allocating objects that
        // consume significant memory here.
        timerHandler.removeCallbacks(timerRunnable);
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
        mGLView.queueEvent(new Runnable() {
            // This method will be called on the rendering
            // thread:
            public void run() {
                PWGLRenderer.mPendulum.setColorPendulum1(PWSimulationParameters.simParams.pendulumColor);
            }
        });

        // The following call resumes a paused rendering thread.
        // If you de-allocated graphic objects for onPause()
        // this is a good place to re-allocate them.
        timerHandler.removeCallbacks(timerRunnable);
        mGLView.onResume();
        if (useDynGravity) mSensorManager.registerListener(this, mGravity, SensorManager.SENSOR_DELAY_GAME);

        makeButtonsVisible();

        paused = false;
        if (isRunning && !paused) {
            PWGLRenderer.mPendulum.frames = 0;
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
        inflater.inflate(R.menu.pendulumwave, menu);
        MenuItem item = menu.findItem(R.id.action_rate);
        item.setVisible(!PreferenceManager.getDefaultSharedPreferences(this).getBoolean("rate_clicked", false));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.PW_parameters:
                Intent intentParam = new Intent(PWGLActivity.this, PWParametersActivity.class);
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
                Intent intent = new Intent(PWGLActivity.this, InformationActivity.class);
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
            findViewById(R.id.PW_buttons).setAlpha(1f);
        findViewById(R.id.PW_buttons).setVisibility(View.VISIBLE);
        buttonsAreOff = false;
    }
}

