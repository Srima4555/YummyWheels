package com.deliveryperson;

import java.io.Serializable;
import java.util.ArrayList;

public class BookingPojo implements Serializable {
    //"Oid","TPrice","Rid","Uid","Add","City","Pincode","Latlng","Phone","Odate","Otime","Ddate","Status",
    // "Payment","Did","Rname","Rcont"
    //Data[] - Fid,Name,Quantity,Price
    private String oid, tprice, rid, uid, add, city, pincode, latlng, phone,
    odate, otime, dDate, status, payment, did, uname, rname, rcontact;
    private String fid, name,quantity, price;
    private ArrayList<BookingPojo> bookingPojos;

    public BookingPojo(String fid, String name, String quantity, String price) {
        this.fid = fid;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    public BookingPojo(String oid, String tprice, String rid, String uid, String add, String city, String pincode, String latlng, String phone, String odate, String otime, String dDate, String status, String payment, String did, String uname, String rname, String rcontact) {
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
        this.dDate = dDate;
        this.status = status;
        this.payment = payment;
        this.did = did;
        this.uname = uname;
        this.rname = rname;
        this.rcontact = rcontact;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getdDate() {
        return dDate;
    }

    public void setdDate(String dDate) {
        this.dDate = dDate;
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

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public ArrayList<BookingPojo> getBookingPojos() {
        return bookingPojos;
    }

    public void setBookingPojos(ArrayList<BookingPojo> bookingPojos) {
        this.bookingPojos = bookingPojos;
    }

    public String getRcontact() {
        return rcontact;
    }

    public void setRcontact(String rcontact) {
        this.rcontact = rcontact;
    }

    public String getRname() {
        return rname;
    }

    public void setRname(String rname) {
        this.rname = rname;
    }
}
