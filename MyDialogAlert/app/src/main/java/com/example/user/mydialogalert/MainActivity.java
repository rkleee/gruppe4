package com.example.user.mydialogalert;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import static com.example.user.mydialogalert.R.layout.dialog_alert;

public class MainActivity extends AppCompatActivity {


    private EditText mName;
    private CheckBox mRoute;
    private CheckBox mPolygon;
    private Button mSave;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // requestWindowFeature(Window.FEATURE_NO_TITLE);
       Button save = (Button) findViewById(R.id.button);
        save.setOnClickListener(new View.OnClickListener(){
           @Override
            public void onClick(View view) {
               AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
               View mView = getLayoutInflater().inflate(dialog_alert, null);

               mName = (EditText) mView.findViewById(R.id.et2);
                mRoute = (CheckBox) mView.findViewById(R.id.checkBox);
               mPolygon = (CheckBox) mView.findViewById(R.id.checkBox2);
                mSave = (Button) mView.findViewById(R.id.savebtn);
               Button mCancel = (Button) mView.findViewById(R.id.cancelbtn);

               mSave.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                        if(!mName.getText().toString().isEmpty()&& ((mRoute.isChecked())||(mPolygon.isChecked()==true))){

                            Toast.makeText(MainActivity.this, "new element created", Toast.LENGTH_SHORT).show();

                        }else {
                            Toast.makeText(MainActivity.this, "please fill any empty fields", Toast.LENGTH_SHORT).show();
                        }

                   }
               });

              mBuilder.setView(mView);
               AlertDialog dialog = mBuilder.create();
               dialog.show();


           }
        });
    }
}
