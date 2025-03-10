package com.fooddelivery.user.orders;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.fooddelivery.user.R;
import com.fooddelivery.user.UtilConstants;
import com.fooddelivery.user.helper.UserPref;
import com.fooddelivery.user.webservice.JSONParse;
import com.fooddelivery.user.webservice.RestAPI;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.content.ContentValues.TAG;

public class Booking_current extends Fragment {

    RelativeLayout nobook;
    ListView list;
    String Uid = "";
    Dialog cd;
    private ArrayList<BookingPojo> bookingPojos;
    private OrderAdap orderAdap;
    private String src = "current";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.booking_each_layout, container, false);
        Uid = UserPref.getId(getActivity());
        nobook = (RelativeLayout) v.findViewById(R.id.tbnobook);
        list = (ListView) v.findViewById(R.id.Blist);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        bookingPojos = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String date = sdf.format(new Date().getTime());
        new getlist().execute(Uid, src, date);
        Log.i(TAG, "onCreateView: current " + Uid + src + date);
    }

    public void dailog() {
        cd = new Dialog(getActivity(), R.style.AppTheme);
        cd.requestWindowFeature(Window.FEATURE_NO_TITLE);
        cd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        cd.setContentView(R.layout.loading);
        cd.setCancelable(false);
        cd.show();
    }

    private class getlist extends AsyncTask<String, JSONObject, String> {
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
                JSONObject json = api.UgetOrders(params[0], params[1], params[2]);
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
                        list.setAdapter(null);
                        nobook.setVisibility(View.VISIBLE);
                        list.setAdapter(null);
                    } else if (ans.compareTo("ok") == 0) {
                        if (bookingPojos.size() > 0)
                            bookingPojos.clear();
                        BookingPojo pojo;
                        ArrayList<BookingPojo> listPojo;
                        JSONArray jarry = json.getJSONArray("Data");
                        for (int j = 0; j < jarry.length(); j++) {
                            JSONObject object = jarry.getJSONObject(j);
                            pojo = new BookingPojo
                                    (object.getString("Oid"),
                                            object.getString("TPrice"), object.getString("Rid"),
                                            object.getString("Uid"), object.getString("Add"), object.getString("City"),
                                            object.getString("Pincode"), object.getString("Latlng"), object.getString("Phone"),
                                            object.getString("Odate"), object.getString("Otime"), object.getString("Ddate"),
                                            object.getString("Status"), object.getString("Payment"), object.getString("Did"),
                                            object.getString("Rname"), object.getString("Rcont"));
                            Log.d(TAG, jarry.length() + " : main arr");
                            listPojo = new ArrayList<BookingPojo>();
                            JSONArray jarr = object.getJSONArray("Data");
                            for (int i = 0; i < jarr.length(); i++) {
                                JSONObject jobj = jarr.getJSONObject(i);
                                listPojo.add(new BookingPojo(jobj.getString("data0"),
                                        jobj.getString("data1"),
                                        jobj.getString("data2"),
                                        jobj.getString("data3")));
                            }
                            pojo.setBookingPojos(listPojo);
                            bookingPojos.add(pojo);
                        }
                        setLV();
                    } else if (ans.compareTo("error") == 0) {
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

    private void setLV() {
        orderAdap = new OrderAdap(getContext(), bookingPojos, this);
        list.setAdapter(orderAdap);
        nobook.setVisibility(View.GONE);
        list.setVisibility(View.VISIBLE);
    }

    public void openOrderDetailActivity(BookingPojo bookingPojo) {
        Intent intentDetail = new Intent(getContext(), OrderDetailActivity.class);
        intentDetail.putExtra(UtilConstants.ORDER_KEY, bookingPojo);
        startActivity(intentDetail);
    }
}
