package com.example.larkinmcmahon.geogoals;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * Created by connor on 7/30/15.
 */
public class GoalLocationFragment extends Fragment {
    private MapFragment fragment;
    private Context mContext;
    private GeofenceService mGeofenceService;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private String TAG = "GOAL_LOCATION_FRAGMENT";
    private EditText mTitleText;
    private View mRootView;

    private ServiceConnection mConnection;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mContext = getActivity();

        mRootView = inflater.inflate(R.layout.fragment_goal_location, container, false);

        mTitleText = (EditText) mRootView.findViewById(R.id.title_box);
        mTitleText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                    ((GoalLocation) mContext).getGoal().setTitle(mTitleText.getText().toString());
                }

            }
        });
        mTitleText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
                if(actionId== EditorInfo.IME_ACTION_DONE ||
                        actionId == EditorInfo.IME_ACTION_SEND) {
                    ((GoalLocation) mContext).getGoal().setTitle(mTitleText.getText().toString());
                    return true;

                }
                return false;
            }
        });


        return mRootView;

    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        FragmentManager manager = getFragmentManager();
        fragment = (MapFragment) manager.findFragmentById(R.id.map);
        if(fragment == null) {
            fragment = MapFragment.newInstance();
            manager.beginTransaction().replace(R.id.map, fragment).commit();
        }


        Intent intent = new Intent(mContext, GeofenceService.class);
        mMap = fragment.getMap();

        mConnection = new ServiceConnection() {
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
                        .build();

                CameraUpdate zoomIn = CameraUpdateFactory.newCameraPosition(camPos);
                mMap.animateCamera(zoomIn);
                setUpMap();


            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.i(TAG, "Disconnected from service.");
            }
        };
        mContext.startService(intent);
        mContext.bindService(intent, mConnection, 0);


    }

    @Override
    public void onResume() {
        super.onResume();
        if(mMap ==null){
            mMap = fragment.getMap();
            setUpMap();
        }
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        mContext.unbindService(mConnection);
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
                Goal goal = ((GoalLocation) mContext).getGoal();
                goal.addGeofence(coords, 50);
                mMap.addMarker(new MarkerOptions()
                        .position(coords)
                        .draggable(true));
            }
        });

    }
}
