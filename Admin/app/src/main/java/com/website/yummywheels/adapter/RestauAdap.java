package com.website.yummywheels.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.website.yummywheels.R;
import com.website.yummywheels.fragment.RestauFragment;
import com.website.yummywheels.pojo.RestaurantPojo;

import java.util.ArrayList;

import static com.website.yummywheels.utils.UtilConstants.TAG;

public class RestauAdap extends BaseAdapter {
    private Context context;
    private ArrayList<RestaurantPojo> restaurantPojos;
    private RestauFragment restauFragment;

    public RestauAdap(Context context, ArrayList<RestaurantPojo> restauPojo,
                      RestauFragment restauFragment) {
        this.context = context;
        this.restaurantPojos = restauPojo;
        this.restauFragment = restauFragment;
    }

    @Override
    public int getCount() {
        return restaurantPojos.size();
    }

    @Override
    public Object getItem(int position) {
        return restaurantPojos.get(position);
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_restau, parent, false);
            viewH.cityTV = convertView.findViewById(R.id.tv_city_r);
            viewH.nameTV = convertView.findViewById(R.id.tv_rname);
            viewH.statusTV = convertView.findViewById(R.id.tv_status_r);
            viewH.typeTV = convertView.findViewById(R.id.tv_type_r);
            convertView.setTag(viewH);
        } else {
            viewH = (ViewH) convertView.getTag();
        }
        //populate
        final RestaurantPojo restaurantPojo;
        if ((restaurantPojo = restaurantPojos.get(position)) != null) {
            viewH.cityTV.setText(restaurantPojo.getCity());
            Log.i(TAG, "getView: " + restaurantPojo.getRname());
            viewH.nameTV.setText(restaurantPojo.getRname());
            if (restaurantPojo.getStatus().equalsIgnoreCase("yes") || restaurantPojo.getStatus().equalsIgnoreCase("open")) {
                viewH.statusTV.setText("OPEN");
            }else viewH.statusTV.setText("CLOSED");
            viewH.typeTV.setText(restaurantPojo.getType());
            viewH.cityTV.getRootView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    restauFragment.onFoodItemClicked(restaurantPojo);
                }
            });
        }
        return convertView;
    }

    static class ViewH {

        private TextView cityTV, nameTV, typeTV, statusTV;
    }
}
