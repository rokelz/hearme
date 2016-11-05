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
 * Created by USER on 5/10/2016.
 *
 * @author R.K.Opatha (IT13510572)
 *         Use to handle the recording main of the activity.
 *         To handle the recording option, view, edit and share options for recordings.
 * @see android.app.ActionBar
 */
public class recordMain extends ActionBarActivity {
    ImageButton imagebtn1;
    ImageButton imagebtn2;
    ImageButton habutton;
    private final static Logger LOGGER = Logger.getLogger(recordMain.class.getName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_harecordmain);
        getSupportActionBar().hide();
        imagebtn1 = (ImageButton) findViewById(R.id.imageButton26);
        imagebtn1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(recordMain.this, record.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent goBack = new Intent(recordMain.this, hearingmain.class);
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
