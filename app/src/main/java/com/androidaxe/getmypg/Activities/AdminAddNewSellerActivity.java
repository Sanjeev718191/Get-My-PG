package com.androidaxe.getmypg.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.androidaxe.getmypg.Module.Offers;
import com.androidaxe.getmypg.R;
import com.androidaxe.getmypg.databinding.ActivityAdminAddNewSellerBinding;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.util.HashMap;
import java.util.UUID;

public class AdminAddNewSellerActivity extends AppCompatActivity {

    ActivityAdminAddNewSellerBinding binding;
    final int SELECT_IMAGE = 200;
    Uri imageUri;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminAddNewSellerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Admin Add new Seller");

        binding.addNewSellerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.newSellerEmail.getText().toString().equals("")){
                    Toast.makeText(AdminAddNewSellerActivity.this, "Add Email Id", Toast.LENGTH_SHORT).show();
                } else {
                    String uniqueID = UUID.randomUUID().toString();
                    HashMap<String, Object> hm = new HashMap<>();
                    hm.put("email", binding.newSellerEmail.getText().toString());
                    hm.put("id", uniqueID);
                    FirebaseDatabase.getInstance().getReference().child("PGOwner").child("NewOwner").child(uniqueID).setValue(hm).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(AdminAddNewSellerActivity.this, binding.newSellerEmail.getText().toString()+" Is added", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        binding.addNewOfferImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_IMAGE);
            }
        });

        binding.addNewOfferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageUri != null){
                    uploadOffer();
                } else {
                    Toast.makeText(AdminAddNewSellerActivity.this, "Select Image", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.updateAboutUsActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminAddNewSellerActivity.this, AdminEditAboutUsActivity.class));
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if (requestCode == SELECT_IMAGE) {
                imageUri = data.getData();
                if (imageUri != null) {
                    binding.addNewOfferImage.setImageURI(imageUri);
                }
            }
        }
    }

    private void uploadOffer(){
        progressDialog.setTitle("Uploading image...");
        progressDialog.setCanceledOnTouchOutside(false);

        if(imageUri != null){
            progressDialog.show();
            String uniqueID = UUID.randomUUID().toString();
            final StorageReference fileRef = FirebaseStorage.getInstance().getReference().child("OfferImage").child(uniqueID+".jpg");

            StorageTask uploadTask = fileRef.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        Uri downloadUri = (Uri) task.getResult();
                        String myUrl = downloadUri.toString();

                        Offers offer = new Offers(myUrl, "na", "na");

                        FirebaseDatabase.getInstance().getReference("Offers").child(uniqueID).setValue(offer).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(AdminAddNewSellerActivity.this, "Offer Added SuccessFully", Toast.LENGTH_SHORT).show();
                            }
                        });

                        progressDialog.dismiss();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(AdminAddNewSellerActivity.this, "Error, Please try again", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            progressDialog.dismiss();
            Toast.makeText(this, "Image is not Selected", Toast.LENGTH_SHORT).show();
        }
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