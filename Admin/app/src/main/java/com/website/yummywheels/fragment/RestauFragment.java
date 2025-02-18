package com.website.yummywheels.fragment;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.website.yummywheels.AddOrDetailRestauActivity;
import com.website.yummywheels.R;
import com.website.yummywheels.adapter.RestauAdap;
import com.website.yummywheels.adapter.SpnAdap;
import com.website.yummywheels.pojo.RestaurantPojo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import static com.website.yummywheels.utils.UtilConstants.TAG;


public class RestauFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    private EditText searchET;
    private boolean firstBool;
    private Button searchBtn;
    private Dialog dialogLoader;
    private LinearLayout llFrag;
    private ListView listView;
    private ArrayList<RestaurantPojo> restaurantPojos;
    private Spinner spnViewOrder;
    private RestauAdap adapter;
    private ArrayList<String> list;
    private static String src = "Mumbai";
    private boolean searched;
    private TextView foundTV;

    public RestauFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_restau, container, false);
        llFrag = rootView.findViewById(R.id.cl_frag);
        listView = rootView.findViewById(R.id.lv);
        searchBtn = rootView.findViewById(R.id.btn_search);
        searchBtn.setOnClickListener(this);
        spnViewOrder = rootView.findViewById(R.id.spn_vieworder);
        searchET = rootView.findViewById(R.id.et_search);
        searchET.setText(src);
        foundTV = rootView.findViewById(R.id.tv_found);
        list = new ArrayList<String>();
        list.addAll(Arrays.asList("Please select a filter", "name", "city", "pincode"));
        spnViewOrder.setAdapter(new SpnAdap(getContext(), list));
        spnViewOrder.setOnItemSelectedListener(this);
        spnViewOrder.setSelection(2);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (searched)
            getData(src);
        else
            getData();
    }

    private void getData() {
        GetRestauTask task = new GetRestauTask();
        task.execute("city", "mumbai");
    }


    public void onFoodItemClicked(RestaurantPojo pojo) {
        Log.i(TAG, "onPersonClicked: " + pojo.getRid());
        Intent intentDetail = new Intent(getActivity(), AddOrDetailRestauActivity.class);
        intentDetail.putExtra(com.website.yummywheels.utils.UtilConstants.RESTAU_ID, pojo);
        startActivity(intentDetail);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        searchET.setHint(list.get(position));
        if (!firstBool) {
            getData(src);
            firstBool = true;
        }
    }

    private void getData(String src) {
        GetRestauTask task = new GetRestauTask();
        task.execute(list.get(spnViewOrder.getSelectedItemPosition()), searchET.getText().toString());
        searched = true;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_search:
                if (searchET.getText().toString().isEmpty()) {
                    searchET.setError("Please enter selection");
                } else if (spnViewOrder.getSelectedItemPosition() == 0) {
                    Snackbar.make(llFrag, "Please enter Restaurant/City/Pincode",
                            Snackbar.LENGTH_LONG).show();
                } else {
                    getData(src);
                }
                break;
        }
    }

    private class GetRestauTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //startAnimation();
        }

        @Override
        protected String doInBackground(String... strings) {
            String result;
            com.website.yummywheels.webservices.RestAPI restAPI = new com.website.yummywheels.webservices.RestAPI();
            try {
                Log.i(TAG, "doInBackground: "+strings[0]+ strings[1]);
                JSONObject jsonObject = restAPI.getRestaurant(strings[0], strings[1]);
                com.website.yummywheels.webservices.JSONParser jsonParser = new com.website.yummywheels.webservices.JSONParser();
                result = jsonParser.parseJSON(jsonObject);
            } catch (Exception e) {
                result = e.getMessage();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("reply getRestau ", s);
            if (s.contains("Unable to resolve host")) {
                AlertDialog.Builder ad = new AlertDialog.Builder(getContext());
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
                    RestaurantPojo restaurantPojo;
                    restaurantPojos = new ArrayList<RestaurantPojo>();
                    String ans = json.getString("status");
                    Log.d("reply::", ans);
                    if (ans.compareTo("no") == 0) {
                        Snackbar.make(llFrag, "No restaurant found", Snackbar.LENGTH_SHORT).show();
                        listView.setAdapter(null);
                        foundTV.setText(restaurantPojos.size() + " Restaurant found.");
                    } else if (ans.compareTo("ok") == 0) {
                        JSONArray array = json.getJSONArray("Data");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject jsonObject = array.getJSONObject(i);
                            restaurantPojo = new RestaurantPojo(jsonObject.getString("data0"), jsonObject.getString("data1"), jsonObject.getString("data2"),
                                    jsonObject.getString("data3"), jsonObject.getString("data4"), jsonObject.getString("data5"),
                                    jsonObject.getString("data6"),
                                    jsonObject.getString("data7"),
                                    jsonObject.getString("data8"),
                                    jsonObject.getString("data9"),
                                    jsonObject.getString("data10"),
                                    jsonObject.getString("data11"),
                                    jsonObject.getString("data12"),
                                    jsonObject.getString("data13"));
                            restaurantPojos.add(restaurantPojo);
                        }
                        //setLV
                        setListView();
                        foundTV.setText(restaurantPojos.size() + " Restaurant found.");
                    } else if (ans.compareTo("error") == 0) {
                        String error = json.getString("Data");
                        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    stopAnimation();
                    Toast.makeText(getContext(), "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            stopAnimation();
        }
    }

    private void setListView() {
        if (restaurantPojos.size() > 0) {
            adapter = new RestauAdap(getContext(), restaurantPojos,
                    RestauFragment.this);
            listView.setAdapter(adapter);
        } else {
            listView.setAdapter(null);
        }
        Log.i(TAG, "count search: " + restaurantPojos.size());
    }

    private void stopAnimation() {
        if (dialogLoader != null && dialogLoader.isShowing())
            dialogLoader.cancel();
    }

    private void startAnimation() {
        dialogLoader = new Dialog(getContext(), R.style.AppTheme_NoActionBar);
        dialogLoader.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#8D000000")));
        final View view = getActivity().getLayoutInflater().inflate(R.layout.custom_dialog_loader, null);
        LottieAnimationView animationView = view.findViewById(R.id.loader);
        animationView.playAnimation();
        dialogLoader.setContentView(view);
        dialogLoader.setCancelable(false);
        dialogLoader.show();
    }
}
