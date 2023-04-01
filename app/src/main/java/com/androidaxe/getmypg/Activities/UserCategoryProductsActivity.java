package com.androidaxe.getmypg.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.androidaxe.getmypg.Adapters.UserProductAdapter;
import com.androidaxe.getmypg.Module.OwnerMess;
import com.androidaxe.getmypg.Module.OwnerPG;
import com.androidaxe.getmypg.databinding.ActivityUserCategoryProductsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UserCategoryProductsActivity extends AppCompatActivity {

    ActivityUserCategoryProductsBinding binding;
    String type, address, city, country, postelCode;
    FusedLocationProviderClient fusedLocationProviderClient;
    private final static int REQUEST_CODE = 100;
    ProgressDialog progressDialog;
    FirebaseDatabase database;
    List<OwnerMess> mess;
    List<OwnerPG> pg;
    UserProductAdapter adapter;
    GridLayoutManager layoutManager;
    String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserCategoryProductsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading data...");
        progressDialog.setCanceledOnTouchOutside(false);

        type = getIntent().getStringExtra("type");
        address = "";
        city = "";
        country = "";
        postelCode = "";
        database = FirebaseDatabase.getInstance();
        mess = new ArrayList<>();
        pg = new ArrayList<>();
        layoutManager = new GridLayoutManager(this, 2);
        binding.userCategoryProductsList.setLayoutManager(layoutManager);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if(type.equals("pg")){
            getSupportActionBar().setTitle("Hostel/PG near me");
            binding.userCategoryProductsNotAvailable.setText("Sorry no Hostel/PG available near you");
            category = "PGs";
        } else {
            getSupportActionBar().setTitle("Mess near me");
            binding.userCategoryProductsNotAvailable.setText("Sorry no mess available near you");
            category = "Mess";
        }
        getUserCurrentLocation();

    }

    private void getUserCurrentLocation(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            progressDialog.show();
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location != null){
                        Geocoder geocoder = new Geocoder(UserCategoryProductsActivity.this, Locale.getDefault());
                        List<Address> addresses = null;

                        try {
                            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            address = addresses.get(0).getAddressLine(0);
                            city = addresses.get(0).getAddressLine(0);
                            country = addresses.get(0).getCountryName();
                            postelCode = addresses.get(0).getPostalCode();

                            if(type.equals("pg")){
                                getPGNearMe();
                            } else {
                                getMessNearMe();
                            }

                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    } else{
                        Toast.makeText(UserCategoryProductsActivity.this, "Unable to get your location", Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                }
            });
        } else {
            askLocationPermission();
        }
    }

    private void askLocationPermission(){
        ActivityCompat.requestPermissions(UserCategoryProductsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getUserCurrentLocation();
            } else {
                Toast.makeText(this, "Please provide the required permission", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void getPGNearMe(){
        database.getReference(category).orderByChild("pin").startAt(postelCode).endAt(postelCode+"\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pg.clear();
                if(snapshot.getChildrenCount() > 0){
                    for(DataSnapshot ds : snapshot.getChildren()){
                        OwnerPG curr = ds.getValue(OwnerPG.class);
                        pg.add(curr);
                    }
                    adapter = new UserProductAdapter((Context) UserCategoryProductsActivity.this, (ArrayList<OwnerPG>) pg);
                    binding.userCategoryProductsList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    binding.userCategoryProductsLocation.setVisibility(View.GONE);
                    binding.userCategoryProductsList.setVisibility(View.GONE);
                    binding.userCategoryProductsLocation.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMessNearMe(){
        database.getReference(category).orderByChild("pin").startAt(postelCode).endAt(postelCode+"\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getChildrenCount() > 0) {
                    mess.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        OwnerMess curr = ds.getValue(OwnerMess.class);
                        mess.add(curr);
                    }
                    adapter = new UserProductAdapter((Context) UserCategoryProductsActivity.this, (ArrayList<OwnerMess>) mess, false);
                    binding.userCategoryProductsList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    binding.userCategoryProductsLocation.setVisibility(View.GONE);
                    binding.userCategoryProductsList.setVisibility(View.GONE);
                    binding.userCategoryProductsLocation.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}