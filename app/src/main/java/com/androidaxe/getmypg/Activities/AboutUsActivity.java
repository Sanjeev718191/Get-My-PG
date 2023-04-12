package com.androidaxe.getmypg.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.androidaxe.getmypg.Module.AboutUs;
import com.androidaxe.getmypg.R;
import com.androidaxe.getmypg.databinding.ActivityAboutUsBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AboutUsActivity extends AppCompatActivity {

    ActivityAboutUsBinding binding;

    AboutUs aboutUs;
    String isSeller;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutUsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("About Us");
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        isSeller = getIntent().getStringExtra("isSeller");

        binding.contactUpCard.setVisibility(View.GONE);

        FirebaseDatabase.getInstance().getReference("AboutUs").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                aboutUs = snapshot.getValue(AboutUs.class);
                binding.aboutUpDescription.setText(aboutUs.getDescription());
                binding.aboutUpHelpCustomer.setText(aboutUs.getHelpCustomer());
                binding.aboutUpHelpSeller.setText(aboutUs.getHelpSeller());
                binding.aboutUpSellerRegistration.setText(aboutUs.getNewSeller());
                binding.aboutUpContact.setText(aboutUs.getContactUs());
                binding.aboutDeveloper.setText(aboutUs.getDeveloper());
                binding.linkedInId.setText(aboutUs.getLinkedIn());
                binding.gmail.setText(aboutUs.getGmail());
                if(isSeller != null && isSeller.equals("Yes")){
                    binding.contactUpCard.setVisibility(View.VISIBLE);
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.linkedInId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String url = "http://www.example.com";
//                Intent i = new Intent(Intent.ACTION_VIEW);
//                i.setData(Uri.parse(url));
//                startActivity(i);
                String id = binding.linkedInId.getText().toString();
                if(id != null && !id.equals("")) {
                    String url = "https://www.linkedin.com/in/" + id;
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                }

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