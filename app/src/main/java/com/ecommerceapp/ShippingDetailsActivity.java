package com.ecommerceapp;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ShippingDetailsActivity extends AppCompatActivity {
    private Toolbar toolbar;

    private Button sdSaveBtn;
    private EditText sdName, sdPhNum, sdAdd, sdCity;
    private String name, phNum, address, city, totalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipping_details);

        toolbar = (Toolbar) findViewById(R.id.shiping_details_activity_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Shipping Details");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        totalPrice = getIntent().getStringExtra("totalprice");

        sdName = findViewById(R.id.sd_name);
        sdAdd = findViewById(R.id.sd_address);
        sdCity = findViewById(R.id.sd_city);
        sdPhNum = findViewById(R.id.sd_phnum);
        sdSaveBtn = findViewById(R.id.sd_save_btn);

        sdSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = sdName.getText().toString();
                city = sdCity.getText().toString();
                phNum = sdPhNum.getText().toString();
                address = sdAdd.getText().toString();

                if(!TextUtils.isEmpty(name)&&!TextUtils.isEmpty(city)
                        &&!TextUtils.isEmpty(phNum)&&!TextUtils.isEmpty(address)){
                    Intent checkOut = new Intent(ShippingDetailsActivity.this, CheckOutActivity.class);

                    checkOut.putExtra("name", name);
                    checkOut.putExtra("address", address);
                    checkOut.putExtra("city", city);
                    checkOut.putExtra("phnum", phNum);
                    checkOut.putExtra("totalprice", totalPrice);
                    startActivity(checkOut);
                }else{
                    Toast.makeText(ShippingDetailsActivity.this, "Please enter full shipping details", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}
