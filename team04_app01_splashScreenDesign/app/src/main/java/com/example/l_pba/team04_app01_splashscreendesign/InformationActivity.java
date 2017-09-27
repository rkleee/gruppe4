/**
 * @author group04
 * @version 1.0
 * InformationActivity, the informative Activity of the app
 */
package com.example.l_pba.team04_app01_splashscreendesign;

/**
 * Necessary Imports
 */
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * InformationActivity to display Appinformation
 */
public class InformationActivity extends AppCompatActivity {

    private ConstraintLayout cL;
    private ImageButton infoBtn;
    private Button secretButton;
    private TextView appversion;
    private TextView informationtext;

    private int informationColor = Color.parseColor("#E6fffacd"); //BackgroundColor

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        //set ScreenOrientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //rotate Animation of the Information-Button
        final Animation anim = AnimationUtils.loadAnimation(this,R.anim.animation);

        //set Layout Background to Coloredbackground
        cL = (ConstraintLayout) findViewById(R.id.constraintLayout);
        cL.setBackgroundColor(informationColor);

        /**initialize Buttons & TextViews **/
        infoBtn = (ImageButton) findViewById(R.id.infoButton); //Information-Button

        secretButton = (Button) findViewById(R.id.secretButton); //Secret Button for showing the Creators
        secretButton.getBackground().setColorFilter(0x00000000, PorterDuff.Mode.MULTIPLY); //set SecretButton invisible

        informationtext = (TextView) findViewById(R.id.informationtext); //informationtext

        appversion = (TextView) findViewById(R.id.appversion); //appversion
        appversion.setVisibility(View.INVISIBLE); //set appversion invisible


        /**Secret Button changes information-text to Creator-displaying**/
        secretButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(informationtext.getTextSize() == 54.0) {
                    informationtext.setText(R.string.informationtext);
                    informationtext.setTextSize(14);
                }else{
                    informationtext.setText(R.string.creators_text);
                    informationtext.setTextSize(18);
                }
            }
        });


        /**Information-Button-Animation by OnClick**/
        infoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(anim);

                //if appversion ist Visible
                if(appversion.getVisibility() == View.VISIBLE) {
                    appversion.setVisibility(View.INVISIBLE);

                //if appversion ist Invisible
                }else {
                    appversion.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent backIntent = new Intent(InformationActivity.this,HomeActivity.class);
                startActivity(backIntent);
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                finish();
            }
        },50);
    }
}

