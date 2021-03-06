package com.google.vrtoolkit.cardboard.samples.treasurehunt.ApplicationSplash;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;

import com.google.vrtoolkit.cardboard.samples.treasurehunt.FileManager.ListFilesActivity;
import com.google.vrtoolkit.cardboard.samples.treasurehunt.R;

/**
 * Created by Horasis Team on 10/05/2016.
 */
public class SplashScreenActivity extends Activity {
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                //Intent i = new Intent(SplashScreenActivity.this, ListFileActivity.class);
                Intent i = new Intent(SplashScreenActivity.this, ListFilesActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

}
