package com.fooddelivery.user.Activity;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fooddelivery.user.fragments.Transactions;
import com.fooddelivery.user.orders.Bookings;
import com.fooddelivery.user.R;
import com.fooddelivery.user.fragments.Cuisine;
import com.fooddelivery.user.fragments.Favourites;
import com.fooddelivery.user.fragments.Home;
import com.fooddelivery.user.fragments.Profile;
import com.fooddelivery.user.helper.UserPref;

import java.util.ArrayList;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity{

    String lat="0.0",lng="0.0";
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    Button frameimg;
    Boolean menu=false;
    RelativeLayout relative;
    LinearLayout linear;
    ImageView home;
    int width=0,height=0;
    TextView head;

    Adapter adapt;
    int Position=0;
    ListView list;
    String[] title=new String[]{"Home","Cuisine","Orders","Favorites","Transaction","Profile","Logout"};
    int[] Nimages=new int[]{R.drawable.lhome,R.drawable.lcuisine,R.drawable.llist,R.drawable.fav,R.drawable.wtrans,R.drawable.lprofile,R.drawable.logout};
    final int REQUEST_CHECK_SETTINGS = 100;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sp=getSharedPreferences("rfu", Context.MODE_PRIVATE);
        getSupportActionBar().hide();
        relative= (RelativeLayout) findViewById(R.id.Relative);
        linear= (LinearLayout) findViewById(R.id.Linear);
        home= (ImageView) findViewById(R.id.home);
        head= (TextView) findViewById(R.id.heading);
        frameimg= (Button) findViewById(R.id.frameimg);
        head.setText(title[0]);

        DisplayMetrics dimension = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dimension);
        width = dimension.widthPixels;
        height = dimension.heightPixels;

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!menu)
                {
                    collapse();
                }
                else
                {
                    expand();
                }
            }
        });

        frameimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                home.performClick();
            }
        });

        list= (ListView) findViewById(R.id.list);

        ArrayList<String> a= new ArrayList<>(Arrays.asList(title));
        adapt=new Adapter(MainActivity.this,a);
        list.setAdapter(adapt);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                callFragment(i);
            }
        });

        Position=getIntent().getIntExtra("pos",0);

        if(Position==2)
        {
            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            nm.cancel("FDU",getIntent().getIntExtra("nid",0));
        }

        displayview(Position);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(message, new IntentFilter("rfu"));
    }

    private BroadcastReceiver message = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            int pos=intent.getIntExtra("pos",0);
            callFragment(pos);
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(message);
        }
        catch (Exception e){}
    }

    public void callFragment(int pos)
    {
        Position = pos;
        head.setText(title[Position]);
        adapt.notifyDataSetChanged();
        expand();
        displayview(Position);
    }

    public void displayview(int pos)
    {
        if(pos==0)
        {
            Fragment frag=new Home();
            FragmentManager fm=getSupportFragmentManager();
            fm.beginTransaction().replace(R.id.frame,frag).commit();
        }
        else if(pos==1)
        {
            Fragment frag=new Cuisine();
            FragmentManager fm=getSupportFragmentManager();
            fm.beginTransaction().replace(R.id.frame,frag).commit();
        }
        else if(pos==2)
        {
            Fragment frag=new Bookings();
            FragmentManager fm=getSupportFragmentManager();
            fm.beginTransaction().replace(R.id.frame,frag).commit();
        }
        else if(pos==3)
        {
            Fragment frag=new Favourites();
            FragmentManager fm=getSupportFragmentManager();
            fm.beginTransaction().replace(R.id.frame,frag).commit();
        }
        else if(pos==4)
        {
            Fragment frag=new Transactions();
            FragmentManager fm=getSupportFragmentManager();
            fm.beginTransaction().replace(R.id.frame,frag).commit();
        }
        else if(pos==5)
        {
            Fragment frag=new Profile();
            FragmentManager fm=getSupportFragmentManager();
            fm.beginTransaction().replace(R.id.frame,frag).commit();
        }
        else if(pos==6)
        {
            UserPref.setValue(MainActivity.this,"uid","");
            UserPref.setValue(MainActivity.this,"name","");
            Intent i=new Intent(MainActivity.this, Login.class);
            startActivity(i);
            finish();
        }
    }

    public void expand()
    {
        list.setVisibility(View.INVISIBLE);
        frameimg.setVisibility(View.GONE);
        RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        linear.setLayoutParams(params);
        menu=false;
    }

    public void collapse()
    {
        list.setVisibility(View.VISIBLE);
        frameimg.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,height/2);
        params.setMargins(width-100,height/4,0,0);
        linear.setLayoutParams(params);
        menu=true;
    }

    private class Adapter extends ArrayAdapter<String> {

        Context con;
        public Adapter(@NonNull Context context, ArrayList<String> a) {
            super(context, R.layout.list_item,a);
            con=context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View v= LayoutInflater.from(con).inflate(R.layout.list_item,null,true);

            ImageView circle= (ImageView) v.findViewById(R.id.circle);
            ImageView arrow= (ImageView) v.findViewById(R.id.arrow);
            ImageView img= (ImageView) v.findViewById(R.id.img);
            TextView name= (TextView) v.findViewById(R.id.name);
            name.setText(title[position]);
            img.setImageResource(Nimages[position]);

            if(position==Position)
            {
                circle.setImageResource(R.drawable.circles);
                arrow.setImageResource(R.drawable.arrows);
            }
            else
            {
                circle.setImageResource(R.drawable.circle);
                arrow.setImageResource(R.drawable.arrow);
            }

            return  v;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CHECK_SETTINGS)
        {
            Intent intent = new Intent("gps");

            switch (resultCode) {
                case RESULT_OK:

                    intent.putExtra("ans","yes");
                    LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(intent);

                    break;
                case RESULT_CANCELED:

                    intent.putExtra("ans","no");
                    LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(intent);

                    break;
            }
        }
    }

}
