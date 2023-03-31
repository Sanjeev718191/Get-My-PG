package com.androidaxe.getmypg.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidaxe.getmypg.Activities.RoomInfoActivity;
import com.androidaxe.getmypg.R;
import com.androidaxe.getmypg.databinding.ItemRoomBinding;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolder>{

    Context context;
    int numberOfRooms;
    String pgId;

    public RoomAdapter(Context context, int numberOfRooms, String pgId) {
        this.context = context;
        this.numberOfRooms = numberOfRooms;
        this.pgId = pgId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_room, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int roomNum = position+1;
        holder.binding.roomNumber.setText(""+roomNum);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, RoomInfoActivity.class);
                intent.putExtra("roomNum", ""+roomNum);
                intent.putExtra("id", pgId);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return numberOfRooms;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ItemRoomBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemRoomBinding.bind(itemView);
        }
    }

}
