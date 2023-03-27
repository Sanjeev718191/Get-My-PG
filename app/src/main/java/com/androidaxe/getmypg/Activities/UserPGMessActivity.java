package com.androidaxe.getmypg.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.androidaxe.getmypg.Module.OwnerMess;
import com.androidaxe.getmypg.Module.OwnerPG;
import com.androidaxe.getmypg.Module.UserSubscribedItem;
import com.androidaxe.getmypg.R;
import com.androidaxe.getmypg.databinding.ActivityOwnerPgmessBinding;
import com.androidaxe.getmypg.databinding.ActivityUserPgmessBinding;
import com.google.firebase.database.FirebaseDatabase;

public class UserPGMessActivity extends AppCompatActivity {

    ActivityUserPgmessBinding binding;
    String type, id;
    UserSubscribedItem item;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserPgmessBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        type = getIntent().getStringExtra("type");
        id = getIntent().getStringExtra("id");
        database = FirebaseDatabase.getInstance();

        if(type.equals("pg")){
            database.getReference("UserSubscription").child("UserPG").child("");
        } else {

        }

    }
}