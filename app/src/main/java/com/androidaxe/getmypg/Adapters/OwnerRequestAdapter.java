package com.androidaxe.getmypg.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidaxe.getmypg.Activities.OwnerAcceptRequestActivity;
import com.androidaxe.getmypg.Module.Request;
import com.androidaxe.getmypg.R;
import com.androidaxe.getmypg.databinding.RequestItemOwnerBinding;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class OwnerRequestAdapter extends RecyclerView.Adapter<OwnerRequestAdapter.RequestViewHolder> {

    Context context;
    ArrayList<Request> requests;

    public OwnerRequestAdapter(Context context, ArrayList<Request> requests) {
        this.context = context;
        this.requests = requests;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RequestViewHolder(LayoutInflater.from(context).inflate(R.layout.request_item_owner, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {

        Request request = requests.get(position);
        Glide.with(context).load(request.getUserImage()).into(holder.binding.requestItemOwnerUserImage);
        holder.binding.requestItemOwnerUserName.setText(request.getUserName());
        holder.binding.requestItemOwnerUserContact.setText(request.getUserContact());
        if(request.getType().equals("mess")){
            holder.binding.requestItemOwnerUserRequestType.setText("Request For : Mess");
            holder.binding.requestItemOwnerBusinessName.setText("In Mess : "+request.getBusinessName());
        } else if(request.getRoomType().equals("1")){
            holder.binding.requestItemOwnerUserRequestType.setText("Request For : Single seater Room");
            holder.binding.requestItemOwnerBusinessName.setText("In Hostel : "+request.getBusinessName());
        } else if(request.getRoomType().equals("2")){
            holder.binding.requestItemOwnerUserRequestType.setText("Request For : Double seater Room");
            holder.binding.requestItemOwnerBusinessName.setText("In Hostel : "+request.getBusinessName());
        } else if(request.getRoomType().equals("3")){
            holder.binding.requestItemOwnerUserRequestType.setText("Request For : Triple seater Room");
            holder.binding.requestItemOwnerBusinessName.setText("In Hostel : "+request.getBusinessName());
        }
        holder.binding.requestItemOwnerPrice.setText("Price (pre month) : Rs. "+request.getPrice());
        holder.binding.requestItemOwnerDate.setText("Date : "+request.getDate());
        holder.binding.requestItemOwnerStatus.setText("Status : "+request.getStatus());
        if(request.getStatus().equals("Accepted")){
            holder.binding.requestItemOwnerAcceptButton.setVisibility(View.GONE);
            holder.binding.requestItemOwnerRejectButton.setVisibility(View.GONE);
            holder.binding.requestItemOwnerStatus.setTextColor(context.getColor(R.color.primary));
        } else if(request.getStatus().equals("Rejected")){
            holder.binding.requestItemOwnerAcceptButton.setVisibility(View.GONE);
            holder.binding.requestItemOwnerRejectButton.setVisibility(View.GONE);
            holder.binding.requestItemOwnerStatus.setTextColor(context.getColor(R.color.red));
        }

        holder.binding.requestItemOwnerAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, OwnerAcceptRequestActivity.class);
                intent.putExtra("type",request.getType());
                intent.putExtra("id",request.getRequestId());
                context.startActivity(intent);
            }
        });

        holder.binding.requestItemOwnerRejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(request.getType().equals("pg")) {
                    FirebaseDatabase.getInstance().getReference("Requests").child("PGRequests").child(request.getRequestId()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            snapshot.getRef().child("status").setValue("Rejected").addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    holder.binding.requestItemOwnerAcceptButton.setVisibility(View.GONE);
                                    holder.binding.requestItemOwnerRejectButton.setVisibility(View.GONE);
                                    holder.binding.requestItemOwnerStatus.setTextColor(context.getColor(R.color.red));
                                    holder.binding.requestItemOwnerStatus.setText("Rejected");
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else if(request.getType().equals("mess")) {
                    FirebaseDatabase.getInstance().getReference("Requests").child("MessRequests").child(request.getRequestId()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            snapshot.getRef().child("status").setValue("Rejected").addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    holder.binding.requestItemOwnerAcceptButton.setVisibility(View.GONE);
                                    holder.binding.requestItemOwnerRejectButton.setVisibility(View.GONE);
                                    holder.binding.requestItemOwnerStatus.setTextColor(context.getColor(R.color.red));
                                    holder.binding.requestItemOwnerStatus.setText("Rejected");
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    public class RequestViewHolder extends RecyclerView.ViewHolder {
        RequestItemOwnerBinding binding;
        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RequestItemOwnerBinding.bind(itemView);
        }
    }

}
