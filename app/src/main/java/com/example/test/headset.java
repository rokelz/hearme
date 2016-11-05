package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import java.util.logging.Logger;

/**
 * Created by USER on 3/12/2016.
 *
 * @author R.K.Opatha (IT13510572)
 *         Use to handle the headset selection of the activity.
 * @see android.app.ActionBar
 */
public class headset extends ActionBarActivity {
    ImageButton fheadset;
    ImageButton lbluetooth;
    ImageButton rbluetooth;
    public String HEADSET_TYPE;
    private final static Logger LOGGER = Logger.getLogger(headset.class.getName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_headset);
        getSupportActionBar().hide();
        fheadset = (ImageButton) findViewById(R.id.imageButton18);
        fheadset.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                HEADSET_TYPE = "full";
                Intent intent = new Intent(headset.this, fullheadsetleft.class);
                intent.putExtra("headsetType", HEADSET_TYPE);
                startActivity(intent);
            }
        });
        lbluetooth = (ImageButton) findViewById(R.id.imageButton19);
        lbluetooth.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                HEADSET_TYPE = "lb";
                Intent intent = new Intent(headset.this, fullheadsetleft.class);
                intent.putExtra("headsetType", HEADSET_TYPE);
                startActivity(intent);
            }
        });
        rbluetooth = (ImageButton) findViewById(R.id.imageButton20);
        rbluetooth.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                HEADSET_TYPE = "rb";
                Intent intent = new Intent(headset.this, fullheadsetleft.class);
                intent.putExtra("headsetType", HEADSET_TYPE);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent goBack = new Intent(headset.this, hearingmain.class);
        startActivity(goBack);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
