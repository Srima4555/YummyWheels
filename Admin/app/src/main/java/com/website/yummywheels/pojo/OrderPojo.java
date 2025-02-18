package com.website.yummywheels.pojo;

import java.io.Serializable;

public class OrderPojo implements Serializable {
    private String oid, tprice, rid, uid, add, city, pincode, latlng, phone,
            odate, otime, ddate, status, payment, did, restname, uname, dname;

    public OrderPojo(String oid, String tprice, String rid, String uid, String add, String city, String pincode, String latlng, String phone, String odate, String otime, String ddate, String status, String payment, String did, String restname, String uname, String dname) {
        this.oid = oid;
        this.tprice = tprice;
        this.rid = rid;
        this.uid = uid;
        this.add = add;
        this.city = city;
        this.pincode = pincode;
        this.latlng = latlng;
        this.phone = phone;
        this.odate = odate;
        this.otime = otime;
        this.ddate = ddate;
        this.status = status;
        this.payment = payment;
        this.did = did;
        this.restname = restname;
        this.uname = uname;
        this.dname = dname;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getTprice() {
        return tprice;
    }

    public void setTprice(String tprice) {
        this.tprice = tprice;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAdd() {
        return add;
    }

    public void setAdd(String add) {
        this.add = add;
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

    public String getLatlng() {
        return latlng;
    }

    public void setLatlng(String latlng) {
        this.latlng = latlng;
    }

    public String getOdate() {
        return odate;
    }

    public void setOdate(String odate) {
        this.odate = odate;
    }

    public String getOtime() {
        return otime;
    }

    public void setOtime(String otime) {
        this.otime = otime;
    }

    public String getDdate() {
        return ddate;
    }

    public void setDdate(String ddate) {
        this.ddate = ddate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public String getRestname() {
        return restname;
    }

    public void setRestname(String restname) {
        this.restname = restname;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getDname() {
        return dname;
    }

    public void setDname(String dname) {
        this.dname = dname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
