package com.managerestaurant;

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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MenuActivity extends AppCompatActivity {
    private String rID;
    private Dialog dialogLoader;
    private RelativeLayout rlLayout;
    private ArrayList<MenuPojo> menuPojos;
    private ListView listView;
    private MenuAdap adapter;
    private RelativeLayout imageNotFound;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = findViewById(R.id.toolbar_add_food);
        toolbar.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_material);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Menu");
        rID = UserPref.getRid(this);
        initUI();
    }

    private void initUI() {
        rlLayout = findViewById(R.id.rl_menu);
        listView = findViewById(R.id.lv);
        imageNotFound = findViewById(R.id.rl_image);
    }

    @Override
    protected void onResume() {
        super.onResume();
        GetMenuTask task = new GetMenuTask();
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

    public void onMenuClicked(MenuPojo menuPojo) {
        Intent menuIntent = new Intent(MenuActivity.this, DetailMenuActivity.class);
        menuIntent.putExtra(UtilConstants.MENU_POJO, menuPojo);
        startActivity(menuIntent);
    }

    private class GetMenuTask extends AsyncTask<String, Void, String> {
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
                JSONObject jsonObject = restAPI.getMenu(rID, "true", UserPref.getId(MenuActivity.this));
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
                AlertDialog.Builder ad = new AlertDialog.Builder(MenuActivity.this);
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
                    MenuPojo menuPojo;
                    menuPojos = new ArrayList<MenuPojo>();
                    String ans = json.getString("status");
                    Log.d("reply::", ans);
                    if (ans.compareTo("no") == 0) {
                        Snackbar.make(rlLayout, "Menu is empty, start adding Food items", Snackbar.LENGTH_SHORT).show();
                        listView.setAdapter(null);
                    } else if (ans.compareTo("ok") == 0) {
                        JSONArray array = json.getJSONArray("Data");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject jsonObject = array.getJSONObject(i);
                            menuPojo = new MenuPojo(jsonObject.getString("data0"), jsonObject.getString("data1"), jsonObject.getString("data2"),
                                    jsonObject.getString("data3"), jsonObject.getString("data4"), jsonObject.getString("data5"),
                                    jsonObject.getString("data6"),
                                    jsonObject.getString("data7"), jsonObject.getString("data8"));
                            menuPojos.add(menuPojo);
                        }
                        //setLV
                        setListView();
                    } else if (ans.compareTo("error") == 0) {
                        String error = json.getString("Data");
                        Toast.makeText(MenuActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(MenuActivity.this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void setListView() {
        if (menuPojos.size() > 0) {
            adapter = new MenuAdap(MenuActivity.this, menuPojos,
                    MenuActivity.this);
            listView.setAdapter(adapter);
            imageNotFound.setVisibility(View.GONE);
        } else {
            listView.setAdapter(null);
            imageNotFound.setVisibility(View.VISIBLE);
        }
        Log.i("TAG", "count search: " + menuPojos.size());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_fooditem, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.addmenu_mi) {
            Intent intentDetailMenu = new Intent(MenuActivity.this, DetailMenuActivity.class);
            startActivity(intentDetailMenu);
        }
        return super.onOptionsItemSelected(item);
    }
}
