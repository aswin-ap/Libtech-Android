package com.wetwo.librarymanagment.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.wetwo.librarymanagment.BaseActivity;
import com.wetwo.librarymanagment.R;
import com.wetwo.librarymanagment.data.prefrence.SessionManager;
import com.wetwo.librarymanagment.ui.admin.HomeActivity;
import com.wetwo.librarymanagment.ui.auth.LoginActivity;
import com.wetwo.librarymanagment.ui.auth.PreAuthActivity;
import com.wetwo.librarymanagment.ui.user.UserHomeActivity;

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
                    Intent i;
                    if (sessionManager.getUserName().equals("admin")) {
                        i = new Intent(SplashActivity.this,
                                HomeActivity.class);
                    } else {
                        i = new Intent(SplashActivity.this,
                                UserHomeActivity.class);
                    }
                    startActivity(i);
                } else {
                    Intent i = new Intent(SplashActivity.this,
                            PreAuthActivity.class);

                    startActivity(i);
                }

                finish();
                //the current activity will get finished.
            }
        }, SPLASH_SCREEN_TIME_OUT);
    }

}