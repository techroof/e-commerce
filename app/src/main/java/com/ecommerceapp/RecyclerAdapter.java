package com.ecommerceapp;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.LayoutInflater;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ImageViewHolder> {
    //private int images[];
    Context context;
    ArrayList<Products> products;

    public RecyclerAdapter(Context context,ArrayList<Products> p){
        this.products=p;
        this.context=context;
    }
    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.new_products_layout, parent, false);
        ImageViewHolder imageViewHolder = new ImageViewHolder(view);

        return imageViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
       // int imageId=images[position];
        Picasso.get().load(products.get(position).getImage()).into(holder.newProdImg);
       // holder.newProdImg.setImageResource(products.get(position).);
        holder.newProdName.setText(products.get(position).getName());
        holder.newProdPrice.setText("Rs. "+products.get(position).getPrice());

        final Typeface font = ResourcesCompat.getFont(context,R.font.open_sans);
        holder.newProdName.setTypeface(font,Typeface.BOLD);
        holder.newProdPrice.setTypeface(font);

    }

    @Override
    public int getItemCount() {
        int size;
        if (products.size()<=7){
            size=products.size();
        }else{
            size=7;
        }
        return size;
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView newProdImg;
        TextView newProdName, newProdPrice;

        public ImageViewHolder(View itemView) {
            super(itemView);
            newProdImg = itemView.findViewById(R.id.new_product_img);
            newProdName = itemView.findViewById(R.id.new_product_tv);
            newProdPrice = itemView.findViewById(R.id.new_product_price);

        }
    }

}
