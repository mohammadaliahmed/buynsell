package com.appsinventiv.buyandsell.Activities.Chat;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.appsinventiv.buyandsell.Models.User;
import com.appsinventiv.buyandsell.R;


import java.util.ArrayList;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {
    Context context;
    ArrayList<User> itemList;
    private ArrayList<User> arrayList;

    public UserListAdapter(Context context, ArrayList<User> itemList) {
        this.context = context;
        this.itemList = itemList;
        this.arrayList = new ArrayList<>(itemList);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    public void updateList(ArrayList<User> itemList) {
        this.itemList = itemList;
        arrayList.clear();
        arrayList.addAll(itemList);
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        itemList.clear();
        if (charText.length() == 0) {
            itemList.addAll(arrayList);
        } else {
            for (User user : arrayList) {
                if (user.getUsername().toLowerCase().contains(charText) ||
                        user.getName().toLowerCase().contains(charText) ||
                        user.getPhone().toLowerCase().contains(charText)

                        ) {
                    itemList.add(user);
                }
            }



        }
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User user = itemList.get(position);
//        Glide.with(context).load(user.getPicUrl()).placeholder(R.drawable.ic_profile).into(holder.userImage);
        holder.name.setText(user.getName());
        holder.number.setText(user.getPhone());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(context, ChatScreen.class);
                i.putExtra("number", user.getPhone());
                i.putExtra("userId", user.getUsername());
                context.startActivity(i);


            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView userImage;
        TextView name, number;

        public ViewHolder(View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.userImage);
            name = itemView.findViewById(R.id.name);
            number = itemView.findViewById(R.id.number);
        }
    }

}
