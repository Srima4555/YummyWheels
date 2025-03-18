package com.managerestaurant;

import org.json.JSONException;
import org.json.JSONObject;

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
