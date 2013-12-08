package stream.pimedia.upnp.server;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import stream.pimedia.R;
import stream.pimedia.settings.SettingsActivity;
import stream.pimedia.util.AboutActivity;

/**
 * Control activity for the yaacc upnp server
 * 
 * @author Logan Miller 
 *
 */
public class PiMediaUpnpServerControlActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pimedia_upnp_server_control);
		// initialize buttons
		Button startButton = (Button) findViewById(R.id.startServer);
		startButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PiMediaUpnpServerControlActivity.this.startService(new Intent(getApplicationContext(),
					PiMediaUpnpServerService.class));

			}
		});
		Button stopButton = (Button) findViewById(R.id.stopServer);
		stopButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PiMediaUpnpServerControlActivity.this.stopService(new Intent(getApplicationContext(),
					PiMediaUpnpServerService.class));

			}
		});
		SharedPreferences preferences = PreferenceManager
		.getDefaultSharedPreferences(getApplicationContext());
		boolean receiverActive = preferences.getBoolean(getString(R.string.settings_local_server_receiver_chkbx),false);
		Log.d(getClass().getName(), "receiverActive: " + receiverActive);
		CheckBox receiverCheckBox = (CheckBox)findViewById(R.id.receiverEnabled);		
		receiverCheckBox.setChecked(receiverActive);
		boolean providerActive = preferences.getBoolean(getString(R.string.settings_local_server_provider_chkbx),false);
		Log.d(getClass().getName(), "providerActive: " + providerActive);
		CheckBox providerCheckBox = (CheckBox)findViewById(R.id.providerEnabled);		
		providerCheckBox.setChecked(providerActive);
		

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pimedia_upnp_server_control,
				menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings:
			Intent i = new Intent(this, SettingsActivity.class);
			startActivity(i);
			return true;
		case R.id.yaacc_about:
			AboutActivity.showAbout(this);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}


}
