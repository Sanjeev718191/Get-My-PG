package com.androidaxe.getmypg.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.androidaxe.getmypg.Module.OwnerPG;
import com.androidaxe.getmypg.R;
import com.androidaxe.getmypg.databinding.ActivityProductPgdetailBinding;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProductPGDetailActivity extends AppCompatActivity {

    ActivityProductPgdetailBinding binding;
    String pgid;
    OwnerPG pg;
    FirebaseDatabase database;
    FirebaseAuth auth;
    boolean flag;
    int count;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductPgdetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading Info...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        database = FirebaseDatabase.getInstance();
        pgid = getIntent().getStringExtra("id");
        auth = FirebaseAuth.getInstance();

        database.getReference("PGs").child(pgid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pg = snapshot.getValue(OwnerPG.class);
                Glide.with(ProductPGDetailActivity.this).load(pg.getImage()).into(binding.productPgImage);
                binding.productPgTitle.setText(pg.getName());
                binding.productPgDescription.setText(pg.getDescription());
                binding.productPgSingleSeaterPrice.setText("Rs. "+pg.getSeater1());
                binding.productPgDoubleSeaterPrice.setText("Rs. "+pg.getSeater2());
                binding.productPgTripleSeaterPrice.setText("Rs. "+pg.getSeater3());
                binding.productPgElectricity.setText(pg.getElectricityBill());
                binding.productPgLocation.setText(pg.getLocality()+", "+pg.getCity()+", "+pg.getState()+" "+pg.getPin());
                binding.productPgOwnerContact.setText(pg.getContact());
                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.productPgImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductPGDetailActivity.this, ImageZoomViewActivity.class);
                intent.putExtra("name", pg.getName());
                intent.putExtra("link", pg.getImage());
                startActivity(intent);
            }
        });

        flag = false;
        database.getReference("UserSubscription").child("UserPG").child(auth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getChildrenCount() > 0){
                    count = 0;
                    for(DataSnapshot ds : snapshot.getChildren()){
                        String id = ds.getValue(String.class);
                        database.getReference("Subscription").child(id).child("PGMessId").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String checkId = snapshot.getValue(String.class);
                                if(checkId.equals(pgid)){
                                    deactivateButton();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        count++;
                        if(flag || count == snapshot.getChildrenCount()){
                            if(!flag){
                                activateButton();
                            }
                            break;
                        }
                    }
                } else {
                    activateButton();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void deactivateButton(){
        flag = true;
        binding.productPgSubscribe.setBackgroundDrawable(getDrawable(R.drawable.button_deactive_background));
        binding.productPgSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ProductPGDetailActivity.this, "You are already in.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void activateButton(){
        binding.productPgSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductPGDetailActivity.this, CheckOutActivity.class);
                intent.putExtra("id", pgid);
                intent.putExtra("type", "pg");
                intent.putExtra("new", "yes");
                startActivity(intent);
            }
        });
    }

}