package com.ecommerceapp;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import io.paperdb.Paper;

public class AdminViewOrderActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView viewOrderRV;
    private ViewOrderAdapter viewOrderAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Cart> list;
    private String userPhoneKey;
    private DatabaseReference mref;
    private String userAccPh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_order);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        userAccPh=getIntent().getStringExtra("accountphnum");

        toolbar = (Toolbar) findViewById(R.id.admin_view_order_activity_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Orders");

        viewOrderRV=findViewById(R.id.view_order_rv);
        viewOrderRV.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        viewOrderRV.setLayoutManager(layoutManager);

        mref = FirebaseDatabase.getInstance().getReference();

        list = new ArrayList<Cart>();
        Paper.init(this);

        userPhoneKey = Paper.book().read(Prevalent.userPhoneKey);

        if (userPhoneKey != "") {
            if (!TextUtils.isEmpty(userPhoneKey)) {
                mref.child("cart").child("admin view").child(userAccPh).child("products")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                    Cart c = dataSnapshot1.getValue(Cart.class);
                                    list.add(c);

                                }
                                viewOrderAdapter = new ViewOrderAdapter(AdminViewOrderActivity.this, list);
                                viewOrderRV.setAdapter(viewOrderAdapter);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(AdminViewOrderActivity.this,
                                        databaseError.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });

            } else {
                Toast.makeText(this, "login first", Toast.LENGTH_SHORT).show();
            }
        }



    }
}
