package com.website.yummywheels.fragment;


import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.website.yummywheels.OrderDetailActivity;
import com.website.yummywheels.R;
import com.website.yummywheels.adapter.OrderAdap;
import com.website.yummywheels.adapter.SpnAdap;
import com.website.yummywheels.pojo.OrderPojo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.website.yummywheels.utils.UtilConstants.ORDER_KEY;
import static com.website.yummywheels.utils.UtilConstants.TAG;

public class OrderedFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private Dialog dialogLoader;
    private com.website.yummywheels.pojo.OrderPojo orderPojo;
    private LinearLayout llTab;
    private String eDate, sDate;
    private ListView listView;
    private ArrayList<OrderPojo> orderPojos;
    Calendar cal;
    private TextView dateTVStart, dateTVEnd, foundTV;
    private Spinner spnViewOrder;
    private String currentTime, currentDate;
    private SimpleDateFormat sdFormat;
    private ImageView dateFromBtn, dateToBtn;
    private OrderAdap orderAdap;
    private ArrayList<String> list;
    private static String src = "Mumbai";
    private ImageView noHistImageView;
    Calendar sCal, eCal;
    private SimpleDateFormat simpleFormat;
    private SimpleDateFormat sdtimeFormat;

    private EditText searchET;
    private boolean firstBool;
    private Button searchBtn;
    private DatePickerDialog pickerDialog;
    private DatePickerDialog pickerDialogE;

    public OrderedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_orders, container, false);
        llTab = rootView.findViewById(R.id.ll_tab);
        dateFromBtn = rootView.findViewById(R.id.btn_datepick_from);
        dateToBtn = rootView.findViewById(R.id.btn_datepick_to);
        searchBtn = rootView.findViewById(R.id.btn_search);
        searchBtn.setOnClickListener(this);
        spnViewOrder = rootView.findViewById(R.id.spn_vieworder);
        searchET = rootView.findViewById(R.id.et_search);
        searchET.setText(src);
        dateToBtn.setOnClickListener(this);
        dateFromBtn.setOnClickListener(this);
        noHistImageView = rootView.findViewById(R.id.iv_no_history);
        dateTVStart = rootView.findViewById(R.id.tv_date_from);
        dateTVEnd = rootView.findViewById(R.id.tv_date_to);
        foundTV = rootView.findViewById(R.id.tv_found);
        listView = rootView.findViewById(R.id.lv_tab);
        initDataObj();
        spnViewOrder.setAdapter(new SpnAdap(getContext(), list));
        spnViewOrder.setOnItemSelectedListener(this);
        spnViewOrder.setSelection(2);
        orderPojos = new ArrayList<>();
        return rootView;
    }

    @Override
    public void onResume() {
        firstBool = true;
        super.onResume();
    }

    private void initDataObj() {
        list = new ArrayList<String>();
        list.addAll(Arrays.asList("Please select a filter", "Restaurant", "city", "pincode"));
        cal = Calendar.getInstance();
        sCal = Calendar.getInstance();
        eCal = Calendar.getInstance();
        Date date = cal.getTime();
        simpleFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
        sdFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
        eDate = sDate = simpleFormat.format(date);
        dateTVEnd.setText("To: " + eDate);
        dateTVStart.setText("From: " + sDate);
    }

    private void getSharedHist(String src) {
        getDateTime();
        if (sDate != null && eDate != null) {
            ViewPrevOrderTask task = new ViewPrevOrderTask();
            task.execute(list.get(spnViewOrder.getSelectedItemPosition()), searchET.getText().toString(), sDate, eDate);
            Log.d(TAG, src + searchET.getText().toString() + sDate + ": sDate, " + eDate + ": eDate" + " , src" + src);
        }
    }

    private void eDatePicker() {
        pickerDialogE = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                eCal.set(Calendar.YEAR, year);
                eCal.set(Calendar.MONTH, month);
                eCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                eDate = sdFormat.format(eCal.getTime());
                //dateEnd = eDate;
                dateTVEnd.setText("To: " + eDate);
                getSharedHist(src);
            }
        }, eCal.get(Calendar.YEAR), eCal.get(Calendar.MONTH), eCal.get(Calendar.DAY_OF_MONTH));
        pickerDialogE.getDatePicker().setMaxDate(new Date().getTime());
        pickerDialogE.getDatePicker().setMinDate(sCal.getTimeInMillis());
        //if (!pickerDialogE.isShowing()) {
        pickerDialogE.show();
        //}
    }

    private void sDatePicker() {
        pickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                sCal.set(Calendar.YEAR, year);
                sCal.set(Calendar.MONTH, month);
                sCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                sDate = sdFormat.format(sCal.getTime());
                //dateStart = sDate;
                dateTVStart.setText("From: " + sDate);
                getSharedHist(src);
            }
        }, sCal.get(Calendar.YEAR), sCal.get(Calendar.MONTH), sCal.get(Calendar.DAY_OF_MONTH));
        pickerDialog.getDatePicker().setMaxDate(new Date().getTime());
        //if (!pickerDialog.isShowing()) {
        pickerDialog.show();
        //}
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_datepick_from:
                if (pickerDialog != null) {
                    if (!pickerDialog.isShowing()) {
                        sDatePicker();
                    }
                }else {
                    sDatePicker();
                }
                break;
            case R.id.btn_datepick_to:
                if (pickerDialogE != null) {
                    if (!pickerDialogE.isShowing()) {
                        eDatePicker();
                    }
                } else eDatePicker();

                break;
            case R.id.btn_search:
                if (searchET.getText().toString().isEmpty()) {
                    searchET.setError("Please enter " + list.get(spnViewOrder.getSelectedItemPosition()));
                } else if (spnViewOrder.getSelectedItemPosition() == 0) {
                    Snackbar.make(llTab, "Please enter Restaurant/City/Pincode",
                            Snackbar.LENGTH_LONG).show();
                } else {
                    getSharedHist(src);
                }
                break;
        }
    }

    private void getDateTime() {
        cal = Calendar.getInstance();
        Date date = cal.getTime();
        sdtimeFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
        currentTime = sdFormat.format(date);
        currentDate = simpleFormat.format(date);
    }

    public void openOrderDetailActivity(com.website.yummywheels.pojo.OrderPojo orderPojo) {
        Intent intentDetail = new Intent(getContext(), OrderDetailActivity.class);
        //intentDetail.putExtra(CHANGE_CURR_STATUS_KEY, false);
        intentDetail.putExtra(ORDER_KEY, orderPojo);
        startActivity(intentDetail);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        searchET.setHint(list.get(position));
        if (firstBool) {
            getSharedHist(src);
            firstBool = false;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    private class ViewPrevOrderTask extends AsyncTask<String, Void, String> {
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
                JSONObject jsonObject = restAPI.viewOrders(strings[0], strings[1], strings[2], strings[3]);
                Log.i(TAG, "doInBackground: " + strings[0] + strings[1] + strings[2] + strings[3]);
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
            Log.d("reply ViewPrevOrderTask", s + "src: : " + src);
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
                    if (orderPojos.size() > 0)
                        orderPojos.clear();
                    JSONObject json = new JSONObject(s);
                    String ans = json.getString("status");
                    if (ans.compareTo("ok") == 0) {
                        JSONArray jarry = json.getJSONArray("Data");
                        Log.d(TAG, jarry.length() + " : main arr");
                        for (int i = 0; i < jarry.length(); i++) {
                            JSONObject jobj = jarry.getJSONObject(i);
                            orderPojo = new com.website.yummywheels.pojo.OrderPojo
                                    (jobj.getString("data0"),
                                            jobj.getString("data1"),
                                            jobj.getString("data2"), jobj.getString("data3"), jobj.getString("data4"), jobj.getString("data5"),
                                            jobj.getString("data6"), jobj.getString("data7"), jobj.getString("data8"), jobj.getString("data9"),
                                            jobj.getString("data10"), jobj.getString("data11"), jobj.getString("data12"),
                                            jobj.getString("data13"), jobj.getString("data14"), jobj.getString("data15"),
                                            jobj.getString("data16"), jobj.getString("data17"));
                            orderPojos.add(orderPojo);
                        }
                        setLV();
                        foundTV.setText(orderPojos.size() + " Orders found.");
                    } else if (ans.compareTo("no") == 0) {
                        listView.setAdapter(null);
                        listView.setVisibility(View.GONE);
                        noHistImageView.setVisibility(View.VISIBLE);
                        foundTV.setText(orderPojos.size() + " Orders found.");
                    } else if (ans.compareTo("error") == 0) {
                        String error = json.getString("Data");
                        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    private void setLV() {
        orderAdap = new OrderAdap(getContext(), orderPojos, src);
        listView.setAdapter(orderAdap);
        orderAdap.setListenerCurr(this);
        noHistImageView.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
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
