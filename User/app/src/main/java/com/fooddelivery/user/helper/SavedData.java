package com.fooddelivery.user.helper;

import com.fooddelivery.user.pojo.restpojo;

public class SavedData {

    static double lat,lng;
    static double Curlat,Curlng;
    static restpojo rp;

    public static void setLat(double lat) {
        SavedData.lat = lat;
    }

    public static void setLng(double lng) {
        SavedData.lng = lng;
    }

    public static double getLat() {
        return lat;
    }

    public static double getLng() {
        return lng;
    }

    public static void setCurlat(double curlat) {
        Curlat = curlat;
    }

    public static void setCurlng(double curlng) {
        Curlng = curlng;
    }

    public static double getCurlat() {
        return Curlat;
    }

    public static double getCurlng() {
        return Curlng;
    }

    public static void setRp(restpojo rp) {
        SavedData.rp = rp;
    }

    public static restpojo getRp() {
        return rp;
    }
}
