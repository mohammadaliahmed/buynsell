package com.appsinventiv.buyandsell.Activities.Locations;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.appsinventiv.buyandsell.Activities.Categories.CategoryAdapter;
import com.appsinventiv.buyandsell.Activities.Categories.ChooseMainCategory;
import com.appsinventiv.buyandsell.Activities.ListOfAds;
import com.appsinventiv.buyandsell.Activities.MainActivity;
import com.appsinventiv.buyandsell.Activities.SubmitAd;
import com.appsinventiv.buyandsell.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChooseLocation extends AppCompatActivity {
    DatabaseReference mDatabase;
    LocationsAdapter adapter;
    RecyclerView recyclerView;
    String parentLocation;
    ArrayList<String> itemList = new ArrayList<>();
    ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_category);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();
        recyclerView = findViewById(R.id.recycler);
        progress = findViewById(R.id.progress);


        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        adapter = new LocationsAdapter(this, itemList, new LocationsAdapter.GetNewData() {
            @Override
            public void whichCategory(String title) {
                getCategoryDataFromDB(title);
            }
        });

        recyclerView.setAdapter(adapter);


        parentLocation = getIntent().getStringExtra("parentLocation");

        if (parentLocation == null) {
            this.setTitle("Choose Location");
            getDataFromDB();
        } else {
            this.setTitle("" + parentLocation);
            getCategoryDataFromDB(parentLocation);
        }


    }

    private void getDataFromDB() {
        progress.setVisibility(View.VISIBLE);
        mDatabase.child("Settings").child("Locations").child("Province").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    itemList.clear();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String value = snapshot.getValue(String.class);
                        itemList.add(value);
                    }

                    progress.setVisibility(View.GONE);

                    adapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getCategoryDataFromDB(final String cat) {
        progress.setVisibility(View.VISIBLE);
        mDatabase.child("Settings").child("Locations").child(cat).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    itemList.clear();
                    if (MainActivity.abc) {
                        itemList.add("All ads");
                    }
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String value = snapshot.getValue(String.class);
                        itemList.add(value);
                    }

                    progress.setVisibility(View.GONE);

                    adapter.notifyDataSetChanged();
                } else {

//                    ChooseMainCategory.activity.finish();
                    if (MainActivity.abc) {
                        Intent i = new Intent(ChooseLocation.this, ListOfAds.class);
                        if (cat.equalsIgnoreCase("All Locations")) {
                            i.putExtra("location", parentLocation);

                        } else {
                            i.putExtra("location", cat);

                        }
                        startActivity(i);
                    }
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {


            if (SubmitAd.categoryList.size() > 0) {
                SubmitAd.categoryList.remove(SubmitAd.categoryList.size() - 1);

                getCategoryDataFromDB(SubmitAd.categoryList.get(SubmitAd.categoryList.size() - 1));


            } else {
                finish();
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            try {


                if (SubmitAd.categoryList.size() > 0) {
                    SubmitAd.categoryList.remove(SubmitAd.categoryList.size() - 1);

                    getCategoryDataFromDB(SubmitAd.categoryList.get(SubmitAd.categoryList.size() - 1));


                } else {
                    finish();
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                finish();
            }
            super.onBackPressed();

        }

        return super.onOptionsItemSelected(item);
    }
}
