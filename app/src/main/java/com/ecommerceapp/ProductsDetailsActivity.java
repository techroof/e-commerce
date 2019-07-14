package com.ecommerceapp;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import io.paperdb.Paper;

public class ProductsDetailsActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Button addToCartBtn, addQuantityBtn, minQuantityBtn, tryProductBtn;
    private ImageView productDescImg;
    private TextView quantityNumText, pDescPriceText, pDescNameText, pDescText, pDescRatingText, pDescWarantyText, pLocationText, totalPriceText;
    private DatabaseReference dbref;
    private String pID, pDesc, pPrice, pRatings, pName, pWarranty, pImg, pLocation,
            userPhoneKey,pCategory;
    private FloatingActionButton gotoCartFAB;
    private int quantity = 1, totalPrice = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_details);

        pID = getIntent().getStringExtra("pos");

        toolbar = (Toolbar) findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_btn);
        getSupportActionBar().setTitle("");

        dbref = FirebaseDatabase.getInstance().getReference();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        gotoCartFAB = findViewById(R.id.goto_cart_fab);
        addToCartBtn = findViewById(R.id.add_to_cart_btn);
        productDescImg = findViewById(R.id.product_details_img);
        pDescNameText = findViewById(R.id.product_details_name);
        pDescPriceText = findViewById(R.id.product_details_price);
        pDescText = findViewById(R.id.product_details_description);
        pDescRatingText = findViewById(R.id.product_details_ratings);
        pDescWarantyText = findViewById(R.id.product_details_warranty);
        pLocationText = findViewById(R.id.product_details_location);
        addQuantityBtn = findViewById(R.id.quantity_add_btn);
        minQuantityBtn = findViewById(R.id.quantity_min_btn);
        totalPriceText = findViewById(R.id.product_details_total_price);
        quantityNumText = findViewById(R.id.quantity_num_text);
        tryProductBtn = findViewById(R.id.try_product_btn);


        addQuantityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity = quantity + 1;
                quantityNumText.setText("" + quantity);
                totalPrice = quantity * Integer.parseInt(pPrice);
                totalPriceText.setText("Total Price: Rs. " + totalPrice);
            }
        });

        minQuantityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity = quantity - 1;
                if (quantity <= 1) {
                    quantity = 1;
                }
                quantityNumText.setText("" + quantity);
                totalPrice = quantity * Integer.parseInt(pPrice);
                totalPriceText.setText("Total Price: Rs. " + totalPrice);

            }
        });

        Paper.init(this);

        userPhoneKey = Paper.book().read(Prevalent.userPhoneKey);

        dbref.child("products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pName = dataSnapshot.child(pID).child("name").getValue().toString();
                pDesc = dataSnapshot.child(pID).child("description").getValue().toString();
                pPrice = dataSnapshot.child(pID).child("price").getValue().toString();
                pImg = dataSnapshot.child(pID).child("image").getValue().toString();
                pCategory = dataSnapshot.child(pID).child("category").getValue().toString();


                totalPrice=Integer.parseInt(pPrice);

                if (dataSnapshot.child(pID).child("location").exists()) {
                    pLocation = dataSnapshot.child(pID).child("location").getValue().toString();
                } else {
                    pLocation = "N/A";
                }
                if (dataSnapshot.child(pID).child("ratings").exists()) {
                    pWarranty = dataSnapshot.child(pID).child("name").getValue().toString();
                } else {
                    pWarranty = "N/A";
                }
                if (dataSnapshot.child(pID).child("ratings").exists()) {
                    pRatings = dataSnapshot.child(pID).child("ratings").getValue().toString();
                } else {
                    pRatings = "N/A";
                }
                productData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        tryProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tryProduct = new Intent(ProductsDetailsActivity.this, TryProductActivity.class);
                tryProduct.putExtra("category",pCategory);
                tryProduct.putExtra("name",pName);
                startActivity(tryProduct);

            }
        });


        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCart();
            }
        });

        gotoCartFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cart = new Intent(ProductsDetailsActivity.this, CartActivity.class);
                startActivity(cart);
            }
        });
    }

    private void addToCart() {
        String currentTime, currentDate;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat date = new SimpleDateFormat("MMM dd,yyyy");
        currentDate = date.format(calendar.getTime());

        SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
        currentTime = time.format(calendar.getTime());

        final HashMap<String, Object> cartMap = new HashMap<>();
        cartMap.put("id", pID);
        cartMap.put("name", pName);
        cartMap.put("warranty", pWarranty);
        cartMap.put("price", pPrice);
        cartMap.put("description", pDesc);
        cartMap.put("location", pLocation);
        cartMap.put("date", currentDate);
        cartMap.put("time", currentTime);
        cartMap.put("image", pImg);
        cartMap.put("quantity", Integer.toString(quantity));
        cartMap.put("total", Integer.toString(totalPrice));

        if (userPhoneKey != "") {
            if (!TextUtils.isEmpty(userPhoneKey)) {

                dbref.child("cart").child("user view")
                        .child(userPhoneKey).child("products")
                        .child(pID).updateChildren(cartMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    dbref.child("cart").child("admin view")
                                            .child(userPhoneKey).child("products")
                                            .child(pID).updateChildren(cartMap)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(ProductsDetailsActivity.this,
                                                                "Added to cart", Toast.LENGTH_SHORT).show();

                                                    }
                                                }
                                            });
                                } else {
                                    Toast.makeText(ProductsDetailsActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                }

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProductsDetailsActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

            } else {
                Toast.makeText(this, "You need to login first", Toast.LENGTH_SHORT).show();
                Intent authentication = new Intent(ProductsDetailsActivity.this,
                        AuthenticationActivity.class);
                startActivity(authentication);
            }
        }

    }

    private void productData() {
        pDescNameText.setText(pName);
        pDescRatingText.setText("Rating: " + pRatings);
        pDescPriceText.setText("Rs. " + pPrice);
        pDescWarantyText.setText("Warranty: " + pWarranty);
        pDescText.setText(pDesc);
        pLocationText.setText("Location: " + pLocation);
        totalPriceText.setText("Total Price: " + pPrice);
        Picasso.get().load(pImg).into(productDescImg);
    }


}
