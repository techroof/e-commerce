package com.ecommerceapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import io.paperdb.Paper;

public class ViewOrderAdapter extends RecyclerView.Adapter<ViewOrderAdapter.ViewOrderViewHolder>{
        private Context context;
        private ArrayList<Cart> cart;
        private String userPhoneKey;


    public ViewOrderAdapter(Context context, ArrayList<Cart> cart) {
        this.context = context;
        this.cart = cart;
        Paper.init(context);

        userPhoneKey = Paper.book().read(Prevalent.userPhoneKey);
    }


    @NonNull
    @Override
    public ViewOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(
                R.layout.view_orders_layout,parent,false);
        ViewOrderViewHolder viewOrderViewHolder=new ViewOrderViewHolder(v);

        return viewOrderViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewOrderViewHolder holder, int position) {
        Picasso.get().load(cart.get(position).getImage()).into(holder.orderItemImg);
        holder.orderItemName.setText(cart.get(position).getName());
        holder.orderItemPrice.setText("Rs. "+cart.get(position).getTotal());
        holder.orderItemQuanity.setText(cart.get(position).getQuantity());

    }

    @Override
    public int getItemCount() {
        return cart.size();
    }

    public static class ViewOrderViewHolder extends RecyclerView.ViewHolder {
    ImageView orderItemImg;
    TextView orderItemName, orderItemPrice, orderItemQuanity,orderTotalPriceText;

    public ViewOrderViewHolder(View itemView) {
        super(itemView);
        orderItemImg = itemView.findViewById(R.id.order_product_img);
        orderItemName = itemView.findViewById(R.id.order_product_name);
        orderItemPrice = itemView.findViewById(R.id.order_product_price);
        orderItemQuanity = itemView.findViewById(R.id.order_quantity_num_text);
    }
}

}
