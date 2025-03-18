package com.managerestaurant;

public class ReviewPojo {
    //{ "status" : "ok","Data" :[{ "data0" : "1003", "data1" : "100", "data2" : "Loved it, One should definitely visit this restaurant.", "data3" : "4.9", "data4" : "2019/04/30 12:19", "data5" : "Rovel"}] }
    private String uid, rid, review, rating, dateTime, reviewerName;

    public ReviewPojo(String uid, String rid, String review, String rating, String dateTime, String reviewerName) {
        this.uid = uid;
        this.rid = rid;
        this.review = review;
        this.rating = rating;
        this.dateTime = dateTime;
        this.reviewerName = reviewerName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }
}
