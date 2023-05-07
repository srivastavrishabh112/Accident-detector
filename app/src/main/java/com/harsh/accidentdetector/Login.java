package com.harsh.accidentdetector;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;


public class Login extends AppCompatActivity {
    Button btn;
    ImageView google;
    ProgressBar progressBar;
    EditText phone;
    FirebaseUser user;
    String number;
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth firebaseAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btn = findViewById(R.id.button);
        phone = findViewById(R.id.phone);
        google = findViewById(R.id.google);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        firebaseAuth = FirebaseAuth.getInstance();


        user = firebaseAuth.getCurrentUser();
        checkuser();

        if (user == null) {


            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    number = phone.getText().toString();
                    if (number != null && number.length() >= 10) {

                        progressBar.setVisibility(View.VISIBLE);
                        otpsend();

                    } else {
                        Toast.makeText(Login.this, "Invalid Number", Toast.LENGTH_SHORT).show();
                    }


                }
            });

        }

    }

    private void otpsend() {


        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(Login.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                progressBar.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(Login.this, verify.class);
                intent.putExtra("num", number);
                intent.putExtra("verificationId", verificationId);
                startActivity(intent);
            }
        };

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber("+91" + number)
                        .setTimeout(10L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }


    private void checkuser() {


        if (user != null) {
            startActivity(new Intent(Login.this, Home.class));
            finish();
        } else {
            GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.client_id))
                    .requestEmail()
                    .build();


            googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
            google.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    progressBar.setVisibility(View.VISIBLE);
                    Intent intent = googleSignInClient.getSignInIntent();

                    startActivityForResult(intent, 100);
                }
            });
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {

            Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(data);


            try {
                GoogleSignInAccount account = accountTask.getResult(ApiException.class);
                authhhh(account.getIdToken());
            } catch (ApiException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void authhhh(String token) {

        if (token != null) {
            AuthCredential credential = GoogleAuthProvider.getCredential(token, null);
            firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        startActivity(new Intent(Login.this, Home.class));
                        progressBar.setVisibility(View.INVISIBLE);
                        finish();
                    }
                }
            });
        } else {
            Toast.makeText(Login.this, "Empty", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.INVISIBLE);
        }

    }


}