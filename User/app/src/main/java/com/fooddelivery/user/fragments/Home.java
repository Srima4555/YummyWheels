package com.fooddelivery.user.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fooddelivery.user.R;

import java.util.Calendar;


public class Home extends Fragment{

    CardView cuisine,bookings,user,fav;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.home,container,false);
        cuisine=(CardView)v.findViewById(R.id.hcuisine);
        bookings=(CardView)v.findViewById(R.id.hbookings);
        user=(CardView)v.findViewById(R.id.huser);
        fav=(CardView)v.findViewById(R.id.hfav);

        cuisine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendBroadcast(1);
            }
        });

        bookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendBroadcast(2);
            }
        });

        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendBroadcast(5);
            }
        });

        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendBroadcast(3);
            }
        });

        return v;
    }

    public void sendBroadcast(int pos)
    {
        Intent intent = new Intent("rfu");
        intent.putExtra("pos",pos);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }
}
