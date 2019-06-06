package com.appsinventiv.buyandsell.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appsinventiv.buyandsell.Models.AdDetails;
import com.appsinventiv.buyandsell.Models.CommentsModel;
import com.appsinventiv.buyandsell.R;
import com.appsinventiv.buyandsell.Utils.CommonUtils;
import com.appsinventiv.buyandsell.Utils.SharedPrefs;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by AliAh on 06/11/2018.
 */

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {
    Context context;
    AdDetails product;
    ArrayList<CommentsModel> arrayList;
    CommentsCallback callback;

    public CommentsAdapter(Context context, ArrayList<CommentsModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    public void setListener(CommentsCallback callback) {
        this.callback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder viewHolder;

        View view = LayoutInflater.from(context).inflate(R.layout.left_comments_item_layout, parent, false);
        viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final CommentsModel model = arrayList.get(position);

        holder.name.setText(model.getName());

        holder.time.setText(CommonUtils.getFormattedDate(model.getTime()));
        holder.comment.setText(model.getCommentText());
        if (model.getPicUrl() != null) {
            Glide.with(context).load(model.getPicUrl()).placeholder(R.drawable.ic_profile_placeholder).into(holder.pic);
        }
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (model.getUserId().equalsIgnoreCase(SharedPrefs.getUsername())) {
                    callback.onDeleteComment(model);
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, time, comment;
        CircleImageView pic;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            time = itemView.findViewById(R.id.time);
            comment = itemView.findViewById(R.id.comment);
            pic = itemView.findViewById(R.id.pic);
        }
    }

    public interface CommentsCallback {
        public void onDeleteComment(CommentsModel model);
    }
}
