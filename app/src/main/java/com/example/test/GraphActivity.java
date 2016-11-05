package com.example.test;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Created by USER on 5/18/2016.
 * To create the graphs for left and right ear based on the past
 * log records and determine the prediction.
 *
 * @author R.K.Opatha (IT13510572)
 * @see android.app.ActionBar
 **/
public class GraphActivity extends ActionBarActivity {
    LineChart lineChartLeft, LineChartRight;
    LineDataSet leftdataset, rightdataset;
    Button predict;
    public int averageLeftLevel, averageRightLevel, sumOfLeft, sumOfRight;
    SqliteController controller = new SqliteController(this);
    Spinner spinner;
    String item;
    private final static Logger LOGGER = Logger.getLogger(GraphActivity.class.getName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphleftright);
        getSupportActionBar().hide();
        lineChartLeft = (LineChart) findViewById(R.id.chart1);
        LineChartRight = (LineChart) findViewById(R.id.chart2);
        String profileId = getIntent().getStringExtra("profileID");
        predict = (Button) findViewById(R.id.imageButton45);
        System.out.println(profileId);
        // creating list of entry
        ArrayList<Entry> leftentries = new ArrayList<>();
        ArrayList<Entry> rightentries = new ArrayList<>();
        sumOfLeft = 0;
        sumOfRight = 0;
        ArrayList<HashMap<String, String>> profileNameList = controller.getGraphDetails(profileId);
        int i = 0;
        int arrleng = profileNameList.size();
        for (i = 0; i < profileNameList.size(); i++) {

            leftentries.add(new Entry(Integer.parseInt(profileNameList.get(i).get("left").toString()), i));
            rightentries.add(new Entry(Integer.parseInt(profileNameList.get(i).get("right").toString()), i));
            sumOfLeft = sumOfLeft + Integer.parseInt(profileNameList.get(i).get("left").toString());
            sumOfRight = sumOfRight + Integer.parseInt(profileNameList.get(i).get("right").toString());
        }
        averageLeftLevel = sumOfLeft / profileNameList.size();
        averageRightLevel = sumOfRight / profileNameList.size();
        System.out.println(averageLeftLevel);
        System.out.println(averageRightLevel);
        leftdataset = new LineDataSet(leftentries, "Decibel Limit");
        rightdataset = new LineDataSet(rightentries, "Decibel Limit");
        // creating labels
        ArrayList<String> leftlabels = new ArrayList<String>();
        ArrayList<String> rightlabels = new ArrayList<String>();
        for (i = 0; i < profileNameList.size(); i++) {
            leftlabels.add(profileNameList.get(i).get("logdate").toString());
            rightlabels.add(profileNameList.get(i).get("logdate").toString());
        }
        LineData dataleft = new LineData(leftlabels, leftdataset);
        lineChartLeft.setData(dataleft); // set the data and list of lables into chart
        lineChartLeft.setDescription("Tested Dates");  // set the description

        LineData dataright = new LineData(rightlabels, rightdataset);
        LineChartRight.setData(dataright); // set the data and list of lables into chart
        LineChartRight.setDescription("Tested Dates");  // set the description

        predict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println(averageLeftLevel);
                System.out.println(averageRightLevel);
                LayoutInflater layoutInflater = LayoutInflater.from(GraphActivity.this);
                View promptView = layoutInflater.inflate(R.layout.activity_age, null);

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(GraphActivity.this);
                alertDialog.setTitle("Select your age group......");
                alertDialog.setIcon(R.drawable.icon);
                alertDialog.setView(promptView);
                spinner = (Spinner) promptView.findViewById(R.id.spinnerage);


                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        item = spinner.getSelectedItem().toString();

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                alertDialog.setPositiveButton("Submit",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                                int which) {
                                Log.i("HearMeAnalyse", item.toString());
                                if (item.matches("greater than 21 years")) {
                                    Intent adult = new Intent(GraphActivity.this, adultAnalysis.class);
                                    adult.putExtra("left", String.valueOf(averageLeftLevel));
                                    adult.putExtra("right", String.valueOf(averageRightLevel));
                                    startActivity(adult);
                                } else {
                                    Intent child = new Intent(GraphActivity.this, childAnalysis.class);
                                    child.putExtra("left", String.valueOf(averageLeftLevel));
                                    child.putExtra("right", String.valueOf(averageRightLevel));
                                    startActivity(child);
                                }


                            }
                        });

                alertDialog.show();

            }


        });

    }


}
