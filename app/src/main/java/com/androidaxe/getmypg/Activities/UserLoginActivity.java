package com.androidaxe.getmypg.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.androidaxe.getmypg.Module.PGOwner;
import com.androidaxe.getmypg.Module.PGUser;
import com.androidaxe.getmypg.R;
import com.androidaxe.getmypg.Utils.Constants;
import com.androidaxe.getmypg.databinding.ActivityUserLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserLoginActivity extends AppCompatActivity {

    ActivityUserLoginBinding binding;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    int RC_SIGN_IN = 10;
    String demoLogin;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setTitle("User Login");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Checking Info");
        progressDialog.setMessage("Please wait, while we are checking info...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        database.getReference("DemoLogin").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                demoLogin = snapshot.getValue(String.class);
                if(demoLogin != null){
                    setUserLogin();
                } else {
                    Toast.makeText(UserLoginActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserLoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        binding.UserloginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(intent, RC_SIGN_IN);
            }
        });

        binding.privacyPolicyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(Constants.privacyPolicy));
                startActivity(intent);
            }
        });

        binding.userDemoLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.demoEmail.getText().toString().equals("")){
                    binding.demoEmail.setError("Please enter your email id");
                } else if(binding.demoPassword.getText().toString().equals("")){
                    binding.demoPassword.setError("Please enter your password");
                } else {
                    loginWithEmailPassword();
                }
            }
        });

    }

    private void setUserLogin() {
        if(demoLogin.equals("enable")){
            binding.demoEmail.setVisibility(View.VISIBLE);
            binding.demoPassword.setVisibility(View.VISIBLE);
            binding.userDemoLoginButton.setVisibility(View.VISIBLE);

            binding.userLoginMainIcon.setVisibility(View.GONE);
            binding.UserloginBtn.setVisibility(View.GONE);
        } else {
            binding.demoEmail.setVisibility(View.GONE);
            binding.demoPassword.setVisibility(View.GONE);
            binding.userDemoLoginButton.setVisibility(View.GONE);

            binding.userLoginMainIcon.setVisibility(View.VISIBLE);
            binding.UserloginBtn.setVisibility(View.VISIBLE);
        }
        progressDialog.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult();
                signInIfNotOwner(account);
            } catch (Exception e){
                Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    }

    boolean flag = false;
    private void signInIfNotOwner(GoogleSignInAccount account){

        if (account != null && account.getIdToken() != null) {
            database.getReference().child("PGOwner").orderByChild("email").startAt(account.getEmail()).endAt(account.getEmail() + "\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getChildrenCount() > 0) {
                        flag = true;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        new CountDownTimer(2000, 2000){

            @Override
            public void onTick(long l) { }

            @Override
            public void onFinish() {
                if(flag){
                    Toast.makeText(UserLoginActivity.this, "You are already a seller", Toast.LENGTH_SHORT).show();
                    mGoogleSignInClient.signOut();
                } else {
                    authWithGoogle(account.getIdToken());
                }
            }
        }.start();
    }

    private void authWithGoogle(String idToken) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Checking Info");
        progressDialog.setMessage("Please wait, while we are checking info...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = mAuth.getCurrentUser();
                            database.getReference()
                                    .child("PGUser")
                                    .child(user.getUid()).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if(snapshot.getValue((String.class)) == null){
                                                PGUser firebaseUser = new PGUser(user.getUid(), user.getDisplayName(), user.getPhotoUrl().toString(), "+91 XXXXXXXXXX", "PGUser", user.getEmail());
                                                database.getReference()
                                                        .child("PGUser")
                                                        .child(user.getUid())
                                                        .setValue(firebaseUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    progressDialog.dismiss();
                                                                    startActivity(new Intent(UserLoginActivity.this, UserSetProfileActivity.class));
                                                                    finishAffinity();
                                                                } else {
                                                                    progressDialog.dismiss();
                                                                    Toast.makeText(UserLoginActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                            } else {
                                                progressDialog.dismiss();
                                                startActivity(new Intent(UserLoginActivity.this, UserMainActivity.class));
                                                finishAffinity();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            progressDialog.dismiss();
                                            Toast.makeText(UserLoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(UserLoginActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void loginWithEmailPassword() {
        String email = binding.demoEmail.getText().toString().trim();
        String password = binding.demoPassword.getText().toString().trim();
        progressDialog.show();
        database.getReference("PGUser").child("DemoUser").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!email.equals(snapshot.child("email").getValue(String.class))){
                    Toast.makeText(UserLoginActivity.this, "Enter correct email", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else if (!password.equals(snapshot.child("password").getValue(String.class))) {
                    Toast.makeText(UserLoginActivity.this, "Enter correct password", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else {
                    if(snapshot.child("firstTime").getValue(String.class).equals("No")){
                        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()) {
                                    progressDialog.dismiss();
                                    startActivity(new Intent(UserLoginActivity.this, UserMainActivity.class));
                                    finishAffinity();
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(UserLoginActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                PGUser firebaseUser = new PGUser(user.getUid(), user.getDisplayName(), "", "+91 XXXXXXXXXX", "PGOwner", user.getEmail());
                                database.getReference().child("PGUser").child(user.getUid()).setValue(firebaseUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            database.getReference("PGUser").child("DemoUser").child("firstTime").setValue("No");
                                            progressDialog.dismiss();
                                            startActivity(new Intent(UserLoginActivity.this, UserSetProfileActivity.class));
                                            finishAffinity();
                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(UserLoginActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserLoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}