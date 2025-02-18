package com.website.yummywheels.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.website.yummywheels.R;
import com.website.yummywheels.pojo.ReviewPojo;

import java.util.ArrayList;

public class ReviewAdap extends BaseAdapter {
    private Context context;
    private ArrayList<ReviewPojo> reviewPojos;

    public ReviewAdap(Context context, ArrayList<ReviewPojo> reviewPojos) {
        this.context = context;
        this.reviewPojos = reviewPojos;
    }

    @Override
    public int getCount() {
        return reviewPojos.size();
    }

    @Override
    public Object getItem(int position) {
        return reviewPojos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewH viewH = new ViewH();
        if (convertView == null) {
            //inflate
            convertView = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false);
            viewH.nameTV = convertView.findViewById(R.id.tv_name_review);
            viewH.ratingTV = convertView.findViewById(R.id.tv_rating);
            viewH.reviewIdTV = convertView.findViewById(R.id.tv_r_id);
            viewH.timeDateTV = convertView.findViewById(R.id.tv_timedate);
            viewH.reviewTV = convertView.findViewById(R.id.tv_review);
            convertView.setTag(viewH);
        } else {
            viewH = (ViewH) convertView.getTag();
        }
        //populate
        final ReviewPojo reviewPojo;
        if ((reviewPojo = reviewPojos.get(position)) != null) {
            viewH.nameTV.setText(reviewPojo.getReviewerName());
            viewH.ratingTV.setText("Rating: "+reviewPojo.getRating());
            viewH.reviewTV.setText("- "+reviewPojo.getReview());
            viewH.timeDateTV.setText("\tposted on "+reviewPojo.getDateTime());
            viewH.reviewIdTV.setText("#"+reviewPojo.getRid());
        }
        return convertView;
    }

    static class ViewH {

        private TextView nameTV, reviewTV, ratingTV, timeDateTV, reviewIdTV;
    }
}
