package com.website.yummywheels;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.website.yummywheels.pojo.UserPojo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class DetailUserActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {
    private boolean isUpdate;
    private ScrollView sView;
    private EditText userNameET, userContactET, addressET, cityET, pinET, emailET, latLongET;
    private ArrayList<String> list;
    private Calendar cal;
    private UserPojo userPojo;
    private SimpleDateFormat format;
    private Dialog dialogLoader;
    private Button btnView;
    private Dialog dialogLatLong;
    private GoogleApiClient googleApiClient;
    private boolean isConnected;
    private SupportMapFragment supportMapFrag;
    private GoogleMap gMaps;
    private LatLng latLng;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_user);
        Toolbar toolbar = findViewById(R.id.toolbar_add_food);
        toolbar.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_material);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("View User Info");
        initUI();
        apiClient();
        makeLatLongDialog();
    }

    private void initUI() {
        //getIntent().
        //if no pojo in intent then no poppulating data
        //title aacordingly
        sView = findViewById(R.id.sv_add_user);
        userNameET = findViewById(R.id.et_username);
        btnView = findViewById(R.id.btn_addorupdate_fence);
        btnView.setOnClickListener(this);
        userContactET = findViewById(R.id.et_usercontact);
        emailET = findViewById(R.id.et_email);
        addressET = findViewById(R.id.et_add);
        cityET = findViewById(R.id.et_city);
        latLongET = findViewById(R.id.et_latlong);
        pinET = findViewById(R.id.et_pincode);
        if ((userPojo = (UserPojo) getIntent().getSerializableExtra(com.website.yummywheels.utils.UtilConstants.USER_KEY)) != null) {
            userNameET.setText(userPojo.getName());
            userNameET.setEnabled(false);
            userContactET.setText(userPojo.getContact());
            userContactET.setEnabled(false);
            emailET.setText(userPojo.getEmail());
            emailET.setEnabled(false);
            addressET.setText(userPojo.getAddress());
            addressET.setEnabled(false);
            cityET.setText(userPojo.getCity());
            cityET.setEnabled(false);
            latLongET.setText(userPojo.getLatlng());
            latLongET.setEnabled(false);
            pinET.setText(userPojo.getPincode());
            pinET.setEnabled(false);
        } else if (getIntent().getStringExtra(com.website.yummywheels.utils.UtilConstants.USER_ID_KEY) != null) {
            ViewUsersTask task = new ViewUsersTask();
            task.execute(getIntent().getStringExtra(com.website.yummywheels.utils.UtilConstants.USER_KEY));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void stopAnimation() {
        if (dialogLoader.isShowing())
            dialogLoader.dismiss();
    }

    private void startAnimation() {
        dialogLoader = new Dialog(DetailUserActivity.this, R.style.AppTheme_NoActionBar);
        dialogLoader.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#8D000000")));
        final View view = getLayoutInflater().inflate(R.layout.custom_dialog_loader, null);
        LottieAnimationView animationView = view.findViewById(R.id.loader);
        animationView.playAnimation();
        dialogLoader.setContentView(view);
        dialogLoader.setCancelable(false);
        dialogLoader.show();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.btn_addorupdate_fence == id) {
            mapInit();
            if (dialogLatLong != null)
                dialogLatLong.show();
        }
    }

    private class ViewUsersTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startAnimation();
        }

        @Override
        protected String doInBackground(String... strings) {
            String result;
            com.website.yummywheels.webservices.RestAPI restAPI = new com.website.yummywheels.webservices.RestAPI();
            try {
                //src - All/city/pincode
                //value - value of src
                JSONObject jsonObject = restAPI.viewUsers("All", "");
                com.website.yummywheels.webservices.JSONParser jsonParser = new com.website.yummywheels.webservices.JSONParser();
                result = jsonParser.parseJSON(jsonObject);
            } catch (Exception e) {
                result = e.getMessage();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("reply", s);
            if (s.contains("Unable to resolve host")) {
                AlertDialog.Builder ad = new AlertDialog.Builder(DetailUserActivity.this);
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
                    if (ans.compareTo("ok") == 0) {
                        JSONArray jsonDataArray = json.getJSONArray("Data");
                        for (int j = 0; j < jsonDataArray.length(); j++) {
                            JSONObject jsonO = jsonDataArray.getJSONObject(j);
                            ////uid,name,contact,email,address,rdate,edate
                            userPojo = new UserPojo(jsonO.getString("data0"), jsonO.getString("data1")
                                    , jsonO.getString("data2"), jsonO.getString("data3"),
                                    jsonO.getString("data4"), jsonO.getString("data5")
                                    , jsonO.getString("data6"),
                                    jsonO.getString("data7"));
                        }
                        userNameET.setText(userPojo.getName());
                        userNameET.setEnabled(false);
                        userContactET.setText(userPojo.getContact());
                        userContactET.setEnabled(false);
                        emailET.setText(userPojo.getEmail());
                        emailET.setEnabled(false);
                        addressET.setText(userPojo.getAddress());
                        addressET.setEnabled(false);
                        cityET.setText(userPojo.getCity());
                        cityET.setEnabled(false);
                        latLongET.setText(userPojo.getLatlng());
                        latLongET.setEnabled(false);
                        pinET.setText(userPojo.getPincode());
                        pinET.setEnabled(false);
                    } else if (ans.compareTo("no") == 0) {
                        /*noRecords.setVisibility(View.VISIBLE);  //visible, gone
                        usersListView.setAdapter(null);*/
                    } else if (ans.compareTo("error") == 0) {
                        String error = json.getString("Data");
                        Toast.makeText(DetailUserActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    stopAnimation();
                    Toast.makeText(DetailUserActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            stopAnimation();
        }
    }

    void makeLatLongDialog() {
        dialogLatLong = new Dialog(this, R.style.AppTheme_NoActionBar);
        //dialogLatLong.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#8D000000")));
        final View view = getLayoutInflater().inflate(R.layout.dialog_add_latlong_plain, null);
        dialogLatLong.setContentView(view);
    }

    private void apiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();
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
        Toast.makeText(this, connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }

    private void mapInit() {
        if (supportMapFrag == null) {
            supportMapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.frag_supportmap);
            supportMapFrag.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        gMaps = googleMap;
        String[] latlong = userPojo.getLatlng().split(",");
        latLng = new LatLng(Double.parseDouble(latlong[0]), Double.parseDouble(latlong[1]));
        setMap(latLng);
        gMaps.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
    }

    private void setMap(LatLng latLng) {
        gMaps.clear();
        //for edit
        MarkerOptions mo = new MarkerOptions();
        mo.position(latLng);
        mo.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mo.draggable(true);
        Marker marker = gMaps.addMarker(mo);
        marker.showInfoWindow();
        Log.d("TAg", "setmap: " + latLng.latitude + " , " + latLng.longitude);
        //add to latLng
        this.latLng = latLng;
        //setAddress(latLng);
    }

}
