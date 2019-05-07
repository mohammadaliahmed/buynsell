package com.appsinventiv.buyandsell.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.appsinventiv.buyandsell.R;

public class AccountSettings extends AppCompatActivity {
    ImageView pickPicture;
    EditText name;
    RadioGroup radioGroup1, radioGroup2, radioGroup3;
    Button save;
    RadioButton radioAccountType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        this.setTitle("Account");

        pickPicture = findViewById(R.id.pickPicture);
        name = findViewById(R.id.name);
        radioGroup1 = findViewById(R.id.radioGroup1);
        radioGroup2 = findViewById(R.id.radioGroup2);
        radioGroup3 = findViewById(R.id.radioGroup3);
        save = findViewById(R.id.save);

//        int selectedId = radioGroup1.getCheckedRadioButtonId();
//        radioAccountType = (RadioButton) findViewById(selectedId);

        radioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.radio0:
                        // do operations specific to this selection
                        radioGroup2.setVisibility(View.VISIBLE);
                        radioGroup3.setVisibility(View.GONE);
                        break;
                    case R.id.radio1:
                        // do operations specific to this selection
                        radioGroup3.setVisibility(View.VISIBLE);
                        radioGroup2.setVisibility(View.GONE);
                        break;

                }
            }
        });

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
