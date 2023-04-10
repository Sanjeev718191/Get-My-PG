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
import android.os.Binder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.androidaxe.getmypg.Adapters.UserProductAdapter;
import com.androidaxe.getmypg.Module.OwnerMess;
import com.androidaxe.getmypg.Module.OwnerPG;
import com.androidaxe.getmypg.R;
import com.androidaxe.getmypg.databinding.ActivityUserSearchBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.SimpleOnSearchActionListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UserSearchActivity extends AppCompatActivity {

    ActivityUserSearchBinding binding;
    String category = "";
    String location = "";
    FirebaseDatabase database;
    List<OwnerMess> mess;
    List<OwnerPG> pg;
    UserProductAdapter adapter;
    GridLayoutManager layoutManager;
    boolean getLocationClicked = false;
    String currLocation = "", postelCode = "", city = "";
    ProgressDialog progressDialog;
    FusedLocationProviderClient fusedLocationProviderClient;
    private final static int REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserSearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading data...");
        progressDialog.setCanceledOnTouchOutside(false);

        database = FirebaseDatabase.getInstance();
        mess = new ArrayList<>();
        pg = new ArrayList<>();
        layoutManager = new GridLayoutManager(this, 2);
        binding.userSearchProductList.setLayoutManager(layoutManager);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        binding.userSearchRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == R.id.user_search_pg){
                    category = "PGs";
                } else if(i == R.id.user_search_mess) {
                    category = "Mess";
                }
            }
        });

        binding.userSearchBar.setOnSearchActionListener(new SimpleOnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                super.onSearchStateChanged(enabled);
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                if(category.equals("")){
                    Toast.makeText(UserSearchActivity.this, "Please select Category", Toast.LENGTH_SHORT).show();
                } else if(location.equals("")) {
                    Toast.makeText(UserSearchActivity.this, "Please enter Location", Toast.LENGTH_SHORT).show();
                } else {
                    //location = binding.userLocationEditText.getText().toString().toLowerCase();
                    search(text.toString().toLowerCase());
                }
            }


        });



