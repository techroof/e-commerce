package com.ecommerceapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class OrderRecylerAdapter extends RecyclerView.Adapter<OrderRecylerAdapter.OrderItemViewHolder> {
    private Context context;
    private ArrayList<orders> orders;
    private DatabaseReference dbref;

    public OrderRecylerAdapter(Context context, ArrayList<com.ecommerceapp.orders> orders) {
        this.context = context;
        this.orders = orders;
        dbref= FirebaseDatabase.getInstance().getReference();
    }


    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout,
                parent, false);
        OrderItemViewHolder orderItemViewHolder = new OrderItemViewHolder(v);

        return orderItemViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        holder.orderNameText.setText(orders.get(position).getName());
        holder.orderAddressText.setText(orders.get(position)
                .getAddress() + ", " + orders.get(position).getCity());
        holder.orderPhoneNumText.setText(orders.get(position).getPhone());
        holder.orderTimeText.setText(orders.get(position).getTime());
        holder.orderDateText.setText(orders.get(position).getDate());
        holder.orderTotalPriceText.setText(orders.get(position).getTotalprice());

        holder.viewOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewOrder = new Intent(context, AdminViewOrderActivity.class);
                viewOrder.putExtra("accountphnum", orders.get(position).getAccountphnum());
                context.startActivity(viewOrder);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                CharSequence options[] = new CharSequence[]{
                        "YES", "NO"
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Have you shipped the order?");

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            if (which==0){
                                dbref.child("orders").
                                        child(orders.get(position).getAccountphnum()).removeValue();
                                Toast.makeText(context, "Order shipped and removed from list",
                                        Toast.LENGTH_SHORT).show();
                                dbref.child("cart").child("admin view").child(orders.get(position).getAccountphnum())
                                        .removeValue();

                                Intent order = new Intent(context, OrdersActivity.class);
                                context.startActivity(order);
                            }else{

                            }
                    }
                });
                    builder.show();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class OrderItemViewHolder extends RecyclerView.ViewHolder {
        TextView orderNameText, orderAddressText, orderPhoneNumText, orderTimeText, orderDateText, orderTotalPriceText;
        Button viewOrderBtn;

        public OrderItemViewHolder(View itemView) {
            super(itemView);
            orderNameText = itemView.findViewById(R.id.order_name);
            orderAddressText = itemView.findViewById(R.id.order_address);
            orderPhoneNumText = itemView.findViewById(R.id.order_ph);
            orderTimeText = itemView.findViewById(R.id.order_time);
            orderDateText = itemView.findViewById(R.id.order_date);
            viewOrderBtn = itemView.findViewById(R.id.view_order_btn);
            orderTotalPriceText = itemView.findViewById(R.id.order_total_price);
        }
    }

}
