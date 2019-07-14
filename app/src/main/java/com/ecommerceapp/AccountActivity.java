package com.ecommerceapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import io.paperdb.Paper;

public class AccountActivity extends AppCompatActivity {
    private String userPhoneKey, userPassKey;
    private DatabaseReference dbRef;
    private TextView userPhoneText;
    private Button logoutBtn;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        dbRef = FirebaseDatabase.getInstance().getReference();
        logoutBtn = findViewById(R.id.signout_btn);
        userPhoneText = findViewById(R.id.userPhNum_tv);
        pd = new ProgressDialog(this);
        pd.setMessage("Please wait...");
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        Paper.init(this);
        userPhoneKey = Paper.book().read(Prevalent.userPhoneKey);
        userPassKey = Paper.book().read(Prevalent.userPasswordKey);

        if (userPhoneKey != "" && userPassKey != "") {
            if (!TextUtils.isEmpty(userPhoneKey) && !TextUtils.isEmpty(userPassKey)) {
                login(userPhoneKey, userPassKey);
                userPhoneText.setText(userPhoneKey);
            } else {
                Intent authentication = new Intent(AccountActivity.this, AuthenticationActivity.class);
                startActivity(authentication);
            }
        }
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Paper.book().destroy();
                Intent authentication = new Intent(AccountActivity.this, AuthenticationActivity.class);
                startActivity(authentication);
            }
        });
    }

    public void login(final String userPhone, final String userPass) {
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("users").child(userPhone).exists()) {
                    Users userData = dataSnapshot.child("users").child(userPhone).getValue(Users.class);

                    if (userData.getPassword().equals(userPass)) {
                        pd.dismiss();
                        Toast.makeText(AccountActivity.this, "Logged in as " + userPhone, Toast.LENGTH_LONG).show();
                    } else {
                        pd.dismiss();
                        Intent main = new Intent(AccountActivity.this, AuthenticationActivity.class);
                        startActivity(main);
                    }
                } else {
                    pd.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AccountActivity.this, "Error", Toast.LENGTH_LONG).show();
            }
        });
    }

}
