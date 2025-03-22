package com.deliveryperson;

import android.content.Context;
import android.content.SharedPreferences;

public class UserPref {

    public static String getName(Context con) {
        return SP(con).getString("name", "");
    }

    public static String getId(Context con) {
        return SP(con).getString("mid", "");
    }

    public static String getStatus(Context con) {
        return SP(con).getString("status", "");
    }

    public static void setValue(Context con, String key, String value) {
        SharedPreferences.Editor editor = SP(con).edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static SharedPreferences SP(Context con) {
        SharedPreferences sp = con.getSharedPreferences("FDU", Context.MODE_PRIVATE);
        return sp;
    }
}
