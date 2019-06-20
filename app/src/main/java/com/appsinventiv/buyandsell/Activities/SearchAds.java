package com.appsinventiv.buyandsell.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.appsinventiv.buyandsell.Activities.Customer.Login;
import com.appsinventiv.buyandsell.Adapters.HomepageAdsAdapter;
import com.appsinventiv.buyandsell.Adapters.MyAdsListAdapter;
import com.appsinventiv.buyandsell.Adapters.SearchAdsAdapter;
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
import java.util.Collections;
import java.util.Comparator;

public class SearchAds extends AppCompatActivity {
    RecyclerView recyclerview;
    private ArrayList<AdDetails> adsList = new ArrayList<>();
    SearchAdsAdapter adapter;
    DatabaseReference mDatabase;
    private ArrayList<String> likedAds = new ArrayList<>();


    long maxPrice = 999999999;
    long minPrice = 0;

    public String location, category, word;
    TextView noAds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_ads);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        this.setTitle("All ads");

        maxPrice = getIntent().getLongExtra("max", 99999999l);
        minPrice = getIntent().getLongExtra("min", 0l);
        location = getIntent().getStringExtra("location");
        category = getIntent().getStringExtra("category");
        word = getIntent().getStringExtra("word");
        if (word == null) {
            word = "";
        }


        mDatabase = FirebaseDatabase.getInstance().getReference();
        noAds = findViewById(R.id.noAds);
        recyclerview = findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new SearchAdsAdapter(this, adsList, likedAds);
        recyclerview.setAdapter(adapter);
        if (SharedPrefs.getUsername().equalsIgnoreCase("")) {
            startActivity(new Intent(SearchAds.this, Login.class));

        } else {
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
        }

        getAdsFromDB();
//        getLikedAdsFromDB();
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
                            if (ad.getTitle().toLowerCase().contains(word.toLowerCase())) {
                                if (ad.getPrice() >= minPrice && ad.getPrice() <= maxPrice) {
                                    if (category == null) {
                                        if (location == null) {
                                            adsList.add(ad);
                                        } else if (ad.getCity() != null && ad.getCity().equalsIgnoreCase(location)) {
                                            adsList.add(ad);
                                        }
                                    } else if (ad.getCategoryList().contains(category)) {
                                        if (location == null) {
//                                            if (ad.getCity().equalsIgnoreCase(SharedPrefs.getUser().getCity())) {
                                            adsList.add(ad);
//                                            }

                                        } else if (ad.getCity() != null && ad.getCity().equalsIgnoreCase(location)) {
                                            adsList.add(ad);

                                        }
                                    }
                                }
                            }

                        }


                    }
                    if (adsList.size() > 0) {
                        noAds.setVisibility(View.GONE);
                    } else {
                        noAds.setVisibility(View.VISIBLE);
                    }
                    Collections.sort(adsList, new Comparator<AdDetails>() {
                        @Override
                        public int compare(AdDetails listData, AdDetails t1) {
                            Long ob1 = listData.getTime();
                            Long ob2 = t1.getTime();

                            return ob1.compareTo(ob2);

                        }
                    });
                    adapter.updateList(adsList);

                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        final MenuItem mSearch = menu.findItem(R.id.action_search);

        SearchView mSearchView = (SearchView) mSearch.getActionView();
        mSearch.expandActionView();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);

                return false;
            }
        });


        return true;
        // Get SearchView object.

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {


            finish();
        }
        if (item.getItemId() == R.id.action_filter) {
            startActivity(new Intent(SearchAds.this, Filters.class));

//            finish();
        }

        return super.onOptionsItemSelected(item);
    }


}
