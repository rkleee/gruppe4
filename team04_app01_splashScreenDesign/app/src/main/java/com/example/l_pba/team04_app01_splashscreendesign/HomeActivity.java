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

    private Button button1;
    private Button button2;
    private ImageButton button3;

    @Override
    /**
     * @param savedInstanceState  Bundle
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homeactivity);

        button1 = (Button) findViewById(R.id.but1);
        button2 = (Button) findViewById(R.id.but2);
        button3 = (ImageButton) findViewById(R.id.imageButton);

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent explicitIntent = new Intent(HomeActivity.this,MapActivity.class);
                startActivity(explicitIntent);
            }
        });
    }

}
