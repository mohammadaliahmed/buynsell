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

import com.appsinventiv.buyandsell.Activities.Chat.ChatScreen;
import com.appsinventiv.buyandsell.Activities.Customer.Login;
import com.appsinventiv.buyandsell.Adapters.SliderAdapter;
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
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;


import java.text.DecimalFormat;
import java.util.Calendar;

public class AdPage extends AppCompatActivity {

    TextView title, price, price1, date1, category, time, description, views, username, viewMore, location;
    ViewPager mViewPager;

    SliderAdapter adapter;
    String adId;
    ProgressDialog progressDialog;
    LinearLayout call, sms, whatsapp, chat;
    String phoneNumber;
    DatabaseReference mDatabase;
    ImageView back;
    String adBy;
    LinearLayout favourite, report, shareAd;
    //    AdDetails adModel;
    RelativeLayout wholeView;
    ScrollView scrollView;
    AdDetails adDetails;
    LinearLayout comments;
    LikeButton heart_button;
    TextView commentsCount, likesCount;
    DotsIndicator dots_indicator;
    private String adType;
    boolean adtyp;

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
        dots_indicator = findViewById(R.id.dots_indicator);
        heart_button = findViewById(R.id.heart_button);

        comments = findViewById(R.id.comments);
        back = findViewById(R.id.back);
        report = findViewById(R.id.report);
        wholeView = findViewById(R.id.wholeView);
        scrollView = findViewById(R.id.scrollView2);
        commentsCount = findViewById(R.id.commentsCount);
        likesCount = findViewById(R.id.likesCount);

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
        chat = findViewById(R.id.chat);
        location = findViewById(R.id.location);
        favourite = findViewById(R.id.favourite);
        shareAd = findViewById(R.id.sharead);

        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        Intent intent = getIntent();
        adId = intent.getStringExtra("adId");
        adType = intent.getStringExtra("type");
        if(adType==null || adType.equalsIgnoreCase("simple") ){
            adtyp=false;
        }else if(adType.equalsIgnoreCase("account")){
            adtyp=true;
        }

        comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SharedPrefs.getIsLoggedIn().equalsIgnoreCase("yes")) {
                    Intent i = new Intent(AdPage.this, CommentsActivity.class);
                    i.putExtra("adId", adId);
                    i.putExtra("type", adDetails.getAdType());
                    startActivity(i);
                } else {
                    startActivity(new Intent(AdPage.this, Login.class));
                }
            }
        });

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SharedPrefs.getIsLoggedIn().equalsIgnoreCase("yes")) {
                    if (adDetails.getUsername().equalsIgnoreCase(SharedPrefs.getUsername())) {
                        CommonUtils.showToast("This is your own ad");
                    } else {
                        Intent i = new Intent(AdPage.this, ChatScreen.class);
                        i.putExtra("adId", adId);
                        i.putExtra("userId", adDetails.getUsername());
                        startActivity(i);
                    }
                } else {
                    startActivity(new Intent(AdPage.this, Login.class));
                }

            }
        });

        if(adType==null || adType.equalsIgnoreCase("simple") ){
            init(adId,false);
        }else if(adType.equalsIgnoreCase("account")){
            init(adId,true);
        }
        if (SharedPrefs.getUsername().equalsIgnoreCase("")) {
            startActivity(new Intent(AdPage.this, Login.class));

        } else if (SharedPrefs.getIsLoggedIn().equalsIgnoreCase("yes")) {
            heart_button.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
//                    adDetails.setLikesCount(adDetails.getLikesCount() );

                    mDatabase.child("Users").child(SharedPrefs.getUsername()).child("likedAds").child(adId).setValue(adId).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            CommonUtils.showToast("Marked as favorite");
                            mDatabase.child(adtyp?"AccountAds":"Ads").child(adId).child("likesCount").setValue(adDetails.getLikesCount()+1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    adDetails.setLikesCount(adDetails.getLikesCount());
                                }
                            });

                        }
                    });
                }

                @Override
                public void unLiked(LikeButton likeButton) {
//                    adDetails.setLikesCount(adDetails.getLikesCount() - 1);

                    mDatabase.child("Users").child(SharedPrefs.getUsername()).child("likedAds").child(adId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            CommonUtils.showToast("Removed from favorites");
                            mDatabase.child(adtyp?"AccountAds":"Ads").child(adId).child("likesCount").setValue(adDetails.getLikesCount() - 1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                }
                            });


                        }
                    });
                }
            });
        }


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
        getLikedAdsFromDB();


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


    public void init(String id,boolean type) {
        mViewPager = findViewById(R.id.viewPager);


        mDatabase.child(type?"AccountAds":"Ads")
                .child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    wholeView.setVisibility(View.GONE);
                    scrollView.setVisibility(View.VISIBLE);
                    adDetails = dataSnapshot.getValue(AdDetails.class);
                    if (adDetails != null) {
//                        adModel = adDetails;
                        DecimalFormat formatter = new DecimalFormat("#,###,###");
                        String formatedPrice = formatter.format(adDetails.getPrice());
                        title.setText(adDetails.getTitle());
                        price.setText("Rs " + formatedPrice);
                        price1.setText("Rs " + formatedPrice);
                        time.setText(getFormattedDate(AdPage.this, adDetails.getTime()));
                        date1.setText(getFormattedDate(AdPage.this, adDetails.getTime()));
                        category.setText("" + adDetails.getCategoryList());
                        commentsCount.setText(adDetails.getCommentsCount() + " Comments");
                        likesCount.setText(adDetails.getLikesCount() + "");

                        description.setText("" + adDetails.getDescription());
                        username.setText(adDetails.getUsername());
                        adBy = adDetails.getUsername();
                        getUserCountry(adBy);

                        adapter = new SliderAdapter(AdPage.this, adDetails.getPictures());
                        mViewPager.setAdapter(adapter);
                        dots_indicator.setViewPager(mViewPager);


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

    private void getLikedAdsFromDB() {
        mDatabase.child("Users").child(SharedPrefs.getUsername()).child("likedAds").child(adId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    heart_button.setLiked(true);


                } else {
                    heart_button.setLiked(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
