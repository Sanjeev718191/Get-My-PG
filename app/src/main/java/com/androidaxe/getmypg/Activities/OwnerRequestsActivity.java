package com.androidaxe.getmypg.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.androidaxe.getmypg.Adapters.OwnerRequestAdapter;
import com.androidaxe.getmypg.Adapters.UserRequestAdapter;
import com.androidaxe.getmypg.Module.Request;
import com.androidaxe.getmypg.R;
import com.androidaxe.getmypg.databinding.ActivityOwnerRequestsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.SimpleOnSearchActionListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class OwnerRequestsActivity extends AppCompatActivity {

    ActivityOwnerRequestsBinding binding;
    ArrayList<Request> requests;
    ArrayList<String> requestsIds;
    FirebaseAuth auth;
    FirebaseDatabase database;
    OwnerRequestAdapter adapter;
    LinearLayoutManager manager;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOwnerRequestsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading data...");
        progressDialog.setCanceledOnTouchOutside(false);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.VERTICAL);
        binding.ownerRequestList.setLayoutManager(manager);
        getAllRequests();

        binding.ownerRequestAll.setEnabled(false);

        binding.ownerRequestRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == R.id.owner_request_all){
                    adapter = new OwnerRequestAdapter(OwnerRequestsActivity.this, requests);
                    binding.ownerRequestList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else if(i == R.id.owner_request_accepted){
                    binding.ownerRequestAll.setEnabled(true);
                    getAcceptedRequests();
                } else if(i == R.id.owner_request_rejected){
                    binding.ownerRequestAll.setEnabled(true);
                    getRejectedRequests();
                } else if(i == R.id.owner_request_pending){
                    binding.ownerRequestAll.setEnabled(true);
                    getPendingRequests();
                } else if(i == R.id.owner_request_pg){
                    binding.ownerRequestAll.setEnabled(true);
                    getPgRequests();
                } else if(i == R.id.owner_request_mess){
                    binding.ownerRequestAll.setEnabled(true);
                    getMessRequests();
                }
            }
        });


        binding.ownerRequestsSearchBar.setOnSearchActionListener(new SimpleOnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                super.onSearchStateChanged(enabled);
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                search(text.toString().toLowerCase());
            }

            @Override
            public void onButtonClicked(int buttonCode) {
                super.onButtonClicked(buttonCode);
            }
        });

    }

    private void getAllRequests() {
        progressDialog.show();
        requestsIds = new ArrayList<>();
        requests = new ArrayList<>();
        database.getReference("Requests").child("OwnerPGRequests").child(auth.getUid()).child("requestIds").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getChildrenCount() > 0){
                    for(DataSnapshot ds : snapshot.getChildren()){
                        String id = ds.getValue(String.class);
                        requestsIds.add(id);
                        database.getReference("Requests").child("PGRequests").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Request request = snapshot.getValue(Request.class);
                                requests.add(request);
                                adapter = new OwnerRequestAdapter(OwnerRequestsActivity.this, requests);
                                binding.ownerRequestList.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                }
                database.getReference("Requests").child("OwnerMessRequests").child(auth.getUid()).child("requestIds").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.getChildrenCount() > 0){
                            for(DataSnapshot ds : snapshot.getChildren()){
                                String id = ds.getValue(String.class);
                                requestsIds.add(id);
                                database.getReference("Requests").child("MessRequests").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Request request = snapshot.getValue(Request.class);
                                        requests.add(request);
                                        adapter = new OwnerRequestAdapter(OwnerRequestsActivity.this, requests);
                                        binding.ownerRequestList.setAdapter(adapter);
                                        adapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();
                        Toast.makeText(OwnerRequestsActivity.this, "Data not found", Toast.LENGTH_SHORT).show();
                    }
                });
                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(OwnerRequestsActivity.this, "Data not found", Toast.LENGTH_SHORT).show();
            }
        });
    }
    ArrayList<Request> categoryRequests;
    private void getAcceptedRequests(){
        categoryRequests = new ArrayList<>();
        adapter = new OwnerRequestAdapter(OwnerRequestsActivity.this, categoryRequests);
        binding.ownerRequestList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        if(requests.size() == 0) return;
        for(Request request : requests){
            if(request.getStatus().equals("Accepted")){
                categoryRequests.add(request);
            }
        }
        Collections.sort(categoryRequests, new Comparator<Request>() {
            @Override
            public int compare(Request t1, Request t2) {
                Date d1 = null;
                Date d2 = null;
                try {
                    d1 = new SimpleDateFormat("dd-MM-yyyy").parse(t1.getDate());
                    d2 = new SimpleDateFormat("dd-MM-yyyy").parse(t2.getDate());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                if(d1 == null || d2 == null) return 0;
                return d2.compareTo(d1);
            }
        });
        adapter = new OwnerRequestAdapter(OwnerRequestsActivity.this, categoryRequests);
        binding.ownerRequestList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void getRejectedRequests(){
        categoryRequests = new ArrayList<>();
        adapter = new OwnerRequestAdapter(OwnerRequestsActivity.this, categoryRequests);
        binding.ownerRequestList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        if(requests.size() == 0) return;
        for(Request request : requests){
            if(request.getStatus().equals("Rejected")){
                categoryRequests.add(request);
            }
        }
        Collections.sort(categoryRequests, new Comparator<Request>() {
            @Override
            public int compare(Request t1, Request t2) {
                Date d1 = null;
                Date d2 = null;
                try {
                    d1 = new SimpleDateFormat("dd-MM-yyyy").parse(t1.getDate());
                    d2 = new SimpleDateFormat("dd-MM-yyyy").parse(t2.getDate());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                if(d1 == null || d2 == null) return 0;
                return d2.compareTo(d1);
            }
        });
        adapter = new OwnerRequestAdapter(OwnerRequestsActivity.this, categoryRequests);
        binding.ownerRequestList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void getPendingRequests(){
        categoryRequests = new ArrayList<>();
        adapter = new OwnerRequestAdapter(OwnerRequestsActivity.this, categoryRequests);
        binding.ownerRequestList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        if(requests.size() == 0) return;
        for(Request request : requests){
            if(request.getStatus().equals("Pending")){
                categoryRequests.add(request);
            }
        }
        Collections.sort(categoryRequests, new Comparator<Request>() {
            @Override
            public int compare(Request t1, Request t2) {
                Date d1 = null;
                Date d2 = null;
                try {
                    d1 = new SimpleDateFormat("dd-MM-yyyy").parse(t1.getDate());
                    d2 = new SimpleDateFormat("dd-MM-yyyy").parse(t2.getDate());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                if(d1 == null || d2 == null) return 0;
                return d2.compareTo(d1);
            }
        });
        adapter = new OwnerRequestAdapter(OwnerRequestsActivity.this, categoryRequests);
        binding.ownerRequestList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void getPgRequests(){
        categoryRequests = new ArrayList<>();
        adapter = new OwnerRequestAdapter(OwnerRequestsActivity.this, categoryRequests);
        binding.ownerRequestList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        if(requests.size() == 0) return;
        for(Request request : requests){
            if(request.getType().equals("pg")){
                categoryRequests.add(request);
            }
        }
        Collections.sort(categoryRequests, new Comparator<Request>() {
            @Override
            public int compare(Request t1, Request t2) {
                Date d1 = null;
                Date d2 = null;
                try {
                    d1 = new SimpleDateFormat("dd-MM-yyyy").parse(t1.getDate());
                    d2 = new SimpleDateFormat("dd-MM-yyyy").parse(t2.getDate());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                if(d1 == null || d2 == null) return 0;
                return d2.compareTo(d1);
            }
        });
        adapter = new OwnerRequestAdapter(OwnerRequestsActivity.this, categoryRequests);
        binding.ownerRequestList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void getMessRequests(){
        categoryRequests = new ArrayList<>();
        adapter = new OwnerRequestAdapter(OwnerRequestsActivity.this, categoryRequests);
        binding.ownerRequestList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        if(requests.size() == 0) return;
        for(Request request : requests){
            if(request.getType().equals("mess")){
                categoryRequests.add(request);
            }
        }
        Collections.sort(categoryRequests, new Comparator<Request>() {
            @Override
            public int compare(Request t1, Request t2) {
                Date d1 = null;
                Date d2 = null;
                try {
                    d1 = new SimpleDateFormat("dd-MM-yyyy").parse(t1.getDate());
                    d2 = new SimpleDateFormat("dd-MM-yyyy").parse(t2.getDate());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                if(d1 == null || d2 == null) return 0;
                return d2.compareTo(d1);
            }
        });
        adapter = new OwnerRequestAdapter(OwnerRequestsActivity.this, categoryRequests);
        binding.ownerRequestList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void search(String text){
        if(text.length() < 1) return;
        text = text.toLowerCase();
        categoryRequests = new ArrayList<>();
        adapter = new OwnerRequestAdapter(OwnerRequestsActivity.this, categoryRequests);
        binding.ownerRequestList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        if(requests.size() == 0) return;
        for(Request request : requests){
            if(request.getUserName().toLowerCase().indexOf(text) >= 0){
                categoryRequests.add(request);
            }
        }
        Collections.sort(categoryRequests, new Comparator<Request>() {
            @Override
            public int compare(Request t1, Request t2) {
                Date d1 = null;
                Date d2 = null;
                try {
                    d1 = new SimpleDateFormat("dd-MM-yyyy").parse(t1.getDate());
                    d2 = new SimpleDateFormat("dd-MM-yyyy").parse(t2.getDate());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                if(d1 == null || d2 == null) return 0;
                return d2.compareTo(d1);
            }
        });
        adapter = new OwnerRequestAdapter(OwnerRequestsActivity.this, categoryRequests);
        binding.ownerRequestList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}