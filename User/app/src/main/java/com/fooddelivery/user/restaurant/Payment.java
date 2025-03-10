package com.fooddelivery.user.restaurant;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.fooddelivery.user.Activity.MainActivity;
import com.fooddelivery.user.Activity.PlaceSelection;
import com.fooddelivery.user.Activity.Register;
import com.fooddelivery.user.R;
import com.fooddelivery.user.helper.SavedData;
import com.fooddelivery.user.helper.UserPref;
import com.fooddelivery.user.pojo.restpojo;
import com.fooddelivery.user.webservice.JSONParse;
import com.fooddelivery.user.webservice.RestAPI;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Time;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by hp on 1/10/2018.
 */

public class Payment extends AppCompatActivity{

    String uid="",Rid="";
    Dialog cd;

    TextView price;
    RadioGroup rg;
    CardView paycard;
    EditText name,cno,year,mon,cvv;
    EditText textboxes[];
    String mesg[]=new String[]{"Name on card","16 digit Card Number","Year ( YYYY )","Month ( MM )","3 digit CVV number"};
    Button placeorder;
    String Pstatus="";
    ArrayList<String> CTid,Cfid,Cfname,Cprice,Cquant,CFprice;
    ScrollView scroll;
    EditText address,city,pincode,cord,contact;
    EditText[] textboxes1;
    String[] mesg1=new String[]{"Address","City","Pincode","Cordinates","Contact"};

    SimpleDateFormat sdfd=new SimpleDateFormat("yyyy/MM/dd");
    SimpleDateFormat sdft=new SimpleDateFormat("HH:mm");
    TextView date,time;

    DatePickerDialog dated;
    TimePickerDialog timed;

