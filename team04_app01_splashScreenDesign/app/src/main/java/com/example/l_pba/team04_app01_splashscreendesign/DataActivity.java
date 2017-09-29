/**
 * @author group04
 * @version 1.0
 * DataActivity, the data Activity of the app
 */
package com.example.l_pba.team04_app01_splashscreendesign;

/**
 * Android Imports
 */
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Java util Imports
 */
import java.util.HashMap;
import java.util.LinkedList;

/**
 * DataActivity to display stored Datasets
 */
public class DataActivity extends AppCompatActivity {

    /**Declarate Layout**/
    private ConstraintLayout cL;

    /**Declarate Buttons and View**/
    private Button show;
    private ImageButton circleButton;
    private ListView listView;

    /**Declarate Arrays and Lists**/
    private LinkedList<String> allItems = new LinkedList<>();       //contains all Route names
    private LinkedList<String> selectedItem = new LinkedList<>();   //Subset allItems
    private String[] caption;
    private String[] prefArray;     //raw data

    /**Declarate Shared Preferences**/
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;


    String selectedColor = "#985358";//winered// //color for selected Items
    String longItem;

    @Override
    /**
     * @param savedInstanceState : Bundle
     */
    protected void onCreate(Bundle savedInstanceState) {
        //Android
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        //set Backgroundcolor for DataActivity
        cL = (ConstraintLayout) findViewById(R.id.cL);
        cL.setBackgroundResource(R.drawable.data_background);

        /**Initialize Buttons**/
        show = (Button) findViewById(R.id.showButton);
        circleButton = (ImageButton) findViewById(R.id.circlebtn);

        //Load data from GPSFile
        preferences = getSharedPreferences("GPSFile", Context.MODE_PRIVATE);
        editor = preferences.edit();

        //Get Captions from GPS
        GetCaption();

        /**Initialize ListView and set functionality**/
        final HashMap<Integer, Boolean> clickedMap = new HashMap<>();
        listView = (ListView) findViewById(R.id.ListView);
        listView.setAdapter(new ArrayAdapter<>(this,R.layout.textview, caption));
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

        /**set OnClick to circleButton to open MapActivity**/
        circleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent mapview = new Intent(DataActivity.this,MapActivity.class);
                        mapview.putExtra("Items", new String[]{});
                        startActivity(mapview);
                        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                        finish();
                    }
                },25);
            }
        });

        /**set OnClick to showButton to display routes on MapActivity**/
        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent mapview = new Intent(DataActivity.this,MapActivity.class);
                        mapview.putExtra("Items",selectedItem.toArray(new String[0]));
                        startActivity(mapview);
                        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                        finish();
                    }
                },25);
            }
        });


    }

    /**
     * Function to getCaption from Shared Preferences
     */
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

    /**
     *
     * @param menu : ContextMenu
     * @param v : View
     * @param menuinfo : ContextMenuInfo
     */
    public void onCreateContextMenu(ContextMenu menu, android.view.View v, ContextMenu.ContextMenuInfo menuinfo) {

        super.onCreateContextMenu(menu, v, menuinfo);

        menu.setHeaderTitle("Select the action");
        menu.add(0, v.getId(), 0, "Delete");
        menu.add(0, v.getId(), 0, "Rename");
    }


    /**
     *
     * @param item : MenuItem
     * @return : boolean
     */
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
            listView.setAdapter(new ArrayAdapter<>(DataActivity.this,R.layout.textview, caption));

        } else if (item.getTitle()=="Rename") {

            /**Build AlertDialog to question User-information**/
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(DataActivity.this);
            View mView = getLayoutInflater().inflate(R.layout.activity_rename,null);
            mBuilder.setView(mView);
            final AlertDialog dialog = mBuilder.create();

            /**Initialize EditText and Button**/
            final EditText rename = (EditText) mView.findViewById(R.id.nntV);
            Button savename = (Button) mView.findViewById(R.id.savebtn);

            dialog.show();

            //set onClick functionality to save Userspecified name
            savename.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                     String input = rename.getText().toString();
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
                    listView.setAdapter(new ArrayAdapter<>(DataActivity.this, R.layout.textview, caption));
                    dialog.cancel();
                }
            });
        }
        return true;
    }

    /**
     * onBackPressed to return to HomeActivity
     */
    @Override
    public void onBackPressed() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent backIntent = new Intent(DataActivity.this, HomeActivity.class);
                startActivity(backIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        }, 50);
    }
}
