package com.appsinventiv.buyandsell.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;


import com.appsinventiv.buyandsell.Adapters.SliderAdapter;
import com.appsinventiv.buyandsell.Models.AdDetails;
import com.appsinventiv.buyandsell.Models.User;
import com.appsinventiv.buyandsell.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.text.DecimalFormat;
import java.util.Calendar;

public class AdPage extends AppCompatActivity {

    TextView title, price, price1, date1, category, time, description, views, username, viewMore, location;
    ViewPager mViewPager;

    SliderAdapter adapter;
    String adId;
    ProgressDialog progressDialog;
    LinearLayout call, sms, whatsapp;
    String phoneNumber;
    DatabaseReference mDatabase;
    ImageView back;
    String adBy;
    LinearLayout favourite, report, shareAd;
    AdDetails adModel;
    RelativeLayout wholeView;
    ScrollView scrollView;
    AdDetails adDetails;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_page);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        setStatusBarColor(true);

        back = findViewById(R.id.back);
        report = findViewById(R.id.report);
        wholeView = findViewById(R.id.wholeView);
        scrollView = findViewById(R.id.scrollView2);

        progressDialog = new ProgressDialog(AdPage.this);
        title = findViewById(R.id.title);
        price = findViewById(R.id.price);
        time = findViewById(R.id.time);
//        city = findViewById(R.id.city);
        username = findViewById(R.id.username);
        price1 = findViewById(R.id.price1);
        date1 = findViewById(R.id.date1);
        category = findViewById(R.id.category);
        viewMore = findViewById(R.id.viewMoreAds);
        description = findViewById(R.id.description);
        views = findViewById(R.id.views);
        call = findViewById(R.id.call);
        sms = findViewById(R.id.sms);
        whatsapp = findViewById(R.id.whatsapp);
        location = findViewById(R.id.location);
        favourite = findViewById(R.id.favourite);
        shareAd = findViewById(R.id.sharead);

        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        Intent intent = getIntent();
        adId = intent.getStringExtra("adId");


        init(adId);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                    if (i1 > 1200) {
                        setStatusBarColor(false);
                    } else {
                        setStatusBarColor(true);
                    }
                }
            });
        }

        shareAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT,
                        "Please checkout this ad on Mobile Mart.\n\nhttp://mobilemart.pk/ad/" + adId + "\n\nOr download app from PlayStore\nhttp://bit.ly/MobileMartApp");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Whatsapp");
                startActivity(Intent.createChooser(shareIntent, "Share ad via.."));
            }
        });



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



    }

    private void setStatusBarColor(boolean abc) {
        if (abc) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
            }
        }
    }

    private void getUserCountry(String username) {

        mDatabase.child("users").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }




    public void init(String id) {
        mViewPager = findViewById(R.id.viewPager);


        mDatabase.child("Ads").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    wholeView.setVisibility(View.GONE);
                    scrollView.setVisibility(View.VISIBLE);
                    adDetails = dataSnapshot.getValue(AdDetails.class);
                    if (adDetails != null) {
                        adModel = adDetails;
                        DecimalFormat formatter = new DecimalFormat("#,###,###");
                        String formatedPrice = formatter.format(adDetails.getPrice());
                        title.setText(adDetails.getTitle());
                        price.setText("Rs " + formatedPrice);
                        price1.setText("Rs " + formatedPrice);
                        time.setText(getFormattedDate(AdPage.this, adDetails.getTime()));
                        date1.setText(getFormattedDate(AdPage.this, adDetails.getTime()));

                        description.setText("" + adDetails.getDescription());
                        username.setText(adDetails.getUsername());
                        adBy = adDetails.getUsername();
                        getUserCountry(adBy);

                        adapter = new SliderAdapter(AdPage.this, adModel.getPictures());
                        mViewPager.setAdapter(adapter);


                        phoneNumber = adDetails.getPhone();


                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
                startActivity(i);
            }
        });
        sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phoneNumber));
                startActivity(i);
            }
        });
        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                phoneNumber = CountryDialCodes.getDialCode(country) + phoneNumber;
//                String url = "https://api.whatsapp.com/send?phone=" + phoneNumber;
//                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                startActivity(i);
////

            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }


    public String getFormattedDate(Context context, long smsTimeInMilis) {
        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(smsTimeInMilis);

        Calendar now = Calendar.getInstance();

        final String timeFormatString = "h:mm aa";
        final String dateTimeFormatString = "dd MMM ";
        final long HOURS = 60 * 60 * 60;
        if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE)) {
            return "" + DateFormat.format(timeFormatString, smsTime);
        } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1) {
            return "Yesterday ";
        } else if (now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR)) {
            return DateFormat.format(dateTimeFormatString, smsTime).toString();
        } else {
            return DateFormat.format("dd MMM , h:mm aa", smsTime).toString();
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_ad_page, menu);
        return true;
    }


}
