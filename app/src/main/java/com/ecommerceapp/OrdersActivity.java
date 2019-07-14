package com.ecommerceapp;

import android.os.Build;
import android.renderscript.Sampler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OrdersActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView ordersRecyclerView;
    private OrderRecylerAdapter orderRecylerAdapter;
    private ArrayList<orders> list;
    private RecyclerView.LayoutManager layoutManager;
    private DatabaseReference mref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        toolbar = (Toolbar) findViewById(R.id.orders_activity_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Orders");

        ordersRecyclerView=findViewById(R.id.order_rv);
        ordersRecyclerView.setHasFixedSize(true);

        layoutManager=new LinearLayoutManager(this);
        ordersRecyclerView.setLayoutManager(layoutManager);

        mref = FirebaseDatabase.getInstance().getReference();

        list = new ArrayList<orders>();

        mref.child("orders").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                        orders order=dataSnapshot1.getValue(orders.class);
                        list.add(order);
                    orderRecylerAdapter=new OrderRecylerAdapter(OrdersActivity.this,list);
                    ordersRecyclerView.setAdapter(orderRecylerAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(OrdersActivity.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
