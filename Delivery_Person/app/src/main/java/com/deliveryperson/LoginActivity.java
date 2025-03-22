package com.deliveryperson;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText emailET, passET;
    private LinearLayout llLogin;
    private Button loginBtn;
    private ArrayList<EditText> editTextAL;
    private Dialog dialogLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //check for logged in or not
        if (!UserPref.getId(LoginActivity.this).equals("")) {
            //already loggedin
            Intent regIntent = new Intent(LoginActivity.this, OrderActivity.class);
            regIntent.putExtra(UtilConstants.LOGGEDIN, true);
            startActivity(regIntent);
            finish();
        }
        setContentView(R.layout.activity_login);
        initUI();
    }

    private void initUI() {
        emailET = findViewById(R.id.et_username_log);
        passET = findViewById(R.id.et_pass_log);
        loginBtn = findViewById(R.id.btn_login);
        loginBtn.setOnClickListener(this);
        llLogin = findViewById(R.id.ll_login_ui);
        editTextAL = new ArrayList<>();
        editTextAL.add(emailET);
        editTextAL.add(passET);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_login) {
            if (emailET.getText().toString().isEmpty()) {
                Snackbar.make(llLogin, "Please enter email id", Snackbar.LENGTH_SHORT).show();
            } else if (passET.getText().toString().isEmpty()) {
                Snackbar.make(llLogin, "Please enter password", Snackbar.LENGTH_SHORT).show();
            } else {
                loginSuccess();
            }
        }
        hideKeyboard(v);
    }

    private void stopAnimation() {
        if (dialogLoader.isShowing())
            dialogLoader.cancel();
    }

    private void startAnimation() {
        dialogLoader = new Dialog(LoginActivity.this, R.style.AppTheme_NoActionBar);
        dialogLoader.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#8D000000")));
        final View view = LoginActivity.this.getLayoutInflater().inflate(R.layout.loading, null);
        LottieAnimationView animationView = view.findViewById(R.id.lottie);
        animationView.playAnimation();
        dialogLoader.setContentView(view);
        dialogLoader.setCancelable(false);
        dialogLoader.show();
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void loginSuccess() {
        LoginTask task = new LoginTask();
        task.execute(emailET.getText().toString().trim(), passET.getText().toString().trim());
        Log.i("TAG", "loginSuccess: " + emailET.getText().toString().trim() + passET.getText().toString().trim());
    }

    private class LoginTask extends AsyncTask<String, Void, String> {
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
                JSONObject jsonObject = restAPI.Dlogin(strings[0], strings[1]);
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
                AlertDialog.Builder ad = new AlertDialog.Builder(LoginActivity.this);
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
                        Snackbar.make(llLogin, "Login credentials do not match", Snackbar.LENGTH_SHORT).show();
                    } else if (ans.compareTo("ok") == 0) {
                        //mid, name, rid
                        JSONArray array = json.getJSONArray("Data");
                        UserPref.setValue(LoginActivity.this, "mid", array.getJSONObject(0).getString("data0"));
                        UserPref.setValue(LoginActivity.this, "name", array.getJSONObject(0).getString("data1"));
                        new GetStatusTask().execute();
                    } else if (ans.compareTo("error") == 0) {
                        String error = json.getString("Data");
                        Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(LoginActivity.this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private class GetStatusTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String result;
            RestAPI restAPI = new RestAPI();
            try {
                JSONObject jsonObject = restAPI.Dgetprofile(UserPref.getId(LoginActivity.this));
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
            Log.d("reply getRestau ", s);
            if (s.contains("Unable to resolve host")) {
                AlertDialog.Builder ad = new AlertDialog.Builder(LoginActivity.this);
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
                            Intent intentLogin;
                            if (jsonObject.getString("data7").equalsIgnoreCase("yes")) {
                                UserPref.setValue(LoginActivity.this, "status", "yes");
                                intentLogin = new Intent(LoginActivity.this, StatusActivity.class);
                                intentLogin.putExtra(UtilConstants.STATUS_SELECTED, true);
                            } else {
                                UserPref.setValue(LoginActivity.this, "status", "no");
                                intentLogin = new Intent(LoginActivity.this, StatusActivity.class);
                                intentLogin.putExtra(UtilConstants.STATUS_SELECTED, false);
                            }
                            startActivity(intentLogin);
                            finish();
                        }
                    } else if (ans.compareTo("error") == 0) {
                        String error = json.getString("Data");
                        Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(LoginActivity.this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
