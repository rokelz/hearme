package com.example.test;

import android.content.Intent;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.h6ah4i.android.media.IBasicMediaPlayer;
import com.h6ah4i.android.media.IMediaPlayerFactory;
import com.h6ah4i.android.media.opensl.OpenSLMediaPlayerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Created by USER on 5/10/2016.
 * To create the recording mechanisms to record the lost moments.
 *
 * @author R.K.Opatha (IT13510572)
 * @see android.app.ActionBar
 */

public class record extends ActionBarActivity {
    Button start, stop;
    private MediaRecorder myAudioRecorder;
    private String outputFile = null;
    SqliteController controller = new SqliteController(this);
    String Datetime;
    AudioRecord n;
    private final static Logger LOGGER = Logger.getLogger(record.class.getName());
    Chronometer mChronometer;
    Boolean mChronoPaused = false;
    long mElapsedTime = 0;
    ArrayList<String> mSplitTimes = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        getSupportActionBar().hide();
        start = (Button) findViewById(R.id.imageButton42);
        stop = (Button) findViewById(R.id.imageButton41);
        //-----------------------------
        mChronometer = (Chronometer) findViewById(R.id.chronometer);
        // create
        IMediaPlayerFactory factory = new OpenSLMediaPlayerFactory(getApplicationContext());
        IBasicMediaPlayer player = factory.createMediaPlayer();

        stop.setEnabled(false);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("ddMMMyyyyhhmmss");
        Datetime = dateformat.format(c.getTime());
        System.out.println(Datetime);

        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/HearMe" + Datetime + "recording.3gp";

        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setOutputFile(outputFile);


        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);

        myAudioRecorder.setOutputFile(outputFile);


        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    System.out.println("b4 prepare");
                    myAudioRecorder.prepare();
                    System.out.println("b4 strt");
                    myAudioRecorder.start();
                    System.out.println("aftr strt");

                    //-----------------------------------------------
                    if (mChronoPaused) {
                        Log.v("StopWatch", "start-chrono was paused");
                        mChronometer.setBase(SystemClock.elapsedRealtime()
                                - mElapsedTime);
                        getWindow().addFlags(
                                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    } else if (!mChronometer.isStarted()) {
                    } else if (!mChronoPaused) {
                    }
                    mChronometer.start();
                    mChronoPaused = false;
                    //-----------------------------------------------
                } catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


                stop.setEnabled(true);

                Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("b4 stop");
                myAudioRecorder.stop();
                System.out.println("after stop");
                myAudioRecorder.release();
                System.out.println("after release");
                myAudioRecorder = null;
                stop.setEnabled(false);


                HashMap<String, String> profileDetails = new HashMap<String, String>();
                profileDetails.put("recordingName", Datetime);
                profileDetails.put("path", outputFile);
                Log.i("info", "Data send to controller");
                controller.insertrecording(profileDetails);
                stop.setEnabled(false);
                //-----------------------------------------------
                mChronometer.stop();
                mChronometer.setBase(SystemClock.elapsedRealtime());
                mSplitTimes = new ArrayList<String>();

                mChronoPaused = false;
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                //-----------------------------------------------
                Toast.makeText(getApplicationContext(), "Audio recorded successfully", Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), "Audio Saved successfully", Toast.LENGTH_LONG).show();
                Intent intents = new Intent(record.this, viewrecords.class);
                startActivity(intents);
            }
        });


    }

    @Override
    public void onBackPressed() {
        Intent goBack = new Intent(record.this, viewrecords.class);
        startActivity(goBack);
    }

    public void initiateRecording() {

        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss aa");
        Datetime = dateformat.format(c.getTime());
        System.out.println(Datetime);

        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + Datetime + ".3gp";

        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setOutputFile(outputFile);
    }

}



