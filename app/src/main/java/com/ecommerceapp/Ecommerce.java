package com.ecommerceapp;

import android.app.Application;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.firebase.database.FirebaseDatabase;

public class Ecommerce extends Application{
    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().getReference().keepSynced(true);
    }
}