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

public class btheadsetleft extends ActionBarActivity {
    /*
    Defining the global variables
     */
    private static String LEFT_VOLUME_LEVEL;
    private static String HEADSET;
    ImageButton imagebtn1, nextbtn, start, prevbtn, leftvolumelevel;
    ImageButton leftimg1[];
    private MusicIntentReceiver myReceiver; // To check whether the headset is plugged or not
    private int volumeLevel = 0;
    Speedometer speedometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leftbt);
        getSupportActionBar().hide();
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

        for (int i = 0; i < 11; i++) {
            leftimg1[i].setVisibility(View.INVISIBLE);
        }
        leftvolumelevel = (ImageButton) findViewById(R.id.imageButton17);

        leftvolumelevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//after confirming the left ear volume level
                int[] decibelArray = {55, 60, 65, 70, 75, 80, 85, 90, 95, 100, 110};
                LEFT_VOLUME_LEVEL = Integer.toString(decibelArray[volumeLevel]);
                System.out.println(LEFT_VOLUME_LEVEL);
                System.out.println(HEADSET);
                String headsetvalu = Integer.toString(0);
                Intent rightheadset = new Intent(btheadsetleft.this, fullheadsetright.class);
                rightheadset.putExtra("keyName", LEFT_VOLUME_LEVEL);
                startActivity(rightheadset);


            }
        });
        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//to move to the next volume level
                try {

                    if (volumeLevel < 10) {
                        int[] decibels = {55, 60, 65, 70, 75, 80, 85, 90, 95, 100, 110};
                        int volumeOFLeft = decibels[volumeLevel + 1];
                        speedometer.onSpeedChanged(volumeOFLeft);
                        setVolumeLevelsOfLeft(volumeLevel + 1);
                        volumeLevel++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });
        prevbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//to move to the previous volume level
                try {
                    if (volumeLevel > 0) {
                        resetVolumeLevelsOfLeft(volumeLevel);
                        volumeLevel--;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//start the left ear testing process
                nextbtn.setVisibility(View.VISIBLE);
                prevbtn.setVisibility(View.VISIBLE);
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
        int id = item.getItemId();
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

        int[] decibelArray = {55, 60, 65, 70, 75, 80, 85, 90, 95, 100, 110};
        int decibelArrayLength = decibelArray.length;
        System.out.println(decibelArrayLength);
        MediaPlayer mp = MediaPlayer.create(this, R.raw.tone);//mp.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
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

        int[] decibelArray = {55, 60, 65, 70, 75, 80, 85, 90, 95, 100, 110};
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


