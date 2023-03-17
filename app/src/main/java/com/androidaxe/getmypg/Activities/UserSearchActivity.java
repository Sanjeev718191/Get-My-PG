package com.androidaxe.getmypg.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Context;
import android.os.Binder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.androidaxe.getmypg.Adapters.UserProductAdapter;
import com.androidaxe.getmypg.Module.OwnerMess;
import com.androidaxe.getmypg.Module.OwnerPG;
import com.androidaxe.getmypg.R;
import com.androidaxe.getmypg.databinding.ActivityUserSearchBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.SimpleOnSearchActionListener;

import java.util.ArrayList;
import java.util.List;

public class UserSearchActivity extends AppCompatActivity {

    ActivityUserSearchBinding binding;
    String category = "";
    String location = "";
    FirebaseDatabase database;
    List<OwnerMess> mess;
    List<OwnerPG> pg;
    UserProductAdapter adapter;
    GridLayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserSearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        mess = new ArrayList<>();
        pg = new ArrayList<>();
        layoutManager = new GridLayoutManager(this, 2);
        binding.userSearchProductList.setLayoutManager(layoutManager);

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

    }

    private void search(String query){
        if(category.equals("PGs")){
            database.getReference(category).orderByChild("search").startAt(query).endAt(query+"\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    pg.clear();
                    for(DataSnapshot ds : snapshot.getChildren()){
                        OwnerPG curr = ds.getValue(OwnerPG.class);
                        String currLocation = curr.getLocality()+", "+curr.getCity()+", "+curr.getState()+", "+curr.getPin();
                        currLocation = currLocation.toLowerCase();
                        if(currLocation.toLowerCase().indexOf(location) >= 0){
                            pg.add(curr);
                            Toast.makeText(UserSearchActivity.this, currLocation.indexOf(location)+" ", Toast.LENGTH_SHORT).show();
                        }
                    }
                    adapter = new UserProductAdapter((Context) UserSearchActivity.this, (ArrayList<OwnerPG>) pg);
                    binding.userSearchProductList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else if(category.equals("Mess")){
            database.getReference(category).orderByChild("search").startAt(query).endAt(query+"\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mess.clear();
                    for(DataSnapshot ds : snapshot.getChildren()){
                        OwnerMess curr = ds.getValue(OwnerMess.class);
                        String currLocation = curr.getLocality()+", "+curr.getCity()+", "+curr.getState()+", "+curr.getPin();
                        currLocation = currLocation.toLowerCase();
                        if(currLocation.contains(location)){
                            mess.add(curr);
                            Toast.makeText(UserSearchActivity.this, curr.getName(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    adapter = new UserProductAdapter((Context) UserSearchActivity.this, (ArrayList<OwnerMess>) mess, false);
                    binding.userSearchProductList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}