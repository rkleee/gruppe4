package com.example.l_pba.team04_app01_splashscreendesign;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DataActivity extends AppCompatActivity {
    private ListView listView;
    public LinkedList<String> selectedItem = new LinkedList<String>();

    SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        //set ScreenOrientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Load data !!!
        preferences = getPreferences(Context.MODE_PRIVATE);
        if (!preferences.getAll().isEmpty()) {
            final String[] caption = preferences.getAll().keySet().toArray(new String[0]);
            Toast.makeText(this, caption[0], Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "else branch", Toast.LENGTH_SHORT).show();
        }
        //final String[] caption = new String[]{"Text 1", "???", "OMG"};

        /**
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, caption); //twoLines


        //Listview
        listView = (ListView) findViewById(R.id.ListView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedItem.add(caption[0]);

                if (selectedItem.size()>0) {
                    Toast.makeText(DataActivity.this, selectedItem.get(0)+" picked", Toast.LENGTH_SHORT).show();
                }

            }
        });
         */
    }
}
