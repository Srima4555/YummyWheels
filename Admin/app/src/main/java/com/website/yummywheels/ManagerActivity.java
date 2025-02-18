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
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.website.yummywheels.pojo.MgrPojo;
import com.website.yummywheels.utils.EmailValidation;
import com.website.yummywheels.utils.MobileNumberValidation;

import org.json.JSONArray;
import org.json.JSONObject;

public class ManagerActivity extends AppCompatActivity {

    private static final CharSequence CANNOTBEEMPTY = "This field cannot be empty";
    private EditText mIdET, mEmailET, mContET, mNameET;
    private String rID;
    private Dialog dialogLoader;
    private MgrPojo mgrPojo;
    private RelativeLayout mgrLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);
        Toolbar toolbar = findViewById(R.id.toolbar_add_food);
        toolbar.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_material);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("View Manager Info");
        rID = getIntent().getStringExtra(com.website.yummywheels.utils.UtilConstants.RESTAU_ID);
        initUI();
    }

    private void initUI() {
        mIdET = findViewById(R.id.et_mid);
        mgrLayout = findViewById(R.id.ll_mgr);
        mNameET = findViewById(R.id.et_mname);
        mContET = findViewById(R.id.et_memail);
        mEmailET = findViewById(R.id.et_mcontact);
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
        final View view = getLayoutInflater().inflate(R.layout.custom_dialog_loader, null);
        LottieAnimationView animationView = view.findViewById(R.id.loader);
        animationView.playAnimation();
        dialogLoader.setContentView(view);
        dialogLoader.setCancelable(false);
        dialogLoader.show();
    }

    public void onUpdateClicked(View view) {
        EmailValidation emailValidation = new EmailValidation();
        if (mIdET.getText().toString().isEmpty()) {
            mIdET.setError(CANNOTBEEMPTY);
        } else if (mNameET.getText().toString().isEmpty()) {
            mNameET.setError(CANNOTBEEMPTY);
        } else if (mContET.getText().toString().isEmpty()) {
            mContET.setError(CANNOTBEEMPTY);
        } else if (!MobileNumberValidation.isValid(mContET.getText().toString())) {
            Snackbar.make(mgrLayout, "Enter valid phone number", Snackbar.LENGTH_LONG).show();
        } else if (mEmailET.getText().toString().isEmpty()) {
            mEmailET.setError(CANNOTBEEMPTY);
        } else if (!emailValidation.validateEmail(mEmailET.getText().toString())) {
            Snackbar.make(mgrLayout, "Enter valid Email id", Snackbar.LENGTH_LONG).show();
        }else {
            UpdateTask task = new UpdateTask();
            task.execute(mIdET.getText().toString(), mNameET.getText().toString(), mEmailET.getText().toString(), mContET.getText().toString());
        }
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
                JSONObject jsonObject = restAPI.getManager(rID);
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
                AlertDialog.Builder ad = new AlertDialog.Builder(ManagerActivity.this);
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
                    if (ans.compareTo("no") == 0) {
                        mIdET.setEnabled(false);
                        mNameET.setEnabled(false);
                        mContET.setEnabled(false);
                        mEmailET.setEnabled(false);
                        Snackbar.make(mgrLayout, "Manager is not assigned for this restaurant", Snackbar.LENGTH_LONG).show();
                    } else if (ans.compareTo("ok") == 0) {
                        JSONArray array = json.getJSONArray("Data");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject jsonObject = array.getJSONObject(i);
                           mgrPojo = new MgrPojo(jsonObject.getString("data0"), jsonObject.getString("data1"), jsonObject.getString("data2"),
                                    jsonObject.getString("data3"));
                        }
                        mIdET.setText(mgrPojo.getMid());
                        mNameET.setText(mgrPojo.getName());
                        mContET.setText(mgrPojo.getContact());
                        mEmailET.setText(mgrPojo.getEmail());
                    } else if (ans.compareTo("error") == 0) {
                        String error = json.getString("Data");
                        Toast.makeText(ManagerActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(ManagerActivity.this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    private class UpdateTask extends AsyncTask<String, Void, String> {
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
                JSONObject jsonObject = restAPI.UpdateManager(strings[0],strings[1],strings[2],strings[3]);
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
                AlertDialog.Builder ad = new AlertDialog.Builder(ManagerActivity.this);
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
                    if (ans.compareTo("no") == 0) {

                    } else if (ans.compareTo("ok") == 0) {
                        JSONArray array = json.getJSONArray("Data");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject jsonObject = array.getJSONObject(i);
                            mgrPojo = new MgrPojo(jsonObject.getString("data0"), jsonObject.getString("data1"), jsonObject.getString("data2"),
                                    jsonObject.getString("data3"));
                        }
                        mIdET.setText(mgrPojo.getMid());
                        mNameET.setText(mgrPojo.getName());
                        mContET.setText(mgrPojo.getContact());
                        mEmailET.setText(mgrPojo.getEmail());
                    } else if (ans.compareTo("error") == 0) {
                        String error = json.getString("Data");
                        Toast.makeText(ManagerActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(ManagerActivity.this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
