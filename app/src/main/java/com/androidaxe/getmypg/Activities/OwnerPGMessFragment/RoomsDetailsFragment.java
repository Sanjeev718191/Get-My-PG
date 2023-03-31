package com.androidaxe.getmypg.Activities.OwnerPGMessFragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidaxe.getmypg.Adapters.MyCustomerAdapter;
import com.androidaxe.getmypg.Adapters.RoomAdapter;
import com.androidaxe.getmypg.Module.OwnerPG;
import com.androidaxe.getmypg.Module.PGUser;
import com.androidaxe.getmypg.Module.UserSubscribedItem;
import com.androidaxe.getmypg.R;
import com.androidaxe.getmypg.databinding.FragmentMyCustomersBinding;
import com.androidaxe.getmypg.databinding.FragmentRoomsDetailsBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RoomsDetailsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    String type, id;
    Context context;
    OwnerPG pg;
    FragmentRoomsDetailsBinding binding;

    public RoomsDetailsFragment(String type, String id, Context context) {
        this.type = type;
        this.id = id;
        this.context = context;
    }

    public RoomsDetailsFragment() {

    }

    public static RoomsDetailsFragment newInstance(String param1, String param2) {
        RoomsDetailsFragment fragment = new RoomsDetailsFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentRoomsDetailsBinding.inflate(inflater, container, false);
        if(type.equals("pg")){
            binding.roomDetailNoRoomText.setVisibility(View.GONE);
            binding.roomDetailRoomList.setVisibility(View.VISIBLE);
            FirebaseDatabase.getInstance().getReference("PGs").child(id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    pg = snapshot.getValue(OwnerPG.class);
                    GridLayoutManager layoutManager = new GridLayoutManager(context, 2);
                    binding.roomDetailRoomList.setLayoutManager(layoutManager);
                    RoomAdapter adapter = new RoomAdapter(context, Integer.parseInt(pg.getRoomCount()), id);
                    binding.roomDetailRoomList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            binding.roomDetailNoRoomText.setVisibility(View.VISIBLE);
            binding.roomDetailRoomList.setVisibility(View.GONE);
        }

        return binding.getRoot();
    }
}