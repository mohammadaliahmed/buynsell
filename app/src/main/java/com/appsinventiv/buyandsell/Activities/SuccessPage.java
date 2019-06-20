package com.appsinventiv.buyandsell.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.appsinventiv.buyandsell.R;


public class SuccessPage extends AppCompatActivity {
    Button backtohome, postnew, myAds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_page);
        SuccessPage.this.setTitle("Success");
        backtohome = (Button) findViewById(R.id.back);
        postnew = (Button) findViewById(R.id.postnew);
        myAds = (Button) findViewById(R.id.viewmyads);

        SubmitAd.categoryList.clear();
        SubmitAccountAd.categoryList.clear();


        myAds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Intent i=new Intent(SuccessPage.this, MyAds.class);
//                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//                startActivity(i);
//                finishAllActivities();
//                finish();
            }
        });


        backtohome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SuccessPage.this, MainActivity.class);
//                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(i);
//                finishAllActivities();
                finish();
            }
        });
        postnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SuccessPage.this, SubmitAd.class);
//                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(i);
//                finishAllActivities();

                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {

//        Intent i =new Intent(SuccessPage.this,MainActivity.class);
//        startActivity(i);
//        finishAllActivities();
//        finish();
    }

    private void finishAllActivities() {
//        ChildCategory.fa.finish();
////        SubChild.fa.finish();
//        MainCategory.fa.finish();
//        SubmitAd.fa.finish();
    }

}
