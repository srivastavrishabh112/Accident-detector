package com.harsh.accidentdetector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.util.List;

public class Home extends AppCompatActivity {
    NavigationView nav;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;
    ImageView image;

    FirebaseAuth auth;
    FirebaseUser user;
TextView card;
    int REQUEST_LOCATION = 88;

    private int size;
    private LocationRequest locationRequest;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();

    }

    private void loadUserList() {
        AppDatabase db = AppDatabase.getDbInstance(this.getApplicationContext());
        List<User> userList =db.userDao().getAllUsers();
        size=userList.size();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        nav=findViewById(R.id.navmenu);
        drawerLayout=findViewById(R.id.drawer);
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        image=findViewById(R.id.imageView2);
        card=findViewById(R.id.card);
        getlocation();
        toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);

        toggle.syncState();

checkuser();
        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.logout:
                        FirebaseAuth.getInstance().signOut();
                        Intent intent3 = new Intent(Home.this, Login.class);

                        startActivity(intent3);
                        finish();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.contacts:
                        Intent intent2 = new Intent(Home.this, Contacts.class);

                        startActivity(intent2);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                }


                return true;
            }
        });

image.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        loadUserList();
        if (size==0) {
            Toast.makeText(Home.this,"Add at least 1 Contact First",Toast.LENGTH_LONG).show();
        }else {

            if ((ActivityCompat.checkSelfPermission(Home.this, Manifest.permission.ACCESS_FINE_LOCATION)) == PackageManager.PERMISSION_GRANTED) {
                location();
            } else {
                ActivityCompat.requestPermissions(Home.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 23);
            }

            Intent intent1 = new Intent(Home.this, Detection.class);
            startActivity(intent1);



        }
    }
});

card.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent det=new Intent(Home.this,Details.class);
        startActivity(det);
    }
});


    }
    private void checkuser(){

        View v=nav.getHeaderView(0);
        ImageView img=v.findViewById(R.id.dpimage);
        TextView name=v.findViewById(R.id.nameee);
        TextView email=v.findViewById(R.id.details);
        TextView phone=v.findViewById(R.id.numer);


        if (user==null){
            startActivity(new Intent(Home.this,Login.class));
            finish();
        }else{
            String emil=user.getEmail();
            email.setText(emil);
            String phn=user.getPhoneNumber();
            phone.setText(phn);


            String namee=user.getDisplayName();

            name.setText(namee);



            Uri uri=user.getPhotoUrl();
            if (uri!=null){

                Glide.with(this)
                        .load(uri)
                        .apply(RequestOptions.circleCropTransform())
                        .placeholder(R.drawable.ic_baseline_person_24)
                        .into(img);

            }

        }
    }

    private void getlocation() {

        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {

            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(Home.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }


        };
        TedPermission.create()
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this APP\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.SEND_SMS, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .check();
    }


    private void location() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());
        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);

                } catch (ApiException e) {
                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(Home.this, REQUEST_LOCATION);
                            } catch (IntentSender.SendIntentException sendIntentException) {

                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            break;
                    }

                }
            }
        });
    }

}