package com.managerestaurant;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.managerestaurant.R;

import java.util.ArrayList;


public class OrderAdap extends BaseAdapter {
    private static final String TAGG = "TAG";
    private Context context;
    private ArrayList<BookingPojo> bookingPojos;
    private BookingPojo orderPojo;
    private Booking_current bookingCurrent;
    private Booking_prev bookingPrev;
    private Booking_upcoming bookingUpcoming;
    private Fragment fragment;

    public OrderAdap(Context context, ArrayList<BookingPojo> issuedBookPojos, Booking_upcoming bookingUpcoming) {
        this.context = context;
        this.bookingPojos = issuedBookPojos;
        fragment = this.bookingUpcoming = bookingUpcoming;
    }
    public OrderAdap(Context context, ArrayList<BookingPojo> issuedBookPojos, Booking_prev bookingPrev) {
        this.context = context;
        this.bookingPojos = issuedBookPojos;
        fragment = this.bookingPrev = bookingPrev;
    }

    public OrderAdap(Context context, ArrayList<BookingPojo> issuedBookPojos, Booking_current bookingCurrent) {
        this.context = context;
        this.bookingPojos = issuedBookPojos;
        fragment = this.bookingCurrent = bookingCurrent;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.booking_listitem, parent, false);
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
        if ((orderPojo = bookingPojos.get(position)) != null) {
            viewHolder.priceTV.setText(orderPojo.getTprice());
            viewHolder.dateTimeTV.setText("Ordered on: "+ orderPojo.getOdate() + " "+ orderPojo.getOtime());
            viewHolder.fromRestau.setText("Ordered by: "+ orderPojo.getUname());
            viewHolder.oidTV.setText("#Order id " + orderPojo.getOid());
            viewHolder.currentStatus.setText(orderPojo.getStatus());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bookingCurrent != null)
                        bookingCurrent.openOrderDetailActivity(bookingPojos.get(position));
                    else if (bookingUpcoming != null)
                        bookingUpcoming.openOrderDetailActivity(bookingPojos.get(position));
                    else
                    if (bookingPrev != null)
                        bookingPrev.openOrderDetailActivity(bookingPojos.get(position));
                }
            });
            Log.i(TAGG, "getView: src ");
        }   //[setText]
        return convertView;
    }

    private static class VHolder {
        public TextView currentStatus;
        private TextView priceTV, dateTimeTV, fromRestau, oidTV;
    }
}
