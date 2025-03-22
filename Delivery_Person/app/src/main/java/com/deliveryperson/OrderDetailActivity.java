package com.deliveryperson;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.maps.model.SquareCap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.ContentValues.TAG;
import static com.deliveryperson.UtilConstants.FROM_CURRENT_TAB;

public class OrderDetailActivity extends AppCompatActivity implements OnMapReadyCallback, AdapterView.OnItemSelectedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private ScrollView sView;
    private TextView oIdTV, statusTV, restauTV, totPriceTV, delAddTV, timeOfOrderTV, dcontactNum, deliveryAssignTV, changeStatusTV;
    private BookingPojo bookingPojo;
    private ListView listView;
    private Dialog cd;
    private LatLng restauLtLng;
    private Dialog dialogLatLong;
    private SupportMapFragment supportMapFrag;
    private GoogleMap gMaps;
    private Spinner spn;
    private ArrayList<String> list;
    private boolean isCurrentOrder;
    private Dialog dialogLoader;
    private List<LatLng> latlongList;
    private ArrayList<DirectionResultPojo.Steps> steps;
    ;
    private static final String DIRECTION_URL_API = "https://maps.googleapis.com/maps/api/directions/json?";
    private DirectionResultPojo directionPojo;
    private String urlFullLink;
    private String storedDirection;
    private GoogleApiClient googleApiClient;
    private FusedLocationProviderClient fusedLocProvClient;
    private boolean isConnected;
    private LocationRequest locationRequest;
    private static final int RCODE_LOC = 10;
    private static final long INTERVAL_ = 10_000;
    private static final long FAST_INTERVAL_ = 10_000;
    private static final int RCODE_RES_CURRENTLOC = 100;
    private LatLng currentLatLng;
    private boolean directionsResultCalled;
    private Marker markerCurrent;
    private TextView durationDirection;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        Toolbar toolbar = findViewById(R.id.toolbar_add_order);
        toolbar.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_material);
        setSupportActionBar(toolbar);
        initUI();
        makeLatLongDialog();
        apiClient();
    }

    @Override
    protected void onResume() {
        super.onResume();
        permissionInit();
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

    private void initUI() {
        bookingPojo = (BookingPojo) getIntent().getSerializableExtra(UtilConstants.ORDER_KEY);
        getSupportActionBar().setTitle("#Order id " + bookingPojo.getOid());
        sView = findViewById(R.id.sv_order_detail);
        oIdTV = findViewById(R.id.tv_oid);
        changeStatusTV = findViewById(R.id.tv_change_status);
        spn = findViewById(R.id.spn_change_status);
        statusTV = findViewById(R.id.tv_status);
        restauTV = findViewById(R.id.tv_restau);
        totPriceTV = findViewById(R.id.tv_tot_price);
        delAddTV = findViewById(R.id.tv_add);
        timeOfOrderTV = findViewById(R.id.tv_time_order);
        dcontactNum = findViewById(R.id.tv_dcontact);
        deliveryAssignTV = findViewById(R.id.tv_delivery_assign);
        listView = findViewById(R.id.list_orders);
        listView.setAdapter(new com.deliveryperson.BookingAdap(OrderDetailActivity.this, bookingPojo.getBookingPojos()));

        oIdTV.setText(getResources().getString(R.string.order_id_1_s, bookingPojo.getOid()));
        statusTV.setText(getResources().getString(R.string.order_status_1_s, bookingPojo.getStatus()));
        restauTV.setText(getResources().getString(R.string.restaurant_name_1_s, bookingPojo.getUname()));
        totPriceTV.setText(getResources().getString(R.string.total_price_1_s, "\u20b9" + bookingPojo.getTprice()));
        delAddTV.setText(getResources().getString(R.string.delivery_address_1_s, bookingPojo.getAdd() + "," + bookingPojo.getCity() + ", " + bookingPojo.getPincode()));
        timeOfOrderTV.setText(getResources().getString(R.string.time_of_order_1_s, bookingPojo.getOdate() + " " + bookingPojo.getOtime()));
        String[] latlong = bookingPojo.getLatlng().split(",");
        restauLtLng = new LatLng(Double.parseDouble(latlong[0]), Double.parseDouble(latlong[1]));
        Log.i(TAG, "onPostExecute: " + restauLtLng.latitude + restauLtLng.longitude);
        isCurrentOrder = getIntent().getBooleanExtra(FROM_CURRENT_TAB, false);
        deliveryAssignTV.setText("Delivery to: " + bookingPojo.getUname());
        dcontactNum.setText("Delivery Assistant No. " + bookingPojo.getRcontact());

        Log.i(TAG, "initUI: " +

                getResources().

                        getString(R.string.order_status_1_s, bookingPojo.getStatus()) + ", " + isCurrentOrder);
        if (isCurrentOrder) {
            Log.i(TAG, "initUI: " + bookingPojo.getStatus().toLowerCase());
            list = new ArrayList<>();
            switch (bookingPojo.getStatus().toLowerCase()) {
                case "processed":
                    list.addAll(Arrays.asList("change the order status", "Picked", "Delivery in Progress", "Delivered"));
                    spn.setAdapter(new SpnAdap(this, list));
                    spn.setVisibility(View.VISIBLE);
                    spn.setOnItemSelectedListener(this);
                    break;
                case "delivered":
                    spn.setVisibility(View.GONE);
                    changeStatusTV.setVisibility(View.GONE);
                    break;
                case "picked":
                    list.addAll(Arrays.asList("change the order status", "Delivery in Progress", "Delivered"));
                    spn.setAdapter(new SpnAdap(this, list));
                    spn.setVisibility(View.VISIBLE);
                    spn.setOnItemSelectedListener(this);
                    break;
                case "delivery in progress":
                    list.addAll(Arrays.asList("change the order status", "Delivered"));
                    spn.setAdapter(new SpnAdap(this, list));
                    spn.setVisibility(View.VISIBLE);
                    spn.setOnItemSelectedListener(this);
                    break;
                case "ready":
                    list.addAll(Arrays.asList("change the order status", "Picked", "Delivery in Progress", "Delivered"));
                    spn.setAdapter(new SpnAdap(this, list));
                    spn.setVisibility(View.VISIBLE);
                    spn.setOnItemSelectedListener(this);
                    break;
                default:
                    spn.setVisibility(View.GONE);
                    changeStatusTV.setVisibility(View.GONE);
            }
        } else {
            spn.setVisibility(View.GONE);
            changeStatusTV.setVisibility(View.GONE);
            deliveryAssignTV.setEnabled(false);
            deliveryAssignTV.setCompoundDrawables(null, null, null, null);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void onMarkerClicked(View view) {
        if (restauLtLng != null && currentLatLng != null) {
            directionAPI();
        } else {
            Log.i(TAG, "onMarkerClicked: " + gMaps + restauLtLng + currentLatLng);
            Snackbar.make(sView, "Retreiving current location, please wait..", Snackbar.LENGTH_SHORT).show();
        }
    }

    void makeLatLongDialog() {
        dialogLatLong = new Dialog(this, R.style.AppTheme_NoActionBar);
        dialogLatLong.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#8D000000")));
        final View view = getLayoutInflater().inflate(R.layout.dialog_add_latlong, null);
        durationDirection = view.findViewById(R.id.dist_dura);
        dialogLatLong.setContentView(view);
    }

    public void dailog() {
        cd = new Dialog(OrderDetailActivity.this, R.style.AppTheme);
        cd.requestWindowFeature(Window.FEATURE_NO_TITLE);
        cd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        cd.setContentView(R.layout.loading);
        cd.setCancelable(false);
        cd.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position != 0 && !bookingPojo.getStatus().equalsIgnoreCase(list.get(position))) {
            ChangeOrderTask task = new ChangeOrderTask();
            task.execute(bookingPojo.getOid(), list.get(position));
            Log.i(TAG, "onItemSelected: " + bookingPojo.getOid() + list.get(position));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
        if (restauLtLng != null && gMaps != null) {
            Log.d(TAG, "onMapReady: " + gMaps + "," + restauLtLng);
            parseJson();
        }
    }

    private void setMap(LatLng ltLng) {
        //gMaps.clear();
        //for edit
        MarkerOptions mo = new MarkerOptions();
        mo.position(ltLng);
        mo.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mo.draggable(false);
        mo.title("you are here");
        if (markerCurrent != null)
            markerCurrent.remove();
        markerCurrent = gMaps.addMarker(mo);
        markerCurrent.showInfoWindow();

        Log.d(TAG, "onMapLongClick: " + ltLng.latitude + " , " + ltLng.longitude);

        //gMaps.moveCamera(CameraUpdateFactory.newLatLngZoom(ltLng, 18));
    }

    private void stopAnimation() {
        if (dialogLoader != null && dialogLoader.isShowing())
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

    private class ChangeOrderTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result;
            RestAPI restAPI = new RestAPI();
            try {
                JSONObject jsonObject = restAPI.Mchangeorderstatus(strings[0], strings[1]);
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
            Log.d("reply ChangeOrde", s);
            if (s.contains("Unable to resolve host")) {
                AlertDialog.Builder ad = new AlertDialog.Builder(OrderDetailActivity.this);
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
                    if (ans.compareTo("false") == 0) {
                        Snackbar.make(sView, "Order is already cancelled", Snackbar.LENGTH_SHORT).show();
                    } else if (ans.compareTo("true") == 0) {
                        bookingPojo.setStatus(list.get(spn.getSelectedItemPosition()));
                        statusTV.setText("Order status: " + bookingPojo.getStatus());
                        setListSpinner(bookingPojo.getStatus());
                        Snackbar.make(sView, "Order status changed", Snackbar.LENGTH_SHORT).show();
                    } else if (ans.compareTo("error") == 0) {
                        String error = json.getString("Data");
                        Snackbar.make(sView, error, Snackbar.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(OrderDetailActivity.this, "Error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, e.getMessage());
                }
            }
        }
    }

    private void setListSpinner(String status) {
        Log.i(TAG, "setListSpinner: " + status);
        list = new ArrayList<>();
        switch (bookingPojo.getStatus().toLowerCase()) {
            case "processed":
                list.addAll(Arrays.asList("change the order status", "Picked", "Delivery in Progress", "Delivered"));
                spn.setAdapter(new SpnAdap(this, list));
                spn.setVisibility(View.VISIBLE);
                spn.setOnItemSelectedListener(this);
                break;
            case "delivered":
                spn.setVisibility(View.GONE);
                changeStatusTV.setVisibility(View.GONE);
                break;
            case "picked":
                list.addAll(Arrays.asList("change the order status", "Delivery in Progress", "Delivered"));
                spn.setAdapter(new SpnAdap(this, list));
                spn.setVisibility(View.VISIBLE);
                spn.setOnItemSelectedListener(this);
                break;
            case "delivery in progress":
                list.addAll(Arrays.asList("change the order status", "Delivered"));
                spn.setAdapter(new SpnAdap(this, list));
                spn.setVisibility(View.VISIBLE);
                spn.setOnItemSelectedListener(this);
                break;
            case "ready":
                list.addAll(Arrays.asList("change the order status", "Picked", "Delivery in Progress", "Delivered"));
                spn.setAdapter(new SpnAdap(this, list));
                spn.setVisibility(View.VISIBLE);
                spn.setOnItemSelectedListener(this);
                break;
            default:
                spn.setVisibility(View.GONE);
                changeStatusTV.setVisibility(View.GONE);
        }
    }

    private void parseJson() {
        String s = storedDirection;
        Log.i(TAG, "parseJson: " + s);
        try {
            if (s == null) return;
            String DURA = "duration", TEXT = "text", VALUE = "value", DIST = "distance", END_LOC = "end_location", MANEUV = "maneuver",
                    START_LOC = "start_location", LAT = "lat", LNG = "lng", POINTS = "points", HTML_INSTRUC = "html_instructions", POLY = "polyline";
            PolylineOptions polylineOptions = new PolylineOptions().color(Color.BLUE).width(8);
            polylineOptions.endCap(new SquareCap());
            polylineOptions.startCap(new RoundCap());
            polylineOptions.jointType(JointType.ROUND);
            //check for has(name)
            JSONObject object = new JSONObject(s);
            JSONArray routes = object.getJSONArray("routes");
            JSONObject route = routes.getJSONObject(0);
            JSONArray legs = route.getJSONArray("legs");
            JSONObject leg = legs.getJSONObject(0);
            JSONObject duration = leg.getJSONObject(DURA);
            directionPojo = new DirectionResultPojo(duration.getString(TEXT).replace("mins", "minutes")); //duration
            JSONObject distance = leg.getJSONObject(DIST);
            directionPojo.setDistance(distance.getString(TEXT));
            JSONObject endLoc = leg.getJSONObject(END_LOC);
            directionPojo.setEnd_loc(new LatLng(endLoc.getDouble(LAT), endLoc.getDouble(LNG)));
            JSONObject startLoc = leg.getJSONObject(START_LOC);
            directionPojo.setStart_loc(new LatLng(startLoc.getDouble(LAT), startLoc.getDouble(LNG)));
            JSONArray stepJsonObj = leg.getJSONArray("steps");
            steps = new ArrayList<>();
            latlongList = new ArrayList<>();
            for (int i = 0; i < stepJsonObj.length(); i++) {
                //add to stepList, each step array
                //steps-start, endloc, dist, dura, html instruc, maneuver, polyline
                DirectionResultPojo.Steps tempStep = null;
                tempStep = new DirectionResultPojo().new Steps();
                JSONObject eachStep = stepJsonObj.getJSONObject(i);
                JSONObject poly = eachStep.getJSONObject(POLY);

                tempStep.setPolyline(poly.getString(POINTS));
                latlongList.addAll(decodePolyLine(poly.getString(POINTS)));
                steps.add(tempStep);
            }
            //draw
            //po.add all latlong
            polylineOptions.addAll(latlongList);
            JSONObject overview_poly = route.getJSONObject("overview_polyline");
            directionPojo.setOverview_poly(overview_poly.getString(POINTS));
            Log.i(TAG, "parseJson: steps list " + steps.size());
            gMaps.addPolyline(polylineOptions);
            //start, end markers
            MarkerOptions moStart = new MarkerOptions();
            moStart.position(directionPojo.getStart_loc());
            moStart.draggable(false);
            moStart.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            if (markerCurrent != null)
                markerCurrent.remove();
            markerCurrent = gMaps.addMarker(moStart);
            gMaps.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 18));
            //end loc
            MarkerOptions moEnd = new MarkerOptions();
            moEnd.position(directionPojo.getEnd_loc());
            moEnd.draggable(false);
            moEnd.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            gMaps.addMarker(moEnd);
            Log.i(TAG, "parseJson: gMaps" + gMaps);
            durationDirection.setVisibility(View.VISIBLE);
            durationDirection.setText("Journey duration: " + directionPojo.getDuration() + ", " +
                    " Distance: " + directionPojo.getDistance());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "parseJson: " + e.getMessage());
        }
    }

    private List<LatLng> decodePolyLine(final String poly) {
        int len = poly.length();
        int index = 0;
        List<LatLng> decoded = new ArrayList<LatLng>();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b;
            int shift = 0;
            int result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            decoded.add(new LatLng(
                    lat / 100000d, lng / 100000d
            ));
        }

        return decoded;
    }


    private class GetDirectionsTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            //makecall
            String ans = "";
            InputStream istream = null;
            try {
                URL url = new URL(strings[0]);
                istream = url.openConnection().getInputStream();
                StringBuilder builder = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(istream));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                istream.close();
                ans = builder.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return ans;
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
            storedDirection = s;
            mapInit();
            dialogLatLong.show();
        }

    }

    private void requestPerm() {
        ActivityCompat.requestPermissions(OrderDetailActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION
                , Manifest.permission.ACCESS_COARSE_LOCATION}, RCODE_LOC);
    }

    private void permissionInit() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //dynamic location permission
            if (ContextCompat.checkSelfPermission(OrderDetailActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(OrderDetailActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
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
                            status.startResolutionForResult(OrderDetailActivity.this, RCODE_RES_CURRENTLOC);
                            Log.i(TAG, "onResult: LocationSettingsStatusCodes.RR");
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                            Toast.makeText(OrderDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest
                Location location = locationList.get(locationList.size() - 1);
                Log.i("MapsInOrderDet", "Location: " + location.getLatitude() + " " + location.getLongitude());

                //Place current location marker
                currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                Log.d(TAG, "onLocationResult: " + currentLatLng.latitude + " , " + currentLatLng.longitude);
                if (!directionsResultCalled) {
                    directionsResultCalled = true;
                } else if (gMaps != null)
                    setMap(currentLatLng);
            }
        }
    };

    @Override
    protected void onDestroy() {
        if (fusedLocProvClient != null)
            fusedLocProvClient.removeLocationUpdates(mLocationCallback);
        super.onDestroy();
    }

    private void directionAPI() {
        Log.i(TAG, "directionAPI: " + currentLatLng);
        if (currentLatLng != null) {
            String destination = restauLtLng.latitude + "," + restauLtLng.longitude,
                    origin = currentLatLng.latitude + "," + currentLatLng.longitude;
            urlFullLink = DIRECTION_URL_API + "origin=" + origin + "&destination=" + destination + "&key=" + getString(R.string.google_maps_key);
            Log.d("urlFullLink", "directionAPI() called " + urlFullLink);
            new GetDirectionsTask().execute(urlFullLink);
        }
    }

}
