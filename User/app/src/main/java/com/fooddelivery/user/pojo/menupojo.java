package com.fooddelivery.user.pojo;

public class menupojo {

    String fid,name,desc,price,category,image,rid,status,isveg,isfav;

    public menupojo(String fid,String name,String desc,String price,String category,String image,String rid,String status,String isveg,String isfav)
    {
        this.fid=fid;
        this.name=name;
        this.desc=desc;
        this.price=price;
        this.category=category;
        this.image=image;
        this.rid=rid;
        this.status=status;
        this.isveg=isveg;
        this.isfav=isfav;
    }

    public String getStatus() {
        return status;
    }

    public String getRid() {
        return rid;
    }

    public String getCategory() {
        return category;
    }

    public String getDesc() {
        return desc;
    }

    public String getFid() {
        return fid;
    }

    public String getImage() {
        return image;
    }

    public String getIsfav() {
        return isfav;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getIsveg() {
        return isveg;
    }

    public void setIsfav(String isfav)
    {
        this.isfav=isfav;
    }
}
