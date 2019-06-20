package com.appsinventiv.buyandsell.Activities.Locations;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appsinventiv.buyandsell.Activities.Customer.SignUp;
import com.appsinventiv.buyandsell.Activities.Filters;
import com.appsinventiv.buyandsell.Activities.SubmitAd;
import com.appsinventiv.buyandsell.R;

import java.util.ArrayList;

/**
 * Created by AliAh on 27/11/2018.
 */

public class LocationsAdapter extends RecyclerView.Adapter<LocationsAdapter.ViewHolder> {
    Context context;
    ArrayList<String> list;
    GetNewData getNewData;

    public LocationsAdapter(Context context, ArrayList<String> list, GetNewData getNewData) {
        this.context = context;
        this.list = list;
        this.getNewData = getNewData;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.choose_category_item, parent, false);
        LocationsAdapter.ViewHolder viewHolder = new LocationsAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final String title = list.get(position);
        holder.title.setText(title);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                SubmitAd.location=title;
                SignUp.location=title;
                Filters.location=title;

                getNewData.whichCategory(title);


            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
        }
    }
    public interface GetNewData {
        public void whichCategory(String title);
    }

}
