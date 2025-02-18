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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.website.yummywheels.AddOrDetailDPersonActivity;
import com.website.yummywheels.R;
import com.website.yummywheels.adapter.DPersonAdap;
import com.website.yummywheels.adapter.SpnAdap;
import com.website.yummywheels.pojo.DPersonPojo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import static com.website.yummywheels.utils.UtilConstants.TAG;

public class DPersonFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private Dialog dialogLoader;
    private String sDate;
    private ArrayList<DPersonPojo> dPersonPojos;
    Calendar cal;
    private TextView foundTV;
    Calendar sCal;
    private SimpleDateFormat sdFormat;
    private ImageView noHistImageView;
    private EditText searchET;
    private boolean firstBool;
    private Button searchBtn;
    private LinearLayout llFrag;
    private ListView listView;
    private Spinner spnViewOrder;
    private DPersonAdap adapter;
    private RelativeLayout rLayoutImage;
    private ArrayList<String> list;
    private static String src = "Mumbai";
    private boolean searched;


    public DPersonFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_dperson, container, false);
        llFrag = rootView.findViewById(R.id.ll_tab);
        noHistImageView = rootView.findViewById(R.id.iv_no_history);
        rLayoutImage = rootView.findViewById(R.id.rl_image);
        listView = rootView.findViewById(R.id.lv_tab);
        searchBtn = rootView.findViewById(R.id.btn_search);
        searchBtn.setOnClickListener(this);
        spnViewOrder = rootView.findViewById(R.id.spn_vieworder);
        searchET = rootView.findViewById(R.id.et_search);
        searchET.setText(src);
        foundTV = rootView.findViewById(R.id.tv_found);
        list = new ArrayList<>();
        list.addAll(Arrays.asList("Please select a filter", "Restaurant", "city", "pincode"));
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
        GetDPersonTask task = new GetDPersonTask();
        task.execute("city", "mumbai");
    }

    public void onPersonClicked(DPersonPojo pojo) {
        Log.i(TAG, "onPersonClicked: " + pojo.getDid());
        Intent intentDetail = new Intent(getActivity(), AddOrDetailDPersonActivity.class);
        intentDetail.putExtra(com.website.yummywheels.utils.UtilConstants.DPERSON_POJO, pojo);
        startActivity(intentDetail);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        searchET.setHint(list.get(position));
        if (firstBool) {
            getData(src);
            firstBool = false;
        }
    }

    private void getData(String src) {
       GetDPersonTask task = new GetDPersonTask();
       if (spnViewOrder.getSelectedItemPosition() == 1) {
           task.execute("name", searchET.getText().toString());
       }else
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
                    searchET.setError("Please enter your search text");
                } else if (spnViewOrder.getSelectedItemPosition() == 0) {
                    Snackbar.make(llFrag, "Please enter Restaurant/City/Pincode",
                            Snackbar.LENGTH_LONG).show();
                } else {
                    getData(src);
                }
                break;
        }
    }

    private class GetDPersonTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startAnimation();
        }

        @Override
        protected String doInBackground(String... strings) {
            String result;
            com.website.yummywheels.webservices.RestAPI restAPI = new com.website.yummywheels.webservices.RestAPI();
            try {
                JSONObject jsonObject = restAPI.getDeliveryP(strings[0], strings[1]);
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
            stopAnimation();
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
                    DPersonPojo dPersonPojo;
                    dPersonPojos = new ArrayList<DPersonPojo>();
                    String ans = json.getString("status");
                    Log.d("reply::", ans);
                    if (ans.compareTo("no") == 0) {
                        listView.setAdapter(null);
                        listView.setVisibility(View.GONE);
                        noHistImageView.setVisibility(View.VISIBLE);
                        foundTV.setText(dPersonPojos.size()+ " Delivery person found.");
                    } else if (ans.compareTo("ok") == 0) {
                        JSONArray array = json.getJSONArray("Data");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject jsonObject = array.getJSONObject(i);
                            dPersonPojo = new DPersonPojo(jsonObject.getString("data0"), jsonObject.getString("data1"), jsonObject.getString("data2"),
                                    jsonObject.getString("data3"), jsonObject.getString("data4"), jsonObject.getString("data5"),
                                    jsonObject.getString("data6"),
                                    jsonObject.getString("data7"));
                            dPersonPojos.add(dPersonPojo);
                        }
                        //setLV
                        setListView();
                        foundTV.setText(dPersonPojos.size()+ " Delivery man found.");
                    } else if (ans.compareTo("error") == 0) {
                        String error = json.getString("Data");
                        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void setListView() {
        if (dPersonPojos.size() > 0) {
            adapter = new DPersonAdap(getContext(), dPersonPojos,
                    this);
            listView.setAdapter(adapter);
            listView.setVisibility(View.VISIBLE);
            rLayoutImage.setVisibility(View.GONE);
        } else {
            listView.setAdapter(null);
            listView.setVisibility(View.GONE);
            rLayoutImage.setVisibility(View.VISIBLE);
        }
        Log.i(TAG, "count search: " + dPersonPojos.size());
    }

    private void stopAnimation() {
        if (dialogLoader.isShowing())
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
