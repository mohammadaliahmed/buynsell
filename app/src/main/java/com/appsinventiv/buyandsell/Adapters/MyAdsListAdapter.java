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
import com.appsinventiv.buyandsell.Models.AdDetails;
import com.appsinventiv.buyandsell.R;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by AliAh on 13/01/2018.
 */

public class MyAdsListAdapter extends RecyclerView.Adapter<MyAdsListAdapter.ViewHolder> {

    List<AdDetails> itemList;
    Context context;
    //    private List<String> adTitlesList = Collections.emptyList();
    private LayoutInflater mInflater;
    DatabaseReference mDatabase;

    // data is passed into the constructor
    public MyAdsListAdapter(Context context, ArrayList<AdDetails> itemList) {
        this.mInflater = LayoutInflater.from(context);
        this.itemList = itemList;
        this.context = context;
        mDatabase = FirebaseDatabase.getInstance().getReference();

    }

    @Override
    public MyAdsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.my_ad_item_layout, parent, false);
        MyAdsListAdapter.ViewHolder viewHolder = new MyAdsListAdapter.ViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final MyAdsListAdapter.ViewHolder holder, int position) {
        final AdDetails model = itemList.get(position);
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
////
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

        public ViewHolder(View itemView) {
            super(itemView);
            adTitleView = itemView.findViewById(R.id.ad_title);
            adPriceView = itemView.findViewById(R.id.ad_price);
            adImageView = itemView.findViewById(R.id.ad_picture);


        }


    }

}
