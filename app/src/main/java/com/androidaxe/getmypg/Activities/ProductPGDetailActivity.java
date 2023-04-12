package com.androidaxe.getmypg.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.androidaxe.getmypg.Module.AdditionalImages;
import com.androidaxe.getmypg.Module.OwnerPG;
import com.androidaxe.getmypg.R;
import com.androidaxe.getmypg.databinding.ActivityProductPgdetailBinding;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.imaginativeworld.whynotimagecarousel.listener.CarouselListener;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;

public class ProductPGDetailActivity extends AppCompatActivity {

    ActivityProductPgdetailBinding binding;
    String pgid;
    OwnerPG pg;
    FirebaseDatabase database;
    FirebaseAuth auth;
    boolean flag;
    int count;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductPgdetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading Info...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        database = FirebaseDatabase.getInstance();
        pgid = getIntent().getStringExtra("id");
        auth = FirebaseAuth.getInstance();
        flag = false;

        database.getReference("PGs").child(pgid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pg = snapshot.getValue(OwnerPG.class);
                if(pg.getImage() != null) {
                    binding.productPgImageCarousel.addData(new CarouselItem(pg.getImage()));
                }
                binding.productPgTitle.setText(pg.getName());
                binding.productPgDescription.setText(pg.getDescription());
                binding.productPgSingleSeaterPrice.setText("Rs. "+pg.getSeater1());
                binding.productPgDoubleSeaterPrice.setText("Rs. "+pg.getSeater2());
                binding.productPgTripleSeaterPrice.setText("Rs. "+pg.getSeater3());
                binding.productPgElectricity.setText(pg.getElectricityBill());
                binding.productPgLocation.setText(pg.getLocality()+", "+pg.getCity()+", "+pg.getState()+" "+pg.getPin());
                binding.productPgOwnerContact.setText(pg.getContact());

                database.getReference("UserSubscription").child("UserPG").child(auth.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.getChildrenCount() > 0){
                            count = 0;
                            for(DataSnapshot ds : snapshot.getChildren()){
                                String id = ds.getValue(String.class);
                                database.getReference("Subscription").child(id).child("PGMessId").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String checkId = snapshot.getValue(String.class);
                                        if(checkId.equals(pgid)){
                                            deactivateButton();
                                            progressDialog.dismiss();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        progressDialog.dismiss();
                                        Toast.makeText(ProductPGDetailActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                                count++;
                                if(flag || count == snapshot.getChildrenCount()){
                                    if(pg.getStopRequests().equals("true")) deactivateButton();
                                    else if(!flag){
                                        activateButton();
                                    }
                                    progressDialog.dismiss();
                                    break;
                                }
                            }
                        } else {
                            if(pg.getStopRequests().equals("true")) deactivateButton();
                            else activateButton();
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();
                        Toast.makeText(ProductPGDetailActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                database.getReference("BusinessAdditionalImages").child(pg.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        AdditionalImages additionalImages = snapshot.getValue(AdditionalImages.class);
                        if(additionalImages != null){
                            if(additionalImages.getImg1() != null){
                                binding.productPgImageCarousel.addData(new CarouselItem(additionalImages.getImg1()));
                            }
                            if(additionalImages.getImg2() != null){
                                binding.productPgImageCarousel.addData(new CarouselItem(additionalImages.getImg2()));
                            }
                            if(additionalImages.getImg3() != null){
                                binding.productPgImageCarousel.addData(new CarouselItem(additionalImages.getImg3()));
                            }
                            if(additionalImages.getImg4() != null){
                                binding.productPgImageCarousel.addData(new CarouselItem(additionalImages.getImg4()));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(ProductPGDetailActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        binding.productPgImageCarousel.setCarouselListener(new CarouselListener() {
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
                    Intent intent = new Intent(ProductPGDetailActivity.this, ImageZoomViewActivity.class);
                    intent.putExtra("name", pg.getName());
                    intent.putExtra("link", Image);
                    startActivity(intent);
                }
            }

            @Override
            public void onLongClick(int i, @NonNull CarouselItem carouselItem) {

            }
        });


    }

    private void deactivateButton(){
        flag = true;
        binding.productPgSubscribe.setBackgroundDrawable(getDrawable(R.drawable.button_deactive_background));
        binding.productPgSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pg.getStopRequests().equals("false"))
                    Toast.makeText(ProductPGDetailActivity.this, "You are already in.", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(ProductPGDetailActivity.this, "Seller is not accepting requests.\nPlease contact seller for more info.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void activateButton(){
        binding.productPgSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductPGDetailActivity.this, CheckOutActivity.class);
                intent.putExtra("id", pgid);
                intent.putExtra("type", "pg");
                intent.putExtra("new", "yes");
                startActivity(intent);
            }
        });
    }

}