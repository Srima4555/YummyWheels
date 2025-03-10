package com.fooddelivery.user.pojo;

import java.io.Serializable;
import java.util.ArrayList;

public class restpojo implements Serializable {
    String rid,rname,contact,address,city,pincode,cuisine,latlng,time,cost,logo,minorder,type,status,rating;

    public restpojo(String rid,String rname,String contact,String address,String city,String pincode,String cuisine,String latlng,
                    String time,String cost,String logo,String minorder,String type,String status,String rating)
    {
        this.rid=rid;
        this.rname=rname;
        this.contact=contact;
        this.address=address;
        this.city=city;
        this.pincode=pincode;
        this.cuisine=cuisine;
        this.latlng=latlng;
        this.time=time;
        this.cost=cost;
        this.logo=logo;
        this.minorder=minorder;
        this.type=type;
        this.status=status;
        this.rating=rating;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getContact() {
        return contact;
    }

    public String getCost() {
        return cost;
    }

    public String getCuisine() {
        return cuisine;
    }

    public String getLatlng() {
        return latlng;
    }

    public String getLogo() {
        return logo;
    }

    public String getMinorder() {
        return minorder;
    }

    public String getPincode() {
        return pincode;
    }

    public String getRating() {
        return rating;
    }

    public String getRid() {
        return rid;
    }

    public String getRname() {
        return rname;
    }

    public String getStatus() {
        return status;
    }

    public String getTime() {
        return time;
    }

    public String getType() {
        return type;
    }

}
