package com.androidaxe.getmypg.Adapters;

import android.content.Context;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidaxe.getmypg.Module.OwnerMess;
import com.androidaxe.getmypg.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MessBusinessItemAdapter extends RecyclerView.Adapter<MessBusinessItemAdapter.MyViewHolder>{

    private Context context;
    private List<OwnerMess> messList;

    public MessBusinessItemAdapter(){}

    public MessBusinessItemAdapter(Context context) {
        this.context = context;
        this.messList = new ArrayList<>();
    }

    public void add(OwnerMess mess){
        messList.add(mess);
        notifyDataSetChanged();
    }

    public void clear(){
        messList.clear();
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
        OwnerMess mess = messList.get(position);
        holder.businessName.setText(mess.getName());
        holder.netRevenue.setText("Net Revenue : Rs. "+mess.getRevenue());
        holder.totalCustomers.setText("Total Customers : "+mess.getTotalUsers());
        int percentage = Integer.parseInt(mess.getTotalUsers()) == 0 ? 0: (Integer.parseInt(mess.getPaidUsers())/Integer.parseInt(mess.getTotalUsers()))*100;
        holder.precentage.setText(percentage+"%");
        holder.progressBar.setMax(100);
        holder.progressBar.setProgress(percentage);
        Picasso.get().load(mess.getImage()).into(holder.mainImage);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return messList.size();
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
