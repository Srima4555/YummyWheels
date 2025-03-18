package com.managerestaurant;

import android.Manifest;
import android.app.Dialog;
import android.app.SearchManager;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MyRestauActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        View.OnClickListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, TimePickerDialog.OnTimeSetListener {
    private boolean isUpdate;
    private static final CharSequence CANNOTBEEMPTY = "This field cannot be empty";
    private ScrollView sView;
    private CheckBox[] cuisineCheckboxes;
    // //Fid,Name,Cat,Desc,Price,Status,img
    private ArrayList<String> list;
    private Calendar cal;
    Calendar fCal, tCal;
    private RestaurantPojo restaurantPojo;
    private Button btnSubmitOrUpdate;
    private static final int GALLERY_PERMISSIONS_REQUEST = 0;
    private static final int GALLERY_IMAGE_REQUEST = 1;
    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int CAMERA_IMAGE_REQUEST = 3;
    public static final String FILE_NAME = "temp.jpg";
    private static final int MAX_DIMENSION = 200;
    private String encode;
    private Switch switchRestau;
    private static final String YES = "Yes";
    private String checked = YES;
    private ImageView iViewUploaded;
    private EditText statusET, rNameET, costET, timeForDelivET, minOrderET, addET, cityET, pincodeET, contactET, typeET;
    private TextView cuisineTV;
    private ImageView latLongMarker;
    private Dialog cuisineCB;
    private int[] cuisineIDs;
    private ArrayList<Boolean> cuisineSelectedList;
    private String latlong;
    private Dialog dialogLatLong;
    private static final String TAG = "AddOrUpdate";
    private static final int ZOOM_DEFAULT_LEVEL_ = 18;
    private static double RADIUS = 30;
    private Button btnAddFence;
    private RelativeLayout relativeLayout;
    private GoogleMap gMaps;
    private SearchView searchView;
    private SearchManager searchMgr;
    private boolean edit;
    private Double longi;
    private GoogleApiClient googleApiClient;
    private FusedLocationProviderClient fusedLocProvClient;
    private boolean isConnected;
    private Double lati;
    private LocationRequest locationRequest;
    private static final int RCODE_LOC = 10;
    private LatLng latLng;
    private static final long INTERVAL_ = 60_000;
    private static final long FAST_INTERVAL_ = 60_000;
    private static final int RCODE_RES_CURRENTLOC = 100;
    private SupportMapFragment supportMapFrag;
    private LinearLayout llCuisine;
    private String cuisineString;
    private String[] cuisineAlready;
    private Dialog dialogLoader;
    private boolean firstChanged;
    private int hrFrom, hrTo;
    private int minFrom, minTo;
    private TimePickerDialog pickerDialog;
    private TimePickerDialog pickerStartDialog;
    private boolean to, selected;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restau_detail);
        Toolbar toolbar = findViewById(R.id.toolbar_add_food);
        toolbar.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_material);
        setSupportActionBar(toolbar);
        initUI();
        initDataObj();
        makeLatLongDialog();
        submit();
    }

    @Override
    protected void onResume() {
        permissionInit();
        super.onResume();
    }

    private void initUI() {
        //getIntent().
        //if no pojo in intent then no poppulating data
        //title aacordingly
        isUpdate = true;
        edit = true;
        sView = findViewById(R.id.sv_add_doc);
        statusET = findViewById(R.id.et_status);
        rNameET = findViewById(R.id.et_rname_);
        typeET = findViewById(R.id.et_rtype);
        costET = findViewById(R.id.et_cost);
        cuisineTV = findViewById(R.id.tv_cuisine);
        cuisineTV.setOnClickListener(this);
        timeForDelivET = findViewById(R.id.et_time_deliv);
        minOrderET = findViewById(R.id.et_min_order);
        addET = findViewById(R.id.et_add_restau);
        cityET = findViewById(R.id.et_rcity);
        pincodeET = findViewById(R.id.et_rpincode);
        contactET = findViewById(R.id.et_rcontact);
        latLongMarker = findViewById(R.id.iv_marker);
        llCuisine = findViewById(R.id.ll_cuisine);
        latLongMarker.setOnClickListener(this);
        cuisineSelectedList = new ArrayList<>(14);
        for (int i = 0; i < 14; i++) {
            cuisineSelectedList.add(i, false);
        }
        btnSubmitOrUpdate = findViewById(R.id.btn_update_orsubmit);
        btnSubmitOrUpdate.setOnClickListener(this);
        cuisineCheckboxes = new CheckBox[14];
        cuisineIDs = new int[14];
        cuisineIDs[0] = R.id.cb_0;
        cuisineIDs[1] = R.id.cb_1;
        cuisineIDs[2] = R.id.cb_2;
        cuisineIDs[3] = R.id.cb_3;
        cuisineIDs[4] = R.id.cb_4;
        cuisineIDs[5] = R.id.cb_5;
        cuisineIDs[6] = R.id.cb_6;
        cuisineIDs[7] = R.id.cb_7;
        cuisineIDs[8] = R.id.cb_8;
        cuisineIDs[9] = R.id.cb_9;
        cuisineIDs[10] = R.id.cb_10;
        cuisineIDs[11] = R.id.cb_11;
        cuisineIDs[12] = R.id.cb_12;
        cuisineIDs[13] = R.id.cb_13;

        list = new ArrayList<>();
        list.addAll(Arrays.asList("Afghani", "American", "BBQ", "British", "Cafe", "Chinese", "European", "French",
                "Indian", "Italian", "Korean", "Mexican", "Others", "Spanish"));
        for (int i = 0; i < list.size(); i++) {
            cuisineCheckboxes[i] = findViewById(cuisineIDs[i]);
            //cuisineCheckboxes[i].setOnCheckedChangeListener(this);
        }
        iViewUploaded = findViewById(R.id.iv_upload);
        apiClient();

    }

    private void hideKeyBoard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void startCamera() {
        if (PermissionUtils.requestPermission(
                this,
                CAMERA_PERMISSIONS_REQUEST,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getCameraFile());
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, CAMERA_IMAGE_REQUEST);
        }
    }

    public void onImageUploadClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MyRestauActivity.this);
        builder
                .setMessage(R.string.dialog_select_prompt)
                .setPositiveButton(R.string.dialog_select_gallery, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MyRestauActivity.this.startGalleryChooser();
                    }
                })
                .setNegativeButton(R.string.dialog_select_camera, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MyRestauActivity.this.startCamera();
                    }
                });
        builder.create().show();
    }

    public void startGalleryChooser() {
        if (PermissionUtils.requestPermission(this, GALLERY_PERMISSIONS_REQUEST, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select a photo"),
                    GALLERY_IMAGE_REQUEST);
        }
    }

    private void requestPerm() {
        ActivityCompat.requestPermissions(MyRestauActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION
                , Manifest.permission.ACCESS_COARSE_LOCATION}, RCODE_LOC);
    }

    private void permissionInit() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //dynamic location permission
            if (ContextCompat.checkSelfPermission(MyRestauActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(MyRestauActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                //ask for permission since not granted

                requestPerm();
                Log.d(TAG, "onCreate: shouldShowReq else");

            } else {
                Log.d(TAG, "onCreate> persmissions granted");
                requestLocUpdates();
            }
        } else {
            //call directly for lessthan Marsh
            requestLocUpdates();
        }
    }

    void setTextt() {
        getSupportActionBar().setTitle(restaurantPojo.getRname() + "(" + restaurantPojo.getType() + ")");
        btnSubmitOrUpdate.setText("update");
        if (restaurantPojo.getStatus().equalsIgnoreCase("open")
                || restaurantPojo.getStatus().equalsIgnoreCase(YES)) {
            Log.i(TAG, "setTextt: " + restaurantPojo.getStatus());
            switchRestau.setChecked(true);
            checked = YES;
            statusET.setText("OPEN");
        } else {
            statusET.setText("CLOSED");
            checked = "No";
            switchRestau.setChecked(false);
        }
        rNameET.setText(restaurantPojo.getRname());
        typeET.setText(restaurantPojo.getType());
        costET.setText(restaurantPojo.getCost());
        timeForDelivET.setText(restaurantPojo.getTime());
        minOrderET.setText(restaurantPojo.getMinorder());
        latlong = restaurantPojo.getLatlng();
        addET.setText(restaurantPojo.getAddress());
        cityET.setText(restaurantPojo.getCity());
        pincodeET.setText(restaurantPojo.getPincode());
        contactET.setText(restaurantPojo.getContact());
        cuisineAlready = restaurantPojo.getCuisine().split(",");
        for (int j = 0; j < cuisineAlready.length; j++) {
            for (int i = 0; i < list.size(); i++) {
                if (cuisineAlready[j].equalsIgnoreCase(list.get(i))) {
                    cuisineSelectedList.set(i, true);
                    cuisineCheckboxes[i].setChecked(true);
                    break;
                }
            }
        }
        //latlong split and set latLong in update
        if (latlong.length() > 0) {
            String[] latilongi = latlong.split(",");
            latLng = new LatLng(Double.valueOf(latilongi[0]), Double.valueOf(latilongi[1]));
        }
        encode = restaurantPojo.getLogo();
        iViewUploaded.setImageBitmap(UtilConstants.decodeBitmap(encode));
    }

    private void requestLocUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setFastestInterval(FAST_INTERVAL_);
        //dialog asking user to turn loc on
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> pendingResult = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        pendingResult.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        callLocationReq();
                        Log.i(TAG, "onResult: LocationSettingsStatusCodes.SUCCESS");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            //isLocDialogSucces = true;
                            status.startResolutionForResult(MyRestauActivity.this, RCODE_RES_CURRENTLOC);
                            Log.i(TAG, "onResult: LocationSettingsStatusCodes.RR");
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                            Toast.makeText(MyRestauActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        });
    }

    private void callLocationReq() {
        if (isConnected) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            fusedLocProvClient.requestLocationUpdates(locationRequest, mLocationCallback, null);
        }
    }

    void makeLatLongDialog() {
        dialogLatLong = new Dialog(this, R.style.AppTheme_NoActionBar);
        dialogLatLong.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#8D000000")));
        final View view = getLayoutInflater().inflate(R.layout.dialog_add_latlong, null);
        btnAddFence = view.findViewById(R.id.btn_addorupdate_fence);
        btnAddFence.setOnClickListener(this);
        relativeLayout = view.findViewById(R.id.sv_add_update);
        mapInit();
        searchView = view.findViewById(R.id.searchview);
        searchMgr = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchMgr.getSearchableInfo(getComponentName()));
        //searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //String query = searchView.getQuery().toString();
                String query = s;
                if (query == null || query.isEmpty()) return false;
                Log.i(TAG, "onClick: query input" + query);
                Geocoder geocoder = new Geocoder(MyRestauActivity.this, Locale.ENGLISH);
                List<Address> address;
                try {
                    address = (List<Address>) geocoder.getFromLocationName(query, 1);
                    if (address != null && address.size() > 0) {
                        setMap(new LatLng(address.get(0).getLatitude(), address.get(0).getLongitude()));
                        lati = address.get(0).getLatitude();
                        longi = address.get(0).getLongitude();
                        Log.i(TAG, "onClick: " + address.get(0).getLocality());
                    } else
                        Snackbar.make(relativeLayout, "Location not found", Snackbar.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    if (e.getMessage().equals("grpc failed"))
                        Snackbar.make(relativeLayout, "Make sure you have active internet connection", Snackbar.LENGTH_LONG).show();
                }
                hideKeyBoard(searchView);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        dialogLatLong.setContentView(view);
    }

    public File getCameraFile() {
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(dir, FILE_NAME);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            if (data.getData() != null) {
                setImg(data.getData());
            }
        } else if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getCameraFile());
            if (photoUri != null) {
                setImg(photoUri);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_PERMISSIONS_REQUEST:
                if (PermissionUtils.permissionGranted(requestCode, CAMERA_PERMISSIONS_REQUEST, grantResults)) {
                    startCamera();
                }
                break;
            case GALLERY_PERMISSIONS_REQUEST:
                if (PermissionUtils.permissionGranted(requestCode, GALLERY_PERMISSIONS_REQUEST, grantResults)) {
                    startGalleryChooser();
                }
                break;
        }
    }


    public void setImg(Uri uri) {
        if (uri != null) {
            try {
                // scale the image to save on bandwidth
                Bitmap bitmap =
                        scaleBitmapDown(
                                MediaStore.Images.Media.getBitmap(getContentResolver(), uri),
                                MAX_DIMENSION);
                encode = null;
                iViewUploaded.setImageBitmap(bitmap);
                encode = UtilConstants.encodeBitmap(bitmap);
            } catch (IOException e) {
                Log.d("TAG", "Image picking failed because " + e.getMessage());
                Toast.makeText(this, "Selecting image failed", Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d("TAG", "Image picker gave us a null image.");
            Toast.makeText(this, "Error selecting an image  ", Toast.LENGTH_LONG).show();
        }
    }

    private Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_switch_myrestau, menu);
        MenuItem mi = menu.findItem(R.id.mi_switch_status);
        mi.setActionView(R.layout.switch_nearby);
        switchRestau = (Switch) mi.getActionView();
        switchRestau.setVisibility(View.VISIBLE);
        switchRestau.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checked = YES;
                    firstChanged = true;
                } else {
                    checked = "No";
                    firstChanged = true;
                }
                if (firstChanged) { //whilesetting once for setdata of existing restau
                    changeStatus();
                }
            }
        });
        //START WITH DELIVERY ASSIGNING
        return true;
    }

    private void changeStatus() {
        ChangeStatusTask task = new ChangeStatusTask();
        task.execute(UserPref.getRid(MyRestauActivity.this), checked);
        Log.i(TAG, "changeStatus: " + checked);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void update() {
        cuisineString = "";
        boolean found = false;
        for (int i = 0; i < cuisineSelectedList.size(); i++) {
            if (cuisineSelectedList.get(i)) {
                if (!found) {
                    cuisineString = list.get(i);
                    found = true;
                } else
                    cuisineString = cuisineString + "," + list.get(i);
            }
        }
        if (rNameET.getText().toString().isEmpty()) {
            rNameET.setError(CANNOTBEEMPTY);
        } else if (typeET.getText().toString().isEmpty()) {
            typeET.setError(CANNOTBEEMPTY);
        } else if (statusET.getText().toString().isEmpty()) {
            statusET.setError(CANNOTBEEMPTY);
        } else if (costET.getText().toString().isEmpty()) {
            costET.setError(CANNOTBEEMPTY);
        } else if (timeForDelivET.getText().toString().isEmpty()) {
            timeForDelivET.setError(CANNOTBEEMPTY);
        } else if (cuisineString.isEmpty()) {
            Snackbar.make(sView, "Please select the cuisine", Snackbar.LENGTH_SHORT).show();
        } else if (encode == null) {
            Snackbar.make(sView, "Please select image for this restaurant", Snackbar.LENGTH_SHORT).show();
        } else {
            UpdateTask task = new UpdateTask();
            //city, string pincode, string cuisine, string latlng, string time, string cost, string logo, string minorder, string type)
            task.execute(UserPref.getRid(this),
                    rNameET.getText().toString(), contactET.getText().toString(), addET.getText().toString(), cityET.getText().toString(),
                    pincodeET.getText().toString(),
                    cuisineString, latlong, timeForDelivET.getText().toString(),
                    costET.getText().toString(), encode,
                    minOrderET.getText().toString(), typeET.getText().toString());
            Log.i(TAG, "update: " + rNameET.getText().toString() + contactET.getText().toString() + addET.getText().toString() + pincodeET.getText().toString() +
                    cuisineString + latlong + timeForDelivET.getText().toString() + costET.getText().toString() + encode +
                    minOrderET.getText().toString() + typeET.getText().toString());
            Log.i("TAG", "update: " + switchRestau.isChecked());
        }
    }

    private void submit() {
        GetRestauTask task = new GetRestauTask();
        // name, string cont, string add, string city, string pincode, string cuisine,string latlng, string time
        // ,string cost, string logo, string minorder,string type,string status,string mname,string memail,string mcont)
        task.execute();
        Log.i(TAG, "submit: " + UserPref.getRid(MyRestauActivity.this));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.i("TAG", "onItemSelected: " + position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void onFileSelectClicked(View view) {
        onImageUploadClick(view);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.btn_update_orsubmit == id) {
            if (btnSubmitOrUpdate.getText().toString().equalsIgnoreCase("update")) {
                update();
            }
        } else if (R.id.iv_marker == id) {
            openMaps();
        } else if (R.id.tv_cuisine == id) {
            openCheckBoxView();
        } else if (R.id.btn_addorupdate_fence == id) {
            if (latLng != null) {
                setAddress(latLng);
                latlong = String.valueOf(latLng.latitude) + "," + String.valueOf(latLng.longitude);
            }
            if (dialogLatLong.isShowing())
                dialogLatLong.dismiss();
        }
    }

    private void openCheckBoxView() {
        if (llCuisine.getVisibility() == View.GONE) {
            llCuisine.setVisibility(View.VISIBLE);
            cuisineTV.setText("Select a cuisine");
        } else {
            llCuisine.setVisibility(View.GONE);
            cuisineTV.setText("Tap to select a cuisine");
        }
    }

    private void openMaps() {
        if (isUpdate) latlong = restaurantPojo.getLatlng();
        if (latLng != null)
            latlong = String.valueOf(latLng.latitude) + "," + String.valueOf(latLng.longitude);
        dialogLatLong.show();
    }

    public void onCheckboxClicked(View view) {
        int id = view.getId();
        if (id == R.id.cb_0) {
            if (cuisineSelectedList.get(0)) {
                cuisineSelectedList.set(0, false);
            } else
                cuisineSelectedList.set(0, true);
        } else if (id == R.id.cb_1) {
            if (cuisineSelectedList.get(1)) {
                cuisineSelectedList.set(1, false);
            } else
                cuisineSelectedList.set(1, true);
        } else if (id == R.id.cb_2) {
            if (cuisineSelectedList.get(2)) {
                cuisineSelectedList.set(2, false);
            } else
                cuisineSelectedList.set(2, true);
        } else if (id == R.id.cb_3) {
            if (cuisineSelectedList.get(3)) {
                cuisineSelectedList.set(3, false);
            } else
                cuisineSelectedList.set(3, true);
        } else if (id == R.id.cb_4) {
            if (cuisineSelectedList.get(4)) {
                cuisineSelectedList.set(4, false);
            } else
                cuisineSelectedList.set(4, true);
        } else if (id == R.id.cb_5) {
            if (cuisineSelectedList.get(5)) {
                cuisineSelectedList.set(5, false);
            } else
                cuisineSelectedList.set(5, true);
        } else if (id == R.id.cb_6) {
            if (cuisineSelectedList.get(6)) {
                cuisineSelectedList.set(6, false);
            } else
                cuisineSelectedList.set(6, true);
        } else if (id == R.id.cb_7) {
            if (cuisineSelectedList.get(7)) {
                cuisineSelectedList.set(7, false);
            } else
                cuisineSelectedList.set(7, true);
        } else if (id == R.id.cb_8) {
            if (cuisineSelectedList.get(8)) {
                cuisineSelectedList.set(8, false);
            } else
                cuisineSelectedList.set(8, true);
        } else if (id == R.id.cb_9) {
            if (cuisineSelectedList.get(9)) {
                cuisineSelectedList.set(9, false);
            } else
                cuisineSelectedList.set(9, true);
        } else if (id == R.id.cb_10) {
            if (cuisineSelectedList.get(10)) {
                cuisineSelectedList.set(10, false);
            } else
                cuisineSelectedList.set(10, true);
        } else if (id == R.id.cb_11) {
            if (cuisineSelectedList.get(11)) {
                cuisineSelectedList.set(11, false);
            } else
                cuisineSelectedList.set(11, true);
        } else if (id == R.id.cb_12) {
            if (cuisineSelectedList.get(12)) {
                cuisineSelectedList.set(12, false);
            } else
                cuisineSelectedList.set(12, true);
        } else if (id == R.id.cb_13) {
            if (cuisineSelectedList.get(13)) {
                cuisineSelectedList.set(13, false);
            } else
                cuisineSelectedList.set(13, true);
        }
    }

    private void stopAnimation() {
        if (dialogLoader.isShowing())
            dialogLoader.cancel();
    }

    private void startAnimation() {
        dialogLoader = new Dialog(MyRestauActivity.this, R.style.AppTheme_NoActionBar);
        dialogLoader.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#8D000000")));
        final View view = getLayoutInflater().inflate(R.layout.loading, null);
        LottieAnimationView animationView = view.findViewById(R.id.lottie);
        animationView.playAnimation();
        dialogLoader.setContentView(view);
        dialogLoader.setCancelable(false);
        dialogLoader.show();
    }

    private class GetRestauTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            String result;
            RestAPI restAPI = new RestAPI();
            try {
                JSONObject jsonObject = restAPI.URestdetails(UserPref.getRid(MyRestauActivity.this));
                JSONParse jsonParser = new JSONParse();
                result = jsonParser.parse(jsonObject);
            } catch (Exception e) {
                result = e.getMessage();
            }
            return result;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startAnimation();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            stopAnimation();
            Log.d("reply submit", s);
            if (s.contains("Unable to resolve host")) {
                AlertDialog.Builder ad = new AlertDialog.Builder(MyRestauActivity.this);
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
                    Log.d("reply", ans);
                    if (ans.compareTo("already") == 0) {
                        Snackbar.make(sView, "This Restaurant already exists", Snackbar.LENGTH_SHORT).show();
                    } else if (ans.compareTo("email") == 0) {
                        Toast.makeText(MyRestauActivity.this, "Manager's Email already exists", Toast.LENGTH_SHORT).show();
                    } else if (ans.compareTo("ok") == 0) {
                        JSONArray array = json.getJSONArray("Data");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject jsonObject = array.getJSONObject(i);
                            restaurantPojo = new RestaurantPojo(jsonObject.getString("data0"), jsonObject.getString("data1"), jsonObject.getString("data2"),
                                    jsonObject.getString("data3"), jsonObject.getString("data4"), jsonObject.getString("data5"),
                                    jsonObject.getString("data6"),
                                    jsonObject.getString("data7"),
                                    jsonObject.getString("data8"),
                                    jsonObject.getString("data9"),
                                    jsonObject.getString("data10"),
                                    jsonObject.getString("data11"),
                                    jsonObject.getString("data12"),
                                    jsonObject.getString("data13"));
                        }
                        setTextt();
                    } else if (ans.compareTo("error") == 0) {
                        String error = json.getString("Data");
                        Snackbar.make(sView, error, Snackbar.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(MyRestauActivity.this, "Error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d(UtilConstants.TAG, e.getMessage());
                }
            }
        }
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //if add, disable update and del
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private class ChangeStatusTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result;
            RestAPI restAPI = new RestAPI();
            try {
                JSONObject jsonObject = restAPI.changeRestStatus(strings[0], strings[1]);
                JSONParse jsonParser = new JSONParse();
                result = jsonParser.parse(jsonObject);
            } catch (Exception e) {
                result = e.getMessage();
            }
            return result;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startAnimation();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            stopAnimation();
            Log.d("reply submit", s);
            if (s.contains("Unable to resolve host")) {
                AlertDialog.Builder ad = new AlertDialog.Builder(MyRestauActivity.this);
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
                    Log.d("reply", ans);
                    if (ans.compareTo("true") == 0) {
                        if (checked.equalsIgnoreCase("yes")) {
                            statusET.setText("OPEN");
                            Snackbar.make(sView, "Restaurant is OPEN", Snackbar.LENGTH_LONG).show();
                        } else {
                            statusET.setText("CLOSED");
                            Snackbar.make(sView, "Note : Restaurant is CLOSED", Snackbar.LENGTH_INDEFINITE).show();
                        }

                    } else if (ans.compareTo("error") == 0) {
                        String error = json.getString("Data");
                        Snackbar.make(sView, error, Snackbar.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(MyRestauActivity.this, "Error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d(UtilConstants.TAG, e.getMessage());
                }
            }
        }
    }

    private class UpdateTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result;
            RestAPI restAPI = new RestAPI();
            try {
                JSONObject jsonObject = restAPI.updateRestaurantDetails(strings[0], strings[1], strings[2], strings[3], strings[4], strings[5], strings[6]
                        , strings[7], strings[8], strings[9]
                        , strings[10], strings[11], strings[12]);
                JSONParse jsonParser = new JSONParse();
                result = jsonParser.parse(jsonObject);
            } catch (Exception e) {
                result = e.getMessage();
            }
            return result;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startAnimation();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            stopAnimation();
            Log.d("reply update", s);
            if (s.contains("Unable to resolve host")) {
                AlertDialog.Builder ad = new AlertDialog.Builder(MyRestauActivity.this);
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
                    Log.d("reply", ans);
                    if (ans.compareTo("already") == 0) {
                        Snackbar.make(sView, "This restaurant already exists", Snackbar.LENGTH_SHORT).show();
                    } else if (ans.compareTo("true") == 0) {
                        Toast.makeText(MyRestauActivity.this, "Restaurant updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else if (ans.compareTo("error") == 0) {
                        String error = json.getString("Data");
                        Snackbar.make(sView, error, Snackbar.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(MyRestauActivity.this, "Error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d(UtilConstants.TAG, e.getMessage());
                }
            }
        }
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
        if (latLng != null && gMaps != null) {
            Log.d(TAG, "onMapReady: " + gMaps + "," + latLng);
            if (restaurantPojo.getLatlng() != null) {
                String[] ltlng = restaurantPojo.getLatlng().split(",");
                latLng = new LatLng(Double.parseDouble(ltlng[0]), Double.parseDouble(ltlng[1]));
            }
            setMap(latLng);
            gMaps.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM_DEFAULT_LEVEL_));
        }

        gMaps.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Log.d(TAG, "onMapLongClick: " + latLng.latitude + " , " + latLng.longitude);
                setMap(latLng);
            }
        });
        gMaps.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                LatLng latLng = marker.getPosition();
                Log.d(TAG, "onMarkerDragEnd: " + latLng.latitude + " , " + latLng.longitude);
                setMap(latLng);
            }
        });
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

        Log.d(TAG, "onMapLongClick: " + latLng.latitude + " , " + latLng.longitude);
        //set circle:raduis:testing 10m, latlon in DB
        if (edit) {
            gMaps.addCircle(new CircleOptions()
                    .center(latLng)
                    .radius(RADIUS)   //10metres
                    .fillColor(Color.LTGRAY)
                    .strokeWidth(2).strokeColor(Color.RED)
            );
        }
        gMaps.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM_DEFAULT_LEVEL_));
        //add to latLng
        this.latLng = latLng;
        if (latLng != null) {
            fusedLocProvClient.removeLocationUpdates(mLocationCallback);
        }
    }

    void setAddress(LatLng latLng) {
        Geocoder geocoder = new Geocoder(MyRestauActivity.this, Locale.ENGLISH);
        List<Address> address;
        try {
            address = (List<Address>) geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (address != null && address.size() > 0) {
                //setMap(new LatLng(address.get(0).getLatitude(), address.get(0).getLongitude()));
                if (!address.get(0).getAddressLine(0).isEmpty())
                    addET.setText(address.get(0).getAddressLine(0));
                else addET.setText(address.get(0).getLocality());
                Log.i(TAG, "onClick: " + address.get(0).getLocality());
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (e.getMessage().equals("grpc failed"))
                Snackbar.make(relativeLayout, "Make sure you have active internet connection", Snackbar.LENGTH_LONG).show();
        }
    }

    private void apiClient() {
        fusedLocProvClient = LocationServices.getFusedLocationProviderClient(this);
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();
        locationRequest = new LocationRequest();
        locationRequest.setInterval(INTERVAL_);
        locationRequest.setFastestInterval(FAST_INTERVAL_);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
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

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest
                Location location = locationList.get(locationList.size() - 1);
                lati = location.getLatitude();
                longi = location.getLongitude();
                Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());

                //Place current location marker
                latLng = new LatLng(location.getLatitude(), location.getLongitude());
                Log.d(TAG, "onLocationResult: " + latLng.latitude + " , " + latLng.longitude);
                if (isConnected && gMaps != null) {
                    //setMap on click only for mock mode OFF
                    Log.d(TAG, "onLocationResult: mock mode OFF" + isConnected);
                    setMap(latLng); //keep this one as current loc
                }
            }
        }
    };


    private void initDataObj() {
        cal = Calendar.getInstance();
        fCal = Calendar.getInstance();
        tCal = Calendar.getInstance();
        tCal.add(Calendar.HOUR_OF_DAY, 1);
        tCal.add(Calendar.MINUTE, 1);
        minFrom = minTo = cal.get(Calendar.MINUTE);
        hrTo = hrFrom = cal.get(Calendar.HOUR_OF_DAY);
    }

    private void toTimePicker() {
        minTo = tCal.get(Calendar.MINUTE);
        hrTo = tCal.get(Calendar.HOUR_OF_DAY);
        //if (selected) {
        pickerDialog = new TimePickerDialog(this, this, hrTo, minTo, true);
        pickerDialog.show();
        to = true;
        //}
        /*else {
            Snackbar.make(sView, "Select starting time first", Snackbar.LENGTH_SHORT).show();
        }*/
    }

    private void fromTimePicker() {
        minFrom = fCal.get(Calendar.MINUTE);
        hrFrom = fCal.get(Calendar.HOUR_OF_DAY);
        pickerStartDialog = new TimePickerDialog(this, this, hrFrom, minFrom, true);
        pickerStartDialog.show();
        to = false;
    }

    public void onFromClicked(View view) {
        int id = view.getId();
        if (id == R.id.btn_from) {
            fromTimePicker();
            Log.i(TAG, "onFromClicked: ");
        } else if (id == R.id.btn_to) {
            toTimePicker();
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        cal.set(Calendar.HOUR_OF_DAY,hourOfDay);

        if (to) {
            //if (cal.get(Calendar.HOUR_OF_DAY) >= fCal.get(Calendar.HOUR_OF_DAY)/* && cal.get(Calendar.MINUTE) > fCal.get(Calendar.MINUTE)*/) {
                tCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                hrTo = hourOfDay;
                minTo = minute;
                timeForDelivET.setText(hrFrom + ":" + minFrom + "-" + hrTo + ":" + minTo);
            //}
        } else {
            //if (cal.get(Calendar.HOUR_OF_DAY) <= tCal.get(Calendar.HOUR_OF_DAY )/* && cal.get(Calendar.MINUTE) < fCal.get(Calendar.MINUTE)*/) {
                fCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                selected = true;
                hrFrom = hourOfDay;
                minFrom = minute;
                timeForDelivET.setText(hrFrom + ":" + minFrom + "-");
            //}
        }
    }
}
