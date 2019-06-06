package com.appsinventiv.buyandsell.Activities.Customer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.appsinventiv.buyandsell.Activities.MainActivity;
import com.appsinventiv.buyandsell.Models.User;
import com.appsinventiv.buyandsell.R;
import com.appsinventiv.buyandsell.Utils.CommonUtils;
import com.appsinventiv.buyandsell.Utils.PrefManager;
import com.appsinventiv.buyandsell.Utils.SharedPrefs;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SignUp extends AppCompatActivity {
    Button signup;
    TextView login;
    DatabaseReference mDatabase;
    private PrefManager prefManager;
    ArrayList<String> userslist = new ArrayList<String>();
    EditText e_fullname, e_username, e_email, e_password, e_phone, e_city, e_confirmPassword;
    String fullname, username, email, password, phone, confirmPassword;

    ArrayList<String> usernameList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        this.setTitle("Signup");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        prefManager = new PrefManager(this);
        if (!prefManager.isFirstTimeLaunch()) {
            launchHomescreen();
            finish();
        }

        getAllUserFromDB();


        e_fullname = (EditText) findViewById(R.id.name);
        e_username = (EditText) findViewById(R.id.username);
        e_email = (EditText) findViewById(R.id.email);
        e_password = (EditText) findViewById(R.id.password);
        e_phone = (EditText) findViewById(R.id.phone);
        e_city = (EditText) findViewById(R.id.city);
        e_confirmPassword = (EditText) findViewById(R.id.confirmPassword);


        signup = (Button) findViewById(R.id.signup);
        login = findViewById(R.id.login);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (e_fullname.getText().toString().length() == 0) {
                    e_fullname.setError("Please enter your name");
                } else if (e_username.getText().toString().length() == 0) {
                    e_username.setError("Please enter username");
                } else if (e_email.getText().toString().length() == 0) {
                    e_email.setError("Please enter your email");
                } else if (e_password.getText().toString().length() == 0) {
                    e_password.setError("Please enter your password");
                } else if (e_confirmPassword.getText().toString().length() == 0) {
                    e_confirmPassword.setError("Please enter password again");
                } else if (!e_password.getText().toString().equalsIgnoreCase(e_confirmPassword.getText().toString())) {
                    e_confirmPassword.setError("Password does not match");
                } else if (e_phone.getText().toString().length() == 0) {
                    e_phone.setError("Please enter your phone number");
                } else {
                    signUpUser();

                }


            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignUp.this, Login.class);
                startActivity(i);
                finish();
            }
        });

    }

    private void getAllUserFromDB() {
        mDatabase.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String usernames = snapshot.getKey();
                        usernameList.add(usernames);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void signUpUser() {
        fullname = e_fullname.getText().toString();
        username = e_username.getText().toString();
        email = e_email.getText().toString();
        password = e_password.getText().toString();
        phone = e_phone.getText().toString();
//        city = e_city.getText().toString();

        if (userslist.contains("" + username)) {
            CommonUtils.showToast("Username is already taken\nPlease choose another");
        } else {
            final User user = new User(fullname, username, email,
                    password, phone, SharedPrefs.getFcmKey(), System.currentTimeMillis(), false);
            mDatabase.child("Users")
                    .child(username)
                    .setValue(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            CommonUtils.showToast("Thank you for registering");
                            SharedPrefs.setUser(user);
                            SharedPrefs.setUsername(username);
                            SharedPrefs.setPhone(phone);
                            SharedPrefs.setName(fullname);
                            SharedPrefs.setIsLoggedIn("yes");
//                            launchHomescreen();
                            launchPhoneVerficationScreen(phone);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    CommonUtils.showToast("There was some error");
                }

            });
        }

    }

    private void launchPhoneVerficationScreen(String phone) {
        Intent i=new Intent(SignUp.this,PhoneVerification.class);
        i.putExtra("phone",phone);
        startActivity(i);
    }
    private void launchHomescreen() {
        prefManager.setFirstTimeLaunch(false);
        startActivity(new Intent(SignUp.this, MainActivity.class));


        finish();
    }
}
