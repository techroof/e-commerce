package com.ecommerceapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddProductActivity extends AppCompatActivity {
    private String category, currentDate, currentTime, productRandomKey, imgURL,
            productName, productDesc, productPrice;
    private static final int GALLERY_PICK_CODE = 1;
    private ImageView imgAddBtn;
    private Uri imgUri;
    private Button addProductBtn;
    private EditText productNameEt, productDescEt, productPriceEt;
    private StorageReference stRef;
    private DatabaseReference dbRef;
    private ProgressDialog pd;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_product);

        category = getIntent().getStringExtra("category");
        toolbar = (Toolbar) findViewById(R.id.add_product_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add " + category);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Toast.makeText(this, category, Toast.LENGTH_SHORT).show();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        pd = new ProgressDialog(this);
        pd.setCanceledOnTouchOutside(false);
        dbRef = FirebaseDatabase.getInstance().getReference();
        stRef = FirebaseStorage.getInstance().getReference();
        addProductBtn = findViewById(R.id.add_product_btn);
        productNameEt = findViewById(R.id.product_name_et);
        productDescEt = findViewById(R.id.product_description_et);
        productPriceEt = findViewById(R.id.product_price_et);

        imgAddBtn = findViewById(R.id.add_image_btn);
        imgAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });
        addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productName = productNameEt.getText().toString();
                productDesc = productDescEt.getText().toString();
                productPrice = productPriceEt.getText().toString();
                if (TextUtils.isEmpty(productName)) {
                    productNameEt.setError("Enter product name");
                } else if (TextUtils.isEmpty(productDesc)) {
                    productDescEt.setError("Enter product description");
                } else if (TextUtils.isEmpty(productPrice)) {
                    productPriceEt.setError("Enter product price");
                }

                if (!TextUtils.isEmpty(productName) && !TextUtils.isEmpty(productDesc) &&
                        !TextUtils.isEmpty(productPrice)) {
                    pd.setMessage("Please wait...");
                    pd.show();
                    validateProductData(productName, productDesc, productPrice);
                }

            }
        });
    }

    private void validateProductData(String name, String desc, String price) {
        if (imgUri == null) {
            pd.dismiss();
            Toast.makeText(this, "Please upload an image", Toast.LENGTH_LONG).show();
        } else {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat date = new SimpleDateFormat("MM dd,yyyy");
            currentDate = date.format(calendar.getTime());

            SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
            currentTime = time.format(calendar.getTime());

            productRandomKey = currentDate + "_" + currentTime;

            final StorageReference filePath = stRef.child("product images").child(category)
                    .child(imgUri.getLastPathSegment() + "_" + productRandomKey);

            final UploadTask uploadTask = filePath.putFile(imgUri);
            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        pd.dismiss();
                        Toast.makeText(AdminAddProductActivity.this, "Uploaded Successfully", Toast.LENGTH_LONG).show();
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
                                Toast.makeText(AdminAddProductActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                                saveProductInfo();
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
                    Toast.makeText(AdminAddProductActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                }
            });

        }
    }

    private void saveProductInfo() {
        HashMap<String, String> productMap = new HashMap<>();
        productMap.put("pid", productRandomKey);
        productMap.put("date", currentDate);
        productMap.put("time", currentTime);
        productMap.put("description", productDesc);
        productMap.put("name", productName);
        productMap.put("price", productPrice);
        productMap.put("category", category);
        productMap.put("image", imgURL);

        dbRef.child("products").child(productRandomKey).setValue(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(AdminAddProductActivity.this, "Product added", Toast.LENGTH_SHORT).show();

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AdminAddProductActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadImage() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_PICK_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK_CODE && resultCode == RESULT_OK && data != null) {
            imgUri = data.getData();
            imgAddBtn.setImageURI(imgUri);
        }
    }
}
