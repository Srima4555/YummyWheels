package com.deliveryperson;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

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
    public static final String DPERSON_ID = "DPERSON_ID";
    public static final String MENU_POJO = "MENU_POJO";
    public static final String FROM_CURRENT_TAB = "FROM_CURRENT_TAB";
    public static final String STATUS_SELECTED = "STATUS_SELECTED";
    public static final String LOGGEDIN = "LOGGEDIN";

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
