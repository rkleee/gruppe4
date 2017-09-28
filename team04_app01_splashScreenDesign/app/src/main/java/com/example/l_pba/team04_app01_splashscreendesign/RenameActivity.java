package com.example.l_pba.team04_app01_splashscreendesign;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RenameActivity extends AppCompatActivity {

    Button back;
    Button save;
    String key2;
    EditText name2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rename);

        final String longItem = getIntent().getExtras().getString("longItem", "");

        back = (Button) findViewById(R.id.backbtn);
        save = (Button) findViewById(R.id.savebtn2);
        name2 = (EditText) findViewById(R.id.nntV);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RenameActivity.this,DataActivity.class);

                startActivity(intent);
            }

        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                key2 = name2.getText().toString();
                Intent saveIntent = new Intent(RenameActivity.this, DataActivity.class);
                saveIntent.putExtra("newName", key2);
                saveIntent.putExtra("longItem", longItem);
                startActivity(saveIntent);
            }
        });
}}