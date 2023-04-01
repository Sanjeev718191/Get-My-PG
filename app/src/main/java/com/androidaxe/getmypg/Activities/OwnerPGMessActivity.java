package com.androidaxe.getmypg.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.androidaxe.getmypg.Activities.OwnerPGMessFragment.DetailsFragment;
import com.androidaxe.getmypg.Activities.OwnerPGMessFragment.EditFragment;
import com.androidaxe.getmypg.Activities.OwnerPGMessFragment.MyCustomersFragment;
import com.androidaxe.getmypg.Activities.OwnerPGMessFragment.RoomsDetailsFragment;
import com.androidaxe.getmypg.R;
import com.androidaxe.getmypg.databinding.ActivityOwnerPgmessBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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
                } else if(id == R.id.navigation_pgmess_rooms){
                    loadFragment(new RoomsDetailsFragment(type, id1, OwnerPGMessActivity.this), false);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.qr_scanner_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.qr_scan:
                Intent intent = new Intent(OwnerPGMessActivity.this, ScanQRCodeActivity.class);
                intent.putExtra("id", id1);
                intent.putExtra("name", name);
                intent.putExtra("type", type);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}