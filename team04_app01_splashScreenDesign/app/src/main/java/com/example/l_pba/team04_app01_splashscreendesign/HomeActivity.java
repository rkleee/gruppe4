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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

/**
 * HomeActivity to go to other Activities
 */
public class HomeActivity extends AppCompatActivity {

    private ImageButton dataButton, settingButton, mapButton;

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

        //Initialize Buttons
        dataButton = (ImageButton) findViewById(R.id.dataButton);
        settingButton = (ImageButton) findViewById(R.id.settingButton);
        mapButton = (ImageButton) findViewById(R.id.mapButton);

        //start the MapActivity
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent explicitIntent = new Intent(HomeActivity.this,MapActivity.class);
                startActivity(explicitIntent);
            }
        });


    }

}
