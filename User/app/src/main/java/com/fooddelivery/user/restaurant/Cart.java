package com.fooddelivery.user.restaurant;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fooddelivery.user.R;
import com.fooddelivery.user.helper.SavedData;
import com.fooddelivery.user.helper.UserPref;
import com.fooddelivery.user.pojo.restpojo;
import com.fooddelivery.user.webservice.JSONParse;
import com.fooddelivery.user.webservice.RestAPI;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Cart extends AppCompatActivity{


    Button proceed;
    SharedPreferences sp;
    String uid="",Rid="";
    Dialog cd;
    ArrayList<String> CTid,Cfid,Cfname,Cprice,Cquant,CFprice;
    TextView totitems,totprice;
    ListView list;
    restpojo rp;
    float minorder=0;
    float totorder=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart);
        setActionbar("Cart");
        uid= UserPref.getId(Cart.this);
        Rid=getIntent().getStringExtra("rid");
        totitems= (TextView) findViewById(R.id.totitems);
        totprice= (TextView) findViewById(R.id.totprice);
        list= (ListView) findViewById(R.id.clist);
        proceed= (Button) findViewById(R.id.cproceed);

        rp= SavedData.getRp();
        minorder=Float.parseFloat(rp.getMinorder());

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(totorder>=minorder) {
                    Intent i=new Intent(Cart.this, Payment.class);
                    i.putExtra("tid",CTid);
                    i.putExtra("fid",Cfid);
                    i.putExtra("name",Cfname);
                    i.putExtra("price",Cprice);
                    i.putExtra("quant",Cquant);
                    i.putExtra("fprice",CFprice);
                    i.putExtra("rid",Rid);
                    i.putExtra("tot",totprice.getText().toString());
                    startActivity(i);
                }
                else
                {
                    AlertDialog.Builder ad = new AlertDialog.Builder(Cart.this);
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
        cd=new Dialog(Cart.this,R.style.AppTheme);
        cd.requestWindowFeature(Window.FEATURE_NO_TITLE);
        cd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        cd.setContentView(R.layout.circular_dialog);
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
                AlertDialog.Builder ad = new AlertDialog.Builder(Cart.this);
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
                        list.setAdapter(null);
                        AlertDialog.Builder ad = new AlertDialog.Builder(Cart.this);
                        ad.setTitle("Cart is Empty!");
                        ad.setCancelable(false);
                        ad.setMessage("Your Cart is Empty, Rediecting back to the Menu");
                        ad.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                finish();
                            }
                        });
                        ad.show();
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

                        Adapter adapt = new Adapter(Cart.this, CTid);
                        list.setAdapter(adapt);
                    } else if (ans.compareTo("error") == 0) {
                        String error = json.getString("Data");
                        Toast.makeText(Cart.this, error, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Cart.this, ans, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(Cart.this, "catch - " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
        }
    }

    private class Adapter extends ArrayAdapter<String>
    {
        Context con;
        public Adapter(@NonNull Context context, ArrayList<String> a) {
            super(context, R.layout.menus_item,a);
            con=context;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View v= LayoutInflater.from(con).inflate(R.layout.cart_item,null,true);

            ImageView plus=(ImageView)v.findViewById(R.id.plus);
            ImageView minus=(ImageView)v.findViewById(R.id.minus);
            ImageView info=(ImageView)v.findViewById(R.id.minfo);
            TextView name=(TextView)v.findViewById(R.id.mname);
            TextView price=(TextView)v.findViewById(R.id.mprice);
            final TextView quantity=(TextView)v.findViewById(R.id.mquant);
            Button madd=(Button)v.findViewById(R.id.madd);
            Button mremove=(Button)v.findViewById(R.id.mremove);

            name.setText(Cfname.get(position));
            String rs = getResources().getString(R.string.pricesymbol) + " "+Cprice.get(position);
            price.setText(Html.fromHtml(rs));
            quantity.setText(Cquant.get(position));

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

            madd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    float tot=Float.parseFloat(Cprice.get(position))*Float.parseFloat(quantity.getText().toString());
                    new addcart().execute(Cfid.get(position),Cfname.get(position),Cprice.get(position),quantity.getText().toString(),""+tot,Rid,uid);
                }
            });

            mremove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new addcart().execute(Cfid.get(position),Cfname.get(position),Cprice.get(position),"0","",Rid,uid);

                }
            });

            return  v;
        }
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
                AlertDialog.Builder ad = new AlertDialog.Builder(Cart.this);
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
                    if (ans.compareTo("ok") == 0) {
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

                        Adapter adapt=new Adapter(Cart.this,CTid);
                        list.setAdapter(adapt);
                    }
                    else if(ans.compareTo("no")==0)
                    {
                        list.setAdapter(null);
                        AlertDialog.Builder ad = new AlertDialog.Builder(Cart.this);
                        ad.setTitle("Cart is Empty!");
                        ad.setCancelable(false);
                        ad.setMessage("Your Cart is Empty, Rediecting back to the Menu");
                        ad.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                finish();
                            }
                        });
                        ad.show();
                    }
                    else if (ans.compareTo("quant") == 0) {
                        Snackbar.make(list, "Quantity Should be more then Zero!", Snackbar.LENGTH_SHORT).show();
                    } else if (ans.compareTo("error") == 0) {
                        String error = json.getString("Data");
                        Toast.makeText(Cart.this, error, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Cart.this, ans, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(Cart.this, "catch - " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
