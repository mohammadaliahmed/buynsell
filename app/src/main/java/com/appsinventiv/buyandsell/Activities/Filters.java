package com.appsinventiv.buyandsell.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.appsinventiv.buyandsell.Activities.Categories.ChooseMainCategory;
import com.appsinventiv.buyandsell.Activities.Locations.ChooseLocation;
import com.appsinventiv.buyandsell.Adapters.HomepageAdsAdapter;
import com.appsinventiv.buyandsell.Adapters.MyAdsListAdapter;
import com.appsinventiv.buyandsell.Models.AdDetails;
import com.appsinventiv.buyandsell.R;
import com.appsinventiv.buyandsell.Utils.CommonUtils;
import com.appsinventiv.buyandsell.Utils.SharedPrefs;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Filters extends AppCompatActivity {
    EditText searchWord, min, max;
    TextView chooseLocation, chooseCategory;
    Button apply;

    long maxPrice = 999999999l;
    long minPrice = 0l;

    public static String location, category;

    @Override
    protected void onResume() {
        super.onResume();
        if (location != null) {
            chooseLocation.setText("Location: " + location);
        }
        if (category != null) {
            chooseCategory.setText("Category: " + category);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        this.setTitle("Filters");

        apply = findViewById(R.id.apply);
        chooseLocation = findViewById(R.id.chooseLocation);
        chooseCategory = findViewById(R.id.chooseCategory);
        searchWord = findViewById(R.id.searchWord);
        min = findViewById(R.id.min);
        max = findViewById(R.id.max);

        chooseCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Filters.this, ChooseMainCategory.class));
            }
        });
        chooseLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Filters.this, ChooseLocation.class));

            }
        });


        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (min.getText().length() > 0) {
                    minPrice = Long.parseLong(min.getText().toString());
                }
                if (max.getText().length() > 0) {
                    maxPrice = Long.parseLong(max.getText().toString());
                }

                Intent i = new Intent(Filters.this, SearchAds.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.putExtra("min", minPrice);
                i.putExtra("max", maxPrice);
                i.putExtra("location", location);
                i.putExtra("category", category);
                i.putExtra("word", searchWord.getText().toString());
                startActivity(i);
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
