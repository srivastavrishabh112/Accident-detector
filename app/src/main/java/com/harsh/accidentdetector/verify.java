package com.harsh.accidentdetector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class verify extends AppCompatActivity {
    TextView sent,resendcode,timer;
    EditText t2;
    Button verifykaro;
    String number;
    FirebaseAuth mAuth;
    String verif;
    CountDownTimer timerr;
    ProgressBar bar;



    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        sent=findViewById(R.id.sent);
        t2=findViewById(R.id.otp);
        resendcode=findViewById(R.id.resendcode);
        resendcode.setVisibility(View.INVISIBLE);
        timer=findViewById(R.id.timer);

        verifykaro=findViewById(R.id.verifykaro);
        number=getIntent().getStringExtra("num").toString();
        verif = getIntent().getStringExtra("verificationId");
        mAuth=FirebaseAuth.getInstance();
        bar=findViewById(R.id.progressBar2);
        bar.setVisibility(View.INVISIBLE);
        timerr=new CountDownTimer(60000,1000) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long l) {
                timer.setText("Resend Otp in : "+ l/1000);

            }

            @Override
            public void onFinish() {
                timer.setVisibility(View.INVISIBLE);
                resendcode.setVisibility(View.VISIBLE);
            }
        }.start();

        sent.setText("Otp has been sent to "+number);
        resendcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resendotp();
                resendcode.setVisibility(View.INVISIBLE);
                timer.setVisibility(View.VISIBLE);
                timerr.start();
            }


        });

        verifykaro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(t2.getText().toString().isEmpty()) {

                    Toast.makeText(getApplicationContext(), "Blank Field can not be processed", Toast.LENGTH_LONG).show();
                }
                else if(t2.getText().toString().length()!=6) {
                    Toast.makeText(getApplicationContext(), "Invalid OTP", Toast.LENGTH_LONG).show();

                }
                else
                {
                    timerr.cancel();
                    PhoneAuthCredential credential= PhoneAuthProvider.getCredential(verif,t2.getText().toString());
                    signInWithPhoneAuthCredential(credential);
                    bar.setVisibility(View.VISIBLE);

                }

            }
        });
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {

                            startActivity(new Intent(verify.this,Home.class));
                            bar.setVisibility(View.INVISIBLE);
                            finish();

                        } else {
                            Toast.makeText(getApplicationContext(),"Invalid OTP",Toast.LENGTH_LONG).show();
                            bar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }



    private void resendotp() {
        timerr.cancel();
        Intent intent=new Intent(verify.this,Login.class);
        startActivity(intent);
        finish();
    }
}