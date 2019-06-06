package com.appsinventiv.buyandsell.Activities.Chat;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appsinventiv.buyandsell.R;
import com.appsinventiv.buyandsell.Utils.CommonUtils;
import com.appsinventiv.buyandsell.Utils.Constants;
import com.bumptech.glide.Glide;


import java.util.ArrayList;



public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    Context context;
    ArrayList<ChatListModel> itemList;


    public ChatListAdapter(Context context, ArrayList<ChatListModel> itemList) {
        this.context = context;
        this.itemList = itemList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_list_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final ChatListModel model = itemList.get(position);

        holder.username.setText(model.getMessage().getName());
        Glide.with(context).load(model.getMessage().getPicUrl()).into(holder.image);
//        holder.message.setText(model.getText());
        if (model.getMessage().getMessageType().equals(Constants.MESSAGE_TYPE_IMAGE)) {
            holder.message.setText("" + "\uD83D\uDCF7 Image");
        } else if (model.getMessage().getMessageType().equals(Constants.MESSAGE_TYPE_AUDIO)) {
            holder.message.setText("" + "\uD83C\uDFB5 Audio");
        } else if (model.getMessage().getMessageType().equals(Constants.MESSAGE_TYPE_DOCUMENT)) {
            holder.message.setText("" + "\uD83D\uDCC4 Document");
        } else if (model.getMessage().getMessageType().equals(Constants.MESSAGE_TYPE_TEXT)) {
            holder.message.setText(model.getMessage().getMessageText());
        }


        holder.time.setText(CommonUtils.getFormattedDate(model.getMessage().getTime()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, ChatScreen.class);
                i.putExtra("userId", model.getMessage().getUsername());
                i.putExtra("adId", model.getMessage().getAdId());
                context.startActivity(i);
            }
        });


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView username, message, time, count;
        ImageView image;
        public ViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            message = itemView.findViewById(R.id.message);
            time = itemView.findViewById(R.id.time);
            count = itemView.findViewById(R.id.count);
            image = itemView.findViewById(R.id.image);

        }
    }
}
