package com.example.yogshala.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import com.example.yogshala.R;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                finish();
            }
        },2000);
    }
}