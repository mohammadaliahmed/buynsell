package com.appsinventiv.buyandsell.Activities.Customer;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.appsinventiv.buyandsell.Activities.Categories.ChooseMainCategory;
import com.appsinventiv.buyandsell.Activities.MainActivity;
import com.appsinventiv.buyandsell.Activities.SubmitAd;
import com.appsinventiv.buyandsell.Models.User;
import com.appsinventiv.buyandsell.R;
import com.appsinventiv.buyandsell.Utils.CommonUtils;
import com.appsinventiv.buyandsell.Utils.CompressImage;
import com.appsinventiv.buyandsell.Utils.GifSizeFilter;
import com.appsinventiv.buyandsell.Utils.SharedPrefs;
import com.bumptech.glide.Glide;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountSettings extends AppCompatActivity {
    CircleImageView pickPicture;
    EditText name;
    Button save;
    Spinner spinner, spinner2, spinner3;
    DatabaseReference mDatabase;
    User user;
    String type1, type2;
    private static final int REQUEST_CODE_CHOOSE = 23;
    private List<Uri> mSelected = new ArrayList<>();
    ArrayList<String> imageUrl = new ArrayList<>();
    StorageReference mStorageRef;
    RelativeLayout wholeLayout;

    TextView category;
    public static String maincategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        this.setTitle("Account");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        getUserDataFromDB();
        getPermissions();


        wholeLayout = findViewById(R.id.wholeLayout);
        category = findViewById(R.id.category);
        pickPicture = findViewById(R.id.pickPicture);
        pickPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initMatisse();
            }
        });
        name = findViewById(R.id.name);


        save = findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData();
            }
        });

        spinner = findViewById(R.id.spinner);
        spinner2 = findViewById(R.id.spinner2);
        spinner3 = findViewById(R.id.spinner3);
        setupSpinner1();

        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.abc = false;
                Intent i = new Intent(AccountSettings.this, ChooseMainCategory.class);
                i.putExtra("abc", true);
                startActivity(i);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        if(maincategory!=null){
            category.setText("Category: "+maincategory);
        }

    }

    private void updateData() {
        if (type1 == null) {
            CommonUtils.showToast("Select account type");
        } else if (type2 == null) {
            CommonUtils.showToast("Select type");
        } else {
            mDatabase.child("Users").child(SharedPrefs.getUsername()).child("name").setValue(name.getText().toString());
            mDatabase.child("Users").child(SharedPrefs.getUsername()).child("type1").setValue(type1);
            mDatabase.child("Users").child(SharedPrefs.getUsername()).child("mainCategory").setValue(maincategory);
            mDatabase.child("Users").child(SharedPrefs.getUsername()).child("type2").setValue(type2).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    CommonUtils.showToast("Updated!");
                    User user = SharedPrefs.getUser();
                    user.setMainCategory(maincategory);
                    SharedPrefs.setUser(user);
                }
            });
            if (imageUrl.size() > 0) {
                wholeLayout.setVisibility(View.VISIBLE);
                putPictures();
            }

        }
    }

    public void putPictures() {
        String imgName = Long.toHexString(Double.doubleToLongBits(Math.random()));

        ;
        Uri file = Uri.fromFile(new File(imageUrl.get(0)));


        StorageReference riversRef = mStorageRef.child("Photos").child(imgName);

        riversRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    @SuppressWarnings("VisibleForTests")
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content

                        final Uri downloadUrl = taskSnapshot.getDownloadUrl();

                        mDatabase.child("Users").child(SharedPrefs.getUsername()).child("picUrl").setValue("" + downloadUrl).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                CommonUtils.showToast("Picture Uploaded");
                                wholeLayout.setVisibility(View.GONE);
                                SharedPrefs.setPicUrl("" + downloadUrl);
                            }
                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                        Toast.makeText(AccountSettings.this, "error", Toast.LENGTH_SHORT).show();
                    }
                });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        // Check which request we're responding to
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            mSelected = Matisse.obtainResult(data);
            Glide.with(AccountSettings.this).load(mSelected.get(0)).into(pickPicture);
            for (Uri img :
                    mSelected) {
                imageUrl.add(CompressImage.compressImage("" + img, AccountSettings.this));
            }
        }


        super.onActivityResult(requestCode, resultCode, data);

    }

    private void initMatisse() {
        Matisse.from(AccountSettings.this)
                .choose(MimeType.allOf())
                .countable(true)
                .maxSelectable(1)
                .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new GlideEngine())
                .forResult(REQUEST_CODE_CHOOSE);
    }

    private void setupSpinner1() {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                if (position == 1) {
                    setupSpinner2();

                    spinner2.setVisibility(View.VISIBLE);
                    spinner3.setVisibility(View.GONE);
                } else if (position == 2) {
                    setupSpinner3();
                    spinner3.setVisibility(View.VISIBLE);
                    spinner2.setVisibility(View.GONE);
                }
                if (position > 0) {
                    type1 = item;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        List<String> categories = new ArrayList<String>();
        categories.add("Select Type");
        categories.add("Personal");
        categories.add("Social");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

    }

    private void setupSpinner2() {
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                if (position > 0) {
                    type2 = item;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        List<String> categories = new ArrayList<String>();
        categories.add("Select Type");
        categories.add("Single User");
        categories.add("Multi User");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner2.setAdapter(dataAdapter);

    }

    private void setupSpinner3() {
        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                if (position > 0) {
                    type2 = item;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        List<String> categories = new ArrayList<String>();
        categories.add("Select Type");
        categories.add("Facebook");
        categories.add("Instagram");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner3.setAdapter(dataAdapter);

    }

    private void getUserDataFromDB() {
        mDatabase.child("Users").child(SharedPrefs.getUsername()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        type1 = user.getType1();
                        type2 = user.getType2();
                        spinner2.setVisibility(View.VISIBLE);
                        setupSpinner2();
                        wholeLayout.setVisibility(View.GONE);
                        if (user.getPicUrl() != null) {
                            try {
                                Glide.with(AccountSettings.this).load(user.getPicUrl()).into(pickPicture);

                            } catch (IllegalArgumentException e) {
                                e.printStackTrace();
                            }
                        }
                        name.setText(user.getName());
                    }
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

    private void getPermissions() {
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,

        };

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
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
