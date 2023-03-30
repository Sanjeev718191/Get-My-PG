package com.androidaxe.getmypg.Activities.OwnerPGMessFragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidaxe.getmypg.Activities.AddNewBussinessActivity;
import com.androidaxe.getmypg.Activities.EditMessMenuActivity;
import com.androidaxe.getmypg.R;
import com.androidaxe.getmypg.databinding.FragmentEditBinding;
import com.google.firebase.database.FirebaseDatabase;

public class EditFragment extends Fragment {

    String type, id;
    Context context;
    FragmentEditBinding binding;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    public EditFragment() {
        // Required empty public constructor
    }
    public EditFragment(String type, String id, Context context) {
        this.type = type;
        this.id = id;
        this.context = context;
    }
    public static EditFragment newInstance(String param1, String param2) {
        EditFragment fragment = new EditFragment();
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

        binding = FragmentEditBinding.inflate(inflater, container, false);
        if(type.equals("pg")){
            binding.editPg.setVisibility(View.VISIBLE);
            binding.editMess.setVisibility(View.GONE);
            binding.editMessMenu.setVisibility(View.GONE);
        } else {
            binding.editPg.setVisibility(View.GONE);
            binding.editMess.setVisibility(View.VISIBLE);
            binding.editMessMenu.setVisibility(View.VISIBLE);
        }

        binding.editPg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AddNewBussinessActivity.class);
                intent.putExtra("type", type);
                intent.putExtra("id", ""+id);
                startActivity(intent);
            }
        });

        binding.editMess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AddNewBussinessActivity.class);
                intent.putExtra("type", type);
                intent.putExtra("id", ""+id);
                startActivity(intent);
            }
        });

        binding.editMessMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EditMessMenuActivity.class);
                intent.putExtra("Mess Id", ""+id);
                startActivity(intent);
            }
        });

        return binding.getRoot();
    }
}