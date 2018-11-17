package com.vlvolad.pendulumstudio;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


public class InformationActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information);
        setTitle(R.string.information);

        TextView tver = (TextView) findViewById(R.id.version_text);
        tver.setText(getText(R.string.version).toString() + " " + BuildConfig.VERSION_NAME);

        TextView tv = (TextView) findViewById(R.id.google_play);
        //makeTextViewHyperlink(tv);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (Exception e) {
                    Log.d("Information", "Message =" + e);

                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                PreferenceManager.getDefaultSharedPreferences(InformationActivity.this).edit().putBoolean("rate_clicked", true).apply();
            }
        });

        TextView tv2 = (TextView) findViewById(R.id.description);
        if (tv2 != null)
            tv2.setMovementMethod(LinkMovementMethod.getInstance());

        TextView tv3 = (TextView) findViewById(R.id.more_apps);
        //makeTextViewHyperlink(tv3);
        tv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://developer?id=Voladd")));
                } catch (Exception e) {
//                    Log.d("Information", "Message =" + e);
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=Voladd")));
                }
            }
        });


        TextView tv5 = (TextView) findViewById(R.id.share_link);
        //makeTextViewHyperlink(tv5);
        tv5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getText(R.string.share_subject).toString());
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, getText(R.string.share_text).toString());
                startActivity(Intent.createChooser(sharingIntent, getText(R.string.share_via).toString()));
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * Sets a hyperlink style to the textview.
     */
    public static void makeTextViewHyperlink(TextView tv) {
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb.append(tv.getText());
        ssb.setSpan(new URLSpan("#"), 0, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setText(ssb, TextView.BufferType.SPANNABLE);
    }
}
