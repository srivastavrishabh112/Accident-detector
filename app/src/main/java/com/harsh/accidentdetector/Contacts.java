package com.harsh.accidentdetector;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class Contacts extends AppCompatActivity {


    private static final int CONTACT_PICK_CODE = 2;
    TextView empty;
    FloatingActionButton floatbutton;
    RecyclerView recyclerView;
    private listadapter adapter;
    private List<User> userList;

    private void checkPermission() {

        if (ContextCompat.checkSelfPermission(Contacts.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Contacts.this, new String[]{Manifest.permission.READ_CONTACTS}, 1);
        } else {
            pickcontact();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            pickcontact();
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();

        }
    }

    private void pickcontact() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, CONTACT_PICK_CODE);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CONTACT_PICK_CODE) {
                Cursor cursor1, cursor2;
                Uri uri = data.getData();
                cursor1 = getContentResolver().query(uri, null, null, null, null);
                if (cursor1.moveToFirst()) {
                    @SuppressLint("Range") String contactId = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts._ID));
                    @SuppressLint("Range") String conname = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    @SuppressLint("Range") String resul = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                    int resid = Integer.parseInt(resul);
                    if (resid == 1) {
                        cursor2 = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                                null,
                                null);
                        while (cursor2.moveToNext()) {
                            @SuppressLint("Range") String contactnum = cursor2.getString(cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            insertt(conname, contactnum);
                            loadUserList();

                        }
                        cursor2.close();
                    }
                    cursor1.close();
                }

            }

        }


    }

    private void insertt(String nam, String numm) {
        AppDatabase db = AppDatabase.getDbInstance(this.getApplicationContext());
        User user = new User();
        User usr = new User();
        user.Name = nam;
        user.Number = numm;
        db.userDao().insertUser(user);
        loadUserList();
        for (int i = 0; i < userList.size() - 1; i++) {
            if (userList.get(i).Name.equals(userList.get(i + 1).Name)) {
                usr = userList.get(i);
                db.userDao().delete(usr);
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        floatbutton = findViewById(R.id.floatbutton);
        empty = findViewById(R.id.empty);
        empty.setVisibility(View.INVISIBLE);

        floatbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();

            }
        });

        initRecyclerView();
        loadUserList();


    }


    private void initRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        adapter = new listadapter(this);

        recyclerView.setAdapter(adapter);
        loadUserList();


    }


    private void loadUserList() {
        AppDatabase db = AppDatabase.getDbInstance(this.getApplicationContext());
        userList = db.userDao().getAllUsers();
        checkempty();
        adapter.setUserList(userList);
    }

    private void checkempty() {
        if (userList.isEmpty()) {
            empty.setVisibility(View.VISIBLE);
        } else {
            empty.setVisibility(View.INVISIBLE);
        }
    }

}




