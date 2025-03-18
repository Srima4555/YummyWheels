package com.managerestaurant;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

import org.json.JSONArray;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {

    private static final CharSequence CANNOTBEEMPTY = "This field cannot be empty";
    private EditText mEmailET, mContET, mNameET;
    private String mID;
    private Dialog dialogLoader;
    private MgrPojo mgrPojo;
    private RelativeLayout mgrLayout;
    private Dialog changePassDialog;
    private View dialogView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mID = UserPref.getId(ProfileActivity.this);
        Toolbar toolbar = findViewById(R.id.toolbar_add_order);
        toolbar.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_material);
        setSupportActionBar(toolbar);
        initUI();
    }

    private void initUI() {
        mgrLayout = findViewById(R.id.ll_mgr);
        mNameET = findViewById(R.id.et_mname);
        mContET = findViewById(R.id.et_mcontact);
        mEmailET = findViewById(R.id.et_memail);
        GetMgrTask task = new GetMgrTask();
        task.execute();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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

    public void onUpdateClicked(View view) {
        EmailValidation emailValidation = new EmailValidation();
        if (mNameET.getText().toString().isEmpty()) {
            mNameET.setError(CANNOTBEEMPTY);
        } else if (mContET.getText().toString().isEmpty()) {
            mContET.setError(CANNOTBEEMPTY);
        } else if (!MobileNumberValidation.isValid(mContET.getText().toString())) {
            Snackbar.make(mgrLayout, "Enter valid phone number", Snackbar.LENGTH_LONG).show();
        } else if (mEmailET.getText().toString().isEmpty()) {
            mEmailET.setError(CANNOTBEEMPTY);
        } else if (!emailValidation.validateEmail(mEmailET.getText().toString())) {
            Snackbar.make(mgrLayout, "Enter valid Email id", Snackbar.LENGTH_LONG).show();
        } else {
            UpdateTask task = new UpdateTask();
            task.execute(mID, mNameET.getText().toString(), mEmailET.getText().toString(), mContET.getText().toString());
        }
    }

    public void onChangePassClicked(View view) {
        changePassDialog = new Dialog(ProfileActivity.this);
        changePassDialog.setTitle("Change Password");
        dialogView = getLayoutInflater().inflate(R.layout.dialog_change_pass, null);
        changePassDialog.setContentView(dialogView);
        changePassDialog.setCancelable(false);
        changePassDialog.show();
        Button cancelBtn, okBtn;
        cancelBtn = dialogView.findViewById(R.id.btn_cancel);
        okBtn = dialogView.findViewById(R.id.btn_ok);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (changePassDialog.isShowing())
                    changePassDialog.dismiss();
            }
        });
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInputEditText oldPass = dialogView.findViewById(R.id.tiet_old_pass);
                TextInputEditText newPass = dialogView.findViewById(R.id.tiet_new_pass);
                String oldPassString = oldPass.getText().toString();
                String newPassString = newPass.getText().toString();
                if (oldPassString.isEmpty()) {
                    oldPass.setError("Enter Old Password");
                } else if (newPassString.isEmpty()) {
                    newPass.setError("Enter new password");
                } else {
                    new ChangePassTask().execute(UserPref.getId(ProfileActivity.this), oldPassString, newPassString);
                }
            }
        });
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
                JSONObject jsonObject = restAPI.getManager(UserPref.getRid(ProfileActivity.this));
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
                AlertDialog.Builder ad = new AlertDialog.Builder(ProfileActivity.this);
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
                        mNameET.setText(mgrPojo.getName());
                        mContET.setText(mgrPojo.getContact());
                        mEmailET.setText(mgrPojo.getEmail());
                    } else if (ans.compareTo("error") == 0) {
                        String error = json.getString("Data");
                        Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(ProfileActivity.this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
            RestAPI restAPI = new RestAPI();
            try {
                JSONObject jsonObject = restAPI.UpdateManager(strings[0], strings[1], strings[2], strings[3]);
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
                AlertDialog.Builder ad = new AlertDialog.Builder(ProfileActivity.this);
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
                    if (ans.compareTo("Email") == 0) {
                        Snackbar.make(mgrLayout, "This Manager already exists", Snackbar.LENGTH_LONG).show();
                    } else if (ans.compareTo("true") == 0) {
                        finish();
                    } else if (ans.compareTo("error") == 0) {
                        String error = json.getString("Data");
                        Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(ProfileActivity.this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private class ChangePassTask extends AsyncTask<String, Void, String> {
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
                JSONObject jsonObject = restAPI.MChangePassword(strings[0], strings[1], strings[2]);
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
                AlertDialog.Builder ad = new AlertDialog.Builder(ProfileActivity.this);
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
                    if (ans.compareTo("false") == 0) {
                        Snackbar.make(mgrLayout, "Error occured while changing password", Snackbar.LENGTH_LONG).show();
                    } else if (ans.compareTo("true") == 0) {
                        Snackbar.make(mgrLayout, "Password changed successfully", Snackbar.LENGTH_LONG).show();
                        if (changePassDialog.isShowing()) changePassDialog.dismiss();
                    } else if (ans.compareTo("error") == 0) {
                        String error = json.getString("Data");
                        Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(ProfileActivity.this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
