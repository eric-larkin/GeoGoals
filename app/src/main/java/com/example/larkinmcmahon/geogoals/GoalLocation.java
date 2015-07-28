package com.example.larkinmcmahon.geogoals;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class GoalLocation extends FragmentActivity{

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private String TAG = "GOAL_LOCATION";
    private GeofenceService mGeofenceService;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service){
            Log.i(TAG, "Connected to service.");
            GeofenceService.GeofenceBinder binder = (GeofenceService.GeofenceBinder) service;
            mGeofenceService = binder.getService();

            Location lastLocation = mGeofenceService.getLastLocation();
            LatLng coords = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());

            CameraPosition camPos = new CameraPosition.Builder()
                    .target(coords)
                    .zoom(18)
                    .bearing(lastLocation.getBearing())
                    .tilt(70)
                    .build();

            CameraUpdate zoomIn = CameraUpdateFactory.newCameraPosition(camPos);
            mMap.animateCamera(zoomIn);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "Disconnected from service.");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_goal_location);
        setUpMapIfNeeded();
    }


    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng coords) {
                onGeofenceSelect(coords);
            }
        });


    }

    public void onGeofenceSelect(LatLng coords){
        Intent intent = new Intent();
        intent.putExtra("latitude", coords.latitude);
        intent.putExtra("longitude", coords.longitude);
        intent.putExtra("radius", 50);
        setResult(RESULT_OK, intent);
        finish();
    }
}
