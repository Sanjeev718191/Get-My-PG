package com.androidaxe.getmypg.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewbinding.ViewBinding;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.androidaxe.getmypg.Activities.OwnerPGMessFragment.ChatFragment;
import com.androidaxe.getmypg.Activities.OwnerPGMessFragment.DetailsFragment;
import com.androidaxe.getmypg.Activities.OwnerPGMessFragment.EditFragment;
import com.androidaxe.getmypg.Activities.OwnerPGMessFragment.MyCustomersFragment;
import com.androidaxe.getmypg.Module.OwnerMess;
import com.androidaxe.getmypg.Module.OwnerPG;
import com.androidaxe.getmypg.R;
import com.androidaxe.getmypg.databinding.ActivityOwnerPgmessBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.imaginativeworld.whynotimagecarousel.listener.CarouselListener;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;

public class OwnerPGMessActivity extends AppCompatActivity {

    ActivityOwnerPgmessBinding binding;
    String type, id1, name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOwnerPgmessBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        type = getIntent().getStringExtra("type");
        id1 = getIntent().getStringExtra("id");
        name = getIntent().getStringExtra("name");
        getSupportActionBar().setTitle(name);

        binding.ownerPGMessBottomNavigationView.setSelectedItemId(R.id.navigation_pgmess_details);
        loadFragment(new DetailsFragment(type, id1, OwnerPGMessActivity.this), true);
        binding.ownerPGMessBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.navigation_pgmess_details){
                    loadFragment(new DetailsFragment(type, id1, OwnerPGMessActivity.this), false);
                } else if(id == R.id.navigation_pgmess_edit){
                    loadFragment(new EditFragment(type, id1, OwnerPGMessActivity.this), false);
                } else if(id == R.id.navigation_pgmess_customers){
                    loadFragment(new MyCustomersFragment(type, id1, OwnerPGMessActivity.this), false);
                } else if(id == R.id.navigation_pgmess_chat){
                    loadFragment(new ChatFragment(), false);
                }
                return true;
            }
        });
        loadFragment(new DetailsFragment(type, id1, OwnerPGMessActivity.this), true);

    }


    public void loadFragment(Fragment fragment, boolean flag){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if(flag) ft.add(R.id.owner_pgmess_frame_layout, fragment);
        else ft.replace(R.id.owner_pgmess_frame_layout, fragment);
        ft.commit();
    }

}