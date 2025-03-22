package com.deliveryperson;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

import org.json.JSONArray;
import org.json.JSONObject;

public class StatusActivity extends AppCompatActivity {

    private static final String CHANGE_STATUS_AVAIL = "Current status : I'm AVAILABLE for deliveries";
    private static final String CHANGE_STATUS_NOT = "Current status : I'm NOT AVAILABLE for deliveries";
    private TextView changeStatusTV;
    private boolean avail;
    private Dialog dialogLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        Toolbar toolbar = findViewById(R.id.toolbar_add_order);
        toolbar.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_material);
        setSupportActionBar(toolbar);
        changeStatusTV = findViewById(R.id.tv_chnagestatus);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void onStatusChangeClicked(View view) {
        if (!avail) {
            avail = true;
            changeStatusTV.setText(CHANGE_STATUS_AVAIL);
        } else {
            avail = false;
            changeStatusTV.setText(CHANGE_STATUS_NOT);
        }
    }

    public void onProceedClicked(View view) {
        String status = "";
        if (avail) {
            status = "Yes";
        } else
            status = "No";
        StatusTask task = new StatusTask();
        task.execute(UserPref.getId(StatusActivity.this), status);
    }

    private void stopAnimation() {
        if (dialogLoader.isShowing())
            dialogLoader.cancel();
    }

    private void startAnimation() {
        dialogLoader = new Dialog(StatusActivity.this, R.style.AppTheme_NoActionBar);
        dialogLoader.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#8D000000")));
        final View view = StatusActivity.this.getLayoutInflater().inflate(R.layout.loading, null);
        LottieAnimationView animationView = view.findViewById(R.id.lottie);
        animationView.playAnimation();
        dialogLoader.setContentView(view);
        dialogLoader.setCancelable(false);
        dialogLoader.show();
    }

    private class StatusTask extends AsyncTask<String, Void, String> {
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
                JSONObject jsonObject = restAPI.DChangestatus(strings[0], strings[1]);
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
            Log.d("reply", s);
            if (s.contains("Unable to resolve host")) {
                AlertDialog.Builder ad = new AlertDialog.Builder(StatusActivity.this);
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
                    Log.d("reply::", ans);
                    if (ans.compareTo("true") == 0) {
                        if (avail){
                            UserPref.setValue(StatusActivity.this, "status", "yes");
                        }else
                            UserPref.setValue(StatusActivity.this, "status", "no");

                        Intent intentOrder = new Intent(StatusActivity.this, OrderActivity.class);
                        intentOrder.putExtra(UtilConstants.STATUS_SELECTED, avail);
                        startActivity(intentOrder);
                        finish();
                    } else if (ans.compareTo("error") == 0) {
                        String error = json.getString("Data");
                        Toast.makeText(StatusActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(StatusActivity.this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
