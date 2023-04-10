package com.androidaxe.getmypg.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidaxe.getmypg.Activities.ProductMessDetailActivity;
import com.androidaxe.getmypg.Activities.ProductPGDetailActivity;
import com.androidaxe.getmypg.Module.OwnerMess;
import com.androidaxe.getmypg.Module.OwnerPG;
import com.androidaxe.getmypg.R;
import com.androidaxe.getmypg.databinding.UserProductItemBinding;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class UserProductAdapter extends RecyclerView.Adapter<UserProductAdapter.ProductViewHolder>{

    Context context;
    ArrayList<OwnerPG> pgs;
    ArrayList<OwnerMess> messes;
    boolean isPG = false;

    public UserProductAdapter(Context context, ArrayList<OwnerPG> pgs){
        this.context = context;
        this.pgs = pgs;
        isPG = true;
    }

    public UserProductAdapter(Context context, ArrayList<OwnerMess> messes, boolean isPG){
        this.context = context;
        this.messes = messes;
        this.isPG = isPG;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductViewHolder(LayoutInflater.from(context).inflate(R.layout.user_product_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {

        if(isPG){
            OwnerPG pg = pgs.get(position);
            if(pg.getStopRequests().equals("false")){
                holder.binding.userProductStatus.setTextColor(context.getColor(R.color.primary));
                holder.binding.userProductStatus.setText("Available");
            } else {
                holder.binding.userProductStatus.setTextColor(context.getColor(R.color.red));
                holder.binding.userProductStatus.setText("Unavailable");
            }
            if(pg.getImage() != null && !pg.getImage().equals("na"))
                Glide.with(context).load(pg.getImage()).into(holder.binding.userProductImage);
            holder.binding.userProductLabel.setText(pg.getName());
            holder.binding.userProductType.setText("Hostel/PG");
            holder.binding.userProductPrice.setText("Rs. "+pg.getSeater1()+" (Single seater)");
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ProductPGDetailActivity.class);
                    intent.putExtra("id", pg.getId());
                    context.startActivity(intent);
                }
            });
        } else {
            OwnerMess mess = messes.get(position);
            if(mess.getImage() != null && !mess.getImage().equals("na"))
                Glide.with(context).load(mess.getImage()).into(holder.binding.userProductImage);
            holder.binding.userProductLabel.setText(mess.getName());
            holder.binding.userProductType.setText("Mess");
            holder.binding.userProductPrice.setText("Rs. "+mess.getFeeMonthly()+" (Monthly)");
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ProductMessDetailActivity.class);
                    intent.putExtra("id", mess.getId());
                    context.startActivity(intent);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        if(isPG) return pgs.size();
        else return messes.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder{
        UserProductItemBinding binding;
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = UserProductItemBinding.bind(itemView);
        }
    }

}
