package com.website.yummywheels.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class LoginSharedPref {
    private static final String SPREF_FILE = "SPREF_LOGIN";
    private static final String NAME_LOGIN_KEY = "name";
    private static final String UID_LOGIN_KEY = "uid";


    public static void setUnameKey(Context context, String name) {
        SharedPreferences sprefLogin = context.getSharedPreferences(SPREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sprefLogin.edit();
        editor.putString(NAME_LOGIN_KEY, name);
        editor.apply();
    }
    public static String getUnameKey(Context context) {
        SharedPreferences sprefLogin = context.getSharedPreferences(SPREF_FILE, Context.MODE_PRIVATE);
        return sprefLogin.getString(NAME_LOGIN_KEY, "");
    }
}
