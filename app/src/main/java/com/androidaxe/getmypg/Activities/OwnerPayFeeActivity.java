package com.androidaxe.getmypg.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.androidaxe.getmypg.Module.OwnerMess;
import com.androidaxe.getmypg.Module.OwnerPG;
import com.androidaxe.getmypg.Module.PGOwner;
import com.androidaxe.getmypg.Module.PGUser;
import com.androidaxe.getmypg.Module.UserSubscribedItem;
import com.androidaxe.getmypg.R;
import com.androidaxe.getmypg.databinding.ActivityOwnerPayFeeBinding;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Permission;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class OwnerPayFeeActivity extends AppCompatActivity {

    ActivityOwnerPayFeeBinding binding;
    DatePickerDialog datePickerDialog;
    FirebaseDatabase database;
    UserSubscribedItem item;
    PGUser user;
//    PGOwner owner;
//    OwnerPG pg;
//    OwnerMess mess;
    String subscriptionId;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOwnerPayFeeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Fee payment");

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

//                database.getReference("PGOwner").child(item.getOid()).addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        owner = snapshot.getValue(PGOwner.class);
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//                if(item.getType().equals("pg")){
//                    database.getReference("PGs").child(item.getPGMessId()).addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            pg = snapshot.getValue(O)
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });
//                } else {
//
//                }
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
                if(month <= 9) {
                    if(day > 9) button.setText(day + "-0" + month + "-" + year);
                    else button.setText("0"+day + "-0" + month + "-" + year);
                }
                else {
                    if(day > 9) button.setText(day + "-" + month + "-" + year);
                    else button.setText("0"+day + "-" + month + "-" + year);
                }
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
        if(day <= 9){
            if(month <= 9) return "0"+day+"-0"+month+"-"+year;
            else return "0"+day+"-"+month+"-"+year;
        } else {
            if(month <= 9) return day+"-0"+month+"-"+year;
            else return day+"-"+month+"-"+year;
        }
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
                if(item.getNotice().equals("Please Pay Fee to Continue"))
                    map.put("Notice", "na");
                database.getReference("Subscription").child(subscriptionId).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(OwnerPayFeeActivity.this, "Fee payment updated", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        finish();
//                        generateInvoice();
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    private void generateInvoice() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
//            && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//
//            PdfDocument myPdfDocument = new PdfDocument();
//            Paint paint = new Paint();
//            Paint forLinePaint = new Paint();
//            PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
//            PdfDocument.Page myPage = myPdfDocument.startPage(myPageInfo);
//            Canvas canvas = myPage.getCanvas();
//
//            RectF dst = new RectF(50, 50, 50 + 50, 50 + 50);
//            canvas.drawBitmap(bmp, null, dst, null);
//            canvas.drawBitmap(Bitmap.createScaledBitmap(bmp, 20, 20, false), 20,20, paint);
//
//            paint.setTextSize(14.5f);
//            paint.setColor(Color.rgb(0, 50, 250));
//
//            canvas.drawText("Hare Krishna Fuel Station", 20, 20, paint);
//            paint.setTextSize(8.5f);
//            canvas.drawText("Plot No. 2, Shri Bharat Marg", 20, 40, paint);
//            canvas.drawText("Ayodhya 224123", 20, 55, paint);
//            forLinePaint.setStyle(Paint.Style.STROKE);
//            forLinePaint.setPathEffect(new DashPathEffect(new float[]{5,5}, 0));
//            forLinePaint.setStrokeWidth(2);
//            canvas.drawLine(20, 65, 230, 65, forLinePaint);
//            canvas.drawText("Customer Name: "+editTextName.getText(), 20, 80, paint);
//            canvas.drawLine(20, 90, 230, 90, forLinePaint);
//            canvas.drawText("Purchase:", 20, 105, paint);
//            canvas.drawText(spinner.getSelectedItem().toString(), 20, 135, paint);
//            canvas.drawText(editTextQty.getText().toString()+ " ltr", 120, 135, paint);
//
//            double amount = itemPrice[spinner.getSelectedItemPosition()]*Double.parseDouble(editTextQty.getText().toString());
//
//            paint.setTextAlign(Paint.Align.RIGHT);
//            canvas.drawText(String.valueOf(decimalFormat.format(amount)), 230, 135, paint);
//            paint.setTextAlign(Paint.Align.LEFT);
//
//            canvas.drawText("+%", 20, 175, paint);
//            canvas.drawText("Tax 5%", 120, 175, paint);
//
//            paint.setTextAlign(Paint.Align.RIGHT);
//            canvas.drawText(String.valueOf(decimalFormat.format(amount*5/100)), 230, 175, paint);
//            paint.setTextAlign(Paint.Align.LEFT);
//
//            canvas.drawLine(20, 210, 230, 210, forLinePaint);
//
//            paint.setTextSize(10f);
//            canvas.drawText("Total", 120, 225, paint);
//
//            paint.setTextAlign(Paint.Align.RIGHT);
//            canvas.drawText(String.valueOf(decimalFormat.format((amount*5/100) + amount)), 230, 225, paint);
//            paint.setTextAlign(Paint.Align.LEFT);
//
//            paint.setTextSize(8.5f);
//
//            canvas.drawText("Date: "+datePatternFormat.format(new Date().getTime()), 20, 260, paint);
//
//            canvas.drawText(String.valueOf(invoiceNo+1), 20, 275, paint);
//
//            canvas.drawText("Payment method : Cash", 20, 290, paint);
//            paint.setTextAlign(Paint.Align.CENTER);
//            paint.setTextSize(12f);
//            canvas.drawText("Thank you!", canvas.getWidth()/2, 320, paint);
//
//            myPdfDocument.finishPage(myPage);
////        File file = new File(this.getExternalFilesDir("/"), "Hare Krishna Fuel.pdf");
//            String pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
//            File file = new File(pdfPath, "myPDF.pdf");
//
//            try {
//                myPdfDocument.writeTo(new FileOutputStream(file));
//            } catch (IOException e){
//                e.printStackTrace();
//            }
//
//            myPdfDocument.close();
//
//        } else {
//            askStoragePermission();
//        }
//    }
//
//    private void askStoragePermission(){
//        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//
//        if(requestCode == PackageManager.PERMISSION_GRANTED){
//            if(grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
//                generateInvoice();
//            } else {
//                Toast.makeText(this, "Please provide the required permission", Toast.LENGTH_SHORT).show();
//            }
//        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }

}