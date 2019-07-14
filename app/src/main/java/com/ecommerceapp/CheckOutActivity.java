package com.ecommerceapp;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import io.paperdb.Paper;

public class CheckOutActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView nameText,addressText,phNumText,totalPriceText,timeText,dateText;
    private String name,currentTime,currentDate,city,address,totalPrice,phNum,userPhoneKey;
    private Button confirmBtn;
    private DatabaseReference dbref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);

        toolbar = (Toolbar) findViewById(R.id.checkout_activity_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Your Order");

        dbref=FirebaseDatabase.getInstance().getReference();
        Paper.init(this);

        userPhoneKey = Paper.book().read(Prevalent.userPhoneKey);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat date = new SimpleDateFormat("MMM dd,yyyy");
        currentDate= date.format(calendar.getTime());

        SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
        currentTime = time.format(calendar.getTime());

        name=getIntent().getStringExtra("name");
        city=getIntent().getStringExtra("city");
        address=getIntent().getStringExtra("address");
        phNum=getIntent().getStringExtra("phnum");
        totalPrice=getIntent().getStringExtra("totalprice");

        nameText=findViewById(R.id.chkout_name);
        addressText=findViewById(R.id.chkout_address);
        phNumText=findViewById(R.id.chkout_ph);
        totalPriceText=findViewById(R.id.checkout_total_price_text);
        timeText=findViewById(R.id.chkout_time);
        dateText=findViewById(R.id.chkout_date);
        confirmBtn=findViewById(R.id.confirm_order_btn);

        nameText.setText(name);
        addressText.setText("Shipping at: "+address+", "+city);
        phNumText.setText(phNum);
        timeText.setText("Time: "+currentTime);
        dateText.setText("Date: "+currentDate);
        totalPriceText.setText(totalPrice);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> orderMap=new HashMap<>();
                orderMap.put("name",name);
                orderMap.put("address",address);
                orderMap.put("date",currentDate);
                orderMap.put("time",currentTime);
                orderMap.put("status","not shipped");
                orderMap.put("city",city);
                orderMap.put("phone",phNum);
                orderMap.put("totalprice",totalPrice);
                orderMap.put("accountphnum",userPhoneKey);

                dbref.child("orders").child(userPhoneKey).setValue(orderMap);
                Intent main=new Intent(CheckOutActivity.this,MainActivity.class);
                startActivity(main);

                dbref.child("cart").child("user view").child(userPhoneKey).removeValue();
                Toast.makeText(CheckOutActivity.this, "Order placed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
