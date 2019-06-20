package com.appsinventiv.buyandsell.Activities;

import android.accounts.Account;
import android.content.Intent;
import android.media.Image;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appsinventiv.buyandsell.Activities.Categories.ChooseMainCategory;
import com.appsinventiv.buyandsell.Activities.Chat.ListOfChats;
import com.appsinventiv.buyandsell.Activities.Customer.AccountSettings;
import com.appsinventiv.buyandsell.Activities.Customer.Login;
import com.appsinventiv.buyandsell.Adapters.HomepageAdsAdapter;
import com.appsinventiv.buyandsell.Adapters.MainSliderAdapter;
import com.appsinventiv.buyandsell.Models.AdDetails;
import com.appsinventiv.buyandsell.Models.User;
import com.appsinventiv.buyandsell.R;
import com.appsinventiv.buyandsell.Utils.CommonUtils;
import com.appsinventiv.buyandsell.Utils.SharedPrefs;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DatabaseReference mDatabase;
    RecyclerView recyclerview;
    Button postAd;
    private ArrayList<AdDetails> adsList = new ArrayList<>();
    private ArrayList<String> likedAds = new ArrayList<>();
    HomepageAdsAdapter adapter;
    LinearLayout searchProfile, account;
    private Toolbar toolbar;
    MainSliderAdapter mViewPagerAdapter;
    private static final int TIME_INTERVAL = 5000;
    ArrayList<String> pics = new ArrayList<>();
    ViewPager viewPager;
    public int currentPic = 0;
    public static boolean abc;
    DotsIndicator dots_indicator;
    ImageView uploadAd;
    private TextView navUsername;
    private TextView navSubtitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerview = findViewById(R.id.recyclerview);
        searchProfile = findViewById(R.id.searchProfile);
        dots_indicator = findViewById(R.id.dots_indicator);
        uploadAd = findViewById(R.id.uploadAd);

        searchProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AccountSettings.class));
            }
        });

        uploadAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SharedPrefs.getIsLoggedIn().equalsIgnoreCase("yes")) {
                    startActivity(new Intent(MainActivity.this, SubmitAd.class));
                } else {
                    startActivity(new Intent(MainActivity.this, Login.class));
                }
            }
        });
        account = findViewById(R.id.account);
        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SharedPrefs.getIsLoggedIn().equalsIgnoreCase("yes")) {
                    startActivity(new Intent(MainActivity.this, MyAccountAds.class));
                } else {
                    startActivity(new Intent(MainActivity.this, Login.class));
                }
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference();


        recyclerview.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new HomepageAdsAdapter(this, adsList, likedAds);
        recyclerview.setAdapter(adapter);

        adapter.setCallbacks(new HomepageAdsAdapter.AdapterCallbacks() {
            @Override
            public void onLiked(final AdDetails model, boolean liked) {
                if (SharedPrefs.getUsername().equalsIgnoreCase("")) {
                    startActivity(new Intent(MainActivity.this, Login.class));

                } else {
                    if (liked) {
                        boolean adtyp = false;
                        if (model.getAdType() == null || model.getAdType().equalsIgnoreCase("simple")) {
                            adtyp = false;
                        } else if (model.getAdType().equalsIgnoreCase("account")) {
                            adtyp = true;
                        }

                        final boolean finalAdtyp = adtyp;
                        mDatabase.child("Users").child(SharedPrefs.getUsername()).child("likedAds").child(model.getAdId()).setValue(model.getAdId()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                CommonUtils.showToast("Marked as favorite");
                                mDatabase.child(finalAdtyp ? "AccountAds" : "Ads").child(model.getAdId()).child("likesCount").setValue(model.getLikesCount());
                            }
                        });
                    } else {
                        boolean adtyp = false;
                        if (model.getAdType() == null || model.getAdType().equalsIgnoreCase("simple")) {
                            adtyp = false;
                        } else if (model.getAdType().equalsIgnoreCase("account")) {
                            adtyp = true;
                        }
                        final boolean finalAdtyp = adtyp;

                        mDatabase.child("Users").child(SharedPrefs.getUsername()).child("likedAds").child(model.getAdId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                CommonUtils.showToast("Removed from favorites");
                                mDatabase.child(finalAdtyp ? "AccountAds" : "Ads").child(model.getAdId()).child("likesCount").setValue(model.getLikesCount());

                            }
                        });
                    }
                }
            }
        });


        getAllAdsFromDB();

        initViewPager();
        initNavDrawer();
        updateFcmKey();
    }

    private void updateFcmKey() {
        if (!SharedPrefs.getFcmKey().equalsIgnoreCase("") && !SharedPrefs.getUsername().equalsIgnoreCase("")
                && SharedPrefs.getIsLoggedIn().equalsIgnoreCase("yes")) {
            mDatabase.child("Users").child(SharedPrefs.getUsername()).child("fcmKey").setValue(SharedPrefs.getFcmKey());
            mDatabase.child("Users").child(SharedPrefs.getUsername()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        SharedPrefs.setUser(user);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    private void initViewPager() {
        pics.add("https://www.designyourway.net/diverse/6/darkwallpapers/290956505.jpg");
        pics.add("https://slm-assets2.secondlife.com/assets/5052724/lightbox/Rose%20Wallpaper1.jpg?1328646839");
        pics.add("http://freebiesland.net/wp-content/uploads/2017/05/Samsung-Galaxi-Wallpaper-700x350.jpg");
        pics.add("http://village.photos/images/user/de06c05d-8665-4b33-a0a0-72443a69d9e9/tn_06ab1c5a-bc41-4b6e-94d9-499e432865b7.jpg");

        viewPager = findViewById(R.id.slider);
        mViewPagerAdapter = new MainSliderAdapter(this, pics);
        viewPager.setAdapter(mViewPagerAdapter);
        dots_indicator.setViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPic = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Magic here
                if (currentPic >= pics.size()) {
                    currentPic = 0;
                    viewPager.setCurrentItem(currentPic);
                } else {
                    viewPager.setCurrentItem(currentPic);
                    currentPic++;
                }
                new Handler().postDelayed(this, TIME_INTERVAL);
            }
        }, 2000); // Millisecond 1000 = 1 sec


    }

    private void initNavDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        navUsername = (TextView) headerView.findViewById(R.id.name_drawer);
        navSubtitle = (TextView) headerView.findViewById(R.id.phone_drawer);
        Menu nav_Menu = navigationView.getMenu();


    }

    @Override
    protected void onResume() {
        super.onResume();
        getLikedAdsFromDB();
        if (SharedPrefs.getUsername().equalsIgnoreCase("")) {
//            nav_Menu.findItem(R.id.signout).setVisible(false);
            navSubtitle.setText("Welcome to Buy n Sell");

            navUsername.setText("Login or Signup");
            navUsername.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(MainActivity.this, Login.class);
                    startActivity(i);
                }
            });
        } else {
            navSubtitle.setText(SharedPrefs.getPhone());

            navUsername.setText(SharedPrefs.getName());
        }
    }

    private void getAllAdsFromDB() {
        mDatabase.child("Ads").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    adsList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        AdDetails ad = snapshot.getValue(AdDetails.class);
                        if (ad != null) {
                            if (SharedPrefs.getUser() != null) {
                                if (SharedPrefs.getUser().getCity() != null && ad.getCity() != null) {
                                    if (ad.getCity().equalsIgnoreCase(SharedPrefs.getUser().getCity())) {
                                        adsList.add(ad);

                                    }
                                } else {

                                }
                            } else {
//                                adsList.add(ad);
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
//            System.exit(1);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
//            startActivity(new Intent(MainActivity.this, SearchAds.class));
            startActivity(new Intent(MainActivity.this, SearchAds.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

        } else if (id == R.id.nav_post_ad) {

            if (SharedPrefs.getIsLoggedIn().equalsIgnoreCase("yes")) {
                startActivity(new Intent(MainActivity.this, SubmitAd.class));
            } else {
                startActivity(new Intent(MainActivity.this, Login.class));
            }

        } else if (id == R.id.nav_friends) {

        } else if (id == R.id.nav_chats) {
            if (SharedPrefs.getIsLoggedIn().equalsIgnoreCase("yes")) {
                startActivity(new Intent(MainActivity.this, ListOfChats.class));
            } else {
                startActivity(new Intent(MainActivity.this, Login.class));
            }
        } else if (id == R.id.nav_favorite_ads) {
            if (SharedPrefs.getIsLoggedIn().equalsIgnoreCase("yes")) {
                startActivity(new Intent(MainActivity.this, FavoriteAds.class));
            } else {
                startActivity(new Intent(MainActivity.this, Login.class));
            }

        } else if (id == R.id.nav_browse_ads) {
            abc = true;
            startActivity(new Intent(MainActivity.this, ChooseMainCategory.class));


        } else if (id == R.id.nav_my_ads) {
            if (SharedPrefs.getIsLoggedIn().equalsIgnoreCase("yes")) {
                startActivity(new Intent(MainActivity.this, MyAds.class));
            } else {
                startActivity(new Intent(MainActivity.this, Login.class));
            }

        } else if (id == R.id.nav_settings) {
            if (SharedPrefs.getIsLoggedIn().equalsIgnoreCase("yes")) {
                startActivity(new Intent(MainActivity.this, AccountSettings.class));
            } else {
                startActivity(new Intent(MainActivity.this, Login.class));
            }
        } else if (id == R.id.nav_logout) {
            SharedPrefs.logout();
            startActivity(new Intent(MainActivity.this, Splash.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
