package com.androidaxe.getmypg.Activities.OwnerPGMessFragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.androidaxe.getmypg.Adapters.MyCustomerAdapter;
import com.androidaxe.getmypg.Module.PGUser;
import com.androidaxe.getmypg.Module.UserSubscribedItem;
import com.androidaxe.getmypg.R;
import com.androidaxe.getmypg.databinding.FragmentMyCustomersBinding;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.SimpleOnSearchActionListener;

import java.util.ArrayList;


public class MyCustomersFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    String type, id;
    Context context;
    FragmentMyCustomersBinding binding;
    FirebaseDatabase database;
    ProgressDialog progressDialog;
    MyCustomerAdapter adapter;
    ArrayList<UserSubscribedItem> subscribers;
    ArrayList<PGUser> users;
    ArrayList<String> subIds;

    public MyCustomersFragment() {
        // Required empty public constructor
    }

    public MyCustomersFragment(String type, String id, Context context) {
        this.type = type;
        this.id = id;
        this.context = context;
    }

    public static MyCustomersFragment newInstance(String param1, String param2) {
        MyCustomersFragment fragment = new MyCustomersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentMyCustomersBinding.inflate(inflater, container, false);

        if(getActivity() != null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Loading Info...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        database = FirebaseDatabase.getInstance();
        binding.myCustomerRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new MyCustomerAdapter(context, new ArrayList<>(), new ArrayList<>());
        binding.myCustomerRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        getAllUsers();

        binding.myCustomerRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == R.id.my_customer_all){
                    getAllUsers();
                } else if(i == R.id.my_customer_paid) {
                    getOngoingUsers();
                } else {
                    getUnpaidUsers();
                }
            }
        });

        binding.myCustomerSearchBar.setOnSearchActionListener(new SimpleOnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                super.onSearchStateChanged(enabled);
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                super.onSearchConfirmed(text);
                search(text.toString().toLowerCase());
            }

            @Override
            public void onButtonClicked(int buttonCode) {
                super.onButtonClicked(buttonCode);
            }
        });

        return binding.getRoot();
    }

    private void getAllUsers(){
        if(type.equals("pg")){
            progressDialog.show();
            database.getReference("BusinessSubscriber").child("HostelUser").child(id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.getChildrenCount() > 0){
                        subscribers = new ArrayList<>();
                        users = new ArrayList<>();
                        subIds = new ArrayList<>();
                        subscribers.clear();
                        for(DataSnapshot ds : snapshot.getChildren()){
                            String subscriptionId = ds.getValue(String.class);
                            subIds.add(subscriptionId);
                            database.getReference("Subscription").child(subscriptionId).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    UserSubscribedItem item = snapshot.getValue(UserSubscribedItem.class);
                                    if(item == null) return;
                                    int ind = subIds.indexOf(item.getSubscriptionId());
                                    if(subscribers.size() > 0 && ind >= 0) {
                                        subscribers.remove(ind);
                                        subscribers.add(ind, item);
                                        adapter = new MyCustomerAdapter(context, subscribers, users);
                                        binding.myCustomerRecyclerView.setAdapter(adapter);
                                        adapter.notifyDataSetChanged();
                                    } else {
                                        subIds.add(item.getSubscriptionId());
                                        subscribers.add(item);
                                        FirebaseDatabase.getInstance().getReference("PGUser").child(item.getUid()).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                PGUser user = snapshot.getValue(PGUser.class);
                                                users.add(user);
                                                adapter = new MyCustomerAdapter(context, subscribers, users);
                                                binding.myCustomerRecyclerView.setAdapter(adapter);
                                                adapter.notifyDataSetChanged();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                    }
                    progressDialog.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressDialog.dismiss();
                    Toast.makeText(context, "Data not found", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            progressDialog.show();
           database.getReference("BusinessSubscriber").child("MessUser").child(id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.getChildrenCount() > 0){
                        subscribers = new ArrayList<>();
                        users = new ArrayList<>();
                        subIds = new ArrayList<>();
                        subscribers.clear();
                        for(DataSnapshot ds : snapshot.getChildren()){
                            String subscriptionId = ds.getValue(String.class);
                            subIds.add(subscriptionId);
                            database.getReference("Subscription").child(subscriptionId).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    UserSubscribedItem item = snapshot.getValue(UserSubscribedItem.class);
                                    if(item == null) return;
                                    int ind = subIds.indexOf(item.getSubscriptionId());
                                    if(subscribers.size() > 0 && ind >= 0) {
                                        subscribers.remove(ind);
                                        subscribers.add(ind, item);
                                        adapter = new MyCustomerAdapter(context, subscribers, users);
                                        binding.myCustomerRecyclerView.setAdapter(adapter);
                                        adapter.notifyDataSetChanged();
                                    } else {
                                        subIds.add(item.getSubscriptionId());
                                        subscribers.add(item);
                                        FirebaseDatabase.getInstance().getReference("PGUser").child(item.getUid()).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                PGUser user = snapshot.getValue(PGUser.class);
                                                users.add(user);
                                                adapter = new MyCustomerAdapter(context, subscribers, users);
                                                binding.myCustomerRecyclerView.setAdapter(adapter);
                                                adapter.notifyDataSetChanged();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                    progressDialog.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressDialog.dismiss();
                    Toast.makeText(context, "Data not found", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    ArrayList<UserSubscribedItem> selectedSubscribers;
    ArrayList<PGUser> selectedUsers;
    private void search(String query){
        selectedSubscribers = new ArrayList<>();
        selectedUsers = new ArrayList<>();
        adapter = new MyCustomerAdapter(context, selectedSubscribers, selectedUsers);
        binding.myCustomerRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        for(int i = 0; i<subscribers.size(); i++){
            if(users.get(i).getName().toLowerCase().indexOf(query) >= 0){
                selectedUsers.add(users.get(i));
                selectedSubscribers.add(subscribers.get(i));
                adapter = new MyCustomerAdapter(context, selectedSubscribers, selectedUsers);
                binding.myCustomerRecyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void getOngoingUsers(){
        selectedSubscribers = new ArrayList<>();
        selectedUsers = new ArrayList<>();
        adapter = new MyCustomerAdapter(context, selectedSubscribers, selectedUsers);
        binding.myCustomerRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        for(int i = 0; i<subscribers.size(); i++){
            UserSubscribedItem item = subscribers.get(i);
            if (item.getCurrentlyActive().equals("true")){
                selectedUsers.add(users.get(i));
                selectedSubscribers.add(subscribers.get(i));
                adapter = new MyCustomerAdapter(context, selectedSubscribers, selectedUsers);
                binding.myCustomerRecyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void getUnpaidUsers(){
        selectedSubscribers = new ArrayList<>();
        selectedUsers = new ArrayList<>();
        adapter = new MyCustomerAdapter(context, selectedSubscribers, selectedUsers);
        binding.myCustomerRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        for(int i = 0; i<subscribers.size(); i++){
            UserSubscribedItem item = subscribers.get(i);
            if (item.getCurrentlyActive().equals("false")){
                selectedUsers.add(users.get(i));
                selectedSubscribers.add(subscribers.get(i));
                adapter = new MyCustomerAdapter(context, selectedSubscribers, selectedUsers);
                binding.myCustomerRecyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }
    }

}