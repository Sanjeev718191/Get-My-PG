package com.androidaxe.getmypg.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidaxe.getmypg.Activities.UserPGMessActivity;
import com.androidaxe.getmypg.Module.OwnerMess;
import com.androidaxe.getmypg.Module.OwnerPG;
import com.androidaxe.getmypg.Module.UserSubscribedItem;
import com.androidaxe.getmypg.R;
import com.androidaxe.getmypg.databinding.UserSubscribedItemBinding;
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

public class UserSubscribedItemAdapter extends RecyclerView.Adapter<UserSubscribedItemAdapter.MyViewHolder> {

    Context context;
    ArrayList<UserSubscribedItem> items;
    OwnerMess mess;
    OwnerPG pg;

    public UserSubscribedItemAdapter(Context context) {
        this.context = context;
        items = new ArrayList<>();
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
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.user_subscribed_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        UserSubscribedItem item = items.get(position);
        if(item.getType().equals("pg")) {
            FirebaseDatabase.getInstance().getReference("PGs").child(item.getPGMessId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    pg = snapshot.getValue(OwnerPG.class);
                    holder.binding.name.setText(pg.getName());
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
                    holder.binding.name.setText(mess.getName());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        holder.binding.fromDateText.setText(item.getFromDate());
        holder.binding.toDateText.setText(item.getToDate());

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
                holder.binding.progressbar.setMax((int)totalDays);
                holder.binding.progressbar.setProgress((int)remainingDays);
                holder.binding.userRemainingDays.setText(remainingDays+"");
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UserPGMessActivity.class);
                intent.putExtra("type", item.getType());
                intent.putExtra("id", item.getPGMessId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        UserSubscribedItemBinding binding;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = UserSubscribedItemBinding.bind(itemView);
        }
    }

}
