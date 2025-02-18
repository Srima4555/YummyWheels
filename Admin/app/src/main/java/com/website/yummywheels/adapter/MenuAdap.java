package com.website.yummywheels.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.website.yummywheels.MenuActivity;
import com.website.yummywheels.R;
import com.website.yummywheels.pojo.MenuPojo;
import com.website.yummywheels.utils.UtilConstants;

import java.util.ArrayList;

import static com.website.yummywheels.utils.UtilConstants.TAG;

public class MenuAdap extends BaseAdapter {
    private Context context;
    private ArrayList<MenuPojo> foodPojos;
    private MenuActivity menuActivity;

    public MenuAdap(Context context, ArrayList<MenuPojo> bookPojoArrayList, MenuActivity menuActivity) {
        this.context = context;
        this.foodPojos = bookPojoArrayList;
        this.menuActivity = menuActivity;
    }

    @Override
    public int getCount() {
        return foodPojos.size();
    }

    @Override
    public Object getItem(int position) {
        return foodPojos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewH viewH = new ViewH();
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_food, parent, false);
            viewH.menuIV = convertView.findViewById(R.id.iv_menu);
            viewH.nameTV = convertView.findViewById(R.id.tv_bname);
            viewH.priceTV = convertView.findViewById(R.id.tv_fprice);
            viewH.statusET = convertView.findViewById(R.id.tv_status_menu);
            viewH.categET = convertView.findViewById(R.id.tv_categ);
            viewH.descriptionTV = convertView.findViewById(R.id.tv_fdesc);
            viewH.typeTV = convertView.findViewById(R.id.tv_type_menu);
            convertView.setTag(viewH);
        } else {
            viewH = (ViewH) convertView.getTag();
        }
        final com.website.yummywheels.pojo.MenuPojo foodPojo;
        if ((foodPojo = foodPojos.get(position)) != null) {
            viewH.menuIV.setImageBitmap(UtilConstants.decodeBitmap(foodPojo.getImage()));
            Log.i("TAG", "getView: " + foodPojo.getName());
            viewH.nameTV.setText(foodPojo.getName());
            viewH.priceTV.setText(context.getString(R.string.price, foodPojo.getPrice()));
            viewH.descriptionTV.setText(foodPojo.getDesc());
            viewH.categET.setText(foodPojo.getCategory());
            Log.i(TAG, "getView: "+foodPojo.getType());
            if (foodPojo.getType().equalsIgnoreCase("true") ||
                    foodPojo.getType().equalsIgnoreCase("yes")) {
                viewH.typeTV.setText("Type: Vegetarian");
            } else {
                viewH.typeTV.setText("Type: Non Vegetarian");
            }
            if (foodPojo.getStatus().equalsIgnoreCase("yes"))
                viewH.statusET.setText("Food item: Available");
            else
                viewH.statusET.setText("Food item: Not Available");
        }
        return convertView;
    }

    static class ViewH {
        private TextView nameTV, descriptionTV, priceTV, categET, statusET, typeTV;
        private ImageView menuIV;
    }
}
