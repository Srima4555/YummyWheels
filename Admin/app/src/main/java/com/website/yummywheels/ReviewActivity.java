package com.website.yummywheels;

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
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.website.yummywheels.adapter.ReviewAdap;
import com.website.yummywheels.pojo.ReviewPojo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.website.yummywheels.utils.UtilConstants.TAG;

public class ReviewActivity extends AppCompatActivity {
    private String rID;
    private Dialog dialogLoader;
    private RelativeLayout rlLayout;
    private ArrayList<ReviewPojo> reviewPojos;
    private ListView listView;
    private ReviewAdap adapter;
    private RelativeLayout imageNotFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        Toolbar toolbar = findViewById(R.id.toolbar_add_food);
        toolbar.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_material);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("View Restaurant's Review");
        rID = getIntent().getStringExtra(com.website.yummywheels.utils.UtilConstants.RESTAU_ID);
        initUI();
    }

    private void initUI() {
        rlLayout = findViewById(R.id.rl_menu);
        imageNotFound = findViewById(R.id.rl_image);
        listView = findViewById(R.id.lv);
        GetMgrTask task = new GetMgrTask();
        task.execute();
    }

    private void stopAnimation() {
        if (dialogLoader.isShowing())
            dialogLoader.cancel();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
    private void startAnimation() {
        dialogLoader = new Dialog(this, R.style.AppTheme_NoActionBar);
        dialogLoader.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#8D000000")));
        final View view = getLayoutInflater().inflate(R.layout.custom_dialog_loader, null);
        LottieAnimationView animationView = view.findViewById(R.id.loader);
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
            com.website.yummywheels.webservices.RestAPI restAPI = new com.website.yummywheels.webservices.RestAPI();
            try {
                JSONObject jsonObject = restAPI.getReviews(rID);
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
                AlertDialog.Builder ad = new AlertDialog.Builder(ReviewActivity.this);
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
                    ReviewPojo menuPojo;
                    reviewPojos = new ArrayList<ReviewPojo>();
                    String ans = json.getString("status");
                    Log.d("reply::", ans);
                    if (ans.compareTo("no") == 0) {
                        Snackbar.make(rlLayout, "No reviews yet", Snackbar.LENGTH_SHORT).show();
                        listView.setAdapter(null);
                        imageNotFound.setVisibility(View.VISIBLE);
                    } else if (ans.compareTo("ok") == 0) {
                        JSONArray array = json.getJSONArray("Data");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject jsonObject = array.getJSONObject(i);
                            menuPojo = new ReviewPojo(jsonObject.getString("data0"), jsonObject.getString("data1"), jsonObject.getString("data2"),
                                    jsonObject.getString("data3"), jsonObject.getString("data4"), jsonObject.getString("data5"));
                            reviewPojos.add(menuPojo);
                        }
                        //setLV
                        setListView();
                    } else if (ans.compareTo("error") == 0) {
                        String error = json.getString("Data");
                        Toast.makeText(ReviewActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(ReviewActivity.this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void setListView() {
        if (reviewPojos.size() > 0) {
            adapter = new ReviewAdap(ReviewActivity.this, reviewPojos);
            listView.setAdapter(adapter);
            imageNotFound.setVisibility(View.GONE);
        } else {
            listView.setAdapter(null);
            imageNotFound.setVisibility(View.VISIBLE);
        }
        Log.i(TAG, "count search: " + reviewPojos.size());
    }
}
