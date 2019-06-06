package com.appsinventiv.buyandsell.Activities.Chat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appsinventiv.buyandsell.Activities.AdPage;
import com.appsinventiv.buyandsell.Interface.NotificationObserver;
import com.appsinventiv.buyandsell.Models.AdDetails;
import com.appsinventiv.buyandsell.Models.User;
import com.appsinventiv.buyandsell.R;

import com.appsinventiv.buyandsell.Utils.CommonUtils;
import com.appsinventiv.buyandsell.Utils.CompressImage;
import com.appsinventiv.buyandsell.Utils.Constants;
import com.appsinventiv.buyandsell.Utils.GifSizeFilter;
import com.appsinventiv.buyandsell.Utils.NotificationAsync;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChatScreen extends AppCompatActivity implements NotificationObserver {

    private static final int REQUEST_CODE_CHOOSE = 100;
    DatabaseReference mDatabase;
    EditText message;
    ImageView send, back;
    RelativeLayout messagingArea;
    CardView adLayout;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    ChatAdapter adapter;
    ArrayList<ChatModel> chatModelArrayList = new ArrayList<>();
    int soundId;
    SoundPool sp;
    ImageView pick;
    List<Uri> mSelected = new ArrayList<>();
    ArrayList<String> imageUrl = new ArrayList<>();
    StorageReference mStorageRef;
    private static final int REQUEST_CODE_FILE = 25;

    String otherFcmkey;

    String msgText;
    AdDetails adFromDB;


    boolean isAttachAreaVisible = false;
    Uri file;
    String messageId;
//    Toolbar toolbar;
//    TextView userName, userStatus;

    User otherParticipant;
    User myselfParticipant;
    String otherUserId;
    String adId;
    TextView adTitle, adPrice;
    ImageView adImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);
//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        getPermissions();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

        }
        adId = getIntent().getStringExtra("adId");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        otherUserId = getIntent().getStringExtra("userId");
        getParticipants(otherUserId);
        this.setTitle("");
