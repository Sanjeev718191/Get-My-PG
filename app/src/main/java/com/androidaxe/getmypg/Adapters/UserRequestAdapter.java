package com.androidaxe.getmypg.Adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidaxe.getmypg.Module.Request;
import com.androidaxe.getmypg.R;
import com.androidaxe.getmypg.databinding.RequestItemUserBinding;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class UserRequestAdapter extends RecyclerView.Adapter<UserRequestAdapter.RequestViewHolder> {

    Context context;
    ArrayList<Request> requests;

    public UserRequestAdapter() { }

    public UserRequestAdapter(Context context, ArrayList<Request> requests) {
        this.context = context;
        this.requests = requests;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RequestViewHolder(LayoutInflater.from(context).inflate(R.layout.request_item_user, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        Request request = requests.get(position);
        Glide.with(context).load(request.getBusinessImage()).into(holder.binding.requestItemUserBusinessImage);
        holder.binding.requestItemUserBusinessName.setText(request.getBusinessName());
        holder.binding.requestItemUserOwnerContact.setText("Contact : "+request.getOwnerContact());
        if(request.getRoomType().equals("na"))
            holder.binding.requestItemUserRequestType.setText("Request for : Mess");
        else if(request.getRoomType().equals("1"))
            holder.binding.requestItemUserRequestType.setText("Request for : Single Seater Room");
        else if(request.getRoomType().equals("2"))
            holder.binding.requestItemUserRequestType.setText("Request for : Double Seater Room");
        else if(request.getRoomType().equals("3"))
            holder.binding.requestItemUserRequestType.setText("Request for : Triple Seater Room");
        holder.binding.requestItemUserPrice.setText("Price (pre month) : Rs. "+request.getPrice());
        holder.binding.requestItemUserDate.setText("Date : "+request.getDate());
        holder.binding.requestItemUserStatus.setText("Status : "+request.getStatus());
        if(request.getStatus().equals("Accepted")){
            holder.binding.requestItemUserStatus.setTextColor(context.getColor(R.color.primary));
            if(request.getType().equals("mess"))
                holder.binding.requestItemUserStatus.setText("Status : "+request.getStatus()+ " check your Mess list for more details");
            else
                holder.binding.requestItemUserStatus.setText("Status : "+request.getStatus()+ " check your Hostel/PG list for more details");
        } else if(request.getStatus().equals("Rejected")){
            holder.binding.requestItemUserStatus.setTextColor(context.getColor(R.color.red));
        }

    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    public class RequestViewHolder extends RecyclerView.ViewHolder{
        RequestItemUserBinding binding;
        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RequestItemUserBinding.bind(itemView);
        }
    }

}
