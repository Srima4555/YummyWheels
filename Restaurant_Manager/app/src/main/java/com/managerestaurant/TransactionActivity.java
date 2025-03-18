package com.managerestaurant;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TransactionActivity extends AppCompatActivity {
    private String rID;
    private Dialog dialogLoader;
    private RelativeLayout rlLayout;
    private ArrayList<TransacPojo> transacPojos;
    private ListView listView;
    private TransacAdap adapter;
    private RelativeLayout imageNotFound;
    private String sDate;
    Calendar sCal, cal;
    private SimpleDateFormat simpleFormat;
    private String src;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transac);
        Toolbar toolbar = findViewById(R.id.toolbar_add_food);
        toolbar.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_material);
        setSupportActionBar(toolbar);
        initDataObj();
        rID = UserPref.getRid(TransactionActivity.this);
        initUI();
        getData();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initUI() {
        rlLayout = findViewById(R.id.rl_menu);
        imageNotFound = findViewById(R.id.rl_image);
        listView = findViewById(R.id.lv);
    }

    private void getData() {
        GetMgrTask task = new GetMgrTask();
        task.execute();
    }

    private void stopAnimation() {
        if (dialogLoader.isShowing())
            dialogLoader.cancel();
    }

    private void startAnimation() {
        dialogLoader = new Dialog(this, R.style.AppTheme_NoActionBar);
        dialogLoader.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#8D000000")));
        final View view = getLayoutInflater().inflate(R.layout.loading, null);
        LottieAnimationView animationView = view.findViewById(R.id.lottie);
        animationView.playAnimation();
        dialogLoader.setContentView(view);
        dialogLoader.setCancelable(false);
        dialogLoader.show();
    }

    private class GetMgrTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startAnimation();
        }

        @Override
        protected String doInBackground(String... strings) {
            String result;
            RestAPI restAPI = new RestAPI();
            try {
                JSONObject jsonObject = restAPI.MgetTransaction("",sDate, rID);
                Log.i("TAG", "doInBackground: " + ""+sDate+ rID);
                JSONParse jsonParser = new JSONParse();
                result = jsonParser.parse(jsonObject);
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
                AlertDialog.Builder ad = new AlertDialog.Builder(TransactionActivity.this);
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
                    TransacPojo pojo;
                    transacPojos = new ArrayList<TransacPojo>();
                    String ans = json.getString("status");
                    Log.d("reply::", ans);
                    if (ans.compareTo("no") == 0) {
                        Snackbar.make(rlLayout, "No transaction record found", Snackbar.LENGTH_SHORT).show();
                        listView.setAdapter(null);
                        imageNotFound.setVisibility(View.VISIBLE);
                    } else if (ans.compareTo("ok") == 0) {
                        JSONArray array = json.getJSONArray("Data");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject jsonObject = array.getJSONObject(i);
                            pojo = new TransacPojo(jsonObject.getString("data0"), jsonObject.getString("data1"), jsonObject.getString("data2"),
                                    jsonObject.getString("data3"));
                            transacPojos.add(pojo);
                        }
                        //setLV
                        setListView();
                    } else if (ans.compareTo("error") == 0) {
                        String error = json.getString("Data");
                        Toast.makeText(TransactionActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(TransactionActivity.this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void setListView() {
        if (transacPojos.size() > 0) {
            adapter = new TransacAdap(TransactionActivity.this, transacPojos);
            listView.setAdapter(adapter);
            imageNotFound.setVisibility(View.GONE);
        } else {
            listView.setAdapter(null);
            imageNotFound.setVisibility(View.VISIBLE);
        }
        Log.i("TAG", "count search: " + transacPojos.size());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_subs, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.datepick_mi) {
            sDatePicker();

        }
        return super.onOptionsItemSelected(item);
    }

    private void initDataObj() {
        cal = Calendar.getInstance();
        sCal = Calendar.getInstance();
        Date date = cal.getTime();
        simpleFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
        sDate = simpleFormat.format(date);
    }

    private void sDatePicker() {
        //initDataObj();
        DatePickerDialog pickerDialog = new DatePickerDialog(TransactionActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                sCal.set(Calendar.YEAR, year);
                sCal.set(Calendar.MONTH, month);
                sCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                sDate = simpleFormat.format(sCal.getTime());
                getData();
            }
        }, sCal.get(Calendar.YEAR), sCal.get(Calendar.MONTH), sCal.get(Calendar.DAY_OF_MONTH));
        pickerDialog.getDatePicker().setMaxDate(new Date().getTime());
        pickerDialog.show();
    }
}