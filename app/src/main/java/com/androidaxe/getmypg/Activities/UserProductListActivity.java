package com.androidaxe.getmypg.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.androidaxe.getmypg.Adapters.UserProductListItemAdapter;
import com.androidaxe.getmypg.Module.UserSubscribedItem;
import com.androidaxe.getmypg.R;
import com.androidaxe.getmypg.databinding.ActivityUserProductListBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayDeque;

public class UserProductListActivity extends AppCompatActivity {

    String type;
    UserProductListItemAdapter adapter;
    FirebaseAuth auth;
    ActivityUserProductListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserProductListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        type = getIntent().getStringExtra("productType");
        auth = FirebaseAuth.getInstance();
        binding.productList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserProductListItemAdapter(this);
        binding.productList.setAdapter(adapter);

        if(type.equals("pg")){
            getSupportActionBar().setTitle("My Subscribed PGs");
            adapter.clear();
            FirebaseDatabase.getInstance().getReference("UserSubscription").child("UserPG").child(auth.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snap) {
                    if(snap.getChildrenCount() > 0){
                        binding.productList.setVisibility(View.VISIBLE);
                        binding.textView.setVisibility(View.GONE);
                        for(DataSnapshot ds : snap.getChildren()){
                            String id = ds.getValue(String.class);
                            FirebaseDatabase.getInstance().getReference("Subscription").child(id).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    UserSubscribedItem item = snapshot.getValue(UserSubscribedItem.class);
                                    adapter.add(item);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }

                    } else {
                        binding.productList.setVisibility(View.GONE);
                        binding.textView.setVisibility(View.VISIBLE);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
        } else {
            getSupportActionBar().setTitle("My Subscribed Mess");
            adapter.clear();

            FirebaseDatabase.getInstance().getReference("UserSubscription").child("UserMess").child(auth.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snap) {
                    if(snap.getChildrenCount() > 0){
                        binding.productList.setVisibility(View.VISIBLE);
                        binding.textView.setVisibility(View.GONE);
                        for(DataSnapshot ds : snap.getChildren()){
                            String id = ds.getValue(String.class);
                            FirebaseDatabase.getInstance().getReference("Subscription").child(id).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    UserSubscribedItem item = snapshot.getValue(UserSubscribedItem.class);
                                    adapter.add(item);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }

                    } else {
                        binding.productList.setVisibility(View.GONE);
                        binding.textView.setVisibility(View.VISIBLE);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });

        }

    }
}