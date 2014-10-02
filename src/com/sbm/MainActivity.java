package com.sbm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.sbm.spotfixrequest.SpotfixRequestActivity;

import static com.sbm.Global.*;

public class MainActivity extends Activity {

    private static String TAG = "MAIN_ACTIVITY";

    private Context context;
    private SharedPreferences preferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        context = this;

        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Log.d(TAG, Long.valueOf(preferences.getLong(CURRENT_USER_ID, 0)).toString());
        Log.d(TAG, preferences.getString(CURRENT_USER_EMAIL, ""));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.action_add_request:
                startActivity(new Intent(this, SpotfixRequestActivity.class));
                return true;
	        default:
	            return super.onOptionsItemSelected(item);
        }
    }

}
