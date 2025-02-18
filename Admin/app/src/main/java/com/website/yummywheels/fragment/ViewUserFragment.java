package com.website.yummywheels.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.website.yummywheels.DetailUserActivity;
import com.website.yummywheels.R;
import com.website.yummywheels.adapter.UsersListViewAdapter;
import com.website.yummywheels.pojo.UserPojo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ViewUserFragment extends android.support.v4.app.Fragment {
    private ArrayList<UserPojo> allUsersList;
    private ListView usersListView;
    private CoordinatorLayout llViewUsers;
    private Dialog dialogLoader;
    private ImageView noRecords;

    public ViewUserFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_users, container, false);
        usersListView = view.findViewById(R.id.lv_view_users);
        llViewUsers = view.findViewById(R.id.ll_viewusers);
        noRecords = view.findViewById(R.id.iv_no_records);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    private void getData() {
        ViewUsersTask task = new ViewUsersTask();
        task.execute();
    }

    private class ViewUsersTask extends AsyncTask<String, Void, String> {
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
                JSONObject jsonObject = restAPI.viewUsers("All", "");
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
            Log.d("reply", s);
            if (s.contains("Unable to resolve host")) {
                AlertDialog.Builder ad = new AlertDialog.Builder(getContext());
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
                    allUsersList = new ArrayList<>();
                    com.website.yummywheels.pojo.UserPojo userPojo;
                    String ans = json.getString("status");
                    if (ans.compareTo("ok") == 0) {
                        JSONArray jsonDataArray = json.getJSONArray("Data");
                        for (int j = 0; j < jsonDataArray.length(); j++) {
                            JSONObject jsonO = jsonDataArray.getJSONObject(j);
                            ////uid,name,contact,email,address,rdate,edate
                            userPojo = new com.website.yummywheels.pojo.UserPojo(jsonO.getString("data0"), jsonO.getString("data1")
                                    , jsonO.getString("data2"), jsonO.getString("data3"),
                                    jsonO.getString("data4"), jsonO.getString("data5")
                                    , jsonO.getString("data6"),
                                    jsonO.getString("data7"));
                            allUsersList.add(userPojo);
                        }
                        setLV();
                    } else if (ans.compareTo("no") == 0) {
                        noRecords.setVisibility(View.VISIBLE);  //visible, gone
                        usersListView.setAdapter(null);
                    } else if (ans.compareTo("error") == 0) {
                        String error = json.getString("Data");
                        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    stopAnimation();
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            stopAnimation();
        }
    }

    private void setLV() {
        noRecords.setVisibility(View.GONE);
        UsersListViewAdapter usersAdap = new UsersListViewAdapter(getContext(), allUsersList, ViewUserFragment.this);
        usersListView.setAdapter(usersAdap);
    }

    private void stopAnimation() {
        if (dialogLoader.isShowing())
            dialogLoader.dismiss();
    }

    private void startAnimation() {
        dialogLoader = new Dialog(getContext(), R.style.AppTheme_NoActionBar);
        dialogLoader.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#8D000000")));
        final View view = getActivity().getLayoutInflater().inflate(R.layout.custom_dialog_loader, null);
        LottieAnimationView animationView = view.findViewById(R.id.loader);
        animationView.playAnimation();
        dialogLoader.setContentView(view);
        dialogLoader.setCancelable(false);
        dialogLoader.show();
    }

    public void onUserDetailClicked(int position) {
        Intent intentDetail = new Intent(getActivity(), DetailUserActivity.class);
        intentDetail.putExtra(com.website.yummywheels.utils.UtilConstants.USER_KEY, allUsersList.get(position));
        startActivity(intentDetail);
    }
}
