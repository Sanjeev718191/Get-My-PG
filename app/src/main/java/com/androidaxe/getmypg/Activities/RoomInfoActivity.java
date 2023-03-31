package com.androidaxe.getmypg.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.androidaxe.getmypg.Adapters.MyCustomerAdapter;
import com.androidaxe.getmypg.Module.PGUser;
import com.androidaxe.getmypg.Module.UserSubscribedItem;
import com.androidaxe.getmypg.databinding.ActivityRoomInfoBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RoomInfoActivity extends AppCompatActivity {

    ActivityRoomInfoBinding binding;
    int roomNum;
    String roomId, pgId;
    FirebaseDatabase database;
    ProgressDialog progressDialog;
    ArrayList<UserSubscribedItem> subscribers;
    ArrayList<PGUser> users;
    MyCustomerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRoomInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        roomNum = Integer.parseInt(getIntent().getStringExtra("roomNum"));
        roomId = "Room"+roomNum;
        pgId = getIntent().getStringExtra("id");
        database = FirebaseDatabase.getInstance();
        binding.roomInfoUserList.setLayoutManager(new LinearLayoutManager(this));
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading Info...");
        progressDialog.setCanceledOnTouchOutside(false);

        getSupportActionBar().setTitle("Room - "+roomNum);

        progressDialog.show();
        database.getReference("PGRoom").child(pgId).child(roomId).child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getChildrenCount() > 0){
                    binding.roomInfoNoUser.setVisibility(View.GONE);
                    binding.roomInfoUserList.setVisibility(View.VISIBLE);
                    subscribers = new ArrayList<>();
                    users = new ArrayList<>();
                    for(DataSnapshot ds : snapshot.getChildren()){
                        String subscriptionId = ds.getKey();
                        database.getReference("Subscription").child(subscriptionId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                UserSubscribedItem item = snapshot.getValue(UserSubscribedItem.class);
                                subscribers.add(item);

                                FirebaseDatabase.getInstance().getReference("PGUser").child(item.getUid()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        PGUser user = snapshot.getValue(PGUser.class);
                                        users.add(user);
                                        adapter = new MyCustomerAdapter(RoomInfoActivity.this, subscribers, users);
                                        binding.roomInfoUserList.setAdapter(adapter);
                                        adapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                } else {
                    binding.roomInfoNoUser.setVisibility(View.VISIBLE);
                    binding.roomInfoUserList.setVisibility(View.GONE);
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(RoomInfoActivity.this, "Data not found", Toast.LENGTH_SHORT).show();
                binding.roomInfoNoUser.setVisibility(View.VISIBLE);
                binding.roomInfoUserList.setVisibility(View.GONE);
            }
        });

    }
}