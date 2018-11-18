package com.vlvolad.pendulumstudio.springpendulum3d;

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

public class SP3DParametersActivity extends Activity {
    private int settingsEvent, pendulumColor;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.springpendulum3d_parameters);
        readParameters();
    }

    public void readParameters() {
        EditText editparam = (EditText) findViewById(R.id.SP3D_edita);
        ((TextView)findViewById(R.id.SP3D_labelaa)).setText(Html.fromHtml(getResources().getString(R.string.SP3D_label_aa)));
        editparam.setText(Float.toString(SP3DSimulationParameters.simParams.aa));
        editparam = (EditText) findViewById(R.id.SP3D_editm);
        ((TextView)findViewById(R.id.SP3D_labelm)).setText(Html.fromHtml(getResources().getString(R.string.SP3D_label_m)));
        editparam.setText(Float.toString(SP3DSimulationParameters.simParams.m));
        ((TextView)findViewById(R.id.SP3D_labelg)).setText(Html.fromHtml(getResources().getString(R.string.SP3D_label_g)));
        editparam = (EditText) findViewById(R.id.SP3D_editg);
        editparam.setText(Float.toString(SP3DSimulationParameters.simParams.g / 100.f));
        ((TextView)findViewById(R.id.SP3D_label_k)).setText(Html.fromHtml(getResources().getString(R.string.SP3D_label_k)));
        editparam = (EditText) findViewById(R.id.SP3D_editk);
        editparam.setText(Float.toString(SP3DSimulationParameters.simParams.k));
        editparam = (EditText) findViewById(R.id.SP3D_editgam);
        editparam.setText(Float.toString(SP3DSimulationParameters.simParams.gam * 1.e3f));

        RadioButton rbrand = (RadioButton) findViewById(R.id.SP3D_radioRand);
        rbrand.setChecked(SP3DSimulationParameters.simParams.initRandom);
        rbrand = (RadioButton) findViewById(R.id.SP3D_radioFixed);
        rbrand.setChecked(!SP3DSimulationParameters.simParams.initRandom);

        ((TextView)findViewById(R.id.SP3D_labelx0)).setText(Html.fromHtml(getResources().getString(R.string.SP3D_label_x0)));
        editparam = (EditText) findViewById(R.id.SP3D_editx0);
        editparam.setText(Float.toString((float) (SP3DSimulationParameters.simParams.x)));
        ((TextView)findViewById(R.id.SP3D_labelxv0)).setText(Html.fromHtml(getResources().getString(R.string.SP3D_label_xv0)));
        editparam = (EditText) findViewById(R.id.SP3D_editxv0);
        editparam.setText(Float.toString((float) (SP3DSimulationParameters.simParams.xv)));


        ((TextView)findViewById(R.id.SP3D_labely0)).setText(Html.fromHtml(getResources().getString(R.string.SP3D_label_y0)));
        editparam = (EditText) findViewById(R.id.SP3D_edity0);
        editparam.setText(Float.toString((float) (SP3DSimulationParameters.simParams.y)));
        ((TextView)findViewById(R.id.SP3D_labelyv0)).setText(Html.fromHtml(getResources().getString(R.string.SP3D_label_yv0)));
        editparam = (EditText) findViewById(R.id.SP3D_edityv0);
        editparam.setText(Float.toString((float) (SP3DSimulationParameters.simParams.yv)));

        ((TextView)findViewById(R.id.SP3D_labelz0)).setText(Html.fromHtml(getResources().getString(R.string.SP3D_label_z0)));
        editparam = (EditText) findViewById(R.id.SP3D_editz0);
        editparam.setText(Float.toString((float) (SP3DSimulationParameters.simParams.z)));
        ((TextView)findViewById(R.id.SP3D_labelzv0)).setText(Html.fromHtml(getResources().getString(R.string.SP3D_label_zv0)));
        editparam = (EditText) findViewById(R.id.SP3D_editzv0);
        editparam.setText(Float.toString((float) (SP3DSimulationParameters.simParams.zv)));

        CheckBox checkTraj = (CheckBox) findViewById(R.id.SP3D_checkBoxTraj);
        checkTraj.setChecked(SP3DSimulationParameters.simParams.showTrajectory);

        editparam = (EditText) findViewById(R.id.SP3D_edittrajle);
        editparam.setText(Integer.toString(SP3DSimulationParameters.simParams.traceLength));

        CheckBox checkTrajInfinite = (CheckBox) findViewById(R.id.SP3D_checkBoxTrajInfinite);
        checkTrajInfinite.setChecked(SP3DSimulationParameters.simParams.infiniteTrajectory);

        pendulumColor = SP3DSimulationParameters.simParams.pendulumColor;
        Button PColor = (Button) findViewById(R.id.SP3D_PendulumColor);
        PColor.setBackgroundColor(pendulumColor);


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
    	EditText editparam = (EditText) findViewById(R.id.SP3D_edita);
        if (!editparam.getText().toString().equals("") && Float.parseFloat(editparam.getText().toString())>0.) SP3DSimulationParameters.simParams.aa = Float.parseFloat(editparam.getText().toString());
    	editparam = (EditText) findViewById(R.id.SP3D_editm);
        if (!editparam.getText().toString().equals("") && Float.parseFloat(editparam.getText().toString())>0.) SP3DSimulationParameters.simParams.m = Float.parseFloat(editparam.getText().toString());
    	editparam = (EditText) findViewById(R.id.SP3D_editg);
        if (!editparam.getText().toString().equals("")) SP3DSimulationParameters.simParams.g = Float.parseFloat(editparam.getText().toString()) * 100.f;
    	editparam = (EditText) findViewById(R.id.SP3D_editk);
        if (!editparam.getText().toString().equals("")) SP3DSimulationParameters.simParams.k = Float.parseFloat(editparam.getText().toString());
    	editparam = (EditText) findViewById(R.id.SP3D_editgam);
        if (!editparam.getText().toString().equals("")) SP3DSimulationParameters.simParams.gam = Float.parseFloat(editparam.getText().toString()) / 1.e3f;
    	
    	RadioButton rbrand = (RadioButton) findViewById(R.id.SP3D_radioRand);
    	SP3DSimulationParameters.simParams.initRandom = rbrand.isChecked();
    	
    	editparam = (EditText) findViewById(R.id.SP3D_editx0);
        if (!editparam.getText().toString().equals("")) SP3DSimulationParameters.simParams.x = (float)(Float.parseFloat(editparam.getText().toString()));
    	editparam = (EditText) findViewById(R.id.SP3D_editxv0);
        if (!editparam.getText().toString().equals("")) SP3DSimulationParameters.simParams.xv = (float)(Float.parseFloat(editparam.getText().toString()));
    	editparam = (EditText) findViewById(R.id.SP3D_edity0);
        if (!editparam.getText().toString().equals("")) SP3DSimulationParameters.simParams.y = (float)(Float.parseFloat(editparam.getText().toString()));
    	editparam = (EditText) findViewById(R.id.SP3D_edityv0);
        if (!editparam.getText().toString().equals("")) SP3DSimulationParameters.simParams.yv = (float)(Float.parseFloat(editparam.getText().toString()));
    	editparam = (EditText) findViewById(R.id.SP3D_editz0);
        if (!editparam.getText().toString().equals("")) SP3DSimulationParameters.simParams.z = (float)(Float.parseFloat(editparam.getText().toString()));
    	editparam = (EditText) findViewById(R.id.SP3D_editzv0);
        if (!editparam.getText().toString().equals("")) SP3DSimulationParameters.simParams.zv = (float)(Float.parseFloat(editparam.getText().toString()));
    	
    	CheckBox checkTraj = (CheckBox) findViewById(R.id.SP3D_checkBoxTraj);
    	SP3DSimulationParameters.simParams.showTrajectory = checkTraj.isChecked();

        CheckBox checkTrajInfinite = (CheckBox) findViewById(R.id.SP3D_checkBoxTrajInfinite);
        SP3DSimulationParameters.simParams.infiniteTrajectory = checkTrajInfinite.isChecked();
    	
    	editparam = (EditText) findViewById(R.id.SP3D_edittrajle);
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
                SP3DSimulationParameters.simParams.traceLength = trlength;
            else {
                SP3DSimulationParameters.simParams.infiniteTrajectory = true;
            }
        }

        SP3DSimulationParameters.simParams.pendulumColor = pendulumColor;

        SP3DSimulationParameters.simParams.writeSettings(this.getSharedPreferences(SP3DSimulationParameters.PREFS_NAME, 0));

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        CheckBox checkFullScreen = (CheckBox) findViewById(R.id.pref_fullscreen_loc);
        editor.putBoolean("pref_fullscreen", checkFullScreen.isChecked());
        CheckBox checkFps = (CheckBox) findViewById(R.id.pref_fps_loc);
        editor.putBoolean("pref_fps", checkFps.isChecked());
        CheckBox checkFade = (CheckBox) findViewById(R.id.pref_buttons_fade_loc);
        editor.putBoolean("pref_buttons_fade", checkFade.isChecked());
        editor.commit();
    	
    	SP3DParametersActivity.this.finish();
    }
    
    public void cancelButton(View v) {
    	SP3DParametersActivity.this.finish();
    }

    public void resetButton(View v) {
       SP3DSimulationParameters.simParams.clearSettings(this.getSharedPreferences(SP3DSimulationParameters.PREFS_NAME, 0));
       readParameters();
    }

    public void PendulumColor(View v) {
        final ColorPickerDialog colorDialog = new ColorPickerDialog(this,pendulumColor);
        colorDialog.setTitle(R.string.pick_color);

        colorDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(android.R.string.ok), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                pendulumColor = colorDialog.getColor();
                Button PColor = (Button) findViewById(R.id.SP3D_PendulumColor);
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
