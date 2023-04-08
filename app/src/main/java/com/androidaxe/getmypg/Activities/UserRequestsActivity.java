package com.androidaxe.getmypg.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.androidaxe.getmypg.Adapters.OwnerRequestAdapter;
import com.androidaxe.getmypg.Adapters.UserRequestAdapter;
import com.androidaxe.getmypg.Module.Request;
import com.androidaxe.getmypg.R;
import com.androidaxe.getmypg.databinding.ActivityUserRequestsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.SimpleOnSearchActionListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class UserRequestsActivity extends AppCompatActivity {

    ActivityUserRequestsBinding binding;
    ArrayList<Request> myRequests;
    ArrayList<String> requestsIds;
    FirebaseAuth auth;
    FirebaseDatabase database;
    UserRequestAdapter adapter;
    LinearLayoutManager manager;
    ProgressDialog progressDialog;
    RadioButton currentButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserRequestsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading data...");
        progressDialog.setCanceledOnTouchOutside(false);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        requestsIds = new ArrayList<>();
        myRequests = new ArrayList<>();

        manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.VERTICAL);
        binding.userRequestList.setLayoutManager(manager);
        getAllRequests();

        binding.userRequestAll.setEnabled(false);
        currentButton = binding.userRequestAll;

        binding.userRequestRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == R.id.user_request_all){
                    adapter = new UserRequestAdapter(UserRequestsActivity.this, myRequests);
                    binding.userRequestList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    currentButton = binding.userRequestAll;
                } else if(i == R.id.user_request_accepted){
                    binding.userRequestAll.setEnabled(true);
                    getAcceptedRequests();
                    currentButton = binding.userRequestAccepted;
                } else if(i == R.id.user_request_rejected){
                    binding.userRequestAll.setEnabled(true);
                    getRejectedRequests();
                    currentButton = binding.userRequestRejected;
                } else if(i == R.id.user_request_pending){
                    binding.userRequestAll.setEnabled(true);
                    getPendingRequests();
                    currentButton = binding.userRequestPending;
                } else if(i == R.id.user_request_pg){
                    binding.userRequestAll.setEnabled(true);
                    getPgRequests();
                    currentButton = binding.userRequestPg;
                } else if(i == R.id.user_request_mess){
                    binding.userRequestAll.setEnabled(true);
                    getMessRequests();
                    currentButton = binding.userRequestMess;
                }
            }
        });


        binding.userRequestsSearchBar.setOnSearchActionListener(new SimpleOnSearchActionListener() {
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

        binding.userRequestRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAllRequests();
                new CountDownTimer(1000, 1000){
                    @Override
                    public void onTick(long l) { }

                    @Override
                    public void onFinish() {
                        if(currentButton == binding.userRequestAccepted){
                            getAcceptedRequests();
                        } else if(currentButton == binding.userRequestRejected){
                            getRejectedRequests();
                        } else if(currentButton == binding.userRequestPending){
                            getPendingRequests();
                        } else if(currentButton == binding.userRequestPg){
                            getPgRequests();
                        } else if(currentButton == binding.userRequestMess){
                            getMessRequests();
                        }
                    }
                }.start();
            }
        });

    }
    private void getAllRequests(){
        progressDialog.show();
        requestsIds = new ArrayList<>();
        myRequests = new ArrayList<>();
        database.getReference("Requests").child("UserPGRequests").child(auth.getUid()).child("requestIds").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressDialog.dismiss();
                if(snapshot.getChildrenCount() > 0){
                    for(DataSnapshot ds : snapshot.getChildren()){
                        String id = ds.getValue(String.class);
                        database.getReference("Requests").child("PGRequests").child(id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Request request = snapshot.getValue(Request.class);
                                if(!checkRequestExpiry(request)) {
                                    requestsIds.add(id);
                                    myRequests.add(request);
                                    adapter = new UserRequestAdapter(UserRequestsActivity.this, myRequests);
                                    binding.userRequestList.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                }
                database.getReference("Requests").child("UserMessRequests").child(auth.getUid()).child("requestIds").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.getChildrenCount() > 0){
                            for(DataSnapshot ds : snapshot.getChildren()){
                                String id = ds.getValue(String.class);
                                database.getReference("Requests").child("MessRequests").child(id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Request request = snapshot.getValue(Request.class);
                                        if(!checkRequestExpiry(request)) {
                                            requestsIds.add(id);
                                            myRequests.add(request);
                                            adapter = new UserRequestAdapter(UserRequestsActivity.this, myRequests);
                                            binding.userRequestList.setAdapter(adapter);
                                            adapter.notifyDataSetChanged();
                                        }
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
                        Toast.makeText(UserRequestsActivity.this, "Data not found", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(UserRequestsActivity.this, "Data not found", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void deleteRequest(Request request) {

        if(request.getType().equals("pg")) {
            database.getReference("Requests").child("UserPGRequests").child(request.getUid()).child("requestIds").child(request.getRequestId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    database.getReference("Requests").child("OwnerPGRequests").child(request.getOid()).child("requestIds").child(request.getRequestId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            database.getReference("Requests").child("PGRequests").child(request.getRequestId()).removeValue();
                        }
                    });
                }
            });
        } else {
            database.getReference("Requests").child("UserMessRequests").child(request.getUid()).child("requestIds").child(request.getRequestId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    database.getReference("Requests").child("OwnerMessRequests").child(request.getOid()).child("requestIds").child(request.getRequestId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            database.getReference("Requests").child("MessRequests").child(request.getRequestId()).removeValue();
                        }
                    });
                }
            });
        }

    }

    private boolean checkRequestExpiry(Request request) {

        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        try {
            Date currDate = sdf.parse(date);
            Date onDate = sdf.parse(request.getDate());
            long diffInMillies = Math.abs(onDate.getTime() - currDate.getTime());
            long days = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            if(days > 7){
                deleteRequest(request);
                return true;
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return false;
    }


    ArrayList<Request> categoryRequests;
    private void getAcceptedRequests(){
        categoryRequests = new ArrayList<>();
        adapter = new UserRequestAdapter(UserRequestsActivity.this, categoryRequests);
        binding.userRequestList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        if(myRequests.size() == 0) return;
        for(Request request : myRequests){
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
        adapter = new UserRequestAdapter(UserRequestsActivity.this, categoryRequests);
        binding.userRequestList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void getRejectedRequests(){
        categoryRequests = new ArrayList<>();
        adapter = new UserRequestAdapter(UserRequestsActivity.this, categoryRequests);
        binding.userRequestList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        if(myRequests.size() == 0) return;
        for(Request request : myRequests){
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
        adapter = new UserRequestAdapter(UserRequestsActivity.this, categoryRequests);
        binding.userRequestList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void getPendingRequests(){
        categoryRequests = new ArrayList<>();
        adapter = new UserRequestAdapter(UserRequestsActivity.this, categoryRequests);
        binding.userRequestList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        if(myRequests.size() == 0) return;
        for(Request request : myRequests){
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
        adapter = new UserRequestAdapter(UserRequestsActivity.this, categoryRequests);
        binding.userRequestList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void getPgRequests(){
        categoryRequests = new ArrayList<>();
        adapter = new UserRequestAdapter(UserRequestsActivity.this, categoryRequests);
        binding.userRequestList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        if(myRequests.size() == 0) return;
        for(Request request : myRequests){
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
        adapter = new UserRequestAdapter(UserRequestsActivity.this, categoryRequests);
        binding.userRequestList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void getMessRequests(){
        categoryRequests = new ArrayList<>();
        adapter = new UserRequestAdapter(UserRequestsActivity.this, categoryRequests);
        binding.userRequestList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        if(myRequests.size() == 0) return;
        for(Request request : myRequests){
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
        adapter = new UserRequestAdapter(UserRequestsActivity.this, categoryRequests);
        binding.userRequestList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void search(String text){
        if(text.length() < 1) return;
        text = text.toLowerCase();
        categoryRequests = new ArrayList<>();
        adapter = new UserRequestAdapter(UserRequestsActivity.this, categoryRequests);
        binding.userRequestList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        if(myRequests.size() == 0) return;
        for(Request request : myRequests){
            if(request.getBusinessName().toLowerCase().indexOf(text) >= 0){
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
        adapter = new UserRequestAdapter(UserRequestsActivity.this, categoryRequests);
        binding.userRequestList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }



}