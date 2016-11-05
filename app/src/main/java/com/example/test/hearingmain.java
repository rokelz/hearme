package com.example.test;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.NoiseSuppressor;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import java.util.logging.Logger;

/**
 * Created by USER on 5/10/2016.
 *
 * @author R.K.Opatha (IT13510572)
 *         Use to handle the hearing main of the activity.
 *         To handle the real time voice in and out option, profile option,
 *         equalizer and record option.
 * @see android.app.ActionBar
 */
public class hearingmain extends ActionBarActivity {

    private final static Logger LOGGER = Logger.getLogger(hearingmain.class.getName());
    Button records, home, equalizer, savedprofiles, freq;
    //com.example.test.RotaryKnobView s;
    Switch realTime, noiseReduction;
    private static short[] buffer;
    // private MediaRecorder myAudioRecorder;
    private String outputFile = null;
    boolean isRecording = false;
    AudioManager am = null;
    AudioRecord record = null;
    AudioTrack track = null;
    SeekBar seekbar;
    //private MediaRecorder recorder ;
    private PowerManager.WakeLock mWakeLock;
    NoiseSuppressor ns;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hearingmain);
        getSupportActionBar().hide();
        records = (Button) findViewById(R.id.button2);
        home = (Button) findViewById(R.id.button5);

        savedprofiles = (Button) findViewById(R.id.button7);
        freq = (Button) findViewById(R.id.button6);
        realTime = (Switch) findViewById(R.id.switch2);
        noiseReduction = (Switch) findViewById(R.id.switch1);
        //recorder = null;
//        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //  s = (com.example.test.RotaryKnobView) findViewById(R.id.jogView);

        // Used to record voice
        initRecordAndTrack();

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        am.setSpeakerphoneOn(false);


        (new Thread() {
            @Override
            public void run() {
                recordAndPlay();

            }
        }).start();

        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "NoiseAlert");

        seekbar = (SeekBar) findViewById(R.id.seekBar1);
        initControls();

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //add here your implementation
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //add here your implementation
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);

            }
        });
        realTime.setChecked(false);
        realTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    Toast.makeText(getApplicationContext(), "Real Time Hearing Aid Ability is ON", Toast.LENGTH_SHORT).show();
                    noiseReduction.setChecked(true);
                    if (am.isWiredHeadsetOn()) {
                        if (!isRecording) {

                            System.out.println("Clicked start");
                            startRecordAndPlay();
                            System.out.println("a Clicked start");

                        }
                    } else {
                        LayoutInflater layoutInflater = LayoutInflater.from(hearingmain.this);

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(hearingmain.this);
                        alertDialog.setTitle("-----HearMe-----");
                        alertDialog.setMessage("Please plug your headset inoder to continue...");
                        alertDialog.setIcon(R.drawable.icon);
                        alertDialog.setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        Toast.makeText(hearingmain.this, "You clicked on Yes", Toast.LENGTH_SHORT).show();
                                        noiseReduction.setChecked(false);
                                        realTime.setChecked(false);
                                    }
                                });

                        alertDialog.show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Real Time Hearing Aid Ability is OFF", Toast.LENGTH_SHORT).show();
                    noiseReduction.setChecked(false);
                    if (isRecording) {
                        stopRecordAndPlay();
                    }
                }

            }
        });
        noiseReduction.setChecked(false);
        noiseReduction.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    Toast.makeText(getApplicationContext(), "Noise Reduction Hearing Aid Ability is ON", Toast.LENGTH_SHORT).show();

                    ns.setEnabled(true);
                } else {
                    Toast.makeText(getApplicationContext(), "Noise Reduction Hearing Aid Ability is OFF", Toast.LENGTH_SHORT).show();
                    ns.setEnabled(false);
                }

            }
        });
        home.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(hearingmain.this, mainoption.class);
                startActivity(intent);
            }
        });
        freq.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(hearingmain.this, realTime.class);
                startActivity(intent);
            }
        });
        records.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(hearingmain.this, viewrecords.class);
                startActivity(intent);
            }
        });
        savedprofiles.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(hearingmain.this, savedProfiles.class);
                startActivity(intent);
            }
        });


    }

    /**
     * Setting the basic control values to options.
     */
    private void initControls() {
        try {
            am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            seekbar.setMax(am
                    .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            seekbar.setProgress(am
                    .getStreamVolume(AudioManager.STREAM_MUSIC));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Use to control the volume increment and decrement visualizing the
     * progress on customized seeking bar.
     * {@inheritDoc}.
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                LOGGER.info("Clicked volume up button");
                am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                //Raise the Volume Bar on the Screen
                seekbar.setProgress(am.getStreamVolume(AudioManager.STREAM_MUSIC)
                );
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                LOGGER.info("Clicked volume down button");
                //Adjust the Volume
                am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                //Lower the VOlume Bar on the Screen
                seekbar.setProgress(am.getStreamVolume(AudioManager.STREAM_MUSIC)
                );
                return true;
            default:
                return false;
        }
    }

    /**
     * Initialize the audiorecord instance, AcousticEchoCanceler instance, AudioTrack instance and noise suppressor instance
     * respectively.
     */
    private void initRecordAndTrack() {
        int min = AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        record = new AudioRecord(MediaRecorder.AudioSource.VOICE_COMMUNICATION, 8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,
                min);
        ns = NoiseSuppressor.create(record.getAudioSessionId());
        if (AcousticEchoCanceler.isAvailable()) {
            AcousticEchoCanceler echoCancler = AcousticEchoCanceler.create(record.getAudioSessionId());
            echoCancler.setEnabled(true);
        }
        int maxJitter = AudioTrack.getMinBufferSize(8000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
        track = new AudioTrack(AudioManager.MODE_IN_COMMUNICATION, 8000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, maxJitter,
                AudioTrack.MODE_STREAM);
    }

    /**
     * Use to record the real time voice and store them in a track instance.
     */
    private void recordAndPlay() {
        short[] lin = new short[1024];
        int num = 0;
        am.setMode(AudioManager.MODE_IN_COMMUNICATION);
        while (true) {
            if (isRecording) {
                num = record.read(lin, 0, 1024);
                track.write(lin, 0, num);
            }
        }
    }

    /**
     * USe to start the recording and get it played using the audiotrack class instance.
     */
    private void startRecordAndPlay() {
        System.out.println("Clicked recording and playing");
        LOGGER.info("Started the realtime input and amplified output unit");
        record.startRecording();
        System.out.println(record);
        track.play();

        isRecording = true;
    }

    /**
     * Use to stop the recording and make the playing track pause in order to stop playing the recorded track.
     */
    private void stopRecordAndPlay() {
        record.stop();
        track.pause();
        isRecording = false;
    }


}