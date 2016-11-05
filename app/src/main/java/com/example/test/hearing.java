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
 *         Use to handle the hearing start of the activity.
 * @see android.app.ActionBar
 */
public class hearing extends ActionBarActivity {
    ImageButton imagebtn1;
    SqliteController controller = new SqliteController(this);
    public static int NUMBER_OF_PROFILES;
    private final static Logger LOGGER = Logger.getLogger(hearing.class.getName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hearing);
        getSupportActionBar().hide();
        NUMBER_OF_PROFILES = controller.getNumberOFProfiles();
        imagebtn1 = (ImageButton) findViewById(R.id.imageButton5);
        imagebtn1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(hearing.this, headset.class);
                startActivity(intent);
            }
        });
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
