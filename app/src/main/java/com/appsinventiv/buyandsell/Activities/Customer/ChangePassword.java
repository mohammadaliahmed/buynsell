package com.appsinventiv.buyandsell.Activities.Customer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.appsinventiv.buyandsell.Activities.MainActivity;
import com.appsinventiv.buyandsell.Models.User;
import com.appsinventiv.buyandsell.R;
import com.appsinventiv.buyandsell.Utils.CommonUtils;
import com.appsinventiv.buyandsell.Utils.PrefManager;
import com.appsinventiv.buyandsell.Utils.SharedPrefs;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChangePassword extends AppCompatActivity {

    DatabaseReference mDatabase;
    EditText confirmPassword, password;
    Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        save = findViewById(R.id.save);
        confirmPassword = findViewById(R.id.confirmPassword);
        password = findViewById(R.id.password);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (password.getText().length() == 0) {
                    password.setError("Enter password");
                } else if (password.getText().length() < 5) {
                    password.setError("Weak password");
                } else if (confirmPassword.getText().length() == 0) {
                    confirmPassword.setError("Enter confirm password");
                } else if (!password.getText().toString().equalsIgnoreCase(confirmPassword.getText().toString())) {
                    CommonUtils.showToast("Password does not match");
                } else {
                    changePassword();
                }
            }
        });
    }

    private void changePassword() {
        mDatabase.child("Users").child(SharedPrefs.getUsername()).child("password").setValue(password.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                CommonUtils.showToast("Password changed");
                startActivity(new Intent(ChangePassword.this, MainActivity.class));
                finish();
            }
        });

    }
}
