package com.vlvolad.pendulumstudio.springsphericalpendulum;

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

public class SSPParametersActivity extends Activity {
    private int settingsEvent, pendulumColor, pendulumColor2;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.springsphericalpendulum_parameters);
        readParameters();
    }

    public void readParameters() {
        EditText editparam = (EditText) findViewById(R.id.SSP_edita);
        ((TextView)findViewById(R.id.SSP_labela)).setText(Html.fromHtml(getResources().getString(R.string.SSP_label_a)));
        editparam.setText(Float.toString(SSPSimulationParameters.simParams.aa));
        editparam = (EditText) findViewById(R.id.SSP_editl);
        ((TextView)findViewById(R.id.SSP_labell)).setText(Html.fromHtml(getResources().getString(R.string.SSP_label_l)));
        editparam.setText(Float.toString(SSPSimulationParameters.simParams.l));
        editparam = (EditText) findViewById(R.id.SSP_editm);
        ((TextView)findViewById(R.id.SSP_labelm1)).setText(Html.fromHtml(getResources().getString(R.string.SSP_label_m)));
        editparam.setText(Float.toString(SSPSimulationParameters.simParams.m1));
        editparam = (EditText) findViewById(R.id.SSP_editm2);
        ((TextView)findViewById(R.id.SSP_labelm2)).setText(Html.fromHtml(getResources().getString(R.string.SSP_label_m2)));
        editparam.setText(Float.toString(SSPSimulationParameters.simParams.m2));
        ((TextView)findViewById(R.id.SSP_labelg)).setText(Html.fromHtml(getResources().getString(R.string.SSP_label_g)));
        editparam = (EditText) findViewById(R.id.SSP_editg);
        editparam.setText(Float.toString(SSPSimulationParameters.simParams.g / 100.f));
        ((TextView)findViewById(R.id.SSP_label_k)).setText(Html.fromHtml(getResources().getString(R.string.SSP_label_k)));
        editparam = (EditText) findViewById(R.id.SSP_editk);
        editparam.setText(Float.toString(SSPSimulationParameters.simParams.k));
        editparam = (EditText) findViewById(R.id.SSP_editgam);
        editparam.setText(Float.toString(SSPSimulationParameters.simParams.gam * 1.e3f));

        RadioButton rbrand = (RadioButton) findViewById(R.id.SSP_radioRand);
        rbrand.setChecked(SSPSimulationParameters.simParams.initRandom);
        rbrand = (RadioButton) findViewById(R.id.SSP_radioFixed);
        rbrand.setChecked(!SSPSimulationParameters.simParams.initRandom);

        ((TextView)findViewById(R.id.SSP_labelx0)).setText(Html.fromHtml(getResources().getString(R.string.SSP_label_x0)));
        editparam = (EditText) findViewById(R.id.SSP_editx0);
        editparam.setText(Float.toString((float) (SSPSimulationParameters.simParams.x)));
        ((TextView)findViewById(R.id.SSP_labelxv0)).setText(Html.fromHtml(getResources().getString(R.string.SSP_label_xv0)));
        editparam = (EditText) findViewById(R.id.SSP_editxv0);
        editparam.setText(Float.toString((float) (SSPSimulationParameters.simParams.xv)));
        ((TextView)findViewById(R.id.SSP_labely0)).setText(Html.fromHtml(getResources().getString(R.string.SSP_label_y0)));
        editparam = (EditText) findViewById(R.id.SSP_edity0);
        editparam.setText(Float.toString((float) (SSPSimulationParameters.simParams.y)));
        ((TextView)findViewById(R.id.SSP_labelyv0)).setText(Html.fromHtml(getResources().getString(R.string.SSP_label_yv0)));
        editparam = (EditText) findViewById(R.id.SSP_edityv0);
        editparam.setText(Float.toString((float) (SSPSimulationParameters.simParams.yv)));
        ((TextView)findViewById(R.id.SSP_labelz0)).setText(Html.fromHtml(getResources().getString(R.string.SSP_label_z0)));
        editparam = (EditText) findViewById(R.id.SSP_editz0);
        editparam.setText(Float.toString((float) (SSPSimulationParameters.simParams.z)));
        ((TextView)findViewById(R.id.SSP_labelzv0)).setText(Html.fromHtml(getResources().getString(R.string.SSP_label_zv0)));
        editparam = (EditText) findViewById(R.id.SSP_editzv0);
        editparam.setText(Float.toString((float) (SSPSimulationParameters.simParams.zv)));


        ((TextView)findViewById(R.id.SSP_labelth0)).setText(Html.fromHtml(getResources().getString(R.string.SSP_label_th0)));
        editparam = (EditText) findViewById(R.id.SSP_editth0);
        editparam.setText(Float.toString((float) (SSPSimulationParameters.simParams.th1 * 180.f / Math.PI)));
        ((TextView)findViewById(R.id.SSP_labelthv0)).setText(Html.fromHtml(getResources().getString(R.string.SSP_label_thv0)));
        editparam = (EditText) findViewById(R.id.SSP_editthv0);
        editparam.setText(Float.toString((float) (SSPSimulationParameters.simParams.thv1 * 180.f / Math.PI)));
        ((TextView)findViewById(R.id.SSP_labelph0)).setText(Html.fromHtml(getResources().getString(R.string.SSP_label_ph0)));
        editparam = (EditText) findViewById(R.id.SSP_editph0);
        editparam.setText(Float.toString((float) (SSPSimulationParameters.simParams.ph1 * 180.f / Math.PI)));
        ((TextView)findViewById(R.id.SSP_labelphv0)).setText(Html.fromHtml(getResources().getString(R.string.SSP_label_phv0)));
        editparam = (EditText) findViewById(R.id.SSP_editphv0);
        editparam.setText(Float.toString((float) (SSPSimulationParameters.simParams.phv1 * 180.f / Math.PI)));

        CheckBox checkTraj = (CheckBox) findViewById(R.id.SSP_checkBoxTraj);
        checkTraj.setChecked(SSPSimulationParameters.simParams.showTrajectory);

        CheckBox checkTrajInfinite = (CheckBox) findViewById(R.id.SSP_checkBoxTrajInfinite);
        checkTrajInfinite.setChecked(SSPSimulationParameters.simParams.infiniteTrajectory);

        editparam = (EditText) findViewById(R.id.SSP_edittrajle);
        editparam.setText(Integer.toString(SSPSimulationParameters.simParams.traceLength));

        pendulumColor = SSPSimulationParameters.simParams.pendulumColor;
        Button PColor = (Button) findViewById(R.id.SSP_PendulumColor);
        PColor.setBackgroundColor(pendulumColor);

        pendulumColor2 = SSPSimulationParameters.simParams.pendulumColor2;
        Button PColor2 = (Button) findViewById(R.id.SSP_PendulumColor2);
        PColor2.setBackgroundColor(pendulumColor2);


        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        boolean full_screen = sharedPref.getBoolean("pref_fullscreen", false);
        CheckBox checkFullScreen = (CheckBox) findViewById(R.id.pref_fullscreen_loc);
        checkFullScreen.setChecked(full_screen);

        boolean fps = sharedPref.getBoolean("pref_fps", false);
        CheckBox checkFps = (CheckBox) findViewById(R.id.pref_fps_loc);
        checkFps.setChecked(fps);
    }
    
    public void okButton(View v) {
    	EditText editparam = (EditText) findViewById(R.id.SSP_edita);
        if (!editparam.getText().toString().equals("") && Float.parseFloat(editparam.getText().toString())>0.) SSPSimulationParameters.simParams.aa = Float.parseFloat(editparam.getText().toString());
    	editparam = (EditText) findViewById(R.id.SSP_editl);
        if (!editparam.getText().toString().equals("") && Float.parseFloat(editparam.getText().toString())>0.) SSPSimulationParameters.simParams.l = Float.parseFloat(editparam.getText().toString());
    	editparam = (EditText) findViewById(R.id.SSP_editm);
        if (!editparam.getText().toString().equals("") && Float.parseFloat(editparam.getText().toString())>0.) SSPSimulationParameters.simParams.m1 = Float.parseFloat(editparam.getText().toString());
    	editparam = (EditText) findViewById(R.id.SSP_editm2);
        if (!editparam.getText().toString().equals("") && Float.parseFloat(editparam.getText().toString())>0.) SSPSimulationParameters.simParams.m2 = Float.parseFloat(editparam.getText().toString());
    	editparam = (EditText) findViewById(R.id.SSP_editg);
        if (!editparam.getText().toString().equals("")) SSPSimulationParameters.simParams.g = Float.parseFloat(editparam.getText().toString()) * 100.f;
    	editparam = (EditText) findViewById(R.id.SSP_editk);
        if (!editparam.getText().toString().equals("")) SSPSimulationParameters.simParams.k = Float.parseFloat(editparam.getText().toString());
    	editparam = (EditText) findViewById(R.id.SSP_editgam);
        if (!editparam.getText().toString().equals("")) SSPSimulationParameters.simParams.gam = Float.parseFloat(editparam.getText().toString()) / 1.e3f;
    	
    	RadioButton rbrand = (RadioButton) findViewById(R.id.SSP_radioRand);
    	SSPSimulationParameters.simParams.initRandom = rbrand.isChecked();
    	
    	editparam = (EditText) findViewById(R.id.SSP_editx0);
        if (!editparam.getText().toString().equals("")) SSPSimulationParameters.simParams.x = (float)(Float.parseFloat(editparam.getText().toString()));
    	editparam = (EditText) findViewById(R.id.SSP_editxv0);
        if (!editparam.getText().toString().equals("")) SSPSimulationParameters.simParams.xv = (float)(Float.parseFloat(editparam.getText().toString()));
    	editparam = (EditText) findViewById(R.id.SSP_edity0);
        if (!editparam.getText().toString().equals("")) SSPSimulationParameters.simParams.y = (float)(Float.parseFloat(editparam.getText().toString()));
    	editparam = (EditText) findViewById(R.id.SSP_edityv0);
        if (!editparam.getText().toString().equals("")) SSPSimulationParameters.simParams.yv = (float)(Float.parseFloat(editparam.getText().toString()));
    	editparam = (EditText) findViewById(R.id.SSP_editz0);
        if (!editparam.getText().toString().equals("")) SSPSimulationParameters.simParams.z = (float)(Float.parseFloat(editparam.getText().toString()));
    	editparam = (EditText) findViewById(R.id.SSP_editzv0);
        if (!editparam.getText().toString().equals("")) SSPSimulationParameters.simParams.zv = (float)(Float.parseFloat(editparam.getText().toString()));
    	
    	editparam = (EditText) findViewById(R.id.SSP_editth0);
        if (!editparam.getText().toString().equals("")) SSPSimulationParameters.simParams.th1 = (float)(Float.parseFloat(editparam.getText().toString()) *
    			Math.PI / 180.f);
    	editparam = (EditText) findViewById(R.id.SSP_editthv0);
        if (!editparam.getText().toString().equals("")) SSPSimulationParameters.simParams.thv1 = (float)(Float.parseFloat(editparam.getText().toString()) *
    			Math.PI / 180.f);
    	editparam = (EditText) findViewById(R.id.SSP_editph0);
        if (!editparam.getText().toString().equals("")) SSPSimulationParameters.simParams.ph1 = (float)(Float.parseFloat(editparam.getText().toString()) *
    			Math.PI / 180.f);
    	editparam = (EditText) findViewById(R.id.SSP_editphv0);
        if (!editparam.getText().toString().equals("")) SSPSimulationParameters.simParams.phv1 = (float)(Float.parseFloat(editparam.getText().toString()) *
    			Math.PI / 180.f);

    	CheckBox checkTraj = (CheckBox) findViewById(R.id.SSP_checkBoxTraj);
    	SSPSimulationParameters.simParams.showTrajectory = checkTraj.isChecked();

        CheckBox checkTrajInfinite = (CheckBox) findViewById(R.id.SSP_checkBoxTrajInfinite);
        SSPSimulationParameters.simParams.infiniteTrajectory = checkTrajInfinite.isChecked();
    	
    	editparam = (EditText) findViewById(R.id.SSP_edittrajle);
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
                SSPSimulationParameters.simParams.traceLength = trlength;
            else {
                SSPSimulationParameters.simParams.infiniteTrajectory = true;
            }
        }

        SSPSimulationParameters.simParams.pendulumColor = pendulumColor;
        SSPSimulationParameters.simParams.pendulumColor2 = pendulumColor2;

        SSPSimulationParameters.simParams.writeSettings(this.getSharedPreferences(SSPSimulationParameters.PREFS_NAME, 0));

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        CheckBox checkFullScreen = (CheckBox) findViewById(R.id.pref_fullscreen_loc);
        editor.putBoolean("pref_fullscreen", checkFullScreen.isChecked());
        CheckBox checkFps = (CheckBox) findViewById(R.id.pref_fps_loc);
        editor.putBoolean("pref_fps", checkFps.isChecked());
        editor.commit();
    	
    	SSPParametersActivity.this.finish();
    }
    
    public void cancelButton(View v) {
        SSPParametersActivity.this.finish();
    }

    public void resetButton(View v) {
        SSPSimulationParameters.simParams.clearSettings(this.getSharedPreferences(SSPSimulationParameters.PREFS_NAME, 0));
        readParameters();
    }

    public void PendulumColor(View v) {
        final ColorPickerDialog colorDialog = new ColorPickerDialog(this,pendulumColor);
        colorDialog.setTitle(R.string.pick_color);

        colorDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(android.R.string.ok), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                pendulumColor = colorDialog.getColor();
                Button PColor = (Button) findViewById(R.id.SSP_PendulumColor);
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
                Button PColor = (Button) findViewById(R.id.SSP_PendulumColor2);
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
