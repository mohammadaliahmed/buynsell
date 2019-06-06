package com.appsinventiv.buyandsell.Activities.Chat;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.appsinventiv.buyandsell.Activities.ViewPictures;
import com.appsinventiv.buyandsell.R;
import com.appsinventiv.buyandsell.Utils.CommonUtils;
import com.appsinventiv.buyandsell.Utils.Constants;
import com.appsinventiv.buyandsell.Utils.SharedPrefs;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;


public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    Context context;
    ArrayList<ChatModel> chatList;

    public int RIGHT_CHAT = 1;
    public int LEFT_CHAT = 0;
    private CallBacks callbacks;


    public ChatAdapter(Context context, ArrayList<ChatModel> chatList) {
        this.context = context;
        this.chatList = chatList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder viewHolder;
        if (viewType == RIGHT_CHAT) {
            View view = LayoutInflater.from(context).inflate(R.layout.right_chat_layout, parent, false);
            viewHolder = new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.left_chat_layout, parent, false);
            viewHolder = new ViewHolder(view);
        }

        return viewHolder;
    }

    public void setCallbacks(CallBacks callbacks) {
        this.callbacks = callbacks;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final ChatModel model = chatList.get(position);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                callbacks.deleteMsg(model);
                return false;
            }
        });
//

        if (model.getMessageType().equals(Constants.MESSAGE_TYPE_IMAGE)) {
            holder.time.setText("" + CommonUtils.getFormattedDate(model.getTime()));
            holder.msgtext.setVisibility(View.GONE);
//            holder.name.setText(model.getName());
            holder.image.setVisibility(View.VISIBLE);
//            Glide.with(context).load(model.getPicUrl()).placeholder(R.drawable.ic_profile).into(holder.profile);
            Glide.with(context).load(model.getImageUrl()).into(holder.image);
        } else if (model.getMessageType().equals(Constants.MESSAGE_TYPE_TEXT)) {
            holder.image.setVisibility(View.GONE);
            holder.msgtext.setVisibility(View.VISIBLE);
//            holder.name.setText(model.getName());

//            Glide.with(context).load(model.getPicUrl()).placeholder(R.drawable.ic_profile).into(holder.profile);
            holder.msgtext.setText(model.getMessageText());
            holder.time.setText("" + CommonUtils.getFormattedDate(model.getTime()));

        }
//        }
//        holder.profile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                Intent i = new Intent(context, UserProfile.class);
////                i.putExtra("username", model.getMessageBy());
////                context.startActivity(i);
//            }
//        });


        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, ViewPictures.class);
                i.putExtra("url", model.getImageUrl());
                context.startActivity(i);
//                String filename = "" + model.getImageUrl().substring(model.getImageUrl().length() - 7, model.getImageUrl().length());

//
//                File applictionFile = new File(Environment.getExternalStoragePublicDirectory(
//                        Environment.DIRECTORY_DOWNLOADS) + "/" + filename + ".png");
//
//                if (applictionFile != null && applictionFile.exists()) {
//                } else {
////                    DownloadFile.fromUrl(model.getImageUrl(), filename + ".png");
//
//
//                }


            }
        });


    }


    //return my chat or other user chat on either sides
    @Override
    public int getItemViewType(int position) {
        ChatModel model = chatList.get(position);
        if (model.getMessageBy().equalsIgnoreCase(SharedPrefs.getUsername())) {
            return RIGHT_CHAT;
        } else {
            return LEFT_CHAT;
        }

    }


    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView msgtext, time;
        ImageView profile;
        ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);

            msgtext = itemView.findViewById(R.id.msgtext);
            time = itemView.findViewById(R.id.time);
//            profile = itemView.findViewById(R.id.profile_image);
//            name = itemView.findViewById(R.id.name);
            image = itemView.findViewById(R.id.image);

//            seekBar.setOnSeekBarChangeListener(this);


        }


    }



    public interface CallBacks {
        public void deleteMsg(ChatModel model);
    }

}
