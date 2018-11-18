package com.vlvolad.pendulumstudio.springmathematicalpendulum;

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

public class SMPParametersActivity extends Activity {
    private int settingsEvent, pendulumColor, pendulumColor2;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.springmathematicalpendulum_parameters);
        readParameters();
    }

    public void readParameters() {
        EditText editparam = (EditText) findViewById(R.id.SMP_editaa);
        ((TextView)findViewById(R.id.SMP_labelaa)).setText(Html.fromHtml(getResources().getString(R.string.SMP_label_l)));
        editparam.setText(Float.toString(SMPSimulationParameters.simParams.aa));
        editparam = (EditText) findViewById(R.id.SMP_editl);
        ((TextView)findViewById(R.id.SMP_labell)).setText(Html.fromHtml(getResources().getString(R.string.SMP_label_l)));
        editparam.setText(Float.toString(SMPSimulationParameters.simParams.l));
        editparam = (EditText) findViewById(R.id.SMP_editm);
        ((TextView)findViewById(R.id.SMP_labelm1)).setText(Html.fromHtml(getResources().getString(R.string.SMP_label_m1)));
        editparam.setText(Float.toString(SMPSimulationParameters.simParams.m1));
        editparam = (EditText) findViewById(R.id.SMP_editm2);
        ((TextView)findViewById(R.id.SMP_labelm2)).setText(Html.fromHtml(getResources().getString(R.string.SMP_label_m2)));
        editparam.setText(Float.toString(SMPSimulationParameters.simParams.m2));
        ((TextView)findViewById(R.id.SMP_labelg)).setText(Html.fromHtml(getResources().getString(R.string.SMP_label_g)));
        editparam = (EditText) findViewById(R.id.SMP_editg);
        editparam.setText(Float.toString(SMPSimulationParameters.simParams.g / 100.f));
        ((TextView)findViewById(R.id.SMP_labelk)).setText(Html.fromHtml(getResources().getString(R.string.SMP_label_k)));
        editparam = (EditText) findViewById(R.id.SMP_editk);
        editparam.setText(Float.toString(SMPSimulationParameters.simParams.k));
        editparam = (EditText) findViewById(R.id.SMP_editgam);
        editparam.setText(Float.toString(SMPSimulationParameters.simParams.gam * 1.e3f));

        RadioButton rbrand = (RadioButton) findViewById(R.id.SMP_radioRand);
        rbrand.setChecked(SMPSimulationParameters.simParams.initRandom);
        rbrand = (RadioButton) findViewById(R.id.SMP_radioFixed);
        rbrand.setChecked(!SMPSimulationParameters.simParams.initRandom);

        ((TextView)findViewById(R.id.SMP_labels)).setText(Html.fromHtml(getResources().getString(R.string.SMP_label_s)));
        editparam = (EditText) findViewById(R.id.SMP_edits);
        editparam.setText(Float.toString((float) (SMPSimulationParameters.simParams.s)));
        ((TextView)findViewById(R.id.SMP_labelsv)).setText(Html.fromHtml(getResources().getString(R.string.SMP_label_sv)));
        editparam = (EditText) findViewById(R.id.SMP_editsv);
        editparam.setText(Float.toString((float) (SMPSimulationParameters.simParams.sv)));


        ((TextView)findViewById(R.id.SMP_labelth0)).setText(Html.fromHtml(getResources().getString(R.string.SMP_label_th0)));
        editparam = (EditText) findViewById(R.id.SMP_editth0);
        editparam.setText(Float.toString((float) (SMPSimulationParameters.simParams.th1 * 180.f / Math.PI)));
        ((TextView)findViewById(R.id.SMP_labelthv0)).setText(Html.fromHtml(getResources().getString(R.string.SMP_label_thv0)));
        editparam = (EditText) findViewById(R.id.SMP_editthv0);
        editparam.setText(Float.toString((float) (SMPSimulationParameters.simParams.thv1 * 180.f / Math.PI)));

        ((TextView)findViewById(R.id.SMP_labelth2)).setText(Html.fromHtml(getResources().getString(R.string.SMP_label_th2)));
        editparam = (EditText) findViewById(R.id.SMP_editth2);
        editparam.setText(Float.toString((float) (SMPSimulationParameters.simParams.th2 * 180.f / Math.PI)));
        ((TextView)findViewById(R.id.SMP_labelthv2)).setText(Html.fromHtml(getResources().getString(R.string.SMP_label_thv2)));
        editparam = (EditText) findViewById(R.id.SMP_editthv2);
        editparam.setText(Float.toString((float) (SMPSimulationParameters.simParams.thv2 * 180.f / Math.PI)));

        CheckBox checkTraj = (CheckBox) findViewById(R.id.SMP_checkBoxTraj);
        checkTraj.setChecked(SMPSimulationParameters.simParams.showTrajectory);

        CheckBox checkTrajInfinite = (CheckBox) findViewById(R.id.SMP_checkBoxTrajInfinite);
        checkTrajInfinite.setChecked(SMPSimulationParameters.simParams.infiniteTrajectory);

        editparam = (EditText) findViewById(R.id.SMP_edittrajle);
        editparam.setText(Integer.toString(SMPSimulationParameters.simParams.traceLength));

        pendulumColor = SMPSimulationParameters.simParams.pendulumColor;
        Button PColor = (Button) findViewById(R.id.SMP_PendulumColor);
        PColor.setBackgroundColor(pendulumColor);

        pendulumColor2 = SMPSimulationParameters.simParams.pendulumColor2;
        Button PColor2 = (Button) findViewById(R.id.SMP_PendulumColor2);
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
    	EditText editparam = (EditText) findViewById(R.id.SMP_editaa);
        if (!editparam.getText().toString().equals("") && Float.parseFloat(editparam.getText().toString())>0.) SMPSimulationParameters.simParams.aa = Float.parseFloat(editparam.getText().toString());
    	editparam = (EditText) findViewById(R.id.SMP_editl);
        if (!editparam.getText().toString().equals("") && Float.parseFloat(editparam.getText().toString())>0.) SMPSimulationParameters.simParams.l = Float.parseFloat(editparam.getText().toString());
    	editparam = (EditText) findViewById(R.id.SMP_editm);
        if (!editparam.getText().toString().equals("") && Float.parseFloat(editparam.getText().toString())>0.) SMPSimulationParameters.simParams.m1 = Float.parseFloat(editparam.getText().toString());
    	editparam = (EditText) findViewById(R.id.SMP_editm2);
        if (!editparam.getText().toString().equals("") && Float.parseFloat(editparam.getText().toString())>0.) SMPSimulationParameters.simParams.m2 = Float.parseFloat(editparam.getText().toString());
    	editparam = (EditText) findViewById(R.id.SMP_editg);
        if (!editparam.getText().toString().equals("")) SMPSimulationParameters.simParams.g = Float.parseFloat(editparam.getText().toString()) * 100.f;
    	editparam = (EditText) findViewById(R.id.SMP_editk);
        if (!editparam.getText().toString().equals("")) SMPSimulationParameters.simParams.k = Float.parseFloat(editparam.getText().toString());
    	editparam = (EditText) findViewById(R.id.SMP_editgam);
        if (!editparam.getText().toString().equals("")) SMPSimulationParameters.simParams.gam = Float.parseFloat(editparam.getText().toString()) / 1.e3f;
    	
    	RadioButton rbrand = (RadioButton) findViewById(R.id.SMP_radioRand);
    	SMPSimulationParameters.simParams.initRandom = rbrand.isChecked();
    	
    	editparam = (EditText) findViewById(R.id.SMP_edits);
        if (!editparam.getText().toString().equals("")) SMPSimulationParameters.simParams.s = (float)(Float.parseFloat(editparam.getText().toString()));
    	editparam = (EditText) findViewById(R.id.SMP_editsv);
        if (!editparam.getText().toString().equals("")) SMPSimulationParameters.simParams.sv = (float)(Float.parseFloat(editparam.getText().toString()));
    	editparam = (EditText) findViewById(R.id.SMP_editth0);
        if (!editparam.getText().toString().equals("")) SMPSimulationParameters.simParams.th1 = (float)(Float.parseFloat(editparam.getText().toString()) *
    			Math.PI / 180.f);
    	editparam = (EditText) findViewById(R.id.SMP_editthv0);
        if (!editparam.getText().toString().equals("")) SMPSimulationParameters.simParams.thv1 = (float)(Float.parseFloat(editparam.getText().toString()) *
    			Math.PI / 180.f);

    	editparam = (EditText) findViewById(R.id.SMP_editth2);
        if (!editparam.getText().toString().equals("")) SMPSimulationParameters.simParams.th2 = (float)(Float.parseFloat(editparam.getText().toString()) *
    			Math.PI / 180.f);
    	editparam = (EditText) findViewById(R.id.SMP_editthv2);
        if (!editparam.getText().toString().equals("")) SMPSimulationParameters.simParams.thv2 = (float)(Float.parseFloat(editparam.getText().toString()) *
    			Math.PI / 180.f);

    	CheckBox checkTraj = (CheckBox) findViewById(R.id.SMP_checkBoxTraj);
    	SMPSimulationParameters.simParams.showTrajectory = checkTraj.isChecked();

        CheckBox checkTrajInfinite = (CheckBox) findViewById(R.id.SMP_checkBoxTrajInfinite);
        SMPSimulationParameters.simParams.infiniteTrajectory = checkTrajInfinite.isChecked();
    	
    	editparam = (EditText) findViewById(R.id.SMP_edittrajle);
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
                SMPSimulationParameters.simParams.traceLength = trlength;
            else {
                SMPSimulationParameters.simParams.infiniteTrajectory = true;
            }
        }

        SMPSimulationParameters.simParams.pendulumColor = pendulumColor;
        SMPSimulationParameters.simParams.pendulumColor2 = pendulumColor2;

        SMPSimulationParameters.simParams.writeSettings(this.getSharedPreferences(SMPSimulationParameters.PREFS_NAME, 0));

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        CheckBox checkFullScreen = (CheckBox) findViewById(R.id.pref_fullscreen_loc);
        editor.putBoolean("pref_fullscreen", checkFullScreen.isChecked());
        CheckBox checkFps = (CheckBox) findViewById(R.id.pref_fps_loc);
        editor.putBoolean("pref_fps", checkFps.isChecked());
        CheckBox checkFade = (CheckBox) findViewById(R.id.pref_buttons_fade_loc);
        editor.putBoolean("pref_buttons_fade", checkFade.isChecked());
        editor.commit();
    	
    	SMPParametersActivity.this.finish();
    }
    
    public void cancelButton(View v) {
    	SMPParametersActivity.this.finish();
    }

    public void resetButton(View v) {
        SMPSimulationParameters.simParams.clearSettings(this.getSharedPreferences(SMPSimulationParameters.PREFS_NAME, 0));
        readParameters();
    }

    public void PendulumColor(View v) {
        final ColorPickerDialog colorDialog = new ColorPickerDialog(this,pendulumColor);
        colorDialog.setTitle(R.string.pick_color);

        colorDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(android.R.string.ok), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                pendulumColor = colorDialog.getColor();
                Button PColor = (Button) findViewById(R.id.SMP_PendulumColor);
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
                Button PColor = (Button) findViewById(R.id.SMP_PendulumColor2);
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
