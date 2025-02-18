package com.website.yummywheels.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.website.yummywheels.R;
import com.website.yummywheels.pojo.UserPojo;

import java.util.ArrayList;

public class UsersListViewAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<UserPojo> userPojoArrayList;
    private com.website.yummywheels.pojo.UserPojo userPojo;
    private com.website.yummywheels.fragment.ViewUserFragment activity;

    public UsersListViewAdapter(Context context, ArrayList<UserPojo> userPojoArrayList, com.website.yummywheels.fragment.ViewUserFragment activity) {
        this.context = context;
        this.userPojoArrayList = userPojoArrayList;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return userPojoArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return userPojoArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewH viewH = new ViewH();
        if (convertView == null){
            //inflate
            convertView = LayoutInflater.from(context).inflate(R.layout.item_view_users, parent, false);
            viewH.uid = convertView.findViewById(R.id.tv_user_uid);
            viewH.name = convertView.findViewById(R.id.tv_user_name);
            viewH.email = convertView.findViewById(R.id.tv_uemail);
            viewH.pin = convertView.findViewById(R.id.tv_upincode);
            viewH.contact = convertView.findViewById(R.id.tv_ucont);
            convertView.setTag(viewH);
        }else {
            viewH = (ViewH) convertView.getTag();
        }
        //populate
        if ((userPojo = userPojoArrayList.get(position)) != null){
            viewH.uid.setText("#"+userPojo.getUid());
            viewH.name.setText(userPojo.getName());
            viewH.email.setText(userPojo.getEmail());
            viewH.contact.setText(userPojo.getContact());
            viewH.pin.setText(userPojo.getPincode());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.onUserDetailClicked(position);
                }
            });
        }
        return convertView;
    }

    private static class ViewH{
        private TextView uid, name, email, contact, pin;
    }
}
