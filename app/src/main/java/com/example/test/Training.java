package com.example.test;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;


import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.example.test.Structures.FeatureExtractionStructure;
import com.example.test.Structures.Keys;
import com.example.test.Structures.ModelingStructure;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import java.io.File;
import java.io.FileFilter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;


/**
 * Created by UZER on 10/24/2016.
 */
public class Training extends Activity {

    public final static String TAG = "ASR";
    public final static String AUDIO_EXT = ".wav";
    public final static String FEATURE_EXT = ".ff";
    public final static String PATH = Environment.getExternalStorageDirectory() + "/ASR";

    public static int[] SVMResults;

    public static TransformSelector transformType;

    public static int numCores;
    //public static String[] names;

    public static String fileName;
    public static boolean isTraining;

    public static Context context = null;

    //private static TextView infos =  null;
    private static EditText etName = null;
    private static EditText etDuration = null;
    private RadioButton rbTrain = null;
    private RadioButton rbRecognize = null;
    private static TextView tvResults = null;
    private static TextView speaker = null;
    private static PieChart pChart = null;

    private FEReceiver feReceiver;
    private SVMReceiver svmReceiver;
    private RecognitionReceiver recognitionReceiver;
    private InputMethodManager inputManager;

    private Toolbar toolbar;

    private int getNumCores()
    {
        //Private Class to display only CPU devices in the directory listing
        class CpuFilter implements FileFilter {
            @Override
            public boolean accept(File pathname) {
                //Check if filename is "cpu", followed by a single digit number
                return Pattern.matches("cpu[0-9]+", pathname.getName());
            }
        }

        try {
            //Get directory containing CPU info
            File dir = new File("/sys/devices/system/cpu/");
            //Filter to only list the devices we care about
            File[] files = dir.listFiles(new CpuFilter());
            //Return the number of cores (virtual CPU devices)
            return files.length;
        } catch(Exception e) {
            //Default to return 1 core
            return 1;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.train_voice);

        numCores = getNumCores();
        initPreferences();
        context = this;

        Button btnRecord = (Button) findViewById(R.id.RecordButton);

        transformType   = TransformSelector.TT_DFT;
        inputManager    = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        toolbar     = (Toolbar)findViewById(R.id.toolbar);
        // infos       = (TextView)findViewById(R.id.Infos);
        pChart      = (PieChart)findViewById(R.id.chart);
        etDuration  = (EditText)findViewById(R.id.edt_Duration);
        etName      = (EditText)findViewById(R.id.edt_Speaker);
        rbRecognize = (RadioButton)findViewById(R.id.rbt_Recognize);
        rbTrain     = (RadioButton)findViewById(R.id.rbt_Train);
        tvResults   = (TextView)findViewById(R.id.tv_Results);



        //setSupportActionBar(toolbar);
        pChart.setDescription(" ");

        rbRecognize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etName.setVisibility(View.INVISIBLE);
                tvResults.setVisibility(View.VISIBLE);//set visibility of the recognition results
            }
        });

        rbTrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etName.setVisibility(View.VISIBLE);
                // Hide previous chart
                pChart.setVisibility(View.INVISIBLE);
                // Hide previous result
                tvResults.setText("");
                tvResults.setVisibility(View.INVISIBLE);
            }
        });

        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputManager.hideSoftInputFromWindow(
                        (null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                fileName = null;
                isTraining = false;

                if (rbTrain.isChecked() && etName.getText().length() != 0) {
                    // Train.
                    fileName = "[" + getCurrentDate() + "]"
                            + etName.getText().toString()
                            + AUDIO_EXT;

                    isTraining = true;
                }

                if (rbRecognize.isChecked()) {
                    // Hide previous chart
                    pChart.setVisibility(View.INVISIBLE);
                    // Hide previous result
                    tvResults.setText("");
                    // Recognize.
                    fileName = "[" + getCurrentDate() + "]"
                            + AUDIO_EXT;

                    // updatePieChart(names,);
                }

                if (null != fileName) {
                    int durationSecs;
                    if(etDuration.getText().toString().length() == 0)
                        durationSecs = 10;
                    else
                        durationSecs = (Integer.valueOf(etDuration.getText().toString()) / 1000);

                    if( durationSecs == 0)
                        durationSecs = 1;

                    Rec rec = new Rec(context, durationSecs, FeatureExtractionStructure.sampleRate);
                    // +1 here because the first second of registration will be ignored
                    // during the features extraction.
                    rec.execute(PATH, fileName);
                }
            }
        });

        //Extract feature of recording wavs.(FeatureExtractor)
        feReceiver = new FEReceiver();
        context.registerReceiver(feReceiver, new IntentFilter("com.example.test.FEATURE_EXTRACT"));

        //Send SVM intent so that SVM recognition may start (MySVM_Async)
        svmReceiver = new SVMReceiver();
        context.registerReceiver(svmReceiver, new IntentFilter("com.example.test.SVM_EXTRACT"));

        //calling the updateRecognitionResults method which display the speaker name (Trainning)
        recognitionReceiver = new RecognitionReceiver();
        context.registerReceiver(recognitionReceiver, new IntentFilter("com.example.test.UPDATE_RECOGNITION"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onMenuOpened(int featureld, Menu menu) {
        inputManager.hideSoftInputFromWindow(
                (null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        return true;
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            super.onResume();
            startActivity(i);
            return true;
        }
        else if(id == R.id.SVM_actions)
        {
            Intent i = new Intent(this, SVMActivity.class);
            super.onResume();
            startActivity(i);
            return true;
        }

        return true;
    }



    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        unregisterReceiver(feReceiver);
        unregisterReceiver(svmReceiver);
        unregisterReceiver(recognitionReceiver);
    }

    private void initPreferences()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        // Feature extraction-related preferences

        FeatureExtractionStructure.frameDuration =
                preferences.getInt(Keys.frameDuration, 32);
        Framer.FRAME_LENGTH_MS = FeatureExtractionStructure.frameDuration;

        FeatureExtractionStructure.sampleRate =
                Integer.parseInt(preferences.getString(Keys.sampleRate, "8000"));
        Framer.SAMPLE_RATE = FeatureExtractionStructure.sampleRate;

        FeatureExtractionStructure.overlapFactor =
                preferences.getInt(Keys.overlapFactor, 75) / 100.f;
        Framer.FRAME_OVERLAP_FACTOR = FeatureExtractionStructure.overlapFactor;

        FeatureExtractionStructure.energyThreshold =
                Double.valueOf(preferences.getString(Keys.energyThreshold, "5e7"));
        Framer.ENERGY_THRESHOLD = FeatureExtractionStructure.energyThreshold;

        Framer.SAMPLES_IN_FRAME = preferences.getInt(Keys.samplesInFrame, 256);


        // Modeling-related preferences
        ModelingStructure.speakersNames =
                preferences.getString(getString(R.string.speakers_name_key), "").replace(" ", "").split(",");

        ModelingStructure.labels = new int[ModelingStructure.speakersNames.length];
        for(int i = 0; i < ModelingStructure.speakersNames.length; i++)
        {
            ModelingStructure.labels[i] = i;
        }
        ModelingStructure.cStart =
                preferences.getInt(Keys.cStart, 1);
        ModelingStructure.cEnd =
                preferences.getInt(Keys.cEnd, 1);
        ModelingStructure.cStep =
                preferences.getInt(Keys.cStep, 1);
        ModelingStructure.gStart =
                preferences.getInt(Keys.gStart, 1);
        ModelingStructure.gEnd =
                preferences.getInt(Keys.gEnd, 1);
        ModelingStructure.gStep =
                preferences.getInt(Keys.gStep, 1);
        ModelingStructure.folds =
                preferences.getInt(Keys.folds, 2);
    }

    private static String getCurrentDate()
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return(dateFormat.format(date));
    }
