package com.androidaxe.getmypg.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.androidaxe.getmypg.Module.OwnerPG;
import com.androidaxe.getmypg.R;
import com.androidaxe.getmypg.databinding.ActivityProductPgdetailBinding;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProductPGDetailActivity extends AppCompatActivity {

    ActivityProductPgdetailBinding binding;
    String pgid;
    OwnerPG pg;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductPgdetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        pgid = getIntent().getStringExtra("id");

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