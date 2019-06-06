package com.appsinventiv.buyandsell.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.appsinventiv.buyandsell.Activities.Categories.ChooseMainCategory;
import com.appsinventiv.buyandsell.Adapters.SelectedImagesAdapter;
import com.appsinventiv.buyandsell.Interface.AdObserver;
import com.appsinventiv.buyandsell.Models.AdDetails;
import com.appsinventiv.buyandsell.Models.SelectedAdImages;
import com.appsinventiv.buyandsell.Models.User;
import com.appsinventiv.buyandsell.R;
import com.appsinventiv.buyandsell.Utils.CommonUtils;
import com.appsinventiv.buyandsell.Utils.CompressImage;
import com.appsinventiv.buyandsell.Utils.GifSizeFilter;
import com.appsinventiv.buyandsell.Utils.SharedPrefs;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.filter.Filter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class SubmitAd extends AppCompatActivity implements AdObserver {
    StorageReference mStorageRef;
    Button pickPicture, submitAd;
    EditText title, price, description;
    DatabaseReference mDatabase;

    ArrayList<String> imageUrl;
    String adId;
    String username, city, phonenumber;

    List<Uri> mSelected;
    ArrayList<SelectedAdImages> selectedAdImages;
    SelectedImagesAdapter adapter;
    AdObserver adObserver;

    ArrayList<String> adCover;
    private static final int REQUEST_CODE_CHOOSE = 23;

    long time;
    EditText usernameField, phoneField, locationField;
    TextView chooseCategoryText;
    public String mainCategory = "test2";
    public static Activity fa;
    String adminFcmKey;
    CheckBox checkbox;

    public static ArrayList<String> categoryList = new ArrayList<>();

    TextView category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_ad);
        SubmitAd.this.setTitle("Submit a free Ad");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        adObserver = SubmitAd.this;

        getPermissions();

        fa = this;
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        imageUrl = new ArrayList<String>();
        adCover = new ArrayList<String>();


        category = findViewById(R.id.category);
        pickPicture = (Button) findViewById(R.id.pickpicture);
        submitAd = (Button) findViewById(R.id.submit);

        title = (EditText) findViewById(R.id.title);
        price = (EditText) findViewById(R.id.price);
        description = (EditText) findViewById(R.id.description);

        usernameField = (EditText) findViewById(R.id.username);
        phoneField = (EditText) findViewById(R.id.phone);
        locationField = (EditText) findViewById(R.id.location);
        checkbox = findViewById(R.id.check);

        chooseCategoryText = (TextView) findViewById(R.id.choose_category);

        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.abc=false;
                startActivity(new Intent(SubmitAd.this, ChooseMainCategory.class));
            }
        });

        submitAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageUrl.isEmpty()) {
                    CommonUtils.showToast("Please select atleast one image");
                } else if (mainCategory == null) {
                    Toast.makeText(SubmitAd.this, "Please choose a category!", Toast.LENGTH_SHORT).show();

                } else if (title.getText().toString().length() == 0) {
                    title.setError("Cannot be null");

                } else if (price.getText().toString().length() == 0) {
                    price.setError("Cannot be null");
                } else if (description.getText().toString().length() == 0) {
                    description.setError("Cannot be null");
                } else if (!checkbox.isChecked()) {
                    checkbox.setError("");
                    CommonUtils.showToast("Please accept the terms and conditions");

                } else if (categoryList.size() < 1) {
                    CommonUtils.showToast("Please choose category");
                } else {
                    submitAd();

                }

            }
        });
        time = System.currentTimeMillis();

        username = SharedPrefs.getUsername();


        mDatabase.child("Users").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    User user = dataSnapshot.getValue(User.class);
                    usernameField.setText(user.getName());
                    phoneField.setText("" + user.getPhone());
                    locationField.setText(user.getCity());
                    username = user.getUsername();
                    phonenumber = user.getPhone();
                    city = user.getCity();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        showPickedPictures();

        pickPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                initMatisse();


            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (categoryList.size() > 0) {
            String cate = "";
            for (String cat : categoryList) {
                cate = cat + " > " + cate;
            }
            category.setText(cate);
        }
    }

    private void initMatisse() {
        Matisse.from(SubmitAd.this)
                .choose(MimeType.allOf())
                .countable(true)
                .maxSelectable(8)
                .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new GlideEngine())
                .forResult(REQUEST_CODE_CHOOSE);
    }

    private void getPermissions() {
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,

        };

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
//        CommonUtils.showToast(PERMISSION_ALL+"");
    }

    private void showPickedPictures() {
        selectedAdImages = new ArrayList<>();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.horizontal_recycler_view);
        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(SubmitAd.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManagaer);
        adapter = new SelectedImagesAdapter(SubmitAd.this, selectedAdImages);
        recyclerView.setAdapter(adapter);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        // Check which request we're responding to
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            mSelected = Matisse.obtainResult(data);

            for (Uri img :
                    mSelected) {
                selectedAdImages.add(new SelectedAdImages("" + img));
                adapter.notifyDataSetChanged();
//                CompressImage compressImage = new CompressImage(SubmitAd.this);
                imageUrl.add(CompressImage.compressImage("" + img, SubmitAd.this));
            }
        }
        if (requestCode == 1) {

        }

        super.onActivityResult(requestCode, resultCode, data);

    }


    void submitAd() {
        adId = mDatabase.child("Ads").push().getKey();

//        String ci = GetAdAddress.getCity(SubmitAd.this, lat, lon);
//        SharedPrefs.setUserCity(ci);
//        mDatabase.child("users").child(username).child("city").setValue(city);

        String Adtitle = title.getText().toString(),
                AdDescription = description.getText().toString();
        long AdPrice = Long.parseLong(price.getText().toString());

        String username = SharedPrefs.getUsername();
        mDatabase.child("Users").child(username).child("adsPosted").child("" + adId).setValue("" + adId);


        mDatabase.child("Ads").child("" + adId).setValue(new AdDetails(
                        adId, Adtitle, AdDescription, SharedPrefs.getUsername(), phonenumber, "",
                        System.currentTimeMillis(),
                        Long.parseLong(price.getText().toString()), categoryList
                )
        ).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SubmitAd.this, "Error" + e, Toast.LENGTH_SHORT).show();
            }
        });

        int count = 0;
        for (String img : imageUrl) {

            putPictures(img, "" + adId, count);
            count++;
            adObserver.onUploaded(count, imageUrl.size());

        }


    }

    public void putPictures(String path, final String key, final int count) {
        String imgName = Long.toHexString(Double.doubleToLongBits(Math.random()));

        ;
        Uri file = Uri.fromFile(new File(path));


        StorageReference riversRef = mStorageRef.child("Photos").child(imgName);

        riversRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    @SuppressWarnings("VisibleForTests")
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content

                        Uri downloadUrl = taskSnapshot.getDownloadUrl();

                        mDatabase.child("Ads").child("" + adId).child("pictures").child("" + count)
                                .setValue("" + downloadUrl);


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                        Toast.makeText(SubmitAd.this, "error", Toast.LENGTH_SHORT).show();
                    }
                });


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onUploaded(int count, int arraySize) {
        if (count == arraySize) {
            Intent i = new Intent(SubmitAd.this, SuccessPage.class);
            startActivity(i);
            finish();
        }


    }

    public boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


}

