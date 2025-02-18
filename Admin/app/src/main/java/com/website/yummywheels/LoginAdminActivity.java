package com.website.yummywheels;

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

import org.json.JSONObject;

import java.util.ArrayList;

public class LoginAdminActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText usernameET, passET;
    private LinearLayout llLogin;
    private Button loginBtn;
    private ArrayList<EditText> editTextAL;
    private String uName;
    private Dialog dialogLoader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //check for logged in or not
        if (!com.website.yummywheels.utils.LoginSharedPref.getUnameKey(LoginAdminActivity.this).equals("")) {
            //already loggedin
            Intent regIntent = new Intent(LoginAdminActivity.this, NavAdminActivity.class);
            startActivity(regIntent);
            finish();
        }
        setContentView(R.layout.activity_login);
        initUI();
    }

    private void initUI() {
        usernameET = findViewById(R.id.et_username_log);
        passET = findViewById(R.id.et_pass_log);
        loginBtn = findViewById(R.id.btn_login);
        loginBtn.setOnClickListener(this);
        llLogin = findViewById(R.id.ll_login_ui);
        editTextAL = new ArrayList<>();
        editTextAL.add(usernameET);
        editTextAL.add(passET);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_login) {
            if (usernameET.getText().toString().isEmpty()) {
                Snackbar.make(llLogin, "Please enter your username", Snackbar.LENGTH_SHORT).show();
            }  else if (passET.getText().toString().isEmpty()) {
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
        dialogLoader = new Dialog(LoginAdminActivity.this, R.style.AppTheme_NoActionBar);
        dialogLoader.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#8D000000")));
        final View view = LoginAdminActivity.this.getLayoutInflater().inflate(R.layout.custom_dialog_loader, null);
        LottieAnimationView animationView = view.findViewById(R.id.loader);
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
        uName = usernameET.getText().toString();
        task.execute(usernameET.getText().toString(), passET.getText().toString());
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
            com.website.yummywheels.webservices.RestAPI restAPI = new com.website.yummywheels.webservices.RestAPI();
            try {
                JSONObject jsonObject = restAPI.ALogin(strings[0], strings[1]);
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
            Log.d("reply", s);
            if (s.contains("Unable to resolve host")) {
                AlertDialog.Builder ad = new AlertDialog.Builder(LoginAdminActivity.this);
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
                    } else if (ans.compareTo("true") == 0) {
                        com.website.yummywheels.utils.LoginSharedPref.setUnameKey(LoginAdminActivity.this, uName);
                        Intent intentLogin = new Intent(LoginAdminActivity.this, NavAdminActivity.class);
                        startActivity(intentLogin);
                        finish();
                    } else if (ans.compareTo("error") == 0) {
                        String error = json.getString("Data");
                        Toast.makeText(LoginAdminActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(LoginAdminActivity.this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
