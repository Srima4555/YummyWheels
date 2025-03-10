package com.fooddelivery.user.orders;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fooddelivery.user.R;
import com.fooddelivery.user.restaurant.Menus;
import com.fooddelivery.user.webservice.JSONParse;
import com.fooddelivery.user.webservice.RestAPI;

import org.json.JSONObject;

import java.util.ArrayList;


public class BookingAdap extends BaseAdapter {
    private static final String TAGG = "TAG";
    private Context context;
    private ArrayList<BookingPojo> bookingPojos;
    private BookingPojo bookingPojo;


    public BookingAdap(Context context, ArrayList<BookingPojo> issuedBookPojos) {
        this.context = context;
        this.bookingPojos = issuedBookPojos;
    }

    @Override
    public int getCount() {
        return bookingPojos.size();
    }

    @Override
    public Object getItem(int position) {
        return bookingPojos.get(position);
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_view_order_detaillist, parent, false);
            viewHolder.qtyTv = convertView.findViewById(R.id.tv_qty_inlist);
            viewHolder.priceTV = convertView.findViewById(R.id.tv_tot_inlist);  //format
            viewHolder.nameTv = convertView.findViewById(R.id.tv_cat_inlist);
            viewHolder.idTV = convertView.findViewById(R.id.tv_fid_inlist);   //clicklistener


            //setTag
            convertView.setTag(viewHolder);

        } else {
            //getTag
            viewHolder = (VHolder) convertView.getTag();
        }

        if ((bookingPojo = bookingPojos.get(position)) != null) {
            viewHolder.priceTV.setText(context.getResources().getString(R.string.price, bookingPojo.getPrice()));
            viewHolder.nameTv.setText("Food item : "+ bookingPojo.getName());
            viewHolder.qtyTv.setText("Qty: "+ bookingPojo.getQuantity());
            viewHolder.idTV.setText("#Food id " + bookingPojo.getFid());

        }   //[setText]
        return convertView;
    }

    private static class VHolder {
        private TextView priceTV, nameTv, qtyTv, idTV;

    }
}
