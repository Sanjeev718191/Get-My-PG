package com.androidaxe.getmypg.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.androidaxe.getmypg.Utils.Constants;
import com.androidaxe.getmypg.databinding.ActivitySelectUserBinding;

public class SelectUserActivity extends AppCompatActivity {

    ActivitySelectUserBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.customerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SelectUserActivity.this, UserLoginActivity.class));
            }
        });

        binding.selectCustomerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SelectUserActivity.this, UserLoginActivity.class));
            }
        });

        binding.ownerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SelectUserActivity.this, OwnerLoginActivity.class));
            }
        });

        binding.selectOwnerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SelectUserActivity.this, OwnerLoginActivity.class));
            }
        });

        binding.privacyPolicyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(Constants.privacyPolicy));
                startActivity(intent);
            }
        });

//        binding.selectAdminText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(SelectUserActivity.this, AdminLoginActivity.class));
//            }
//        });
    }
}