package com.appsinventiv.buyandsell.Activities.Chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.appsinventiv.buyandsell.Models.User;
import com.appsinventiv.buyandsell.R;
import com.appsinventiv.buyandsell.Utils.SharedPrefs;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ListOfChats extends AppCompatActivity {

    RecyclerView recyclerview;
    DatabaseReference mDatabase;
    ArrayList<ChatListModel> itemList = new ArrayList<>();
    ChatListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_user);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();
        this.setTitle("Chats");

        recyclerview = findViewById(R.id.recyclerview);

        adapter = new ChatListAdapter(this, itemList);
        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerview.setAdapter(adapter);


        getMessagesFromDB();
    }

    private void getMessagesFromDB() {
        mDatabase.child("Chats").child(SharedPrefs.getUsername()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {

                    itemList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        final String abc = snapshot.getKey();
                        mDatabase.child("Chats").child(SharedPrefs.getUsername()).child(snapshot.getKey()).limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null) {

                                    for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                                        if (snapshot1 != null) {
                                            ChatModel model = snapshot1.getValue(ChatModel.class);
                                            if (model != null) {
                                                itemList.add(new ChatListModel(abc, model));
                                            }
                                        }
                                    }

                                    Collections.sort(itemList, new Comparator<ChatListModel>() {
                                        @Override
                                        public int compare(ChatListModel listData, ChatListModel t1) {
                                            Long ob1 = listData.getMessage().getTime();
                                            Long ob2 = t1.getMessage().getTime();

                                            return ob2.compareTo(ob1);

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
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {


            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
