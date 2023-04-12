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

import com.androidaxe.getmypg.Module.AdditionalImages;
import com.androidaxe.getmypg.R;
import com.androidaxe.getmypg.databinding.ActivityAddAdditionalImagesBinding;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.util.HashMap;

public class AddAdditionalImagesActivity extends AppCompatActivity {

    ActivityAddAdditionalImagesBinding binding;
    String name, type, id, isNew;
    ProgressDialog progressDialog;
    FirebaseDatabase database;
    AdditionalImages images;
    final int SELECT_IMG1 = 1, SELECT_IMG2 = 2, SELECT_IMG3 = 3, SELECT_IMG4 = 4;
    Uri img1Uri, img2Uri, img3Uri, img4Uri;
    int imageCount = 0, uploadedImageCount = 0;
    boolean img1Clicked = false, img2Clicked = false, img3Clicked = false, img4Clicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddAdditionalImagesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Getting Data");
        progressDialog.setCanceledOnTouchOutside(false);

        name = getIntent().getStringExtra("name");
        type = getIntent().getStringExtra("type");
        id = getIntent().getStringExtra("id");
        isNew = getIntent().getStringExtra("isNew");
        database = FirebaseDatabase.getInstance();
        getSupportActionBar().setTitle(name);

        if(isNew.equals("No")){
            progressDialog.show();
            database.getReference("BusinessAdditionalImages").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.getChildrenCount() > 0){
                        images = snapshot.getValue(AdditionalImages.class);
                        if(images.getImg1() != null){
                            Glide.with(AddAdditionalImagesActivity.this).load(images.getImg1()).into(binding.img1);
                        }
                        if(images.getImg2() != null){
                            Glide.with(AddAdditionalImagesActivity.this).load(images.getImg2()).into(binding.img2);
                        }
                        if(images.getImg3() != null){
                            Glide.with(AddAdditionalImagesActivity.this).load(images.getImg3()).into(binding.img3);
                        }
                        if(images.getImg4() != null){
                            Glide.with(AddAdditionalImagesActivity.this).load(images.getImg4()).into(binding.img4);
                        }
                        progressDialog.dismiss();
                    } else {
                        progressDialog.dismiss();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressDialog.dismiss();
                    Toast.makeText(AddAdditionalImagesActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        setOnClicks();

        binding.uploadAdditionalImagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!img1Clicked && !img2Clicked && !img3Clicked && !img4Clicked){
                    Toast.makeText(AddAdditionalImagesActivity.this, "Please select images.", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.setTitle("Uploading image...");
                    progressDialog.setCanceledOnTouchOutside(false);
                    if(img1Uri != null){
                        imageCount++;
                        uploadImages(1);
                        progressDialog.show();
                    }
                    if(img2Uri != null){
                        imageCount++;
                        uploadImages(2);
                        progressDialog.show();
                    }
                    if(img3Uri != null){
                        imageCount++;
                        uploadImages(3);
                        progressDialog.show();
                    }
                    if(img4Uri != null){
                        imageCount++;
                        uploadImages(4);
                        progressDialog.show();
                    }
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

    private void setOnClicks(){
        binding.img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                img1Clicked = true;
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_IMG1);
            }
        });

        binding.img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                img2Clicked = true;
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_IMG2);
            }
        });

        binding.img3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                img3Clicked = true;
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_IMG3);
            }
        });

        binding.img4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                img4Clicked = true;
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_IMG4);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == SELECT_IMG1){
                img1Uri = data.getData();
                if(img1Uri != null){
                    binding.img1.setImageURI(img1Uri);
                    binding.img1.setBackgroundDrawable(getDrawable(R.drawable.input_background));
                }
            }
            if(requestCode == SELECT_IMG2){
                img2Uri = data.getData();
                if(img2Uri != null){
                    binding.img2.setImageURI(img2Uri);
                    binding.img2.setBackgroundDrawable(getDrawable(R.drawable.input_background));
                }
            }
            if(requestCode == SELECT_IMG3){
                img3Uri = data.getData();
                if(img3Uri != null){
                    binding.img3.setImageURI(img3Uri);
                    binding.img3.setBackgroundDrawable(getDrawable(R.drawable.input_background));
                }
            }
            if(requestCode == SELECT_IMG4){
                img4Uri = data.getData();
                if(img4Uri != null){
                    binding.img4.setImageURI(img4Uri);
                    binding.img4.setBackgroundDrawable(getDrawable(R.drawable.input_background));
                }
            }
        }
    }

    private void uploadImages(int index){
        StorageReference fileRef;
        StorageTask uploadTask;
        if(type.equals("pg")) fileRef = FirebaseStorage.getInstance().getReference().child("PGs").child(id+"img"+index+".jpg");
        else fileRef = FirebaseStorage.getInstance().getReference().child("Mess").child(id+"img"+index+".jpg");

        if(index == 1){
            uploadTask = fileRef.putFile(img1Uri);
        } else if (index == 2) {
            uploadTask = fileRef.putFile(img2Uri);
        } else if (index == 3) {
            uploadTask = fileRef.putFile(img3Uri);
        } else {
            uploadTask = fileRef.putFile(img4Uri);
        }

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

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("BusinessAdditionalImages");
                    HashMap<String, Object> pgUserMap = new HashMap<>();
                    if(index == 1){
                        pgUserMap.put("img1",myUrl);
                    } else if (index == 2) {
                        pgUserMap.put("img2",myUrl);
                    } else if (index == 3) {
                        pgUserMap.put("img3",myUrl);
                    } else {
                        pgUserMap.put("img4",myUrl);
                    }
                    uploadedImageCount++;
                    ref.child(id).updateChildren(pgUserMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(AddAdditionalImagesActivity.this, "Image "+index+" Added", Toast.LENGTH_SHORT).show();
                            if(uploadedImageCount == imageCount){
                                if(isNew.equals("No") || type.equals("pg")){
                                    finish();
                                } else {
                                    Intent intent = new Intent(AddAdditionalImagesActivity.this, EditMessMenuActivity.class);
                                    intent.putExtra("Mess Id", ""+id);
                                    startActivity(intent);
                                    finish();
                                }
                                progressDialog.dismiss();
                            }
                        }
                    });
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(AddAdditionalImagesActivity.this, "Error unable to upload image "+index, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}