package com.deliveryperson;

import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;

public class LocationUpdateService extends Service {
    private Timer timer;
    private TimerTask task;
    public static String currentLatLong;
    private LocationHelper locationHelper;

    public LocationUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        locationHelper = new LocationHelper(LocationUpdateService.this);
        timer = new Timer();
        initTimerTask();
        timer.schedule(task, 0, 20_000);
    }

    private void initTimerTask() {
        task = new TimerTask() {
            @Override
            public void run() {
                currentLatLong = locationHelper.getLtLng();
                if (currentLatLong != null &&
                        UserPref.getStatus(LocationUpdateService.this).equalsIgnoreCase("yes"))
                    sendCurrentLocation();
            }
        };
    }

    private void sendCurrentLocation() {
        LocationTask task = new LocationTask();
        task.execute(UserPref.getId(LocationUpdateService.this), currentLatLong);
    }

    private class LocationTask extends AsyncTask<String, JSONObject, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String a = "back";
            RestAPI api = new RestAPI();
            try {
                JSONObject json = api.DLocationUpdates(params[0], params[1]);
                JSONParse jp = new JSONParse();
                a = jp.parse(json);
            } catch (Exception e) {
                a = e.getMessage();
            }
            return a;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("reply LUService", s);
            super.onPostExecute(s);

            if (s.contains("Unable to resolve host")) {
                Log.i(TAG, "LocationUpdateService: Unable to resolve host");
            } else {
                try {
                    JSONObject json = new JSONObject(s);
                    String ans = json.getString("status");
                    if (ans.compareTo("true") == 0) {
                        Log.i(TAG, "LocationUpdateService sent" + s);
                    } else if (ans.compareTo("error") == 0) {
                        String error = json.getString("Data");
                        Log.i(TAG, "LocationUpdateService: - " + error);
                    } else {
                        Log.i(TAG, "LocationUpdateService: - " + ans);
                    }
                } catch (Exception e) {
                    Log.i(TAG, "LocationUpdateService: catch - " + e.getMessage());
                }
            }
        }
    }
}
