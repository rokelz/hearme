package com.example.test;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import java.io.IOException;

/**
 * Created by USER on 3/12/2016.
 *
 * @author R.K.Opatha (IT13510572)
 *         Use to handle the left headset of a bluetooth headset device
 *         for testing purpose.
 * @see android.app.ActionBar
 */
public class fullheadsetleft extends ActionBarActivity {
    //private final static Logger LOGGER = Logger.getLogger(fullheadsetleft.class.getName());
    private static String LEFT_VOLUME_LEVEL;
    private static String HEADSET;
    ImageButton imagebtn1, nextbtn, start, prevbtn, leftvolumelevel;
    ImageButton leftimg1[];
    private MusicIntentReceiver myReceiver;
    private int volumeLevel = 0;
    Speedometer speedometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leftheadset);
        getSupportActionBar().hide();
        HEADSET = getIntent().getStringExtra("headsetType");
        myReceiver = new MusicIntentReceiver();
        start = (ImageButton) findViewById(R.id.imageButton38);
        nextbtn = (ImageButton) findViewById(R.id.imageButton35);
        prevbtn = (ImageButton) findViewById(R.id.imageButton36);
        speedometer = (Speedometer) findViewById(R.id.Speedometer);
        leftimg1 = new ImageButton[]{(ImageButton) findViewById(R.id.imageButton6),
                (ImageButton) findViewById(R.id.imageButton7),
                (ImageButton) findViewById(R.id.imageButton8),
                (ImageButton) findViewById(R.id.imageButton9),
                (ImageButton) findViewById(R.id.imageButton10),
                (ImageButton) findViewById(R.id.imageButton11),
                (ImageButton) findViewById(R.id.imageButton12),
                (ImageButton) findViewById(R.id.imageButton13),
                (ImageButton) findViewById(R.id.imageButton14),
                (ImageButton) findViewById(R.id.imageButton15),
                (ImageButton) findViewById(R.id.imageButton16)};
        nextbtn.setVisibility(View.INVISIBLE);
        prevbtn.setVisibility(View.INVISIBLE);
        //leftvolumelevel.setVisibility(View.INVISIBLE);
        for (int i = 0; i < 11; i++) {
            leftimg1[i].setVisibility(View.INVISIBLE);
        }
        leftvolumelevel = (ImageButton) findViewById(R.id.imageButton17);

        leftvolumelevel.setOnClickListener(new View.OnClickListener() {//after confirming the left ear volume level
            @Override
            public void onClick(View v) {//after confirming the left ear volume level
                int[] decibelArray = {10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110};
                LEFT_VOLUME_LEVEL = Integer.toString(decibelArray[volumeLevel]);
                System.out.println(LEFT_VOLUME_LEVEL);

                Intent rightheadset = new Intent(fullheadsetleft.this, fullheadsetright.class);
                rightheadset.putExtra("keyName", LEFT_VOLUME_LEVEL);
                startActivity(rightheadset);


            }
        });
        nextbtn.setOnClickListener(new View.OnClickListener() {//to move to the next volume level
            @Override
            public void onClick(View v) {//to move to the next volume level
                try {
                    if (volumeLevel < 11) {
                        int[] decibels = {10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110};
                        int volumeOFLeft = decibels[volumeLevel + 1];
                        speedometer.onSpeedChanged(volumeLevel);
                        setVolumeLevelsOfLeft(volumeLevel + 1);
                        volumeLevel++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });
        prevbtn.setOnClickListener(new View.OnClickListener() {//to move to the previous volume level
            @Override
            public void onClick(View v) {//to move to the previous volume level
                try {
                    if (volumeLevel > 0) {
                        resetVolumeLevelsOfLeft(volumeLevel);
                        volumeLevel--;
                        int[] decibels = {10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110};
                        int volumeOFLeft = decibels[volumeLevel];
                        speedometer.onSpeedChanged(volumeLevel);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });
        start.setOnClickListener(new View.OnClickListener() {//start the left ear testing process
            @Override
            public void onClick(View v) {//start the left ear testing process
                // leftvolumelevel.setVisibility(View.VISIBLE);

                nextbtn.setVisibility(View.VISIBLE);
                prevbtn.setVisibility(View.VISIBLE);
                speedometer.onSpeedChanged(0);
                for (int i = 0; i < 11; i++) {
                    leftimg1[i].setVisibility(View.VISIBLE);
                }

                try {
                    setVolumeLevelsOfLeft(0);
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
        // if(am.isWiredHeadsetOn()) {
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    final Handler mHandler = new Handler();


    /**
     * Use to handle the volume setting function for the left ear.
     * Use to convert the volume scale into a linear scalar which co-operate
     * to decibel limits from 55 to 110 with an increment of 5dB.
     *
     * @param level
     * @throws IOException
     */
    public void setVolumeLevelsOfLeft(int level) throws IOException {
        Testhearing testhearing = new Testhearing();

        int[] decibelArray = {10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110};
        int decibelArrayLength = decibelArray.length;
        System.out.println(decibelArrayLength);
        MediaPlayer mp = MediaPlayer.create(this, R.raw.tone);
        //mp.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
        if (level < decibelArrayLength && level >= 0) {
            leftimg1[level].setColorFilter(Color.BLUE);
            float leftVolume = testhearing.setthevolumeofleft(decibelArray[level]);
            float rightVolume = 0.0f;
            int maxVolume = 110;
            int currVolume = decibelArray[level];
            float log1 = (float) (Math.log(maxVolume - currVolume) / Math.log(maxVolume));
            System.out.println(log1);
            System.out.println(leftVolume);
            mp.setVolume(0, 1 - log1);
            mp.start();
        }
        //mp.stop();
    }

    /**
     * Use to handle the volume resetting function for the left ear.
     * Use to convert the volume scale into a linear scalar which co-operate
     * to decibel limits from 55 to 110 with an increment of 5dB.
     *
     * @param level
     * @throws IOException
     */
    public void resetVolumeLevelsOfLeft(int level) throws IOException {

        Testhearing testhearing = new Testhearing();

        int[] decibelArray = {10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110};
        int decibelArrayLength = decibelArray.length;
        System.out.println(decibelArrayLength);
        MediaPlayer mp1 = MediaPlayer.create(this, R.raw.tone);
        if (level < decibelArrayLength) {
            leftimg1[level].setColorFilter(Color.BLACK);
            float leftVolume = testhearing.setthevolumeofleft(decibelArray[level]);
            float rightVolume = 0.0f;
            int maxVolume = 110;
            int currVolume = decibelArray[level];
            float log1 = (float) (Math.log(maxVolume - currVolume) / Math.log(maxVolume));
            System.out.println(log1);
            System.out.println(leftVolume);
            mp1.setVolume(0, 1 - log1);
            mp1.start();
        }
        //mp.stop();
    }


    @Override
    public void onResume() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(myReceiver, filter);
        super.onResume();
    }

    @Override
    public void onPause() {
        unregisterReceiver(myReceiver);
        super.onPause();
    }

}

