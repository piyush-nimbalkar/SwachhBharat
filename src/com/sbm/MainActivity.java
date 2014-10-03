package com.sbm;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sbm.model.Spotfix;
import com.sbm.repository.SpotfixRepository;
import com.sbm.spotfixrequest.SpotfixRequestActivity;

import java.util.ArrayList;

import static com.sbm.Global.*;

public class MainActivity extends Activity implements DataReceiver {

    private static String TAG = "MAIN_ACTIVITY";

    private final static int DEFAULT_ZOOM_LEVEL = 14;

    private Context context;
    private SharedPreferences preferences;

    private GoogleMap map;

    private Handler locationUpdateHandler = new Handler();
    private NetworkLocationListener listener;
    private LocationManager locationManager;

    private ArrayList<Spotfix> spotfixes = new ArrayList<Spotfix>();

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

        listener = new NetworkLocationListener();
        locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, listener);

        MapFragment mapFragment = ((MapFragment) getFragmentManager().findFragmentById(R.id.spotfixes_map));
        assert mapFragment != null;
        map = mapFragment.getMap();
        map.getUiSettings().setZoomControlsEnabled(false);

        SyncSpotfixesTask task = new SyncSpotfixesTask(context);
        task.delegate = (DataReceiver) context;
        task.execute();
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
        locationManager.removeUpdates(listener);
    }

    @Override
    public void receive(ServerResponse serverResponse) {
        SpotfixRepository repository = new SpotfixRepository(context);
        spotfixes = repository.getSpotfixes();
        map.clear();
        for (Spotfix spotfix : spotfixes)
            map.addMarker(new MarkerOptions()
                    .position(new LatLng(spotfix.getLatitude(), spotfix.getLongitude()))
                    .title(String.valueOf(spotfix.getTitle())));
    }


    class NetworkLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            NetworkLocationRefresher task = new NetworkLocationRefresher(location);
            locationUpdateHandler.post(task);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

    }

    class NetworkLocationRefresher implements Runnable {

        Location location;

        public NetworkLocationRefresher(Location location) {
            this.location = location;
        }

        @Override
        public void run() {
            map.animateCamera(CameraUpdateFactory
                    .newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), DEFAULT_ZOOM_LEVEL));
        }

    }

}
