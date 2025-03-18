package com.managerestaurant;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

import java.util.ArrayList;
import java.util.Arrays;

import static android.content.ContentValues.TAG;
import static com.managerestaurant.UtilConstants.FROM_CURRENT_TAB;

public class OrderDetailActivity extends AppCompatActivity implements OnMapReadyCallback, AdapterView.OnItemSelectedListener {
    private ScrollView sView;
    private TextView oIdTV, statusTV, restauTV, totPriceTV, delAddTV, timeOfOrderTV, dcontactNum, deliveryAssignTV, changeStatusTV;
    private BookingPojo bookingPojo;
    private ListView listView;
    private Dialog cd;
    private LatLng ltLng;
    private Dialog dialogLatLong;
    private SupportMapFragment supportMapFrag;
    private GoogleMap gMaps;
    private Spinner spn;
    private ArrayList<String> list;
    private boolean isCurrentOrder;
    private DPersonPojo dPersonPojo;
    private ArrayList<DPersonPojo> dPersonPojos;
    private Dialog dialogLoader;
    private ListView listViewDPerson;
    private Dialog dialogShowDPerson;
    private TextView selectTV;
    private ImageView crossIV;
    boolean isdeliverypersonflag=false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        Toolbar toolbar = findViewById(R.id.toolbar_add_order);
        toolbar.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_material);
        setSupportActionBar(toolbar);
        initUI();
        makeLatLongDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DetailOrderTask task = new DetailOrderTask();
        task.execute(bookingPojo.getOid());
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
        listView.setAdapter(new com.managerestaurant.BookingAdap(OrderDetailActivity.this, bookingPojo.getBookingPojos()));

        oIdTV.setText(getResources().getString(R.string.order_id_1_s, bookingPojo.getOid()));
        statusTV.setText(getResources().getString(R.string.order_status_1_s, bookingPojo.getStatus()));
        restauTV.setText("Ordered by: "+bookingPojo.getUname());
        totPriceTV.setText(getResources().getString(R.string.total_price_1_s, "\u20b9" + bookingPojo.getTprice()));
        delAddTV.setText(getResources().getString(R.string.delivery_address_1_s, bookingPojo.getAdd() + "," + bookingPojo.getCity() + ", " + bookingPojo.getPincode()));
        timeOfOrderTV.setText(getResources().getString(R.string.time_of_order_1_s, bookingPojo.getOdate() + " " + bookingPojo.getOtime()));
        isCurrentOrder = getIntent().getBooleanExtra(FROM_CURRENT_TAB, false);

        Log.i(TAG, "initUI: " + getResources().getString(R.string.order_status_1_s, bookingPojo.getStatus()) + ", " + isCurrentOrder);
        if (isCurrentOrder) {
            Log.i(TAG, "initUI: " + bookingPojo.getStatus().toLowerCase());
            list = new ArrayList<String>();
            switch (bookingPojo.getStatus().toLowerCase()) {
                case "ordered":
                    list.addAll(Arrays.asList("change the order status", "Processing", "Processed", "Ready"));
                    spn.setAdapter(new SpnAdap(this, list));
                    spn.setVisibility(View.VISIBLE);
                    spn.setOnItemSelectedListener(this);
                    break;
                case "cancelled":
                    spn.setVisibility(View.GONE);
                    changeStatusTV.setVisibility(View.GONE);
                    break;
                case "processing":
                    list.addAll(Arrays.asList("change the order status", "Processing", "Processed", "Ready"));
                    spn.setAdapter(new SpnAdap(this, list));
                    spn.setVisibility(View.VISIBLE);
                    spn.setOnItemSelectedListener(this);
                    break;
                case "processed":
                    list.addAll(Arrays.asList("change the order status", "Processed", "Ready"));
                    spn.setAdapter(new SpnAdap(this, list));
                    spn.setVisibility(View.VISIBLE);
                    spn.setOnItemSelectedListener(this);
                    break;
                case "ready":
                    spn.setVisibility(View.GONE);
                    changeStatusTV.setVisibility(View.GONE);
                    break;
                default:
                    spn.setVisibility(View.GONE);
                    changeStatusTV.setVisibility(View.GONE);
            }
        } else {
            spn.setVisibility(View.GONE);
            changeStatusTV.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void onMarkerClicked(View view) {
        if (ltLng != null)
            dialogLatLong.show();
    }

    void makeLatLongDialog() {
        dialogLatLong = new Dialog(this, R.style.AppTheme_NoActionBar);
        dialogLatLong.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#8D000000")));
        final View view = getLayoutInflater().inflate(R.layout.dialog_add_latlong_plain, null);
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

    public void onPersonSelected(final DPersonPojo dpPojo) {
        //dialog to confirm the assigning of dPerson to this order
        AlertDialog.Builder ad = new AlertDialog.Builder(OrderDetailActivity.this);
        ad.setMessage("Continue assigning delivery person " + dpPojo.getName() + " for this order Id #" + bookingPojo.getOid() + " ?");
        ad.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        ad.setPositiveButton("assign", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AssignDPersonTask task = new AssignDPersonTask();
                task.execute(bookingPojo.getOid(), dpPojo.getDid());
                if (dialogShowDPerson.isShowing()) dialogShowDPerson.dismiss();
            }
        });
        ad.show();
    }


    private class DetailOrderTask extends AsyncTask<String, JSONObject, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dailog();
        }

        @Override
        protected String doInBackground(String... params) {
            String a = "back";
            RestAPI api = new RestAPI();
            try {
                JSONObject json = api.UgetDeliveryDetails(params[0]);
                JSONParse jp = new JSONParse();
                a = jp.parse(json);
            } catch (Exception e) {
                a = e.getMessage();
            }
            return a;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("reply", s);
            super.onPostExecute(s);
            cd.cancel();

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
                    if (ans.compareTo("no") == 0) {
                        deliveryAssignTV.setVisibility(View.GONE);
//                        statusTV.setVisibility(View.GONE);
                        dcontactNum.setVisibility(View.GONE);
                    } else if (ans.compareTo("ok") == 0) {
                        JSONArray jarry = json.getJSONArray("Data");
                        JSONObject object = jarry.getJSONObject(0);
                        statusTV.setText("Order status: " + object.getString("data0"));
                        if (bookingPojo.getDid().equalsIgnoreCase("0")) {
                            deliveryAssignTV.setText("Delivery assigned to: Not yet assigned");
                        } else {
                            deliveryAssignTV.setText("Delivery assigned to: " + object.getString("data1"));
                        }
                        dcontactNum.setText("Delivery Assistant No. " + object.getString("data2"));
                        String[] latlong = object.getString("data3").split(",");
                        ltLng = new LatLng(Double.parseDouble(latlong[0]), Double.parseDouble(latlong[1]));
                        Log.i(TAG, "onPostExecute: " + latlong[0] + ", " + latlong[1]);
                        mapInit();

                    } else if (ans.compareTo("error") == 0) {
                        String error = json.getString("Data");
                        Toast.makeText(OrderDetailActivity.this, error, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(OrderDetailActivity.this, ans, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(OrderDetailActivity.this, "catch - " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private class AssignDPersonTask extends AsyncTask<String, JSONObject, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dailog();
        }

        @Override
        protected String doInBackground(String... params) {
            String a = "back";
            RestAPI api = new RestAPI();
            try {
                JSONObject json = api.MassignDeliverP_toOrder(params[0], params[1]);
                JSONParse jp = new JSONParse();
                a = jp.parse(json);
            } catch (Exception e) {
                a = e.getMessage();
            }
            return a;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("reply", s);
            super.onPostExecute(s);
            cd.cancel();

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
                    if (ans.compareTo("true") == 0) {
                        Snackbar.make(sView, "Delivery person is assigned successfully", Snackbar.LENGTH_LONG).show();
                    } else if (ans.compareTo("error") == 0) {
                        String error = json.getString("Data");
                        Toast.makeText(OrderDetailActivity.this, error, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(OrderDetailActivity.this, ans, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(OrderDetailActivity.this, "catch - " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //
    private void mapInit() {
        if (supportMapFrag == null) {
            supportMapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.frag_supportmap);
            supportMapFrag.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        gMaps = googleMap;
        if (ltLng != null && gMaps != null) {
            Log.d(TAG, "onMapReady: " + gMaps + "," + ltLng);
            setMap(ltLng);
            gMaps.animateCamera(CameraUpdateFactory.newLatLngZoom(ltLng, 18));
        }

        gMaps.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng ltLng) {
                Log.d(TAG, "onMapLongClick: " + ltLng.latitude + " , " + ltLng.longitude);
                //setMap(ltLng);
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

            }
        });
    }

    private void setMap(LatLng ltLng) {
        gMaps.clear();
        //for edit
        MarkerOptions mo = new MarkerOptions();
        mo.position(ltLng);
        mo.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mo.draggable(true);
        Marker marker = gMaps.addMarker(mo);
        marker.showInfoWindow();

        Log.d(TAG, "onMapLongClick: " + ltLng.latitude + " , " + ltLng.longitude);
        //set circle:raduis:testing 10m, latlon in DB
        gMaps.addCircle(new CircleOptions()
                .center(ltLng)
                .radius(30)   //10metres
                .fillColor(Color.LTGRAY)
                .strokeWidth(2).strokeColor(Color.RED)
        );
        gMaps.moveCamera(CameraUpdateFactory.newLatLngZoom(ltLng, 18));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_order_detail, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!getIntent().getBooleanExtra(FROM_CURRENT_TAB, false)) {
            menu.findItem(R.id.deliv_assign_mi).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //processed or ready then can assign
        //
        if (item.getItemId() == R.id.deliv_assign_mi) {
            if (bookingPojo.getStatus().equalsIgnoreCase("processed") || bookingPojo.getStatus().equalsIgnoreCase("ready")) {
                isdeliverypersonflag=true;
                assignDelivery("true");
            } else
                Snackbar.make(sView, "A delivery person cannot be assigned for CURRENT ORDER STATUS: " + bookingPojo.getStatus(), Snackbar.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }


    private void assignDelivery(String firstSearch) {
        GetDeliveryPersonTask task = new GetDeliveryPersonTask();
        task.execute(bookingPojo.getOid(), firstSearch);
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

    private void showDPersonNearby() {
        dialogShowDPerson = new Dialog(this);
        final View view = getLayoutInflater().inflate(R.layout.dialog_dperson, null);
        listViewDPerson = view.findViewById(R.id.lv);
        crossIV = view.findViewById(R.id.iv_cross);
        crossIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogShowDPerson.isShowing()) dialogShowDPerson.dismiss();
            }
        });
        selectTV = view.findViewById(R.id.tv_select);
        if (dPersonPojos.size() > 0)
            listViewDPerson.setAdapter(new DPersonAdap(OrderDetailActivity.this, dPersonPojos, OrderDetailActivity.this));
        else {
            selectTV.setVisibility(View.GONE);
            listViewDPerson.setVisibility(View.GONE);
            listViewDPerson.setAdapter(null);
        }
        dialogShowDPerson.setContentView(view);
        dialogShowDPerson.show();
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
        switch (status.toLowerCase()) {
            case "ordered":
                list.addAll(Arrays.asList("change the order status", "Processing", "Processed", "Ready"));
                spn.setAdapter(new SpnAdap(this, list));
                spn.setVisibility(View.VISIBLE);
                spn.setOnItemSelectedListener(this);
                break;
            case "cancelled":
                spn.setVisibility(View.GONE);
                changeStatusTV.setVisibility(View.GONE);
                break;
            case "processing":
                list.addAll(Arrays.asList("change the order status", "Processing", "Processed", "Ready"));
                spn.setAdapter(new SpnAdap(this, list));
                spn.setVisibility(View.VISIBLE);
                spn.setOnItemSelectedListener(this);
                break;
            case "processed":
                list.addAll(Arrays.asList("change the order status", "Processed", "Ready"));
                spn.setAdapter(new SpnAdap(this, list));
                spn.setVisibility(View.VISIBLE);
                spn.setOnItemSelectedListener(this);
                break;
            case "ready":
                spn.setVisibility(View.GONE);
                changeStatusTV.setVisibility(View.GONE);
                break;
            default:
                spn.setVisibility(View.GONE);
                changeStatusTV.setVisibility(View.GONE);
        }
    }

    private class GetDeliveryPersonTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result;
            RestAPI restAPI = new RestAPI();
            try {
                JSONObject jsonObject = restAPI.MgetDeliveryPerson(strings[0], strings[1]);
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
            Log.d("reply delivPerson", s);
            if (s.contains("Unable to resolve host")) {
                AlertDialog.Builder ad = new AlertDialog.Builder(OrderDetailActivity.this);
                ad.setTitle("Unable to Connect!");
                ad.setMessage("Check your Internet Connection, Unable to connect the Server");
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
                    dPersonPojos = new ArrayList<>();
                    if (ans.compareTo("no") == 0) {
                        Snackbar.make(sView, "No delivery person is available nearby, increasing search distance", Snackbar.LENGTH_LONG).show();
                        if (dialogShowDPerson != null && dialogShowDPerson.isShowing())
                            dialogShowDPerson.dismiss();
                        if(isdeliverypersonflag)
                        {
                            isdeliverypersonflag=false;
                            assignDelivery("false");
                        }

                    } else if (ans.compareTo("ok") == 0) {
                        JSONArray jarry = json.getJSONArray("Data");
                        for (int i = 0; i < jarry.length(); i++) {
                            JSONObject object = jarry.getJSONObject(i);
                            dPersonPojo = new DPersonPojo(object.getString("data0"), object.getString("data1"), object.getString("data2"), object.getString("data3"),
                                    object.getString("data4"), object.getString("data5"), object.getString("data6"), object.getString("data7"));
                            dPersonPojos.add(dPersonPojo);
                        }
                        showDPersonNearby();
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
}
