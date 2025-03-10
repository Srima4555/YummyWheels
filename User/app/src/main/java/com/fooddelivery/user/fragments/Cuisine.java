package com.fooddelivery.user.fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.fooddelivery.user.R;
import com.fooddelivery.user.restaurant.RestaurantList;
import com.fooddelivery.user.helper.UserPref;
import com.fooddelivery.user.webservice.JSONParse;
import com.fooddelivery.user.webservice.RestAPI;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import org.json.JSONArray;
import org.json.JSONObject;

public class Cuisine extends Fragment {

    String fcname="";
    int selection=0;
    String city="";
    String pincode="";
    boolean isgps=false;
    String latlng="0.0";
    CardView Indian,Afghani,American,BBQ,British,Cafe,Chinese,European,French,Italian,Korean,Mexican,Spanish,Others;

    CardView[] cards;
    String cname[]=new String[]{"Indian","Afghani","American","BBQ","British","Cafe","Chinese","European","French",
            "Italian","Korean","Mexican","Spanish","Others"};

    SharedPreferences sp;
    Dialog cd;
    String uid="";
    boolean check=false;
    protected GoogleApiClient mGoogleApiClient;
    final int REQUEST_CHECK_SETTINGS = 100;
    com.fooddelivery.user.helper.LocationServices ls;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.cuisine,container,false);
        uid= UserPref.getId(getActivity());
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();

        Indian=(CardView)v.findViewById(R.id.indian);
        Afghani=(CardView)v.findViewById(R.id.afgani);
        American=(CardView)v.findViewById(R.id.american);
        BBQ=(CardView)v.findViewById(R.id.bbq);
        British=(CardView)v.findViewById(R.id.british);
        Cafe=(CardView)v.findViewById(R.id.cafe);
        Chinese=(CardView)v.findViewById(R.id.chinese);
        European=(CardView)v.findViewById(R.id.european);
        French=(CardView)v.findViewById(R.id.french);
        Italian=(CardView)v.findViewById(R.id.italian);
        Korean=(CardView)v.findViewById(R.id.korean);
        Mexican=(CardView)v.findViewById(R.id.mexican);
        Spanish=(CardView)v.findViewById(R.id.spanish);
        Others=(CardView)v.findViewById(R.id.others);
        cards=new CardView[]{Indian,Afghani,American,BBQ,British,Cafe,Chinese,European,French,Italian,Korean,Mexican,Spanish,Others};

        for(int i=0;i<cards.length;i++)
        {
            cardonClick(cards[i],i);
        }

        new getProfile().execute(uid);

        return v;
    }

    public void cardonClick(CardView card,final int pos)
    {
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fcname=cname[pos];
                if(!weHavePermission())
                {
                    requestforPermissionFirst();
                }
                else
                {
                    check=true;
                    showSettingDialog();
                }
            }
        });
    }

    public void CDialog()
    {
        selection=0;
        Dialog d=new Dialog(getActivity());
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.cusinedialog);
        CardView zipcard=(CardView)d.findViewById(R.id.zipcard);
        CardView citycard=(CardView)d.findViewById(R.id.citycard);
        CardView nearcard=(CardView)d.findViewById(R.id.nearcard);
        final LinearLayout ziplin=(LinearLayout)d.findViewById(R.id.ziplin);
        final LinearLayout citylin=(LinearLayout)d.findViewById(R.id.citylin);
        final LinearLayout nearlin=(LinearLayout)d.findViewById(R.id.nearlin);
        final TableRow edittb=(TableRow)d.findViewById(R.id.edittb);
        final EditText editvalue=(EditText)d.findViewById(R.id.editvalue);
        final TableRow cdproceedtb=(TableRow)d.findViewById(R.id.cdproceedtb);
        final Button cdproceed=(Button)d.findViewById(R.id.cdproceed);

        zipcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selection=1;
                ziplin.setBackground(getResources().getDrawable(R.drawable.edittext_back));
                citylin.setBackgroundColor(Color.TRANSPARENT);
                nearlin.setBackgroundColor(Color.TRANSPARENT);
                edittb.setVisibility(View.VISIBLE);
                editvalue.setText(pincode);
                editvalue.setHint("Enter Zipcode");
                editvalue.setSelection(editvalue.length());
                cdproceedtb.setVisibility(View.VISIBLE);
            }
        });

        citycard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selection=2;
                ziplin.setBackgroundColor(Color.TRANSPARENT);
                citylin.setBackground(getResources().getDrawable(R.drawable.edittext_back));
                nearlin.setBackgroundColor(Color.TRANSPARENT);
                edittb.setVisibility(View.VISIBLE);
                editvalue.setText(city);
                editvalue.setHint("Enter City");
                editvalue.setSelection(editvalue.length());
                cdproceedtb.setVisibility(View.VISIBLE);
            }
        });

        nearcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selection=3;
                ziplin.setBackgroundColor(Color.TRANSPARENT);
                citylin.setBackgroundColor(Color.TRANSPARENT);
                nearlin.setBackground(getResources().getDrawable(R.drawable.edittext_back));
                edittb.setVisibility(View.GONE);
                editvalue.setText(latlng);
                cdproceedtb.setVisibility(View.VISIBLE);
            }
        });

        cdproceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean ans=true;
                if(selection==1 || selection==2)
                {
                    if(editvalue.length()==0)
                    {
                        ans=false;
                        if(selection==1)
                        {
                            Snackbar.make(view,"Enter Zipcode",Snackbar.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Snackbar.make(view,"Enter City",Snackbar.LENGTH_SHORT).show();
                        }

                    }
                }

                if(ans)
                {
                    Intent i=new Intent(getActivity(), RestaurantList.class);
                    i.putExtra("latlng",latlng);
                    i.putExtra("cuisine",fcname);
                    i.putExtra("selection",selection);
                    i.putExtra("value",editvalue.getText().toString());
                    startActivity(i);
                }
            }
        });

        d.show();
    }

    private boolean weHavePermission()
    {
        return  (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestforPermissionFirst()
    {
        if ((ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)) && (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)))
        {
            requestForResultPermission();
        }
        else
        {
            requestForResultPermission();
        }
    }

    private void requestForResultPermission()
    {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION}, 111);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==111)
        {
            Toast.makeText(getActivity(), "permission granted", Toast.LENGTH_SHORT).show();
            int totpermission=0;
            for(int i=0;i<grantResults.length;i++)
            {
                totpermission+=grantResults[i];
            }

            if(totpermission==0)
            {
                showSettingDialog();
            }
            else
            {
                requestforPermissionFirst();
            }
        }
    }

    private void showSettingDialog() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setFastestInterval(1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
//                        Toast.makeText(getActivity(), "true", Toast.LENGTH_SHORT).show();
                        isgps=true;
                        ls=new com.fooddelivery.user.helper.LocationServices(getContext());
                        Toast.makeText(getActivity(), "Please Wait, determining your Location!", Toast.LENGTH_SHORT).show();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
//                        Toast.makeText(getActivity(), "false", Toast.LENGTH_SHORT).show();
                        isgps=false;
                        try {
                            status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                        break;

                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
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

    private class getProfile extends AsyncTask<String,JSONObject,String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            check=false;
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
                try {
                    JSONObject json = new JSONObject(s);
                    String ans = json.getString("status");
                    if (ans.compareTo("no") == 0) {
                        AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
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
                        city=jobj.getString("data4");
                        pincode=jobj.getString("data6");

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


    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(message, new IntentFilter("gps"));
    }

    private BroadcastReceiver message = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            String ans=intent.getStringExtra("ans");
            String ll=intent.getStringExtra("ll");

            try
            {
                if(!isgps)
                {
                    if(ans.compareTo("yes")==0)
                    {
                        isgps=true;
                        ls=new com.fooddelivery.user.helper.LocationServices(getActivity());
                        Toast.makeText(getActivity(), "Please Wait, determining your Location!", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        showSettingDialog();
                    }
                }
                else
                {
                    if(ll.length()>6)
                    {
                        latlng=ll;
                        CDialog();
                        ls.Stopupdates();
                    }
                }
            }
            catch (Exception e)
            {

            }

        }
    };

    @Override
    public void onPause() {
        super.onPause();
        super.onPause();
        try {
            getActivity().unregisterReceiver(message);
        }
        catch (Exception e){}
    }
}
