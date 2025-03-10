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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fooddelivery.user.R;
import com.fooddelivery.user.helper.SavedData;
import com.fooddelivery.user.helper.UserPref;
import com.fooddelivery.user.pojo.restpojo;
import com.fooddelivery.user.restaurant.RestaurantDetails;
import com.fooddelivery.user.webservice.JSONParse;
import com.fooddelivery.user.webservice.RestAPI;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Transactions extends Fragment {

    ArrayList<String> rname,price,type,dt;
    ListView list;
    String uid;
    Adapter adapt;
    Dialog cd;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.transactions,container,false);
        uid= UserPref.getId(getActivity());
        list=v.findViewById(R.id.flist);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        new getTrans().execute("1",uid);
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

    private class getTrans extends AsyncTask<String,JSONObject,String>
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
                JSONObject json=api.UgetTransaction(params[0],params[1]);
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
            Log.d("RESPONSE",s);
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
                        Snackbar.make(list,"There are no Tranactions",Snackbar.LENGTH_SHORT).show();
                    }
                    else if(ans.compareTo("ok")==0)
                    {
                        JSONArray jarry = json.getJSONArray("Data");
                        rname=new ArrayList<String>();
                        price=new ArrayList<String>();
                        type=new ArrayList<String>();
                        dt=new ArrayList<String>();

                        for(int i=0;i<jarry.length();i++)
                        {
                            JSONObject jobj = jarry.getJSONObject(i);
                            rname.add(jobj.getString("data0"));
                            price.add(jobj.getString("data1"));
                            type.add(jobj.getString("data2"));
                            dt.add(jobj.getString("data3"));
                        }

                        adapt=new Adapter(getActivity(),rname);
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
                    Toast.makeText(getActivity(), "catch - "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private class Adapter extends ArrayAdapter<String>
    {
        Context con;
        public Adapter(@NonNull Context context, ArrayList<String> a) {
            super(context, R.layout.transations_listitem,a);
            con=context;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View v= LayoutInflater.from(con).inflate(R.layout.transations_listitem,null,true);

            CardView card=v.findViewById(R.id.cardid);
            TextView Tname=(TextView)v.findViewById(R.id.rname);
            TextView Tprice=(TextView)v.findViewById(R.id.price);
            TextView Tdt=(TextView)v.findViewById(R.id.dt);

            Tname.setText(rname.get(position));
            String rs = getResources().getString(R.string.pricesymbol) + " "+price.get(position)+" ("+type.get(position)+")";
            Tprice.setText(Html.fromHtml(rs));
            Tdt.setText(dt.get(position));

            return  v;
        }
    }
}
