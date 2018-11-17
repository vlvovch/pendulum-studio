package com.vlvolad.pendulumstudio;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
//import android.support.v4.view.MenuItemCompat;
//import android.support.v7.app.ActionBarActivity;
//import android.support.v7.widget.ShareActionProvider;
//import android.widget.ShareActionProvider;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter;
//import android.support.v7.app.ActionBar;

import com.vlvolad.pendulumstudio.mathematicalpendulum.MPGLActivity;
import com.vlvolad.pendulumstudio.mathematicalpendulum.MPGLRenderer;
import com.vlvolad.pendulumstudio.mathematicalpendulum.MPSimulationParameters;
import com.vlvolad.pendulumstudio.pendulumwave.PWGLActivity;
import com.vlvolad.pendulumstudio.pendulumwave.PWGLRenderer;
import com.vlvolad.pendulumstudio.pendulumwave.PWSimulationParameters;
import com.vlvolad.pendulumstudio.sphericalpendulum.SPGLActivity;
import com.vlvolad.pendulumstudio.sphericalpendulum.SPGLRenderer;
import com.vlvolad.pendulumstudio.sphericalpendulum.SPSimulationParameters;
import com.vlvolad.pendulumstudio.springmathematicalpendulum.SMPGLActivity;
import com.vlvolad.pendulumstudio.springmathematicalpendulum.SMPGLRenderer;
import com.vlvolad.pendulumstudio.springmathematicalpendulum.SMPSimulationParameters;
import com.vlvolad.pendulumstudio.springpendulum2d.SP2DGLActivity;
import com.vlvolad.pendulumstudio.springpendulum2d.SP2DGLRenderer;
import com.vlvolad.pendulumstudio.springpendulum2d.SP2DSimulationParameters;
import com.vlvolad.pendulumstudio.springpendulum3d.SP3DGLActivity;
import com.vlvolad.pendulumstudio.springpendulum3d.SP3DGLRenderer;
import com.vlvolad.pendulumstudio.springpendulum3d.SP3DSimulationParameters;
import com.vlvolad.pendulumstudio.springsphericalpendulum.SSPGLActivity;
import com.vlvolad.pendulumstudio.springsphericalpendulum.SSPGLRenderer;
import com.vlvolad.pendulumstudio.springsphericalpendulum.SSPSimulationParameters;
import com.vlvolad.pendulumstudio.doublependulum.DPGLActivity;
import com.vlvolad.pendulumstudio.doublependulum.DPGLRenderer;
import com.vlvolad.pendulumstudio.doublependulum.DPSimulationParameters;
import com.vlvolad.pendulumstudio.doublesphericalpendulum.DSPGLActivity;
import com.vlvolad.pendulumstudio.doublesphericalpendulum.DSPGLRenderer;
import com.vlvolad.pendulumstudio.doublesphericalpendulum.DSPSimulationParameters;

public class MainActivity extends ListActivity {

	private static final String TAG = "MainActivity";
	private static final String ITEM_IMAGE = "item_image";
	private static final String ITEM_TITLE = "item_title";
	private static final String ITEM_SUBTITLE = "item_subtitle";

    //private ShareActionProvider mShareActionProvider;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.toc);
        setContentView(R.layout.activity_main);
        
        final List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		final SparseArray<Class<? extends Activity>> activityMapping = new SparseArray<Class<? extends Activity>>();
		
		int i = 0;
		
		{
			final Map<String, Object> item = new HashMap<String, Object>();
			item.put(ITEM_IMAGE, R.drawable.ic_mp);
			item.put(ITEM_TITLE, getText(R.string.mp));
			item.put(ITEM_SUBTITLE, getText(R.string.mp_subtitle));
			data.add(item);
			activityMapping.put(i++, MPGLActivity.class);
			
			MPSimulationParameters.simParams.readSettings(this.getSharedPreferences(MPSimulationParameters.PREFS_NAME, 0));
			MPGLRenderer.mPendulum.restart();
			MPGLRenderer.mPendulum.k = 0.;
		}

