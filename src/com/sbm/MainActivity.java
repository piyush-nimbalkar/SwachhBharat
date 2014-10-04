package com.sbm;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sbm.model.Spotfix;
import com.sbm.repository.SpotfixRepository;
import com.sbm.spotfixrequest.SpotfixRequestActivity;
import com.sbm.spotfixrequest.ViewSpotfixActivity;

import static com.sbm.Global.*;

public class MainActivity extends Activity implements DataReceiver, GoogleMap.OnInfoWindowClickListener {

//    private static String TAG = "MAIN_ACTIVITY";

    private final static int DEFAULT_ZOOM_LEVEL = 14;
    private final static int GPS_UPDATE_INTERVAL = 60 * 1000;
    private final static int NETWORK_UPDATE_INTERVAL = 30 * 1000;

    private Context context;
    private SharedPreferences preferences;

    private GoogleMap map;
    HashMap<String, Long> extraMarkerInfo = new HashMap<String, Long>();

    private Handler locationUpdateHandler = new Handler();
    private NetworkLocationListener networkListener;
    private GpsLocationListener gpsListener;
    private LocationManager locationManager;

    private ArrayList<Spotfix> spotfixes = new ArrayList<Spotfix>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences.getLong(CURRENT_USER_ID, 0) == 0) {
            startActivity(new Intent(context, LoginActivity.class));
            finish();
        } else {
            setContentView(R.layout.main);

            networkListener = new NetworkLocationListener();
            gpsListener = new GpsLocationListener();
            locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);

            MapFragment mapFragment = ((MapFragment) getFragmentManager().findFragmentById(R.id.spotfixes_map));
            assert mapFragment != null;
            map = mapFragment.getMap();
            map.getUiSettings().setZoomControlsEnabled(false);

            SyncSpotfixesTask task = new SyncSpotfixesTask(context);
            task.delegate = (DataReceiver) context;
            task.execute();

            map.setOnInfoWindowClickListener(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, NETWORK_UPDATE_INTERVAL, 0, networkListener);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPS_UPDATE_INTERVAL, 0, gpsListener);
        else
            updateBestKnownLocation();
    }

    private void updateBestKnownLocation() {
        showEnableGpsDialog();
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Location gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (gpsLocation != null && networkLocation != null) {
                if (gpsLocation.getTime() > networkLocation.getTime())
                    updateLocationOnGpsDisabled(gpsLocation);
                else
                    updateLocationOnGpsDisabled(networkLocation);
            } else if (gpsLocation != null) {
                updateLocationOnGpsDisabled(gpsLocation);
            } else if (networkLocation != null) {
                updateLocationOnGpsDisabled(networkLocation);
            }
        }
    }

    private void showEnableGpsDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(R.string.gps_dialog_title);
        alertDialog.setMessage(R.string.gps_dialog_message);

        alertDialog.setPositiveButton(R.string.settings_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        alertDialog.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    private void updateLocationOnGpsDisabled(Location location) {
        map.animateCamera(CameraUpdateFactory
                .newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), DEFAULT_ZOOM_LEVEL));
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
        locationManager.removeUpdates(gpsListener);
        locationManager.removeUpdates(networkListener);
    }

    @Override
    public void receive(ServerResponse serverResponse) {
        SpotfixRepository repository = new SpotfixRepository(context);
        spotfixes = repository.getSpotfixes();
        map.clear();
        for (Spotfix spotfix : spotfixes) {
            Marker marker = map.addMarker(new MarkerOptions()
                    .position(new LatLng(spotfix.getLatitude(), spotfix.getLongitude()))
                    .title(spotfix.getTitle()));
            extraMarkerInfo.put(marker.getId(), spotfix.getId());
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent intent = new Intent(context, ViewSpotfixActivity.class);
        intent.putExtra(SPOTFIX_ID, extraMarkerInfo.get(marker.getId()));
        startActivity(intent);
    }

    class NetworkLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            LocationRefresher task = new LocationRefresher(location);
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

    class GpsLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            LocationRefresher task = new LocationRefresher(location);
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

    class LocationRefresher implements Runnable {

        Location location;

        public LocationRefresher(Location location) {
            this.location = location;
        }

        @Override
        public void run() {
            map.animateCamera(CameraUpdateFactory
                    .newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), DEFAULT_ZOOM_LEVEL));
        }

    }

}
