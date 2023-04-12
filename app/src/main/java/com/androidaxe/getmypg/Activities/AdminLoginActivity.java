package com.androidaxe.getmypg.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.androidaxe.getmypg.R;
import com.androidaxe.getmypg.databinding.ActivityAdminLoginBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminLoginActivity extends AppCompatActivity {

    ActivityAdminLoginBinding binding;
    FirebaseDatabase database ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Admin Panel Login");
        database = FirebaseDatabase.getInstance();

        binding.adminLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.adminEmail.getText().toString().equals("")){
                    Toast.makeText(AdminLoginActivity.this, "Please Enter email", Toast.LENGTH_SHORT).show();
                } else if (binding.adminPassword.getText().toString().equals("")){
                    Toast.makeText(AdminLoginActivity.this, "Please enter password", Toast.LENGTH_SHORT).show();
                } else {
                    checkDetailsAndLogin();
                }
            }
        });

    }

    private void checkDetailsAndLogin(){
        database.getReference("Admin").child("MainAdmin").child("email").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String email = snapshot.getValue(String.class);
                if(email.equals(binding.adminEmail.getText().toString())){
                    database.getReference("Admin").child("MainAdmin").child("password").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String pass = snapshot.getValue(String.class);
                            if(pass.equals(binding.adminPassword.getText().toString())){
                                binding.adminPassword.setText("");
                                binding.adminEmail.setText("");
                                startActivity(new Intent(AdminLoginActivity.this, AdminAddNewSellerActivity.class));
                            } else {
                                Toast.makeText(AdminLoginActivity.this, "You are not an Admin", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else {
                    Toast.makeText(AdminLoginActivity.this, "You are not an Admin", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}