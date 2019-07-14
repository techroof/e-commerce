package com.ecommerceapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class SettingsActivity extends AppCompatActivity {
    private Button settingsSaveBtn;
    private Toolbar toolbar;
    private EditText fullNameEt, addressEt;
    private CircleImageView userImg;
    private static final int GALLERY_PICK_CODE = 1;
    private Uri imageUri;
    private StorageReference stRef;
    private String fullName, address,currentDate,currentTime,productRandomKey;
    private String userPhoneKey, userPassKey, dbName,imgURL;
    private ProgressDialog pd;
    private DatabaseReference dbref;
    private Users users;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toolbar = findViewById(R.id.settings_activity_toolbar);
        settingsSaveBtn = findViewById(R.id.settings_done_btn);
        fullNameEt = findViewById(R.id.full_name_et);
        addressEt = findViewById(R.id.address_et);
        userImg=findViewById(R.id.user_profile_img);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Settings");

        pd=new ProgressDialog(this);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("Please wait...");
        users=new Users();

        Paper.init(this);
        stRef= FirebaseStorage.getInstance().getReference();
        dbName = Paper.book().read(Prevalent.loginType);
        userPhoneKey = Paper.book().read(Prevalent.userPhoneKey);
        userPassKey = Paper.book().read(Prevalent.userPasswordKey);

        dbref= FirebaseDatabase.getInstance().getReference();

        if (userPhoneKey != "" && userPassKey != "") {
            if (!TextUtils.isEmpty(userPhoneKey) && !TextUtils.isEmpty(userPassKey)) {

                Toast.makeText(this, dbName, Toast.LENGTH_SHORT).show();
            }else {
                dbName = "logintype";
                Toast.makeText(this, dbName, Toast.LENGTH_SHORT).show();
            }
        }else{
            dbName = "logintype";
            Toast.makeText(this, dbName, Toast.LENGTH_SHORT).show();
        }

        if (dbName.equals("logintype")){
            Intent authentication=new Intent(SettingsActivity.this,AuthenticationActivity.class);
            startActivity(authentication);

        }else {
            dbref.child(dbName).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(userPhoneKey).exists()){
                        if (dataSnapshot.child(userPhoneKey).child("image").exists()
                                && dataSnapshot.child(userPhoneKey).child("address").exists())
                        {
                            String img=dataSnapshot.child(userPhoneKey).child("image").getValue().toString();
                            String address=dataSnapshot.child(userPhoneKey).child("address").getValue().toString();
                            String name=dataSnapshot.child(userPhoneKey).child("name").getValue().toString();

                            Picasso.get().load(img).into(userImg);
                            fullNameEt.setText(name);
                            addressEt.setText(address);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }



        userImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent();
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                startActivityForResult(gallery, GALLERY_PICK_CODE);
               }
        });

        settingsSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullName=fullNameEt.getText().toString();
                address=addressEt.getText().toString();

                if (!TextUtils.isEmpty(fullName) && !TextUtils.isEmpty(address)){

                    pd.show();
                    saveData(fullName,address);
                }
            }
        });

    }

    public void uploadImg() {
            pd.show();
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat date = new SimpleDateFormat("MM dd,yyyy");
            currentDate = date.format(calendar.getTime());

            SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
            currentTime = time.format(calendar.getTime());

            productRandomKey = currentDate + "_" + currentTime;

            final StorageReference filePath = stRef.child("profile image").child(dbName)
                    .child(userPhoneKey)
                    .child(imageUri.getLastPathSegment() + "_" + productRandomKey);

            final UploadTask uploadTask = filePath.putFile(imageUri);
            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        pd.dismiss();
                        Toast.makeText(SettingsActivity.this, "Uploaded Successfully", Toast.LENGTH_LONG).show();
                        Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (task.isSuccessful()) {
                                    pd.dismiss();
                                    imgURL = filePath.getDownloadUrl().toString();
                                    return filePath.getDownloadUrl();


                                } else {
                                    pd.dismiss();
                                    throw task.getException();
                                }
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                pd.dismiss();
                                imgURL = task.getResult().toString();
                                Map imgMap=new HashMap();
                                imgMap.put("image",imgURL);
                                dbref.child(dbName).child(userPhoneKey).updateChildren(imgMap);
                                Toast.makeText(SettingsActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred())
                            / taskSnapshot.getTotalByteCount();
                    pd.setMessage((int) progress + "% Uploaded");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(SettingsActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                }
            });




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK_CODE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            userImg.setImageURI(imageUri);
            uploadImg();
        }
    }

    public void saveData(String fname,String address){
        Map settingsMap=new HashMap();
        settingsMap.put("name",fname);
        settingsMap.put("address",address);
        dbref.child(dbName).child(userPhoneKey).updateChildren(settingsMap)
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()){
                            pd.dismiss();
                            Intent main=new Intent(SettingsActivity.this,MainActivity.class);
                            startActivity(main);

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(SettingsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
