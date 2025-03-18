package com.managerestaurant;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class OrderActivity extends AppCompatActivity {
    private String rName = "Restaurant";
    private RestaurantPojo restaurantPojo;
    private Fragment frag;
    private FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        //restaurantPojo
        getSupportActionBar().setTitle("Manage " + rName);
        frag = new com.managerestaurant.Bookings();
        fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.frame, frag).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_order, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.mi_profile) {
            profile();
            return true;
        } else if (id == R.id.mi_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        UserPref.setValue(OrderActivity.this, "mid", "");
        startActivity(new Intent(OrderActivity.this, LoginActivity.class));
        finish();
    }

    private void profile() {
        startActivity(new Intent(OrderActivity.this, ProfileActivity.class));
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    public void onIconClicked(View view) {
        int id = view.getId();
        Intent intent = null;
        switch (id) {
            case R.id.iv_restau:
                intent = new Intent(OrderActivity.this, MyRestauActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.iv_menu:
                intent = new Intent(OrderActivity.this, MenuActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.iv_rating:
                intent = new Intent(OrderActivity.this, ReviewActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.iv_transaction:
                intent = new Intent(OrderActivity.this, TransactionActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
    }
}
