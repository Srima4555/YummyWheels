package com.managerestaurant;

public class TransacPojo {
    private String uname, price, type, dt;

    public TransacPojo(String uname, String price, String type, String dt) {
        this.uname = uname;
        this.price = price;
        this.type = type;
        this.dt = dt;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDt() {
        return dt;
    }

    public void setDt(String dt) {
        this.dt = dt;
    }
}
