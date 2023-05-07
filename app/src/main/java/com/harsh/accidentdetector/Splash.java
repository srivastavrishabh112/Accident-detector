package com.harsh.accidentdetector;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {



            @Override

            public void run() {

                FirebaseAuth mauth=FirebaseAuth.getInstance();
                FirebaseUser user=mauth.getCurrentUser();
                if (user==null){
                    Intent i = new Intent(Splash.this, Login.class);

                    startActivity(i);
                    finish();
                }else{
                Intent i = new Intent(Splash.this, Home.class);

                startActivity(i);
                finish();

            }}

        },700);
    }
}