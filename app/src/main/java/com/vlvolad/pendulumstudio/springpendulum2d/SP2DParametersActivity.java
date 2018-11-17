package com.vlvolad.pendulumstudio.springpendulum2d;

import com.vlvolad.pendulumstudio.R;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import afzkl.development.colorpickerview.dialog.ColorPickerDialog;

public class SP2DParametersActivity extends Activity {
    private int settingsEvent, pendulumColor;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.springpendulum2d_parameters);
        readParameters();
    }

    public void readParameters() {
        EditText editparam = (EditText) findViewById(R.id.SP2D_edita);
        ((TextView)findViewById(R.id.SP2D_labelaa)).setText(Html.fromHtml(getResources().getString(R.string.SP2D_label_aa)));
        editparam.setText(Float.toString(SP2DSimulationParameters.simParams.aa));
        editparam = (EditText) findViewById(R.id.SP2D_editm);
        ((TextView)findViewById(R.id.SP2D_labelm)).setText(Html.fromHtml(getResources().getString(R.string.SP2D_label_m)));
        editparam.setText(Float.toString(SP2DSimulationParameters.simParams.m));
        ((TextView)findViewById(R.id.SP2D_labelg)).setText(Html.fromHtml(getResources().getString(R.string.SP2D_label_g)));
        editparam = (EditText) findViewById(R.id.SP2D_editg);
        editparam.setText(Float.toString(SP2DSimulationParameters.simParams.g / 100.f));
        ((TextView)findViewById(R.id.SP2D_label_k)).setText(Html.fromHtml(getResources().getString(R.string.SP2D_label_k)));
        editparam = (EditText) findViewById(R.id.SP2D_editk);
        editparam.setText(Float.toString(SP2DSimulationParameters.simParams.k));
        editparam = (EditText) findViewById(R.id.SP2D_editgam);
        editparam.setText(Float.toString(SP2DSimulationParameters.simParams.gam * 1.e3f));

        RadioButton rbrand = (RadioButton) findViewById(R.id.SP2D_radioRand);
        rbrand.setChecked(SP2DSimulationParameters.simParams.initRandom);
        rbrand = (RadioButton) findViewById(R.id.SP2D_radioFixed);
        rbrand.setChecked(!SP2DSimulationParameters.simParams.initRandom);

        ((TextView)findViewById(R.id.SP2D_labelx0)).setText(Html.fromHtml(getResources().getString(R.string.SP2D_label_x0)));
        editparam = (EditText) findViewById(R.id.SP2D_editx0);
        editparam.setText(Float.toString((float) (SP2DSimulationParameters.simParams.x)));
        ((TextView)findViewById(R.id.SP2D_labelxv0)).setText(Html.fromHtml(getResources().getString(R.string.SP2D_label_xv0)));
        editparam = (EditText) findViewById(R.id.SP2D_editxv0);
        editparam.setText(Float.toString((float) (SP2DSimulationParameters.simParams.xv)));


        ((TextView)findViewById(R.id.SP2D_labely0)).setText(Html.fromHtml(getResources().getString(R.string.SP2D_label_y0)));
        editparam = (EditText) findViewById(R.id.SP2D_edity0);
        editparam.setText(Float.toString((float) (SP2DSimulationParameters.simParams.y)));
        ((TextView)findViewById(R.id.SP2D_labelyv0)).setText(Html.fromHtml(getResources().getString(R.string.SP2D_label_yv0)));
        editparam = (EditText) findViewById(R.id.SP2D_edityv0);
        editparam.setText(Float.toString((float) (SP2DSimulationParameters.simParams.yv)));

        CheckBox checkTraj = (CheckBox) findViewById(R.id.SP2D_checkBoxTraj);
        checkTraj.setChecked(SP2DSimulationParameters.simParams.showTrajectory);

        CheckBox checkTrajInfinite = (CheckBox) findViewById(R.id.SP2D_checkBoxTrajInfinite);
        checkTrajInfinite.setChecked(SP2DSimulationParameters.simParams.infiniteTrajectory);

        editparam = (EditText) findViewById(R.id.SP2D_edittrajle);
        editparam.setText(Integer.toString(SP2DSimulationParameters.simParams.traceLength));

        pendulumColor = SP2DSimulationParameters.simParams.pendulumColor;
        Button PColor = (Button) findViewById(R.id.SP2D_PendulumColor);
        PColor.setBackgroundColor(pendulumColor);


        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        boolean full_screen = sharedPref.getBoolean("pref_fullscreen", false);
        CheckBox checkFullScreen = (CheckBox) findViewById(R.id.pref_fullscreen_loc);
        checkFullScreen.setChecked(full_screen);

        boolean fps = sharedPref.getBoolean("pref_fps", false);
        CheckBox checkFps = (CheckBox) findViewById(R.id.pref_fps_loc);
        checkFps.setChecked(fps);
    }
    
    public void okButton(View v) {
    	EditText editparam = (EditText) findViewById(R.id.SP2D_edita);
        if (!editparam.getText().toString().equals("") && Float.parseFloat(editparam.getText().toString())>0.) SP2DSimulationParameters.simParams.aa = Float.parseFloat(editparam.getText().toString());
    	editparam = (EditText) findViewById(R.id.SP2D_editm);
        if (!editparam.getText().toString().equals("") && Float.parseFloat(editparam.getText().toString())>0.) SP2DSimulationParameters.simParams.m = Float.parseFloat(editparam.getText().toString());
    	editparam = (EditText) findViewById(R.id.SP2D_editg);
        if (!editparam.getText().toString().equals("")) SP2DSimulationParameters.simParams.g = Float.parseFloat(editparam.getText().toString()) * 100.f;
    	editparam = (EditText) findViewById(R.id.SP2D_editk);
        if (!editparam.getText().toString().equals("")) SP2DSimulationParameters.simParams.k = Float.parseFloat(editparam.getText().toString());
    	editparam = (EditText) findViewById(R.id.SP2D_editgam);
        if (!editparam.getText().toString().equals("")) SP2DSimulationParameters.simParams.gam = Float.parseFloat(editparam.getText().toString()) / 1.e3f;
    	
    	RadioButton rbrand = (RadioButton) findViewById(R.id.SP2D_radioRand);
    	SP2DSimulationParameters.simParams.initRandom = rbrand.isChecked();
    	
    	editparam = (EditText) findViewById(R.id.SP2D_editx0);
        if (!editparam.getText().toString().equals("")) SP2DSimulationParameters.simParams.x = (float)(Float.parseFloat(editparam.getText().toString()));
    	editparam = (EditText) findViewById(R.id.SP2D_editxv0);
        if (!editparam.getText().toString().equals("")) SP2DSimulationParameters.simParams.xv = (float)(Float.parseFloat(editparam.getText().toString()));
    	editparam = (EditText) findViewById(R.id.SP2D_edity0);
        if (!editparam.getText().toString().equals("")) SP2DSimulationParameters.simParams.y = (float)(Float.parseFloat(editparam.getText().toString()));
    	editparam = (EditText) findViewById(R.id.SP2D_edityv0);
        if (!editparam.getText().toString().equals("")) SP2DSimulationParameters.simParams.yv = (float)(Float.parseFloat(editparam.getText().toString()));

    	CheckBox checkTraj = (CheckBox) findViewById(R.id.SP2D_checkBoxTraj);
    	SP2DSimulationParameters.simParams.showTrajectory = checkTraj.isChecked();

        CheckBox checkTrajInfinite = (CheckBox) findViewById(R.id.SP2D_checkBoxTrajInfinite);
        SP2DSimulationParameters.simParams.infiniteTrajectory = checkTrajInfinite.isChecked();
    	
    	editparam = (EditText) findViewById(R.id.SP2D_edittrajle);
        if (!editparam.getText().toString().equals("")) {
            int trlength = 0;
            try {
                trlength = (int) (Integer.parseInt(editparam.getText().toString()));
            } catch (NumberFormatException e) {
                trlength = -1;
            }
            if (trlength>100000) {
                trlength = 100000;
                Context context = getApplicationContext();
                CharSequence text = getString(R.string.TraceTooLong);//"Trace too long! Setting to 100000...";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
            if (trlength>0)
                SP2DSimulationParameters.simParams.traceLength = trlength;
            else {
                SP2DSimulationParameters.simParams.infiniteTrajectory = true;
            }
        }

        SP2DSimulationParameters.simParams.pendulumColor = pendulumColor;

        SP2DSimulationParameters.simParams.writeSettings(this.getSharedPreferences(SP2DSimulationParameters.PREFS_NAME, 0));

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        CheckBox checkFullScreen = (CheckBox) findViewById(R.id.pref_fullscreen_loc);
        editor.putBoolean("pref_fullscreen", checkFullScreen.isChecked());
        CheckBox checkFps = (CheckBox) findViewById(R.id.pref_fps_loc);
        editor.putBoolean("pref_fps", checkFps.isChecked());
        editor.commit();
    	
    	SP2DParametersActivity.this.finish();
    }
    
    public void cancelButton(View v) {
    	SP2DParametersActivity.this.finish();
    }

    public void resetButton(View v) {
        SP2DSimulationParameters.simParams.clearSettings(this.getSharedPreferences(SP2DSimulationParameters.PREFS_NAME, 0));
        readParameters();
    }

    public void PendulumColor(View v) {
        final ColorPickerDialog colorDialog = new ColorPickerDialog(this,pendulumColor);
        colorDialog.setTitle(R.string.pick_color);

        colorDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(android.R.string.ok), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                pendulumColor = colorDialog.getColor();
                Button PColor = (Button) findViewById(R.id.SP2D_PendulumColor);
                PColor.setBackgroundColor(pendulumColor);
            }
        });

        colorDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(android.R.string.cancel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Nothing to do here.
            }
        });
        colorDialog.show();
    }

}
