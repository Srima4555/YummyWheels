package com.fooddelivery.user.Activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.fooddelivery.user.R;
import com.fooddelivery.user.helper.SavedData;
import com.fooddelivery.user.webservice.JSONParse;
import com.fooddelivery.user.webservice.RestAPI;

import org.json.JSONObject;

/**
 * Created by hp on 1/8/2018.
 */

public class Register extends AppCompatActivity{

    ImageView map;
    Button register;
    EditText name,email,contact,address,city,pincode,cord,pass;
    EditText[] textboxes;
    String[] mesg=new String[]{"Name","Email","Contact","Address","City","Pincode","Cordinates","Password"};
    Dialog cd;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        setActionbar("Register");
        name= (EditText) findViewById(R.id.name);
        email= (EditText) findViewById(R.id.email);
        contact= (EditText) findViewById(R.id.cont);
        address= (EditText) findViewById(R.id.add);
        city= (EditText) findViewById(R.id.city);
        pincode= (EditText) findViewById(R.id.pincode);
        cord= (EditText) findViewById(R.id.ll);
        pass= (EditText) findViewById(R.id.pass);
        textboxes=new EditText[]{name,email,contact,address,city,pincode,cord,pass};
        register= (Button) findViewById(R.id.register);
        map= (ImageView) findViewById(R.id.map);

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cord.length()>0)
                {
                    String t[]=cord.getText().toString().split(",");
                    SavedData.setLat(Double.parseDouble(t[0]));
                    SavedData.setLng(Double.parseDouble(t[1]));
                }
                Intent i=new Intent(Register.this,PlaceSelection.class);
                startActivity(i);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean ans=true;
                for(int i=0;i<textboxes.length;i++)
                {
                    if(textboxes[i].length()==0)
                    {
                        ans=false;
                        if(i==6)
                        {
                            Snackbar.make(view,"Select your Location from the Map, Click on the Right Arrow",Snackbar.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Snackbar.make(view,"Enter "+mesg[i],Snackbar.LENGTH_SHORT).show();
                        }
                        break;
                    }
                    else
                    {
                        if(i==1)
                        {
                            if(!isEmailValid(textboxes[i].getText().toString()))
                            {
                                ans=false;
                                Snackbar.make(view,"Enter Valid Email",Snackbar.LENGTH_SHORT).show();
                                break;
                            }
                        }

                        if(i==2)
                        {
                            if(textboxes[i].length()!=10) {
                                ans = false;
                                Snackbar.make(view, "Contact Number should have 10 digits", Snackbar.LENGTH_SHORT).show();
                                break;
                            }
                        }
                    }
                }

                if(ans)
                {
                    Log.d("PARAMTERS",name.getText().toString()+"\n"+contact.getText().toString()+"\n"+email.getText().toString()+"\n"+city.getText().toString()+"\n"+
                            address.getText().toString()+"\n"+pincode.getText().toString()+"\n"+cord.getText().toString()+"\n"+pass.getText().toString());
                    new register().execute(name.getText().toString(),contact.getText().toString(),email.getText().toString(),city.getText().toString(),
                            address.getText().toString(),pincode.getText().toString(),cord.getText().toString(),pass.getText().toString());
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(SavedData.getLat()>0)
        {
            cord.setText(SavedData.getLat()+","+SavedData.getLng());
        }
    }

    public void setActionbar(String title)
    {
        String Title="<font color='#D70C16'>"+title+"</font>";
        getSupportActionBar().setTitle(Html.fromHtml(Title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)
        {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void dailog()
    {
        cd=new Dialog(Register.this,R.style.AppTheme);
        cd.requestWindowFeature(Window.FEATURE_NO_TITLE);
        cd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        cd.setContentView(R.layout.loading);
        cd.setCancelable(false);
        cd.show();
    }

    private class register extends AsyncTask<String,JSONObject,String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dailog();
        }

        @Override
        protected String doInBackground(String... params) {
            String a = "back";
            RestAPI api = new RestAPI();
            try {
                JSONObject json = api.Uregister(params[0], params[1], params[2], params[3], params[4], params[5], params[6], params[7]);
                JSONParse jp = new JSONParse();
                a = jp.parse(json);
            } catch (Exception e) {
                a = e.getMessage();
            }
            return a;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("RESPONSE",s);
            cd.cancel();
            if (s.contains("Unable to resolve host")) {
                AlertDialog.Builder ad = new AlertDialog.Builder(Register.this);
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
                    if (ans.compareTo("already") == 0) {
                        Snackbar.make(map, "Email address already Exists", Snackbar.LENGTH_SHORT).show();
                    } else if (ans.compareTo("true") == 0) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                        builder.setTitle("Registration successful");
                        builder.setCancelable(false);
                        builder.setMessage("You will be redirected to Login Screen for Authentication");
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                        builder.show();
                    } else if (ans.compareTo("error") == 0) {
                        String error = json.getString("Data");
                        Toast.makeText(Register.this, error, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(Register.this, "catch - " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
