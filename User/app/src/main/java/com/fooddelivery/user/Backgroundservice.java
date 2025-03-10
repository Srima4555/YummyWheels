package com.fooddelivery.user;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.fooddelivery.user.Activity.MainActivity;
import com.fooddelivery.user.helper.UserPref;
import com.fooddelivery.user.restaurant.Cart;
import com.fooddelivery.user.webservice.JSONParse;
import com.fooddelivery.user.webservice.RestAPI;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Backgroundservice extends Service{

    String oid,status;
    boolean ans=false;
    Timer timer;
    TimerTask task;
    Handler hand=new Handler();
    public static String CHANNEL_ID = "ChildChannel";


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        timer=new Timer();
        inittimer();
        timer.schedule(task,0,2000);
    }

    public  void inittimer()
    {
        task=new TimerTask() {
            @Override
            public void run() {
                hand.post(new TimerTask() {
                    @Override
                    public void run() {
                        if(!ans)
                        {
                            String uid= UserPref.getId(Backgroundservice.this);
                            if(uid.length()>0)
                            {
                                ans=true;
                                new getNotification().execute(uid);
                            }
                        }
                    }
                });
            }
        };
    }

    private class getNotification extends AsyncTask<String,JSONObject,String>
    {

        @Override
        protected String doInBackground(String... params) {
            String a="back";
            RestAPI api=new RestAPI();
            try {
                JSONObject json=api.UgetNotification(params[0]);
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
            Log.d("BACKGROUND",s);
            if (s.contains("Unable to resolve host")) {

            } else {

                try {
                    JSONObject json = new JSONObject(s);
                    String ans = json.getString("status");
                    if (ans.compareTo("no") == 0) {

                    } else if (ans.compareTo("ok") == 0) {
                        JSONArray jarry = json.getJSONArray("Data");
                        JSONObject jobj = jarry.getJSONObject(0);

                        oid= jobj.getString("data0");
                        status= jobj.getString("data1");

                        send_notification(oid,status);

                    } else if (ans.compareTo("error") == 0) {
                        String error = json.getString("Data");
                    } else {
                    }
                } catch (Exception e) {
                }
            }

            ans=false;
        }
    }

    public void send_notification(String oid,String status)
    {
        String title="Your order is "+status;
        String content="Order Id "+oid+" is "+status;
        int randomValue = new Random().nextInt(10000) + 1;

        Intent notificationIntent = new Intent(Backgroundservice.this, MainActivity.class);
        notificationIntent.putExtra("pos",2);
        notificationIntent.putExtra("nid",randomValue);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent contentIntent = PendingIntent.getActivity(Backgroundservice.this,randomValue, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        createNotificationChannel(Backgroundservice.this, CHANNEL_ID);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(Backgroundservice.this, CHANNEL_ID)
                .setSmallIcon(R.drawable.nicon)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true)
                .setContentIntent(contentIntent);

        Notification n = builder.build();
        nm.notify("FDU", randomValue, n);
    }


    public static void createNotificationChannel(@NonNull Context context, @NonNull String CHANNEL_ID) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name ="Channel name";
            String description ="channel_description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            } else {
                Log.d("NotificationLog", "NotificationManagerNull");
            }
        }
    }
}
