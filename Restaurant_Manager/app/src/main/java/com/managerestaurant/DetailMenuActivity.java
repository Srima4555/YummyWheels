package com.managerestaurant;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Arrays;

public class DetailMenuActivity extends AppCompatActivity implements View.OnClickListener {

    private MenuPojo pojo;
    private boolean isUpdate;
    private RelativeLayout rLayout;
    private ImageView imageView;
    private EditText descET, nameET, priceET, vegNonvegET;
    private Spinner spnCateg;
    private Button btnUpdateSubmit;
    private Dialog dialogLoader;
    private Switch switchRestau;
    private static final String YES = "Yes";
    private String checked = YES;
    private static final int GALLERY_PERMISSIONS_REQUEST = 0;
    private static final int GALLERY_IMAGE_REQUEST = 1;
    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int CAMERA_IMAGE_REQUEST = 3;
    public static final String FILE_NAME = "temp.jpg";
    private static final int MAX_DIMENSION = 200;
    private String encode;
    private TextView isAvailTV;
    private boolean firstCall;
    private static final CharSequence CANNOTBEEMPTY = "This field cannot be empty";
    private String isVeg;
    private ArrayList<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_menu);
        Toolbar toolbar = findViewById(R.id.toolbar_add_food);
        toolbar.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_material);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Menu");
        initUI();
        if ((pojo = (MenuPojo) getIntent().getSerializableExtra(UtilConstants.MENU_POJO)) != null) {
            isUpdate = true;
            getSupportActionBar().setTitle("Update food item");
            btnUpdateSubmit.setText("Update");
            //settext
            encode = pojo.getImage();
            imageView.setImageBitmap(UtilConstants.decodeBitmap(encode));
            nameET.setText(pojo.getName());
            descET.setText(pojo.getDesc());
            spnCateg.setSelection(list.indexOf(pojo.getCategory()));
            Log.i("TAG", "onCreate: "+pojo.getType());
            if (pojo.getType().equalsIgnoreCase("true") ||
                    pojo.getType().equalsIgnoreCase("yes")) {
                vegNonvegET.setText("Veg");
            } else vegNonvegET.setText("NonVeg");
            priceET.setText(pojo.getPrice());
        } else {
            getSupportActionBar().setTitle("Add food item");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initUI() {
        rLayout = findViewById(R.id.rl_food);
        isAvailTV = findViewById(R.id.tv_avail);
        imageView = findViewById(R.id.iv_menu);
        nameET = findViewById(R.id.et_bname);
        descET = findViewById(R.id.et_fdesc);
        spnCateg = findViewById(R.id.til_cate);
        list = new ArrayList<>();
        list.add("Select category");
        list.add("Snacks");
        list.add("Appetizers");
        list.add("Main Course");
        list.add("Breads");
        list.add("Sides");
        list.add("Desserts");
        list.add("Beverages");
        spnCateg.setAdapter(new SpnAdap(this, list));
        spnCateg.setVisibility(View.VISIBLE);
        vegNonvegET = findViewById(R.id.et_status_menu);
        priceET = findViewById(R.id.et_fprice);
        btnUpdateSubmit = findViewById(R.id.btn_update_orsubmit);
        btnUpdateSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_update_orsubmit) {
            if (nameET.getText().toString().isEmpty()) {
                nameET.setError(CANNOTBEEMPTY);
            } else if (descET.getText().toString().isEmpty()) {
                descET.setError(CANNOTBEEMPTY);
            } else if (spnCateg.getSelectedItemPosition() == 0) {
                Snackbar.make(rLayout, "Please select the category", Snackbar.LENGTH_LONG).show();
            } else if (vegNonvegET.getText().toString().isEmpty()) {
                vegNonvegET.setError(CANNOTBEEMPTY);
            } else if (!vegNonvegET.getText().toString().contains("eg")) {
                vegNonvegET.setError("Enter veg/nonveg type of food item");
            } else if (priceET.getText().toString().isEmpty()) {
                priceET.setError(CANNOTBEEMPTY);
            } else if (encode == null) {
                Snackbar.make(rLayout, "Please select an image for this food item", Snackbar.LENGTH_LONG).show();
            } else {
                if (vegNonvegET.getText().toString().startsWith("veg") || vegNonvegET.getText().toString().equalsIgnoreCase("veg")
                        || vegNonvegET.getText().toString().equalsIgnoreCase("vegetarian") || vegNonvegET.getText().toString().startsWith("Veg") ) {
                    isVeg = "Yes";
                } else {
                    isVeg = "No";
                }
                if (isUpdate) {
                    UpdateTask task = new UpdateTask();
                    task.execute(pojo.getFid(), nameET.getText().toString(), descET.getText().toString(),
                            priceET.getText().toString(),
                            list.get(spnCateg.getSelectedItemPosition()),
                            encode, UserPref.getRid(this), isVeg, checked);
                    //string fid,string fname, string desc, string price, string cat, string image, string rid, string isveg,string status)
                } else {
                    SubmitTask task = new SubmitTask();
                    task.execute(nameET.getText().toString(), descET.getText().toString(),
                            priceET.getText().toString(),
                            list.get(spnCateg.getSelectedItemPosition()),
                            encode, UserPref.getRid(this), isVeg, checked);
                }
            }
        }
    }

    private void stopAnimation() {
        if (dialogLoader.isShowing())
            dialogLoader.cancel();
    }

    private void startAnimation() {
        dialogLoader = new Dialog(this, R.style.AppTheme_NoActionBar);
        dialogLoader.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#8D000000")));
        final View view = getLayoutInflater().inflate(R.layout.loading, null);
        LottieAnimationView animationView = view.findViewById(R.id.lottie);
        animationView.playAnimation();
        dialogLoader.setContentView(view);
        dialogLoader.setCancelable(false);
        dialogLoader.show();
    }

    private class UpdateTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startAnimation();
        }

        @Override
        protected String doInBackground(String... strings) {
            String result;
            RestAPI restAPI = new RestAPI();
            try {
                JSONObject jsonObject = restAPI.MUpdateMenu(strings[0], strings[1], strings[2], strings[3], strings[4], strings[5], strings[6], strings[7]
                        , strings[8]);
                JSONParse jsonParser = new JSONParse();
                result = jsonParser.parse(jsonObject);
            } catch (Exception e) {
                result = e.getMessage();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            stopAnimation();
            Log.d("reply getRestau ", s);
            if (s.contains("Unable to resolve host")) {
                AlertDialog.Builder ad = new AlertDialog.Builder(DetailMenuActivity.this);
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
                    Log.d("reply::", ans);
                    if (ans.compareTo("already") == 0) {
                        Snackbar.make(rLayout, "This Menu Item already exists", Snackbar.LENGTH_LONG).show();
                    } else if (ans.compareTo("true") == 0) {
                        finish();
                    } else if (ans.compareTo("error") == 0) {
                        String error = json.getString("Data");
                        Toast.makeText(DetailMenuActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(DetailMenuActivity.this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private class SubmitTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startAnimation();
        }

        @Override
        protected String doInBackground(String... strings) {
            String result;
            RestAPI restAPI = new RestAPI();
            try {
                JSONObject jsonObject = restAPI.MAddMenu(strings[0], strings[1], strings[2], strings[3],
                        strings[4], strings[5], strings[6]);
                JSONParse jsonParser = new JSONParse();
                result = jsonParser.parse(jsonObject);
            } catch (Exception e) {
                result = e.getMessage();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            stopAnimation();
            Log.d("reply getRestau ", s);
            if (s.contains("Unable to resolve host")) {
                AlertDialog.Builder ad = new AlertDialog.Builder(DetailMenuActivity.this);
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
                    Log.d("reply::", ans);
                    if (ans.compareTo("already") == 0) {
                        Snackbar.make(rLayout, "This Menu Item already exists", Snackbar.LENGTH_LONG).show();
                    } else if (ans.compareTo("true") == 0) {
                        finish();
                    } else if (ans.compareTo("error") == 0) {
                        String error = json.getString("Data");
                        Toast.makeText(DetailMenuActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(DetailMenuActivity.this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_switch_food, menu);
        MenuItem mi = menu.findItem(R.id.mi_switch_status);
        mi.setActionView(R.layout.switch_nearby);
        switchRestau = (Switch) mi.getActionView();
        if (getIntent().getSerializableExtra(UtilConstants.MENU_POJO) != null) {
            if (pojo.getStatus().equalsIgnoreCase("Yes")) {
                switchRestau.setChecked(true);
                checked = YES;
                isAvailTV.setText("NOTE: This food item is available");
            } else {
                switchRestau.setChecked(false);
                checked = "No";
                isAvailTV.setText("NOTE: This food item is not available");
            }
        } else {
            switchRestau.setChecked(true);
            checked = YES;
            isAvailTV.setText("NOTE: This food item is available");
        }
        switchRestau.setVisibility(View.VISIBLE);
        switchRestau.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switchRestau.setChecked(true);
                    checked = YES;
                    isAvailTV.setText("NOTE: This food item is available");
                } else {
                    switchRestau.setChecked(false);
                    checked = "No";
                    isAvailTV.setText("NOTE: This food item is not available");
                }
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
       /* if (!isUpdate) {
            menu.findItem(R.id.mi_del_food).setVisible(false);
        }*/
        return true;
    }

    public void startCamera() {
        if (PermissionUtils.requestPermission(
                this,
                CAMERA_PERMISSIONS_REQUEST,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getCameraFile());
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, CAMERA_IMAGE_REQUEST);
        }
    }

    public File getCameraFile() {
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(dir, FILE_NAME);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            if (data.getData() != null) {
                setImg(data.getData());
            }
        } else if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getCameraFile());
            if (photoUri != null) {
                setImg(photoUri);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_PERMISSIONS_REQUEST:
                if (PermissionUtils.permissionGranted(requestCode, CAMERA_PERMISSIONS_REQUEST, grantResults)) {
                    startCamera();
                }
                break;
            case GALLERY_PERMISSIONS_REQUEST:
                if (PermissionUtils.permissionGranted(requestCode, GALLERY_PERMISSIONS_REQUEST, grantResults)) {
                    startGalleryChooser();
                }
                break;
        }
    }


    public void setImg(Uri uri) {
        if (uri != null) {
            try {
                // scale the image to save on bandwidth
                Bitmap bitmap =
                        scaleBitmapDown(
                                MediaStore.Images.Media.getBitmap(getContentResolver(), uri),
                                MAX_DIMENSION);
                encode = null;
                imageView.setImageBitmap(bitmap);
                encode = UtilConstants.encodeBitmap(bitmap);
            } catch (IOException e) {
                Log.d("TAG", "Image picking failed because " + e.getMessage());
                Toast.makeText(this, "Selecting image failed", Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d("TAG", "Image picker gave us a null image.");
            Toast.makeText(this, "Error selecting an image  ", Toast.LENGTH_LONG).show();
        }
    }

    private Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    public void onImageUploadClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailMenuActivity.this);
        builder
                .setMessage(R.string.dialog_select_prompt)
                .setPositiveButton(R.string.dialog_select_gallery, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DetailMenuActivity.this.startGalleryChooser();
                    }
                })
                .setNegativeButton(R.string.dialog_select_camera, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DetailMenuActivity.this.startCamera();
                    }
                });
        builder.create().show();
    }

    public void startGalleryChooser() {
        if (PermissionUtils.requestPermission(this, GALLERY_PERMISSIONS_REQUEST, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select a photo"),
                    GALLERY_IMAGE_REQUEST);
        }
    }
}
