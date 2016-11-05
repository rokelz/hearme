package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

/**
 * @author R.K.Opatha (IT13510572)
 *         The starting activity which handles the animation.
 * @see android.app.ActionBar
 */


public class MainActivity extends ActionBarActivity {
    SqliteController controller = new SqliteController(this);
    public static int NUMBER_OF_PROFILES;
    private final static Logger LOGGER = Logger.getLogger(MainActivity.class.getName());
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        final ImageView iv = (ImageView) findViewById(R.id.imageView);
        final Animation an = AnimationUtils.loadAnimation(getBaseContext(), R.anim.abc_fade_in);
        final Animation an2 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.abc_fade_out);
        final Intent intent = new Intent(this, mainoption.class);
        image = (ImageView) findViewById(R.id.imageView);

        iv.startAnimation(an);


        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {

                startActivity(intent);
            }
        }, 3000);


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
