package com.androidaxe.getmypg.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidaxe.getmypg.Activities.OwnerPGMessActivity;
import com.androidaxe.getmypg.Module.OwnerPG;
import com.androidaxe.getmypg.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PGBusinessItemAdapter extends RecyclerView.Adapter<PGBusinessItemAdapter.MyViewHolder> {

    private Context context;
    private List<OwnerPG> pgList;

    public PGBusinessItemAdapter() {
    }

    public PGBusinessItemAdapter(Context context) {
        this.context = context;
        pgList = new ArrayList<>();
    }

    public void add(OwnerPG pg){
        pgList.add(pg);
        notifyDataSetChanged();
    }

    public void clear(){
        pgList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.owner_business_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        OwnerPG pg = pgList.get(position);
        holder.businessName.setText(pg.getName());
        holder.netRevenue.setText("Net Revenue : Rs. "+pg.getRevenue());
        holder.totalCustomers.setText("Total Customers : "+pg.getTotalUsers());
        holder.progressBar.setMax(Integer.parseInt(pg.getTotalUsers()));
        holder.progressBar.setProgress(Integer.parseInt(pg.getPaidUsers()));
        holder.precentage.setText(pg.getPaidUsers());
        Picasso.get().load(pg.getImage()).into(holder.mainImage);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, OwnerPGMessActivity.class);
                intent.putExtra("type", "pg");
                intent.putExtra("id", pg.getId());
                intent.putExtra("name", pg.getName());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return pgList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView businessName, totalCustomers, netRevenue, precentage;
        private ProgressBar progressBar;
        private ImageView mainImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            businessName = itemView.findViewById(R.id.ob_recycler_heading);
            totalCustomers = itemView.findViewById(R.id.ob_recycler_customers_count);
            netRevenue = itemView.findViewById(R.id.ob_recycler_revenue);
            precentage = itemView.findViewById(R.id.ob_recycler_percentage);
            progressBar = itemView.findViewById(R.id.progressbar);
            mainImage = itemView.findViewById(R.id.ob_recycler_image);
        }
    }

}
