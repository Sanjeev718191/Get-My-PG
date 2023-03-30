package com.androidaxe.getmypg.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.androidaxe.getmypg.Module.PGUser;
import com.androidaxe.getmypg.Module.UserSubscribedItem;
import com.androidaxe.getmypg.databinding.ActivityOwnerPayFeeBinding;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;

public class OwnerPayFeeActivity extends AppCompatActivity {

    ActivityOwnerPayFeeBinding binding;
    DatePickerDialog datePickerDialog;
    FirebaseDatabase database;
    UserSubscribedItem item;
    PGUser user;
    String subscriptionId;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOwnerPayFeeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Updating Info...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        subscriptionId = getIntent().getStringExtra("subId");
        database = FirebaseDatabase.getInstance();
        database.getReference("Subscription").child(subscriptionId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                item = snapshot.getValue(UserSubscribedItem.class);
                database.getReference("PGUser").child(item.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        user = snapshot.getValue(PGUser.class);
                        Glide.with(OwnerPayFeeActivity.this).load(user.getProfile()).into(binding.payFeeUserImage);
                        binding.payFeeUserName.setText(user.getName());
                        binding.payFeeUserNumber.setText(user.getContact());
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                binding.ownerPayFeeAmount.setText(item.getPrice());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(OwnerPayFeeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        binding.ownerPayFeeFromDate.setText(getTodayDate());
        binding.ownerPayFeeToDate.setText(getTodayDate());
        binding.ownerPayFeeFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDate(binding.ownerPayFeeFromDate);
            }
        });

        binding.ownerPayFeeToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDate(binding.ownerPayFeeToDate);
            }
        });

        binding.payFeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.ownerPayFeeAmount.getText().equals("")){
                    binding.ownerPayFeeAmount.setError("Please Enter amount");
                } else if(binding.ownerPayFeeFromDate.getText().equals("")){
                    binding.ownerPayFeeAmount.setError("Select Starting date");
                } else if(binding.ownerPayFeeToDate.getText().equals("")){
                    binding.ownerPayFeeToDate.setError("Select Ending date");
                } else {
                    rechargeSubscription();
                }
            }
        });

    }

    private void setDate(Button button){
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                if(month <= 9) button.setText(day+"-0"+month+"-"+year);
                else button.setText(day+"-"+month+"-"+year);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;
        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        datePickerDialog.show();
    }

    private String getTodayDate(){
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        if(month <= 9) return day+"-0"+month+"-"+year;
        else return day+"-"+month+"-"+year;
    }

    private void rechargeSubscription(){
        ProgressDialog userDeleteDialog = new ProgressDialog(this);
        userDeleteDialog.setTitle("Pay Fee");
        userDeleteDialog.setMessage("Details\n" +
                "Name : "+user.getName() +
                "\nContact : "+user.getContact()+
                "\nFrom date : "+binding.ownerPayFeeFromDate.getText()+
                "\nTo date : "+binding.ownerPayFeeToDate.getText()+
                "\nAmount : "+binding.ownerPayFeeAmount.getText());
        userDeleteDialog.setCancelable(false);
        userDeleteDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        userDeleteDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Correct", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialog.show();
                HashMap<String, Object> map = new HashMap<>();
                map.put("fromDate", binding.ownerPayFeeFromDate.getText().toString());
                map.put("toDate", binding.ownerPayFeeToDate.getText().toString());
                map.put("currentlyActive", "true");
                map.put("lastPaidAmount", binding.ownerPayFeeAmount.getText().toString());
                map.put("paymentDate", getTodayDate());
                database.getReference("Subscription").child(subscriptionId).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(OwnerPayFeeActivity.this, "Fee payment updated", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(OwnerPayFeeActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            }
        });
        userDeleteDialog.show();
    }

}