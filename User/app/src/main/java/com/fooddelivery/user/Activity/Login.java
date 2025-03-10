package com.fooddelivery.user.Activity;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fooddelivery.user.Backgroundservice;
import com.fooddelivery.user.R;
import com.fooddelivery.user.helper.UserPref;
import com.fooddelivery.user.webservice.JSONParse;
import com.fooddelivery.user.webservice.RestAPI;

import org.json.JSONArray;
import org.json.JSONObject;

public class Login extends AppCompatActivity{

    EditText User,Pass;
    Button Signin,Signup;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    Dialog cd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        stopService(new Intent(Login.this, Backgroundservice.class));
        startService(new Intent(Login.this,Backgroundservice.class));


        if(UserPref.getId(Login.this).length()>0)
        {
            Intent i=new Intent(Login.this, MainActivity.class);
            startActivity(i);
            finish();
        }
        else
        {
            setContentView(R.layout.login);
            getSupportActionBar().hide();
            User= (EditText) findViewById(R.id.user);
            Pass= (EditText) findViewById(R.id.pass);
            Signin= (Button) findViewById(R.id.login);
            Signup= (Button) findViewById(R.id.register);

            Signin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(User.getText().toString().length()>0)
                    {
                        if(isEmailValid(User.getText().toString()))
                        {
                            if(Pass.getText().toString().length()>0)
                            {
                                if(!weHavePermission())
                                {
                                    requestforPermissionFirst();
                                }
                                else {
                                    new login().execute(User.getText().toString(), Pass.getText().toString());
                                }
                            }
                            else
                            {
                                Snackbar.make(view,"Enter Password",Snackbar.LENGTH_SHORT).show();
                                Pass.requestFocus();
                            }
                        }
                        else
                        {
                            Snackbar.make(view,"Invalid Email",Snackbar.LENGTH_SHORT).show();
                            User.requestFocus();
                        }
                    }
                    else
                    {
                        Snackbar.make(view,"Enter Email",Snackbar.LENGTH_SHORT).show();
                        User.requestFocus();
                    }

                }
            });

            Signup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i=new Intent(Login.this, Register.class);
                    startActivity(i);
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void dailog()
    {
        cd=new Dialog(Login.this,R.style.AppTheme);
        cd.requestWindowFeature(Window.FEATURE_NO_TITLE);
        cd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        cd.setContentView(R.layout.loading);
        cd.setCancelable(false);
        cd.show();
    }

    private class login extends AsyncTask<String,JSONObject,String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dailog();
        }

        @Override
        protected String doInBackground(String... params) {
            String a="back";
            RestAPI api=new RestAPI();
            try {
                JSONObject json=api.Ulogin(params[0],params[1]);
                JSONParse jp = new JSONParse();
                a=jp.parse(json);
            } catch (Exception e) {
                a=e.getMessage();
            }
            return a;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("RESPONSE",s);
            cd.cancel();

            if (s.contains("Unable to resolve host")) {
                AlertDialog.Builder ad = new AlertDialog.Builder(Login.this);
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
                    if (ans.compareTo("false") == 0) {
                        AlertDialog.Builder ad = new AlertDialog.Builder(Login.this);
                        ad.setTitle("Incorrect Credentials!");
                        ad.setMessage("Please check your Email or Password!");
                        ad.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        ad.show();
                    } else if (ans.compareTo("ok") == 0) {

                        JSONArray jarry = json.getJSONArray("Data");
                        JSONObject jobj = jarry.getJSONObject(0);
                        String uid=jobj.getString("data0");
                        String name=jobj.getString("data1");

                        UserPref.setValue(Login.this,"uid",uid);
                        UserPref.setValue(Login.this,"name",name);

                        Intent i=new Intent(Login.this,MainActivity.class);
                        startActivity(i);
                        finish();

                    } else if (ans.compareTo("error") == 0) {
                        String error = json.getString("Data");
                        Toast.makeText(Login.this, error, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(Login.this, "catch - " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private boolean weHavePermission()
    {
        return  (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestforPermissionFirst()
    {
        if ((ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) && (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)))
        {
            requestForResultPermission();
        }
        else
        {
            requestForResultPermission();
        }
    }

    private void requestForResultPermission()
    {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION}, 111);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==111)
        {
            int totpermission=0;
            for(int i=0;i<grantResults.length;i++)
            {
                totpermission+=grantResults[i];
            }

            if(totpermission==0)
            {
                new login().execute(User.getText().toString(), Pass.getText().toString());
            }
            else
            {
                requestforPermissionFirst();
            }
        }
    }
}
