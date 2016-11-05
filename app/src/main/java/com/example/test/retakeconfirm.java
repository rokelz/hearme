package com.example.test;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Created by USER on 5/19/2016.
 *
 * @author R.K.Opatha (IT13510572)
 *         Use to handle the profile confirm saving option after the hearing re test is being done.
 * @see android.app.ActionBar
 */
public class retakeconfirm extends ActionBarActivity {
    ImageButton imagebtn1, btnOpenPopup;
    public static int LEFT_VOLUME;
    public static int RIGHT_VOLUME;
    EditText profileName;
    public static String SAVE_PROFILE;
    public String value;
    public String value1;
    public String reprofileName, reprId;
    SqliteController controller = new SqliteController(this);
    private final static Logger LOGGER = Logger.getLogger(retakeconfirm.class.getName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soundprofile);
        getSupportActionBar().hide();
        value = getIntent().getStringExtra("left");
        int leftvol = Integer.parseInt(value);
        value1 = getIntent().getStringExtra("right");
        int rightvol = Integer.parseInt(value1);
        reprofileName = getIntent().getStringExtra("retakeprofname");
        reprId = getIntent().getStringExtra("retakeprofid");
        LEFT_VOLUME = leftvol;
        RIGHT_VOLUME = rightvol;
        btnOpenPopup = (ImageButton) findViewById(R.id.imageButton21);
        btnOpenPopup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                HashMap<String, String> profileDetails = new HashMap<String, String>();
                profileDetails.put("left", value);
                profileDetails.put("right", value1);
                controller.retakeProfile(profileDetails, reprofileName, reprId);
                Intent backToMain = new Intent(retakeconfirm.this, hearingmain.class);
                startActivity(backToMain);

            }
        });


        imagebtn1 = (ImageButton) findViewById(R.id.imageButton37);
        imagebtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    confirmAudibility(LEFT_VOLUME, RIGHT_VOLUME);
                } catch (IOException e) {
                    e.printStackTrace();
                }


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

    /**
     * Use to confirm the sound profile hearing levels by playing the hearing levels again.
     *
     * @param left
     * @param right
     * @throws IOException
     */
    public void confirmAudibility(int left, int right) throws IOException {
        Testhearing testhearing = new Testhearing();
        int[] decibelArray = {55, 60, 65, 70, 75, 80, 85, 90, 95, 100, 110};
        int[] decibelArray1 = {55, 60, 65, 70, 75, 80, 85, 90, 95, 100, 110};
        int decibelArrayLength = decibelArray.length;
        System.out.println(decibelArrayLength);
        MediaPlayer mp = MediaPlayer.create(this, R.raw.tone);

        float rightVolume = 0.0f;
        int maxVolume = 110;
        int currLeftVolume = left;
        int currRightVolume = right;
        float logleft = (float) (Math.log(maxVolume - currLeftVolume) / Math.log(maxVolume));
        float logright = (float) (Math.log(maxVolume - currRightVolume) / Math.log(maxVolume));

        System.out.println("left......................." + logleft + "right...................." + logright);
        mp.setVolume(1 - logright, 1 - logleft);
        mp.start();
    }
}


