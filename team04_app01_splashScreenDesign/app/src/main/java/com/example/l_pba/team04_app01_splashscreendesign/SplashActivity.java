/**
 * @author group04
 * @version 1.0
 * SplashActivity, the loading screen of the app
 */
package com.example.l_pba.team04_app01_splashscreendesign;

/**
 * Android Imports
 */
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

/**
 * SplashScreen with the loading animation
 */
public class SplashActivity extends AppCompatActivity {
    private ConstraintLayout cL;

    //loading images (flipbook)
    int images[] = {R.drawable.mcroute0,R.drawable.mcroute1,R.drawable.mcroute2,R.drawable.mcroute3,
            R.drawable.mcroute4,R.drawable.mcroute5,R.drawable.mcroute6,R.drawable.mcroute7,
            R.drawable.mcroute8,R.drawable.mcroute9,R.drawable.mcroute10,R.drawable.mcroute11,
            R.drawable.mcroute11,R.drawable.mcroute11};

    @Override
    /**
     * @param savedInstanceState Bundle
     */
    protected void onCreate(Bundle savedInstanceState) {
        /**
         * Android Shit
         */
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //No title on splashScreen
        setContentView(R.layout.splashactivity_main);

        /**
         * Loading Audio from Settings
         */
        SharedPreferences sPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String prefAudioKey = getString(R.string.preference_audio_key);
        Boolean audio = sPrefs.getBoolean(prefAudioKey, true);

        //set ScreenOrientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        /**
         * Layout initialize
         */
        cL = (ConstraintLayout) findViewById(R.id.constraintLayout);
        cL.setBackgroundResource(images[0]); //first image


        //start MusicBackgroundService if audio-setting is on
        if (audio) startService(new Intent(this,MusicBackgroundService.class));

        //flipbook implementation
        new CountDownTimer(3900, 300) {
            int i = 1; //get the right image of the images[]
            public void onTick(long millisUntilFinished) {
                cL.setBackgroundResource(images[i]);
                i++;
            }

            public void onFinish() {
                cL.setBackgroundResource(images[11]); //last image
                //stop playing Music
                stopService(new Intent(SplashActivity.this, MusicBackgroundService.class));
                //Open HomeActivity for Userinteraction
                Intent homeIntent = new Intent(SplashActivity.this, HomeActivity.class);
                startActivity(homeIntent);
                finish();
            }
        }.start();
    }

}
