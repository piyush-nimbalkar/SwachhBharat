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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.sbm.spotfixrequest.SpotfixRequestActivity;
import org.json.JSONException;

import static com.sbm.Global.*;

public class MainActivity extends Activity implements DataReceiver {

    private static String TAG = "MAIN_ACTIVITY";

    private Context context;
    private SharedPreferences preferences;

    private GoogleMap map;
    private LocationTracker locationTracker;

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

        locationTracker = new LocationTracker(context);

        MapFragment mapFragment = ((MapFragment) getFragmentManager().findFragmentById(R.id.spotfixes_map));
        assert mapFragment != null;
        map = mapFragment.getMap();
        map.getUiSettings().setZoomControlsEnabled(false);

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(39.255, -76.710), 15));

        SyncSpotfixesTask task = new SyncSpotfixesTask(context);
        task.delegate = (DataReceiver) context;
        task.execute();

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
            case R.id.action_logout:
                SharedPreferences.Editor editor = preferences.edit();
                editor.putLong(CURRENT_USER_ID, 0);
                editor.putString(CURRENT_USER_EMAIL, "");
                editor.commit();
                startActivity(new Intent(context, LoginActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (locationTracker != null)
            locationTracker.removeLocationUpdates();
    }

    @Override
    public void receive(ServerResponse serverResponse) throws JSONException {
        Log.d(TAG, "Response received!");
    }
}
