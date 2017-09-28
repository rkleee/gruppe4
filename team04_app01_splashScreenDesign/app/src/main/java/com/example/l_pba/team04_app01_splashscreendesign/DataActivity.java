package com.example.l_pba.team04_app01_splashscreendesign;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;



public class DataActivity extends AppCompatActivity {

    private Button delete;
    private Button show;

    private ListView listView;
    private LinkedList<String> allItems = new LinkedList<>();       //contains all Route names
    private LinkedList<String> selectedItem = new LinkedList<>();   //Teilmenge allItems

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private String[] caption;
    private String[] prefArray;     //raw data


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Android
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //Buttons
        delete = (Button) findViewById(R.id.deleteButton);
        show = (Button) findViewById(R.id.showButton);


        //Load data
        preferences = getSharedPreferences("GPSFile", Context.MODE_PRIVATE);
        editor = preferences.edit();

        GetCaption();

        //Listview

        listView = (ListView) findViewById(R.id.ListView);
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, caption));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!selectedItem.contains(allItems.get(position))){
                    selectedItem.add(allItems.get(position));
                    Toast.makeText(DataActivity.this, allItems.get(position)+" picked", Toast.LENGTH_SHORT).show();
                } else {
                    selectedItem.remove(allItems.get(position));
                    Toast.makeText(DataActivity.this, allItems.get(position)+" removed", Toast.LENGTH_SHORT).show();
                }
            }
        });


        //Buttons
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String [] helpselected = selectedItem.toArray(new String[0]);
                for (int i=0; i<prefArray.length; i++){
                    for (int j=0; j<helpselected.length; j++) {
                        if (prefArray[i].replaceAll("[0-9]","").replace("#","").equals(helpselected[j])) {
                            editor.remove(prefArray[i]);
                            break;
                        }
                    }
                }
                editor.commit();

                Toast.makeText(DataActivity.this, "Route deleted", Toast.LENGTH_SHORT).show();

                //update listview
                selectedItem.clear();
                GetCaption();
                listView.setAdapter(new ArrayAdapter<>(DataActivity.this, android.R.layout.simple_list_item_1, caption));
            }
        });


        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DataActivity.this,MapActivity.class);
                intent.putExtra("Items",selectedItem.toArray(new String[0]));
                startActivity(intent);
            }
        });


    }




    private void GetCaption(){
        String[] empty = new String[]{}; //String if preferences.isEmpty
        allItems.clear();

        if (!preferences.getAll().isEmpty()) {
            prefArray = preferences.getAll().keySet().toArray(new String[0]);
            //extract Route names
            for (int i=0; i<prefArray.length; i++){
                String help = prefArray[i].replaceAll("[0-9]", "").replace("#",""); //remove numbers
                if (!allItems.contains(help)){
                    allItems.add(help); //allItems contains all Route names by String
                }
            }
            caption = allItems.toArray(new String[0]);

        } else {
            caption = empty;
            Toast.makeText(this, "no Data found", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onBackPressed() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent backIntent = new Intent(DataActivity.this,HomeActivity.class);
                startActivity(backIntent);
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                finish();
            }
        },50);
    }

}
