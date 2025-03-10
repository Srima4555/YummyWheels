package com.fooddelivery.user.restaurant;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.fooddelivery.user.R;
import com.fooddelivery.user.helper.SavedData;
import com.fooddelivery.user.helper.UserPref;
import com.fooddelivery.user.pojo.menupojo;
import com.fooddelivery.user.pojo.restpojo;
import com.fooddelivery.user.webservice.JSONParse;
import com.fooddelivery.user.webservice.RestAPI;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Menus extends AppCompatActivity{

    Button proceed;
    TextView totitems,totprice;
    ArrayList<String> CTid,Cfid,Cfname,Cprice,Cquant,CFprice;
    String uid="",Rid="";
    Dialog cd;
    TableRow items,noitems;
    Spinner spin;
    ListView list;
    Adapter adapt;
    ArrayList<menupojo> mdata,FilterData;
    int pos;
    restpojo rp;
    float minorder=0;
    float totorder=0;

    boolean isveg=false;
    SwitchCompat vegswitch;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menus);
        setActionbar("Menu");
        uid= UserPref.getId(Menus.this);

        Rid=getIntent().getStringExtra("rid");
        items= (TableRow) findViewById(R.id.items);
        noitems= (TableRow) findViewById(R.id.noitems);
        spin= (Spinner) findViewById(R.id.spinner);
        list= (ListView) findViewById(R.id.mlist);
        totitems= (TextView) findViewById(R.id.totitems);
        totprice= (TextView) findViewById(R.id.totprice);
        proceed= (Button) findViewById(R.id.mproceed);

        rp= SavedData.getRp();
        minorder=Float.parseFloat(rp.getMinorder());
