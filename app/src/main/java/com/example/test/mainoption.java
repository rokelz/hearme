package com.example.test;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * @author R.K.Opatha (IT13510572)
 *         To handle the options in the mobile application in a horizontal pattern.
 * @see android.app.ActionBar
 */
public class mainoption extends ActionBarActivity {
    ImageButton imagebtn1;
    ImageButton imagebtn2;
    ImageButton habutton, threadViewButton,trainVoice;
    SqliteController controller = new SqliteController(this);
    public static int NUMBER_OF_PROFILES;
    final public double EMA_FILTER = 0.6;
    private final static Logger LOGGER = Logger.getLogger(mainoption.class.getName());
    public MediaRecorder mRecorder = null;
    public double mEMA = 0.0;
    /* constants */
    private static final int POLL_INTERVAL = 300;

    /**
     * running state *
     */
    private boolean mRunning = false;

    /**
     * config state *
     */
    private int mThreshold;

    private PowerManager.WakeLock mWakeLock;

    private Handler mHandler = new Handler();

    /* References to view elements */
    private TextView mStatusView, tv_noice;

    /* sound data source */
    private Detect_noise mSensor;
    ProgressBar bar;
    /**
     * *************** Define runnable thread again and again detect noise ********
     */

    private Runnable mSleepTask = new Runnable() {
        public void run() {
            //Log.i("Noise", "runnable mSleepTask");

            starts();
        }
    };

    // Create runnable thread to Monitor Voice
    private Runnable mPollTask = new Runnable() {
        public void run() {
            double amp = mSensor.getAmplitude();
            //Log.i("Noise", "runnable mPollTask");
            updateDisplay("Monitoring Voice...", amp);

            if ((amp > mThreshold)) {
                callForHelp(amp);
                //Log.i("Noise", "==== onCreate ===");
            }
            // Runnable(mPollTask) will again execute after POLL_INTERVAL
            mHandler.postDelayed(mPollTask, POLL_INTERVAL);
        }
    };
    private Runnable mPollTask2 = new Runnable() {
        public void run() {

        }
    };

    @Override
    public void onResume() {
        super.onResume();
        //Log.i("Noise", "==== onResume ===");

        initializeApplicationConstants();
        if (!mRunning) {
            mRunning = true;
            starts();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        // Log.i("Noise", "==== onStop ===");
        //Stop noise monitoring
        stop();
    }

    private void starts() {
        //Log.i("Noise", "==== start ===");
        mSensor.starts();
        if (!mWakeLock.isHeld()) {
            mWakeLock.acquire();
        }
        //Noise monitoring start
        // Runnable(mPollTask) will execute after POLL_INTERVAL
        mHandler.postDelayed(mPollTask, POLL_INTERVAL);
    }

    private void stop() {
        Log.i("Noise", "==== Stop Noise Monitoring===");
        if (mWakeLock.isHeld()) {
            mWakeLock.release();
        }
        mHandler.removeCallbacks(mSleepTask);
        mHandler.removeCallbacks(mPollTask);
        mSensor.stop();
        bar.setProgress(0);
        updateDisplay("stopped...", 0.0);
        mRunning = false;

    }


    private void initializeApplicationConstants() {
        // Set Noise Threshold
        mThreshold = 8;

    }

    private void updateDisplay(String status, double signalEMA) {
        mStatusView.setText(status);
        //
        bar.setProgress((int) signalEMA);
        Log.d("SOUND", String.valueOf(signalEMA));
        tv_noice.setText(signalEMA + "dB");
    }


    private void callForHelp(double signalEMA) {

        //stop();

        // Show alert when noise thersold crossed
        //Toast.makeText(getApplicationContext(), "Noise Thersold Crossed, do here your stuff.",
        //      Toast.LENGTH_LONG).show();
        Log.d("SONUND", String.valueOf(signalEMA));
        tv_noice.setText(signalEMA + "dB");
    }

    public String HEADSET_TYPE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainoption);
        getSupportActionBar().hide();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "NoiseAlert");
        mStatusView = (TextView) findViewById(R.id.status);
        tv_noice = (TextView) findViewById(R.id.tv_noice);
        bar = (ProgressBar) findViewById(R.id.progressBar1);
        mSensor = new Detect_noise();
        threadViewButton = (ImageButton) findViewById(R.id.imageButton2);
        imagebtn1 = (ImageButton) findViewById(R.id.imageButton28);
        trainVoice = (ImageButton)findViewById(R.id.imageButton);
        trainVoice.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(mainoption.this, Training.class);
                startActivity(intent);
            }
        });
        imagebtn1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(mainoption.this, mainopdown.class);
                startActivity(intent);
            }
        });
        imagebtn2 = (ImageButton) findViewById(R.id.imageButton27);
        imagebtn2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(mainoption.this, mainoption.class);
                startActivity(intent);
            }
        });
        habutton = (ImageButton) findViewById(R.id.imageButton4);

        NUMBER_OF_PROFILES = controller.getNumberOFProfiles();
        if (NUMBER_OF_PROFILES <= 0) {

            habutton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    LayoutInflater layoutInflater = LayoutInflater.from(mainoption.this);
                    View promptView = layoutInflater.inflate(R.layout.welcome, null);

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(mainoption.this);
                    alertDialog.setTitle("HearMe");

                    alertDialog.setIcon(R.drawable.icon);
                    alertDialog.setView(promptView);
                    alertDialog.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // User pressed Cancel button. Write Logic Here
                                    Toast.makeText(mainoption.this, "You clicked on Ok", Toast.LENGTH_SHORT).show();
                                }
                            });

                    alertDialog.show();

                    HEADSET_TYPE = "full";
                    Intent intent = new Intent(mainoption.this, fullheadsetleft.class);
                    intent.putExtra("headsetType", HEADSET_TYPE);
                    startActivity(intent);

                }
            });
        } else {
            habutton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    Intent intent = new Intent(mainoption.this, hearingmain.class);
                    startActivity(intent);
                }
            });
        }
        threadViewButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(mainoption.this, OptionsSubActivity.class);
                startActivity(i);
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

    /*   public void  gotomainopdown(View view){
           Intent intent=new Intent(this, mainopdown.class);
           startActivity(intent);
       }
       public void  gotomainop(){
           Intent intent=new Intent(this, mainoption.class);
           startActivity(intent);
       }*/
    class Detect_noise {
        // This file is used to record voice


        public void starts() {

            if (mRecorder == null) {

                mRecorder = new MediaRecorder();
                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                mRecorder.setOutputFile("/dev/null");

                try {
                    mRecorder.prepare();
                } catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                mRecorder.start();
                mEMA = 0.0;
            }
        }

        public void stop() {
            if (mRecorder != null) {
                mRecorder.stop();
                mRecorder.release();
                mRecorder = null;
            }
        }

        public double getAmplitude() {
            if (mRecorder != null)
                //return  (mRecorder.getMaxAmplitude()/2700.0);
                return 20 * Math.log10(mRecorder.getMaxAmplitude() / 2700.0);
            else
                return 0;

        }

        public double getAmplitudeEMA() {
            double amp = getAmplitude();
            mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
            return mEMA;
        }
    }
}
