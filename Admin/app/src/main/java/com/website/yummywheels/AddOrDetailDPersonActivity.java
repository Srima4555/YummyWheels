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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.website.yummywheels.pojo.DPersonPojo;
import com.website.yummywheels.utils.EmailValidation;
import com.website.yummywheels.utils.MobileNumberValidation;
import com.website.yummywheels.utils.UtilConstants;

import org.json.JSONObject;

public class AddOrDetailDPersonActivity extends AppCompatActivity {

    private static final CharSequence CANNOTBEEMPTY = "This field cannot be empty";
    private EditText mEmailET, mContET, mNameET, cityET, pincodeET;
    private String dID;
    private Dialog dialogLoader;
    private DPersonPojo dPersonPojo;
    private ScrollView llDeli;
    private boolean isUpdate;
    private Button btnUpdateSubmit;

    //map for location view
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deliveryp);
        Toolbar toolbar = findViewById(R.id.toolbar_add_food);
        toolbar.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_material);
        setSupportActionBar(toolbar);
        initUI();
        if ((dPersonPojo = (DPersonPojo) getIntent().getSerializableExtra(UtilConstants.DPERSON_POJO)) != null) {
            dID = dPersonPojo.getDid();
            isUpdate = true;
            mNameET.setText(dPersonPojo.getName());
            mContET.setText(dPersonPojo.getContact());
            mEmailET.setText(dPersonPojo.getEmail());
            cityET.setText(dPersonPojo.getCity());
            pincodeET.setText(dPersonPojo.getPincode());
            getSupportActionBar().setTitle("ID #" + dPersonPojo.getDid());
        } else {
            getSupportActionBar().setTitle("Add Delivery person");
            btnUpdateSubmit.setText("Submit");
        }
    }

    private void initUI() {
        //mIdET = findViewById(R.id.et_did);
        mNameET = findViewById(R.id.et_dname);
        mEmailET = findViewById(R.id.et_demail);
        mContET = findViewById(R.id.et_dcontact);
        cityET = findViewById(R.id.et_dcity);
        pincodeET = findViewById(R.id.et_dpincode);
        btnUpdateSubmit = findViewById(R.id.btn_update_orsubmit);
        llDeli = findViewById(R.id.ll_deli);
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
        if (isUpdate) {
            if (mNameET.getText().toString().isEmpty()) {
                mNameET.setError(CANNOTBEEMPTY);
            } else if (mEmailET.getText().toString().isEmpty()) {
                mEmailET.setError(CANNOTBEEMPTY);
            } else if (!emailValidation.validateEmail(mEmailET.getText().toString())) {
                Snackbar.make(llDeli, "Enter valid Email id", Snackbar.LENGTH_LONG).show();
            } else if (mContET.getText().toString().isEmpty()) {
                mContET.setError(CANNOTBEEMPTY);
            } else if (!MobileNumberValidation.isValid(mContET.getText().toString())) {
                mContET.setError(CANNOTBEEMPTY);
            } else if (cityET.getText().toString().isEmpty()) {
                cityET.setError(CANNOTBEEMPTY);
            } else if (pincodeET.getText().toString().isEmpty()) {
                pincodeET.setError(CANNOTBEEMPTY);
            } else {
                UpdateTask task = new UpdateTask();
                task.execute(dPersonPojo.getDid(), mNameET.getText().toString(),
                        mContET.getText().toString(), mEmailET.getText().toString(),
                        cityET.getText().toString(), pincodeET.getText().toString());
            }
        } else {
            if (mNameET.getText().toString().isEmpty()) {
                mNameET.setError(CANNOTBEEMPTY);
            } else if (mEmailET.getText().toString().isEmpty()) {
                mEmailET.setError(CANNOTBEEMPTY);
            } else if (!emailValidation.validateEmail(mEmailET.getText().toString())) {
                Snackbar.make(llDeli, "Enter valid Email id", Snackbar.LENGTH_LONG).show();
            } else if (mContET.getText().toString().isEmpty()) {
                mContET.setError(CANNOTBEEMPTY);
            } else if (!MobileNumberValidation.isValid(mContET.getText().toString())) {
                mContET.setError("Enter a valid contact number");
            } else if (cityET.getText().toString().isEmpty()) {
                cityET.setError(CANNOTBEEMPTY);
            } else if (pincodeET.getText().toString().isEmpty()) {
                pincodeET.setError(CANNOTBEEMPTY);
            } else {
                AddDPersonTask task = new AddDPersonTask();
                task.execute(mNameET.getText().toString(), mContET.getText().toString(),
                        mEmailET.getText().toString(),
                        cityET.getText().toString(), pincodeET.getText().toString());
            }
        }
    }

    private class DelDPersonTask extends AsyncTask<String, Void, String> {
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
                JSONObject jsonObject = restAPI.DeleteDeliveryP(dID);
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
            Log.d("reply DelDPerson ", s);
            if (s.contains("Unable to resolve host")) {
                AlertDialog.Builder ad = new AlertDialog.Builder(AddOrDetailDPersonActivity.this);
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
                        finish();
                    } else if (ans.compareTo("error") == 0) {
                        String error = json.getString("Data");
                        Toast.makeText(AddOrDetailDPersonActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(AddOrDetailDPersonActivity.this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                JSONObject jsonObject = restAPI.UpdateDeliveryP(strings[0], strings[1], strings[2], strings[3], strings[4], strings[5]);
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
            Log.d("reply up dper ", s);
            if (s.contains("Unable to resolve host")) {
                AlertDialog.Builder ad = new AlertDialog.Builder(AddOrDetailDPersonActivity.this);
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
                    if (ans.compareTo("already") == 0) {
                        Snackbar.make(llDeli, "This delivery person already exists, add new delivery person", Snackbar.LENGTH_LONG).show();
                    } else if (ans.compareTo("true") == 0) {
                        finish();
                    } else if (ans.compareTo("error") == 0) {
                        String error = json.getString("Data");
                        Toast.makeText(AddOrDetailDPersonActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(AddOrDetailDPersonActivity.this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private class AddDPersonTask extends AsyncTask<String, Void, String> {
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
                JSONObject jsonObject = restAPI.AddDeliveryP(strings[0], strings[1], strings[2], strings[3], strings[4]);
                Log.i("TAG", "doInBackground: " + strings[0] + strings[1] + strings[2] + strings[3] + strings[4]);
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
                AlertDialog.Builder ad = new AlertDialog.Builder(AddOrDetailDPersonActivity.this);
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
                    if (ans.compareTo("already") == 0) {
                        Snackbar.make(llDeli, "Could not update, try later", Snackbar.LENGTH_LONG).show();
                    } else if (ans.compareTo("true") == 0) {
                        finish();
                    } else if (ans.compareTo("error") == 0) {
                        String error = json.getString("Data");
                        Toast.makeText(AddOrDetailDPersonActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(AddOrDetailDPersonActivity.this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_del_dperson, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (getIntent().getSerializableExtra(UtilConstants.DPERSON_POJO) == null)
            menu.findItem(R.id.del_mi).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.del_mi) {
            AlertDialog.Builder ad = new AlertDialog.Builder(AddOrDetailDPersonActivity.this);
            ad.setTitle("Confirm deleting");
            ad.setMessage("Delete delivery person " + dPersonPojo.getName());
            ad.setNeutralButton("delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DelDPersonTask task = new DelDPersonTask();
                    task.execute();
                }
            });
            ad.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            ad.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
