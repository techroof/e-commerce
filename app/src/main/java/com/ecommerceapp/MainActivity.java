package com.ecommerceapp;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Toolbar toolbar;
    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mToggle;
    private NavigationView mNavigation;
    private String userPhoneKey, userPassKey, dbName;
    private DatabaseReference dbRef;
    private TextView userNameTv, loginTypeTv, userPhText;
    private Button navLoginBtn;
    private CircleImageView userImg;
    ViewFlipper viewFlipper;
    private RecyclerView recyclerView,productRecyclerView;
    private RecyclerAdapter adapter;
    private ProductViewHolder productViewHolder;
    private RecyclerView.LayoutManager layoutManager,productLayoutManager;
    ArrayList<Products> list;
    private FloatingActionButton cartFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int images[]={R.drawable.ecom_one,R.drawable.ecom_two,R.drawable.ecom_three,
                R.drawable.ecome_four,R.drawable.ecome_five};

        viewFlipper=findViewById(R.id.view_flipper);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        recyclerView=findViewById(R.id.new_products_rv);
        layoutManager=new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        productLayoutManager=new GridLayoutManager(this,2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        productRecyclerView=findViewById(R.id.products_rv);
        productRecyclerView.setHasFixedSize(true);
        productRecyclerView.setLayoutManager(productLayoutManager);


        list=new ArrayList<Products>();


        mDrawer = findViewById(R.id.nav_drawer);
        mToggle = new ActionBarDrawerToggle(this, mDrawer, R.string.open, R.string.close);
        mDrawer.addDrawerListener(mToggle);
        mToggle.syncState();
        mToggle.setDrawerIndicatorEnabled(true);

        toolbar = (Toolbar) findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu_nav_btn);
        getSupportActionBar().setTitle("Shop Online");

        dbRef = FirebaseDatabase.getInstance().getReference();

        dbRef.child("products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    Products p=dataSnapshot1.getValue(Products.class);
                    list.add(p);
                }
                adapter=new RecyclerAdapter(MainActivity.this,list);
                recyclerView.setAdapter(adapter);

                productViewHolder=new ProductViewHolder(MainActivity.this,list);
                productRecyclerView.setAdapter(productViewHolder);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        dbRef.keepSynced(true);

        Paper.init(this);

        dbName = Paper.book().read(Prevalent.loginType);

        setNavigationViewListener();

        userPhoneKey = Paper.book().read(Prevalent.userPhoneKey);
        userPassKey = Paper.book().read(Prevalent.userPasswordKey);
        //Toast.makeText(this, "logintype :" + dbName, Toast.LENGTH_SHORT).show();

        View navHeader = mNavigation.getHeaderView(0);
        userNameTv = navHeader.findViewById(R.id.username_text);
        loginTypeTv = navHeader.findViewById(R.id.login_type_text);
        userPhText = navHeader.findViewById(R.id.user_ph_text);
        navLoginBtn = navHeader.findViewById(R.id.nav_login_btn);
        userImg = navHeader.findViewById(R.id.user_img);
        cartFab=findViewById(R.id.cart_fab);

        cartFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cart=new Intent(MainActivity.this,CartActivity.class);
                startActivity(cart);
            }
        });

        final Typeface font = ResourcesCompat.getFont(this,R.font.open_sans);

        userNameTv.setTypeface(font,Typeface.BOLD);
        userPhText.setTypeface(font);
        loginTypeTv.setTypeface(font);

        if (userPhoneKey != "" && userPassKey != "") {
            if (!TextUtils.isEmpty(userPhoneKey) && !TextUtils.isEmpty(userPassKey)) {
                navLoginBtn.setVisibility(View.GONE);
                userNameTv.setVisibility(View.VISIBLE);
                loginTypeTv.setVisibility(View.VISIBLE);

                dbRef.child(dbName).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(userPhoneKey).child("image").exists()){
                            String img=dataSnapshot.child(userPhoneKey).child("image").getValue().toString();
                            Picasso.get().load(img).into(userImg);
                        }
                        if (dataSnapshot.child(userPhoneKey).child("name").exists()
                        && dataSnapshot.child(userPhoneKey).child("phone").exists()) {
                            String name = dataSnapshot.child(userPhoneKey).child("name").getValue().toString();
                            String phone = dataSnapshot.child(userPhoneKey).child("phone").getValue().toString();

                            userNameTv.setText(name);
                            if (dbName.equals("users")) {
                                loginTypeTv.setText("Logged in as User");
                            } else {
                                loginTypeTv.setText("Logged in as Admin");
                            }
                            userPhText.setText(phone);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            } else {
                navLoginBtn.setVisibility(View.VISIBLE);
                userNameTv.setVisibility(View.GONE);
                loginTypeTv.setVisibility(View.GONE);
            }
        }
        navLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent authentication = new Intent(MainActivity.this, AuthenticationActivity.class);
                startActivity(authentication);
            }
        });
        for (int image:images){
            flipImage(image);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.cart:
                Intent cart = new Intent(MainActivity.this, CartActivity.class);
                startActivity(cart);
                break;
            case R.id.settings:
                Intent settings = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settings);
                break;
            case R.id.orders:
                Intent orders = new Intent(MainActivity.this, OrdersActivity.class);
                startActivity(orders);
                break;
            case R.id.categories:
                Intent categories = new Intent(MainActivity.this, AdminCategoryActivity.class);
                startActivity(categories);
                break;
            case R.id.logout:
                Paper.book().destroy();
                finish();
                startActivity(getIntent());
                break;

        }
        mDrawer.closeDrawer(GravityCompat.START);
        return false;
    }

    public void setNavigationViewListener() {
        mNavigation = (NavigationView) findViewById(R.id.nav_view);
        mNavigation.setNavigationItemSelectedListener(this);
        if (dbName != "" || dbName != "admins" ) {
            if (!TextUtils.isEmpty(dbName) && dbName.equals("admins")) {

            } else {
                Menu m = mNavigation.getMenu();
                m.findItem(R.id.categories).setVisible(false);
                m.findItem(R.id.orders).setVisible(false);
            }
        }


    }
    public void flipImage(int images){
        ImageView imageView=new ImageView(this);
        imageView.setBackgroundResource(images);
        viewFlipper.addView(imageView);
        viewFlipper.setFlipInterval(4000);
        viewFlipper.setAutoStart(true);
        viewFlipper.setInAnimation(this,android.R.anim.slide_in_left);
        viewFlipper.setOutAnimation(this,android.R.anim.slide_out_right);

    }

}
