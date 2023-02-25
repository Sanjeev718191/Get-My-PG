package com.androidaxe.getmypg.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Checking Info...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            FirebaseDatabase.getInstance().getReference("PGUser").child(auth.getCurrentUser().getUid()).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.getValue(String.class) != null){
                        progressDialog.dismiss();
                        Intent intent = new Intent(WelcomeActivity.this, UserMainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        FirebaseDatabase.getInstance().getReference("PGOwner").child(auth.getCurrentUser().getUid()).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.getValue(String.class) != null){
                                    progressDialog.dismiss();
                                    Intent intent = new Intent(WelcomeActivity.this, OwnerMainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    progressDialog.dismiss();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
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