package com.androidaxe.getmypg.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.androidaxe.getmypg.Module.OwnerMess;
import com.androidaxe.getmypg.Module.OwnerPG;
import com.androidaxe.getmypg.Module.PGOwner;
import com.androidaxe.getmypg.Module.PGUser;
import com.androidaxe.getmypg.R;
import com.androidaxe.getmypg.databinding.ActivityCheckOutBinding;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class CheckOutActivity extends AppCompatActivity {

    ActivityCheckOutBinding binding;
    String productId;
    String type;
    String newUser;
    String total = "", roomType = "";
    String date;
    OwnerMess mess;
    OwnerPG pg;
    FirebaseDatabase database;
    FirebaseAuth auth;
    PGUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckOutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        type = getIntent().getStringExtra("type");
        productId = getIntent().getStringExtra("id");
        newUser = getIntent().getStringExtra("new");
        date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        getSupportActionBar().setTitle("Subscribe");

        database.getReference("PGUser").child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentUser = snapshot.getValue(PGUser.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if(type.equals("mess")){
            binding.checkoutMess.setVisibility(View.VISIBLE);
            binding.checkoutPg.setVisibility(View.GONE);
            checkoutMess();
        } else {
            binding.checkoutMess.setVisibility(View.GONE);
            binding.checkoutPg.setVisibility(View.VISIBLE);
            checkoutPG();
        }

    }

    private void checkoutPG() {

        database.getReference("PGs").child(productId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pg = snapshot.getValue(OwnerPG.class);
                binding.checkoutPgName.setText(pg.getName());
                binding.checkoutPgPrice1.setText("Price (Single seater pre Month) : Rs. "+pg.getSeater1());
                binding.checkoutPgPrice2.setText("Price (Double seater pre Month) : Rs. "+pg.getSeater2());
                binding.checkoutPgPrice3.setText("Price (Triple seater pre Month) : Rs. "+pg.getSeater3());
                binding.checkoutPgElectricity.setText("Electricity bill : "+pg.getElectricityBill());
                binding.checkoutPgLocation.setText("Location : "+pg.getLocality()+", "+pg.getCity()+", "+pg.getState()+" "+pg.getPin());
                getSupportActionBar().setTitle("Subscribe "+pg.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if(newUser.equals("yes")){
            binding.checkoutPgPaynowButton.setText("Request Owner");
        }

        binding.checkoutPgRadioGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int buttonId) {
                if(buttonId == R.id.checkout_pg_radio_single){
                    roomType = "1";
                    total = pg.getSeater1();
                    binding.checkoutPgTotal.setText("Rs. "+total);
                } else if(buttonId == R.id.checkout_pg_radio_double){
                    roomType = "2";
                    total = pg.getSeater2();
                    binding.checkoutPgTotal.setText("Rs. "+total);
                } else if (buttonId == R.id.checkout_pg_radio_triple) {
                    roomType = "3";
                    total = pg.getSeater3();
                    binding.checkoutPgTotal.setText("Rs. "+total);
                }
            }
        });

        binding.checkoutPgPaynowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(roomType.equals("")){
                    Toast.makeText(CheckOutActivity.this, "Please select room type", Toast.LENGTH_SHORT).show();
                } else {
                    final ProgressDialog progressDialog = new ProgressDialog(CheckOutActivity.this);
                    progressDialog.setTitle("Sending request...");
                    progressDialog.setMessage("Please wait, while we are sending request");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    String requestId = database.getReference().push().getKey();

                    HashMap<String, Object> requestMap = new HashMap<>();
                    requestMap.put("uid", currentUser.getuId());
                    requestMap.put("oid", pg.getOid());
                    requestMap.put("pgid", pg.getId());
                    requestMap.put("requestId", requestId);
                    requestMap.put("userName", currentUser.getName());
                    requestMap.put("type", type);
                    requestMap.put("roomType", roomType);
                    requestMap.put("status", "Pending");
                    requestMap.put("date", date);
                    requestMap.put("businessName", pg.getName());
                    requestMap.put("userContact", currentUser.getContact());
                    requestMap.put("ownerContact", pg.getContact());
                    requestMap.put("userImage", currentUser.getProfile());
                    requestMap.put("businessImage", pg.getImage());
                    requestMap.put("price", total);

                    database.getReference("Requests").child("PGRequests").child(requestId).updateChildren(requestMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            HashMap<String, Object> ownerRequestMap = new HashMap<>();
                            ownerRequestMap.put(requestId, requestId);
                            database.getReference("Requests").child("OwnerPGRequests").child(pg.getOid()).child("requestIds").updateChildren(ownerRequestMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    database.getReference("Requests").child("UserPGRequests").child(currentUser.getuId()).child("requestIds").updateChildren(ownerRequestMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            HashMap<String, Object> NewRequestMap = new HashMap<>();
                                            NewRequestMap.put("NewRequest", "Yes");
                                            database.getReference("Requests").child("OwnerPGRequests").child(pg.getOid()).child("newRequest").updateChildren(NewRequestMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(CheckOutActivity.this, "Request Send Successfully Check status in Request tab", Toast.LENGTH_SHORT).show();
                                                    binding.checkoutPgPaynowButton.setEnabled(false);
                                                    binding.checkoutPgPaynowButton.setBackgroundDrawable(getDrawable(R.drawable.button_background_grey));
                                                }
                                            });

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.dismiss();
                                            Toast.makeText(CheckOutActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(CheckOutActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(CheckOutActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });

    }

    private void checkoutMess(){
        database.getReference("Mess").child(productId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mess = snapshot.getValue(OwnerMess.class);
                binding.checkoutMessName.setText(mess.getName());
                binding.checkoutMessPrice.setText("Price (pre month) : Rs. "+mess.getFeeMonthly());
                binding.checkoutMessLocation.setText(mess.getLocality()+", "+mess.getCity()+", "+mess.getState()+" "+mess.getPin());
                binding.checkoutMessTotal.setText("Rs. "+mess.getFeeMonthly());
                getSupportActionBar().setTitle("Subscribe "+mess.getName());
                total = mess.getFeeMonthly();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if(newUser.equals("yes")){
            binding.checkoutMessPaynowButton.setText("Request Owner");
        }

        binding.checkoutMessPaynowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog progressDialog = new ProgressDialog(CheckOutActivity.this);
                progressDialog.setTitle("Sending request...");
                progressDialog.setMessage("Please wait, while we are sending request");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                String requestId = database.getReference().push().getKey();

                HashMap<String, Object> requestMap = new HashMap<>();
                requestMap.put("uid", currentUser.getuId());
                requestMap.put("oid", mess.getOid());
                requestMap.put("pgid", mess.getId());
                requestMap.put("requestId", requestId);
                requestMap.put("userName", currentUser.getName());
                requestMap.put("type", type);
                requestMap.put("roomType", "na");
                requestMap.put("status", "Pending");
                requestMap.put("date", date);
                requestMap.put("businessName", mess.getName());
                requestMap.put("userContact", currentUser.getContact());
                requestMap.put("ownerContact", mess.getContact());
                requestMap.put("userImage", currentUser.getProfile());
                requestMap.put("businessImage", mess.getImage());
                requestMap.put("price", total);

                database.getReference("Requests").child("MessRequests").child(requestId).updateChildren(requestMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        HashMap<String, Object> ownerRequestMap = new HashMap<>();
                        ownerRequestMap.put(requestId, requestId);
                        database.getReference("Requests").child("OwnerMessRequests").child(mess.getOid()).child("requestIds").updateChildren(ownerRequestMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                database.getReference("Requests").child("UserMessRequests").child(currentUser.getuId()).child("requestIds").updateChildren(ownerRequestMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        HashMap<String, Object> NewRequestMap = new HashMap<>();
                                        NewRequestMap.put("NewRequest", "Yes");
                                        database.getReference("Requests").child("OwnerMessRequests").child(mess.getOid()).child("newRequest").updateChildren(NewRequestMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();
                                                Toast.makeText(CheckOutActivity.this, "Request Send Successfully Check status in Request tab", Toast.LENGTH_SHORT).show();
                                                binding.checkoutMessPaynowButton.setEnabled(false);
                                                binding.checkoutMessPaynowButton.setBackgroundDrawable(getDrawable(R.drawable.button_background_grey));
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(CheckOutActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(CheckOutActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(CheckOutActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

}