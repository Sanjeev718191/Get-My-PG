package com.androidaxe.getmypg.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Toast;

import com.androidaxe.getmypg.Module.PGOwner;
import com.androidaxe.getmypg.Module.PGUser;
import com.androidaxe.getmypg.R;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOwnerLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        binding.OwnerloginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(intent, RC_SIGN_IN);
            }
        });

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

    boolean flag = false;
    private void signInIfNotCustomer(GoogleSignInAccount account){

        if (account != null && account.getIdToken() != null)
            database.getReference().child("PGUser").orderByChild("email").startAt(account.getEmail()).endAt(account.getEmail()+"\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.getChildrenCount() > 0){
                        flag = true;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        new CountDownTimer(2000, 2000){

            @Override
            public void onTick(long l) { }

            @Override
            public void onFinish() {
                if(flag){
                    Toast.makeText(OwnerLoginActivity.this, "You are already a customer", Toast.LENGTH_SHORT).show();
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
                                    .child("PGOwner")
                                    .child(user.getUid()).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if(snapshot.getValue((String.class)) == null){
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

}