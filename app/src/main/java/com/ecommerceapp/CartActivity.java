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

import java.util.ArrayList;
import java.util.HashMap;

import io.paperdb.Paper;

public class CartActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView cartRecyclerView;
    private CartItemsAdapter cartItemsAdapter;
    private ArrayList<Cart> list;
    private RecyclerView.LayoutManager layoutManager;
    private Button chkOutBtn;
    private TextView shippingFeeText, priceText;
    private String price, shippingFee, userPhoneKey;
    private DatabaseReference mref;
    private int totalPrice = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        toolbar = (Toolbar) findViewById(R.id.cart_activity_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Your Cart");

        chkOutBtn = findViewById(R.id.checkout_btn);
        shippingFeeText = findViewById(R.id.shipping_fee_text);
        priceText = findViewById(R.id.total_price_text);
        cartRecyclerView = findViewById(R.id.cart_rv);
        cartRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        cartRecyclerView.setLayoutManager(layoutManager);

        mref = FirebaseDatabase.getInstance().getReference();

        list = new ArrayList<Cart>();
        Paper.init(this);

        userPhoneKey = Paper.book().read(Prevalent.userPhoneKey);

        if (userPhoneKey != "") {
            if (!TextUtils.isEmpty(userPhoneKey)) {
                mref.child("cart").child("user view").child(userPhoneKey).child("products")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                    Cart c = dataSnapshot1.getValue(Cart.class);
                                    list.add(c);
                                    price = c.getTotal();
                                    totalPrice = totalPrice + Integer.parseInt(price)+130;
                                }
                                cartItemsAdapter = new CartItemsAdapter(CartActivity.this, list);
                                cartRecyclerView.setAdapter(cartItemsAdapter);
                                priceText.setText("Rs. " + totalPrice);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(CartActivity.this,
                                        databaseError.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });

            } else {
                Toast.makeText(this, "login first", Toast.LENGTH_SHORT).show();
            }
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        chkOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shippingDetails=new Intent(CartActivity.this,
                        ShippingDetailsActivity.class);
                String tp=Integer.toString(totalPrice);
                shippingDetails.putExtra("totalprice",tp);
                startActivity(shippingDetails);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
