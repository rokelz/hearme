package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Created by USER on 5/10/2016.
 *
 * @author R.K.Opatha (IT13510572)
 *         Use to view the recorded recordings in a grid view.
 * @see ActionBarActivity
 */
public class viewrecords extends ActionBarActivity {
    GridView gridView;
    Button imagebtn1;
    ArrayList<HashMap<String, String>> gridArray = new ArrayList<HashMap<String, String>>();
    CustomGridViewAdapter customGridAdapter;
    SqliteController controller = new SqliteController(this);
    public EditText input;
    public String strArr[], pathArr[], idArr[];
    private final static Logger LOGGER = Logger.getLogger(viewrecords.class.getName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewrecords);
        gridView = (GridView) findViewById(R.id.gridView1);
        getSupportActionBar().hide();
        gridArray = controller.getAllRecordings();
        imagebtn1 = (Button) findViewById(R.id.button2);
        imagebtn1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(viewrecords.this, record.class);
                startActivity(intent);
            }
        });
        System.out.println("*****************************************");
        System.out.println(gridArray);
        int i = 0;
        int arrleng = gridArray.size();
        System.out.println(arrleng);
        strArr = new String[gridArray.size()];
        pathArr = new String[gridArray.size()];
        idArr = new String[gridArray.size()];
        for (i = 0; i < gridArray.size(); i++) {
            strArr[i] = gridArray.get(i).get("recordings").toString();
            pathArr[i] = gridArray.get(i).get("path").toString();
            idArr[i] = gridArray.get(i).get("id").toString();
            System.out.println(strArr[i]);
            System.out.println(pathArr[i]);
            System.out.println(idArr[i]);
        }


        customGridAdapter = new CustomGridViewAdapter(this, strArr, idArr, pathArr);
        gridView.setAdapter(customGridAdapter);
        registerForContextMenu(gridView);


    }

    @Override
    public void onBackPressed() {
        Intent goBack = new Intent(viewrecords.this, hearingmain.class);
        startActivity(goBack);
    }

    /**
     * Use to create a menu option once a recording is long pressed.
     *
     * @param menu
     * @param v
     * @param menuInfo
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle("----HearMe Menu----");
        menu.setHeaderIcon(R.drawable.icon);
        menu.add(0, v.getId(), 0, "Edit");
        menu.add(0, v.getId(), 0, "Delete");
        menu.add(0, v.getId(), 0, "Share");


    }

    String newProfileName;

    /**
     * Use to fire the actions when the necessary menu item is being selected.
     *
     * @param item
     * @return
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle() == "Edit") {
            customGridAdapter.getItemAction("Edit");


        } else if (item.getTitle() == "Delete") {
            customGridAdapter.getItemAction("Delete");

        } else if (item.getTitle() == "Share") {
            customGridAdapter.getItemAction("Share");
        } else {
            return false;
        }

        return true;
    }
}
