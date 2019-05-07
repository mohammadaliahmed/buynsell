package com.appsinventiv.buyandsell.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.appsinventiv.buyandsell.Models.User;
import com.appsinventiv.buyandsell.R;
import com.appsinventiv.buyandsell.Utils.CommonUtils;
import com.appsinventiv.buyandsell.Utils.PrefManager;
import com.appsinventiv.buyandsell.Utils.SharedPrefs;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Login extends AppCompatActivity {
    Button signup, login;
    EditText password, username;
    DatabaseReference mDatabase;
    ArrayList<String> userList = new ArrayList<>();
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        password = findViewById(R.id.password);
        username = findViewById(R.id.username);
        signup = findViewById(R.id.signup);
        login = findViewById(R.id.login);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        prefManager = new PrefManager(this);
        if (!prefManager.isFirstTimeLaunch()) {
            launchHomescreen();
            finish();
        }

        getAllUsersFromDB();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (username.getText().length() == 0) {
                    username.setError("Enter email");
                } else if (password.getText().length() == 0) {
                    password.setError("Enter password");
                } else {
                    if (userList.contains(username.getText().toString())) {
                        loginUser();

                    } else {
                        CommonUtils.showToast("Username does not exit\nPlease signup");
                    }
                }
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Login.this, SignUp.class);
                startActivity(i);
                finish();
            }
        });

    }

    private void getAllUsersFromDB() {
        mDatabase.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String usernames = snapshot.getKey();
                        userList.add(usernames);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loginUser() {
        mDatabase.child("Users").child(username.getText().toString())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            User user = dataSnapshot.getValue(User.class);
                            if (user != null) {
                                if (password.getText().toString().equals(user.getPassword())) {
                                    CommonUtils.showToast("Login successful");
                                    SharedPrefs.setUsername(user.getUsername());
                                    SharedPrefs.setName(user.getName());
                                    SharedPrefs.setIsLoggedIn("yes");
                                    launchHomescreen();
                                } else {
                                    CommonUtils.showToast("Wrong password");
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void launchHomescreen() {
        prefManager.setFirstTimeLaunch(false);
        startActivity(new Intent(Login.this, MainActivity.class));


        finish();
    }
}
