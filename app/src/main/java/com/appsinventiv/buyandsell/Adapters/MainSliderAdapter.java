package com.appsinventiv.buyandsell.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.appsinventiv.buyandsell.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by AliAh on 21/02/2018.
 */

public class MainSliderAdapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;
    ArrayList<String> picturesList;

    public MainSliderAdapter(Context context, ArrayList<String> picturesList) {

        this.context = context;
        this.picturesList = picturesList;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.product_slider, container, false);
        ImageView imageView = view.findViewById(R.id.slider_image);
        Glide.with(context).load(picturesList.get(position)).into(imageView);
        container.addView(view);


        return view;
    }

    @Override
    public int getCount() {
        return picturesList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);

    }

}
