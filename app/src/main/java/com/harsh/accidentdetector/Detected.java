package com.harsh.accidentdetector;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.List;

public class Detected extends AppCompatActivity {


    TextView timer;
    List<User> userList;
    Button yes, no;
    private LocationRequest locationRequest;
    CountDownTimer countDownTimer;


    @Override
    public void onBackPressed() {
        countDownTimer.onFinish();
         finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detected);
        yes = findViewById(R.id.button3);
        no = findViewById(R.id.button4);
        timer = findViewById(R.id.timer);



        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);


        Intent intent=new Intent(Detected.this,Home.class);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        loadUserList();

        countDownTimer = new CountDownTimer(31000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int ak = (int) (millisUntilFinished / 1000);
                timer.setText(String.valueOf(ak));
                if (ak==0){
                    sendsos();
                    cancel();
                    finish();

                }

            }

            @Override
            public void onFinish() {
                cancel();

            }
        }.start();


        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendsos();
                countDownTimer.onFinish();
                finish();

                startActivity(intent);
            }
        });


        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                countDownTimer.onFinish();
                finish();

            }
        });
    }
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if (requestCode == 1) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                getCurrentLocation(phonenumber);
//            }
//        }
//
//
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == 2) {
//            if (resultCode == Activity.RESULT_OK) {
//
//                getCurrentLocation(phonenumber);
//            }
//        }
//    }
    private void getCurrentLocation(String phone) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(Detected.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {


                SmsManager sms = SmsManager.getDefault();


                String phoneNumber;
                phoneNumber = phone;

                LocationServices.getFusedLocationProviderClient(Detected.this)
                        .requestLocationUpdates(locationRequest, new LocationCallback() {
                            @Override
                            public void onLocationResult(@NonNull LocationResult locationResult) {
                                super.onLocationResult(locationResult);

                                LocationServices.getFusedLocationProviderClient(Detected.this)
                                        .removeLocationUpdates(this);

                                if (locationResult != null && locationResult.getLocations().size() > 0) {


                                    int index = locationResult.getLocations().size() - 1;
                                    double latitude = locationResult.getLocations().get(index).getLatitude();
                                    double longitude = locationResult.getLocations().get(index).getLongitude();
                                    String messageBody = "I Need Your Help ! \n Please take me from :\n https://maps.google.com/?q=" + (latitude) + "," + (longitude);
                                    try {
                                        sms.sendTextMessage(phoneNumber, null, messageBody, null, null);


                                        Toast.makeText(getApplicationContext(), "S.O.S. message sent !", Toast.LENGTH_LONG).show();
                                    } catch (Exception e) {

                                        Toast.makeText(getApplicationContext(), "Message sending failed !!!", Toast.LENGTH_LONG).show();
                                    }

                                }
                            }
                        }, Looper.getMainLooper());

            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }


    private void loadUserList() {
        AppDatabase db = AppDatabase.getDbInstance(this.getApplicationContext());
        userList = db.userDao().getAllUsers();

    }
    private void sendsos() {
        for (int i = 0; i < userList.size(); i++) {
            String phonenumber = String.valueOf(userList.get(i).Number);
            getCurrentLocation(phonenumber);
        }
    }
}