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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.*;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fooddelivery.user.R;
import com.fooddelivery.user.helper.UserPref;
import com.fooddelivery.user.pojo.restpojo;
import com.fooddelivery.user.webservice.JSONParse;
import com.fooddelivery.user.webservice.RestAPI;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Reviews extends AppCompatActivity{

    String Rname;
    boolean ans=false;
    ArrayList<String> ratingl,reviewl,datetimel,unamel;
    ListView list;
    TableLayout tbreview;
    RatingBar ratingbar;
    EditText review;
    Button submit;
    String uid="",Rid="";
    Dialog cd;
    ImageView google,yelp;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reviews);
        setActionbar("Reviews");
        list= (ListView) findViewById(R.id.Rlist);
        tbreview= (TableLayout) findViewById(R.id.tbreview);
        ratingbar= (RatingBar) findViewById(R.id.rratingbar);
        review= (EditText) findViewById(R.id.rreview);
        submit= (Button) findViewById(R.id.rsubmit);
        google= (ImageView) findViewById(R.id.google);
        yelp= (ImageView) findViewById(R.id.yelp);

        uid= UserPref.getId(Reviews.this);
        Rid=getIntent().getStringExtra("rid");
        Rname=getIntent().getStringExtra("rname");


        ratingbar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                if(v>=0.1 && v<=1.0)
                {
                    review.setText("Hated It, Do not visit this restaurant.");
                    review.setSelection(review.length());
                }
                else if(v>1.0 && v<=2.0)
                {
                    review.setText("Disliked It, Should ignore visiting this restaurant.");
                    review.setSelection(review.length());
                }
                else if(v>2.0 && v<=3.0)
                {
                    review.setText("Ok Restaurant");
                    review.setSelection(review.length());
                }
                else if(v>3.0 && v<=4.0)
                {
                    review.setText("Excellent Restaurant, Good Quality food.");
                    review.setSelection(review.length());
                }
                else if(v>4.0 && v<=5.0)
                {
                    review.setText("Loved it, One should definitely visit this restaurant.");
                    review.setSelection(review.length());
                }
                else {
                    review.setText("");
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ratingbar.getRating()>0.0)
                {
                    if(review.length()>0)
                    {
                        SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd HH:mm");
                        String dt=sdf.format(new Date().getTime());
                        new addReview().execute(uid,Rid,review.getText().toString(),ratingbar.getRating()+"",dt);
                    }
                    else
                    {
                        Snackbar.make(view,"Select your Review",Snackbar.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Snackbar.make(view,"Select your Rating",Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.co.in/search?q= " + Rname + " Restaurant Google Reviews"));
                startActivity(browserIntent);
            }
        });

        yelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.co.in/search?q= " + Rname + " Restaurant Yelp Reviews"));
                startActivity(browserIntent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        new getReview().execute(Rid);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
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

    private class Adapter extends ArrayAdapter<String>
    {
        Context con;
        public Adapter(@NonNull Context context,ArrayList<String> a) {
            super(context, R.layout.review_item,a);
            con=context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View v= LayoutInflater.from(con).inflate(R.layout.review_item,null,true);

            RatingBar rate=(RatingBar)v.findViewById(R.id.rratingbar);
            TextView ratetext=(TextView)v.findViewById(R.id.rratingtext);
            TextView date=(TextView)v.findViewById(R.id.rdate);
            TextView review=(TextView)v.findViewById(R.id.rreviewtext);
            TextView name=(TextView)v.findViewById(R.id.rname);

            name.setText(unamel.get(position));
            review.setText(reviewl.get(position));
            rate.setRating(Float.parseFloat(ratingl.get(position)));
            ratetext.setText(ratingl.get(position));
            date.setText(datetimel.get(position));

            return  v;
        }
    }

    public void dailog()
    {
        cd=new Dialog(Reviews.this,R.style.AppTheme);
        cd.requestWindowFeature(Window.FEATURE_NO_TITLE);
        cd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        cd.setContentView(R.layout.loading);
        cd.setCancelable(false);
        cd.show();
    }

    private class addReview extends AsyncTask<String,JSONObject,String>
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
                JSONObject json=api.UAddReviews(params[0],params[1],params[2],params[3],params[4]);
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
                AlertDialog.Builder ad = new AlertDialog.Builder(Reviews.this);
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
            else
            {
                try {
                    JSONObject json = new JSONObject(s);
                    String ans = json.getString("status");

                    if(ans.compareTo("true")==0)
                    {
                        ratingbar.setRating(0);
                        new getReview().execute(Rid);
                    }
                    else if (ans.compareTo("error") == 0) {
                        String error = json.getString("Data");
                        Toast.makeText(Reviews.this, error, Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(Reviews.this, ans, Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e) {
                    Toast.makeText(Reviews.this, "catch - " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    private class getReview extends AsyncTask<String,JSONObject,String>
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
                JSONObject json=api.getReviews(params[0]);
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
                AlertDialog.Builder ad = new AlertDialog.Builder(Reviews.this);
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

                        AlertDialog.Builder ad=new AlertDialog.Builder(Reviews.this);
                        ad.setTitle("No Reviews");
                        ad.setMessage("Be the first one to review !");
                        ad.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                        ad.show();

                    } else if (ans.compareTo("ok") == 0) {

                        JSONArray jarry = json.getJSONArray("Data");
                        reviewl=new ArrayList<String>();
                        datetimel=new ArrayList<String>();
                        unamel=new ArrayList<String>();
                        ratingl=new ArrayList<String>();

                        for(int i=0;i<jarry.length();i++)
                        {
                            JSONObject jobj = jarry.getJSONObject(i);
                            String review=jobj.getString("data2");
                            String rating=jobj.getString("data3");
                            String dt=jobj.getString("data4");
                            String uname=jobj.getString("data5");

                            reviewl.add(review);
                            ratingl.add(rating);
                            datetimel.add(dt);
                            unamel.add(uname);

                        }

                        Adapter adt=new Adapter(Reviews.this,ratingl);
                        list.setAdapter(adt);


                    } else if (ans.compareTo("error") == 0) {
                        String error = json.getString("Data");
                        Toast.makeText(Reviews.this, error, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(Reviews.this, "catch - " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}
