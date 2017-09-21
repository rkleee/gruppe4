/**
 * @author group04
 * @version 1.0
 * HomeActivity, the central Activity of the app
 */
package com.example.l_pba.team04_app01_splashscreendesign;

/**
 * Android Imports
 */

import android.content.Intent;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.gjiazhe.panoramaimageview.GyroscopeObserver;
import com.gjiazhe.panoramaimageview.PanoramaImageView;


/**
 * HomeActivity to go to other Activities
 */
public class HomeActivity extends AppCompatActivity {

    private ConstraintLayout cL;
    private ImageButton settingbutton;
    private ImageButton databutton;
    private Button secretButton;
    private Button mapButton;
    private TextView textView;

    private GyroscopeObserver gyroscopeObserver;

    @Override
    /**
     * @param savedInstanceState  Bundle
     */
    protected void onCreate(Bundle savedInstanceState) {
        /**
         * Android Shit
         */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homeactivity);

        final Animation anim = AnimationUtils.loadAnimation(this,R.anim.animation);

        //Initialize Buttons
        gyroscopeObserver = new GyroscopeObserver();
        gyroscopeObserver.setMaxRotateRadian(Math.PI/9);

        PanoramaImageView panoramaImageView = (PanoramaImageView) findViewById(R.id.panorama_image_view);
        panoramaImageView.setGyroscopeObserver(gyroscopeObserver);


        cL = (ConstraintLayout) findViewById(R.id.constraintLayout);
        settingbutton = (ImageButton) findViewById(R.id.settingButton);
        databutton = (ImageButton) findViewById(R.id.dataButton);
        secretButton = (Button) findViewById(R.id.secretButton);
        textView = (TextView) findViewById(R.id.textViewLogo);
        mapButton = (Button) findViewById(R.id.mapbutton);

        cL.setBackgroundResource(R.drawable.background);

        secretButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                textView.startAnimation(anim);
            }
        });

        settingbutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                settingbutton.startAnimation(anim);
            }
        });

        databutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                databutton.startAnimation(anim);
            }
        });


        // --start the MapActivity--
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent explicitIntent = new Intent(HomeActivity.this,MapActivity.class);
                startActivity(explicitIntent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        gyroscopeObserver.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        gyroscopeObserver.unregister();
    }

}
