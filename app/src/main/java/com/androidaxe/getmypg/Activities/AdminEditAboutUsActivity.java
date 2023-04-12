package com.androidaxe.getmypg.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.androidaxe.getmypg.Module.AboutUs;
import com.androidaxe.getmypg.R;
import com.androidaxe.getmypg.databinding.ActivityAdminEditAboutUsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminEditAboutUsActivity extends AppCompatActivity {

    ActivityAdminEditAboutUsBinding binding;

    AboutUs aboutUs;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminEditAboutUsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Update about us");
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        FirebaseDatabase.getInstance().getReference("AboutUs").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                aboutUs = snapshot.getValue(AboutUs.class);
                binding.editDescription.setText(aboutUs.getDescription());
                binding.editHelpCustomer.setText(aboutUs.getHelpCustomer());
                binding.editHelpSeller.setText(aboutUs.getHelpSeller());
                binding.editSellerRegistration.setText(aboutUs.getNewSeller());
                binding.editContactUs.setText(aboutUs.getContactUs());
                binding.editAboutDeveloper.setText(aboutUs.getDeveloper());
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.updateAboutUsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.editDescription.toString().equals("")){
                    binding.editDescription.setError("Field can't empty");
                } else if(binding.editHelpCustomer.toString().equals("")){
                    binding.editHelpCustomer.setError("Field can't empty");
                } else if(binding.editHelpSeller.toString().equals("")){
                    binding.editHelpSeller.setError("Field can't empty");
                } else if(binding.editSellerRegistration.toString().equals("")){
                    binding.editSellerRegistration.setError("Field can't empty");
                } else if(binding.editContactUs.toString().equals("")){
                    binding.editContactUs.setError("Field can't empty");
                } else if(binding.editAboutDeveloper.toString().equals("")){
                    binding.editAboutDeveloper.setError("Field can't empty");
                } else {
                    updateAboutUs();
                }
            }
        });

    }

    private void updateAboutUs(){
        AboutUs updatedAbout = new AboutUs(
                binding.editContactUs.getText().toString(),
                binding.editDescription.getText().toString(),
                binding.editAboutDeveloper.getText().toString(),
                aboutUs.getGmail(),
                binding.editHelpCustomer.getText().toString(),
                binding.editHelpSeller.getText().toString(),
                aboutUs.getLinkedIn(),
                binding.editSellerRegistration.getText().toString());

        FirebaseDatabase.getInstance().getReference("AboutUs").setValue(updatedAbout).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(AdminEditAboutUsActivity.this, "About us updated", Toast.LENGTH_SHORT).show();
            }
        });

    }

}