package com.vlvolad.pendulumstudio;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class AboutActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_about);
    }

    /**
     * Callback method defined by the View
     * @param v
     */
    public void finishDialog(View v) {
        AboutActivity.this.finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
