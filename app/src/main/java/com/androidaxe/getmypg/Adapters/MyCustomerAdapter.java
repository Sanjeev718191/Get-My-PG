package com.androidaxe.getmypg.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.androidaxe.getmypg.Activities.OwnerCustomerDetailsActivity;
import com.androidaxe.getmypg.Module.PGUser;
import com.androidaxe.getmypg.Module.UserSubscribedItem;
import com.androidaxe.getmypg.R;
import com.androidaxe.getmypg.databinding.ItemMyCustomerBinding;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MyCustomerAdapter extends RecyclerView.Adapter<MyCustomerAdapter.ViewHolder> {

    ArrayList<UserSubscribedItem> items;
    ArrayList<PGUser> users;
    Context context;

    public MyCustomerAdapter(Context context, ArrayList<UserSubscribedItem> items, ArrayList<PGUser> users) {
        this.items = items;
        this.users = users;
        this.context = context;
    }

//    public void add(UserSubscribedItem item){
//        items.add(item);
////        Collections.sort(items);
//        notifyDataSetChanged();
//    }
//
//    public void clear(){
//        items.clear();
//        notifyDataSetChanged();
//    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_my_customer, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(position >= items.size() || position >= users.size()) return;
        UserSubscribedItem item = items.get(position);
        PGUser user = users.get(position);
        holder.binding.myCustomerDueDate.setText("Due date : "+item.getToDate());
        if(item.getToDate().equals("na")){
            holder.binding.myCustomerPlanStatus.setTextColor(context.getColor(R.color.red));
            holder.binding.myCustomerPlanStatus.setText("Not Started Yet");
        } else {
            String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            try {
                Date currDate = sdf.parse(date);
                Date toDate = sdf.parse(item.getToDate());
                Date fromDate = sdf.parse(item.getFromDate());
                if(toDate.compareTo(currDate) >= 0){
                    holder.binding.myCustomerPlanStatus.setTextColor(context.getColor(R.color.primary));
                    holder.binding.myCustomerPlanStatus.setText("Active");
                } else {
                    holder.binding.myCustomerPlanStatus.setTextColor(context.getColor(R.color.red));
                    holder.binding.myCustomerPlanStatus.setText("Expired");
                    FirebaseDatabase.getInstance().getReference("Subscription").child(item.getSubscriptionId()).child("currentlyActive").setValue("false");
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

        holder.binding.myCustomerName.setText(user.getName());
        Glide.with(context).load(user.getProfile()).into(holder.binding.myCustomerImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, OwnerCustomerDetailsActivity.class);
                intent.putExtra("SubId", item.getSubscriptionId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ItemMyCustomerBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemMyCustomerBinding.bind(itemView);
        }
    }

}
