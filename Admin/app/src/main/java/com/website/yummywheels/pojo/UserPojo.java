package com.website.yummywheels.pojo;

import java.io.Serializable;

public class UserPojo implements Serializable {
    private String uid, name, contact, email, city, address, pincode, latlng;

    public UserPojo(String uid, String name, String contact, String email, String city, String address, String pincode, String latlng) {
        this.uid = uid;
        this.name = name;
        this.contact = contact;
        this.email = email;
        this.city = city;
        this.address = address;
        this.pincode = pincode;
        this.latlng = latlng;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getLatlng() {
        return latlng;
    }

    public void setLatlng(String latlng) {
        this.latlng = latlng;
    }
}
