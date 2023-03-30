package com.androidaxe.getmypg.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.androidaxe.getmypg.Module.Request;
import com.androidaxe.getmypg.R;
import com.androidaxe.getmypg.databinding.ActivityOwnerAcceptRequestBinding;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class OwnerAcceptRequestActivity extends AppCompatActivity {

    ActivityOwnerAcceptRequestBinding binding;
    String type, requestId;
    Request request;
    FirebaseDatabase database;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOwnerAcceptRequestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Updating Info...");
        progressDialog.setCanceledOnTouchOutside(false);

        type = getIntent().getStringExtra("type");
        requestId = getIntent().getStringExtra("id");
        database = FirebaseDatabase.getInstance();
        getSupportActionBar().setTitle("Set new Customer");

        if(type.equals("mess")){
            progressDialog.show();
            FirebaseDatabase.getInstance().getReference("Requests").child("MessRequests").child(requestId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    request = snapshot.getValue(Request.class);
                    progressDialog.dismiss();
                    binding.acceptRequestUserName.setText(request.getUserName());
                    binding.acceptRequestRoomTypeLayout.setVisibility(View.GONE);
                    binding.acceptRequestRoomNumberLayout.setVisibility(View.GONE);
                    binding.acceptRequestSetPrice.setText(request.getPrice());
                    Glide.with(OwnerAcceptRequestActivity.this).load(request.getUserImage()).into(binding.acceptRequestUserImage);
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
                    progressDialog.dismiss();
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
                                    startActivity(new Intent(OwnerAcceptRequestActivity.this, OwnerRequestsActivity.class));
                                    finishAffinity();
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
                                    startActivity(new Intent(OwnerAcceptRequestActivity.this, OwnerRequestsActivity.class));
                                    finishAffinity();
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

        binding.acceptRequestSetUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                if(request.getType().equals("pg")) {
                    if(binding.acceptRequestRoomType.getText().equals("")){
                        binding.acceptRequestRoomType.setError("Please enter Room type");
                    } else if(binding.acceptRequestRoomNumber.getText().equals("")){
                        binding.acceptRequestRoomNumber.setError("Please enter Room Number");
                    } else if(binding.acceptRequestNote.getText().equals("")){
                        binding.acceptRequestNote.setError("Please enter user note");
                    } else if(binding.acceptRequestSetPrice.getText().equals("")){
                        binding.acceptRequestSetPrice.setError("Please set Price");
                    } else {
                        addPGToUser();
                    }
                } else if(request.getType().equals("mess")) {
                    if(binding.acceptRequestNote.getText().equals("")){
                        binding.acceptRequestNote.setError("Please enter user note");
                    } else if(binding.acceptRequestSetPrice.getText().equals("")){
                        binding.acceptRequestSetPrice.setError("Please set Price");
                    } else {
                        addMessToUser();
                    }
                }
            }
        });

    }

    long messCount = 1;
    private void addMessToUser() {
        //add info to user and owner database
        progressDialog.show();
        String newSubscriptionID = database.getReference().push().getKey();
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

        database.getReference("Subscription").child(newSubscriptionID).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                database.getReference("UserSubscription").child("UserMess").child(request.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messCount = snapshot.getChildrenCount() + 1;
                        HashMap<String, Object> countMap = new HashMap<>();
                        countMap.put(newSubscriptionID, newSubscriptionID);

                        database.getReference("UserSubscription").child("UserMess").child(request.getUid()).updateChildren(countMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                HashMap<String, Object> map1 = new HashMap<>();
                                map1.put(newSubscriptionID, newSubscriptionID);
                                database.getReference("BusinessSubscriber").child("MessUser").child(request.getPgid()).updateChildren(map1);

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
                                        startActivity(new Intent(OwnerAcceptRequestActivity.this, OwnerRequestsActivity.class));
                                        finishAffinity();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        progressDialog.dismiss();
                                    }
                                });
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();
                    }
                });
            }
        });

    }

    long pgCount = 1;
    private void addPGToUser() {
        progressDialog.show();
        String newSubscriptionID = database.getReference().push().getKey();
        HashMap<String, Object> map = new HashMap<>();
        map.put("PGMessId",request.getPgid());
        map.put("type", "pg");
        map.put("fromDate","na");
        map.put("toDate","na");
        map.put("uid", request.getUid());
        map.put("oid", request.getOid());
        map.put("roomType", binding.acceptRequestRoomType.getText().toString());
        map.put("roomNumber", binding.acceptRequestRoomNumber.getText().toString());
        map.put("note", binding.acceptRequestNote.getText().toString());
        map.put("price", binding.acceptRequestSetPrice.getText().toString());
        map.put("Notice", "na");
        map.put("currentlyActive", "false");
        map.put("subscriptionId",newSubscriptionID);
        map.put("lastPaidAmount", "na");
        map.put("paymentDate", "na");

        database.getReference("Subscription").child(newSubscriptionID).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                database.getReference("UserSubscription").child("UserPG").child(request.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        pgCount = snapshot.getChildrenCount() + 1;
                        HashMap<String, Object> countMap = new HashMap<>();
                        countMap.put("pg"+pgCount, newSubscriptionID);

                        database.getReference("UserSubscription").child("UserPG").child(request.getUid()).updateChildren(countMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                HashMap<String, Object> map1 = new HashMap<>();
                                map1.put(newSubscriptionID, newSubscriptionID);
                                database.getReference("BusinessSubscriber").child("HostelUser").child(request.getPgid()).updateChildren(map1);

                                database.getReference("PGs").child(request.getPgid()).child("totalUsers").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        int userCount = Integer.parseInt(snapshot.getValue(String.class));
                                        userCount++;
                                        HashMap<String, Object> map2 = new HashMap<>();
                                        map2.put("totalUsers", userCount+"");
                                        database.getReference("PGs").child(request.getPgid()).updateChildren(map2);
                                        database.getReference("Requests").child("PGRequests").child(requestId).child("status").setValue("Accepted");
                                        progressDialog.dismiss();
                                        binding.acceptRequestRejectUserButton.setEnabled(false);
                                        binding.acceptRequestRejectUserButton.setBackgroundDrawable(getDrawable(R.drawable.button_deactive_background));
                                        binding.acceptRequestSetUserButton.setEnabled(false);
                                        binding.acceptRequestSetUserButton.setBackgroundDrawable(getDrawable(R.drawable.button_deactive_background));
                                        startActivity(new Intent(OwnerAcceptRequestActivity.this, OwnerRequestsActivity.class));
                                        finishAffinity();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        progressDialog.dismiss();
                                    }
                                });
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();
                    }
                });

            }
        });


    }

}