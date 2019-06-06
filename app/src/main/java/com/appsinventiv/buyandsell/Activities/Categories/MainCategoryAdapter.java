package com.appsinventiv.buyandsell.Activities.Categories;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appsinventiv.buyandsell.Activities.SubmitAd;
import com.appsinventiv.buyandsell.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MainCategoryAdapter extends RecyclerView.Adapter<MainCategoryAdapter.ViewHolder> {
    Context context;
    ArrayList<MainCategoryModel> itemList;


    public MainCategoryAdapter(Context context, ArrayList<MainCategoryModel> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.main_category_item_layout, parent, false);
        MainCategoryAdapter.ViewHolder viewHolder = new MainCategoryAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final MainCategoryModel model = itemList.get(position);

        holder.maincategory.setText(model.getMainCategory());
        Glide.with(context).load(model.getUrl()).into(holder.icon);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SubmitAd.categoryList.clear();
                SubmitAd.categoryList.add(model.getMainCategory());
                Intent i = new Intent(context, ChooseCategory.class);
                i.putExtra("parentCategory", model.getMainCategory());
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView maincategory, subCategories;
        ImageView icon;

        public ViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            maincategory = itemView.findViewById(R.id.maincategory);
            subCategories = itemView.findViewById(R.id.subCategories);
        }
    }
}
