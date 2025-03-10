package com.fooddelivery.user.orders;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fooddelivery.user.R;
import com.fooddelivery.user.fragments.Favourites;
import com.fooddelivery.user.helper.SavedData;
import com.fooddelivery.user.pojo.restpojo;
import com.fooddelivery.user.restaurant.RestaurantDetails;
import com.fooddelivery.user.webservice.JSONParse;
import com.fooddelivery.user.webservice.RestAPI;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class OrderAdap extends BaseAdapter {
    private static final String TAGG = "TAG";
    private Context context;
    private ArrayList<BookingPojo> bookingPojos;
    private BookingPojo orderPojo;
    private Booking_current bookingCurrent;
    private Booking_prev bookingPrev;
    private Booking_upcoming bookingUpcoming;
    private Fragment fragment;
    Dialog cd;
    int pos;

    public OrderAdap(Context context, ArrayList<BookingPojo> issuedBookPojos, Booking_upcoming bookingUpcoming) {
        this.context = context;
        this.bookingPojos = issuedBookPojos;
        fragment = this.bookingUpcoming = bookingUpcoming;
    }

    public OrderAdap(Context context, ArrayList<BookingPojo> issuedBookPojos, Booking_prev bookingPrev) {
        this.context = context;
        this.bookingPojos = issuedBookPojos;
        fragment = this.bookingPrev = bookingPrev;
    }

    public OrderAdap(Context context, ArrayList<BookingPojo> issuedBookPojos, Booking_current bookingCurrent) {
        this.context = context;
        this.bookingPojos = issuedBookPojos;
        fragment = this.bookingCurrent = bookingCurrent;
    }

    @Override
    public int getCount() {
        return bookingPojos.size();
    }

    @Override
    public Object getItem(int position) {
        return bookingPojos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        VHolder viewHolder = new VHolder();
        if (convertView == null) {
            ///inflate
            convertView = LayoutInflater.from(context).inflate(R.layout.booking_listitem, parent, false);
            viewHolder.fromRestau = convertView.findViewById(R.id.tv_from_restau);
            viewHolder.priceTV = convertView.findViewById(R.id.tv_tot_price);  //format
            viewHolder.dateTimeTV = convertView.findViewById(R.id.tv_datetime);
            viewHolder.currentStatus = convertView.findViewById(R.id.tv_status_current);
            viewHolder.oidTV = convertView.findViewById(R.id.tv_oid);   //clicklistener
            viewHolder.repeat=convertView.findViewById(R.id.repeat);
            viewHolder.repeat.setVisibility(View.GONE);

            if(bookingPrev!=null)
            {
                Log.d("ADAPTER",position+" - if");
                viewHolder.repeat.setVisibility(View.VISIBLE);
            }
            else
            {
                String s=bookingPojos.get(position).getStatus();
                if(s.compareTo("Ordered")==0)
                {
                    Log.d("ADAPTER",position+" - o");
                    viewHolder.repeat.setImageResource(R.drawable.corder);
                    viewHolder.repeat.setVisibility(View.VISIBLE);
                }
                else if(s.compareTo("Processing")==0)
                {
                    Log.d("ADAPTER",position+" - p");
                    viewHolder.repeat.setImageResource(R.drawable.corder);
                    viewHolder.repeat.setVisibility(View.VISIBLE);
                }
                else
                {
                    Log.d("ADAPTER",position+" - else");
                    viewHolder.repeat.setVisibility(View.GONE);
                    viewHolder.repeat.setImageBitmap(null);
                }
            }


            //setTag
            convertView.setTag(viewHolder);
        } else {
            //getTag
            viewHolder = (VHolder) convertView.getTag();
        }
        //settexts
        if ((orderPojo = bookingPojos.get(position)) != null) {
            viewHolder.priceTV.setText(orderPojo.getTprice());
            viewHolder.dateTimeTV.setText("Ordered on: "+ orderPojo.getOdate() + " "+ orderPojo.getOtime());
            viewHolder.fromRestau.setText("Restaurant: "+ orderPojo.getRname());
            viewHolder.oidTV.setText("#Order id " + orderPojo.getOid());
            viewHolder.currentStatus.setText(orderPojo.getStatus());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bookingCurrent != null)
                        bookingCurrent.openOrderDetailActivity(bookingPojos.get(position));
                    else if (bookingUpcoming != null)
                        bookingUpcoming.openOrderDetailActivity(bookingPojos.get(position));
                    else
                    if (bookingPrev != null)
                        bookingPrev.openOrderDetailActivity(bookingPojos.get(position));
                }
            });
            Log.i(TAGG, "getView: src ");

            viewHolder.repeat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(bookingPrev!=null)
                    {
                        Log.d("OrderID",bookingPojos.get(position).getOid());
                        new reorder().execute(bookingPojos.get(position).getOid(),bookingPojos.get(position).getRid(),bookingPojos.get(position).getUid());
                    }
                    else
                    {
                        if(bookingPojos.get(position).getStatus().compareTo("Cancelled")!=0)
                        {
                            android.support.v7.app.AlertDialog.Builder ad = new android.support.v7.app.AlertDialog.Builder(context);
                            ad.setTitle("Want to Cancel the Order?");
                            ad.setMessage("Are you sure you want to cancel this Order?");
                            ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    String dt=new SimpleDateFormat("yyyy/MM/dd").format(new Date().getTime());
                                    pos=position;
                                    new cnorder().execute(bookingPojos.get(position).getOid(),dt);
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
                        else
                        {
                            Toast.makeText(context, "Order is Already Cancelled!", Toast.LENGTH_SHORT).show();
                        }

                    }

                }
            });

        }   //[setText]
        return convertView;
    }

    private static class VHolder {
        public TextView currentStatus;
        private TextView priceTV, dateTimeTV, fromRestau, oidTV;
        private ImageView repeat;
    }

    private class reorder extends AsyncTask<String, JSONObject,String>
    {
        String fid="";
        String rid="";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dailog();
        }

        @Override
        protected String doInBackground(String... params) {
            fid=params[0];
            rid=params[1];
            String a="back";
            RestAPI api=new RestAPI();
            try {
                JSONObject json=api.Ureorder(params[0],params[1],params[2]);
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
                AlertDialog.Builder ad = new AlertDialog.Builder(context);
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
                        android.support.v7.app.AlertDialog.Builder ad = new android.support.v7.app.AlertDialog.Builder(context);
                        ad.setTitle("Order is Added to the Cart");
                        ad.setMessage("Open Restaurant details to check the Cart?");
                        ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                new restdetails().execute(rid);
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
                    else  if (ans.compareTo("false") == 0) {
                        Alert("Coudnt Repeat the Order!","The Restuarant is no Longer Open or is Closed for the Day!");
                    }
                    else if (ans.compareTo("no") == 0) {
                        Alert("Coudnt Repeat the Order!","One or more Food Items are not Available");
                    }else if (ans.compareTo("error") == 0) {
                        String error = json.getString("Data");
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, ans, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(context, "catch - " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private class cnorder extends AsyncTask<String, JSONObject,String>
    {
        String fid="";
        String rid="";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dailog();
        }

        @Override
        protected String doInBackground(String... params) {
            fid=params[0];
            rid=params[1];
            String a="back";
            RestAPI api=new RestAPI();
            try {
                JSONObject json=api.UcancelOrder(params[0],params[1]);
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
                AlertDialog.Builder ad = new AlertDialog.Builder(context);
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
                        bookingPojos.get(pos).setStatus("Cancelled");
                        notifyDataSetChanged();
                        Alert("Order is Cancelled!","Your Order is Cancelled.");
                    }
                    else  if (ans.compareTo("false") == 0) {
                        Alert("Cannot Cancel!","Your Order is Already Processed ! Contact the Restaurant for more Details.");
                    }
                    else if (ans.compareTo("no") == 0) {
                        Alert("Order doesnt Exits!","Contact the Restaurant");
                    }else if (ans.compareTo("error") == 0) {
                        String error = json.getString("Data");
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, ans, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(context, "catch - " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void dailog()
    {
        cd=new Dialog(context,R.style.AppTheme);
        cd.requestWindowFeature(Window.FEATURE_NO_TITLE);
        cd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        cd.setContentView(R.layout.loading);
        cd.setCancelable(false);
        cd.show();
    }

    public void Alert(String title,String mesg)
    {
        AlertDialog.Builder ad = new AlertDialog.Builder(context);
        ad.setTitle(title);
        ad.setMessage(mesg);
        ad.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        ad.show();
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
                android.support.v7.app.AlertDialog.Builder ad = new android.support.v7.app.AlertDialog.Builder(context);
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
                        Toast.makeText(context, "This Restaurant doesnt Exist!", Toast.LENGTH_SHORT).show();
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

                        Intent i=new Intent(context, RestaurantDetails.class);
                        context.startActivity(i);

                    } else if (ans.compareTo("error") == 0) {
                        String error = json.getString("Data");
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(context, "catch - " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

        }
    }
}