//        userStatus = findViewById(R.id.userStatus);
//        userName = findViewById(R.id.userName);
        adTitle = findViewById(R.id.adTitle);
        adPrice = findViewById(R.id.adPrice);
        adImage = findViewById(R.id.adImage);
        send = findViewById(R.id.send);
        message = findViewById(R.id.message);
        pick = findViewById(R.id.pick);
        adLayout = findViewById(R.id.adLayout);

        adLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChatScreen.this, AdPage.class);
                i.putExtra("adId", adId);
                startActivity(i);
            }
        });

        messagingArea = findViewById(R.id.messageArea);


        recyclerView = findViewById(R.id.chats);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ChatAdapter(this, chatModelArrayList);
        recyclerView.setAdapter(adapter);
        adapter.setCallbacks(new ChatAdapter.CallBacks() {
            @Override
            public void deleteMsg(ChatModel model) {
                showAlert(model);
            }
        });

        getAdDetailsFromDB();

        message.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if (b) {
                    recyclerView.scrollToPosition(chatModelArrayList.size() - 1);
                }

            }
        });


        sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        soundId = sp.load(ChatScreen.this, R.raw.tick_sound, 1);


        pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isAttachAreaVisible = false;

                mSelected.clear();
                imageUrl.clear();
                initMatisse();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (message.getText().length() == 0) {
                    message.setError("Can't send empty message");
                } else {
                    msgText = message.getText().toString();
                    sp.play(soundId, 1, 1, 0, 0, 1);

                    messageId = mDatabase.push().getKey();


                    sendMyMessageToServer(Constants.MESSAGE_TYPE_TEXT, "", "", otherParticipant);
                    sendParticipantMessageToServer(Constants.MESSAGE_TYPE_TEXT, "", "", myselfParticipant);


                }
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_CHOOSE && data != null) {
            mSelected = Matisse.obtainResult(data);
            for (Uri img : mSelected) {
                imageUrl.add(CompressImage.compressImage("" + img, ChatScreen.this));
            }
            for (String img : imageUrl) {
                sendMyPicMessageToServer(Constants.MESSAGE_TYPE_IMAGE, img, "", otherParticipant);
//                sendParticipantMessageToServer(Constants.MESSAGE_TYPE_TEXT, "", "", myselfParticipant);

            }
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getAdDetailsFromDB() {
        mDatabase.child("Ads").child(adId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    adFromDB = dataSnapshot.getValue(AdDetails.class);
                    if (adFromDB != null) {
                        Glide.with(ChatScreen.this).load(adFromDB.getPictures().get(0)).into(adImage);
                        adTitle.setText(adFromDB.getTitle());
                        adPrice.setText("Rs " + adFromDB.getPrice());
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //delete chat
    private void showAlert(final ChatModel model) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete message? ");

        // add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mDatabase.child("Chats").child(SharedPrefs.getUsername()).child(otherParticipant.getUsername())
                        .child(model.getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        CommonUtils.showToast("Message deleted!");
                    }
                });

            }
        });
        builder.setNegativeButton("Cancel", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }


    private void getParticipants(final String us) {
        mDatabase.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    User i = dataSnapshot.child(us).getValue(User.class);
                    if (i != null) {
                        otherParticipant = i;
                        getMessagesFromServer(otherParticipant);
                        otherFcmkey = i.getFcmKey();
                        ChatScreen.this.setTitle(i.getName());


                    }
                    User i2 = dataSnapshot.child(SharedPrefs.getUsername()).getValue(User.class);
                    if (i2 != null) {
                        myselfParticipant = i2;

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void initMatisse() {

        Matisse.from(ChatScreen.this)
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


    private void sendMyMessageToServer(final String type, final String url, String extension, final User model) {

//        final String msg = message.getText().toString();
        message.setText(null);

        final ChatModel modell = new ChatModel(
                messageId,
                msgText,
                SharedPrefs.getUsername(),
                type.equals(Constants.MESSAGE_TYPE_IMAGE) ? url : "",
                type.equals(Constants.MESSAGE_TYPE_AUDIO) ? url : "",
                type.equals(Constants.MESSAGE_TYPE_VIDEO) ? url : "",
                type.equals(Constants.MESSAGE_TYPE_DOCUMENT) ? url : "",
                model.getUsername(),
                adFromDB.getTitle(),
                adFromDB.getPictures().get(0),
                type,
                "." + extension,
                adId,
                System.currentTimeMillis(),
                12312

        );

        mDatabase.child("Chats").child(SharedPrefs.getUsername()).child(adId)
                .child(messageId).setValue(modell).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
//                mDatabase.child("Chats").child(otherParticipant.getUsername()).child(adId)
//                        .child(messageId).setValue(modell);
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(chatModelArrayList.size() - 1);
                sendNotification(Constants.MESSAGE_TYPE_TEXT);

            }
        }).

                addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });


    }

    private void sendNotification(String type) {
        NotificationAsync notificationAsync = new NotificationAsync(ChatScreen.this);
        String NotificationTitle = "New message ";
        String NotificationMessage = "";
        if (type.equals(Constants.MESSAGE_TYPE_TEXT)) {
            NotificationMessage = SharedPrefs.getName() + ": " + msgText;
        } else if (type.equals(Constants.MESSAGE_TYPE_IMAGE)) {
            NotificationMessage = SharedPrefs.getName() + ": \uD83D\uDCF7 Image";
        } else if (type.equals(Constants.MESSAGE_TYPE_AUDIO)) {
            NotificationMessage = SharedPrefs.getName() + ": \uD83C\uDFB5 Audio";
        } else if (type.equals(Constants.MESSAGE_TYPE_DOCUMENT)) {
            NotificationMessage = SharedPrefs.getName() + ": \uD83D\uDCC4 Document";
        }
        notificationAsync.execute("aliahmed", otherParticipant.getFcmKey(), NotificationTitle, NotificationMessage, "Chat", otherUserId, adId);

    }


    private void sendParticipantMessageToServer(final String type, final String url, String extension, final User model) {

//        final String msg = message.getText().toString();
        message.setText(null);

        final ChatModel modell = new ChatModel(
                messageId,
                msgText,
                SharedPrefs.getUsername(),
                type.equals(Constants.MESSAGE_TYPE_IMAGE) ? url : "",
                type.equals(Constants.MESSAGE_TYPE_AUDIO) ? url : "",
                type.equals(Constants.MESSAGE_TYPE_VIDEO) ? url : "",
                type.equals(Constants.MESSAGE_TYPE_DOCUMENT) ? url : "",
                model.getUsername(),
                adFromDB.getTitle(),
                adFromDB.getPictures().get(0),
                type,
                "." + extension,
                adId,
                System.currentTimeMillis(),
                12312

        );

        mDatabase.child("Chats").child(otherParticipant.getUsername()).child(adId)
                .child(messageId).setValue(modell);

    }


    private void sendMyPicMessageToServer(final String type, final String url, String extension, final User model) {

        final String msg = message.getText().toString();
        message.setText(null);
        final String newMsgId = mDatabase.push().getKey();

        final ChatModel modell = new ChatModel(
                newMsgId,
                msgText,
                SharedPrefs.getUsername(),
                type.equals(Constants.MESSAGE_TYPE_IMAGE) ? url : "",
                type.equals(Constants.MESSAGE_TYPE_AUDIO) ? url : "",
                type.equals(Constants.MESSAGE_TYPE_VIDEO) ? url : "",
                type.equals(Constants.MESSAGE_TYPE_DOCUMENT) ? url : "",
                model.getUsername(),
                adFromDB.getTitle(),
                adFromDB.getPictures().get(0),
                type,
                "." + extension,
                adId,
                System.currentTimeMillis(),
                12312

        );

        mDatabase.child("Chats").child(SharedPrefs.getUsername()).child(adId)
                .child(newMsgId).setValue(modell).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                putPictures(url, modell, newMsgId);


                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(chatModelArrayList.size() - 1);
                sendNotification(Constants.MESSAGE_TYPE_IMAGE);

            }
        }).

                addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }


    public void putPictures(String path, final ChatModel chatModel, final String newMsgId) {
        String imgName = Long.toHexString(Double.doubleToLongBits(Math.random()));

        final Uri file = Uri.fromFile(new File(path));

        mStorageRef = FirebaseStorage.getInstance().getReference();

        StorageReference riversRef = mStorageRef.child("Photos").child(imgName);

        riversRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    @SuppressWarnings("VisibleForTests")
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Uri downloadUrl = taskSnapshot.getDownloadUrl();

                        mDatabase.child("Chats").child(SharedPrefs.getUsername()).child(adId)
                                .child(newMsgId).child("imageUrl").setValue("" + downloadUrl);
                        chatModel.setImageUrl("" + downloadUrl);
                        mDatabase.child("Chats").child(otherParticipant.getUsername()).child(adId)
                                .child(newMsgId).setValue(chatModel);


                        String k = mDatabase.push().getKey();
                        mDatabase.child("Images").child(k).setValue(new MediaModel(k, Constants.MESSAGE_TYPE_IMAGE, "" + downloadUrl, System.currentTimeMillis()));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                        CommonUtils.showToast(exception.getMessage() + "");

                    }
                });


    }

    //get all messages from server of mine
    private void getMessagesFromServer(User other) {


        mDatabase.child("Chats").child(SharedPrefs.getUsername()).child(adId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    chatModelArrayList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ChatModel model = snapshot.getValue(ChatModel.class);
                        if (model != null) {
                            chatModelArrayList.add(model);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(chatModelArrayList.size() - 1);

                } else {
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getPermissions() {
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.USE_SIP,
        };

        if (!hasPermissions(ChatScreen.this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }


    public boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                } else {

                }
            }
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void setMargins(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (item.getItemId() == android.R.id.home) {


            finish();
        }

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onSuccess(String chatId) {

    }

    @Override
    public void onFailure() {

    }
}


