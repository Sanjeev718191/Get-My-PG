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

import com.androidaxe.getmypg.Module.OwnerMess;
import com.androidaxe.getmypg.Module.OwnerPG;
import com.androidaxe.getmypg.R;
import com.androidaxe.getmypg.databinding.ActivityProductMessDetailBinding;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.imaginativeworld.whynotimagecarousel.listener.CarouselListener;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;

public class ProductMessDetailActivity extends AppCompatActivity {

    ActivityProductMessDetailBinding binding;

    String messid;
    OwnerMess mess;
    FirebaseDatabase database;
    FirebaseAuth auth;
    boolean flag;
    int count;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductMessDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading Info...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        database = FirebaseDatabase.getInstance();
        messid = getIntent().getStringExtra("id");
        auth = FirebaseAuth.getInstance();
        binding.productMessCarousel.setScaleOnScroll(true);
        flag = false;
        database.getReference("Mess").child(messid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mess = snapshot.getValue(OwnerMess.class);
                binding.productMessCarousel.addData(new CarouselItem(mess.getImage()));
                binding.productMessCarousel.addData(new CarouselItem(mess.getMenu()));
                binding.productMessTitle.setText(mess.getName());
                binding.productMessDescription.setText(mess.getDescription());
                binding.productMessPrice.setText("Rs. "+mess.getFeeMonthly());
                binding.productMessLocation.setText(mess.getLocality()+", "+mess.getCity()+", "+mess.getState()+" "+mess.getPin());
                binding.productMessOwnerContact.setText(mess.getContact());
                database.getReference("UserSubscription").child("UserMess").child(auth.getUid()).addValueEventListener(new ValueEventListener() {
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
                                        if(checkId.equals(messid)){
                                            deactivateButton();
                                            progressDialog.dismiss();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        progressDialog.dismiss();
                                        Toast.makeText(ProductMessDetailActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                                count++;
                                if(flag || count == snapshot.getChildrenCount()){
                                    if(mess.getStopRequests().equals("true")) deactivateButton();
                                    if(!flag){
                                        activateButton();
                                    }
                                    progressDialog.dismiss();
                                    break;
                                }
                            }
                        } else {
                            activateButton();
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();
                        Toast.makeText(ProductMessDetailActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(ProductMessDetailActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        binding.productMessCarousel.setCarouselListener(new CarouselListener() {
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
                String Image;
                if(i == 0){
                    Image = mess.getImage();
                } else {
                    Image = mess.getMenu();
                }
                if(Image != null && !Image.equals("na")){
                    Intent intent = new Intent(ProductMessDetailActivity.this, ImageZoomViewActivity.class);
                    intent.putExtra("name", mess.getName());
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
        binding.productMessSubscribe.setBackgroundDrawable(getDrawable(R.drawable.button_deactive_background));
        binding.productMessSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mess.getStopRequests().equals("false"))
                    Toast.makeText(ProductMessDetailActivity.this, "You are already in.", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(ProductMessDetailActivity.this, "Seller is not accepting requests.\nPlease contact seller for more info.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void activateButton(){
        binding.productMessSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductMessDetailActivity.this, CheckOutActivity.class);
                intent.putExtra("id", messid);
                intent.putExtra("type", "mess");
                intent.putExtra("new", "yes");
                startActivity(intent);
            }
        });
    }
}