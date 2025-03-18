package com.managerestaurant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class TransacAdap extends BaseAdapter {
    private Context context;
    private ArrayList<TransacPojo> transacPojos;

    public TransacAdap(Context context, ArrayList<TransacPojo> transacPojos) {
        this.context = context;
        this.transacPojos = transacPojos;
    }

    @Override
    public int getCount() {
        return transacPojos.size();
    }

    @Override
    public Object getItem(int position) {
        return transacPojos.get(position);
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_transac, parent, false);
            viewH.nameTV = convertView.findViewById(R.id.tv_name_review);
            viewH.paidTV = convertView.findViewById(R.id.tv_paid);
            viewH.timeDateTV = convertView.findViewById(R.id.tv_timedate);
            viewH.typeTV = convertView.findViewById(R.id.tv_type);
            convertView.setTag(viewH);
        } else {
            viewH = (ViewH) convertView.getTag();
        }
        //populate
        final TransacPojo reviewPojo;
        if ((reviewPojo = transacPojos.get(position)) != null) {
            viewH.nameTV.setText(reviewPojo.getUname());
            viewH.paidTV.setText(context.getResources().getString(R.string.amount_paid_u20b9,reviewPojo.getPrice()));
            viewH.typeTV.setText(context.getResources().getString(R.string.payment_type_1_,reviewPojo.getType()));
            viewH.timeDateTV.setText(context.getResources().getString(R.string.transaction_successfully_done_on_1_s_2_s,reviewPojo.getDt()));
        }
        return convertView;
    }

    static class ViewH {
        private TextView nameTV, typeTV, paidTV, timeDateTV;
    }
}
