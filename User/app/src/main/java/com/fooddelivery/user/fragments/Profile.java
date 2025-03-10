package com.fooddelivery.user.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.fooddelivery.user.Activity.Login;
import com.fooddelivery.user.Activity.PlaceSelection;
import com.fooddelivery.user.Activity.Register;
import com.fooddelivery.user.R;
import com.fooddelivery.user.helper.SavedData;
import com.fooddelivery.user.helper.UserPref;
import com.fooddelivery.user.webservice.JSONParse;
import com.fooddelivery.user.webservice.RestAPI;

import org.json.JSONArray;
import org.json.JSONObject;

public class Profile extends Fragment{

    ImageView map;
    String uid="";
    Button register,cp;
    EditText name,email,contact,address,city,pincode,cord;
    EditText[] textboxes;
    String[] mesg=new String[]{"Name","Email","Contact","Address","City","Pincode","Cordinates"};
    Dialog cd,changePassDialog;
    View dialogView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.profile,container,false);
        uid= UserPref.getId(getActivity());
        name= (EditText) v.findViewById(R.id.name);
        email= (EditText) v.findViewById(R.id.email);
        contact= (EditText) v.findViewById(R.id.cont);
        address= (EditText) v.findViewById(R.id.add);
        city= (EditText) v.findViewById(R.id.city);
        pincode= (EditText) v.findViewById(R.id.pincode);
        cord= (EditText) v.findViewById(R.id.ll);
        textboxes=new EditText[]{name,email,contact,address,city,pincode,cord};
        register= (Button) v.findViewById(R.id.register);
        cp= (Button) v.findViewById(R.id.cp);
        map= (ImageView) v.findViewById(R.id.map);

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cord.length()>0)
                {
                    String t[]=cord.getText().toString().split(",");
                    SavedData.setLat(Double.parseDouble(t[0]));
                    SavedData.setLng(Double.parseDouble(t[1]));
                }
                Intent i=new Intent(getActivity(), PlaceSelection.class);
                startActivity(i);
            }
        });

        cp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogChangePass();
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
                    new updateProfile().execute(uid,name.getText().toString(),contact.getText().toString(),email.getText().toString(),city.getText().toString(),address.getText().toString(),pincode.getText().toString(),cord.getText().toString());
                }
            }
        });

        new getProfile().execute(uid);

        return v;
    }

    private void dialogChangePass() {
        changePassDialog = new Dialog(getActivity());
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
                    new Changepass().execute(uid,oldPassString,newPassString);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if(SavedData.getLat()>0)
        {
            cord.setText(SavedData.getLat()+","+SavedData.getLng());
        }
    }

    public void dailog()
    {
        cd=new Dialog(getActivity(),R.style.AppTheme);
        cd.requestWindowFeature(Window.FEATURE_NO_TITLE);
        cd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        cd.setContentView(R.layout.loading);
        cd.setCancelable(false);
        cd.show();
    }

    private class getProfile extends AsyncTask<String,JSONObject,String>
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
                JSONObject json=api.UgetProfile(params[0]);
                JSONParse jp=new JSONParse();
                a=jp.parse(json);
            } catch (Exception e) {
                a=e.getMessage();
            }
            return a;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            cd.cancel();
            if (s.contains("Unable to resolve host")) {
                AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
                ad.setTitle("Unable to Connect!");
                ad.setMessage("Check your Internet Connection,Unable to connect the Server");
                ad.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                ad.show();
            }
            else {
                try {
                    JSONObject json = new JSONObject(s);
                    String ans = json.getString("status");
                    if (ans.compareTo("no") == 0) {
                        AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
                        ad.setTitle("NO Profile Found!");
                        ad.setMessage("Contact Admin");
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
                        name.setText(jobj.getString("data1"));
                        contact.setText(jobj.getString("data2"));
                        email.setText(jobj.getString("data3"));
                        city.setText(jobj.getString("data4"));
                        address.setText(jobj.getString("data5"));
                        pincode.setText(jobj.getString("data6"));
                        cord.setText(jobj.getString("data7"));

                    } else if (ans.compareTo("error") == 0) {
                        String error = json.getString("Data");
                        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "catch - " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private class updateProfile extends AsyncTask<String,JSONObject,String>
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
                JSONObject json=api.UupdateProfile(params[0],params[1],params[2],params[3],params[4],params[5],params[6],params[7]);
                JSONParse jp=new JSONParse();
                a=jp.parse(json);
            } catch (Exception e) {
                a=e.getMessage();
            }
            return a;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            cd.cancel();
            if (s.contains("Unable to resolve host")) {
                AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
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
                        Toast.makeText(getActivity(), "Profile Updated", Toast.LENGTH_SHORT).show();
                    } else if (ans.compareTo("error") == 0) {
                        String error = json.getString("Data");
                        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "catch - " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    private class Changepass extends AsyncTask<String,JSONObject,String>
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
                JSONObject json=api.UChangePassword(params[0],params[1],params[2]);
                JSONParse jp=new JSONParse();
                a=jp.parse(json);
            } catch (Exception e) {
                a=e.getMessage();
            }
            return a;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            cd.cancel();
            if (s.contains("Unable to resolve host")) {
                AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
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
                    if (ans.compareTo("true") == 0) {
                        //Toast.makeText(ProfileActivity.this, "Password updated", Toast.LENGTH_SHORT).show();
                        if (changePassDialog.isShowing())
                            changePassDialog.dismiss();
                        UserPref.setValue(getActivity(),"uid","");
                        UserPref.setValue(getActivity(),"name","");

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Password Changed");
                        builder.setCancelable(false);
                        builder.setMessage("You will be redirected to Login Screen for Authentication");
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intentLogin = new Intent(getActivity(), Login.class);
                                intentLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intentLogin);
                                getActivity().finish();
                            }
                        });
                        builder.show();
                    } else if (ans.compareTo("false") == 0) {
                        Snackbar.make(dialogView, "Entered Existing Password is Wrong.", Snackbar.LENGTH_LONG).show();
                    } else if (ans.compareTo("error") == 0) {
                        String error = json.getString("Data");
                        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "catch - " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
