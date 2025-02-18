package com.website.yummywheels.pojo;

import java.io.Serializable;

public class MenuPojo implements Serializable {
//    //fid,name,desc,price,category,image,rid,status
    private String fid, name, desc, price, category, image, rid, status, type;

    public MenuPojo(String fid, String name, String desc, String price, String category, String image, String rid, String status, String type) {
        this.fid = fid;
        this.name = name;
        this.desc = desc;
        this.price = price;
        this.category = category;
        this.image = image;
        this.rid = rid;
        this.status = status;
        this.type = type;
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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}