        {
            final Map<String, Object> item = new HashMap<String, Object>();
            item.put(ITEM_IMAGE, R.drawable.ic_pw);
            item.put(ITEM_TITLE, getText(R.string.pw));
            item.put(ITEM_SUBTITLE, getText(R.string.pw_subtitle));
            data.add(item);
            activityMapping.put(i++, PWGLActivity.class);

            PWSimulationParameters.simParams.readSettings(this.getSharedPreferences(PWSimulationParameters.PREFS_NAME, 0));
            PWGLRenderer.mPendulum.restart();
            PWGLRenderer.mPendulum.k = 0.;
            PWGLRenderer.mPendulum.setDamping(0.);
        }
		
		{
			final Map<String, Object> item = new HashMap<String, Object>();
			item.put(ITEM_IMAGE, R.drawable.ic_sp);
			item.put(ITEM_TITLE, getText(R.string.sp));
			item.put(ITEM_SUBTITLE, getText(R.string.sp_subtitle));
			data.add(item);
			activityMapping.put(i++, SPGLActivity.class);
			
			SPSimulationParameters.simParams.readSettings(this.getSharedPreferences(SPSimulationParameters.PREFS_NAME, 0));
			SPGLRenderer.mPendulum.restart();
			SPGLRenderer.mPendulum.k = 0.;
		}
		
		{
			final Map<String, Object> item = new HashMap<String, Object>();
			item.put(ITEM_IMAGE, R.drawable.ic_sp2d);
			item.put(ITEM_TITLE, getText(R.string.sp2d));
			item.put(ITEM_SUBTITLE, getText(R.string.sp2d_subtitle));
			data.add(item);
			activityMapping.put(i++, SP2DGLActivity.class);
			
			SP2DSimulationParameters.simParams.readSettings(this.getSharedPreferences(SP2DSimulationParameters.PREFS_NAME, 0));
			SP2DGLRenderer.mPendulum.restart();
			SP2DGLRenderer.mPendulum.gam = 0.;
		}
		
		{
			final Map<String, Object> item = new HashMap<String, Object>();
			item.put(ITEM_IMAGE, R.drawable.ic_sp3d);
			item.put(ITEM_TITLE, getText(R.string.sp3d));
			item.put(ITEM_SUBTITLE, getText(R.string.sp3d_subtitle));
			data.add(item);
			activityMapping.put(i++, SP3DGLActivity.class);	
			
			SP3DSimulationParameters.simParams.readSettings(this.getSharedPreferences(SP3DSimulationParameters.PREFS_NAME, 0));
			SP3DGLRenderer.mPendulum.restart();
			SP3DGLRenderer.mPendulum.gam = 0.;
		}
		
		{
			final Map<String, Object> item = new HashMap<String, Object>();
			item.put(ITEM_IMAGE, R.drawable.ic_dp);
			item.put(ITEM_TITLE, getText(R.string.dp));
			item.put(ITEM_SUBTITLE, getText(R.string.dp_subtitle));
			data.add(item);
			activityMapping.put(i++, DPGLActivity.class);	
			
			DPSimulationParameters.simParams.readSettings(this.getSharedPreferences(DPSimulationParameters.PREFS_NAME, 0));
			DPGLRenderer.mPendulum.restart();
			DPGLRenderer.mPendulum.k = 0.;
		}
		
		{
			final Map<String, Object> item = new HashMap<String, Object>();
			item.put(ITEM_IMAGE, R.drawable.ic_dsp);
			item.put(ITEM_TITLE, getText(R.string.dsp));
			item.put(ITEM_SUBTITLE, getText(R.string.dsp_subtitle));
			data.add(item);
			activityMapping.put(i++, DSPGLActivity.class);		
			
			DSPSimulationParameters.simParams.readSettings(this.getSharedPreferences(DSPSimulationParameters.PREFS_NAME, 0));
			DSPGLRenderer.mPendulum.restart();
			DSPGLRenderer.mPendulum.k = 0.;
		}
		
