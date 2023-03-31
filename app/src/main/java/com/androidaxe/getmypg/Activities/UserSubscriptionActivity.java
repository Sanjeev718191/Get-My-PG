package com.androidaxe.getmypg.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;

import com.androidaxe.getmypg.Module.OwnerMess;
import com.androidaxe.getmypg.Module.OwnerPG;
import com.androidaxe.getmypg.Module.UserSubscribedItem;
import com.androidaxe.getmypg.databinding.ActivityUserSubscriptionBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserSubscriptionActivity extends AppCompatActivity {

    ActivityUserSubscriptionBinding binding;
    FirebaseDatabase database;
    UserSubscribedItem subscription;
    OwnerPG pg;
    OwnerMess mess;
    String id, type;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserSubscriptionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading Info...");
        progressDialog.setCanceledOnTouchOutside(false);
        id = getIntent().getStringExtra("id");
        type = getIntent().getStringExtra("type");
        database = FirebaseDatabase.getInstance();

        progressDialog.show();
        database.getReference("Subscription").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                subscription = snapshot.getValue(UserSubscribedItem.class);
                if(type.equals("pg")){
                    database.getReference("PGs").child(subscription.getPGMessId()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            pg = snapshot.getValue(OwnerPG.class);
                            setUserData();
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            progressDialog.dismiss();
                            Toast.makeText(UserSubscriptionActivity.this, "Data Not found", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    database.getReference("Mess").child(subscription.getPGMessId()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            mess = snapshot.getValue(OwnerMess.class);
                            setUserData();
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            progressDialog.dismiss();
                            Toast.makeText(UserSubscriptionActivity.this, "Data Not found", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(UserSubscriptionActivity.this, "Data Not found", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setUserData(){
        if(type.equals("pg")){
            getSupportActionBar().setTitle(pg.getName());
        } else {
            getSupportActionBar().setTitle(mess.getName());
        }
    }
}