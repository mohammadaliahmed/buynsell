package com.appsinventiv.buyandsell.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.appsinventiv.buyandsell.Adapters.HomepageAdsAdapter;
import com.appsinventiv.buyandsell.Adapters.MyAdsListAdapter;
import com.appsinventiv.buyandsell.Models.AdDetails;
import com.appsinventiv.buyandsell.Models.CommentsModel;
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
import java.util.Collections;
import java.util.Comparator;

public class ListOfAds extends AppCompatActivity {
    RecyclerView recyclerview;
    private ArrayList<AdDetails> adsList = new ArrayList<>();
    MyAdsListAdapter adapter;
    DatabaseReference mDatabase;
    private ArrayList<String> likedAds = new ArrayList<>();
    String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ads);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
//

        category = getIntent().getStringExtra("category");
        this.setTitle(category);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        recyclerview = findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new MyAdsListAdapter(this, adsList, likedAds);
        recyclerview.setAdapter(adapter);
        adapter.setCallbacks(new HomepageAdsAdapter.AdapterCallbacks() {
            @Override
            public void onLiked(final AdDetails model, boolean liked) {
                if (liked) {
                    mDatabase.child("Users").child(SharedPrefs.getUsername()).child("likedAds").child(model.getAdId()).setValue(model.getAdId()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            CommonUtils.showToast("Marked as favorite");
                            mDatabase.child("Ads").child(model.getAdId()).child("likesCount").setValue(model.getLikesCount() + 1);
                        }
                    });
                } else {
                    mDatabase.child("Users").child(SharedPrefs.getUsername()).child("likedAds").child(model.getAdId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            CommonUtils.showToast("Removed from favorites");
                            mDatabase.child("Ads").child(model.getAdId()).child("likesCount").setValue(model.getLikesCount() - 1);

                        }
                    });
                }
            }
        });
        getAdsFromDB();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getLikedAdsFromDB();
    }

    private void getLikedAdsFromDB() {
        mDatabase.child("Users").child(SharedPrefs.getUsername()).child("likedAds").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    likedAds.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String likes = snapshot.getValue(String.class);
                        if (likes != null) {

                            likedAds.add(likes);
                        }
                    }
                    adapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getAdsFromDB() {
        mDatabase.child("Ads").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        AdDetails ad = snapshot.getValue(AdDetails.class);
                        if (ad != null) {
                            if (category.contains("All ads")) {
                                adsList.add(ad);
                            } else if (ad.getCategoryList().contains(category)) {
                                adsList.add(ad);
                            }
                        }
                    }
                    Collections.sort(adsList, new Comparator<AdDetails>() {
                        @Override
                        public int compare(AdDetails listData, AdDetails t1) {
                            Long ob1 = listData.getTime();
                            Long ob2 = t1.getTime();

                            return ob1.compareTo(ob2);

                        }
                    });

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
