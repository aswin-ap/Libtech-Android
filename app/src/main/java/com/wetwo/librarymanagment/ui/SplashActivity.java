package com.wetwo.librarymanagment.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.wetwo.librarymanagment.BaseActivity;
import com.wetwo.librarymanagment.R;
import com.wetwo.librarymanagment.data.prefrence.SessionManager;

public class SplashActivity extends BaseActivity {

    private static final int SPLASH_SCREEN_TIME_OUT = 2000;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sessionManager = new SessionManager(this);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (sessionManager.isLoggedin()) {
                    Intent i = new Intent(SplashActivity.this,
                            AdminHomeActivity.class);

                    startActivity(i);
                } else {
                    Intent i = new Intent(SplashActivity.this,
                            LoginActivity.class);

                    startActivity(i);
                }

                finish();
                //the current activity will get finished.
            }
        }, SPLASH_SCREEN_TIME_OUT);
    }

}