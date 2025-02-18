package com.website.yummywheels;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.EditText;
import android.widget.ScrollView;

public class OrderDetailActivity extends AppCompatActivity {
    private boolean isUpdate;
    private static final CharSequence CANNOTBEEMPTY = "This field cannot be empty";
    private ScrollView sView;
    private EditText oIdET, statusET, restauET, totPriceET, addressET, timeOfOrderET, customerNameET, deliveryAssignET;
    private com.website.yummywheels.pojo.OrderPojo orderPojo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        Toolbar toolbar = findViewById(R.id.toolbar_add_order);
        toolbar.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_material);
        setSupportActionBar(toolbar);
        initUI();
    }

    private void initUI() {
        sView = findViewById(R.id.sv_order_detail);
        oIdET = findViewById(R.id.et_oid);
        oIdET.setEnabled(false);
        statusET = findViewById(R.id.et_status);
        statusET.setEnabled(false);
        restauET = findViewById(R.id.et_restau);
        restauET.setEnabled(false);
        totPriceET = findViewById(R.id.et_tot_price);
        totPriceET.setEnabled(false);
        addressET = findViewById(R.id.et_add);
        addressET.setEnabled(false);
        timeOfOrderET = findViewById(R.id.et_time_order);
        timeOfOrderET.setEnabled(false);
        customerNameET = findViewById(R.id.et_customername);
        customerNameET.setEnabled(false);
        deliveryAssignET = findViewById(R.id.et_delivery_assign);
        deliveryAssignET.setEnabled(false);
        if ((orderPojo = (com.website.yummywheels.pojo.OrderPojo) getIntent().getSerializableExtra(com.website.yummywheels.utils.UtilConstants.ORDER_KEY)) != null) {
            isUpdate = true;
            getSupportActionBar().setTitle("Order details");
            //populate
            oIdET.setText(orderPojo.getOid());
            statusET.setText(orderPojo.getStatus());
            Log.i("TAG", "initUI: " + orderPojo.getStatus());
            restauET.setText(orderPojo.getRestname());
            totPriceET.setText("\u20b9" + orderPojo.getTprice());
            addressET.setText(orderPojo.getAdd());
            timeOfOrderET.setText(orderPojo.getOdate() + " " + orderPojo.getOtime());
            customerNameET.setText(orderPojo.getUname());
            if (orderPojo.getDname().equalsIgnoreCase("-1")) {
                deliveryAssignET.setText("Not yet assigned");
            } else
                deliveryAssignET.setText(orderPojo.getDname());
        }

        /*if (getIntent().getBooleanExtra(com.website.yummywheels.utils.UtilConstants.CHANGE_CURR_STATUS_KEY, false)) {

        }*/
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