/*
* This method return the speaker ,speaking
* */
    public static void updateRecognitionResults(int result)
    {
        String[] names = ModelingStructure.speakersNames;
        String speaker;
        switch(result)
        {
            case(0):
            case(1):
            case(2):
            case(3):
                speaker = names[result];
                break;
            default:
                speaker = "<unknown_speaker>";
                break;
        }

        // Show the results in a PieChart, in a Toast and in a TextView.
        updatePieChart(names, SVMResults);
        String text = " " + speaker + " did speak.";
        tvResults.setText(text);
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    private static void updatePieChart(String[] names, int[] values)
    {
        // By default the chart is not visible
        pChart.setVisibility(View.VISIBLE);
        // The output is automatically in percentages
        pChart.setUsePercentValues(true);
        // Write the x-value on the chart
        pChart.setDrawSliceText(false);
        // You spin the chart round, baby right round like a record, baby, right round round round
        pChart.setDragDecelerationFrictionCoef(0.96f);
        // Avoid punching yourself in the eyes when the background isn't white
        pChart.setHoleColorTransparent(true);

        // List of Entry(float val, int index), necessary for the ChartDataSet
        ArrayList<Entry> results = new ArrayList<>();

        for(int i = 0; i < values.length; i++)
        {
            results.add(new Entry(values[i], i));
        }

        // Create a new data set for the pie chart with the desired values
        PieDataSet pieDataSet = new PieDataSet(results, "");
        // Set colors for each slice
        pieDataSet.setColors(new int[] {Color.rgb(164, 230, 255), Color.rgb(0, 185, 255), Color.rgb(0, 116, 159)});
        // Space between slices (in degrees
        pieDataSet.setSliceSpace(5f);
        // Create the data that will be displayed by the chart
        PieData pieData = new PieData(names, pieDataSet);

        pChart.setRotationAngle(0);
        // Impress an initial spin that fades away within 3 seconds and makes the pie rotate 720 degrees
        pChart.spin(3000,0,720, Easing.EasingOption.EaseOutCubic);

        // Display the data
        pChart.setData(pieData);
        pChart.refreshDrawableState();
    }
}
