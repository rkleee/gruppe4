package com.example.l_pba.team04_app01_splashscreendesign;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;



public class DataActivity extends AppCompatActivity {

    private Button show;

    private ListView listView;
    private LinkedList<String> allItems = new LinkedList<>();       //contains all Route names
    private LinkedList<String> selectedItem = new LinkedList<>();   //Teilmenge allItems

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private String[] caption;
    private String[] prefArray;     //raw data

    private ConstraintLayout cL;

    String selectedColor = "#ffff0000"; //red

    String longItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Android
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        //set Backgroundcolor for DataActivity
        cL = (ConstraintLayout) findViewById(R.id.cL);
        cL.setBackgroundResource(R.drawable.data_background);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Buttons
        show = (Button) findViewById(R.id.showButton);

        //Load data
        preferences = getSharedPreferences("GPSFile", Context.MODE_PRIVATE);
        editor = preferences.edit();

        GetCaption();

        //Listview
        final HashMap<Integer, Boolean> clickedMap = new HashMap<>();
        listView = (ListView) findViewById(R.id.ListView);
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, caption));
        registerForContextMenu(listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Selected Color
                if(!clickedMap.containsKey(position)) {
                    clickedMap.put(position, true); //insert new input
                } else { //change settings
                    if (clickedMap.get(position)) {
                        clickedMap.put(position, false);
                    } else {
                        clickedMap.put(position, true);
                    }
                }
                if (clickedMap.get(position)) parent.getChildAt(position).setBackgroundColor(Color.parseColor(selectedColor)); //set selected color
                else parent.getChildAt(position).setBackgroundColor(Color.parseColor("#00000000")); //set transparent default color

                //functionality
                if (!selectedItem.contains(allItems.get(position))){
                    selectedItem.add(allItems.get(position));
                    Toast.makeText(DataActivity.this, allItems.get(position)+" picked", Toast.LENGTH_SHORT).show();
                } else {
                    selectedItem.remove(allItems.get(position));
                    Toast.makeText(DataActivity.this, allItems.get(position)+" removed", Toast.LENGTH_SHORT).show();
                }
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

        //rename
        if(getIntent().hasExtra("newName") == true) {
            String input = getIntent().getExtras().getString("newName", "");
            longItem = getIntent().getExtras().getString("longItem", "");
            for (int i=0; i<prefArray.length; i++){
                if (prefArray[i].replaceAll("[0-9]","").replace("#","").equals(longItem)) {
                    String extra = prefArray[i].replaceAll("[^\\d.]", "");
                    String key = input + extra;
                    String copy = preferences.getString(prefArray[i], "");
                    editor.remove(prefArray[i]);
                    editor.putString(key, copy);
                }
            }

            editor.commit();

            //update listview
            GetCaption();
            listView.setAdapter(new ArrayAdapter<>(DataActivity.this, android.R.layout.simple_list_item_1, caption));
        }

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
            Toast.makeText(this, R.string.no_data, Toast.LENGTH_SHORT).show();
        }
    }

    public void onCreateContextMenu(ContextMenu menu, android.view.View v, ContextMenu.ContextMenuInfo menuinfo) {

        super.onCreateContextMenu(menu, v, menuinfo);

        menu.setHeaderTitle("Select the action");
        menu.add(0, v.getId(), 0, "Delete");
        menu.add(0, v.getId(), 0, "Rename");
    }


    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int listPosition = info.position;
        longItem = allItems.get(listPosition);

        if (item.getTitle()=="Delete") {
            for (int i=0; i<prefArray.length; i++){
                if (prefArray[i].replaceAll("[0-9]","").replace("#","").equals(longItem)) {
                    editor.remove(prefArray[i]);
                }
            }

            editor.commit();

            Toast.makeText(DataActivity.this, "Route deleted", Toast.LENGTH_SHORT).show();

            //update listview
            GetCaption();
            listView.setAdapter(new ArrayAdapter<>(DataActivity.this, android.R.layout.simple_list_item_1, caption));

        } else if (item.getTitle()=="Rename") {

            Intent ren = new Intent(DataActivity.this,RenameActivity.class);
            ren.putExtra("longItem", longItem);
            startActivity(ren);

        }
        return true;
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
