package com.appsinventiv.buyandsell.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.appsinventiv.buyandsell.Adapters.HomepageAdsAdapter;
import com.appsinventiv.buyandsell.Adapters.MyAdsListAdapter;
import com.appsinventiv.buyandsell.Models.AdDetails;
import com.appsinventiv.buyandsell.R;
import com.appsinventiv.buyandsell.Utils.SharedPrefs;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyAds extends AppCompatActivity {
    RecyclerView recyclerview;
    private ArrayList<AdDetails> adsList = new ArrayList<>();
    MyAdsListAdapter adapter;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ads);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        this.setTitle("My Ads");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        recyclerview=findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        adapter = new MyAdsListAdapter(this, adsList);
        recyclerview.setAdapter(adapter);

        getMyAdsFromDB();

    }

    private void getMyAdsFromDB() {
        mDatabase.child("Users").child(SharedPrefs.getUsername()).child("adsPosted").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    adsList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        getAdsFromDB(snapshot.getKey());
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getAdsFromDB(String adId) {
        mDatabase.child("Ads").child(adId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    AdDetails ad = dataSnapshot.getValue(AdDetails.class);
                    if (ad != null) {

                        adsList.add(ad);
                    }

                    adapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
