package com.fooddelivery.user.orders;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.fooddelivery.user.R;
import com.fooddelivery.user.webservice.JSONParse;
import com.fooddelivery.user.webservice.RestAPI;
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

import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;

public class OrderDetailActivity extends AppCompatActivity implements OnMapReadyCallback {
    private ScrollView sView;
    private TextView oIdTV, statusTV, restauTV,rcontactNum,totPriceTV, delAddTV, timeOfOrderTV, dcontactNum, deliveryAssignTV;
    ImageView dmap;
    private BookingPojo bookingPojo;
    private ListView listView;
    private Dialog cd;
    private LatLng ltLng,curll;
    private Dialog dialogLatLong;
    private SupportMapFragment supportMapFrag;
    private GoogleMap gMaps;
    String Dname;

    Timer timer;
    TimerTask task;
    Handler handler=new Handler();
    boolean istimer=false;

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

    private void initUI() {
        bookingPojo = (BookingPojo) getIntent().getSerializableExtra(com.fooddelivery.user.UtilConstants.ORDER_KEY);
        getSupportActionBar().setTitle("#Order id "+bookingPojo.getOid());
        sView = findViewById(R.id.sv_order_detail);
        oIdTV = findViewById(R.id.tv_oid);
        statusTV = findViewById(R.id.tv_status);
        restauTV = findViewById(R.id.tv_restau);
        totPriceTV = findViewById(R.id.tv_tot_price);
        delAddTV = findViewById(R.id.tv_add);
        timeOfOrderTV = findViewById(R.id.tv_time_order);
        dcontactNum = findViewById(R.id.tv_dcontact);
        rcontactNum = findViewById(R.id.tv_rcont);
        deliveryAssignTV = findViewById(R.id.tv_delivery_assign);
        dmap = findViewById(R.id.dmap);
        listView = findViewById(R.id.list_orders);
        listView.setAdapter(new BookingAdap(OrderDetailActivity.this, bookingPojo.getBookingPojos()));

        oIdTV.setText(getResources().getString(R.string.order_id_1_s, bookingPojo.getOid()));
        statusTV.setText(getResources().getString(R.string.order_status_1_s, bookingPojo.getStatus()));
        restauTV.setText(getResources().getString(R.string.restaurant_name_1_s, bookingPojo.getRname()));
        rcontactNum.setText(getResources().getString(R.string.restaurant_cont_1_s,bookingPojo.getPhone()));
        totPriceTV.setText(getResources().getString(R.string.total_price_1_s, "\u20b9" + bookingPojo.getTprice()));
        delAddTV.setText(getResources().getString(R.string.delivery_address_1_s, bookingPojo.getAdd() + "," + bookingPojo.getCity() + ", " + bookingPojo.getPincode()));
        timeOfOrderTV.setText(getResources().getString(R.string.time_of_order_1_s, bookingPojo.getOdate() + " " + bookingPojo.getOtime()));
        String temp[]=bookingPojo.getLatlng().split(",");
        curll=new LatLng(Double.parseDouble(temp[0]),Double.parseDouble(temp[1]));

        String ss=bookingPojo.getStatus();
        if(ss.compareTo("Delivered")!=0 && ss.compareTo("Cancelled")!=0)
        {
            if(bookingPojo.getDid().compareTo("0")==0)
            {
                if(ss.compareTo("Ordered")==0 || ss.compareTo("Processing")==0)
                {
                    Toast.makeText(this, "You cannot Track the Delivery Person as it is Not Processed Yet", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    AlertDialog.Builder ad = new AlertDialog.Builder(OrderDetailActivity.this);
                    ad.setTitle("Delivery Person Not Assigned!");
                    ad.setMessage("You cannot Track the Delivery Person as the Order is not Assigned Yet, Contact the Restaurant for More Assistance.");
                    ad.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    ad.show();
                }

                dmap.setVisibility(View.GONE);
                deliveryAssignTV.setVisibility(View.GONE);
                dcontactNum.setVisibility(View.GONE);
            }
            else
            {
                DetailOrderTask task = new DetailOrderTask();
                task.execute(bookingPojo.getOid());
            }
        }
        else
        {
            dmap.setVisibility(View.GONE);
            deliveryAssignTV.setVisibility(View.GONE);
            dcontactNum.setVisibility(View.GONE);
        }

        rcontactNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+bookingPojo.getPhone()));
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void onMarkerClicked(View view) {
        dialogLatLong.show();
        loadMap(true);

        cancelTimer();
        timer=new Timer();
        intTimer();
        timer.schedule(task,0,1000);
    }