//        String minOrderStr = rp.getMinorder();
//
//        if (minOrderStr != null && !minOrderStr.isEmpty()) {
//            minorder = Float.parseFloat(minOrderStr);
//        } else {
//            minorder = 0; // Assign a default value
//        }

        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SortItems();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(totorder>=minorder) {
                    Intent i = new Intent(Menus.this, Cart.class);
                    i.putExtra("rid", Rid);
                    startActivity(i);
                }
                else
                {
                    AlertDialog.Builder ad = new AlertDialog.Builder(Menus.this);
                    ad.setTitle("Minimum Order : "+getResources().getString(R.string.pricesymbol)+" "+minorder);
                    ad.setMessage("As per the Restaurant, you can order Food if you have a Mimum order of "+getResources().getString(R.string.pricesymbol)+" "+minorder);
                    ad.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    ad.show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        new getcart().execute(uid,Rid);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem item = menu.findItem(R.id.onlyveg);
        MenuItemCompat.setActionView(item, R.layout.menu_isveg);
        RelativeLayout rootView = (RelativeLayout) MenuItemCompat.getActionView(item);
        vegswitch=rootView.findViewById(R.id.vegswitch);
        vegswitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isveg)
                {
                    isveg=false;
                    vegswitch.setChecked(false);
                    SortItems();
                }
                else
                {
                    isveg=true;
                    vegswitch.setChecked(true);
                    SortItems();
                }
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)
        {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void setActionbar(String title)
    {
        String Title="<font color='#D70C16'>"+title+"</font>";
        getSupportActionBar().setTitle(Html.fromHtml(Title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
    }

    public void dailog()
    {
        cd=new Dialog(Menus.this,R.style.AppTheme);
        cd.requestWindowFeature(Window.FEATURE_NO_TITLE);
        cd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        cd.setContentView(R.layout.loading);
        cd.setCancelable(false);
        cd.show();
    }

    private class getcart extends AsyncTask<String,JSONObject,String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dailog();
        }

        @Override
        protected String doInBackground(String... params) {
            String a="back";
            RestAPI api=new RestAPI();
            try {
                JSONObject json=api.UgetCart(params[0],params[1]);
                JSONParse jp=new JSONParse();
                a=jp.parse(json);
            } catch (Exception e) {
                a=e.getMessage();
            }
            return a;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            cd.cancel();

            if (s.contains("Unable to resolve host")) {
                AlertDialog.Builder ad = new AlertDialog.Builder(Menus.this);
                ad.setTitle("Unable to Connect!");
                ad.setMessage("Check your Internet Connection,Unable to connect the Server");
                ad.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                ad.show();
            } else {

                try
                {
                    JSONObject json=new JSONObject(s);
                    String ans = json.getString("status");
                    if(ans.compareTo("no")==0)
                    {
                        items.setVisibility(View.GONE);
                        noitems.setVisibility(View.VISIBLE);

                        new getMenu().execute(Rid);
                    }
                    else if(ans.compareTo("ok")==0)
                    {
                        JSONArray jarry = json.getJSONArray("Data");

                        CTid=new ArrayList<String>();
                        Cfid=new ArrayList<String>();
                        Cfname=new ArrayList<String>();
                        Cprice=new ArrayList<String>();
                        Cquant=new ArrayList<String>();
                        CFprice=new ArrayList<String>();
                        totorder=0;
                        for(int i=0;i<jarry.length();i++)
                        {
                            JSONObject jobj = jarry.getJSONObject(i);
                            String tid=jobj.getString("data0");
                            String fid=jobj.getString("data1");
                            String fname=jobj.getString("data2");
                            String price=jobj.getString("data3");
                            String qaunt=jobj.getString("data4");
                            String fprice=jobj.getString("data5");

                            CTid.add(tid);
                            Cfid.add(fid);
                            Cfname.add(fname);
                            Cprice.add(price);
                            Cquant.add(qaunt);
                            CFprice.add(fprice);
                            totorder+=Float.parseFloat(fprice);
                        }

                        calcItems();

                        new getMenu().execute(Rid);
                    }
                    else if(ans.compareTo("error")==0)
                    {
                        String error = json.getString("Data");
                        Toast.makeText(Menus.this, error, Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(Menus.this,ans, Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e)
                {
                    Toast.makeText(Menus.this, "catch - "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    private class getMenu extends AsyncTask<String,JSONObject,String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dailog();
        }

        @Override
        protected String doInBackground(String... params) {
            String a="back";
            RestAPI api=new RestAPI();
            try {
                JSONObject json=api.getMenu(params[0],"false",uid);
                JSONParse jp=new JSONParse();
                a=jp.parse(json);
            } catch (Exception e) {
                a=e.getMessage();
            }
            return a;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            cd.cancel();

            if (s.contains("Unable to resolve host")) {
                AlertDialog.Builder ad = new AlertDialog.Builder(Menus.this);
                ad.setTitle("Unable to Connect!");
                ad.setMessage("Check your Internet Connection,Unable to connect the Server");
                ad.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                ad.show();
            }
            else {

                try
                {
                    JSONObject json=new JSONObject(s);
                    String ans = json.getString("status");
                    if(ans.compareTo("no")==0)
                    {
                        AlertDialog.Builder ad = new AlertDialog.Builder(Menus.this);
                        ad.setTitle("Menu is Empty");
                        ad.setMessage("Sorry! There are no items in the Menu");
                        ad.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                finish();
                            }
                        });
                        ad.show();
                    }
                    else if(ans.compareTo("ok")==0)
                    {
                        JSONArray jarry = json.getJSONArray("Data");
                        mdata=new ArrayList<menupojo>();

                        for(int i=0;i<jarry.length();i++)
                        {
                            JSONObject jobj = jarry.getJSONObject(i);
                            String fid=jobj.getString("data0");
                            String name=jobj.getString("data1");
                            String desc=jobj.getString("data2");
                            String price=jobj.getString("data3");
                            String category=jobj.getString("data4");
                            String image=jobj.getString("data5");
                            String rid=jobj.getString("data6");
                            String status=jobj.getString("data7");
                            String isveg=jobj.getString("data8");
                            String isfav=jobj.getString("data9");

                            menupojo mp=new menupojo(fid,name,desc,price,category,image,rid,status,isveg,isfav);
                            mdata.add(mp);
                        }

                        ArrayList<String> list=new ArrayList<String>();
                        list.add("Snacks");
                        list.add("Appetizers");
                        list.add("Main Course");
                        list.add("Breads");
                        list.add("Sides");
                        list.add("Desserts");
                        list.add("Beverages");

                        ArrayAdapter<String> adapt=new ArrayAdapter<String>(Menus.this,R.layout.spinitem,R.id.spinid,list);
                        spin.setAdapter(adapt);
                    }
                    else if(ans.compareTo("error")==0)
                    {
                        String error = json.getString("Data");
                        Toast.makeText(Menus.this, error, Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(Menus.this,ans, Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e)
                {
                    Toast.makeText(Menus.this, "catch - "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void calcItems()
    {
        float ans=0;
        int count=0;
        for(int i=0;i<CTid.size();i++)
        {
            int quant=Integer.parseInt(Cquant.get(i));
            float price=Float.parseFloat(Cprice.get(i));
            float fprice=price*quant;
            ans+=fprice;
            count+=quant;
        }

        if(ans>0)
        {
            totitems.setText("Items : "+count);
            String rs = getResources().getString(R.string.pricesymbol) + " "+ans;
            totprice.setText(Html.fromHtml(rs));

            items.setVisibility(View.VISIBLE);
            noitems.setVisibility(View.GONE);
        }
        else
        {
            items.setVisibility(View.GONE);
            noitems.setVisibility(View.VISIBLE);
        }
    }

    public void SortItems()
    {
        FilterData =new ArrayList<menupojo>();
        for(int i=0;i<mdata.size();i++)
        {
            menupojo mp=mdata.get(i);
            if(spin.getSelectedItem().toString().compareTo(mp.getCategory())==0)
            {
                if(isveg)
                {
                    if(mp.getIsveg().compareTo("Yes")==0)
                    {
                        FilterData.add(mp);
                    }
                }
                else
                {
                    FilterData.add(mp);
                }

            }
        }

        if(FilterData.size()>0) {
            adapt = new Adapter(Menus.this, FilterData);
            list.setAdapter(adapt);
        }
        else
        {
            list.setAdapter(null);
            Snackbar.make(list,"There are no Items in "+spin.getSelectedItem().toString(),Snackbar.LENGTH_SHORT).show();
        }

    }

    private class Adapter extends ArrayAdapter<menupojo>
    {
        Context con;
        public Adapter(@NonNull Context context, ArrayList<menupojo> a) {
            super(context, R.layout.menus_item,a);
            con=context;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View v= LayoutInflater.from(con).inflate(R.layout.menus_item,null,true);

            ImageView img=(ImageView)v.findViewById(R.id.mimg);
            ImageView plus=(ImageView)v.findViewById(R.id.plus);
            ImageView minus=(ImageView)v.findViewById(R.id.minus);
            ImageView info=(ImageView)v.findViewById(R.id.minfo);
            ImageView fav=(ImageView)v.findViewById(R.id.mfav);
            TextView name=(TextView)v.findViewById(R.id.mname);
            TextView price=(TextView)v.findViewById(R.id.mprice);
            final TextView quantity=(TextView)v.findViewById(R.id.mquant);
            Button madd=(Button)v.findViewById(R.id.madd);

            final menupojo mp=FilterData.get(position);

            name.setText(mp.getName());
            String rs = getResources().getString(R.string.pricesymbol) + " "+mp.getPrice();
            price.setText(Html.fromHtml(rs));
            quantity.setText(setQuantityforItems(mp.getFid()));

            if(mp.getIsfav().compareTo("no")!=0)
            {
                fav.setImageResource(R.drawable.favs);
            }
            else
            {
                fav.setImageResource(R.drawable.nfavs);
            }

            plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int i=Integer.parseInt(quantity.getText().toString());
                    if(i<=9)
                    {
                        i++;
                        quantity.setText(i+"");
                    }
                }
            });

            minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int i=Integer.parseInt(quantity.getText().toString());
                    if(i>=1)
                    {
                        i--;
                        quantity.setText(i+"");
                    }
                }
            });

            info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MenuDialog(position);
                }
            });

            Log.d("IMAGE",mp.getImage());

            try{
                byte[] imageAsBytes = Base64.decode(mp.getImage().getBytes(), Base64.DEFAULT);
                Bitmap bt= BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
                bt=Bitmap.createScaledBitmap(bt, (int)70, (int)70, false);
                img.setImageBitmap(bt);
            }catch (Exception e){
                img.setImageResource(R.drawable.uicon);
            }

            madd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        float tot=Float.parseFloat(mp.getPrice())*Float.parseFloat(quantity.getText().toString());
                        new addcart().execute(mp.getFid(),mp.getName(),mp.getPrice(),quantity.getText().toString(),""+tot,Rid,uid);
                    } catch (NumberFormatException e) {
                        Toast.makeText(con, "error"+e, Toast.LENGTH_SHORT).show();
                        throw new RuntimeException(e);
                    } catch (Error error){
                        Toast.makeText(con, "error"+error, Toast.LENGTH_SHORT).show();
                    }
                }
            });

            fav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pos=position;
                    if(mp.getIsfav().compareTo("no")==0)
                    {
                        new addFav().execute(mp.getFid(),Rid,uid);
                    }
                    else
                    {
                        new RemoveFav().execute(mp.getFid(),Rid,uid);
                    }
                }
            });

            return  v;
        }
    }

    public String setQuantityforItems(String id)
    {
        String ans="0";
        if(CTid!=null) {
            for (int i = 0; i < CTid.size(); i++) {

                if (Cfid.get(i).compareTo(id) == 0) {
                    ans = Cquant.get(i);
                }
            }
        }
        return ans;
    }

    private class addcart extends AsyncTask<String,JSONObject,String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dailog();
        }

        @Override
        protected String doInBackground(String... params) {
            String a="back";
            RestAPI api=new RestAPI();
            try {
                JSONObject json=api.UAddtoCart(params[0],params[1],params[2],params[3],params[4],params[5],params[6]);
                JSONParse jp=new JSONParse();
                a=jp.parse(json);
            } catch (Exception e) {
                a=e.getMessage();
            }
            return a;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            cd.cancel();

            if (s.contains("Unable to resolve host")) {
                AlertDialog.Builder ad = new AlertDialog.Builder(Menus.this);
                ad.setTitle("Unable to Connect!");
                ad.setMessage("Check your Internet Connection,Unable to connect the Server");
                ad.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                ad.show();
            } else {

                try {
                    JSONObject json = new JSONObject(s);
                    String ans = json.getString("status");
                    if (ans.compareTo("no") == 0) {
                        items.setVisibility(View.GONE);
                        noitems.setVisibility(View.VISIBLE);
                    } else if (ans.compareTo("ok") == 0) {
                        JSONArray jarry = json.getJSONArray("Data");

                        CTid = new ArrayList<String>();
                        Cfid = new ArrayList<String>();
                        Cfname = new ArrayList<String>();
                        Cprice = new ArrayList<String>();
                        Cquant = new ArrayList<String>();
                        CFprice = new ArrayList<String>();
                        totorder=0;
                        for (int i = 0; i < jarry.length(); i++) {
                            JSONObject jobj = jarry.getJSONObject(i);
                            String tid = jobj.getString("data0");
                            String fid = jobj.getString("data1");
                            String fname = jobj.getString("data2");
                            String price = jobj.getString("data3");
                            String qaunt = jobj.getString("data4");
                            String fprice = jobj.getString("data5");

                            CTid.add(tid);
                            Cfid.add(fid);
                            Cfname.add(fname);
                            Cprice.add(price);
                            Cquant.add(qaunt);
                            CFprice.add(fprice);
                            totorder+=Float.parseFloat(fprice);
                        }

                        calcItems();
                    } else if (ans.compareTo("quant") == 0) {
                        Snackbar.make(list, "Quantity Should be more then Zero!", Snackbar.LENGTH_SHORT).show();
                    } else if (ans.compareTo("error") == 0) {
                        String error = json.getString("Data");
                        Toast.makeText(Menus.this, error, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Menus.this, ans, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(Menus.this, "catch - " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void MenuDialog(int pos)
    {
        Dialog d=new Dialog(Menus.this);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.menu_dialog);

        ImageView img=(ImageView)d.findViewById(R.id.mimg);
        TextView name=(TextView)d.findViewById(R.id.mname);
        TextView type=(TextView)d.findViewById(R.id.mtype);
        TextView price=(TextView)d.findViewById(R.id.mprice);
        TextView desc=(TextView)d.findViewById(R.id.mdesc);

        menupojo mp=FilterData.get(pos);

        try{
            byte[] imageAsBytes = Base64.decode(mp.getImage().getBytes(), Base64.DEFAULT);
            Bitmap bt= BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
            img.setImageBitmap(bt);
        }catch (Exception e){
            img.setImageResource(R.drawable.uicon);
        }

        name.setText(mp.getName());
        desc.setText(mp.getDesc());
        price.setText("Price : "+mp.getPrice());
        type.setText("Type : "+mp.getCategory());

        d.show();
    }

    private class addFav extends AsyncTask<String,JSONObject,String>
    {
        String fid="";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dailog();
        }

        @Override
        protected String doInBackground(String... params) {
            fid=params[0];
            String a="back";
            RestAPI api=new RestAPI();
            try {
                JSONObject json=api.UrAddFav(params[0],params[1],params[2]);
                JSONParse jp=new JSONParse();
                a=jp.parse(json);
            } catch (Exception e) {
                a=e.getMessage();
            }
            return a;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            cd.cancel();

            if (s.contains("Unable to resolve host")) {
                AlertDialog.Builder ad = new AlertDialog.Builder(Menus.this);
                ad.setTitle("Unable to Connect!");
                ad.setMessage("Check your Internet Connection,Unable to connect the Server");
                ad.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                ad.show();
            } else {

                try {
                    JSONObject json = new JSONObject(s);
                    String ans = json.getString("status");
                    if (ans.compareTo("true") == 0) {

                        UpdateFav("yes");
                        Toast.makeText(Menus.this, "Added to Favorites", Toast.LENGTH_SHORT).show();

                    }  else if (ans.compareTo("error") == 0) {
                        String error = json.getString("Data");
                        Toast.makeText(Menus.this, error, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Menus.this, ans, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(Menus.this, "catch - " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private class RemoveFav extends AsyncTask<String,JSONObject,String>
    {
        String fid="";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dailog();
        }

        @Override
        protected String doInBackground(String... params) {
            fid=params[0];
            String a="back";
            RestAPI api=new RestAPI();
            try {
                JSONObject json=api.UremoveFav(params[0],params[1],params[2]);
                JSONParse jp=new JSONParse();
                a=jp.parse(json);
            } catch (Exception e) {
                a=e.getMessage();
            }
            return a;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            cd.cancel();

            if (s.contains("Unable to resolve host")) {
                AlertDialog.Builder ad = new AlertDialog.Builder(Menus.this);
                ad.setTitle("Unable to Connect!");
                ad.setMessage("Check your Internet Connection,Unable to connect the Server");
                ad.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                ad.show();
            } else {

                try {
                    JSONObject json = new JSONObject(s);
                    String ans = json.getString("status");
                    if (ans.compareTo("true") == 0) {

                        UpdateFav("no");
                        Toast.makeText(Menus.this, "Removed from Favorites", Toast.LENGTH_SHORT).show();

                    }  else if (ans.compareTo("error") == 0) {
                        String error = json.getString("Data");
                        Toast.makeText(Menus.this, error, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Menus.this, ans, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(Menus.this, "catch - " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void UpdateFav(String value)
    {
        menupojo menupojo=FilterData.get(pos);
        menupojo.setIsfav(value);

        FilterData.set(pos,menupojo);
        adapt.notifyDataSetChanged();
    }

}
