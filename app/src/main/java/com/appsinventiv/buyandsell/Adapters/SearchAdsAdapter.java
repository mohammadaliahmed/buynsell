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
import com.appsinventiv.buyandsell.Utils.SharedPrefs;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by AliAh on 13/01/2018.
 */

public class SearchAdsAdapter extends RecyclerView.Adapter<SearchAdsAdapter.ViewHolder> {

    List<AdDetails> itemList;
    Context context;
    private LayoutInflater mInflater;
    private ArrayList<AdDetails> arrayList;
    ArrayList<String> likedAds;
    HomepageAdsAdapter.AdapterCallbacks callbacks;


    public SearchAdsAdapter(Context context, ArrayList<AdDetails> itemList, ArrayList<String> likedAds) {
        this.mInflater = LayoutInflater.from(context);
        this.itemList = itemList;
        this.context = context;
        this.arrayList = new ArrayList<>(itemList);
        this.likedAds = likedAds;

    }


    public void setCallbacks(HomepageAdsAdapter.AdapterCallbacks callbacks) {
        this.callbacks = callbacks;
    }

    public void updateList(ArrayList<AdDetails> productList) {
        this.itemList = productList;
        arrayList.clear();
        arrayList.addAll(productList);
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        itemList.clear();
        if (charText.length() == 0) {
            itemList.addAll(arrayList);
        } else {
            for (AdDetails product : arrayList) {
                if (product.getTitle().toLowerCase().contains(charText) ||
                        product.getCategoryList().get(0).contains(charText) || product.getCategoryList().get(1).contains(charText)

                        ) {
                    itemList.add(product);
                }
            }


        }
        notifyDataSetChanged();
    }

    @Override
    public SearchAdsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.search_ad_item_layout, parent, false);
        SearchAdsAdapter.ViewHolder viewHolder = new SearchAdsAdapter.ViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final SearchAdsAdapter.ViewHolder holder, int position) {
        final AdDetails model = itemList.get(position);
        if (likedAds.contains(model.getAdId())) {
            holder.heart_button.setLiked(true);
        } else {
            holder.heart_button.setLiked(false);
        }
        DecimalFormat formatter = new DecimalFormat("##,###,###");
        String formatedPrice = formatter.format(model.getPrice());
        holder.adTitleView.setText(model.getTitle());
        holder.adPriceView.setText("Rs " + formatedPrice);
        Glide.with(context).load(model.getPictures().get(0)).into(holder.adImageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, AdPage.class);

                i.putExtra("adId", "" + model.getAdId());
                context.startActivity(i);
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
        public TextView adTitleView, adPriceView;
        public ImageView adImageView;
        LikeButton heart_button;

        public ViewHolder(View itemView) {
            super(itemView);
            adTitleView = itemView.findViewById(R.id.ad_title);
            adPriceView = itemView.findViewById(R.id.ad_price);
            adImageView = itemView.findViewById(R.id.ad_picture);
            heart_button = itemView.findViewById(R.id.heart_button);


        }


    }

    public interface AdapterCallbacks {
        public void onLiked(AdDetails model, boolean liked);
    }


}
