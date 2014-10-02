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
        context = this;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences.getLong(CURRENT_USER_ID, 0) == 0) {
            startActivity(new Intent(context, LoginActivity.class));
            finish();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

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
                startActivity(new Intent(context, SpotfixRequestActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
