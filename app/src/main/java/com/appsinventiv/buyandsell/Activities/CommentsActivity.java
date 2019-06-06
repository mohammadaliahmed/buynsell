package com.appsinventiv.buyandsell.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.appsinventiv.buyandsell.Activities.Chat.ChatModel;
import com.appsinventiv.buyandsell.Adapters.CommentsAdapter;
import com.appsinventiv.buyandsell.Models.AdDetails;
import com.appsinventiv.buyandsell.Models.CommentsModel;
import com.appsinventiv.buyandsell.R;
import com.appsinventiv.buyandsell.Utils.CommonUtils;
import com.appsinventiv.buyandsell.Utils.SharedPrefs;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class CommentsActivity extends AppCompatActivity {
    DatabaseReference mDatabase;
    String adId;
    CommentsAdapter adapter;
    RecyclerView recyclerView;
    EditText comment;
    ImageView send;
    ArrayList<CommentsModel> itemList = new ArrayList<>();
    ImageView productImage;
    TextView title, price;
    CardView adLayout;
    private AdDetails adDetails;
    int commentsCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        this.setTitle("Comments");
        adId = getIntent().getStringExtra("adId");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        recyclerView = findViewById(R.id.recycler);
        comment = findViewById(R.id.comment);
        send = findViewById(R.id.send);
        productImage = findViewById(R.id.productImage);
        title = findViewById(R.id.title);
        price = findViewById(R.id.price);
        adLayout = findViewById(R.id.adLayout);
        adLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CommentsActivity.this, AdPage.class);
                intent.putExtra("adId", adId);
                startActivity(intent);
                finish();
            }
        });

        getCommentsFromDB();
        getAdDetailsFromDB();

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new CommentsAdapter(this, itemList);
        recyclerView.setAdapter(adapter);
        adapter.setListener(new CommentsAdapter.CommentsCallback() {
            @Override
            public void onDeleteComment(CommentsModel model) {
                showAlert(model);
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (comment.getText().length() == 0) {
                    comment.setError("Empty");
                } else {
                    postComment();
                }
            }
        });

    }

    private void showAlert(final CommentsModel model) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete comment? ");

        // add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                mDatabase.child("Comments").child(adId).child(model.getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        CommonUtils.showToast("Comment deleted");
                        updateCommentCount();
                    }
                });
            }
        });
        builder.setNegativeButton("Cancel", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void getAdDetailsFromDB() {
        mDatabase.child("Ads").child(adId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    adDetails = dataSnapshot.getValue(AdDetails.class);
                    if (adDetails != null) {
                        title.setText(adDetails.getTitle());
                        price.setText("Rs " + adDetails.getPrice());
                        try {
                            Glide.with(CommentsActivity.this).load(adDetails.getPictures().get(0)).into(productImage);

                        } catch (IllegalArgumentException e) {
                        }

                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void getCommentsFromDB() {

        mDatabase.child("Comments").child(adId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    itemList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        CommentsModel model = snapshot.getValue(CommentsModel.class);
                        if (model != null) {
                            itemList.add(model);

                        }
                    }


                    Collections.sort(itemList, new Comparator<CommentsModel>() {
                        @Override
                        public int compare(CommentsModel listData, CommentsModel t1) {
                            Long ob1 = listData.getTime();
                            Long ob2 = t1.getTime();

                            return ob1.compareTo(ob2);

                        }
                    });
                    adapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(itemList.size() - 1);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void postComment() {
        String key = mDatabase.push().getKey();
        mDatabase.child("Comments").child(adId).child(key)
                .setValue(new CommentsModel(key,
                        adId,
                        SharedPrefs.getUsername(), SharedPrefs.getUser().getPicUrl(),
                        SharedPrefs.getName()
                        , comment.getText().toString()
                        , System.currentTimeMillis()
                ))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        CommonUtils.showToast("Comment added");
                        recyclerView.scrollToPosition(itemList.size() - 1);

                        updateCommentCount();

                        comment.setText("");
                    }
                });
    }

    private void updateCommentCount() {
        mDatabase.child("Ads").child(adId).child("commentsCount").setValue(itemList.size() ).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                adapter.notifyDataSetChanged();
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
