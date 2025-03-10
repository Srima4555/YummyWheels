package com.fooddelivery.user.restaurant;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fooddelivery.user.R;
import com.fooddelivery.user.helper.SavedData;
import com.fooddelivery.user.pojo.restpojo;
import com.fooddelivery.user.webservice.JSONParse;
import com.fooddelivery.user.webservice.RestAPI;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class RestaurantList extends AppCompatActivity{

    boolean issearch;
    Toolbar toolbar;
    String latlng;
    RelativeLayout tbno;
    GridView grid;
    String cuisine,value;
    int selection;
    SharedPreferences sp;
    Dialog cd;
    String uid="";
    ArrayList<restpojo> data;
    View mCustomView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurantlist);
        sp = getSharedPreferences("rfu", Context.MODE_PRIVATE);
        uid = sp.getString("uid", "");
        cuisine = getIntent().getStringExtra("cuisine");
        value = getIntent().getStringExtra("value");
        latlng = getIntent().getStringExtra("latlng");
        selection = getIntent().getIntExtra("selection", 0);
        issearch=false;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setActionbar(cuisine);

        grid = (GridView) findViewById(R.id.grid);
        tbno = (RelativeLayout) findViewById(R.id.tbnorest);

        defaultcalls();

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                SavedData.setRp(data.get(position));
                Intent i = new Intent(RestaurantList.this, RestaurantDetails.class);
                startActivity(i);

            }
        });
    }

    public void defaultcalls()
    {
        if (selection == 1) {
            new getList().execute(cuisine, "pincode", value);
        } else if (selection == 2) {
            new getList().execute(cuisine, "city", value);
        } else {
            new getList().execute(cuisine, "near", latlng);
        }
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

    public void addSearchview()
    {
        LayoutInflater mInflater=LayoutInflater.from(RestaurantList.this);
        mCustomView = mInflater.inflate(R.layout.searchview, null);
        toolbar.addView(mCustomView);
        SearchView sv=mCustomView.findViewById(R.id.searchv);

        issearch=true;
        invalidateOptionsMenu();

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                new getList().execute(cuisine, "name", s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rest,menu);
        MenuItem s = menu.findItem(R.id.search);
        MenuItem r = menu.findItem(R.id.close);

        if(issearch)
        {
            s.setVisible(false);
            r.setVisible(true);
        }
        else
        {
            r.setVisible(false);
            s.setVisible(true);

        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)
        {
            finish();
        }
        else if(item.getItemId()==R.id.search)
        {
            addSearchview();
        }
        else if(item.getItemId()==R.id.close)
        {
            issearch=false;
            toolbar.removeView(mCustomView);
            setActionbar(cuisine);
            invalidateOptionsMenu();
            defaultcalls();
        }
        return super.onOptionsItemSelected(item);
    }

    private class  Adapter extends ArrayAdapter<restpojo> {

        Context con;
        ArrayList<restpojo> data;
        public Adapter(@NonNull Context context, ArrayList<restpojo> a) {
            super(context, R.layout.restaurantlist_item,a);
            con=context;
            data=a;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View v= LayoutInflater.from(con).inflate(R.layout.restaurantlist_item,null,true);
            CardView card=(CardView)v.findViewById(R.id.card);
            ImageView img=(ImageView)v.findViewById(R.id.rlimg);
            TextView rate=(TextView)v.findViewById(R.id.rlrating);
            TextView name=(TextView)v.findViewById(R.id.rlname);
            TextView cuisine=(TextView)v.findViewById(R.id.rlcuisine);
            TextView type=(TextView)v.findViewById(R.id.rltype);
            TextView add=(TextView)v.findViewById(R.id.radd);

            restpojo rp=data.get(position);

            name.setText(rp.getRname());
            cuisine.setText(rp.getCuisine());
            add.setText(rp.getAddress());
            type.setText(rp.getType());

            try{
                byte[] imageAsBytes = Base64.decode(rp.getLogo().getBytes(), Base64.DEFAULT);
                img.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
            }catch (Exception e){}

            float val=Float.parseFloat(rp.getRating());
            if(val>=0)
            {
                if(val>=0 && val<2.5)
                {
                    rate.setBackgroundColor(getResources().getColor(R.color.red));
                }
                else if(val>=2.5 && val<3.5)
                {
                    rate.setBackgroundColor(getResources().getColor(R.color.orange));
                }
                else if(val>=3.5)
                {
                    rate.setBackgroundColor(getResources().getColor(R.color.green));
                }
                rate.setText(FormatRatings(val));
            }
            else
            {
                rate.setVisibility(View.GONE);
            }

            return v;
        }
    }

    public void dailog()
    {
        cd=new Dialog(RestaurantList.this,R.style.AppTheme);
        cd.requestWindowFeature(Window.FEATURE_NO_TITLE);
        cd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        cd.setContentView(R.layout.loading);
        cd.setCancelable(false);
        cd.show();
    }

    private class getList extends AsyncTask<String,JSONObject,String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dailog();
        }

        @Override
        protected String doInBackground(String... params) {
            String a="back";
            RestAPI api=new RestAPI();
            try {
                JSONObject json=api.Usearchby(params[0],params[1],params[2]);
                JSONParse jp=new JSONParse();
                a=jp.parse(json);
            } catch (Exception e) {
                a=e.getMessage();
            }
            return a;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            cd.cancel();
            Log.d("REPLY",s);
            if (s.contains("Unable to resolve host")) {
                AlertDialog.Builder ad = new AlertDialog.Builder(RestaurantList.this);
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
                        tbno.setVisibility(View.VISIBLE);
                        grid.setAdapter(null);
                    } else if (ans.compareTo("ok") == 0) {

                        JSONArray jarry = json.getJSONArray("Data");
                        data=new ArrayList<restpojo>();

                        for(int i=0;i<jarry.length();i++)
                        {
                            JSONObject jobj = jarry.getJSONObject(i);
                            String rid=jobj.getString("data0");
                            String rname=jobj.getString("data1");
                            String contact=jobj.getString("data2");
                            String address=jobj.getString("data3");
                            String city=jobj.getString("data4");
                            String pincode=jobj.getString("data5");
                            String cuisine=jobj.getString("data6");
                            String latlng=jobj.getString("data7");
                            String time=jobj.getString("data8");
                            String cost=jobj.getString("data9");
                            String logo=jobj.getString("data10");
                            String minorder=jobj.getString("data11");
                            String type=jobj.getString("data12");
                            String status=jobj.getString("data13");
                            String rating=jobj.getString("data14");

                            restpojo rp=new restpojo(rid,rname,contact,address,city,pincode,cuisine,latlng,time,cost,logo,minorder,type,status,rating);
                            data.add(rp);
                        }

                        Adapter adapt = new Adapter(RestaurantList.this, data);
                        grid.setAdapter(adapt);
                        tbno.setVisibility(View.GONE);

                    } else if (ans.compareTo("error") == 0) {
                        String error = json.getString("Data");
                        Toast.makeText(RestaurantList.this, error, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(RestaurantList.this, "catch - " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

        }
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
