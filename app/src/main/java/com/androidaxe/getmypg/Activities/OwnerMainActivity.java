package com.androidaxe.getmypg.Activities;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidaxe.getmypg.Adapters.MessBusinessItemAdapter;
import com.androidaxe.getmypg.Adapters.PGBusinessItemAdapter;
import com.androidaxe.getmypg.Module.OwnerMess;
import com.androidaxe.getmypg.Module.OwnerPG;
import com.androidaxe.getmypg.Module.PGOwner;
import com.androidaxe.getmypg.R;
import com.androidaxe.getmypg.databinding.ActivityOwnerMainBinding;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.security.acl.Owner;

import de.hdodenhof.circleimageview.CircleImageView;


public class OwnerMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityOwnerMainBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    PGBusinessItemAdapter pgAdapter;
    MessBusinessItemAdapter messAdapter;
    RecyclerView pgRecycler;
    RecyclerView messRecycler;
    TextView pgtext;
    TextView messtext;
    GoogleSignInClient mGoogleSignInClient;
    ProgressDialog progressDialog;
    NavigationView navigationView;
    PGOwner curr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityOwnerMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarOwnerMain.toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, binding.drawerLayout, binding.appBarOwnerMain.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

//        binding.appBarOwnerMain.fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        DrawerLayout drawer = binding.drawerLayout;
        navigationView = binding.navViewOwner;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_add_pg, R.id.nav_add_mess, R.id.edit_profile_owner, R.id.owner_logout, R.id.share_owner, R.id.about_up_owner)
                .setOpenableLayout(drawer)
                .build();

        //My Code============================================================================================================

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        getSupportActionBar().setTitle("Home");
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        pgAdapter = new PGBusinessItemAdapter(this);
        messAdapter = new MessBusinessItemAdapter(this);

        pgtext = findViewById(R.id.com_PG_text);
        pgtext.setVisibility(View.GONE);
        messtext = findViewById(R.id.com_Mess_text);
        messtext.setVisibility(View.GONE);

        pgRecycler = findViewById(R.id.com_PG_Recycler);
        pgRecycler.setVisibility(View.GONE);
        messRecycler = findViewById(R.id.com_Mess_Recycler);
        messRecycler.setVisibility(View.GONE);

        checkData();
        NavigationMenuOnClick();
        getNewRequestNotification();

        View headerView = navigationView.getHeaderView(0);
        TextView ownerName = headerView.findViewById(R.id.OwnerNavigationBarName);
        TextView ownerEmail = headerView.findViewById(R.id.OwnerNavigationBarEmail);
        TextView ownerContact = headerView.findViewById(R.id.OwnerNavigationBarContactNumber);
        ownerEmail.setText(auth.getCurrentUser().getEmail());
        CircleImageView OwnerImage = headerView.findViewById(R.id.OwnerNavigationBarImageView);
        database.getReference("PGOwner").child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                curr = snapshot.getValue(PGOwner.class);
                ownerName.setText(curr.getName());
                ownerContact.setText("Contact : "+curr.getContact());
                Glide.with(OwnerMainActivity.this)
                        .load(curr.getProfile())
                        .into(OwnerImage);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(OwnerMainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        pgRecycler.setAdapter(pgAdapter);
        LinearLayoutManager pgLayoutManager = new LinearLayoutManager(this);
        pgLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        pgRecycler.setLayoutManager(pgLayoutManager);

        database.getReference("OwnerPGs").child(auth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pgAdapter.clear();
                if(snapshot.getChildrenCount() > 0) {
                    pgtext.setVisibility(View.VISIBLE);
                    pgRecycler.setVisibility(View.VISIBLE);
                    for(DataSnapshot ds : snapshot.getChildren()){
                        String pgid = ds.getValue(String.class);
                        FirebaseDatabase.getInstance().getReference("PGs").child(pgid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                OwnerPG pg = snapshot.getValue(OwnerPG.class);
                                if(pg != null) pgAdapter.add(pg);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {}
                        });
                    }
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        messRecycler.setAdapter(messAdapter);
        LinearLayoutManager messLayoutManager = new LinearLayoutManager(this);
        messLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        messRecycler.setLayoutManager(messLayoutManager);

        database.getReference("OwnerMess").child(auth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messAdapter.clear();
                if(snapshot.getChildrenCount() > 0){
                    messtext.setVisibility(View.VISIBLE);
                    messRecycler.setVisibility(View.VISIBLE);
                    for(DataSnapshot ds : snapshot.getChildren()){
                        String messid = ds.getValue(String.class);
                        FirebaseDatabase.getInstance().getReference("Mess").child(messid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                OwnerMess mess = snapshot.getValue(OwnerMess.class);
                                if(mess != null) messAdapter.add(mess);
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

            }
        });

    }

    private void NavigationMenuOnClick(){
        navigationView.getMenu().findItem(R.id.nav_home).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                binding.drawerLayout.closeDrawer(GravityCompat.START);
                return false;
            }
        });

        navigationView.getMenu().findItem(R.id.nav_add_pg).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                Intent intent = new Intent(OwnerMainActivity.this, AddNewBussinessActivity.class);
                intent.putExtra("type", "pg");
                intent.putExtra("id", "new");
                startActivity(intent);
                return false;
            }
        });

        navigationView.getMenu().findItem(R.id.nav_add_mess).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                Intent intent = new Intent(OwnerMainActivity.this, AddNewBussinessActivity.class);
                intent.putExtra("type", "mess");
                intent.putExtra("id", "new");
                startActivity(intent);
                return false;
            }
        });

        navigationView.getMenu().findItem(R.id.edit_profile_owner).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                binding.drawerLayout.closeDrawer(GravityCompat.START);
                startActivity(new Intent(OwnerMainActivity.this, OwnerSetProfileActivity.class));
                return false;
            }
        });
        navigationView.getMenu().findItem(R.id.owner_requests_menu).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                binding.drawerLayout.closeDrawer(GravityCompat.START);
                startActivity(new Intent(OwnerMainActivity.this, OwnerRequestsActivity.class));
                return false;
            }
        });
        navigationView.getMenu().findItem(R.id.owner_logout).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {

                mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        auth.signOut();
                        startActivity(new Intent(OwnerMainActivity.this, SelectUserActivity.class));
                        finishAffinity();
                    }
                });
                return false;
            }
        });
        navigationView.getMenu().findItem(R.id.share_owner).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody =  "http://play.google.com/store/apps/detail?id=" + getPackageName();
                String shareSub = "Try Get My PG app now";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSub);
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share using"));

                return false;
            }
        });
        navigationView.getMenu().findItem(R.id.about_up_owner).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                Intent intent = new Intent(OwnerMainActivity.this, AboutUsActivity.class);
                intent.putExtra("isSeller", "Yes");
                startActivity(intent);
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.owner_main, menu);
        return true;
    }

