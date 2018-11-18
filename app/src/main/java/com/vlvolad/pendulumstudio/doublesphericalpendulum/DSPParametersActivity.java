package com.vlvolad.pendulumstudio.doublesphericalpendulum;

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

public class DSPParametersActivity extends Activity {
    private int settingsEvent, pendulumColor, pendulumColor2;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doublesphericalpendulum_parameters);
        readParameters();
    }

    public void readParameters() {
        EditText editparam = (EditText) findViewById(R.id.DSP_editl);
        ((TextView)findViewById(R.id.DSP_labell1)).setText(Html.fromHtml(getResources().getString(R.string.DSP_label_l)));
        editparam.setText(Float.toString(DSPSimulationParameters.simParams.l1));
        editparam = (EditText) findViewById(R.id.DSP_editl2);
        ((TextView)findViewById(R.id.DSP_labell2)).setText(Html.fromHtml(getResources().getString(R.string.DSP_label_l2)));
        editparam.setText(Float.toString(DSPSimulationParameters.simParams.l2));
        editparam = (EditText) findViewById(R.id.DSP_editm);
        ((TextView)findViewById(R.id.DSP_labelm1)).setText(Html.fromHtml(getResources().getString(R.string.DSP_label_m)));
        editparam.setText(Float.toString(DSPSimulationParameters.simParams.m1));
        editparam = (EditText) findViewById(R.id.DSP_editm2);
        ((TextView)findViewById(R.id.DSP_labelm2)).setText(Html.fromHtml(getResources().getString(R.string.DSP_label_m2)));
        editparam.setText(Float.toString(DSPSimulationParameters.simParams.m2));
        ((TextView)findViewById(R.id.DSP_labelg)).setText(Html.fromHtml(getResources().getString(R.string.DSP_label_g)));
        editparam = (EditText) findViewById(R.id.DSP_editg);
        editparam.setText(Float.toString(DSPSimulationParameters.simParams.g / 100.f));
        editparam = (EditText) findViewById(R.id.DSP_editk);
        editparam.setText(Float.toString(DSPSimulationParameters.simParams.k * 1.e3f));

        RadioButton rbrand = (RadioButton) findViewById(R.id.DSP_radioRand);
        rbrand.setChecked(DSPSimulationParameters.simParams.initRandom);
        rbrand = (RadioButton) findViewById(R.id.DSP_radioFixed);
        rbrand.setChecked(!DSPSimulationParameters.simParams.initRandom);

        ((TextView)findViewById(R.id.DSP_labelth0)).setText(Html.fromHtml(getResources().getString(R.string.DSP_label_th0)));
        editparam = (EditText) findViewById(R.id.DSP_editth0);
        editparam.setText(Float.toString((float) (DSPSimulationParameters.simParams.th1 * 180.f / Math.PI)));
        ((TextView)findViewById(R.id.DSP_labelthv0)).setText(Html.fromHtml(getResources().getString(R.string.DSP_label_thv0)));
        editparam = (EditText) findViewById(R.id.DSP_editthv0);
        editparam.setText(Float.toString((float) (DSPSimulationParameters.simParams.thv1 * 180.f / Math.PI)));
        ((TextView)findViewById(R.id.DSP_labelph0)).setText(Html.fromHtml(getResources().getString(R.string.DSP_label_ph0)));
        editparam = (EditText) findViewById(R.id.DSP_editph0);
        editparam.setText(Float.toString((float) (DSPSimulationParameters.simParams.ph1 * 180.f / Math.PI)));
        ((TextView)findViewById(R.id.DSP_labelphv0)).setText(Html.fromHtml(getResources().getString(R.string.DSP_label_phv0)));
        editparam = (EditText) findViewById(R.id.DSP_editphv0);
        editparam.setText(Float.toString((float) (DSPSimulationParameters.simParams.phv1 * 180.f / Math.PI)));

        ((TextView)findViewById(R.id.DSP_labelth2)).setText(Html.fromHtml(getResources().getString(R.string.DSP_label_th2)));
        editparam = (EditText) findViewById(R.id.DSP_editth2);
        editparam.setText(Float.toString((float) (DSPSimulationParameters.simParams.th2 * 180.f / Math.PI)));
        ((TextView)findViewById(R.id.DSP_labelthv2)).setText(Html.fromHtml(getResources().getString(R.string.DSP_label_thv2)));
        editparam = (EditText) findViewById(R.id.DSP_editthv2);
        editparam.setText(Float.toString((float) (DSPSimulationParameters.simParams.thv2 * 180.f / Math.PI)));
        ((TextView)findViewById(R.id.DSP_labelph2)).setText(Html.fromHtml(getResources().getString(R.string.DSP_label_ph2)));
        editparam = (EditText) findViewById(R.id.DSP_editph2);
        editparam.setText(Float.toString((float) (DSPSimulationParameters.simParams.ph2 * 180.f / Math.PI)));
        ((TextView)findViewById(R.id.DSP_labelphv2)).setText(Html.fromHtml(getResources().getString(R.string.DSP_label_phv2)));
        editparam = (EditText) findViewById(R.id.DSP_editphv2);
        editparam.setText(Float.toString((float) (DSPSimulationParameters.simParams.phv2 * 180.f / Math.PI)));

        CheckBox checkTraj = (CheckBox) findViewById(R.id.DSP_checkBoxTraj);
        checkTraj.setChecked(DSPSimulationParameters.simParams.showTrajectory);

        editparam = (EditText) findViewById(R.id.DSP_edittrajle);
        editparam.setText(Integer.toString(DSPSimulationParameters.simParams.traceLength));

        CheckBox checkTrajInfinite = (CheckBox) findViewById(R.id.DSP_checkBoxTrajInfinite);
        checkTrajInfinite.setChecked(DSPSimulationParameters.simParams.infiniteTrajectory);

        pendulumColor = DSPSimulationParameters.simParams.pendulumColor;
        Button PColor = (Button) findViewById(R.id.DSP_PendulumColor);
        PColor.setBackgroundColor(pendulumColor);

        pendulumColor2 = DSPSimulationParameters.simParams.pendulumColor2;
        Button PColor2 = (Button) findViewById(R.id.DSP_PendulumColor2);
        PColor2.setBackgroundColor(pendulumColor2);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        boolean full_screen = sharedPref.getBoolean("pref_fullscreen", false);
        CheckBox checkFullScreen = (CheckBox) findViewById(R.id.pref_fullscreen_loc);
        checkFullScreen.setChecked(full_screen);

        boolean fps = sharedPref.getBoolean("pref_fps", false);
        CheckBox checkFps = (CheckBox) findViewById(R.id.pref_fps_loc);
        checkFps.setChecked(fps);

        boolean fade = sharedPref.getBoolean("pref_buttons_fade", true);
        CheckBox checkFade = (CheckBox) findViewById(R.id.pref_buttons_fade_loc);
        checkFade.setChecked(fade);
    }
    
    public void okButton(View v) {
    	EditText editparam = (EditText) findViewById(R.id.DSP_editl);
        if (!editparam.getText().toString().equals("") && Float.parseFloat(editparam.getText().toString())>0.) DSPSimulationParameters.simParams.l1 = Float.parseFloat(editparam.getText().toString());
    	editparam = (EditText) findViewById(R.id.DSP_editl2);
        if (!editparam.getText().toString().equals("") && Float.parseFloat(editparam.getText().toString())>0.) DSPSimulationParameters.simParams.l2 = Float.parseFloat(editparam.getText().toString());
    	editparam = (EditText) findViewById(R.id.DSP_editm);
        if (!editparam.getText().toString().equals("") && Float.parseFloat(editparam.getText().toString())>0.) DSPSimulationParameters.simParams.m1 = Float.parseFloat(editparam.getText().toString());
    	editparam = (EditText) findViewById(R.id.DSP_editm2);
        if (!editparam.getText().toString().equals("") && Float.parseFloat(editparam.getText().toString())>0.) DSPSimulationParameters.simParams.m2 = Float.parseFloat(editparam.getText().toString());
    	editparam = (EditText) findViewById(R.id.DSP_editg);
        if (!editparam.getText().toString().equals("")) DSPSimulationParameters.simParams.g = Float.parseFloat(editparam.getText().toString()) * 100.f;
    	editparam = (EditText) findViewById(R.id.DSP_editk);
        if (!editparam.getText().toString().equals("")) DSPSimulationParameters.simParams.k = Float.parseFloat(editparam.getText().toString()) / 1.e3f;
    	
    	RadioButton rbrand = (RadioButton) findViewById(R.id.DSP_radioRand);
    	DSPSimulationParameters.simParams.initRandom = rbrand.isChecked();
    	
    	editparam = (EditText) findViewById(R.id.DSP_editth0);
        if (!editparam.getText().toString().equals("")) DSPSimulationParameters.simParams.th1 = (float)(Float.parseFloat(editparam.getText().toString()) *
    			Math.PI / 180.f);
    	editparam = (EditText) findViewById(R.id.DSP_editthv0);
        if (!editparam.getText().toString().equals("")) DSPSimulationParameters.simParams.thv1 = (float)(Float.parseFloat(editparam.getText().toString()) *
    			Math.PI / 180.f);
    	editparam = (EditText) findViewById(R.id.DSP_editph0);
        if (!editparam.getText().toString().equals("")) DSPSimulationParameters.simParams.ph1 = (float)(Float.parseFloat(editparam.getText().toString()) *
    			Math.PI / 180.f);
    	editparam = (EditText) findViewById(R.id.DSP_editphv0);
        if (!editparam.getText().toString().equals("")) DSPSimulationParameters.simParams.phv1 = (float)(Float.parseFloat(editparam.getText().toString()) *
    			Math.PI / 180.f);
    	
    	editparam = (EditText) findViewById(R.id.DSP_editth2);
        if (!editparam.getText().toString().equals("")) DSPSimulationParameters.simParams.th2 = (float)(Float.parseFloat(editparam.getText().toString()) *
    			Math.PI / 180.f);
    	editparam = (EditText) findViewById(R.id.DSP_editthv2);
        if (!editparam.getText().toString().equals("")) DSPSimulationParameters.simParams.thv2 = (float)(Float.parseFloat(editparam.getText().toString()) *
    			Math.PI / 180.f);
    	editparam = (EditText) findViewById(R.id.DSP_editph2);
        if (!editparam.getText().toString().equals("")) DSPSimulationParameters.simParams.ph2 = (float)(Float.parseFloat(editparam.getText().toString()) *
    			Math.PI / 180.f);
    	editparam = (EditText) findViewById(R.id.DSP_editphv2);
        if (!editparam.getText().toString().equals("")) DSPSimulationParameters.simParams.phv2 = (float)(Float.parseFloat(editparam.getText().toString()) *
    			Math.PI / 180.f);
    	
    	CheckBox checkTraj = (CheckBox) findViewById(R.id.DSP_checkBoxTraj);
    	DSPSimulationParameters.simParams.showTrajectory = checkTraj.isChecked();

        CheckBox checkTrajInfinite = (CheckBox) findViewById(R.id.DSP_checkBoxTrajInfinite);
        DSPSimulationParameters.simParams.infiniteTrajectory = checkTrajInfinite.isChecked();
    	
    	editparam = (EditText) findViewById(R.id.DSP_edittrajle);
        //if (!editparam.getText().toString().equals("")) DSPSimulationParameters.simParams.traceLength = (int)(Integer.parseInt(editparam.getText().toString()));
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
                DSPSimulationParameters.simParams.traceLength = trlength;
            else {
                DSPSimulationParameters.simParams.infiniteTrajectory = true;
            }
        }

        DSPSimulationParameters.simParams.pendulumColor = pendulumColor;
        DSPSimulationParameters.simParams.pendulumColor2 = pendulumColor2;

        DSPSimulationParameters.simParams.writeSettings(this.getSharedPreferences(DSPSimulationParameters.PREFS_NAME, 0));

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        CheckBox checkFullScreen = (CheckBox) findViewById(R.id.pref_fullscreen_loc);
        editor.putBoolean("pref_fullscreen", checkFullScreen.isChecked());
        CheckBox checkFps = (CheckBox) findViewById(R.id.pref_fps_loc);
        editor.putBoolean("pref_fps", checkFps.isChecked());
        CheckBox checkFade = (CheckBox) findViewById(R.id.pref_buttons_fade_loc);
        editor.putBoolean("pref_buttons_fade", checkFade.isChecked());
        editor.commit();
    	
    	DSPParametersActivity.this.finish();
    }
    
    public void cancelButton(View v) {
        DSPParametersActivity.this.finish();
    }

    public void resetButton(View v) {
        DSPSimulationParameters.simParams.clearSettings(this.getSharedPreferences(DSPSimulationParameters.PREFS_NAME, 0));
        readParameters();
    }

    public void PendulumColor(View v) {
        final ColorPickerDialog colorDialog = new ColorPickerDialog(this,pendulumColor);
        colorDialog.setTitle(R.string.pick_color);

        colorDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(android.R.string.ok), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                pendulumColor = colorDialog.getColor();
                Button PColor = (Button) findViewById(R.id.DSP_PendulumColor);
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

    public void PendulumColor2(View v) {
        final ColorPickerDialog colorDialog = new ColorPickerDialog(this,pendulumColor2);
        colorDialog.setTitle(R.string.pick_color);

        colorDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(android.R.string.ok), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                pendulumColor2 = colorDialog.getColor();
                Button PColor = (Button) findViewById(R.id.DSP_PendulumColor2);
                PColor.setBackgroundColor(pendulumColor2);
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