//        binding.userLocationEditText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                binding.userSearchGetCurrentLocationButton.setVisibility(View.VISIBLE);
//            }
//        });

        binding.userLocationEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                location = charSequence.toString().toLowerCase();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.userLocationEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                    if(category.equals("")){
                        Toast.makeText(UserSearchActivity.this, "Please select Category", Toast.LENGTH_SHORT).show();
                    } else if(location.equals("")) {
                        Toast.makeText(UserSearchActivity.this, "Please enter Location", Toast.LENGTH_SHORT).show();
                    } else if(binding.userSearchBar.getText().equals("")){
                        Toast.makeText(UserSearchActivity.this, "Please enter name", Toast.LENGTH_SHORT).show();
                    } else {
                        search(binding.userSearchBar.getText());
                    }
                    return true;
                }
                return false;
            }
        });

        binding.userSearchGetCurrentLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUserCurrentLocation();
            }
        });

    }

    private void getUserCurrentLocation(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            progressDialog.show();
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location != null){
                        Geocoder geocoder = new Geocoder(UserSearchActivity.this, Locale.getDefault());
                        List<Address> addresses = null;

                        try {
                            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            currLocation = addresses.get(0).getAddressLine(0);
                            city = addresses.get(0).getLocality();
                            postelCode = addresses.get(0).getPostalCode();
                            binding.userLocationEditText.setText(currLocation);
                            getLocationClicked = true;
                            binding.userSearchGetCurrentLocationButton.setImageDrawable(getDrawable(R.drawable.baseline_my_location_dark));

                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    } else{
                        Toast.makeText(UserSearchActivity.this, "Unable to get your location", Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                }
            });
        } else {
            askLocationPermission();
        }
    }

    private void askLocationPermission(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
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



    private void search(String query){
        if(getLocationClicked){

            database.getReference(category).orderByChild("pin").startAt(postelCode).endAt(postelCode+"\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.getChildrenCount() > 0){

                        if(category.equals("PGs")){
                            pg.clear();
                            for(DataSnapshot ds : snapshot.getChildren()){
                                OwnerPG curr = ds.getValue(OwnerPG.class);
                                if(curr.getSearch().indexOf(query) >= 0) {
                                    pg.add(curr);
                                }
                            }
                            if(pg.size() > 0){
                                binding.userSearchProductList.setVisibility(View.VISIBLE);
                                binding.userSearchNoDataText.setVisibility(View.GONE);
                                adapter = new UserProductAdapter((Context) UserSearchActivity.this, (ArrayList<OwnerPG>) pg);
                                binding.userSearchProductList.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            } else {
                                binding.userSearchProductList.setVisibility(View.GONE);
                                binding.userSearchNoDataText.setVisibility(View.VISIBLE);
                            }
                        } else {
                            mess.clear();
                            for(DataSnapshot ds : snapshot.getChildren()){
                                OwnerMess curr = ds.getValue(OwnerMess.class);
                                if(curr.getSearch().indexOf(query) >= 0) {
                                    mess.add(curr);
                                }
                            }
                            if(mess.size() > 0){
                                binding.userSearchProductList.setVisibility(View.VISIBLE);
                                binding.userSearchNoDataText.setVisibility(View.GONE);
                                adapter = new UserProductAdapter((Context) UserSearchActivity.this, (ArrayList<OwnerMess>) mess, false);
                                binding.userSearchProductList.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            } else {
                                binding.userSearchProductList.setVisibility(View.GONE);
                                binding.userSearchNoDataText.setVisibility(View.VISIBLE);
                            }
                        }

                    } else {
                        binding.userSearchProductList.setVisibility(View.GONE);
                        binding.userSearchNoDataText.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        else if(category.equals("PGs")){
            database.getReference(category).orderByChild("search").startAt(query).endAt(query+"\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.getChildrenCount() > 0) {
                        pg.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            OwnerPG curr = ds.getValue(OwnerPG.class);
                            String currLocation = curr.getLocality() + ", " + curr.getCity() + ", " + curr.getState() + ", " + curr.getPin();
                            currLocation = currLocation.toLowerCase();
                            if (currLocation.toLowerCase().indexOf(location) >= 0) {
                                pg.add(curr);
//                            Toast.makeText(UserSearchActivity.this, currLocation.indexOf(location)+" ", Toast.LENGTH_SHORT).show();
                            }
                        }
                        if(pg.size() > 0){
                            binding.userSearchProductList.setVisibility(View.VISIBLE);
                            binding.userSearchNoDataText.setVisibility(View.GONE);
                            adapter = new UserProductAdapter((Context) UserSearchActivity.this, (ArrayList<OwnerPG>) pg);
                            binding.userSearchProductList.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        } else {
                            binding.userSearchProductList.setVisibility(View.GONE);
                            binding.userSearchNoDataText.setVisibility(View.VISIBLE);
                        }
                    } else {
                        binding.userSearchProductList.setVisibility(View.GONE);
                        binding.userSearchNoDataText.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else if(category.equals("Mess")){
            database.getReference(category).orderByChild("search").startAt(query).endAt(query+"\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.getChildrenCount() > 0){
                        mess.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            OwnerMess curr = ds.getValue(OwnerMess.class);
                            String currLocation = curr.getLocality() + ", " + curr.getCity() + ", " + curr.getState() + ", " + curr.getPin();
                            currLocation = currLocation.toLowerCase();
                            if (currLocation.contains(location)) {
                                mess.add(curr);
                            }
                        }
                        if(mess.size() > 0){
                            binding.userSearchProductList.setVisibility(View.VISIBLE);
                            binding.userSearchNoDataText.setVisibility(View.GONE);
                            adapter = new UserProductAdapter((Context) UserSearchActivity.this, (ArrayList<OwnerMess>) mess, false);
                            binding.userSearchProductList.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        } else {
                            binding.userSearchProductList.setVisibility(View.GONE);
                            binding.userSearchNoDataText.setVisibility(View.VISIBLE);
                        }
                    }  else {
                        binding.userSearchProductList.setVisibility(View.GONE);
                        binding.userSearchNoDataText.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}