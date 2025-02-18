package com.website.yummywheels.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.website.yummywheels.R;
import com.website.yummywheels.fragment.DPersonFragment;
import com.website.yummywheels.pojo.DPersonPojo;

import java.util.ArrayList;

public class DPersonAdap extends BaseAdapter {
    private Context context;
    private ArrayList<DPersonPojo> dPersonPojos;
    private com.website.yummywheels.pojo.DPersonPojo dPersonPojo;
    private DPersonFragment activity;

    public DPersonAdap(Context context, ArrayList<DPersonPojo> userPojoArrayList, DPersonFragment activity) {
        this.context = context;
        this.dPersonPojos = userPojoArrayList;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return dPersonPojos.size();
    }

    @Override
    public Object getItem(int position) {
        return dPersonPojos.get(position);
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_dperson, parent, false);
            viewH.didTV = convertView.findViewById(R.id.tv_tid);
            viewH.emailTV = convertView.findViewById(R.id.tv_email);
            viewH.contaTv = convertView.findViewById(R.id.tv_contact);
            viewH.nameTV = convertView.findViewById(R.id.tv_tname);
            viewH.statusTV = convertView.findViewById(R.id.tv_status);

            convertView.setTag(viewH);
        } else {
            viewH = (ViewH) convertView.getTag();
        }
        //populate
        if ((dPersonPojo = dPersonPojos.get(position)) != null) {
            viewH.didTV.setText("#" + dPersonPojo.getDid());
            viewH.emailTV.setText(dPersonPojo.getEmail());
            if (dPersonPojo.getStatus().equalsIgnoreCase("No")) {
                viewH.statusTV.setText("Not assigned");
            } else
                viewH.statusTV.setText("Assigned: " + dPersonPojo.getStatus());
            viewH.nameTV.setText(dPersonPojo.getName());
            viewH.contaTv.setText(context.getResources().getString(R.string.contact_no_1_s, dPersonPojo.getContact()));
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.onPersonClicked(dPersonPojos.get(position));
                }
            });

        }
        return convertView;
    }

    class ViewH {
        private TextView didTV, emailTV, contaTv, nameTV, statusTV;
    }
}
