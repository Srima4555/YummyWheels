package com.fooddelivery.user.Activity;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.fooddelivery.user.R;
import com.fooddelivery.user.helper.SavedData;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class PlaceSelection extends AppCompatActivity   implements OnMapReadyCallback {

    Button submit;
    double lat=0,lng=0;
    EditText stxt;
    ImageView sbtn;
    TableRow search;
    private GoogleMap mMap;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);
        setActionbar("Place Selection");

        stxt=findViewById(R.id.stext);
        sbtn=findViewById(R.id.sbtn);
        search=findViewById(R.id.searchtb);
        search.setVisibility(View.VISIBLE);
        submit=findViewById(R.id.ssubmit);
        submit.setVisibility(View.VISIBLE);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stxt.length()>0) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    search();
                } else {
                    Snackbar snack = Snackbar.make(v, "Enter the Place to Search!", Snackbar.LENGTH_SHORT);
                    View vs = snack.getView();
                    TextView txt = (TextView) vs.findViewById(android.support.design.R.id.snackbar_text);
                    txt.setTextColor(Color.RED);
                    snack.show();
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    SavedData.setLat(lat);
                    SavedData.setLng(lng);
                    finish();

            }
        });
    }

    public void search () {
        try {
            mMap.clear();
            Geocoder g = new Geocoder(PlaceSelection.this);
            List<Address> list = g.getFromLocationName(stxt.getText().toString(), 1);
            if(list.size()>0)
            {
                Address add = list.get(0);
                lat = add.getLatitude();
                lng = add.getLongitude();

                drawMarker(lat,lng);
            }
            else
            {
                Toast.makeText(PlaceSelection.this, "NO Place Found", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Toast.makeText(PlaceSelection.this, "NO Match Found" + "\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void drawMarker(double lat,double lng)
    {
        LatLng latLng = new LatLng(lat, lng);
        MarkerOptions mark = new MarkerOptions();
        mark.position(latLng);
        mark.draggable(true);
        Marker marker = mMap.addMarker(mark);
        marker.showInfoWindow();
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }

    public void setActionbar(String title)
    {
        String Title="<font color='#D70C16'>"+title+"</font>";
        getSupportActionBar().setTitle(Html.fromHtml(Title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)
        {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(SavedData.getLat()>0)
        {
            lat=SavedData.getLat();
            lng=SavedData.getLng();

            drawMarker(lat,lng);
        }

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                mMap.clear();
                lat=latLng.latitude;
                lng=latLng.longitude;

                drawMarker(lat,lng);
            }
        });

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker mark) {

            }

            @Override
            public void onMarkerDragEnd(Marker mark) {
                LatLng latLng = mark.getPosition();
                lat=latLng.latitude;
                lng=latLng.longitude;

                drawMarker(lat,lng);
            }
        });
    }
}
