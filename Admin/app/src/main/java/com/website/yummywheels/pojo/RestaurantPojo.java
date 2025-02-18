package com.website.yummywheels.pojo;

import java.io.Serializable;

public class RestaurantPojo implements Serializable {
    private String rid, rname, contact, address, city, pincode, cuisine, latlng, time, cost, logo, minorder, type, status;

    public RestaurantPojo(String rid, String rname, String contact, String address, String city, String pincode, String cuisine, String latlng, String time, String cost, String logo, String minorder, String type, String status) {
        this.rid = rid;
        this.rname = rname;
        this.contact = contact;
        this.address = address;
        this.city = city;
        this.pincode = pincode;
        this.cuisine = cuisine;
        this.latlng = latlng;
        this.time = time;
        this.cost = cost;
        this.logo = logo;
        this.minorder = minorder;
        this.type = type;
        this.status = status;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getRname() {
        return rname;
    }

    public void setRname(String rname) {
        this.rname = rname;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getCuisine() {
        return cuisine;
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    public String getLatlng() {
        return latlng;
    }

    public void setLatlng(String latlng) {
        this.latlng = latlng;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getMinorder() {
        return minorder;
    }

    public void setMinorder(String minorder) {
        this.minorder = minorder;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
