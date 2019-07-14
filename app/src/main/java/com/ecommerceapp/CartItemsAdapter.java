package com.ecommerceapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import io.paperdb.Paper;

public class CartItemsAdapter extends RecyclerView.Adapter<CartItemsAdapter.CartItemViewHolder> {
    private Context context;
    private DatabaseReference mref;
    private ArrayList<Cart> cart;
    private String userPhoneKey;
    private ProgressDialog pd;

    public CartItemsAdapter(Context context, ArrayList<Cart> cart) {
        this.context = context;
        this.cart = cart;
        mref = FirebaseDatabase.getInstance().getReference();
        Paper.init(context);

        userPhoneKey = Paper.book().read(Prevalent.userPhoneKey);
        pd = new ProgressDialog(context);
        pd.setMessage("Deleting item...");
        pd.setCanceledOnTouchOutside(false);
    }


    @NonNull
    @Override
    public CartItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_items_layout, parent, false);
        CartItemViewHolder viewHolder = new CartItemViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final CartItemViewHolder holder, final int position) {
        Picasso.get().load(cart.get(position).getImage()).into(holder.cartItemImg);
        holder.cartItemName.setText(cart.get(position).getName());
        holder.cartItemPrice.setText("Rs. "+cart.get(position).getTotal());
        holder.cartItemQuanity.setText(cart.get(position).getQuantity());

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.show();
                mref.child("cart").child("user view").child(userPhoneKey)
                        .child("products")
                        .child(cart.get(position).getId()).removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    mref.child("cart").child("admin view").child(userPhoneKey)
                                            .child("products")
                                            .child(cart.get(position).getId()).removeValue();
                                    Intent cart = new Intent(context, CartActivity.class);
                                    context.startActivity(cart);
                                    pd.dismiss();
                                } else {
                                    pd.dismiss();
                                    Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });
    }

    @Override
    public int getItemCount() {
        return cart.size();
    }

    public static class CartItemViewHolder extends RecyclerView.ViewHolder {
        ImageView cartItemImg;
        TextView cartItemName, cartItemPrice, cartItemQuanity, deleteBtn;

        public CartItemViewHolder(View itemView) {
            super(itemView);
            cartItemImg = itemView.findViewById(R.id.cart_product_img);
            cartItemName = itemView.findViewById(R.id.cart_product_name);
            cartItemPrice = itemView.findViewById(R.id.cart_product_price);
            cartItemQuanity = itemView.findViewById(R.id.quantity_num_text);
            deleteBtn = itemView.findViewById(R.id.delete_btn);
        }
    }
}
