package com.androidaxe.getmypg.Activities.OwnerPGMessFragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.androidaxe.getmypg.Activities.EditMessMenuActivity;
import com.androidaxe.getmypg.Activities.ImageZoomViewActivity;
import com.androidaxe.getmypg.Activities.OwnerPGMessActivity;
import com.androidaxe.getmypg.Module.OwnerMess;
import com.androidaxe.getmypg.Module.OwnerPG;
import com.androidaxe.getmypg.R;
import com.androidaxe.getmypg.databinding.FragmentDetailsBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.imaginativeworld.whynotimagecarousel.listener.CarouselListener;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailsFragment extends Fragment {

    FragmentDetailsBinding binding;
    String type, id;
    OwnerPG pg;
    OwnerMess mess;
    FirebaseDatabase database;
    ProgressDialog progressDialog;
    Context context;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public DetailsFragment() {
        // Required empty public constructor
    }

    public DetailsFragment(String type, String id, Context context) {
        this.type = type;
        this.id = id;
        this.context = context;
    }

    public static DetailsFragment newInstance(String param1, String param2) {
        DetailsFragment fragment = new DetailsFragment();
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
        // Inflate the layout for this fragment

        binding = FragmentDetailsBinding.inflate(inflater, container, false);
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Loading Info...");
        progressDialog.setCanceledOnTouchOutside(false);

        database = FirebaseDatabase.getInstance();
        if(type.equals("pg")){
            progressDialog.show();
            database.getReference("PGs").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    pg = snapshot.getValue(OwnerPG.class);
                    int unpaidUsers = Integer.parseInt(pg.getTotalUsers()) - Integer.parseInt(pg.getPaidUsers());
                    binding.ownerPrmessImageCarousel.addData(new CarouselItem(pg.getImage()));
                    binding.ownerPgmessName.setText("Name : "+pg.getName());
                    binding.ownerPgmessDescription.setText("Description : "+pg.getDescription());
                    binding.ownerPgmessMessPrice.setVisibility(View.GONE);
                    binding.ownerPgmessSeater1Price.setText("Single seater price : Rs."+pg.getSeater1());
                    binding.ownerPgmessSeater2Price.setText("Double seater price : Rs."+pg.getSeater2());
                    binding.ownerPgmessSeater3Price.setText("Triple seater price : Rs."+pg.getSeater3());
                    binding.ownerPgmessTotalUsers.setText("Total customer : "+pg.getTotalUsers());
                    binding.ownerPgmessPaidUsers.setText("Received Payment : "+pg.getPaidUsers());
                    binding.ownerPgmessUnpaidUsers.setText("User not paid : "+unpaidUsers);
                    binding.ownerPgmessRevenue.setText("Total profit(this month) : Rs."+pg.getRevenue());
                    progressDialog.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressDialog.dismiss();
                    Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            progressDialog.show();
            database.getReference("Mess").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mess = snapshot.getValue(OwnerMess.class);
                    int unpaidUsers = Integer.parseInt(mess.getTotalUsers()) - Integer.parseInt(mess.getPaidUsers());
                    binding.ownerPrmessImageCarousel.addData(new CarouselItem(mess.getImage()));
                    binding.ownerPrmessImageCarousel.addData(new CarouselItem(mess.getMenu()));
                    binding.ownerPgmessName.setText("Name : "+mess.getName());
                    binding.ownerPgmessDescription.setText("Description : "+mess.getDescription());
                    binding.ownerPgmessMessPrice.setText("Price(per month) : Rs."+mess.getFeeMonthly());
                    binding.ownerPgmessSeater1Price.setVisibility(View.GONE);
                    binding.ownerPgmessSeater2Price.setVisibility(View.GONE);
                    binding.ownerPgmessSeater3Price.setVisibility(View.GONE);
                    binding.ownerPgmessTotalUsers.setText("Total customer : "+mess.getTotalUsers());
                    binding.ownerPgmessPaidUsers.setText("Received Payment : "+mess.getPaidUsers());
                    binding.ownerPgmessUnpaidUsers.setText("User not paid : "+unpaidUsers);
                    binding.ownerPgmessRevenue.setText("Total profit(this month) : Rs."+mess.getRevenue());
                    progressDialog.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressDialog.dismiss();
                    Toast.makeText(context,  error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        binding.ownerPrmessImageCarousel.setCarouselListener(new CarouselListener() {
            @Nullable
            @Override
            public ViewBinding onCreateViewHolder(@NonNull LayoutInflater layoutInflater, @NonNull ViewGroup viewGroup) {
                return null;
            }

            @Override
            public void onBindViewHolder(@NonNull ViewBinding viewBinding, @NonNull CarouselItem carouselItem, int i) {

            }

            @Override
            public void onClick(int i, @NonNull CarouselItem carouselItem) {
                if(type.equals("pg")){
                    String Image = pg.getImage();
                    Intent intent = new Intent(context, ImageZoomViewActivity.class);
                    intent.putExtra("name", pg.getName());
                    intent.putExtra("link", Image);
                    startActivity(intent);
                } else {
                    String Image;
                    if(i == 0){
                        Image = mess.getImage();
                    } else {
                        Image = mess.getMenu();
                    }
                    Intent intent = new Intent(context, ImageZoomViewActivity.class);
                    intent.putExtra("name", mess.getName());
                    intent.putExtra("link", Image);
                    startActivity(intent);
                }
            }

            @Override
            public void onLongClick(int i, @NonNull CarouselItem carouselItem) {

            }
        });

        binding.ownerPgmessNotifyAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                notifyAllUsers();
            }
        });

        binding.ownerPgmessUnpaidUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                notifyOnlyUnpaid();
            }
        });

//        if(type.equals("mess")){
//            binding.ownerPgmessEditMenuButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent intent = new Intent(context, EditMessMenuActivity.class);
//                    intent.putExtra("Mess Id", ""+mess.getId());
//                    startActivity(intent);
//                }
//            });
//        }

        return binding.getRoot();
    }
}