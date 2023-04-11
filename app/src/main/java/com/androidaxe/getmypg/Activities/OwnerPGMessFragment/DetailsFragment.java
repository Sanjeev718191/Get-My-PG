package com.androidaxe.getmypg.Activities.OwnerPGMessFragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.SnapHelper;
import androidx.viewbinding.ViewBinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.androidaxe.getmypg.Activities.EditMessMenuActivity;
import com.androidaxe.getmypg.Activities.ImageZoomViewActivity;
import com.androidaxe.getmypg.Activities.OwnerPGMessActivity;
import com.androidaxe.getmypg.Activities.OwnerPayFeeActivity;
import com.androidaxe.getmypg.Activities.UserSubscriptionActivity;
import com.androidaxe.getmypg.Adapters.MyCustomerAdapter;
import com.androidaxe.getmypg.Module.OwnerMess;
import com.androidaxe.getmypg.Module.OwnerPG;
import com.androidaxe.getmypg.Module.PGUser;
import com.androidaxe.getmypg.Module.Request;
import com.androidaxe.getmypg.Module.UserSubscribedItem;
import com.androidaxe.getmypg.R;
import com.androidaxe.getmypg.databinding.FragmentDetailsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.imaginativeworld.whynotimagecarousel.listener.CarouselListener;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DetailsFragment extends Fragment {

    FragmentDetailsBinding binding;
    String type, id, subscriptionType;
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
        if(type.equals("pg")) subscriptionType = "HostelUser";
        else subscriptionType = "MessUser";
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
            database.getReference("PGs").child(id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    pg = snapshot.getValue(OwnerPG.class);
                    int unpaidUsers = Integer.parseInt(pg.getTotalUsers()) - Integer.parseInt(pg.getPaidUsers());
                    binding.ownerPgmessDetails.setText("Hostel/PG Details");
                    if(pg.getStopRequests().equals("false")){
                        binding.ownerPgmessStatus.setTextColor(context.getColor(R.color.primary));
                        binding.ownerPgmessStatus.setText("Status : Active");
                    } else {
                        binding.ownerPgmessStatus.setTextColor(context.getColor(R.color.red));
                        binding.ownerPgmessStatus.setText("Status : You are currently not Accepting Requests");
                    }
                    binding.ownerPrmessImageCarousel.addData(new CarouselItem(pg.getImage()));
                    binding.ownerPrmessViewMessMenuPdf.setVisibility(View.GONE);
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
                    if(pg.getStopRequests().equals("false")){
                        binding.stopGettingRequests.setBackgroundDrawable(context.getDrawable(R.drawable.button_deactive_background));
                        binding.stopGettingRequests.setText("Stop Getting Requests");
                    } else {
                        binding.stopGettingRequests.setBackgroundDrawable(context.getDrawable(R.drawable.button_background));
                        binding.stopGettingRequests.setText("Start Getting Requests");
                    }
                    calculateDetails();
                    deleteUselessSubscriptions();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressDialog.dismiss();
                    Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            progressDialog.show();
            database.getReference("Mess").child(id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mess = snapshot.getValue(OwnerMess.class);
                    int unpaidUsers = Integer.parseInt(mess.getTotalUsers()) - Integer.parseInt(mess.getPaidUsers());
                    binding.ownerPgmessDetails.setText("Mess Details");
                    if(mess.getStopRequests().equals("false")){
                        binding.ownerPgmessStatus.setTextColor(context.getColor(R.color.primary));
                        binding.ownerPgmessStatus.setText("Status : Active");
                    } else {
                        binding.ownerPgmessStatus.setTextColor(context.getColor(R.color.red));
                        binding.ownerPgmessStatus.setText("Status : You are currently not Accepting Requests");
                    }
                    binding.ownerPrmessImageCarousel.addData(new CarouselItem(mess.getImage()));
                    binding.ownerPrmessImageCarousel.addData(new CarouselItem(mess.getMenu()));
                    binding.ownerPrmessViewMessMenuPdf.setVisibility(View.VISIBLE);
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
                    if(mess.getStopRequests().equals("false")){
                        binding.stopGettingRequests.setBackgroundDrawable(context.getDrawable(R.drawable.button_deactive_background));
                        binding.stopGettingRequests.setText("Stop Getting Requests");
                    } else {
                        binding.stopGettingRequests.setBackgroundDrawable(context.getDrawable(R.drawable.button_background));
                        binding.stopGettingRequests.setText("Start Getting Requests");
                    }
                    calculateDetails();
                    deleteUselessSubscriptions();
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
                String Image;
                if(type.equals("pg")){
                    Image = pg.getImage();
                } else {
                    if(i == 0){
                        Image = mess.getImage();
                    } else {
                        Image = mess.getMenu();
                    }
                }
                if(Image != null && !Image.equals("na")){
                    Intent intent = new Intent(context, ImageZoomViewActivity.class);
                    intent.putExtra("name", pg != null ? pg.getName() : mess.getName());
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
                notifyUsers(true);
            }
        });

        binding.ownerPgmessNotifyUnpaidButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyUsers(false);
            }
        });

        binding.stopGettingRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleGettingRequest();
            }
        });

        binding.ownerPrmessViewMessMenuPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mess != null && mess.getMenuPDF() != null && !mess.getMenuPDF().equals("na")){
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(mess.getMenuPDF()));
                    startActivity(intent);
                } else {
                    Toast.makeText(context, "You have not added menu.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return binding.getRoot();
    }
    String todayDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
    long netRevenue, paidUsers;
    int count;
    private void calculateDetails(){
        netRevenue = 0;
        paidUsers = 0;
        count = 0;
        if(type.equals("pg")){
            database.getReference("BusinessSubscriber").child(subscriptionType).child(id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.getChildrenCount() > 0){
                        for(DataSnapshot ds : snapshot.getChildren()){
                            count++;
                            String subscriptionId = ds.getValue(String.class);
                            if(subscriptionId == null) return;
                            database.getReference("Subscription").child(subscriptionId).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snap) {
                                    UserSubscribedItem item = snap.getValue(UserSubscribedItem.class);
                                    if(item == null) return;
                                    if(!item.getToDate().equals("na") && isActiveUser(item)){
                                        incrementPaidUser();
                                    }
                                    if(!item.getPaymentDate().equals("na") && item.getPaymentDate().substring(3,5).equals(todayDate.substring(3,5))){
                                        addRevenue(Long.parseLong(item.getLastPaidAmount()));
                                    }
                                    if(count == snapshot.getChildrenCount()){
                                        HashMap<String, Object> map = new HashMap<>();
                                        map.put("revenue", netRevenue+"");
                                        map.put("paidUsers",""+paidUsers);
                                        database.getReference("PGs").child(id).updateChildren(map);
                                        progressDialog.dismiss();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    progressDialog.dismiss();
                                }
                            });

                        }

                    } else {
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("revenue", netRevenue+"");
                        map.put("paidUsers",""+paidUsers);
                        database.getReference("PGs").child(id).updateChildren(map);
                        progressDialog.dismiss();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressDialog.dismiss();
                }
            });

        } else {
            database.getReference("BusinessSubscriber").child(subscriptionType).child(id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.getChildrenCount() > 0){
                        for(DataSnapshot ds : snapshot.getChildren()){
                            count++;
                            String subscriptionId = ds.getValue(String.class);
                            database.getReference("Subscription").child(subscriptionId).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snap) {
                                    UserSubscribedItem item = snap.getValue(UserSubscribedItem.class);
                                    if(item == null) return;
                                    if(!item.getToDate().equals("na") && isActiveUser(item)){
                                        incrementPaidUser();
                                    }
                                    if(!item.getPaymentDate().equals("na") && item.getPaymentDate().substring(3,10).equals(todayDate.substring(3,10))){
                                        addRevenue(Long.parseLong(item.getLastPaidAmount()));
                                    }
                                    if(count == snapshot.getChildrenCount()){
                                        HashMap<String, Object> map = new HashMap<>();
                                        map.put("revenue", netRevenue+"");
                                        map.put("paidUsers",""+paidUsers);
                                        database.getReference("Mess").child(id).updateChildren(map);
                                        progressDialog.dismiss();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    progressDialog.dismiss();
                                }
                            });

                        }
                    } else {
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("revenue", netRevenue+"");
                        map.put("paidUsers",""+paidUsers);
                        database.getReference("Mess").child(id).updateChildren(map);
                        progressDialog.dismiss();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressDialog.dismiss();
                }
            });
        }
    }

    private void addRevenue(long revenue){
        netRevenue += revenue;
    }
    private void incrementPaidUser(){
        paidUsers++;
    }

    private void deleteUselessSubscriptions(){
        if(type.equals("pg")){
            database.getReference("deletedSubscription").child(pg.getOid()).child(pg.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot ds : snapshot.getChildren()){
                        String currentId = ds.getValue(String.class);
                        if(currentId == null) return;
                        database.getReference("Subscription").child(currentId).removeValue();
                        database.getReference("deletedSubscription").child(pg.getOid()).child(pg.getId()).child(currentId).removeValue();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } else {

            database.getReference("deletedSubscription").child(mess.getOid()).child(mess.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot ds : snapshot.getChildren()){
                        String currentId = ds.getValue(String.class);
                        if(currentId == null) return;
                        database.getReference("Subscription").child(currentId).removeValue();
                        database.getReference("deletedSubscription").child(mess.getOid()).child(mess.getId()).child(currentId).removeValue();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

    }

    private void notifyUsers(boolean toAll){

        progressDialog.setTitle("Sending Notice...");
        progressDialog.show();
        count = 0;
        database.getReference("BusinessSubscriber").child(subscriptionType).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getChildrenCount() > 0){
                    for(DataSnapshot ds : snapshot.getChildren()){
                        count++;
                        String subscriptionId = ds.getValue(String.class);
                        if(subscriptionId == null) return;
                        database.getReference("Subscription").child(subscriptionId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snap) {
                                UserSubscribedItem item = snap.getValue(UserSubscribedItem.class);
                                if(item == null) return;
                                if(item.getToDate().equals("na") || !isActiveUser(item) || toAll){
                                    database.getReference("Subscription").child(subscriptionId).child("Notice").setValue("Please Pay Fee to Continue");
                                    if(count == snapshot.getChildrenCount()){
                                        Toast.makeText(context, "Notification sent", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                progressDialog.dismiss();
                            }
                        });

                    }

                } else {
                    Toast.makeText(context, "Notification sent", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
            }
        });
    }

    private boolean isActiveUser(UserSubscribedItem item){
        try {
            Date currDate = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            Date toDate = sdf.parse(item.getToDate());
            if (toDate.compareTo(currDate) > 0) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    String message = "";
    String businessType = "";
    private void toggleGettingRequest(){
        String warning = "";
        if(type.equals("pg")){
            businessType = "PGs";
            if(pg.getStopRequests().equals("false")) {
                message = "true";
                warning = "If you do so, Customers will not able to send you request.";
            }
            else {
                message = "false";
                warning = "If you do so, Customers will able to send you request.";
            }
        } else {
            businessType = "Mess";
            if(mess.getStopRequests().equals("false")) {
                message = "true";
                warning = "If you do so, Customers will not able to send you request.";
            }
            else {
                message = "false";
                warning = "If you do so, Customers will able to send you request.";
            }
        }

        ProgressDialog userDeleteDialog = new ProgressDialog(context);
        userDeleteDialog.setTitle("Are you sure?");
        userDeleteDialog.setMessage(warning);
        userDeleteDialog.setCancelable(false);
        userDeleteDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        userDeleteDialog.setButton(DialogInterface.BUTTON_POSITIVE, "YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialog.setTitle("Please wait");
                progressDialog.setMessage("");
                progressDialog.show();
                database.getReference(businessType).child(id).child("stopRequests").setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        if(message.equals("false")){
                            Toast.makeText(context, "Now you can get requests.", Toast.LENGTH_SHORT).show();
                            binding.stopGettingRequests.setBackgroundDrawable(context.getDrawable(R.drawable.button_deactive_background));
                            binding.stopGettingRequests.setText("Stop Getting Requests");
                        } else {
                            Toast.makeText(context, "Now you will not get requests.", Toast.LENGTH_SHORT).show();
                            binding.stopGettingRequests.setBackgroundDrawable(context.getDrawable(R.drawable.button_background));
                            binding.stopGettingRequests.setText("Start Getting Requests");
                        }
                    }
                });

            }
        });
        userDeleteDialog.show();

    }

}