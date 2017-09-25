package com.example.l_pba.team04_app01_splashscreendesign;


/**
 * Android Imports
 */

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.gjiazhe.panoramaimageview.GyroscopeObserver;
import com.gjiazhe.panoramaimageview.PanoramaImageView;
import com.hitomi.cmlibrary.CircleMenu;
import com.hitomi.cmlibrary.OnMenuSelectedListener;

/**
 * Gjiazhe Imports for PanoramaImage at the Start-/MapButton
 * <p>
 * Hitomi Imports for CircleMenu
 */
/**
 * Hitomi Imports for CircleMenu
 */


/**
 * HomeActivity to go to other Activities
 */
public class HomeActivity extends AppCompatActivity {

    private ConstraintLayout cL; //only for background

    private Button secretButton; //for the easteregg
    private Button mapButton; //to start the MapActivity
    private TextView logo; //the spinning logo
    private TextView welcome; //welcome
    private CircleMenu circleMenu; //circleMenu

    private int circleMenuColor = Color.WHITE;
    private int settingsColor = Color.parseColor("#ffc1e3ff"); //blue
    private int dataColor = Color.parseColor("#ffffa0a0"); //red
    private int informationColor = Color.parseColor("#ffe8ffda"); //green
    private int statisticColor = Color.parseColor("#eeeeee"); //grey

    private GyroscopeObserver gyroscopeObserver;// for the panoramaImage

    //The buttons of the circle menu
    String circleArray[] = {"Settings","Data","Information","Statistic"};

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


        //rotate Animation of the easteregg
        final Animation anim = AnimationUtils.loadAnimation(this,R.anim.animation);

        //for the panoramaImage behind the Start-/MapButton
        gyroscopeObserver = new GyroscopeObserver();
        gyroscopeObserver.setMaxRotateRadian(Math.PI/9);
        PanoramaImageView panoramaImageView = (PanoramaImageView) findViewById(R.id.panorama_image_view);
        panoramaImageView.setGyroscopeObserver(gyroscopeObserver);

        //Initialize
        secretButton = (Button) findViewById(R.id.secretButton);
        secretButton.getBackground().setColorFilter(0x00000000, PorterDuff.Mode.MULTIPLY);
        logo = (TextView) findViewById(R.id.textViewLogo);
        mapButton = (Button) findViewById(R.id.mapbutton);
        welcome = (TextView) findViewById(R.id.textViewWelcome);

        //set the Background
        cL = (ConstraintLayout) findViewById(R.id.constraintLayout);
        cL.setBackgroundResource(R.drawable.background);


        //set Screen Orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        //SecretButton, which will rotate the logo
        secretButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                logo.startAnimation(anim);
            }
        });


        // --start the MapActivity--
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent mapview = new Intent(HomeActivity.this,MapActivity.class);
                        startActivity(mapview);
                        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                        finish();
                    }
                },50);
            }
        });

        /**Start new Activities from the Circle-Menu**/
        circleMenu = (CircleMenu) findViewById(R.id.circleMenu);
        circleMenu.setMainMenu(circleMenuColor,R.drawable.ic_addbtn,R.drawable.ic_clear)
                .addSubMenu(settingsColor,R.drawable.ic_settings03)
                .addSubMenu(dataColor,R.drawable.ic_data03)
                .addSubMenu(informationColor,R.drawable.ic_information)
                .addSubMenu(statisticColor,R.drawable.ic_statistic)
                .setOnMenuSelectedListener(new OnMenuSelectedListener() {

                    @Override
                    public void onMenuSelected(int index) {
                        switch(index) {
                            case 0:
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent setting = new Intent(HomeActivity.this,SettingsActivity.class);
                                        startActivity(setting);
                                        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                                        finish();
                                    }
                                },500);
                                break;
                            case 1:
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent data = new Intent(HomeActivity.this,DataActivity.class);
                                        startActivity(data);
                                        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                                        finish();
                                    }
                                },500);
                                break;
                            case 2:
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent fade = new Intent(HomeActivity.this, InformationActivity.class);
                                        startActivity(fade);
                                        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                                        finish();
                                    }
                                },500);
                                break;
                            case 3:
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent fade = new Intent(HomeActivity.this, StatisticActivity.class);
                                        startActivity(fade);
                                        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                                        finish();
                                    }
                                },500);
                                break;
                        }
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
