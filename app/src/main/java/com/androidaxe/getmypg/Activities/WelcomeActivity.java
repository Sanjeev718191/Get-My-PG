package com.androidaxe.getmypg.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.androidaxe.getmypg.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        FirebaseAuth auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser() != null){
            final int[] user = {-1};
            FirebaseDatabase.getInstance().getReference().child("PGUser").child(auth.getUid()).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        user[0] = 1;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            FirebaseDatabase.getInstance().getReference().child("PGOwner").child(auth.getUid()).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        user[0] = 2;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            if(user[0] == -1) {
                auth.signOut();
                Intent intent = new Intent(WelcomeActivity.this, SelectUserActivity.class);
                startActivity(intent);
            } else if(user[0] == 1) {
                Intent intent = new Intent(WelcomeActivity.this, UserMainActivity.class);
                startActivity(intent);
            } else if(user[0] == 2) {
                Intent intent = new Intent(WelcomeActivity.this, OwnerMainActivity.class);
                startActivity(intent);
            }

        }
        findViewById(R.id.getStartButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WelcomeActivity.this, SelectUserActivity.class);
                startActivity(intent);
            }
        });
    }
}