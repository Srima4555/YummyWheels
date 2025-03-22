package com.deliveryperson;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.List;

public class LocationHelper implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private Context context;
    private GoogleApiClient googleApiClient;
    private FusedLocationProviderClient fusedLocProvClient;
    private boolean isConnected;
    private LocationRequest locationRequest;
    private static final long INTERVAL_ = 60_000;
    private static final long FAST_INTERVAL_ = 30_000;
    public String currentLatLong;


    public LocationHelper(Context context) {
        LocationHelper.this.context = context;
        apiClient();
    }

    private static final String TAG = "TAG";


    @SuppressLint("MissingPermission")
    private void apiClient() {
        fusedLocProvClient = LocationServices.getFusedLocationProviderClient(context);
        googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();
        locationRequest = new LocationRequest();
        locationRequest.setInterval(INTERVAL_);
        locationRequest.setFastestInterval(FAST_INTERVAL_);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        fusedLocProvClient.requestLocationUpdates(locationRequest, mLocationCallback, null);
    }


    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest
                Location location = locationList.get(locationList.size() - 1);
                currentLatLong = String.valueOf(location.getLatitude()) + "," + location.getLongitude();
                Log.i("MapsInService", "Location: " + currentLatLong);
            }
        }
    };
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        isConnected = true;
    }
    @Override
    public void onConnectionSuspended(int i) {
        isConnected = false;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(context, connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }

    public String getLtLng() {
        return currentLatLong;
    }
}