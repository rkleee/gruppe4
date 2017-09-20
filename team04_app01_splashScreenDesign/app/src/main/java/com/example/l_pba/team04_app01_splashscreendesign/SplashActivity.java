package com.example.l_pba.team04_app01_splashscreendesign;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ProgressBar;

public class SplashActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 4000;
    private static final int TIME_RUNTIME = 1000;
    private static final String TAG = "splashScreenDesign";

    protected boolean mbActive;
    protected ProgressBar progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splashactivity_main);
        //Toast.makeText(this, "Opening", Toast.LENGTH_SHORT).show();

        progressbar = (ProgressBar) findViewById(R.id.progressBar2);

        final Thread timerThread = new Thread() {
            @Override
            public void run() {
                mbActive = true;
                try {
                    int wait = 0;
                    while (mbActive && (wait < TIME_RUNTIME)) {
                        sleep(200);
                        if (mbActive) {
                            wait += 200;
                            updateProgress(wait);
                        }
                    }
                } catch (InterruptedException e) {
                    Log.d(TAG,"InterruptedException was thrown.");
                } finally {
                    onContinue();
                }
            }
        };
        timerThread.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent homeIntent = new Intent(SplashActivity.this, HomeActivity.class);
                startActivity(homeIntent);
                finish();
            }
        },SPLASH_TIME_OUT);

    }
        public void updateProgress(final int timePassed) {
            if(null != progressbar) {
                final int progress = progressbar.getMax() * timePassed/TIME_RUNTIME;
                progressbar.setProgress(progress);
            }
        }

        public void onContinue() {
            Log.d(TAG,"Found something in onContinue()-Method");
        }
}
