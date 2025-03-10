package com.fooddelivery.user.webservice;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hp on 10/18/2017.
 */

public class JSONParse {

    public String parse(JSONObject json) {
        String p="parse";
        try {
            p=json.getString("Value");
        } catch (JSONException e) {
            p=e.getMessage();
        }
        return p;
    }
}
