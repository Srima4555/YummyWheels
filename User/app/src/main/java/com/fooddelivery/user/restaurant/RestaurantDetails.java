package com.fooddelivery.user.restaurant;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import com.fooddelivery.user.MapActivity;
import com.fooddelivery.user.R;
import com.fooddelivery.user.helper.SavedData;
import com.fooddelivery.user.pojo.restpojo;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class RestaurantDetails extends AppCompatActivity{

    String mylat,mylng;
    ArrayList<Bitmap> bts;
    Dialog cd;
    String Rid="";
    String lat="",lng="",name="",ratings="";
    ImageView Banner;
    TextView rfname,cusine,type,cost,mcost,time,add,city,pin,cont,rating;
    Button Review,Menu;

    ArrayList<String> Rlist;

    AppBarLayout app;
    CollapsingToolbarLayout collapse;
    NestedScrollView nest;
    TableRow rtb;

    FloatingActionButton fabrating;
    restpojo data;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurantdetails);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            setActionbar("");
        }

        app= (AppBarLayout) findViewById(R.id.app_bar_layout);
        nest= (NestedScrollView) findViewById(R.id.nest);
        rtb= (TableRow) findViewById(R.id.rtb);
        collapse= (CollapsingToolbarLayout) findViewById(R.id.collapse);
        collapse.setCollapsedTitleTextColor(Color.RED);
        collapse.setExpandedTitleColor(Color.RED);

        mylat=SavedData.getCurlat()+"";
        mylng=SavedData.getCurlng()+"";

        Banner= (ImageView) findViewById(R.id.rbanner);

        fabrating= (FloatingActionButton) findViewById(R.id.fabrating);
        rfname= (TextView) findViewById(R.id.rfname);
        rating= (TextView) findViewById(R.id.rrating);
        cusine= (TextView) findViewById(R.id.rcuisine);
        type= (TextView) findViewById(R.id.rtype);
        cost= (TextView) findViewById(R.id.rcost);
        mcost= (TextView) findViewById(R.id.mcost);
        time= (TextView) findViewById(R.id.rtime);
        add= (TextView) findViewById(R.id.radd);
        city= (TextView) findViewById(R.id.rcity);
        pin= (TextView) findViewById(R.id.rpin);
        cont= (TextView) findViewById(R.id.rcont);
        Review= (Button) findViewById(R.id.rreview);
        Menu= (Button) findViewById(R.id.rmenu);

        data=SavedData.getRp();
        Init();

        Review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(RestaurantDetails.this, Reviews.class);
                i.putExtra("rid",Rid);
                i.putExtra("rname",name);
                startActivity(i);
            }
        });

        Menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(RestaurantDetails.this, Menus.class);
                i.putExtra("rid",Rid);
                startActivity(i);
            }
        });
    }

    public void Init()
    {
        if (data != null) {
            setActionbar(data.getRname());
        } else {
            Log.e("RestaurantDetails", "data is null in Init()");
        }

//        setActionbar(data.getRname());
//        setActionbar(name);

        try{
            byte[] imageAsBytes = Base64.decode(data.getLogo().getBytes(), Base64.DEFAULT);
            Banner.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
        }catch (Exception e){}

        //cusine,type,cost,time,add,city,pin,cont,rating;
        String ll[] = data.getLatlng().split(",");
        lat=ll[0];
        lng=ll[1];

        Rid=data.getRid();
        name=data.getRname();
        rfname.setText(name);
        cusine.setText(data.getCuisine());
        type.setText(data.getType());
        cost.setText(data.getCost());
        mcost.setText(data.getMinorder());
        time.setText(data.getTime());
        add.setText(data.getAddress());
        city.setText(data.getCity());
        pin.setText(data.getPincode());
        cont.setText(data.getContact());

        float val=Float.parseFloat(data.getRating());
        if(val>=0)
        {
            rating.setVisibility(View.VISIBLE);
            rating.setText(FormatRatings(val));
            fabrating.setVisibility(View.VISIBLE);
        }
        else {
            fabrating.setVisibility(View.GONE);
            rating.setVisibility(View.GONE);
        }

        app.setVisibility(View.VISIBLE);
        nest.setVisibility(View.VISIBLE);
        rtb.setVisibility(View.VISIBLE);
    }

    public void dailog()
    {
        cd=new Dialog(RestaurantDetails.this,R.style.AppTheme);
        cd.requestWindowFeature(Window.FEATURE_NO_TITLE);
        cd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        cd.setContentView(R.layout.loading);
        cd.setCancelable(false);
        cd.show();
    }

    public void setActionbar(String title)
    {
        String Title="<font color='#D70C16'>"+title+"</font>";
        getSupportActionBar().setTitle(Html.fromHtml(Title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.restdetails,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        else if(item.getItemId()==R.id.map)
        {
            Intent i=new Intent(RestaurantDetails.this, MapActivity.class);
            i.putExtra("lat",lat);
            i.putExtra("lng",lng);
            i.putExtra("mylat",mylat);
            i.putExtra("mylng",mylng);
            i.putExtra("name",name);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    public String FormatRatings(float rate)
    {
        DecimalFormat dc=new DecimalFormat("#.0");
        if(rate>1)
        {
            return dc.format(rate);
        }
        else
        {
            return "0"+dc.format(rate);
        }
    }
}
