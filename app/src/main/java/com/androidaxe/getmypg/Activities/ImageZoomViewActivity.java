package com.androidaxe.getmypg.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.androidaxe.getmypg.R;
import com.androidaxe.getmypg.databinding.ActivityImageZoomViewBinding;
import com.bumptech.glide.Glide;

public class ImageZoomViewActivity extends AppCompatActivity {

    ActivityImageZoomViewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImageZoomViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setTitle(getIntent().getStringExtra("name"));

        String image = getIntent().getStringExtra("link");

        Glide.with(this).load(image).into(binding.zoomImageView);

    }
}