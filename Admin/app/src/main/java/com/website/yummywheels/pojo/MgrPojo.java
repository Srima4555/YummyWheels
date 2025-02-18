package com.website.yummywheels.pojo;

public class MgrPojo {
    private String mid, name, email, contact;

    public MgrPojo(String mid, String name, String email, String contact) {
        this.mid = mid;
        this.name = name;
        this.email = email;
        this.contact = contact;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
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
}