		{
			final Map<String, Object> item = new HashMap<String, Object>();
			item.put(ITEM_IMAGE, R.drawable.ic_smp);
			item.put(ITEM_TITLE, getText(R.string.smp));
			item.put(ITEM_SUBTITLE, getText(R.string.smp_subtitle));
			data.add(item);
			activityMapping.put(i++, SMPGLActivity.class);	
			
			SMPSimulationParameters.simParams.readSettings(this.getSharedPreferences(SMPSimulationParameters.PREFS_NAME, 0));
			SMPGLRenderer.mPendulum.restart();
			SMPGLRenderer.mPendulum.gam = 0.;
		}
		
		{
			final Map<String, Object> item = new HashMap<String, Object>();
			item.put(ITEM_IMAGE, R.drawable.ic_ssp3d);
			item.put(ITEM_TITLE, getText(R.string.ssp));
			item.put(ITEM_SUBTITLE, getText(R.string.ssp_subtitle));
			data.add(item);
			activityMapping.put(i++, SSPGLActivity.class);		
			
			SSPSimulationParameters.simParams.readSettings(this.getSharedPreferences(SSPSimulationParameters.PREFS_NAME, 0));
			SSPGLRenderer.mPendulum.restart();
			SSPGLRenderer.mPendulum.gam = 0.;
		}
		
		final SimpleAdapter dataAdapter = new SimpleAdapter(this, data, R.layout.toc_item, new String[] {ITEM_IMAGE, ITEM_TITLE, ITEM_SUBTITLE}, new int[] {R.id.Image, R.id.Title, R.id.SubTitle});
		setListAdapter(dataAdapter);	
		
		getListView().setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			 public void onItemClick(AdapterView<?> parent, View view,
				        int position, long id) 
			{
				final Class<? extends Activity> activityToLaunch = activityMapping.get(position);
				
				if (activityToLaunch != null)
				{
					final Intent launchIntent = new Intent(MainActivity.this, activityToLaunch);
					startActivity(launchIntent);
				}				
			}
		});
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
		MenuItem item = menu.findItem(R.id.action_rate);
		item.setVisible(!PreferenceManager.getDefaultSharedPreferences(this).getBoolean("rate_clicked", false));

//        if (Build.VERSION.SDK_INT >= 14) {
//            MenuItem item = menu.findItem(R.id.menu_item_share);
//            //mShareActionProvider = (ShareActionProvider)  MenuItemCompat.getActionProvider(item);
//            mShareActionProvider = (ShareActionProvider) item.getActionProvider();
//
//            // Create the share Intent
//            String shareText = getText(R.string.share_text).toString();//"https://play.google.com/store/apps/details?id=com.vlvolad.pendulumstudio";
//            Intent myShareIntent = new Intent(Intent.ACTION_SEND);
//            myShareIntent.setType("text/plain");
//            myShareIntent.putExtra(Intent.EXTRA_SUBJECT, getText(R.string.share_subject).toString());//"Pendulum Studio app for Android");
//            myShareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
//            mShareActionProvider.setShareIntent(myShareIntent);
//        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        Intent intentParam;
        switch (item.getItemId()) {
            case R.id.menu_item_share:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getText(R.string.share_subject).toString());
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, getText(R.string.share_text).toString());
                startActivity(Intent.createChooser(sharingIntent, getText(R.string.share_via).toString()));
                return true;
            case R.id.action_settings:
                //showHelp();
                intentParam = new Intent(MainActivity.this, SettingsActivity.class);
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
                //showHelp();
                intentParam = new Intent(MainActivity.this, InformationActivity.class);
                startActivity(intentParam);
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
    }
    
}
