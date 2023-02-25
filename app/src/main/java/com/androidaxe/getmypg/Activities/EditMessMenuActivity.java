package com.androidaxe.getmypg.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.androidaxe.getmypg.R;
import com.androidaxe.getmypg.databinding.ActivityEditMessMenuBinding;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
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

public class EditMessMenuActivity extends AppCompatActivity {

    ActivityEditMessMenuBinding binding;
    //String[] week = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};
    String Clicked = "No";
    int SELECT_MENU = 1;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditMessMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.menuImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Clicked = "Yes";
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_MENU);
            }
        });

        binding.AddMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //checkText();
                if(Clicked.equals("Yes"))
                    uploadImage();
                else
                    Toast.makeText(EditMessMenuActivity.this, "Please Select an Image", Toast.LENGTH_SHORT).show();
            }
        });

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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == SELECT_MENU){
                imageUri = data.getData();
                if(imageUri != null){
                    binding.menuImageView.setImageURI(imageUri);
                }
            }
        }
    }

    private void uploadImage(){
        String MessID = getIntent().getStringExtra("Mess Id");
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Adding Info...");
        progressDialog.setMessage("Please wait, while we are adding data");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if(imageUri != null){
            final StorageReference fileRef = FirebaseStorage.getInstance().getReference().child("MessMenu").child(MessID+".jpg");

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
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(EditMessMenuActivity.this, "Error, Please try again", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, "Image is not Selected", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }

}