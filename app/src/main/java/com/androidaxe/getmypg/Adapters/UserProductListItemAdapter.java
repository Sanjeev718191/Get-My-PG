package com.androidaxe.getmypg.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidaxe.getmypg.Activities.UserSubscriptionActivity;
import com.androidaxe.getmypg.Module.OwnerMess;
import com.androidaxe.getmypg.Module.OwnerPG;
import com.androidaxe.getmypg.Module.UserSubscribedItem;
import com.androidaxe.getmypg.R;
import com.androidaxe.getmypg.databinding.UserProductsListItemBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class UserProductListItemAdapter extends RecyclerView.Adapter<UserProductListItemAdapter.ViewHolder>{

    Context context;
    ArrayList<UserSubscribedItem> items;
    OwnerMess mess;
    OwnerPG pg;

    public UserProductListItemAdapter(Context context) {
        this.context = context;
        this.items = new ArrayList<>();
    }

    public void add(UserSubscribedItem item){
        items.add(item);
        notifyDataSetChanged();
    }
    public void clear(){
        items.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.user_products_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserSubscribedItem item = items.get(position);
        if(item.getType().equals("pg")) {
            FirebaseDatabase.getInstance().getReference("PGs").child(item.getPGMessId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    pg = snapshot.getValue(OwnerPG.class);
                    holder.binding.userProductListName.setText(pg.getName());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            FirebaseDatabase.getInstance().getReference("Mess").child(item.getPGMessId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mess = snapshot.getValue(OwnerMess.class);
                    holder.binding.userProductListName.setText(mess.getName());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        if(!item.getToDate().equals("na")){
            String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            try {
                Date currDate = sdf.parse(date);
                Date toDate = sdf.parse(item.getToDate());
                Date fromDate = sdf.parse(item.getFromDate());
                long diffInMillies = Math.abs(toDate.getTime() - currDate.getTime());
                long remainingDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                diffInMillies = Math.abs(toDate.getTime() - fromDate.getTime());
                long totalDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                if(remainingDays < 0){
                    remainingDays = 0;
                    holder.binding.userProductListStatus.setText("Expired");
                    holder.binding.userProductListStatus.setTextColor(Color.RED);
                } else{
                    holder.binding.userProductListStatus.setText("Active");
                    holder.binding.userProductListStatus.setTextColor(context.getColor(R.color.primary));
                }
                holder.binding.userProductListProgressBar.setMax((int)totalDays);
                holder.binding.userProductListProgressBar.setProgress((int)remainingDays);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        } else {
            holder.binding.userProductListStatus.setText("Not Started Yet");
            holder.binding.userProductListStatus.setTextColor(Color.RED);
            holder.binding.userProductListProgressBar.setMax(10);
            holder.binding.userProductListProgressBar.setProgress(0);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UserSubscriptionActivity.class);
                intent.putExtra("type", item.getType());
                intent.putExtra("id", item.getSubscriptionId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        UserProductsListItemBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = UserProductsListItemBinding.bind(itemView);
        }
    }

}
