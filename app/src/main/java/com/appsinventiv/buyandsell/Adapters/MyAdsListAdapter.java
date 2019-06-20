package com.appsinventiv.buyandsell.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appsinventiv.buyandsell.Activities.AdPage;
import com.appsinventiv.buyandsell.Activities.Customer.Login;
import com.appsinventiv.buyandsell.Models.AdDetails;
import com.appsinventiv.buyandsell.R;
import com.appsinventiv.buyandsell.Utils.CommonUtils;
import com.appsinventiv.buyandsell.Utils.SharedPrefs;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by AliAh on 13/01/2018.
 */

public class MyAdsListAdapter extends RecyclerView.Adapter<MyAdsListAdapter.ViewHolder> {

    List<AdDetails> itemList;
    Context context;
    HomepageAdsAdapter.AdapterCallbacks callbacks;
    ArrayList<String> likedAds;
    public MyAdsListAdapter(Context context, ArrayList<AdDetails> itemList, ArrayList<String> likedAds) {
        this.itemList = itemList;
        this.context = context;
        this.likedAds = likedAds;

    }


    @Override
    public MyAdsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.my_ad_item_layout, parent, false);
        MyAdsListAdapter.ViewHolder viewHolder = new MyAdsListAdapter.ViewHolder(view);
        return viewHolder;
    }
    public void setCallbacks(HomepageAdsAdapter.AdapterCallbacks callbacks) {
        this.callbacks = callbacks;
    }


    @Override
    public void onBindViewHolder(final MyAdsListAdapter.ViewHolder holder, int position) {
        final AdDetails model = itemList.get(position);
        if (likedAds.contains(model.getAdId())) {
            holder.heart_button.setLiked(true);
        } else {
            holder.heart_button.setLiked(false);
        }


        DecimalFormat formatter = new DecimalFormat("##,###,###");
        String formatedPrice = formatter.format(model.getPrice());
        holder.ad_time.setText(CommonUtils.getFormattedDate(model.getTime()));
        holder.adTitleView.setText(model.getTitle());
        holder.adPriceView.setText("Rs " + formatedPrice);
        Glide.with(context).load(model.getPictures().get(0)).into(holder.adImageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, AdPage.class);

                i.putExtra("adId", "" + model.getAdId());
                i.putExtra("type", model.getAdType());

                context.startActivity(i);
////
            }
        });


            holder.heart_button.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    if (SharedPrefs.getUsername().equalsIgnoreCase("")) {
                        holder.heart_button.setLiked(false);
                        context.startActivity(new Intent(context,Login.class));

                    } else {
                        model.setLikesCount(model.getLikesCount() + 1);

                        callbacks.onLiked(model, true);
                    }                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    model.setLikesCount(model.getLikesCount() - 1);

                    callbacks.onLiked(model, false);

                }
            });





    }

    @Override
    public int getItemCount() {

        return itemList.size();

    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        public View myView;
        public TextView adTitleView, adPriceView,ad_time;
        public ImageView adImageView;
        LikeButton heart_button;

        public ViewHolder(View itemView) {
            super(itemView);
            adTitleView = itemView.findViewById(R.id.ad_title);
            adPriceView = itemView.findViewById(R.id.ad_price);
            adImageView = itemView.findViewById(R.id.ad_picture);
            ad_time = itemView.findViewById(R.id.ad_time);
            heart_button = itemView.findViewById(R.id.heart_button);


        }


    }
    public interface AdapterCallbacks {
        public void onLiked(AdDetails model, boolean liked);
    }

}
