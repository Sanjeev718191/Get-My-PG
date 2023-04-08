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

import com.androidaxe.getmypg.Module.PGOwner;
import com.androidaxe.getmypg.Module.PGUser;
import com.androidaxe.getmypg.R;
import com.androidaxe.getmypg.databinding.ActivityOwnerSetProfileBinding;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.util.HashMap;
import java.util.regex.Pattern;

public class OwnerSetProfileActivity extends AppCompatActivity {

    ActivityOwnerSetProfileBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    PGOwner pgOwner;
    int SELECT_PICTURE = 100;
    String clicked = "", myUrl = "";
    Uri imageUri;
    StorageReference storageReference;
    private StorageTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOwnerSetProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Profile");

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference().child("PGOwner profile picture");

        database.getReference().child("PGOwner")
                .child(currentUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        pgOwner = snapshot.getValue(PGOwner.class);
                        Glide.with(OwnerSetProfileActivity.this)
                                .load(pgOwner.getProfile())
                                .into(binding.resetOwnerImage);

                        binding.editOwnerName.setText(pgOwner.getName());
                        if(!pgOwner.getContact().equals("+91 XXXXXXXXXX"))
                            binding.editOwnerContact.setText(pgOwner.getContact());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        binding.resetOwnerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked = "Yes";
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
            }
        });

        binding.SetOwnerProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(binding.editOwnerName.getText().equals("")) {
                    binding.editOwnerName.setError("Please Enter Name");
                } else if(binding.editOwnerContact.getText().equals("") && !Pattern.matches("if(binding.editUserName.getText().equals(\"\"))", binding.editOwnerContact.getText()) ){
                    binding.editOwnerName.setError("Please enter valid Number");
                } else{
                    setProfileToDatabase();
                }
            }
        });

    }

    private void setProfileToDatabase() {

        if(clicked.equals("Yes")){
            uploadInfo();
        } else {
            uploadInfoWithoutImage();
        }

    }

    private void uploadInfoWithoutImage() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Update Profile");
        progressDialog.setMessage("Please wait, while we are updating your account information");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("PGOwner");

        HashMap<String, Object> pgUserMap = new HashMap<>();
        pgUserMap.put("name",binding.editOwnerName.getText().toString());
        pgUserMap.put("contact", binding.editOwnerContact.getText().toString());
        ref.child(pgOwner.getuId()).updateChildren(pgUserMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        startActivity(new Intent(OwnerSetProfileActivity.this, OwnerMainActivity.class));
                        Toast.makeText(OwnerSetProfileActivity.this, "Profile Info update successfully", Toast.LENGTH_SHORT).show();
                        finishAffinity();
                    }
                });

    }

    private void uploadInfo() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Update Profile");
        progressDialog.setMessage("Please wait, while we are updating your account information");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if(imageUri != null){
            final StorageReference fileRef = storageReference.child(pgOwner.getuId()+".jpg");

            uploadTask = fileRef.putFile(imageUri);
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
                        myUrl = downloadUri.toString();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("PGOwner");

                        HashMap<String, Object> pgUserMap = new HashMap<>();
                        pgUserMap.put("name",binding.editOwnerName.getText().toString());
                        pgUserMap.put("contact", binding.editOwnerContact.getText().toString());
                        pgUserMap.put("profile", myUrl);
                        ref.child(pgOwner.getuId()).updateChildren(pgUserMap);

                        progressDialog.dismiss();
                        startActivity(new Intent(OwnerSetProfileActivity.this, OwnerMainActivity.class));
                        Toast.makeText(OwnerSetProfileActivity.this, "Profile Info update successfully", Toast.LENGTH_SHORT).show();
                        finishAffinity();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(OwnerSetProfileActivity.this, "Error, Please try again", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, "Image is not selected", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                // Get the uri of the image from data
                imageUri = data.getData();
                if (null != imageUri) {
                    // update the preview image in the layout
                    binding.resetOwnerImage.setImageURI(imageUri);
                }
            }
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