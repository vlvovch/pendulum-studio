package com.vlvolad.pendulumstudio.pendulumwave;

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

import com.vlvolad.pendulumstudio.R;

import afzkl.development.colorpickerview.dialog.ColorPickerDialog;

/**
 * Created by Volodymyr on 26.05.2015.
 */
public class PWParametersActivity extends Activity {
    private int settingsEvent, pendulumColor;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pendulumwave_parameters);
        readParameters();
        settingsEvent = -1;
    }

    public void readParameters() {
        EditText editparam = (EditText) findViewById(R.id.PW_editl);
        editparam.setText(Float.toString(PWSimulationParameters.simParams.l));
        editparam = (EditText) findViewById(R.id.PW_editm);
        editparam.setText(Float.toString(PWSimulationParameters.simParams.m));
        editparam = (EditText) findViewById(R.id.PW_editNP);
        editparam.setText(Integer.toString(PWSimulationParameters.simParams.NP));
        editparam = (EditText) findViewById(R.id.PW_editNT);
        editparam.setText(Integer.toString(PWSimulationParameters.simParams.NT));
        ((TextView) findViewById(R.id.PW_labelg)).setText(Html.fromHtml(getResources().getString(R.string.PW_label_g)));
        editparam = (EditText) findViewById(R.id.PW_editg);
        editparam.setText(Float.toString(PWSimulationParameters.simParams.g / 100.f));
        editparam = (EditText) findViewById(R.id.PW_editk);
        editparam.setText(Float.toString(PWSimulationParameters.simParams.k * 1.e3f));

        RadioButton rbrand = (RadioButton) findViewById(R.id.PW_radioRand);
        rbrand.setChecked(PWSimulationParameters.simParams.initRandom);
        rbrand = (RadioButton) findViewById(R.id.PW_radioFixed);
        rbrand.setChecked(!PWSimulationParameters.simParams.initRandom);

        ((TextView) findViewById(R.id.PW_label_th0)).setText(Html.fromHtml(getResources().getString(R.string.PW_label_th0)));
        editparam = (EditText) findViewById(R.id.PW_editth0);
        editparam.setText(Float.toString((float) (PWSimulationParameters.simParams.th0 * 180.f / Math.PI)));

        pendulumColor = PWSimulationParameters.simParams.pendulumColor;
        Button PColor = (Button) findViewById(R.id.PW_PendulumColor);
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
        EditText editparam = (EditText) findViewById(R.id.PW_editl);
        if (!editparam.getText().toString().equals("") && Float.parseFloat(editparam.getText().toString())>0.) PWSimulationParameters.simParams.l = Float.parseFloat(editparam.getText().toString());
        editparam = (EditText) findViewById(R.id.PW_editm);
        if (!editparam.getText().toString().equals("") && Float.parseFloat(editparam.getText().toString())>0.) PWSimulationParameters.simParams.m = Float.parseFloat(editparam.getText().toString());
        editparam = (EditText) findViewById(R.id.PW_editNP);
        if (!editparam.getText().toString().equals("")) {
            int NP = 0;

            try {
                NP = (int)(Integer.parseInt(editparam.getText().toString()));
            } catch (NumberFormatException e) {
                NP = -1;
            }

            if (NP>100) {
                NP = 100;
                Context context = getApplicationContext();
                CharSequence text = getString(R.string.PWLimit);//"Trace too long! Setting to 100000...";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }

            if (NP<=0)
                NP = PWSimulationParameters.simParams.NP;

            PWSimulationParameters.simParams.NP = NP;
        }
        editparam = (EditText) findViewById(R.id.PW_editNT);
        if (!editparam.getText().toString().equals("")) {
            int NT = 0;

            try {
                NT = (int)(Integer.parseInt(editparam.getText().toString()));
            } catch (NumberFormatException e) {
                NT = -1;
            }

            if (NT<=0)
                NT = PWSimulationParameters.simParams.NT;

            PWSimulationParameters.simParams.NT = NT;
        }
        editparam = (EditText) findViewById(R.id.PW_editg);
        if (!editparam.getText().toString().equals("")) {
            PWSimulationParameters.simParams.g = Float.parseFloat(editparam.getText().toString()) * 100.f;
            if (PWSimulationParameters.simParams.g<0.001f) {
                PWSimulationParameters.simParams.g = 0.001f;

                Context context = getApplicationContext();
                CharSequence text = getString(R.string.PWg);//"Trace too long! Setting to 100000...";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        }
        editparam = (EditText) findViewById(R.id.PW_editk);
        if (!editparam.getText().toString().equals("")) PWSimulationParameters.simParams.k = Float.parseFloat(editparam.getText().toString()) / 1.e3f;

        RadioButton rbrand = (RadioButton) findViewById(R.id.PW_radioRand);
        PWSimulationParameters.simParams.initRandom = rbrand.isChecked();

        editparam = (EditText) findViewById(R.id.PW_editth0);
        if (!editparam.getText().toString().equals("")) PWSimulationParameters.simParams.th0 = (float)(Float.parseFloat(editparam.getText().toString()) *
                Math.PI / 180.f);

        PWSimulationParameters.simParams.pendulumColor = pendulumColor;

        PWSimulationParameters.simParams.writeSettings(this.getSharedPreferences(PWSimulationParameters.PREFS_NAME, 0));

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        CheckBox checkFullScreen = (CheckBox) findViewById(R.id.pref_fullscreen_loc);
        editor.putBoolean("pref_fullscreen", checkFullScreen.isChecked());
        CheckBox checkFps = (CheckBox) findViewById(R.id.pref_fps_loc);
        editor.putBoolean("pref_fps", checkFps.isChecked());
        CheckBox checkFade = (CheckBox) findViewById(R.id.pref_buttons_fade_loc);
        editor.putBoolean("pref_buttons_fade", checkFade.isChecked());
        editor.commit();

        PWParametersActivity.this.finish();
    }

    public void cancelButton(View v) {
        PWParametersActivity.this.finish();
    }

    public void PendulumColor(View v) {
        final ColorPickerDialog colorDialog = new ColorPickerDialog(this,pendulumColor);
        colorDialog.setTitle(R.string.pick_color);

        colorDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(android.R.string.ok), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                pendulumColor = colorDialog.getColor();
                Button PColor = (Button) findViewById(R.id.PW_PendulumColor);
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

    @Override
    protected void onResume() {
        super.onResume();
        if (settingsEvent==1) {
            Button PColor = (Button) findViewById(R.id.PW_PendulumColor);
            PColor.setBackgroundColor(pendulumColor);
        }
        settingsEvent = -1;
    }

    public void resetButton(View v) {
        PWSimulationParameters.simParams.clearSettings(this.getSharedPreferences(PWSimulationParameters.PREFS_NAME, 0));
        readParameters();
    }

    public void colorChanged(String key, int color) {
        // TODO Auto-generated method stub
        //paint.setColor(color);
    }
}
