package com.deliveryperson;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import org.json.JSONArray;
import org.json.JSONObject;

public class OrderActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private String rName = "Restaurant";
    private RestaurantPojo restaurantPojo;
    private Fragment frag;
    private FragmentManager fm;
    private Intent service;
    private boolean permissionGranted;
    private static final int RCODE_LOC = 10;
    private static final int RCODE_RES_CURRENTLOC = 100;
    private static final long FAST_INTERVAL_ = 60_000;
    private Dialog dialogLoader;
    private DeliveryPersonPojo deliveryPersonPojo;
    private GoogleApiClient googleApiClient;
    private boolean isConnected;
    private Dialog dialogLatLong;
    private boolean avail;
    private Menu menU;
    private TextView statusCurrentTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        apiClient();
        if (getIntent().getBooleanExtra(UtilConstants.LOGGEDIN, false)) {
            new GetStatusTask().execute();
        } else {
            if (getIntent().getBooleanExtra(UtilConstants.STATUS_SELECTED, false)) {
                avail = true;
                if (service != null)
                    stopService(service);
                service = new Intent(OrderActivity.this, LocationUpdateService.class);
                //permissionGrantedcheck assignment
                if (permissionGranted) {
                    startService(service);
                }
            } else {
                if (service != null)
                    stopService(service);
                avail = false;
            }
        }
        getSupportActionBar().setTitle("Orders");
        frag = new com.deliveryperson.Bookings();
        fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.frame, frag).commit();
    }

    private void apiClient() {
        googleApiClient = new GoogleApiClient.Builder(OrderActivity.this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();
    }

    private void requestPerm() {
        ActivityCompat.requestPermissions(OrderActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION
                , Manifest.permission.ACCESS_COARSE_LOCATION}, RCODE_LOC);
    }

    private void permissionInit() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //dynamic location permission
            if (ContextCompat.checkSelfPermission(OrderActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(OrderActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                //ask for permission since not granted
                permissionGranted = false;
                requestPerm();
                Log.d("TAG", "onCreate: shouldShowReq else");

            } else {
                Log.d("TAG", "onCreate> persmissions granted");
                requestLocUpdates();
                permissionGranted = true;
            }
        } else {
            //call directly for lessthan Marsh
            requestLocUpdates();
        }
    }

    private void requestLocUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setFastestInterval(FAST_INTERVAL_);
        //dialog asking user to turn loc off
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> pendingResult = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        pendingResult.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        permissionGranted = true;
                        Log.i("TAG", "onResult: " + permissionGranted);
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            //isLocDialogSucces = true;
                            status.startResolutionForResult(OrderActivity.this, RCODE_RES_CURRENTLOC);
                            Log.i("TAG", "onResult: LocationSettingsStatusCodes.RR");
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                            Toast.makeText(OrderActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        permissionInit();
    }

    private class GetStatusTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String result;
            RestAPI restAPI = new RestAPI();
            try {
                JSONObject jsonObject = restAPI.Dgetprofile(UserPref.getId(OrderActivity.this));
                JSONParse jsonParser = new JSONParse();
                result = jsonParser.parse(jsonObject);
            } catch (Exception e) {
                result = e.getMessage();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("reply getRestau ", s);
            if (s.contains("Unable to resolve host")) {
                AlertDialog.Builder ad = new AlertDialog.Builder(OrderActivity.this);
                ad.setTitle("Unable to Connect!");
                ad.setMessage("Check your Internet Connection,Unable to connect the Server");
                ad.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                ad.show();
            } else {
                try {
                    JSONObject json = new JSONObject(s);
                    String ans = json.getString("status");
                    Log.d("reply::", ans);
                    if (ans.compareTo("no") == 0) {

                    } else if (ans.compareTo("ok") == 0) {
                        JSONArray array = json.getJSONArray("Data");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject jsonObject = array.getJSONObject(i);
                            if (jsonObject.getString("data7").equalsIgnoreCase("yes")) {
                                avail = true;
                                if (service != null)
                                    stopService(service);
                                service = new Intent(OrderActivity.this, LocationUpdateService.class);
                                startService(service);
                            } else {
                                avail = false;
                                if (service != null)
                                    stopService(service);
                            }
                            if (menU != null)
                                if (avail) {
                                    menU.findItem(R.id.mi_status).setIcon(R.drawable.status_on);
                                    getSupportActionBar().setSubtitle("My Status: Available");
                                    UserPref.setValue(OrderActivity.this, "status", "yes");
                                } else {
                                    menU.findItem(R.id.mi_status).setIcon(R.drawable.status_off);
                                    UserPref.setValue(OrderActivity.this, "status", "no");
                                    getSupportActionBar().setSubtitle("My Status: Not Available");
                                }
                        }
                    } else if (ans.compareTo("error") == 0) {
                        String error = json.getString("Data");
                        Toast.makeText(OrderActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(OrderActivity.this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_order, menu);
        menU = menu;
        if (getIntent().getBooleanExtra(UtilConstants.STATUS_SELECTED, false)) {
            menu.findItem(R.id.mi_status).setIcon(R.drawable.status_on);
        } else {
            menu.findItem(R.id.mi_status).setIcon(R.drawable.status_off);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.mi_profile) {
            profile();
            return true;
        } else if (id == R.id.mi_logout) {
            logout();
            return true;
        } else if (id == R.id.mi_status) {
            showDialogStatus();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDialogStatus() {
        dialogLatLong = new Dialog(this);
        final View view = getLayoutInflater().inflate(R.layout.dialog_status, null);
        statusCurrentTV = view.findViewById(R.id.tv_chnagestatus);
        if (avail) statusCurrentTV.setText("Current status : I'm AVAILABLE for deliveries");
        else statusCurrentTV.setText("Current status : I'm NOT AVAILABLE for deliveries");
        view.findViewById(R.id.iv_status).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //change and dismiss
                String status;
                avail = !avail;
                status = avail ? "Yes" : "No";
                new StatusTask().execute(UserPref.getId(OrderActivity.this), status);
            }
        });
        dialogLatLong.setContentView(view);
        dialogLatLong.show();
    }

    private class StatusTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startAnimation();
        }

        @Override
        protected String doInBackground(String... strings) {
            String result;
            RestAPI restAPI = new RestAPI();
            try {
                JSONObject jsonObject = restAPI.DChangestatus(strings[0], strings[1]);
                JSONParse jsonParser = new JSONParse();
                result = jsonParser.parse(jsonObject);
            } catch (Exception e) {
                result = e.getMessage();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            stopAnimation();
            Log.d("reply", s);
            if (s.contains("Unable to resolve host")) {
                AlertDialog.Builder ad = new AlertDialog.Builder(OrderActivity.this);
                ad.setTitle("Unable to Connect!");
                ad.setMessage("Check your Internet Connection,Unable to connect the Server");
                ad.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                ad.show();
            } else {
                try {
                    JSONObject json = new JSONObject(s);
                    String ans = json.getString("status");
                    Log.d("reply::", ans);
                    if (ans.compareTo("true") == 0) {
                        if (dialogLatLong.isShowing())
                            dialogLatLong.dismiss();
                        if (avail) {
                            menU.findItem(R.id.mi_status).setIcon(R.drawable.status_on);
                            UserPref.setValue(OrderActivity.this, "status", "yes");
                            getSupportActionBar().setSubtitle("My Status: Available");
                        } else {
                            menU.findItem(R.id.mi_status).setIcon(R.drawable.status_off);
                            UserPref.setValue(OrderActivity.this, "status", "no");
                            getSupportActionBar().setSubtitle("My Status: Not Available");
                        }
                    } else if (ans.compareTo("error") == 0) {
                        String error = json.getString("Data");
                        Toast.makeText(OrderActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(OrderActivity.this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void logout() {
        UserPref.setValue(OrderActivity.this, "mid", "");
        startActivity(new Intent(OrderActivity.this, LoginActivity.class));
        finish();
    }

    private void profile() {
        startActivity(new Intent(OrderActivity.this, ProfileActivity.class));
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

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
        Toast.makeText(OrderActivity.this, connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }

    private void stopAnimation() {
        if (dialogLoader.isShowing())
            dialogLoader.cancel();
    }

    private void startAnimation() {
        dialogLoader = new Dialog(this, R.style.AppTheme_NoActionBar);
        dialogLoader.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#8D000000")));
        final View view = getLayoutInflater().inflate(R.layout.loading, null);
        LottieAnimationView animationView = view.findViewById(R.id.lottie);
        animationView.playAnimation();
        dialogLoader.setContentView(view);
        dialogLoader.setCancelable(false);
        dialogLoader.show();
    }
}