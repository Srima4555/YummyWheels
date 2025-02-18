package com.website.yummywheels.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.website.yummywheels.R;
import com.website.yummywheels.fragment.OrderedFragment;
import com.website.yummywheels.pojo.OrderPojo;

import java.util.ArrayList;

import static com.website.yummywheels.utils.UtilConstants.TAG;


public class OrderAdap extends BaseAdapter {
    private final String src;
    private Context context;
    private ArrayList<OrderPojo> orderPojos;
    private com.website.yummywheels.pojo.OrderPojo orderPojo;
    private OrderedFragment fragment;

    public OrderAdap(Context context, ArrayList<OrderPojo> issuedBookPojos, String src) {
        this.context = context;
        this.orderPojos = issuedBookPojos;
        this.src = src;
    }

    @Override
    public int getCount() {
        return orderPojos.size();
    }

    @Override
    public Object getItem(int position) {
        return orderPojos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        VHolder viewHolder = new VHolder();
        if (convertView == null) {
            ///inflate
            convertView = LayoutInflater.from(context).inflate(R.layout.item_orders, parent, false);
            viewHolder.fromRestau = convertView.findViewById(R.id.tv_from_restau);
            viewHolder.priceTV = convertView.findViewById(R.id.tv_tot_price);  //format
            viewHolder.dateTimeTV = convertView.findViewById(R.id.tv_datetime);
            viewHolder.currentStatus = convertView.findViewById(R.id.tv_status_current);
            viewHolder.oidTV = convertView.findViewById(R.id.tv_oid);   //clicklistener
            //setTag
            convertView.setTag(viewHolder);
        } else {
            //getTag
            viewHolder = (VHolder) convertView.getTag();
        }
        //settexts
        if ((orderPojo = orderPojos.get(position)) != null) {
            viewHolder.priceTV.setText(context.getString(R.string.price, orderPojo.getTprice()));
            viewHolder.dateTimeTV.setText("Ordered on: "+ orderPojo.getOdate() + " "+ orderPojo.getOtime());
            viewHolder.fromRestau.setText("Restaurant: "+ orderPojo.getRestname());
            viewHolder.oidTV.setText("#Order id " + orderPojo.getOid());
            viewHolder.currentStatus.setText(orderPojo.getStatus());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (fragment != null)
                        fragment.openOrderDetailActivity(orderPojos.get(position));
                }
            });
            Log.i(TAG, "getView: src " + src);
        }   //[setText]
        return convertView;
    }
    public void setListenerCurr(OrderedFragment frag) {
        this.fragment = frag;
    }

    private static class VHolder {
        public TextView currentStatus;
        private TextView priceTV, dateTimeTV, fromRestau, oidTV;
    }
}
