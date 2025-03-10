package com.fooddelivery.user;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by hp on 1/11/20.018.
 */

public class MapActivity extends AppCompatActivity  implements OnMapReadyCallback {

    private GoogleMap mMap;
    Double mylat=0.0,mylng=0.0;
    Double lat=0.0,lng=0.0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);

        mylat=Double.parseDouble(getIntent().getStringExtra("mylat"));
        mylng=Double.parseDouble(getIntent().getStringExtra("mylng"));
        lat=Double.parseDouble(getIntent().getStringExtra("lat"));
        lng=Double.parseDouble(getIntent().getStringExtra("lng"));

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        setActionbar("Map View");
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)
        {
            finish();
        }
        return super.onOptionsItemSelected(item);
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
    public void onMapReady(GoogleMap googleMap) {
        mMap=googleMap;

        MarkerOptions mo=new MarkerOptions();
        mo.title(getIntent().getStringExtra("name"));
        mo.position(new LatLng(lat,lng));
        mo.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        Marker m=mMap.addMarker(mo);
        m.showInfoWindow();
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lng),15));

        MarkerOptions mo1=new MarkerOptions();
        mo1.title("My Location");
        mo1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        mo1.position(new LatLng(mylat,mylng));
        mMap.addMarker(mo1);

    }
}