    Calendar Ncald,Ncalt;
    restpojo rp;
    Calendar startcal,endcal;
    ImageView map;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment);
        setActionbar("Order/Payment");
        uid= UserPref.getId(Payment.this);
        checkTime();
        init();
        new getProfile().execute(uid);
    }

    public void init()
    {
        map= (ImageView) findViewById(R.id.map);
        Rid=getIntent().getStringExtra("rid");
        price= (TextView) findViewById(R.id.pprice);
        paycard= (CardView) findViewById(R.id.paycard);
        rg= (RadioGroup) findViewById(R.id.prg);
        name= (EditText) findViewById(R.id.pname);
        cno= (EditText) findViewById(R.id.pcno);
        mon= (EditText) findViewById(R.id.pmon);
        year= (EditText) findViewById(R.id.pyear);
        cvv= (EditText) findViewById(R.id.pcvv);
        address= (EditText) findViewById(R.id.padd);
        city= (EditText) findViewById(R.id.pcity);
        pincode= (EditText) findViewById(R.id.ppin);
        cord= (EditText) findViewById(R.id.pcord);
        contact= (EditText) findViewById(R.id.pphone);
        placeorder= (Button) findViewById(R.id.placeorder);
        scroll= (ScrollView) findViewById(R.id.scroll);
        textboxes=new EditText[]{name,cno,year,mon,cvv};

        CTid=getIntent().getStringArrayListExtra("tid");
        Cfid=getIntent().getStringArrayListExtra("fid");
        Cfname=getIntent().getStringArrayListExtra("name");
        Cprice=getIntent().getStringArrayListExtra("price");
        Cquant=getIntent().getStringArrayListExtra("quant");
        CFprice=getIntent().getStringArrayListExtra("fprice");

        date= (TextView) findViewById(R.id.pdate);
        time= (TextView) findViewById(R.id.ptime);

        textboxes1=new EditText[]{address,city,pincode,cord,contact};

        price.setText(getIntent().getStringExtra("tot"));

        Ncald=Calendar.getInstance();
        Ncalt=Calendar.getInstance();
        date.setText(sdfd.format(Ncald.getTime()));
        time.setText(sdft.format(Ncalt.getTime()));

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cord.length()>0)
                {
                    String t[]=cord.getText().toString().split(",");
                    SavedData.setLat(Double.parseDouble(t[0]));
                    SavedData.setLng(Double.parseDouble(t[1]));
                }
                Intent i=new Intent(Payment.this, PlaceSelection.class);
                startActivity(i);
            }
        });

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if(i==R.id.payn)
                {
                    Pstatus="bank";
                    paycard.setVisibility(View.VISIBLE);
                    scroll.fullScroll(View.FOCUS_DOWN);
                    cvv.requestFocus();
                }
                else
                {
                    scroll.fullScroll(View.FOCUS_UP);
                    Pstatus="cod";
                    paycard.setVisibility(View.GONE);
                    name.setText("");cno.setText("");mon.setText("");year.setText("");cvv.setText("");
                }
            }
        });

        placeorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(time.getText().toString().compareTo("Click to Add Time")!=0)
                {
                    boolean ans=true;
                    for(int i=0;i<textboxes1.length;i++)
                    {
                        if(textboxes1[i].length()==0)
                        {
                            ans=false;
                            if(i==3)
                            {
                                Snackbar.make(view,"Select your Location from the Map, Click on the Right Arrow",Snackbar.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Snackbar.make(view,"Enter "+mesg1[i],Snackbar.LENGTH_SHORT).show();
                            }
                            break;
                        }
                        else
                        {
                            if(i==4)
                            {
                                if(textboxes1[i].length()!=10) {
                                    ans = false;
                                    Snackbar.make(view, "Contact Number should have 10 digits", Snackbar.LENGTH_SHORT).show();
                                    break;
                                }
                            }
                        }
                    }

                    if(ans)
                    {
                        if(Pstatus.length()>0)
                        {
                            boolean a=true;
                            if(Pstatus.compareTo("bank")==0)
                            {
                                a=checkFields();
                            }

                            if(a)
                            {
                                String d=sdfd.format(new Date().getTime());
                                String t=sdft.format(new Date().getTime());
                                String dt=date.getText().toString()+" "+time.getText().toString();
                                new PlaceOrder().execute(Rid,uid,address.getText().toString(),city.getText().toString(),pincode.getText().toString(),cord.getText().toString(),
                                    contact.getText().toString(),d,t,dt,Pstatus,getTotal()+"");
                            }
                        }
                        else
                        {
                            Snackbar.make(view, "Choose a Option for Payment", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                }
                else
                {
                    Snackbar.make(view, "Choose Time for Delivery", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CallDate(Ncald.get(Calendar.YEAR),Ncald.get(Calendar.MONTH),Ncald.get(Calendar.DAY_OF_MONTH));
            }
        });

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CallTime(Ncalt.get(Calendar.HOUR_OF_DAY),Ncalt.get(Calendar.MINUTE));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(SavedData.getLat()>0)
        {
            cord.setText(SavedData.getLat()+","+SavedData.getLng());
        }
    }

    public float getTotal()
    {
        int ans=0;
        for(int i=0;i<CFprice.size();i++)
        {
            ans+=Float.parseFloat(CFprice.get(i));
        }
        return ans;
    }

    public void checkTime()
    {
        rp= SavedData.getRp();
        String[] timein;
        String[] timeout;
        String t[]=rp.getTime().split("-");
        timein=t[0].trim().split(":");
        timeout=t[1].trim().split(":");

        startcal=Calendar.getInstance();
        startcal.set(Calendar.HOUR_OF_DAY,Integer.parseInt(timein[0]));
        startcal.set(Calendar.MINUTE,Integer.parseInt(timein[1]));

        endcal=Calendar.getInstance();
        endcal.set(Calendar.HOUR_OF_DAY,Integer.parseInt(timeout[0]));
        endcal.set(Calendar.MINUTE,Integer.parseInt(timeout[1]));

        if(startcal.before(endcal))
        {

        }
        else
        {
            endcal.add(Calendar.DAY_OF_MONTH,1);
        }

        Log.d("start",startcal.getTime().toString());
        Log.d("end",endcal.getTime().toString());
    }

    public void CallDate(int yr,int mon,int dt)
    {
        dated = new DatePickerDialog(Payment.this, new DatePickerDialog.OnDateSetListener()
        {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Ncald = Calendar.getInstance();
                Ncald.set(year, monthOfYear, dayOfMonth);
                date.setText(sdfd.format(Ncald.getTime()));
                time.setText("Click to Add Time");
//                if(Ncald.after(cald) || Ncald.equals(cald))
//                {
//                    date.setText(sdfd.format(Ncald.getTime()));
//                }
//                else
//                {
//                    cald=Calendar.getInstance();
//                    cald.add(Calendar.HOUR,2);
//                    date.setText(sdfd.format(cald.getTime()));
//                    Ncald.setTime(cald.getTime());
//                    Snackbar.make(scroll,"Booking Date/Time Should be more then 2 hours from now!",Snackbar.LENGTH_SHORT).show();
//                }
            }

        },yr, mon, dt);
        dated.getDatePicker().setMinDate(startcal.getTimeInMillis());
        dated.getDatePicker().setMaxDate(endcal.getTimeInMillis());
        dated.show();
    }

    public void CallTime(int hr,int min)
    {
        timed=new TimePickerDialog(Payment.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                Calendar c=Calendar.getInstance();
                c.setTime(Ncald.getTime());
                c.set(Calendar.HOUR_OF_DAY,hourOfDay);
                c.set(Calendar.MINUTE,minute);
                c.set(Calendar.SECOND,0);
                c.set(Calendar.MILLISECOND,0);

                Log.d("current",c.getTime().toString());
                if(c.after(startcal) && c.before(endcal))
                {
                    Ncalt.set(Calendar.HOUR_OF_DAY,hourOfDay);
                    Ncalt.set(Calendar.MINUTE,minute);
                    Ncalt.set(Calendar.SECOND,0);
                    Ncalt.set(Calendar.MILLISECOND,0);
                    time.setText(sdft.format(Ncalt.getTime()));
                }
                else {
                    AlertDialog.Builder ad = new AlertDialog.Builder(Payment.this);
                    ad.setTitle("Sorry Cannot Deliver at that Time");
                    ad.setMessage("The Hotel Delivers between " + sdfd.format(startcal.getTime()) + " " + sdft.format(startcal.getTime()) + " and " +
                            sdfd.format(endcal.getTime()) + " " + sdft.format(endcal.getTime()));
                    ad.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    ad.show();
                }
            }
        },hr,min,true);
        timed.show();
    }

    public boolean checkFields()
    {
        SimpleDateFormat sdfy=new SimpleDateFormat("yyyy");
        SimpleDateFormat sdfm=new SimpleDateFormat("MM");
        Integer YEAR= Integer.parseInt(sdfy.format(new Date().getTime()));
        Integer MONTH=Integer.parseInt(sdfm.format(new Date().getTime()));

        boolean ans=true;

        for(int i=0;i<textboxes.length;i++)
        {
            if(textboxes[i].length()==0)
            {
                Snackbar.make(price,"Enter "+mesg[i],Snackbar.LENGTH_SHORT).show();
                return false;
            }
            else
            {
                if(i==1)
                {
                    if(textboxes[i].length()!=16)
                    {
                        Snackbar.make(price,"card no Should have 16 Digits",Snackbar.LENGTH_SHORT).show();
                        return false;
                    }
                }

                if(i==4)
                {
                    if(textboxes[i].length()!=3)
                    {
                        Snackbar.make(price,"CVV Should have 3 Digits",Snackbar.LENGTH_SHORT).show();
                        return false;
                    }
                }

                if(i==2)
                {
                    if(textboxes[i].length()!=4)
                    {
                        Snackbar.make(price,"Year Should be in YYYY format",Snackbar.LENGTH_SHORT).show();
                        return false;
                    }
                    else
                    {
                        int y=Integer.parseInt(year.getText().toString());
                        if(y<YEAR)
                        {
                            Snackbar.make(price,"Your Card is Expired",Snackbar.LENGTH_SHORT).show();
                            return false;
                        }
                    }
                }

                if(i==3)
                {
                    if(textboxes[i].length()!=2)
                    {
                        Snackbar.make(price,"Month Should be in MM format",Snackbar.LENGTH_SHORT).show();
                        return false;
                    }
                    else
                    {
                        int y=Integer.parseInt(year.getText().toString());
                        int m=Integer.parseInt(mon.getText().toString());
                        try {
                            if (m>0 && m<=12) {

                                if(y==YEAR)
                                {
                                    if(m<MONTH)
                                    {
                                        Snackbar.make(price,"Your Card is Expired",Snackbar.LENGTH_SHORT).show();
                                        return false;
                                    }
                                }

                            }
                            else {

                                Snackbar.make(price, "Invalid Month", Snackbar.LENGTH_SHORT).show();
                                return false;

                            }
                        }
                        catch (Exception e){}
                    }
                }
            }

        }

        return ans;
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

    public void dailog()
    {
        cd=new Dialog(Payment.this,R.style.AppTheme);
        cd.requestWindowFeature(Window.FEATURE_NO_TITLE);
        cd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        cd.setContentView(R.layout.loading);
        cd.setCancelable(false);
        cd.show();
    }

    private class PlaceOrder extends AsyncTask<String,JSONObject,String> {
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
                JSONObject json = api.Uplaceorder(params[0], params[1], params[2], params[3], params[4], params[5], params[6], params[7], params[8], params[9], params[10],
                        Cfid, Cfname, Cquant, CFprice, params[11]);
                JSONParse jp = new JSONParse();
                a = jp.parse(json);
            } catch (Exception e) {
                a = e.getMessage();
            }
            return a;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            cd.cancel();
            Log.d("RESPONSE", s);
            if (s.contains("Unable to resolve host")) {
                AlertDialog.Builder ad = new AlertDialog.Builder(Payment.this);
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
                        AlertDialog.Builder ad = new AlertDialog.Builder(Payment.this);
                        ad.setTitle("Order Placed!");
                        ad.setCancelable(false);
                        ad.setMessage("Your order was Successfully placed, you can check your order status under Orders.");
                        ad.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                Intent intent = new Intent(Payment.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
                        ad.show();
                    } else if (ans.compareTo("error") == 0) {
                        String error = json.getString("Data");
                        Toast.makeText(Payment.this, error, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(Payment.this, "catch - " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    private class getProfile extends AsyncTask<String,JSONObject,String>
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
                JSONObject json=api.UgetProfile(params[0]);
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
                AlertDialog.Builder ad = new AlertDialog.Builder(Payment.this);
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
                try {
                    JSONObject json = new JSONObject(s);
                    String ans = json.getString("status");
                    if (ans.compareTo("no") == 0) {
                        AlertDialog.Builder ad = new AlertDialog.Builder(Payment.this);
                        ad.setTitle("NO Profile Found!");
                        ad.setMessage("Contact Admin");
                        ad.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        ad.show();
                    } else if (ans.compareTo("ok") == 0) {

                        JSONArray jarry = json.getJSONArray("Data");
                        JSONObject jobj = jarry.getJSONObject(0);
                        //uid,name,contact,email,city,add,pincode,latlng
                        address.setText(jobj.getString("data5"));
                        city.setText(jobj.getString("data4"));
                        pincode.setText(jobj.getString("data6"));
                        cord.setText(jobj.getString("data7"));
                        contact.setText(jobj.getString("data2"));

                    } else if (ans.compareTo("error") == 0) {
                        String error = json.getString("Data");
                        Toast.makeText(Payment.this, error, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(Payment.this, "catch - " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}
