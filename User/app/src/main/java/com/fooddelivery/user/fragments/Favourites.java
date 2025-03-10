package com.fooddelivery.user.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fooddelivery.user.R;
import com.fooddelivery.user.helper.SavedData;
import com.fooddelivery.user.helper.UserPref;
import com.fooddelivery.user.pojo.menupojo;
import com.fooddelivery.user.pojo.restpojo;
import com.fooddelivery.user.restaurant.Menus;
import com.fooddelivery.user.restaurant.RestaurantDetails;
import com.fooddelivery.user.restaurant.RestaurantList;
import com.fooddelivery.user.webservice.JSONParse;
import com.fooddelivery.user.webservice.RestAPI;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Favourites extends Fragment {

    ArrayList<String> rid,fid,name,price,category,restname,img;
    ListView list;
    String uid;
    Adapter adapt;
    Dialog cd;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.favourites,container,false);
        uid= UserPref.getId(getActivity());
        list=v.findViewById(R.id.flist);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        new getFav().execute(uid);
    }

    public void dailog()
    {
        cd=new Dialog(getActivity(),R.style.AppTheme);
        cd.requestWindowFeature(Window.FEATURE_NO_TITLE);
        cd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        cd.setContentView(R.layout.loading);
        cd.setCancelable(false);
        cd.show();
    }

    private class getFav extends AsyncTask<String,JSONObject,String>
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
                JSONObject json=api.UgetFav(params[0]);
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

            if (s.contains("Unable to resolve host")) {
                AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
                ad.setTitle("Unable to Connect!");
                ad.setMessage("Check your Internet Connection,Unable to connect the Server");
                ad.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                ad.show();
            }
            else {

                try
                {
                    JSONObject json=new JSONObject(s);
                    String ans = json.getString("status");
                    if(ans.compareTo("no")==0)
                    {
                        list.setAdapter(null);
                        Snackbar.make(list,"No Items are in Favourites",Snackbar.LENGTH_SHORT).show();
                    }
                    else if(ans.compareTo("ok")==0)
                    {
                        JSONArray jarry = json.getJSONArray("Data");
                        rid=new ArrayList<String>();
                        fid=new ArrayList<String>();
                        name=new ArrayList<String>();
                        price=new ArrayList<String>();
                        category=new ArrayList<String>();
                        restname=new ArrayList<String>();
                        img=new ArrayList<String>();

                        for(int i=0;i<jarry.length();i++)
                        {
                            JSONObject jobj = jarry.getJSONObject(i);
                            rid.add(jobj.getString("data0"));
                            fid.add(jobj.getString("data1"));
                            name.add(jobj.getString("data5"));
                            price.add(jobj.getString("data7"));
                            category.add(jobj.getString("data8"));
                            restname.add(jobj.getString("data3"));
                            img.add(jobj.getString("data9"));
                        }

                        adapt=new Adapter(getActivity(),rid);
                        list.setAdapter(adapt);
                    }
                    else if(ans.compareTo("error")==0)
                    {
                        String error = json.getString("Data");
                        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(getActivity(),ans, Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e)
                {
//                    Toast.makeText(getContext(), "catch - "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private class Adapter extends ArrayAdapter<String>
    {
        Context con;
        public Adapter(@NonNull Context context, ArrayList<String> a) {
            super(context, R.layout.fav_item,a);
            con=context;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View v= LayoutInflater.from(con).inflate(R.layout.fav_item,null,true);

            CardView card=v.findViewById(R.id.cardid);
            ImageView Timg=(ImageView)v.findViewById(R.id.mimg);
            ImageView Tdel=(ImageView)v.findViewById(R.id.del);
            ImageView Tfav=(ImageView)v.findViewById(R.id.mfav);
            TextView Tname=(TextView)v.findViewById(R.id.mname);
            TextView Tprice=(TextView)v.findViewById(R.id.mprice);
            TextView Tcat=(TextView)v.findViewById(R.id.cat);
            TextView Trname=(TextView)v.findViewById(R.id.rname);

            //rid,fid,name,price,category,restname,img;
            Tname.setText(name.get(position));
            String rs = getResources().getString(R.string.pricesymbol) + " "+price.get(position);
            Tprice.setText(Html.fromHtml(rs));
            Tcat.setText("Cat : "+category.get(position));
            Trname.setText(restname.get(position));

            Tdel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new RemoveFav().execute(fid.get(position),rid.get(position),uid);
                }
            });


            try{
                byte[] imageAsBytes = Base64.decode(img.get(position).getBytes(), Base64.DEFAULT);
                Bitmap bt= BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
                bt=Bitmap.createScaledBitmap(bt, (int)70, (int)70, false);
                Timg.setImageBitmap(bt);
            }catch (Exception e){
                Timg.setImageResource(R.drawable.uicon);
            }

            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
                    ad.setTitle("Open Restaurant Details?");
                    ad.setMessage("Open Restaurant details to check the Menu and other things.");
                    ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            new restdetails().execute(rid.get(position));
                        }
                    });
                    ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    ad.show();
                }
            });

            return  v;
        }
    }

    private class RemoveFav extends AsyncTask<String,JSONObject,String>
    {
        String fid="";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dailog();
        }

        @Override
        protected String doInBackground(String... params) {
            fid=params[0];
            String a="back";
            RestAPI api=new RestAPI();
            try {
                JSONObject json=api.UremoveFav(params[0],params[1],params[2]);
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

            if (s.contains("Unable to resolve host")) {
                AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
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
                        Toast.makeText(getActivity(), "Removed from Favorites", Toast.LENGTH_SHORT).show();
                        new getFav().execute(uid);
                    }  else if (ans.compareTo("error") == 0) {
                        String error = json.getString("Data");
                        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), ans, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "catch - " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private class restdetails extends AsyncTask<String,JSONObject,String>
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
                JSONObject json=api.URestdetails(params[0]);
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
                AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
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
                        Toast.makeText(getActivity(), "This Restaurant doesnt Exist!", Toast.LENGTH_SHORT).show();
                    } else if (ans.compareTo("ok") == 0) {

                        JSONArray jarry = json.getJSONArray("Data");

                        JSONObject jobj = jarry.getJSONObject(0);
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
                        SavedData.setRp(rp);

                        Intent i=new Intent(getActivity(), RestaurantDetails.class);
                        startActivity(i);

                    } else if (ans.compareTo("error") == 0) {
                        String error = json.getString("Data");
                        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "catch - " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

        }
    }
}
