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
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Gjiazhe Imports for PanoramaImage at the Start-/MapButton
 */
import com.gjiazhe.panoramaimageview.GyroscopeObserver;
import com.gjiazhe.panoramaimageview.PanoramaImageView;

/**
 * Hitomi Imports for CircleMenu
 */
import com.hitomi.cmlibrary.CircleMenu;
import com.hitomi.cmlibrary.OnMenuSelectedListener;


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

    private GyroscopeObserver gyroscopeObserver;// for the panoramaImage

    //The buttons of the circle menu
    String circleArray[] = {"Settings","Data","Information"};

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
                Intent explicitIntent = new Intent(HomeActivity.this,MapActivity.class);
                startActivity(explicitIntent);
            }
        });

        // --circleMenu--
        circleMenu = (CircleMenu) findViewById(R.id.circleMenu);

        circleMenu.setMainMenu(circleMenuColor,R.drawable.ic_addbtn,R.drawable.ic_clear)
                .addSubMenu(settingsColor,R.drawable.ic_settings03)
                .addSubMenu(dataColor,R.drawable.ic_data03)
                .addSubMenu(informationColor,R.drawable.ic_information)
                .setOnMenuSelectedListener(new OnMenuSelectedListener() {

                    @Override
                    public void onMenuSelected(int index) {
                        //welcome.setVisibility(View.INVISIBLE);
                        switch(index) {
                            case 0:
                                new CountDownTimer(900, 300) {
                                    public void onTick(long millisUntilFinished) {
                                        //--
                                    }
                                    public void onFinish() {
                                        Intent setting = new Intent(HomeActivity.this,SettingsActivity.class);
                                        startActivity(setting);
                                    }
                                }.start();
                                break;
                            case 1:
                                new CountDownTimer(900, 300) {
                                    public void onTick(long millisUntilFinished) {
                                        //--
                                    }
                                    public void onFinish() {
                                        Intent data = new Intent(HomeActivity.this,DataActivity.class);
                                        startActivity(data);
                                    }
                                }.start();
                                break;
                            case 2:
                                new CountDownTimer(900, 300) {
                                    public void onTick(long millisUntilFinished) {
                                        //--
                                    }
                                    public void onFinish() {
                                        Intent info = new Intent(HomeActivity.this,InformationActivity.class);
                                        startActivity(info);
                                    }
                                }.start();
                                break;
                        }
                        Toast.makeText(HomeActivity.this, "You selected " + circleArray[index], Toast.LENGTH_SHORT).show();

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
