package com.androidaxe.getmypg.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.androidaxe.getmypg.Module.PGUser;
import com.androidaxe.getmypg.Module.UserSubscribedItem;
import com.androidaxe.getmypg.R;
import com.androidaxe.getmypg.databinding.ActivityOwnerCustomerDetailsBinding;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class OwnerCustomerDetailsActivity extends AppCompatActivity {

    ActivityOwnerCustomerDetailsBinding binding;
    FirebaseDatabase database;
    String subId;
    PGUser user;
    UserSubscribedItem subscription;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOwnerCustomerDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading Info...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        subId = getIntent().getStringExtra("SubId");
        database = FirebaseDatabase.getInstance();

        database.getReference("Subscription").child(subId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressDialog.dismiss();
                subscription = snapshot.getValue(UserSubscribedItem.class);
                binding.ownerCustomerDetailsFromDate.setText("From : "+subscription.getFromDate());
                binding.ownerCustomerDetailsToDate.setText("To : "+subscription.getToDate());
                binding.ownerCustomerDetailsNote.setText("Note : "+subscription.getNote());
                binding.ownerCustomerDetailsPrice.setText("Price : Rs."+subscription.getPrice());
                binding.ownerCustomerDetailsLastPaid.setText("Last paid amount : Rs."+subscription.getLastPaidAmount());
                if(subscription.getType().equals("pg")){
                    binding.ownerCustomerDetailsRoomNum.setVisibility(View.VISIBLE);
                    binding.ownerCustomerDetailsRoomType.setVisibility(View.VISIBLE);
                    binding.ownerCustomerDetailsRoomNum.setText("Room Number : "+subscription.getRoomNumber());
                    binding.ownerCustomerDetailsRoomType.setText("Room Type : "+subscription.getRoomType());
                } else {
                    binding.ownerCustomerDetailsRoomNum.setVisibility(View.GONE);
                    binding.ownerCustomerDetailsRoomType.setVisibility(View.GONE);
                }
                setUserSubscriptionStatus();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.ownerCustomerDetailsRechargeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OwnerCustomerDetailsActivity.this, OwnerPayFeeActivity.class);
                intent.putExtra("subId", subId);
                startActivity(intent);
            }
        });

        binding.ownerCustomerDetailsRemoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteSubscription();
            }
        });

    }

    private void setUserSubscriptionStatus(){
        FirebaseDatabase.getInstance().getReference("PGUser").child(subscription.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(PGUser.class);
                binding.ownerCustomerDetailsUserName.setText(user.getName());
                Glide.with(OwnerCustomerDetailsActivity.this).load(user.getProfile()).into(binding.ownerCustomerDetailsUserImage);
                if(!subscription.getToDate().equals("na")){
                    String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
                    try {
                        Date currDate = sdf.parse(date);
                        Date toDate = sdf.parse(subscription.getToDate());
                        long diffInMillies = Math.abs(toDate.getTime() - currDate.getTime());
                        long remainingDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                        if (toDate.compareTo(currDate) >= 0) {
                            binding.ownerCustomerDetailsStatusCard.setBackgroundColor(OwnerCustomerDetailsActivity.this.getColor(R.color.light_green));
                            binding.ownerCustomerDetailsStatus.setText("Active");
                            binding.ownerCustomerDetailsStatus.setTextColor(OwnerCustomerDetailsActivity.this.getColor(R.color.primary_dark));
                            binding.ownerCustomerDetailsStatusImage.setImageResource(R.drawable.correct);
                        } else {
                            binding.ownerCustomerDetailsStatusCard.setBackgroundColor(OwnerCustomerDetailsActivity.this.getColor(R.color.light_red));
                            binding.ownerCustomerDetailsStatus.setText("Expired");
                            binding.ownerCustomerDetailsStatus.setTextColor(OwnerCustomerDetailsActivity.this.getColor(R.color.red));
                            binding.ownerCustomerDetailsStatusImage.setImageResource(R.drawable.wrong);
                        }
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    binding.ownerCustomerDetailsStatus.setText("Not Started Yet");
                    binding.ownerCustomerDetailsStatusImage.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void deleteSubscription(){
        ProgressDialog userDeleteDialog = new ProgressDialog(this);
        userDeleteDialog.setTitle("Delete User");
        userDeleteDialog.setMessage("Are you sure?\n" + "You want to remove "+user.getName());
        userDeleteDialog.setCancelable(false);
        userDeleteDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        userDeleteDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialog.show();
                database.getReference("Subscription").child(subscription.getSubscriptionId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        if(subscription.getType().equals("pg")){
                            database.getReference("UserSubscription").
                                    child("UserPG").
                                    child(subscription.getUid()).
                                    child(subscription.getSubscriptionId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    database.getReference("BusinessSubscriber").child("HostelUser").child(subscription.getPGMessId()).child(subscription.getSubscriptionId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            database.getReference("PGs").child(subscription.getPGMessId()).child("totalUsers").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    int count = Integer.parseInt(snapshot.getValue(String.class));
                                                    count--;
                                                    database.getReference("PGs").child(subscription.getPGMessId()).child("totalUsers").setValue(count+"");
                                                    progressDialog.dismiss();
                                                    finish();
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        } else {
                            database.getReference("UserSubscription").
                                    child("UserMess").
                                    child(subscription.getUid()).
                                    child(subscription.getSubscriptionId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            database.getReference("BusinessSubscriber").child("MessUser").child(subscription.getPGMessId()).child(subscription.getSubscriptionId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    finish();
                                                    progressDialog.dismiss();
                                                }
                                            });
                                        }
                                    });
                        }
                    }
                });
            }
        });
        userDeleteDialog.show();
    }

}