    void makeLatLongDialog() {
        dialogLatLong = new Dialog(this, R.style.AppTheme_NoActionBar);
        dialogLatLong.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#8D000000")));
        final View view = getLayoutInflater().inflate(R.layout.dialog_add_latlong, null);
        mapInit();
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
                        dmap.setVisibility(View.GONE);
                        deliveryAssignTV.setVisibility(View.GONE);
//                        statusTV.setVisibility(View.GONE);
                        dcontactNum.setVisibility(View.GONE);
                    } else if (ans.compareTo("ok") == 0) {
                        JSONArray jarry = json.getJSONArray("Data");
                        final JSONObject object = jarry.getJSONObject(0);
                        statusTV.setText(getResources().getString(R.string.order_status_1_s, object.getString("data0")));
                        if (bookingPojo.getDid().equalsIgnoreCase("0")) {
                            deliveryAssignTV.setText("Not yet assigned");
                        } else {
                            deliveryAssignTV.setText("Delivery Person: "+object.getString("data1"));
                            Dname=object.getString("data1");
                        }
                        final String cont=object.getString("data2");
                        dcontactNum.setText("Delivery Assistance: "+cont);
                        String[] latlong = object.getString("data3").split(",");
                        ltLng = new LatLng(Double.parseDouble(latlong[0]), Double.parseDouble(latlong[1]));
                        dcontactNum.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:"+cont));
                                startActivity(intent);
                            }
                        });

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

    private void mapInit() {
        if (supportMapFrag == null) {
            supportMapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.frag_supportmap);
            supportMapFrag.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        gMaps = googleMap;

        gMaps.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String url="http://maps.google.com/maps?saddr="+ltLng+"&daddr="+curll;
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse(url));
                startActivity(intent);
                return false;
            }
        });
    }

    public void loadMap(boolean isanimate)
    {
        gMaps.clear();
        setMarker(Dname+"'s Location",ltLng);
        setMarker("Delivery Location",curll);
        if(isanimate)
            gMaps.animateCamera(CameraUpdateFactory.newLatLngZoom(ltLng,18));
    }

    public void setMarker(String name,LatLng ll)
    {
        MarkerOptions mo=new MarkerOptions();
        mo.title(name);
        mo.position(ll);
        if(name.compareTo("Delivery Location")!=0)
        {
            mo.icon(BitmapDescriptorFactory.fromResource(R.drawable.delivery));
        }
        gMaps.addMarker(mo);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        cancelTimer();
    }

    public void cancelTimer()
    {
        try
        {
            if(timer!=null)
            {
                timer.cancel();
                task.cancel();
                timer=null;
                Log.d("Cancelled","cancelled");
            }
        }
        catch (Exception e){}
    }

    public void intTimer()
    {
        task=new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(!istimer)
                        {
                            istimer=true;
                            new Deldetails().execute(bookingPojo.getOid());
                        }
                    }
                });
            }
        };
    }

    private class Deldetails extends AsyncTask<String, JSONObject, String> {

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
            if (s.contains("Unable to resolve host")) {
            } else {
                try {
                    JSONObject json = new JSONObject(s);
                    String ans = json.getString("status");
                    if (ans.compareTo("no") == 0) {
                    } else if (ans.compareTo("ok") == 0) {
                        JSONArray jarry = json.getJSONArray("Data");
                        final JSONObject object = jarry.getJSONObject(0);
                        String[] latlong = object.getString("data3").split(",");
                        ltLng = new LatLng(Double.parseDouble(latlong[0]), Double.parseDouble(latlong[1]));

                        loadMap(false);

                    } else if (ans.compareTo("error") == 0) {
                        String error = json.getString("Data");
//                        Toast.makeText(OrderDetailActivity.this, error, Toast.LENGTH_SHORT).show();
                    } else {
//                        Toast.makeText(OrderDetailActivity.this, ans, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
//                    Toast.makeText(OrderDetailActivity.this, "catch - " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            istimer=false;
        }
    }
}