//    @Override
//    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_owner_main);
//        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
//                || super.onSupportNavigateUp();
//    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.owner_main_action_refresh:
                startActivity(new Intent(OwnerMainActivity.this, OwnerMainActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    private void checkData(){
        database.getReference("OwnerPGs").child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getChildrenCount() == 0){
                    database.getReference().child("OwnerMess").child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.getChildrenCount() == 0){
                                AlertDialog.Builder builder = new AlertDialog.Builder(OwnerMainActivity.this);
                                builder.setTitle("No Data");
                                builder.setMessage("No PG(Hostel) or Mess is added. Please add to continue.");

                                builder.setPositiveButton("Add Hostel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(OwnerMainActivity.this, AddNewBussinessActivity.class);
                                        intent.putExtra("type", "pg");
                                        intent.putExtra("id", "new");
                                        startActivity(intent);
                                    }
                                });
                                builder.setNegativeButton("Add Mess", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(OwnerMainActivity.this, AddNewBussinessActivity.class);
                                        intent.putExtra("type", "mess");
                                        intent.putExtra("id", "new");
                                        startActivity(intent);
                                    }
                                });

                                // create and show the alert dialog
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
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

    private void getNewRequestNotification(){
        database.getReference().child("Requests").child("OwnerMessRequests").child(auth.getUid()).child("newRequest").child("NewRequest").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String s = snapshot.getValue(String.class);
                if(s.equals("Yes")){
                    setNewRequestNotice();
                    database.getReference().child("Requests").child("OwnerMessRequests").child(auth.getUid()).child("newRequest").child("NewRequest").setValue("No");
                    database.getReference().child("Requests").child("OwnerPGRequests").child(auth.getUid()).child("newRequest").child("NewRequest").setValue("No");
                } else {
                    database.getReference().child("Requests").child("OwnerPGRequests").child(auth.getUid()).child("newRequest").child("NewRequest").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String s1 = snapshot.getValue(String.class);
                            if(s1.equals("Yes")){
                                setNewRequestNotice();
                                database.getReference().child("Requests").child("OwnerPGRequests").child(auth.getUid()).child("newRequest").child("NewRequest").setValue("No");
                            } else {
                                findViewById(R.id.com_notification_card).setVisibility(View.GONE);
                            }
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

    boolean colorFlag;
    private void setNewRequestNotice(){
        findViewById(R.id.com_notification_card).setVisibility(View.VISIBLE);
        TextView textView = findViewById(R.id.com_notification_text);
        colorFlag = false;
        textView.setText("You have new Requests click to see details.");
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OwnerMainActivity.this, OwnerRequestsActivity.class));
            }
        });
        new CountDownTimer(3200, 400){

            @Override
            public void onTick(long l) {
                if(colorFlag){
                    findViewById(R.id.com_notification_card).setBackgroundDrawable(getDrawable(R.drawable.input_background));
                    colorFlag = !colorFlag;
                } else {
                    findViewById(R.id.com_notification_card).setBackgroundDrawable(getDrawable(R.drawable.input_backround_highlighted));
                    colorFlag = !colorFlag;
                }
            }

            @Override
            public void onFinish() {
                findViewById(R.id.com_notification_card).setBackgroundDrawable(getDrawable(R.drawable.input_background));
            }
        }.start();

    }

    @Override
    public void onBackPressed() {
        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}