package com.ecommerceapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductViewHolder extends RecyclerView.Adapter<ProductViewHolder.PDataViewHolder>{
        private Context context;
        private ArrayList<Products> products;

    public ProductViewHolder(Context context, ArrayList<Products> products) {
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public PDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v=LayoutInflater.from(parent.getContext())
                .inflate(R.layout.products_layout,parent,false);
        PDataViewHolder pDataViewHolder=new PDataViewHolder(v);

        return pDataViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PDataViewHolder holder, final int position) {
        Picasso.get().load(products.get(position).getImage()).into(holder.pImg);
        holder.pName.setText(products.get(position).getName());
        holder.pPrice.setText("Rs. "+products.get(position).getPrice());

        final Typeface font = ResourcesCompat.getFont(context,R.font.open_sans);
        holder.pName.setTypeface(font,Typeface.BOLD);
        holder.pPrice.setTypeface(font);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent productDetailsActivity=new Intent(context,ProductsDetailsActivity.class);
                productDetailsActivity.putExtra("pos",products.get(position).getPid());

                context.startActivity(productDetailsActivity);

            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class PDataViewHolder extends RecyclerView.ViewHolder{
         TextView pName,pPrice;
         ImageView pImg;

        public PDataViewHolder(View itemView) {
            super(itemView);

            pName=itemView.findViewById(R.id.product_name);
            pPrice=itemView.findViewById(R.id.product_price);
            pImg=itemView.findViewById(R.id.product_img);
        }
    }
}