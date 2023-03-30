package com.androidaxe.getmypg.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.androidaxe.getmypg.Module.OwnerMess;
import com.androidaxe.getmypg.Module.OwnerPG;
import com.androidaxe.getmypg.Module.PGOwner;
import com.androidaxe.getmypg.databinding.ActivityAddNewBussinessBinding;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.net.URL;
import java.util.HashMap;

public class AddNewBussinessActivity extends AppCompatActivity {

    ActivityAddNewBussinessBinding binding;
    String[] ElectricityBillItems = {"Included in monthly fee", "Meter reading"};
    ArrayAdapter<String> businessTypeAdapter;
    ArrayAdapter<String> eleBillAdapter;
    final int SELECT_HOSTEL = 1, SELECT_MESS = 2;
    String Clicked = "No";
    Uri imageUri;
    FirebaseDatabase database;
    FirebaseAuth auth;
    FirebaseStorage storage;
    String selectedBill;

    PGOwner owner;
    String type, id;
    OwnerPG pg;
    OwnerMess mess;

    String newId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddNewBussinessBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        type = getIntent().getStringExtra("type");
        id = getIntent().getStringExtra("id");

        database.getReference().child("PGOwner").child(auth.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        owner = snapshot.getValue(PGOwner.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        showBusinessType();

    }

    private void showBusinessType(){
        if(type.equals("pg")){
            binding.PGCardView.setVisibility(View.VISIBLE);
            binding.MessCardView.setVisibility(View.GONE);
            showElectricityBill();

            if(!id.equals("new")){
                showMyPG();
            }

            binding.addPGImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Clicked = "PG";
                    Intent i = new Intent();
                    i.setType("image/*");
                    i.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_HOSTEL);
                }
            });
            binding.AddPGButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addMYPG();
                }
            });
        } else {
            binding.PGCardView.setVisibility(View.GONE);
            binding.MessCardView.setVisibility(View.VISIBLE);

            if(!id.equals("new")){
                showMyMess();
            }

            binding.addMessImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Clicked = "Mess";
                    Intent i = new Intent();
                    i.setType("image/*");
                    i.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_MESS);
                }
            });
            binding.AddMessButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addMyMess();
                }
            });
        }

    }
    private void showElectricityBill(){
        eleBillAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, ElectricityBillItems);
        binding.PGeleBillSpinner.setAdapter(eleBillAdapter);
        binding.PGeleBillSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                if(item.equals("Included in monthly fee")){
                    selectedBill = item;
                } else if(item.equals("Meter reading")){
                    selectedBill = item;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void showMyPG(){
        database.getReference("PGs").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pg = snapshot.getValue(OwnerPG.class);
                Glide.with(AddNewBussinessActivity.this).load(pg.getImage()).into(binding.addPGImage);
                binding.PGNameEditText.setText(pg.getName());
                binding.PGDescriptionEditText.setText(pg.getDescription());
                binding.PGAddressLocalityEditText.setText(pg.getLocality());
                binding.PGAddressCityEditText.setText(pg.getCity());
                binding.PGAddressStateEditText.setText(pg.getState());
                binding.PGAddressPinCodeEditText.setText(pg.getPin());
                binding.PG1seaterEditText.setText(pg.getSeater1());
                binding.PG2seaterEditText.setText(pg.getSeater2());
                binding.PG3seaterEditText.setText(pg.getSeater3());
                if(pg.getElectricityBill().equals("Meter reading")){
                    binding.PGeleBillSpinner.setSelection(1);
                } else {
                    binding.PGeleBillSpinner.setSelection(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showMyMess(){
        database.getReference("Mess").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mess = snapshot.getValue(OwnerMess.class);
                Glide.with(AddNewBussinessActivity.this).load(mess.getImage()).into(binding.addMessImage);
                binding.MessNameEditText.setText(mess.getName());
                binding.MessDescriptionEditText.setText(mess.getDescription());
                binding.MessAddressLocalityEditText.setText(mess.getLocality());
                binding.MessAddressCityEditText.setText(mess.getCity());
                binding.MessAddressStateEditText.setText(mess.getState());
                binding.MessAddressPinCodeEditText.setText(mess.getPin());
                binding.MessMonthlyEditText.setText(mess.getFeeMonthly());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_MESS) {
                imageUri = data.getData();
                if (imageUri != null) {
                    binding.addMessImage.setImageURI(imageUri);
                }
            } else if(requestCode == SELECT_HOSTEL){
                imageUri = data.getData();
                if (imageUri != null) {
                    binding.addPGImage.setImageURI(imageUri);
                }
            }
        }
    }

    private void uploadPGImage(){

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Adding Info...");
        progressDialog.setMessage("Please wait, while we are adding data");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if(imageUri != null){
            final StorageReference fileRef = storage.getReference().child("PGs").child(newId+".jpg");

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

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("PGs");

                        HashMap<String, Object> pgUserMap = new HashMap<>();
                        pgUserMap.put("name",binding.PGNameEditText.getText().toString());
                        pgUserMap.put("description",binding.PGDescriptionEditText.getText().toString());
                        pgUserMap.put("locality",binding.PGAddressLocalityEditText.getText().toString());
                        pgUserMap.put("city",binding.PGAddressCityEditText.getText().toString());
                        pgUserMap.put("state",binding.PGAddressStateEditText.getText().toString());
                        pgUserMap.put("pin",binding.PGAddressPinCodeEditText.getText().toString());
                        pgUserMap.put("seater1",binding.PG1seaterEditText.getText().toString());
                        pgUserMap.put("seater2",binding.PG2seaterEditText.getText().toString());
                        pgUserMap.put("seater3",binding.PG3seaterEditText.getText().toString());
                        pgUserMap.put("image",myUrl);
                        pgUserMap.put("electricityBill",selectedBill);
                        pgUserMap.put("totalUsers","0");
                        pgUserMap.put("paidUsers","0");
                        pgUserMap.put("revenue","0");
                        pgUserMap.put("search", binding.PGNameEditText.getText().toString().toLowerCase()+" pg hostel");
                        pgUserMap.put("id", newId);
                        pgUserMap.put("oid", auth.getUid());
                        pgUserMap.put("contact", owner.getContact());
                        pgUserMap.put("deleted", "false");
                        pgUserMap.put("stopRequests", "false");
                        pgUserMap.put("deactivated", "false");

                        ref.child(newId).updateChildren(pgUserMap);

                        if(id.equals("new")){
                            FirebaseDatabase.getInstance().getReference("OwnerPGs").child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    long count = snapshot.getChildrenCount()+1;
                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("PG"+count,newId);
                                    FirebaseDatabase.getInstance().getReference("OwnerPGs").child(auth.getUid()).updateChildren(map);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                        progressDialog.dismiss();
                        startActivity(new Intent(AddNewBussinessActivity.this, OwnerMainActivity.class));
                        Toast.makeText(AddNewBussinessActivity.this, "Info added successfully", Toast.LENGTH_SHORT).show();
                        finishAffinity();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(AddNewBussinessActivity.this, "Error, Please try again", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, "Image is not selected", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }

    }

    private void uploadMessImage(){

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Adding Info...");
        progressDialog.setMessage("Please wait, while we are adding data");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if(imageUri != null){
            final StorageReference fileRef = storage.getReference().child("Mess").child(newId+".jpg");

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
                        pgUserMap.put("name",binding.MessNameEditText.getText().toString());
                        pgUserMap.put("description",binding.MessDescriptionEditText.getText().toString());
                        pgUserMap.put("locality",binding.MessAddressLocalityEditText.getText().toString());
                        pgUserMap.put("city",binding.MessAddressCityEditText.getText().toString());
                        pgUserMap.put("state",binding.MessAddressStateEditText.getText().toString());
                        pgUserMap.put("pin",binding.MessAddressPinCodeEditText.getText().toString());
                        pgUserMap.put("feeMonthly",binding.MessMonthlyEditText.getText().toString());
                        pgUserMap.put("image",myUrl);
                        pgUserMap.put("totalUsers","0");
                        pgUserMap.put("paidUsers","0");
                        pgUserMap.put("revenue","0");
                        pgUserMap.put("search", binding.MessNameEditText.getText().toString().toLowerCase()+" mess");
                        pgUserMap.put("id", newId);
                        pgUserMap.put("oid", auth.getUid());
                        pgUserMap.put("contact", owner.getContact());
                        pgUserMap.put("deleted", "false");
                        pgUserMap.put("stopRequests", "false");
                        pgUserMap.put("deactivated", "false");

                        ref.child(newId).updateChildren(pgUserMap);

                        if(id.equals("new")){
                            FirebaseDatabase.getInstance().getReference("OwnerMess").child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    long count = snapshot.getChildrenCount()+1;
                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("Mess"+count,newId);
                                    FirebaseDatabase.getInstance().getReference("OwnerMess").child(auth.getUid()).updateChildren(map);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                        progressDialog.dismiss();
                        Intent intent = new Intent(AddNewBussinessActivity.this, EditMessMenuActivity.class);
                        intent.putExtra("Mess Id", ""+newId);
                        startActivity(intent);
                        Toast.makeText(AddNewBussinessActivity.this, "Info added successfully", Toast.LENGTH_SHORT).show();
                        finishAffinity();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(AddNewBussinessActivity.this, "Error, Please try again", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else {
            Toast.makeText(this, "Image is not selected", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }

    }

    private void addMYPG(){
        if(binding.PGNameEditText.getText().toString().equals("")){
            binding.PGNameEditText.setError("Field can't be empty");
            Toast.makeText(this, "Please enter name of PG/Hostel", Toast.LENGTH_SHORT).show();
        } else if(binding.PGDescriptionEditText.getText().toString().equals("")){
            binding.PGDescriptionEditText.setError("Field can't be empty");
            Toast.makeText(this, "Please enter Description", Toast.LENGTH_SHORT).show();
        } else if(binding.PGAddressLocalityEditText.getText().toString().equals("")){
            binding.PGAddressLocalityEditText.setError("Field can't be empty");
            Toast.makeText(this, "Please enter Locality", Toast.LENGTH_SHORT).show();
        } else if(binding.PGAddressCityEditText.getText().toString().equals("")){
            binding.PGAddressCityEditText.setError("Field can't be empty");
            Toast.makeText(this, "Please enter City", Toast.LENGTH_SHORT).show();
        } else if(binding.PGAddressStateEditText.getText().toString().equals("")){
            binding.PGAddressStateEditText.setError("Field can't be empty");
            Toast.makeText(this, "Please enter State", Toast.LENGTH_SHORT).show();
        } else if(binding.PGAddressPinCodeEditText.getText().toString().equals("")){
            binding.PGAddressPinCodeEditText.setError("Field can't be empty");
            Toast.makeText(this, "Please enter Pin Code", Toast.LENGTH_SHORT).show();
        } else if(binding.PG1seaterEditText.getText().toString().equals("")){
            binding.PG1seaterEditText.setError("Field can't be empty");
            Toast.makeText(this, "Please enter 1 seater fee", Toast.LENGTH_SHORT).show();
        } else if(binding.PG2seaterEditText.getText().toString().equals("")){
            binding.PG2seaterEditText.setError("Field can't be empty");
            Toast.makeText(this, "Please enter 2 seater fee", Toast.LENGTH_SHORT).show();
        } else if(binding.PG3seaterEditText.getText().toString().equals("")){
            binding.PG3seaterEditText.setError("Field can't be empty");
            Toast.makeText(this, "Please enter 3 seater fee", Toast.LENGTH_SHORT).show();
        } else {
            newId = id;
            if(id.equals("new"))
                newId = database.getReference().push().getKey();
            if(Clicked.equals("PG")){
                uploadPGImage();
            } else {
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Adding Info...");
                progressDialog.setMessage("Please wait, while we are adding data");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("PGs");

                HashMap<String, Object> pgUserMap = new HashMap<>();
                pgUserMap.put("name",binding.PGNameEditText.getText().toString());
                pgUserMap.put("description",binding.PGDescriptionEditText.getText().toString());
                pgUserMap.put("locality",binding.PGAddressLocalityEditText.getText().toString());
                pgUserMap.put("city",binding.PGAddressCityEditText.getText().toString());
                pgUserMap.put("state",binding.PGAddressStateEditText.getText().toString());
                pgUserMap.put("pin",binding.PGAddressPinCodeEditText.getText().toString());
                pgUserMap.put("seater1",binding.PG1seaterEditText.getText().toString());
                pgUserMap.put("seater2",binding.PG2seaterEditText.getText().toString());
                pgUserMap.put("seater3",binding.PG3seaterEditText.getText().toString());
                pgUserMap.put("electricityBill",selectedBill);
                pgUserMap.put("totalUsers","0");
                pgUserMap.put("paidUsers","0");
                pgUserMap.put("revenue","0");
                pgUserMap.put("search", binding.PGNameEditText.getText().toString().toLowerCase()+" pg hostel");
                pgUserMap.put("id", newId);
                pgUserMap.put("oid", auth.getUid());
                pgUserMap.put("contact", owner.getContact());
                pgUserMap.put("deleted", "false");
                pgUserMap.put("stopRequests", "false");
                pgUserMap.put("deactivated", "false");

                ref.child(newId).updateChildren(pgUserMap);

                if(id.equals("new")){
                    FirebaseDatabase.getInstance().getReference("OwnerPGs").child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            long count = snapshot.getChildrenCount()+1;
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("PG"+count,newId);
                            FirebaseDatabase.getInstance().getReference("OwnerPGs").child(auth.getUid()).updateChildren(map);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                progressDialog.dismiss();
                startActivity(new Intent(AddNewBussinessActivity.this, OwnerMainActivity.class));
                Toast.makeText(AddNewBussinessActivity.this, "Info added successfully", Toast.LENGTH_SHORT).show();
                finishAffinity();
            }
        }
    }

    private void addMyMess(){
        if(binding.MessNameEditText.getText().toString().equals("")){
            binding.MessNameEditText.setError("Field can't be empty");
            Toast.makeText(this, "Please enter Name of Mess", Toast.LENGTH_SHORT).show();
        } else if(binding.MessDescriptionEditText.getText().toString().equals("")){
            binding.MessDescriptionEditText.setError("Field can't be empty");
            Toast.makeText(this, "Please enter Description", Toast.LENGTH_SHORT).show();
        } else if(binding.MessAddressLocalityEditText.getText().toString().equals("")){
            binding.MessAddressLocalityEditText.setError("Field can't be empty");
            Toast.makeText(this, "Please enter Locality", Toast.LENGTH_SHORT).show();
        } else if(binding.MessAddressCityEditText.getText().toString().equals("")){
            binding.MessAddressCityEditText.setError("Field can't be empty");
            Toast.makeText(this, "Please enter City", Toast.LENGTH_SHORT).show();
        } else if(binding.MessAddressStateEditText.getText().toString().equals("")){
            binding.MessAddressStateEditText.setError("Field can't be empty");
            Toast.makeText(this, "Please enter State", Toast.LENGTH_SHORT).show();
        } else if(binding.MessAddressPinCodeEditText.getText().toString().equals("")){
            binding.MessAddressPinCodeEditText.setError("Field can't be empty");
            Toast.makeText(this, "Please enter Pin Code", Toast.LENGTH_SHORT).show();
        } else if(binding.MessMonthlyEditText.getText().toString().equals("")){
            binding.MessMonthlyEditText.setError("Field can't be empty");
            Toast.makeText(this, "Please enter Monthly fee", Toast.LENGTH_SHORT).show();
        } else {
            newId = id;
            if(id.equals("new")) newId = database.getReference().push().getKey();
            if(Clicked.equals("Mess")){
                uploadMessImage();
            } else {
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Adding Info...");
                progressDialog.setMessage("Please wait, while we are adding data");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Mess");

                HashMap<String, Object> pgUserMap = new HashMap<>();
                pgUserMap.put("name",binding.MessNameEditText.getText().toString());
                pgUserMap.put("description",binding.MessDescriptionEditText.getText().toString());
                pgUserMap.put("locality",binding.MessAddressLocalityEditText.getText().toString());
                pgUserMap.put("city",binding.MessAddressCityEditText.getText().toString());
                pgUserMap.put("state",binding.MessAddressStateEditText.getText().toString());
                pgUserMap.put("pin",binding.MessAddressPinCodeEditText.getText().toString());
                pgUserMap.put("feeMonthly",binding.MessMonthlyEditText.getText().toString());
                pgUserMap.put("totalUsers","0");
                pgUserMap.put("paidUsers","0");
                pgUserMap.put("revenue","0");
                pgUserMap.put("search", binding.MessNameEditText.getText().toString().toLowerCase()+" mess");
                pgUserMap.put("id", newId);
                pgUserMap.put("oid", owner.getuId());
                pgUserMap.put("contact", owner.getContact());
                pgUserMap.put("deleted", "false");
                pgUserMap.put("stopRequests", "false");
                pgUserMap.put("deactivated", "false");

                ref.child(newId).updateChildren(pgUserMap);

                if(id.equals("new")){
                    FirebaseDatabase.getInstance().getReference("OwnerMess").child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            long count = snapshot.getChildrenCount()+1;
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("Mess"+count,newId);
                            FirebaseDatabase.getInstance().getReference("OwnerMess").child(auth.getUid()).updateChildren(map);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                progressDialog.dismiss();
                Intent intent = new Intent(AddNewBussinessActivity.this, EditMessMenuActivity.class);
                intent.putExtra("Mess Id", ""+newId);
                startActivity(intent);
                Toast.makeText(AddNewBussinessActivity.this, "Info added successfully", Toast.LENGTH_SHORT).show();
                finishAffinity();
            }
        }
    }

}