package com.androidaxe.getmypg.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidaxe.getmypg.Adapters.UserSubscribedItemAdapter;
import com.androidaxe.getmypg.Module.PGUser;
import com.androidaxe.getmypg.Module.UserSubscribedItem;
import com.androidaxe.getmypg.R;
import com.androidaxe.getmypg.databinding.ActivityUserMainBinding;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayDeque;
import java.util.ArrayList;

public class UserMainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityUserMainBinding binding;

    FirebaseAuth auth;
    FirebaseDatabase database;
    PGUser currentUser;
    UserSubscribedItemAdapter messAdapter, pgAdapter;
    TextView userPGText, userMessText;
    RecyclerView userPGRecycler, userMessRecycle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUserMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarUserMain.toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, binding.drawerLayout, binding.appBarUserMain.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        binding.appBarUserMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navViewUser;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.user_nav_home, R.id.user_edit_profile, R.id.user_pg, R.id.user_mess, R.id.user_my_requests, R.id.user_logout, R.id.share_user, R.id.about_up_user)
                .setOpenableLayout(drawer)
                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_user_main);
//        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
//        NavigationUI.setupWithNavController(navigationView, navController);

        // my code ==============================================================================================================================================================================================================

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        userPGText = findViewById(R.id.user_main_pg_text);
        userMessText = findViewById(R.id.user_main_mess_text);
        userPGRecycler = findViewById(R.id.user_main_PG_Recycler);
        userMessRecycle = findViewById(R.id.user_main_Mess_Recycler);
        pgAdapter = new UserSubscribedItemAdapter(this);
        messAdapter = new UserSubscribedItemAdapter(this);

        View headerView = navigationView.getHeaderView(0);
        TextView userName = headerView.findViewById(R.id.UserNavigationBarName);
        TextView userEmail = headerView.findViewById(R.id.UserNavigationBarEmail);
        TextView userContact = headerView.findViewById(R.id.UserNavigationBarContactNumber);
        userEmail.setText(auth.getCurrentUser().getEmail());
        ImageView userImage = headerView.findViewById(R.id.UserNavigationBarImage);
        database.getReference("PGUser").child(auth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentUser =snapshot.getValue(PGUser.class);
                userName.setText(currentUser.getName());
                userContact.setText("Contact : "+currentUser.getContact());
                Glide.with(UserMainActivity.this).load(currentUser.getProfile()).into(userImage);
                getUserInfo();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        //Setting navigation drawer buttons ======================================================================================================================================================================================================================================

        navigationView.getMenu().findItem(R.id.user_nav_home).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                binding.drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        navigationView.getMenu().findItem(R.id.user_edit_profile).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                binding.drawerLayout.closeDrawer(GravityCompat.START);
                startActivity(new Intent(UserMainActivity.this, UserSetProfileActivity.class));
                return true;
            }
        });
        navigationView.getMenu().findItem(R.id.user_pg).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                binding.drawerLayout.closeDrawer(GravityCompat.START);
                Intent intent = new Intent(UserMainActivity.this, UserProductListActivity.class);
                intent.putExtra("productType", "pg");
                startActivity(intent);
                return true;
            }
        });
        navigationView.getMenu().findItem(R.id.user_mess).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                binding.drawerLayout.closeDrawer(GravityCompat.START);
                Intent intent = new Intent(UserMainActivity.this, UserProductListActivity.class);
                intent.putExtra("productType", "mess");
                startActivity(intent);
                return true;
            }
        });
        navigationView.getMenu().findItem(R.id.user_my_requests).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                binding.drawerLayout.closeDrawer(GravityCompat.START);
                startActivity(new Intent(UserMainActivity.this, UserRequestsActivity.class));
                return true;
            }
        });
        navigationView.getMenu().findItem(R.id.user_logout).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                auth.signOut();
                startActivity(new Intent(UserMainActivity.this, WelcomeActivity.class));
                finishAffinity();
                return true;
            }
        });
        navigationView.getMenu().findItem(R.id.share_user).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                return false;
            }
        });
        navigationView.getMenu().findItem(R.id.about_up_user).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                binding.drawerLayout.closeDrawer(GravityCompat.START);
                startActivity(new Intent(UserMainActivity.this, AboutUsActivity.class));
                return true;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.user_action_search:
                startActivity(new Intent(UserMainActivity.this, UserSearchActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //    @Override
//    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_user_main);
//        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
//                || super.onSupportNavigateUp();
//    }

    private void getUserInfo(){
        userPGRecycler.setAdapter(pgAdapter);
        LinearLayoutManager pgLayoutManager = new LinearLayoutManager(this);
        pgLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        userPGRecycler.setLayoutManager(pgLayoutManager);

        database.getReference("UserSubscription").child("UserPG").child(currentUser.getuId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getChildrenCount() > 0){
                    userPGText.setVisibility(View.VISIBLE);
                    userPGRecycler.setVisibility(View.VISIBLE);
//                    ArrayDeque<UserSubscribedItem> curr = new ArrayDeque<>();
//                    for(DataSnapshot ds : snapshot.getChildren()){
//                        UserSubscribedItem item = ds.getValue(UserSubscribedItem.class);
//                        if(item.getCurrentlyActive().equals("true")){
//                            curr.addFirst(item);
//                        } else {
//                            curr.addLast(item);
//                        }
//                    }
//                    pgAdapter.clear();
//                    for(UserSubscribedItem item : curr) {
//                        pgAdapter.add(item);
//                    }
                    for(DataSnapshot ds : snapshot.getChildren()){
                        String id = ds.getValue(String.class);
                        ArrayDeque<UserSubscribedItem> curr = new ArrayDeque<>();
                        database.getReference("Subscription").child(id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                UserSubscribedItem item = snapshot.getValue(UserSubscribedItem.class);
                                if(item.getCurrentlyActive().equals("true")){
                                    curr.addFirst(item);
                                } else {
                                    curr.addLast(item);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        pgAdapter.clear();
                        for(UserSubscribedItem item : curr) {
                            pgAdapter.add(item);
                        }
                    }

                } else {
                    userPGText.setVisibility(View.GONE);
                    userPGRecycler.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        userMessRecycle.setAdapter(messAdapter);
        LinearLayoutManager messLayoutManager = new LinearLayoutManager(this);
        messLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        userMessRecycle.setLayoutManager(messLayoutManager);

        database.getReference("UserSubscription").child("UserMess").child(currentUser.getuId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getChildrenCount() > 0){
                    userMessText.setVisibility(View.VISIBLE);
                    userMessRecycle.setVisibility(View.VISIBLE);
                    for(DataSnapshot ds : snapshot.getChildren()){
                        String id = ds.getValue(String.class);
                        ArrayDeque<UserSubscribedItem> curr = new ArrayDeque<>();
                        database.getReference("Subscription").child(id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                UserSubscribedItem item = snapshot.getValue(UserSubscribedItem.class);
                                if(item.getCurrentlyActive().equals("true")){
                                    curr.addFirst(item);
                                } else {
                                    curr.addLast(item);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        pgAdapter.clear();
                        for(UserSubscribedItem item : curr) {
                            pgAdapter.add(item);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        database.getReference("UserSubscription").child("UserMess").child(currentUser.getuId()).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(snapshot.getChildrenCount() > 0){
//                    userMessText.setVisibility(View.VISIBLE);
//                    userMessRecycle.setVisibility(View.VISIBLE);
//                    ArrayDeque<UserSubscribedItem> curr = new ArrayDeque<>();
//                    for(DataSnapshot ds : snapshot.getChildren()){
//                        UserSubscribedItem item = ds.getValue(UserSubscribedItem.class);
//                        if(item.getCurrentlyActive().equals("true")){
//                            curr.addFirst(item);
//                        } else {
//                            curr.addLast(item);
//                        }
//                    }
//                    messAdapter.clear();
//                    for(UserSubscribedItem item : curr) {
//                        messAdapter.add(item);
//                    }
//                } else {
//                    userMessText.setVisibility(View.GONE);
//                    userMessRecycle.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) { }
//        });
    }

}