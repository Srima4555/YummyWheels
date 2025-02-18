package com.website.yummywheels.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class UtilConstants {
    public static final String TAG = "TAG";
    public static final String USER_ID_KEY = "uid_key";
    public static final String LOGO_KEY = "LOGO_KEY";
    public static final String DETAIL_SHAREDDOC_KEY = "DETAIL_SHAREDDOC_KEY";
    public static final String FROM_SERVICE_NOTI = "service_noti";
    public static final String CHANNEL_ID = "ChannelNotiService";
    public static final String SHORTCUT_ADD_DOC_TYPE_KEY = "SHORTCUT_ADD_DOC_TYPE";
    public static final String USER_KEY = "USER_KEY";
    public static final String FOOD_KEY = "FOOD_KEY";
    public static final String ORDER_KEY = "ORDER_KEY";
    public static final String CHANGE_CURR_STATUS_KEY = "CHANGE_CURR_STATUS_KEY";
    public static final String RESTAU_ID = "RESTAU_ID";
    public static final String DPERSON_POJO = "DPERSON_POJO";

    public static <T> void forLoop(ArrayList<T> list) {
        Log.d(UtilConstants.TAG, "list->");
        for (T str :
                list) {
            Log.d(UtilConstants.TAG, "each: " + str);
        }
        //
        Log.d(UtilConstants.TAG, list.size() + ": AL size");
    }
    public static final String TYPE_KEY = "TYPE_KEY";
    public static final String DETAIL_DOC_KEY= "DETAIL_DOC_KEY";

    public static Bitmap decodeBitmap(String imageString) {
        ByteArrayInputStream bArrIS = new ByteArrayInputStream(Base64.decode(imageString, Base64.DEFAULT));
        return BitmapFactory.decodeStream(bArrIS);
    }

    public static String encodeBitmap(Bitmap bitmap) {
        ByteArrayOutputStream barrOS = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, barrOS);
        return Base64.encodeToString(barrOS.toByteArray(), Base64.DEFAULT);
    }
}
