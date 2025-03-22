package com.deliveryperson;

class DeliveryPersonPojo {
    private String did, name, email, contact, latlng, city, pincode, status;

    public DeliveryPersonPojo(String did, String name, String contact, String email, String latlng, String city, String pincode, String status) {
        this.did = did;
        this.name = name;
        this.email = email;
        this.contact = contact;
        this.latlng = latlng;
        this.city = city;
        this.pincode = pincode;
        this.status = status;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getLatlng() {
        return latlng;
    }

    public void setLatlng(String latlng) {
        this.latlng = latlng;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
