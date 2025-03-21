package com.managerestaurant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class DPersonAdap extends BaseAdapter {
    private Context context;
    private ArrayList<DPersonPojo> dPersonPojos;
    private DPersonPojo dPersonPojo;
    private OrderDetailActivity activity;
    private DecimalFormat format;

    public DPersonAdap(Context context, ArrayList<DPersonPojo> userPojoArrayList, OrderDetailActivity activity) {
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
            viewH.distTV = convertView.findViewById(R.id.tv_dist);

            convertView.setTag(viewH);
        } else {
            viewH = (ViewH) convertView.getTag();
        }
        //populate
        if ((dPersonPojo = dPersonPojos.get(position)) != null) {
            viewH.didTV.setText("#" + dPersonPojo.getDid());
            viewH.emailTV.setText(dPersonPojo.getEmail());
            if (dPersonPojo.getDist().equalsIgnoreCase("No")) {
                viewH.distTV.setText("Not assigned");
            } else {
                format = new DecimalFormat("#.##");
                viewH.distTV.setText(format.format(Double.parseDouble(dPersonPojo.getDist()))+" Km");
            }
            viewH.nameTV.setText(dPersonPojo.getName());
            viewH.contaTv.setText(context.getResources().getString(R.string.contact_no_1_s, dPersonPojo.getContact()));
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.onPersonSelected(dPersonPojo);
                }
            });

        }
        return convertView;
    }

    class ViewH {
        private TextView didTV, emailTV, contaTv, nameTV, distTV;
    }
}
