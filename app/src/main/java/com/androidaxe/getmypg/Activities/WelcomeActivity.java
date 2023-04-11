package com.androidaxe.getmypg.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.androidaxe.getmypg.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WelcomeActivity extends AppCompatActivity {

    Button startButton;
    ImageButton refreshButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        FirebaseAuth auth = FirebaseAuth.getInstance();

        startButton = findViewById(R.id.getStartButton);
        startButton.setVisibility(View.GONE);
        refreshButton = findViewById(R.id.welcome_refresh);
        refreshButton.setVisibility(View.GONE);

        if (haveNetworkConnection()) {

            startButton.setVisibility(View.VISIBLE);
            refreshButton.setVisibility(View.GONE);
            startButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(WelcomeActivity.this, SelectUserActivity.class);
                    startActivity(intent);
                }
            });

            if (auth.getCurrentUser() != null) {
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Checking Info...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                FirebaseDatabase.getInstance().getReference("PGUser").child(auth.getCurrentUser().getUid()).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue(String.class) != null) {
                            progressDialog.dismiss();
                            Intent intent = new Intent(WelcomeActivity.this, UserMainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            FirebaseDatabase.getInstance().getReference("PGOwner").child(auth.getCurrentUser().getUid()).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getValue(String.class) != null) {
                                        progressDialog.dismiss();
                                        Intent intent = new Intent(WelcomeActivity.this, OwnerMainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        progressDialog.dismiss();
                                        startButton.setVisibility(View.VISIBLE);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    progressDialog.dismiss();
                                    Toast.makeText(WelcomeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();
                        Toast.makeText(WelcomeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            Toast.makeText(this, "Network connection not available!", Toast.LENGTH_SHORT).show();
            refreshButton.setVisibility(View.VISIBLE);
            startButton.setVisibility(View.GONE);
        }

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WelcomeActivity.this, WelcomeActivity.class));
                finish();
            }
        });

    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

}