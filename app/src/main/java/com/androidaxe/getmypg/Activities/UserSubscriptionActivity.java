package com.androidaxe.getmypg.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.androidaxe.getmypg.Module.AdditionalImages;
import com.androidaxe.getmypg.Module.OwnerMess;
import com.androidaxe.getmypg.Module.OwnerPG;
import com.androidaxe.getmypg.Module.UserSubscribedItem;
import com.androidaxe.getmypg.R;
import com.androidaxe.getmypg.databinding.ActivityUserSubscriptionBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.imaginativeworld.whynotimagecarousel.listener.CarouselListener;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class UserSubscriptionActivity extends AppCompatActivity {

    ActivityUserSubscriptionBinding binding;
    FirebaseDatabase database;
    UserSubscribedItem subscription;
    OwnerPG pg;
    OwnerMess mess;
    String id, type;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserSubscriptionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading Info...");
        progressDialog.setCanceledOnTouchOutside(false);
        id = getIntent().getStringExtra("id");
        type = getIntent().getStringExtra("type");
        database = FirebaseDatabase.getInstance();

        progressDialog.show();
        database.getReference("Subscription").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                subscription = snapshot.getValue(UserSubscribedItem.class);
                if(type.equals("pg")){
                    database.getReference("PGs").child(subscription.getPGMessId()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            pg = snapshot.getValue(OwnerPG.class);
                            setUserData();
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            progressDialog.dismiss();
                            Toast.makeText(UserSubscriptionActivity.this, "Data Not found", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    database.getReference("Mess").child(subscription.getPGMessId()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            mess = snapshot.getValue(OwnerMess.class);
                            setUserData();
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            progressDialog.dismiss();
                            Toast.makeText(UserSubscriptionActivity.this, "Data Not found", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(UserSubscriptionActivity.this, "Data Not found", Toast.LENGTH_SHORT).show();
            }
        });

        binding.userSubscriptionProductDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(type.equals("pg")) {
                    Intent intent = new Intent(UserSubscriptionActivity.this, ProductPGDetailActivity.class);
                    intent.putExtra("id", subscription.getPGMessId());
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(UserSubscriptionActivity.this, ProductMessDetailActivity.class);
                    intent.putExtra("id", subscription.getPGMessId());
                    startActivity(intent);
                }
            }
        });

        binding.userSubscriptionImageCarousel.setCarouselListener(new CarouselListener() {
            @Nullable
            @Override
            public ViewBinding onCreateViewHolder(@NonNull LayoutInflater layoutInflater, @NonNull ViewGroup viewGroup) {
                return null;
            }

            @Override
            public void onBindViewHolder(@NonNull ViewBinding viewBinding, @NonNull CarouselItem carouselItem, int i) {

            }

            @Override
            public void onClick(int i, @NonNull CarouselItem carouselItem) {
                String Image = carouselItem.getImageUrl();
                if(Image != null && !Image.equals("na")){
                    Intent intent = new Intent(UserSubscriptionActivity.this, ImageZoomViewActivity.class);
                    intent.putExtra("name", pg != null ? pg.getName() : mess.getName());
                    intent.putExtra("link", Image);
                    startActivity(intent);
                }
            }

            @Override
            public void onLongClick(int i, @NonNull CarouselItem carouselItem) {

            }
        });

        binding.userSubscriptionViewMessMenuPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mess != null && mess.getMenuPDF() != null && !mess.getMenuPDF().equals("na")){
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(mess.getMenuPDF()));
                    startActivity(intent);
                } else {
                    Toast.makeText(UserSubscriptionActivity.this, "Seller not added menu.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.qr_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.qrShow:
                Intent intent = new Intent(UserSubscriptionActivity.this, ShowQRCodeActivity.class);
                intent.putExtra("url", subscription.getQrCode());
                intent.putExtra("name", type.equals("pg") ? pg.getName() : mess.getName());
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setUserData(){
        if(type.equals("pg")){
            getSupportActionBar().setTitle(pg.getName());
            binding.userSubscriptionDetails.setText("Your Hostel/Pg Subscription Details");
            if(subscription.getNotice().equals("na")){
                binding.userSubscriptionNotice.setVisibility(View.GONE);
            } else {
                binding.userSubscriptionNotice.setText("Notice : "+subscription.getNotice());
            }
            if(pg.getImage() != null) {
                binding.userSubscriptionImageCarousel.addData(new CarouselItem(pg.getImage()));
            }
            binding.userSubscriptionNote.setText(subscription.getNote());
            binding.userSubscriptionFromDate.setText("From date : "+subscription.getFromDate());
            binding.userSubscriptionToDate.setText("To date : "+subscription.getToDate());
            if(subscription.getLastPaidAmount().equals("na")) {
                binding.userSubscriptionLastPaidAmount.setText("Last payment amount : " + subscription.getLastPaidAmount());
                binding.userSubscriptionProgressBar.setMax(100);
                binding.userSubscriptionProgressBar.setProgress(0);
                binding.userSubscriptionProgressBar.setAnimDuration(1600);
                binding.userSubscriptionRemainingDays.setText("Status : Not Active Yet contact Seller");
            }
            else {
                binding.userSubscriptionLastPaidAmount.setText("Last payment amount : Rs." + subscription.getLastPaidAmount());
                setRemaningDaysInfo();
            }
            binding.userSubscriptionLastPaidDate.setText("On date : "+subscription.getPaymentDate());
            binding.userSubscriptionPrice.setText("Price : Rs."+subscription.getPrice());
            binding.userSubscriptionRoomType.setText("Room type : "+subscription.getRoomType());
            binding.userSubscriptionRoomNumber.setText("Room No. : "+subscription.getRoomNumber());
            binding.userSubscriptionProductDetailsButton.setText("View Hostel/PG details");
            binding.userSubscriptionSellerContact.setText("Seller Contact No. : "+pg.getContact());
            database.getReference("BusinessAdditionalImages").child(pg.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    AdditionalImages additionalImages = snapshot.getValue(AdditionalImages.class);
                    if(additionalImages != null){
                        if(additionalImages.getImg1() != null){
                            binding.userSubscriptionImageCarousel.addData(new CarouselItem(additionalImages.getImg1()));
                        }
                        if(additionalImages.getImg2() != null){
                            binding.userSubscriptionImageCarousel.addData(new CarouselItem(additionalImages.getImg2()));
                        }
                        if(additionalImages.getImg3() != null){
                            binding.userSubscriptionImageCarousel.addData(new CarouselItem(additionalImages.getImg3()));
                        }
                        if(additionalImages.getImg4() != null){
                            binding.userSubscriptionImageCarousel.addData(new CarouselItem(additionalImages.getImg4()));
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            binding.userSubscriptionRoomNumber.setVisibility(View.GONE);
            binding.userSubscriptionRoomType.setVisibility(View.GONE);
            binding.userSubscriptionViewMessMenuPdf.setVisibility(View.VISIBLE);
            getSupportActionBar().setTitle(mess.getName());
            binding.userSubscriptionDetails.setText("Your Mess Subscription Details");
            if(subscription.getNotice().equals("na")){
                binding.userSubscriptionNotice.setVisibility(View.GONE);
            } else {
                binding.userSubscriptionNotice.setText("Notice : "+subscription.getNotice());
            }
            if(mess.getImage() != null) {
                binding.userSubscriptionImageCarousel.addData(new CarouselItem(mess.getImage()));
            }
            if(mess.getMenu() != null) {
                binding.userSubscriptionImageCarousel.addData(new CarouselItem(mess.getMenu()));
            }
            binding.userSubscriptionNote.setText(subscription.getNote());
            binding.userSubscriptionFromDate.setText("From date : "+subscription.getFromDate());
            binding.userSubscriptionToDate.setText("To date : "+subscription.getToDate());
            if(subscription.getLastPaidAmount().equals("na")) {
                binding.userSubscriptionLastPaidAmount.setText("Last payment amount : " + subscription.getLastPaidAmount());
                binding.userSubscriptionProgressBar.setMax(100);
                binding.userSubscriptionProgressBar.setProgress(0);
                binding.userSubscriptionProgressBar.setAnimDuration(1600);
                binding.userSubscriptionRemainingDays.setText("Status : Not Active Yet contact Seller");
            }
            else {
                binding.userSubscriptionLastPaidAmount.setText("Last payment amount : Rs." + subscription.getLastPaidAmount());
                setRemaningDaysInfo();
            }
            binding.userSubscriptionLastPaidDate.setText("On date : "+subscription.getPaymentDate());
            binding.userSubscriptionPrice.setText("Price : Rs."+subscription.getPrice());
            binding.userSubscriptionProductDetailsButton.setText("View Mess details");
            binding.userSubscriptionSellerContact.setText("Seller Contact No. : "+mess.getContact());
            database.getReference("BusinessAdditionalImages").child(mess.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    AdditionalImages additionalImages = snapshot.getValue(AdditionalImages.class);
                    if(additionalImages != null){
                        if(additionalImages.getImg1() != null){
                            binding.userSubscriptionImageCarousel.addData(new CarouselItem(additionalImages.getImg1()));
                        }
                        if(additionalImages.getImg2() != null){
                            binding.userSubscriptionImageCarousel.addData(new CarouselItem(additionalImages.getImg2()));
                        }
                        if(additionalImages.getImg3() != null){
                            binding.userSubscriptionImageCarousel.addData(new CarouselItem(additionalImages.getImg3()));
                        }
                        if(additionalImages.getImg4() != null){
                            binding.userSubscriptionImageCarousel.addData(new CarouselItem(additionalImages.getImg4()));
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
    private void setRemaningDaysInfo(){
        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        try {
            Date currDate = sdf.parse(date);
            Date toDate = sdf.parse(subscription.getToDate());
            Date fromDate = sdf.parse(subscription.getFromDate());
            long diffInMillies = Math.abs(toDate.getTime() - currDate.getTime());
            long remainingDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            diffInMillies = Math.abs(toDate.getTime() - fromDate.getTime());
            long totalDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            if(remainingDays > 3){
                binding.userSubscriptionProgressBar.setMax((int)totalDays);
                binding.userSubscriptionProgressBar.setProgress((int)remainingDays);
                binding.userSubscriptionProgressBar.setAnimDuration(1600);
                binding.userSubscriptionRemainingDays.setText("Status : "+remainingDays+" days remain to end Subscription");
            } else if(remainingDays >= 0) {
                binding.userSubscriptionProgressBar.setMax((int)totalDays);
                binding.userSubscriptionProgressBar.setProgress((int)remainingDays);
                binding.userSubscriptionProgressBar.setAnimDuration(1600);
                binding.userSubscriptionRemainingDays.setText("Status : "+remainingDays+" days remain to end Subscription contact seller to renew subscription.");
                binding.userSubscriptionRemainingDays.setTextColor(Color.RED);
            } else {
                binding.userSubscriptionProgressBar.setProgressTipColor(Color.RED);
                binding.userSubscriptionProgressBar.setMax(100);
                binding.userSubscriptionProgressBar.setProgress(0);
                binding.userSubscriptionProgressBar.setAnimDuration(1600);
                binding.userSubscriptionRemainingDays.setText("Status : Subscription Expired contact seller to renew.");
                binding.userSubscriptionRemainingDays.setTextColor(Color.RED);
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }



}