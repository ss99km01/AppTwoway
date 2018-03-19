package com.jefeko.apptwoway.ui;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.jefeko.apptwoway.R;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                goPage();
            }
        }, 2000);
    }
    public void goPage(){
        Intent intent = new Intent(this,LoginActivity.class);

        startActivity(intent);
        finish();
    }
}
