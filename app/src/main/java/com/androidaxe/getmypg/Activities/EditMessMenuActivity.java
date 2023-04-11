package com.androidaxe.getmypg.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.androidaxe.getmypg.R;
import com.androidaxe.getmypg.databinding.ActivityEditMessMenuBinding;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;

public class EditMessMenuActivity extends AppCompatActivity {

    ActivityEditMessMenuBinding binding;
    //String[] week = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};
    String imageSelected = "No", pdfSelected = "No";
    int SELECT_MENU = 1;
    final int SELECT_PDF = 2;
    Uri imageUri;
    Uri pdfData;
    String pdfName ;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    String MessID;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditMessMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setTitle("Add your mess menu");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        MessID = getIntent().getStringExtra("Mess Id");
        progressDialog = new ProgressDialog(this);


        binding.menuImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageSelected = "Yes";
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_MENU);
            }
        });

        binding.selectMessMenuPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pdfSelected = "Yes";
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                startActivityForResult(Intent.createChooser(intent ,"Select Menu PDF File"), SELECT_PDF);
            }
        });

        binding.AddMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //checkText();
                if(pdfSelected.equals("No"))
                    Toast.makeText(EditMessMenuActivity.this, "Please Select menu pdf", Toast.LENGTH_SHORT).show();
                else if(imageSelected.equals("No")){
                    Toast.makeText(EditMessMenuActivity.this, "Please Select menu Image", Toast.LENGTH_SHORT).show();
                }
                else {
                    uploadPDF();
                }
            }
        });

    }

    private void uploadPDF() {

        progressDialog.setTitle("Uploading PDF...");
        progressDialog.setCanceledOnTouchOutside(false);

        if(pdfData != null){
            progressDialog.show();
            final StorageReference fileRef = FirebaseStorage.getInstance().getReference().child("MessMenu").child("pdf").child(MessID+"pdf"+".pdf");
            fileRef.putFile(pdfData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isComplete());
                    Uri uri = uriTask.getResult();
                    String url = String.valueOf(uri);
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Mess");
                    HashMap<String, Object> pgUserMap = new HashMap<>();
                    pgUserMap.put("menuPDF",url);
                    ref.child(MessID).updateChildren(pgUserMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(EditMessMenuActivity.this, "PDF uploaded successfully", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            uploadImage();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditMessMenuActivity.this, "Unable to upload pdf", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.show();
                    Toast.makeText(EditMessMenuActivity.this, "Unable to upload pdf try again.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Please select menu pdf file", Toast.LENGTH_SHORT).show();
        }

    }

//    private void checkText(){
//        if(binding.MondayBreakfastEditText.getText().toString().equals("")){
//            binding.MondayBreakfastEditText.setError("Field can't be empty");
//        } else if(binding.MondayLunchEditText.getText().toString().equals("")){
//            binding.MondayLunchEditText.setError("Field can't be empty");
//        } else if(binding.MondayDinnerEditText.getText().toString().equals("")){
//            binding.MondayDinnerEditText.setError("Field can't be empty");
//        } else if(binding.TuesdayBreakfastEditText.getText().toString().equals("")){
//            binding.TuesdayBreakfastEditText.setError("Field can't be empty");
//        } else if(binding.TuesdayLunchEditText.getText().toString().equals("")){
//            binding.TuesdayLunchEditText.setError("Field can't be empty");
//        } else if(binding.TuesdayDinnerEditText.getText().toString().equals("")){
//            binding.TuesdayDinnerEditText.setError("Field can't be empty");
//        } else if(binding.WednesdayBreakfastEditText.getText().toString().equals("")){
//            binding.WednesdayBreakfastEditText.setError("Field can't be empty");
//        } else if(binding.WednesdayLunchEditText.getText().toString().equals("")){
//            binding.WednesdayLunchEditText.setError("Field can't be empty");
//        } else if(binding.WednesdayDinnerEditText.getText().toString().equals("")){
//            binding.WednesdayDinnerEditText.setError("Field can't be empty");
//        } else if(binding.ThursdayBreakfastEditText.getText().toString().equals("")){
//            binding.ThursdayBreakfastEditText.setError("Field can't be empty");
//        } else if(binding.ThursdayLunchEditText.getText().toString().equals("")){
//            binding.ThursdayLunchEditText.setError("Field can't be empty");
//        } else if(binding.ThursdayDinnerEditText.getText().toString().equals("")){
//            binding.ThursdayDinnerEditText.setError("Field can't be empty");
//        } else if(binding.FridayBreakfastEditText.getText().toString().equals("")){
//            binding.FridayBreakfastEditText.setError("Field can't be empty");
//        } else if(binding.FridayLunchEditText.getText().toString().equals("")){
//            binding.FridayLunchEditText.setError("Field can't be empty");
//        } else if(binding.FridayDinnerEditText.getText().toString().equals("")){
//            binding.FridayDinnerEditText.setError("Field can't be empty");
//        } else if(binding.SaturdayBreakfastEditText.getText().toString().equals("")){
//            binding.SaturdayBreakfastEditText.setError("Field can't be empty");
//        } else if(binding.SaturdayLunchEditText.getText().toString().equals("")){
//            binding.SaturdayLunchEditText.setError("Field can't be empty");
//        } else if(binding.SaturdayDinnerEditText.getText().toString().equals("")){
//            binding.SaturdayDinnerEditText.setError("Field can't be empty");
//        } else if(binding.SundayBreakfastEditText.getText().toString().equals("")){
//            binding.SundayBreakfastEditText.setError("Field can't be empty");
//        } else if(binding.SundayLunchEditText.getText().toString().equals("")){
//            binding.SundayLunchEditText.setError("Field can't be empty");
//        } else if(binding.SundayDinnerEditText.getText().toString().equals("")){
//            binding.SundayDinnerEditText.setError("Field can't be empty");
//        } else {
////            addData();
//            uploadImage();
//        }
//    }

//    private void addData(){
//        String MessID = getIntent().getStringExtra("Mess Id");
//
//        final ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.setTitle("Adding Info...");
//        progressDialog.setMessage("Please wait, while we are adding data");
//        progressDialog.setCanceledOnTouchOutside(false);
//        progressDialog.show();
//
//        String[] data = {binding.MondayBreakfastEditText.getText().toString(),
//                binding.MondayLunchEditText.getText().toString(),
//                binding.MondayDinnerEditText.getText().toString(),
//                binding.TuesdayBreakfastEditText.getText().toString(),
//                binding.TuesdayLunchEditText.getText().toString(),
//                binding.TuesdayDinnerEditText.getText().toString(),
//                binding.WednesdayBreakfastEditText.getText().toString(),
//                binding.WednesdayLunchEditText.getText().toString(),
//                binding.WednesdayDinnerEditText.getText().toString(),
//                binding.ThursdayBreakfastEditText.getText().toString(),
//                binding.ThursdayLunchEditText.getText().toString(),
//                binding.ThursdayDinnerEditText.getText().toString(),
//                binding.FridayBreakfastEditText.getText().toString(),
//                binding.FridayLunchEditText.getText().toString(),
//                binding.FridayDinnerEditText.getText().toString(),
//                binding.SaturdayBreakfastEditText.getText().toString(),
//                binding.SaturdayLunchEditText.getText().toString(),
//                binding.SaturdayDinnerEditText.getText().toString(),
//                binding.SundayBreakfastEditText.getText().toString(),
//                binding.SundayLunchEditText.getText().toString(),
//                binding.SundayDinnerEditText.getText().toString()};
//
//
//
//    }


    @SuppressLint("Range")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == SELECT_MENU){
                imageUri = data.getData();
                if(imageUri != null){
                    binding.menuImageView.setImageURI(imageUri);
                    binding.menuImageView.setBackgroundDrawable(getDrawable(R.drawable.input_background));
                }
            } else if (requestCode == SELECT_PDF){
                pdfData = data.getData();
                if(pdfData.toString().startsWith("content://")){
                    Cursor cursor = null;
                    try {
                        cursor = EditMessMenuActivity.this.getContentResolver().query(pdfData, null, null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            pdfName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else if (pdfData.toString().startsWith("file://")){
                    pdfName = new File(pdfData.toString()).getName();
                }
                binding.editMessMenuSelectPdfText.setTextColor(getColor(R.color.dark_blue));
                binding.selectMessMenuPdf.setBackgroundDrawable(getDrawable(R.drawable.input_background));
                binding.editMessMenuSelectPdfText.setText(pdfName);
            }
        }
    }

    private void uploadImage(){

        progressDialog.setTitle("Uploading image...");
        progressDialog.setCanceledOnTouchOutside(false);

        if(imageUri != null){
            progressDialog.show();
            final StorageReference fileRef = FirebaseStorage.getInstance().getReference().child("MessMenu").child("image").child(MessID+".jpg");

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

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Mess");
                        HashMap<String, Object> pgUserMap = new HashMap<>();
                        pgUserMap.put("menu",myUrl);
                        ref.child(MessID).updateChildren(pgUserMap);
                        startActivity(new Intent(EditMessMenuActivity.this, OwnerMainActivity.class));
                        finishAffinity();
                        progressDialog.dismiss();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(EditMessMenuActivity.this, "Error, Please try again", Toast.LENGTH_SHORT).show();
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