package com.androidaxe.getmypg.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

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
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle(getIntent().getStringExtra("name"));

        String image = getIntent().getStringExtra("link");
        if(image != null && !image.equals("na"))
            Glide.with(this).load(image).into(binding.zoomImageView);

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

}