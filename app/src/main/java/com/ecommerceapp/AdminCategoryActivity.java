package com.ecommerceapp;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class AdminCategoryActivity extends AppCompatActivity {
    private ImageView shirtImg, tshirtImg, ladiesDressImg, glassesImg, shoeImg, headPhoneImg,
            jacketImg, purseImg, watchImg, laptopImg, mobileImg, hatImg;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);

        toolbar = (Toolbar) findViewById(R.id.category_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Product Categories");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        shirtImg = findViewById(R.id.shirt_img);
        tshirtImg = findViewById(R.id.t_shirt_img);
        ladiesDressImg = findViewById(R.id.ladies_dress_img);
        glassesImg = findViewById(R.id.glasses_img);
        shoeImg = findViewById(R.id.shoe_img);
        jacketImg = findViewById(R.id.jacket_img);
        headPhoneImg = findViewById(R.id.headphone_img);
        purseImg = findViewById(R.id.purse_img);
        watchImg = findViewById(R.id.watch_img);
        laptopImg = findViewById(R.id.laptop_img);
        mobileImg = findViewById(R.id.phone_img);
        hatImg = findViewById(R.id.hat_img);
    }
    public void addProduct(View v){
        String tag=v.getTag().toString();
        Intent addProductIntent=new Intent(AdminCategoryActivity.this,AdminAddProductActivity.class);
        addProductIntent.putExtra("category",tag);
        startActivity(addProductIntent);
    }
}
