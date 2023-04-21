package com.androidaxe.getmypg.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.androidaxe.getmypg.Module.NewSeller;
import com.androidaxe.getmypg.Module.PGOwner;
import com.androidaxe.getmypg.Module.PGUser;
import com.androidaxe.getmypg.R;
import com.androidaxe.getmypg.Utils.Constants;
import com.androidaxe.getmypg.databinding.ActivityOwnerLoginBinding;
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

public class OwnerLoginActivity extends AppCompatActivity {

    ActivityOwnerLoginBinding binding;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    int RC_SIGN_IN = 11;
    ProgressDialog progressDialog;
    String restrictSeller, demoLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOwnerLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Seller/Owner Login");

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
                    if(demoLogin.equals("enable")){
                        setDemoLogin();
                    } else {
                        restrictSeller = "disable";
                        database.getReference("RestrictSellerLogin").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                restrictSeller = snapshot.getValue(String.class);
                                setSellerLoginMethod();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(OwnerLoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    Toast.makeText(OwnerLoginActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OwnerLoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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

        binding.OwnerloginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(intent, RC_SIGN_IN);
            }
        });

        binding.ownerLoginNewSellerSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(OwnerLoginActivity.this)
                        .setTitle("New Seller/Owner Registration")
                        .setMessage("Mail us your details to verify at get getmypg.help@gmail.com\nMandatory details :-\nSeller gmail, Contact number, Your business name and photos to verify.")
                        .setPositiveButton("Send Email", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ShareCompat.IntentBuilder.from(OwnerLoginActivity.this)
                                        .setType("message/rfc822")
                                        .addEmailTo("getmypg.help@gamil.com")
                                        .setSubject("New Seller Registration Request")
                                        .setText("Hi, I'm ")
                                        .setChooserTitle("Choose an email client")
                                        .startChooser();
                                dialog.dismiss();
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        binding.ownerDemoLoginButton.setOnClickListener(new View.OnClickListener() {
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

    private void setDemoLogin() {

        binding.demoEmail.setVisibility(View.VISIBLE);
        binding.demoPassword.setVisibility(View.VISIBLE);
        binding.ownerDemoLoginButton.setVisibility(View.VISIBLE);

        binding.ownerLoginMainIcon.setVisibility(View.GONE);
        binding.ownerLoginNewSellerSignup.setVisibility(View.GONE);
        binding.OwnerloginBtn.setVisibility(View.GONE);
        progressDialog.dismiss();

    }

    private void setSellerLoginMethod() {

        binding.demoEmail.setVisibility(View.GONE);
        binding.demoPassword.setVisibility(View.GONE);
        binding.ownerDemoLoginButton.setVisibility(View.GONE);

        binding.ownerLoginMainIcon.setVisibility(View.VISIBLE);
        if(restrictSeller.equals("enable")){
            binding.textView112.setText("Sign in as an existing seller");
            binding.ownerLoginNewSellerSignup.setVisibility(View.VISIBLE);
        } else {
            binding.textView112.setText("Sign in as seller");
            binding.ownerLoginNewSellerSignup.setVisibility(View.GONE);
        }
        binding.OwnerloginBtn.setVisibility(View.VISIBLE);
        progressDialog.dismiss();

    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult();
                signInIfNotCustomer(account);
            } catch (Exception e){
                Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    }


    private void signInIfNotCustomer(GoogleSignInAccount account){
        progressDialog.show();
        if (account != null && account.getIdToken() != null)
            database.getReference().child("PGUser").orderByChild("email").startAt(account.getEmail()).endAt(account.getEmail()+"\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.getChildrenCount() > 0){
                        Toast.makeText(OwnerLoginActivity.this, "You are already a customer.", Toast.LENGTH_SHORT).show();
                        mGoogleSignInClient.signOut();
                    } else if(restrictSeller.equals("enable")) {
                        database.getReference().child("PGOwner").child("NewOwner").orderByChild("email").startAt(account.getEmail()).endAt(account.getEmail()+"\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.getChildrenCount() > 0){
                                    for (DataSnapshot ds : snapshot.getChildren()){
                                        NewSeller newSeller = ds.getValue(NewSeller.class);
                                        if(newSeller != null && newSeller.getEmail() != null && newSeller.getEmail().equals(account.getEmail())){
                                            authWithGoogle(account.getIdToken());
                                            database.getReference().child("PGOwner").child("NewOwner").child(newSeller.getId()).removeValue();
//                                            Toast.makeText(OwnerLoginActivity.this, snapshot.getKey(), Toast.LENGTH_SHORT).show();
                                            return;
                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(OwnerLoginActivity.this, "Unable to get data", Toast.LENGTH_SHORT).show();
                                            mGoogleSignInClient.signOut();
                                        }
                                    }

                                } else {
                                    database.getReference().child("PGOwner").orderByChild("email").startAt(account.getEmail()).endAt(account.getEmail()+"\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if(snapshot.getChildrenCount() > 0){
                                                authWithGoogle(account.getIdToken());
                                            } else {
                                                progressDialog.dismiss();
                                                Toast.makeText(OwnerLoginActivity.this, "For new seller registration please mail Data to our team.", Toast.LENGTH_SHORT).show();
                                                mGoogleSignInClient.signOut();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            progressDialog.dismiss();
                                            mGoogleSignInClient.signOut();
                                            Toast.makeText(OwnerLoginActivity.this, "Unable to get data.", Toast.LENGTH_SHORT).show();

                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                progressDialog.dismiss();
                                mGoogleSignInClient.signOut();
                                Toast.makeText(OwnerLoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        authWithGoogle(account.getIdToken());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressDialog.dismiss();
                    mGoogleSignInClient.signOut();
                    Toast.makeText(OwnerLoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        else {
            progressDialog.dismiss();
            mGoogleSignInClient.signOut();
            Toast.makeText(this, "Unable to get data.", Toast.LENGTH_SHORT).show();
        }
    }

    private void authWithGoogle(String idToken) {

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = mAuth.getCurrentUser();
                            database.getReference()
                                    .child("PGOwner")
                                    .child(user.getUid()).child("profile").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if(snapshot.getValue(String.class) == null){
                                                PGOwner firebaseUser = new PGOwner(user.getUid(), user.getDisplayName(), user.getPhotoUrl().toString(), "+91 XXXXXXXXXX", "PGOwner", user.getEmail());
                                                database.getReference()
                                                        .child("PGOwner")
                                                        .child(user.getUid())
                                                        .setValue(firebaseUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    progressDialog.dismiss();
                                                                    startActivity(new Intent(OwnerLoginActivity.this, OwnerSetProfileActivity.class));
                                                                    finishAffinity();
                                                                } else {
                                                                    progressDialog.dismiss();
                                                                    Toast.makeText(OwnerLoginActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                            } else {
                                                progressDialog.dismiss();
                                                startActivity(new Intent(OwnerLoginActivity.this, OwnerMainActivity.class));
                                                finishAffinity();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            progressDialog.dismiss();
                                            Toast.makeText(OwnerLoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(OwnerLoginActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void loginWithEmailPassword() {

        String email = binding.demoEmail.getText().toString().trim();
        String password = binding.demoPassword.getText().toString().trim();
        progressDialog.show();
        database.getReference("PGOwner").child("DemoOwner").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!email.equals(snapshot.child("email").getValue(String.class))){
                    Toast.makeText(OwnerLoginActivity.this, "Enter correct email", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else if (!password.equals(snapshot.child("password").getValue(String.class))) {
                    Toast.makeText(OwnerLoginActivity.this, "Enter correct password", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else {
                    if(snapshot.child("firstTime").getValue(String.class).equals("No")){
                        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()) {
                                    progressDialog.dismiss();
                                    startActivity(new Intent(OwnerLoginActivity.this, OwnerMainActivity.class));
                                    finishAffinity();
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(OwnerLoginActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                PGOwner firebaseUser = new PGOwner(user.getUid(), user.getDisplayName(), "", "+91 XXXXXXXXXX", "PGOwner", user.getEmail());
                                database.getReference().child("PGOwner").child(user.getUid()).setValue(firebaseUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            database.getReference("PGOwner").child("DemoOwner").child("firstTime").setValue("No");
                                            progressDialog.dismiss();
                                            startActivity(new Intent(OwnerLoginActivity.this, OwnerSetProfileActivity.class));
                                            finishAffinity();
                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(OwnerLoginActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(OwnerLoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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