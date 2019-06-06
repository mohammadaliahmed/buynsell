package com.appsinventiv.buyandsell.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.appsinventiv.buyandsell.R;
import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.bumptech.glide.Glide;


public class ViewPictures extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pictures);
        Intent i=getIntent();
        String  url=i.getStringExtra("url");
        ImageView img=findViewById(R.id.img);

        Glide.with(this).load(url).placeholder(R.drawable.placeholder).into(img);
        img.setOnTouchListener(new ImageMatrixTouchHandler(this));
    }
}
