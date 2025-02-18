package com.website.yummywheels;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.website.yummywheels.fragment.DPersonFragment;
import com.website.yummywheels.fragment.RestauFragment;

public class NavAdminActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private static final String VIEW_USERS_TITLE = "Users";
    private static final String RESTAU_TITLE = "Restaurants";
    private static final String ORDERS_TITLE = "Orders";
    private static final String DELIVERY_P_TITLE = "Delivery Person";

    private int fragLoadedId = -1;

    private LinearLayout profileLL;
    private FloatingActionButton fab;
    private CoordinatorLayout coordinatorLayout;
    private DrawerLayout.LayoutParams layoutParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_admin);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // which fragment is loaded accordingly loadnew ActivityDetail
                switch (fragLoadedId) {
                    case 1://load book add
                        startActivity(new Intent(NavAdminActivity.this, AddOrDetailRestauActivity.class));
                        break;
                    case 3://load dperson
                        startActivity(new Intent(NavAdminActivity.this, AddOrDetailDPersonActivity.class));
                        break;
                }
            }
        });

        coordinatorLayout = findViewById(R.id.app_bar_nav_layout);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        profileLL = (LinearLayout) navigationView.getHeaderView(0);
        if (!com.website.yummywheels.utils.LoginSharedPref.getUnameKey(this).equals("")) {
            TextView userName = profileLL.findViewById(R.id.tv_admin_name);
            userName.setText(com.website.yummywheels.utils.LoginSharedPref.getUnameKey(this));
        }
        //profile section in user app only
        profileLL.setOnClickListener(this);
        navigationView.setNavigationItemSelectedListener(this);
        loadFragInit();
    }

    private void loadFragInit() {
        Fragment fragment = new com.website.yummywheels.fragment.OrderedFragment();
        setFragmentWithTitle(ORDERS_TITLE, fragment);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment;
        if (id == R.id.nav_usrs) {
            fragment = new com.website.yummywheels.fragment.ViewUserFragment();
            setFragmentWithTitle(VIEW_USERS_TITLE, fragment);
            //layoutParams.bottomMargin = 0;
        } else if (id == R.id.nav_restau) {
            fragment = new RestauFragment();
            setFragmentWithTitle(RESTAU_TITLE, fragment);
            layoutParams = (DrawerLayout.LayoutParams) coordinatorLayout.getLayoutParams();
            //layoutParams.bottomMargin = 80;
            layoutParams.setMargins(0,0,0,80);
        } else if (id == R.id.nav_order) {
            fragment = new com.website.yummywheels.fragment.OrderedFragment();
            setFragmentWithTitle(ORDERS_TITLE, fragment);
            //layoutParams.bottomMargin = 0;
        } else if (id == R.id.nav_delivery) {
            fragment = new DPersonFragment();
            setFragmentWithTitle(DELIVERY_P_TITLE, fragment);
            //layoutParams.bottomMargin = 0;
        } else if (id == R.id.nav_logout) {
            logout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setFragmentWithTitle(String title, Fragment fragment) {
        getSupportActionBar().setTitle(title);
        if (title.equals(VIEW_USERS_TITLE)) {
            fragLoadedId = 0;
            getSupportActionBar().setSubtitle("");
            if (fab.isOrWillBeShown())
                fab.hide();
        } else if (title.equals(RESTAU_TITLE)) {
            fragLoadedId = 1;
            getSupportActionBar().setSubtitle("");
            if (fab.isOrWillBeHidden())
                fab.show();
        } else if (title.equals(ORDERS_TITLE)) {
            fragLoadedId = 2;
            //disable fab
            getSupportActionBar().setSubtitle("");
            if (fab.isOrWillBeShown())
                fab.hide();
        } else if (title.equals(DELIVERY_P_TITLE)) {
            fragLoadedId = 3;
            getSupportActionBar().setSubtitle("");
            if (fab.isOrWillBeHidden())
                fab.show();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_nav, fragment).commit();
    }

    private void logout() {
        com.website.yummywheels.utils.LoginSharedPref.setUnameKey(NavAdminActivity.this, "");
        startActivity(new Intent(NavAdminActivity.this, LoginAdminActivity.class));
        finish();
    }

    @Override
    public void onClick(View v) {

    }
}
