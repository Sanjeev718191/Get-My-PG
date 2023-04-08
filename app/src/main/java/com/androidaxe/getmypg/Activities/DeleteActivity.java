package com.androidaxe.getmypg.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.androidaxe.getmypg.Module.UserSubscribedItem;
import com.androidaxe.getmypg.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DeleteActivity extends AppCompatActivity {

    FirebaseDatabase database;
    UserSubscribedItem subscription;
    String id;
    int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);

        id = getIntent().getStringExtra("id");
        database = FirebaseDatabase.getInstance();

        database.getReference("Subscription").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                subscription = snapshot.getValue(UserSubscribedItem.class);
                if(subscription != null){
                    if(subscription.getType().equals("pg")){
                        Toast.makeText(DeleteActivity.this, "start", Toast.LENGTH_SHORT).show();
                        database.getReference("UserSubscription").child("UserPG").child(subscription.getUid()).child(subscription.getSubscriptionId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(DeleteActivity.this, "u-s", Toast.LENGTH_SHORT).show();
                                database.getReference("BusinessSubscriber").child("HostelUser").child(subscription.getPGMessId()).child(subscription.getSubscriptionId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(DeleteActivity.this, "b-s", Toast.LENGTH_SHORT).show();
                                        database.getReference("Subscription").child(subscription.getSubscriptionId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(DeleteActivity.this, "s-s", Toast.LENGTH_SHORT).show();
                                                database.getReference("PGRoom").child(subscription.getPGMessId()).child("Room"+subscription.getRoomNumber()).child("users").child(subscription.getSubscriptionId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(DeleteActivity.this, "r-s", Toast.LENGTH_SHORT).show();
                                                        database.getReference("PGs").child(subscription.getPGMessId()).child("totalUsers").addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                count = Integer.parseInt(snapshot.getValue(String.class));
                                                                count--;
                                                                Toast.makeText(DeleteActivity.this, count+"", Toast.LENGTH_SHORT).show();
                                                                database.getReference("PGs").child(subscription.getPGMessId()).child("totalUsers").setValue(count+"").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        Toast.makeText(DeleteActivity.this, "count", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });

                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        });


                                        database.getReference("PGs").child(subscription.getPGMessId()).child("totalUsers").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                count = Integer.parseInt(snapshot.getValue(String.class));
                                                count--;
                                                database.getReference("PGs").child(subscription.getPGMessId()).child("totalUsers").setValue(count+"").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(DeleteActivity.this, count, Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                });
                            }
                        });

                    } else {
                        database.getReference("UserSubscription").child("UserMess").child(subscription.getUid()).child(subscription.getSubscriptionId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                database.getReference("BusinessSubscriber").child("MessUser").child(subscription.getPGMessId()).child(subscription.getSubscriptionId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        database.getReference("Subscription").child(subscription.getSubscriptionId()).removeValue();

                                        database.getReference("Mess").child(subscription.getPGMessId()).child("totalUsers").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                count = Integer.parseInt(snapshot.getValue(String.class));
                                                count--;
                                                database.getReference("Mess").child(subscription.getPGMessId()).child("totalUsers").setValue(count+"").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(DeleteActivity.this, count, Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }
}