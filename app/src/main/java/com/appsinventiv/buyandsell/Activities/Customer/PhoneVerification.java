package com.appsinventiv.buyandsell.Activities.Customer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.appsinventiv.buyandsell.Activities.MainActivity;
import com.appsinventiv.buyandsell.Activities.Splash;
import com.appsinventiv.buyandsell.ApplicationClass;
import com.appsinventiv.buyandsell.Models.User;
import com.appsinventiv.buyandsell.R;
import com.appsinventiv.buyandsell.Utils.CommonUtils;
import com.appsinventiv.buyandsell.Utils.PrefManager;
import com.appsinventiv.buyandsell.Utils.SharedPrefs;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class PhoneVerification extends AppCompatActivity {

    Button sendCode;
    Button logout;
    EditText number;
    String phone;
    PhoneAuthProvider phoneAuth;
    DatabaseReference mDatabase;
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification);
        number = findViewById(R.id.number);
        logout = findViewById(R.id.logout);
        sendCode = findViewById(R.id.sendCode);
        phone = getIntent().getStringExtra("phone");
        phoneAuth = PhoneAuthProvider.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        number.setText(phone);
        prefManager = new PrefManager(this);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        this.setTitle("Verify your phone");

        sendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phone != null && !phone.equalsIgnoreCase("") && phone.length() > 10) {
                    sendCodeVerification();
                } else {
                    CommonUtils.showToast("Please check phone number and try again");
                }
            }
        });
    }

    private void sendCodeVerification() {
        phoneAuth.verifyPhoneNumber(
                number.getText().toString(),
                60,
                TimeUnit.SECONDS,
                this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        CommonUtils.showToast("Completed");
                        mDatabase.child("Users").child(SharedPrefs.getUsername()).child("numberVerified").setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                launchHomescreen();
                            }
                        });
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        CommonUtils.showToast(e.getMessage());
                    }

                    @Override
                    public void onCodeSent(String verificationId,
                                           PhoneAuthProvider.ForceResendingToken token) {

                        CommonUtils.showToast("Code sent");
                    }

                    @Override
                    public void onCodeAutoRetrievalTimeOut(String s) {
                        super.onCodeAutoRetrievalTimeOut(s);
                        CommonUtils.showToast("Time out");
                    }

                }

        );
    }

    private void launchHomescreen() {
        User user = SharedPrefs.getUser();
        user.setNumberVerified(true);
        SharedPrefs.setUser(user);
        SharedPrefs.setIsLoggedIn("yes");
        prefManager.setFirstTimeLaunch(false);
        startActivity(new Intent(PhoneVerification.this, MainActivity.class));


        finish();
    }

    public void logout() {
        SharedPrefs.logout();
        startActivity(new Intent(PhoneVerification.this, Splash.class));
        finish();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {


            finish();
        }

        return super.onOptionsItemSelected(item);
    }


}
