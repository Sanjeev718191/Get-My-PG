package com.androidaxe.getmypg.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.androidaxe.getmypg.Module.OwnerMess;
import com.androidaxe.getmypg.Module.OwnerPG;
import com.androidaxe.getmypg.Module.Request;
import com.androidaxe.getmypg.R;
import com.androidaxe.getmypg.databinding.ActivityOwnerAcceptRequestBinding;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.HashMap;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class OwnerAcceptRequestActivity extends AppCompatActivity {

    ActivityOwnerAcceptRequestBinding binding;
    String type, requestId;
    Request request;
    FirebaseDatabase database;
    ProgressDialog progressDialog;
    OwnerPG pg;
    QRGEncoder qrgEncoder;
    Uri qrUri;
    StorageReference storageReference;
    String customerQRLink;
    private StorageTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOwnerAcceptRequestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Updating Info...");
        progressDialog.setCanceledOnTouchOutside(false);

        type = getIntent().getStringExtra("type");
        requestId = getIntent().getStringExtra("id");
        database = FirebaseDatabase.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference().child("User Subscription QR");
        getSupportActionBar().setTitle("Set new Customer");

        if(type.equals("mess")){
            progressDialog.show();
            FirebaseDatabase.getInstance().getReference("Requests").child("MessRequests").child(requestId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    request = snapshot.getValue(Request.class);
                    binding.acceptRequestUserName.setText(request.getUserName());
                    binding.acceptRequestRoomTypeLayout.setVisibility(View.GONE);
                    binding.acceptRequestRoomNumberLayout.setVisibility(View.GONE);
                    binding.acceptRequestSetPrice.setText(request.getPrice());
                    Glide.with(OwnerAcceptRequestActivity.this).load(request.getUserImage()).into(binding.acceptRequestUserImage);

                    database.getReference("BusinessSubscriber").child("BusinessUser").child(request.getPgid()).child(request.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String id = snapshot.getValue(String.class);
                            if(id != null && !id.equals("")) {
                                deactivateAcceptBtn();
                            }
                            else {
                                activateAcceptBtn();
                            }
                            progressDialog.dismiss();
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            progressDialog.dismiss();
                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } else if(type.equals("pg")){
            progressDialog.show();
            FirebaseDatabase.getInstance().getReference("Requests").child("PGRequests").child(requestId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    request = snapshot.getValue(Request.class);
                    FirebaseDatabase.getInstance().getReference("PGs").child(request.getPgid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            pg = snapshot.getValue(OwnerPG.class);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    binding.acceptRequestUserName.setText(request.getUserName());
                    binding.acceptRequestRoomTypeLayout.setVisibility(View.VISIBLE);
                    binding.acceptRequestRoomNumberLayout.setVisibility(View.VISIBLE);
                    Glide.with(OwnerAcceptRequestActivity.this).load(request.getUserImage()).into(binding.acceptRequestUserImage);
                    if(request.getRoomType().equals("1")){
                        binding.acceptRequestRoomType.setText("Single Seater");
                    } else if(request.getRoomType().equals("2")){
                        binding.acceptRequestRoomType.setText("Double Seater");
                    } else {
                        binding.acceptRequestRoomType.setText("Triple Seater");
                    }
                    binding.acceptRequestSetPrice.setText(request.getPrice());

                    database.getReference("BusinessSubscriber").child("BusinessUser").child(request.getPgid()).child(request.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String id = snapshot.getValue(String.class);
                            if(id != null && !id.equals("")) {
                                deactivateAcceptBtn();
                            }
                            else {
                                activateAcceptBtn();
                            }
                            progressDialog.dismiss();
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            progressDialog.dismiss();
                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

        binding.acceptRequestRejectUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                if(request.getType().equals("pg")) {
                    FirebaseDatabase.getInstance().getReference("Requests").child("PGRequests").child(request.getRequestId()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            snapshot.getRef().child("status").setValue("Rejected").addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    progressDialog.dismiss();
                                    binding.acceptRequestRejectUserButton.setEnabled(false);
                                    binding.acceptRequestRejectUserButton.setBackgroundDrawable(getDrawable(R.drawable.button_deactive_background));
                                    binding.acceptRequestSetUserButton.setEnabled(false);
                                    binding.acceptRequestSetUserButton.setBackgroundDrawable(getDrawable(R.drawable.button_deactive_background));
                                    finish();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            progressDialog.dismiss();
                            Toast.makeText(OwnerAcceptRequestActivity.this, "Please try again...", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if(request.getType().equals("mess")) {
                    FirebaseDatabase.getInstance().getReference("Requests").child("MessRequests").child(request.getRequestId()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            snapshot.getRef().child("status").setValue("Rejected").addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    progressDialog.dismiss();
                                    binding.acceptRequestRejectUserButton.setEnabled(false);
                                    binding.acceptRequestRejectUserButton.setBackgroundDrawable(getDrawable(R.drawable.button_deactive_background));
                                    binding.acceptRequestSetUserButton.setEnabled(false);
                                    binding.acceptRequestSetUserButton.setBackgroundDrawable(getDrawable(R.drawable.button_deactive_background));
                                    finish();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            progressDialog.dismiss();
                            Toast.makeText(OwnerAcceptRequestActivity.this, "Please try again...", Toast.LENGTH_SHORT).show();
                        }
                    });
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

    private void activateAcceptBtn() {
        binding.acceptRequestSetUserButton.setBackgroundDrawable(getDrawable(R.drawable.button_background));
        binding.acceptRequestSetUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(request.getType().equals("pg")) {
                    if(binding.acceptRequestRoomType.getText().equals("")){
                        binding.acceptRequestRoomType.setError("Please enter Room type");
                    } else if(binding.acceptRequestRoomNumber.getText().equals("")){
                        binding.acceptRequestRoomNumber.setError("Please enter Room Number");
                    } else if(Integer.parseInt(binding.acceptRequestRoomNumber.getText().toString()) > Integer.parseInt(pg.getRoomCount()) ||  Integer.parseInt(binding.acceptRequestRoomNumber.getText().toString()) < 0){
                        binding.acceptRequestRoomNumber.setError("Please enter Valid Room No.");
                    } else if(binding.acceptRequestNote.getText().equals("")){
                        binding.acceptRequestNote.setError("Please enter user note");
                    } else if(binding.acceptRequestSetPrice.getText().equals("")){
                        binding.acceptRequestSetPrice.setError("Please set Price");
                    } else {
                        progressDialog.show();
                        addPGToUser();
                    }
                } else if(request.getType().equals("mess")) {
                    if(binding.acceptRequestNote.getText().equals("")){
                        binding.acceptRequestNote.setError("Please enter user note");
                    } else if(binding.acceptRequestSetPrice.getText().equals("")){
                        binding.acceptRequestSetPrice.setError("Please set Price");
                    } else {
                        progressDialog.show();
                        addMessToUser();
                    }
                }
            }
        });
    }

    private void deactivateAcceptBtn() {
        String requestType = type.equals("pg") ? "PGRequests":"MessRequests";
        binding.acceptRequestSetUserButton.setBackgroundDrawable(getDrawable(R.drawable.button_deactive_background));
        binding.acceptRequestSetUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(OwnerAcceptRequestActivity.this, "This user is Already Your customer.", Toast.LENGTH_SHORT).show();
                database.getReference("Requests").child(requestType).child(requestId).child("status").setValue("Accepted");
                progressDialog.dismiss();
                binding.acceptRequestRejectUserButton.setEnabled(false);
                binding.acceptRequestRejectUserButton.setBackgroundDrawable(getDrawable(R.drawable.button_deactive_background));
                binding.acceptRequestSetUserButton.setEnabled(false);
                binding.acceptRequestSetUserButton.setBackgroundDrawable(getDrawable(R.drawable.button_deactive_background));
                finish();
            }
        });
    }


    private void addMessToUser() {
        //add info to user and owner database
        progressDialog.show();
        String newSubscriptionID = database.getReference().push().getKey();

        //generate qr code
        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int height = point.y;
        int dimen = width<height? width: height;
        dimen = dimen*3/4;
        qrgEncoder = new QRGEncoder(newSubscriptionID, null, QRGContents.Type.TEXT, dimen);
        try {
            qrUri = getImageUri(qrgEncoder.getBitmap());
            final StorageReference fileRef = storageReference.child(newSubscriptionID+".jpg");
            uploadTask = fileRef.putFile(qrUri);
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
                    Uri downloadUri = (Uri) task.getResult();
                    customerQRLink = downloadUri.toString();

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("PGMessId",request.getPgid());
                    map.put("type", "mess");
                    map.put("fromDate","na");
                    map.put("toDate","na");
                    map.put("uid", request.getUid());
                    map.put("oid", request.getOid());
                    map.put("roomType", "na");
                    map.put("roomNumber", "na");
                    map.put("note", binding.acceptRequestNote.getText().toString());
                    map.put("price", binding.acceptRequestSetPrice.getText().toString());
                    map.put("Notice", "na");
                    map.put("currentlyActive", "false");
                    map.put("subscriptionId",newSubscriptionID);
                    map.put("lastPaidAmount", "na");
                    map.put("paymentDate", "na");
                    map.put("qrCode", customerQRLink);

                    database.getReference("Subscription").child(newSubscriptionID).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            HashMap<String, Object> countMap = new HashMap<>();
                            countMap.put(newSubscriptionID, newSubscriptionID);
                            database.getReference("UserSubscription").child("UserMess").child(request.getUid()).updateChildren(countMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    database.getReference("BusinessSubscriber").child("MessUser").child(request.getPgid()).updateChildren(countMap);
                                    database.getReference("BusinessSubscriber").child("BusinessUser").child(request.getPgid()).child(request.getUid()).setValue(request.getUid());

                                    database.getReference("Mess").child(request.getPgid()).child("totalUsers").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            int userCount = Integer.parseInt(snapshot.getValue(String.class));
                                            userCount++;
                                            HashMap<String, Object> map2 = new HashMap<>();
                                            map2.put("totalUsers", userCount+"");
                                            database.getReference("Mess").child(request.getPgid()).updateChildren(map2);
                                            database.getReference("Requests").child("MessRequests").child(requestId).child("status").setValue("Accepted");
                                            progressDialog.dismiss();
                                            binding.acceptRequestRejectUserButton.setEnabled(false);
                                            binding.acceptRequestRejectUserButton.setBackgroundDrawable(getDrawable(R.drawable.button_deactive_background));
                                            binding.acceptRequestSetUserButton.setEnabled(false);
                                            binding.acceptRequestSetUserButton.setBackgroundDrawable(getDrawable(R.drawable.button_deactive_background));
                                            finish();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            progressDialog.dismiss();
                                        }
                                    });
                                }
                            });
                        }
                    });

                }
            });
        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "Unable to add user. Please try ones again..", Toast.LENGTH_SHORT).show();
        }

    }

    long pgCount = 1;
    private void addPGToUser() {
        progressDialog.show();
        String newSubscriptionID = database.getReference().push().getKey();

        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int height = point.y;
        int dimen = width<height? width: height;
        dimen = dimen*3/4;
        qrgEncoder = new QRGEncoder(newSubscriptionID, null, QRGContents.Type.TEXT, dimen);
        try {
            qrUri = getImageUri(qrgEncoder.getBitmap());
            final StorageReference fileRef = storageReference.child(newSubscriptionID+".jpg");
            uploadTask = fileRef.putFile(qrUri);
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
                    Uri downloadUri = (Uri) task.getResult();
                    customerQRLink = downloadUri.toString();

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("PGMessId",request.getPgid());
                    map.put("type", "pg");
                    map.put("fromDate","na");
                    map.put("toDate","na");
                    map.put("uid", request.getUid());
                    map.put("oid", request.getOid());
                    map.put("roomType", binding.acceptRequestRoomType.getText().toString());
                    map.put("roomNumber", ""+Integer.parseInt(binding.acceptRequestRoomNumber.getText().toString()));
                    map.put("note", binding.acceptRequestNote.getText().toString());
                    map.put("price", binding.acceptRequestSetPrice.getText().toString());
                    map.put("Notice", "na");
                    map.put("currentlyActive", "false");
                    map.put("subscriptionId",newSubscriptionID);
                    map.put("lastPaidAmount", "na");
                    map.put("paymentDate", "na");
                    map.put("qrCode", customerQRLink);

                    database.getReference("Subscription").child(newSubscriptionID).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            HashMap<String, Object> countMap = new HashMap<>();
                            countMap.put(newSubscriptionID, newSubscriptionID);
//                            database.getReference("UserSubscription").child("UserPG").child(request.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//
//
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError error) {
//                                    progressDialog.dismiss();
//                                }
//                            });
                            database.getReference("UserSubscription").child("UserPG").child(request.getUid()).updateChildren(countMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    database.getReference("BusinessSubscriber").child("HostelUser").child(request.getPgid()).updateChildren(countMap);
                                    database.getReference("BusinessSubscriber").child("BusinessUser").child(request.getPgid()).child(request.getUid()).setValue(request.getUid());

                                    database.getReference("PGs").child(request.getPgid()).child("totalUsers").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            int userCount = Integer.parseInt(snapshot.getValue(String.class));
                                            userCount++;
                                            HashMap<String, Object> map2 = new HashMap<>();
                                            map2.put("totalUsers", userCount+"");
                                            database.getReference("PGs").child(request.getPgid()).updateChildren(map2);
                                            database.getReference("Requests").child("PGRequests").child(requestId).child("status").setValue("Accepted");
                                            database.getReference("PGRoom").child(pg.getId()).child("Room"+Integer.parseInt(binding.acceptRequestRoomNumber.getText().toString())).child("users").updateChildren(countMap);
                                            progressDialog.dismiss();
                                            binding.acceptRequestRejectUserButton.setEnabled(false);
                                            binding.acceptRequestRejectUserButton.setBackgroundDrawable(getDrawable(R.drawable.button_deactive_background));
                                            binding.acceptRequestSetUserButton.setEnabled(false);
                                            binding.acceptRequestSetUserButton.setBackgroundDrawable(getDrawable(R.drawable.button_deactive_background));
//                                            startActivity(new Intent(OwnerAcceptRequestActivity.this, OwnerRequestsActivity.class));
                                            finish();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            progressDialog.dismiss();
                                        }
                                    });
                                }
                            });

                        }
                    });

                }
            });
        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "Unable to add user. Please try ones again..", Toast.LENGTH_SHORT).show();
        }

    }

    public Uri getImageUri( Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), inImage, "IMG_" + Calendar.getInstance().getTime(), null);
        return Uri.parse(path);
    }

}