package com.ecommerceapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.HashMap;

import io.paperdb.Paper;

public class AuthenticationActivity extends AppCompatActivity {
    private EditText loginPhoneET, loginPasswordET, regPhoneET, regPasswordET, regNameET;
    private Button loginBtn, registerBtn;
    private TextView forgetPassText, adminLoginText;
    private DatabaseReference dbref;
    private ProgressDialog pd;
    private String dbName = "users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        Paper.init(this);
        loginBtn = findViewById(R.id.login_btn);
        registerBtn = findViewById(R.id.reg_btn);
        loginPhoneET = findViewById(R.id.login_phone_et);
        loginPasswordET = findViewById(R.id.login_password_et);
        regPhoneET = findViewById(R.id.reg_phone_et);
        regPasswordET = findViewById(R.id.reg_password_et);
        regNameET = findViewById(R.id.reg_name_et);
        pd = new ProgressDialog(this);
        pd.setCanceledOnTouchOutside(false);
        adminLoginText = findViewById(R.id.admin_login_text);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        final int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk > Build.VERSION_CODES.KITKAT) {
            registerBtn.setBackgroundResource(R.drawable.btn_bg);
        }

            adminLoginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dbName.equals("users")){
                    dbName = "admins";
                    loginPhoneET.setHint("Admin phone");
                    adminLoginText.setText("Login as users");
                }else if (dbName.equals("admins")){
                    dbName = "users";
                    loginPhoneET.setHint("Phone");
                    adminLoginText.setText("Login as admin");
                }
            }
        });

        dbref = FirebaseDatabase.getInstance().getReference();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.setMessage("Logging in");
                String phone = loginPhoneET.getText().toString();
                String password = loginPasswordET.getText().toString();
                if (TextUtils.isEmpty(phone)) {
                    loginPhoneET.setError("Enter phone number");
                } else if (TextUtils.isEmpty(password)) {
                    loginPasswordET.setError("Enter password");
                } else {
                    pd.show();
                    loginUser(phone, password);
                }
            }
        });

       registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.setMessage("Creating account");
                String phone = regPhoneET.getText().toString();
                String password = regPasswordET.getText().toString();
                String name = regNameET.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    regNameET.setError("Enter name");
                } else if (TextUtils.isEmpty(phone)) {
                    regPhoneET.setError("Enter phone number");
                } else if (TextUtils.isEmpty(password)) {
                    regPasswordET.setError("Enter password");
                } else {
                    pd.show();
                    registerUser(phone, password, name);
                }
            }
        });

    }

    private void registerUser(final String phone, final String password, final String name) {
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(dbName).child(phone).exists()) {
                    pd.dismiss();
                    Toast.makeText(AuthenticationActivity.this, "This phone number already exixts. Please try another one", Toast.LENGTH_LONG).show();
                } else {
                    HashMap<String, String> userMap = new HashMap<>();
                    userMap.put("name", name);
                    userMap.put("phone", phone);
                    userMap.put("password", password);
                    dbref.child(dbName).child(phone).setValue(userMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        pd.dismiss();
                                        Paper.book().write(Prevalent.userPhoneKey, phone);
                                        Paper.book().write(Prevalent.userPasswordKey, password);
                                        Paper.book().write(Prevalent.loginType, dbName);

                                        Intent main = new Intent(AuthenticationActivity.this, MainActivity.class);
                                        main.putExtra("dbname",dbName);
                                        startActivity(main);
                                    } else {
                                        pd.dismiss();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(AuthenticationActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                pd.dismiss();
                Toast.makeText(AuthenticationActivity.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginUser(final String phone, final String password) {
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(dbName).child(phone).exists()) {
                    Users userData = dataSnapshot.child(dbName).child(phone).getValue(Users.class);

                    if (userData.getPassword().equals(password)) {
                        if (dbName.equals("admins")) {
                            pd.dismiss();
                            Paper.book().write(Prevalent.userPhoneKey, phone);
                            Paper.book().write(Prevalent.userPasswordKey, password);
                            Paper.book().write(Prevalent.loginType, dbName);
                            Intent main = new Intent(AuthenticationActivity.this, MainActivity.class);
                            main.putExtra("dbname",dbName);
                            startActivity(main);
                            Toast.makeText(AuthenticationActivity.this, "logged in as admin", Toast.LENGTH_SHORT).show();

                        } else if (dbName.equals("users")) {
                            pd.dismiss();
                            Paper.book().write(Prevalent.userPhoneKey, phone);
                            Paper.book().write(Prevalent.userPasswordKey, password);
                            Paper.book().write(Prevalent.loginType, dbName);
                            Intent main = new Intent(AuthenticationActivity.this, MainActivity.class);
                            main.putExtra("dbname",dbName);
                            startActivity(main);
                            Toast.makeText(AuthenticationActivity.this, "logged in as" + phone, Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        pd.dismiss();
                        Toast.makeText(AuthenticationActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();

                    }
                } else {
                    pd.dismiss();
                    Toast.makeText(AuthenticationActivity.this, "Phone number doesn't exists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                pd.dismiss();
                Toast.makeText(AuthenticationActivity.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
