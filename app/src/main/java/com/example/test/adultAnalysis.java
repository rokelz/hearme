package com.example.test;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Created by USER on 7/19/2016.
 *
 * @author R.K.Opatha (IT13510572)
 *         Use to handle the neural network for adults.
 * @see android.app.ActionBar
 */
public class adultAnalysis extends ActionBarActivity {
    //user defineable variables
    public static int numEpochs = 150; //number of training cycles
    public static int numInputs = 10; //number of inputs - this includes the input bias
    public static int numHidden = 10; //number of hidden units
    public static int numPatterns = 320; //number of training patterns
    public static double LR_IH = 0.7; //learning rate
    public static double LR_HO = 0.07; //learning rate
    private final static Logger LOGGER = Logger.getLogger(adultAnalysis.class.getName());
    //process variables
    public static int patNum;
    public static double errThisPat;
    public static double outPred;
    public static double RMSerror;

    //training data
    public static int[][] childInputs = new int[numPatterns][numInputs];
    public static double[] childOutputs = new double[numPatterns];

    //the outputs of the hidden neurons
    public static double[] hiddenVal = new double[numHidden];

    //the weights
    public static double[][] weightsIH = new double[numInputs][numHidden];
    public static double[] weightsHO = new double[numHidden];

    /**
     * Use to calculate the outputs of the hidden neurons
     */
    public void calcNet() {


        //calculate the outputs of the hidden neurons
        //the hidden neurons are tanh
        for (int i = 0; i < numHidden; i++)//numhi=4
        {
            hiddenVal[i] = 0.0;

            for (int j = 0; j < numInputs; j++)
                hiddenVal[i] = hiddenVal[i] + (childInputs[patNum][j] * weightsIH[j][i]);

            hiddenVal[i] = tanh(hiddenVal[i]);
        }

        //calculate the output of the network
        //the output neuron is linear
        outPred = 0.0;

        for (int i = 0; i < numHidden; i++)
            outPred = outPred + hiddenVal[i] * weightsHO[i];

        //calculate the error
        errThisPat = outPred - childOutputs[patNum];
    }


    /**
     * Use to adjust the weights input-output
     */
    public void WeightChangesHO()
    //adjust the weights hidden-output
    {
        for (int k = 0; k < numHidden; k++) {
            double weightChange = LR_HO * errThisPat * hiddenVal[k];
            weightsHO[k] = weightsHO[k] - weightChange;

            //regularisation on the output weights
            if (weightsHO[k] < -5)
                weightsHO[k] = -5;
            else if (weightsHO[k] > 5)
                weightsHO[k] = 5;
        }
    }


    /**
     * Use to adjust the weights input-hidden
     */
    public void WeightChangesIH()
    //adjust the weights input-hidden
    {
        for (int i = 0; i < numHidden; i++) {
            for (int k = 0; k < numInputs; k++) {
                double x = 1 - (hiddenVal[i] * hiddenVal[i]);
                x = x * weightsHO[i] * errThisPat * LR_IH;
                x = x * childInputs[patNum][k];
                double weightChange = x;
                weightsIH[k][i] = weightsIH[k][i] - weightChange;
            }
        }
    }


    /**
     * Use to initialize the random numbers as weights
     */
    public void initWeights() {

        for (int j = 0; j < numHidden; j++)//numh = 4
        {
            weightsHO[j] = (Math.random() - 0.5) / 2;
            for (int i = 0; i < numInputs; i++)//numi = 3
                weightsIH[i][j] = (Math.random() - 0.5) / 5;
        }

    }

    /**
     * Use to implement the tan function for the neural network
     *
     * @param x
     * @return
     */
    public double tanh(double x) {
        if (x > 20)
            return 1;
        else if (x < -20)
            return -1;
        else {
            double a = Math.exp(x);
            double b = Math.exp(-x);
            return (a - b) / (a + b);
        }
    }

    public double soutPred;

    /**
     * Use to implement a method to calculate the ouput based
     * on the trained data set.
     *
     * @param input
     * @return
     */
    public double scalcNet(int input[][]) {
        //calculate the outputs of the hidden neurons
        //the hidden neurons are tanh
        for (int i = 0; i < numHidden; i++)//numhi=4
        {
            //shiddenVal[i] = 0.0;

            for (int j = 0; j < numInputs; j++) {
                hiddenVal[i] = hiddenVal[i] + (input[0][j] * weightsIH[j][i]);
                System.out.print(hiddenVal[i]);
            }

            hiddenVal[i] = tanh(hiddenVal[i]);
        }

        //calculate the output of the network
        //the output neuron is linear
        soutPred = 0.0;
        for (int j = 0; j < numHidden; j++) {
            System.out.println(hiddenVal[j] * weightsHO[j]);
            soutPred = soutPred + hiddenVal[j] * weightsHO[j];
            System.out.println(soutPred);
        }
        return soutPred;

    }


    /**
     * Use to calculate the overall error with each iteration
     */
    public void calcOverallError() {
        RMSerror = 0.0;
        for (int i = 0; i < numPatterns; i++) {
            patNum = i;
            calcNet();
            RMSerror = RMSerror + (errThisPat * errThisPat);
        }
        RMSerror = RMSerror / numPatterns;
        RMSerror = java.lang.Math.sqrt(RMSerror);
    }

    /**
     * Include the variable initialization
     */
    public void initData() {
        initChildData();


    }

    /**
     * Use to initialize the training data sets
     */
    public void initializechildData() {


        for (int i = 0; i < numPatterns; i++) {
            for (int j = 0; j < numInputs; j++)
                childInputs[i][j] = 0;
            childInputs[i][9] = 1;
        }

    }

    /**
     * Use to read the training data from a text file and to feed the neural network
     */
    public void childdatafirstfour() {
        String inputOne, inputGender, inputSh, inputPs, inputBp, inputEsh, inputHl, inputTg;
        StringBuilder contents = new StringBuilder();
        int count;
        String sep = System.getProperty("line.separator");
        try {
            InputStream is = this.getResources().openRawResource(R.raw.adult);
            BufferedReader input = new BufferedReader(new InputStreamReader(is), 1024 * 8);
            try {
                String line = null;
                count = 0;
                while ((line = input.readLine()) != null) {
                    contents.append(line);
                    contents.append(sep);
                    String words[] = line.split("\t");
                    inputOne = words[0];
                    inputGender = words[1];
                    inputSh = words[2];
                    inputPs = words[3];
                    inputBp = words[4];
                    inputEsh = words[5];
                    inputHl = words[6];
                    inputTg = words[7];
                    childInputs[count][0] = Integer.parseInt(inputOne);
                    childInputs[count][1] = Integer.parseInt(inputGender);
                    childInputs[count][2] = Integer.parseInt(inputSh);
                    childInputs[count][3] = Integer.parseInt(inputPs);
                    childInputs[count][4] = Integer.parseInt(inputBp);
                    childInputs[count][5] = Integer.parseInt(inputEsh);
                    int hearing = Integer.parseInt(inputHl);
                    if (hearing == 1) {
                        childInputs[count][6] = 0;
                        childInputs[count][7] = 0;
                        childInputs[count][8] = 0;
                    } else if (hearing == 2) {
                        childInputs[count][6] = 0;
                        childInputs[count][7] = 0;
                        childInputs[count][8] = 1;
                    } else if (hearing == 3) {
                        childInputs[count][6] = 0;
                        childInputs[count][7] = 1;
                        childInputs[count][8] = 0;
                    } else if (hearing == 4) {
                        childInputs[count][6] = 0;
                        childInputs[count][7] = 1;
                        childInputs[count][8] = 1;
                    } else if (hearing == 5) {
                        childInputs[count][6] = 1;
                        childInputs[count][7] = 0;
                        childInputs[count][8] = 0;
                    }
                    childInputs[count][9] = 1;
                    childOutputs[count] = Double.parseDouble(inputTg);
                    count++;

                }

            } finally {
                input.close();
            }

        } catch (FileNotFoundException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    /**
     * Initialize the training data sets
     */
    public void initChildData() {

        initializechildData();
        childdatafirstfour();

        //childdataOuts();

    }

    /**
     * Use to display the result of neural network agter calculation
     *
     * @param input
     * @param right
     */
    public void displayResults(int input[][], int right[][]) {
        for (int i = 0; i < numPatterns; i++) {
            patNum = i;
            calcNet();
            System.out.println("pat = " + (patNum + 1) + " actual = " + childOutputs[patNum] + " neural model = " + outPred);
        }
        System.out.println("*********************");
        for (int i = 0; i < 7; i++)
            System.out.print(input[0][i]);
        System.out.println("*********************");

        double leftres = scalcNet(input);
        double rightres = scalcNet(right);

        //int num = (int)res;
        calcOverallError();
        //vtw.setText(Double.toString(res));
        //vw.setText(selectStage(num));
        //tw.setText(Double.toString(RMSerror));
        double accuracy = (1 - RMSerror) * 100;
        accuracytext.setText("Accuracy of the prediction    :  " + Double.toString(accuracy) + "%");


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(adultAnalysis.this);
        alertDialog.setTitle("How's your hearing going on...");
        alertDialog.setIcon(R.drawable.icon);
        String messageForDialog = "Left ear hearing stage = " + selectifStage(leftres) + " \n \n Right ear hearing stage = " + selectifStage(rightres) + " \n \n Please take the" +
                "necessary safety actions....";
        alertDialog.setMessage(messageForDialog);
        alertDialog.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog,
                                        int which) {


                    }
                });
        alertDialog.show();

    }


    /**
     * Use to give a stage of hearing loss based on the neural network output
     * value.
     *
     * @param num
     * @return
     */
    public String selectifStage(double num) {
        String stage = "";
        if (num < 1.5)
            stage = "Normal Stage";
        else if (num >= 1.5 && num < 2)
            stage = "In between Normal & Mild Hearing Loss";
        else if (num >= 2 && num < 2.5)
            stage = " Mild Hearing Loss";
        else if (num >= 2.5 && num < 3)
            stage = "In between Mild & Moderate Hearing Loss";
        else if (num >= 3 && num < 3.5)
            stage = " Moderate Hearing Loss";
        else if (num >= 3.5 && num < 4)
            stage = "In between Moderate & Severe Hearing Loss";
        else if (num >= 4 && num < 4.5)
            stage = "Severe Hearing Loss";
        else if (num >= 4.5 && num < 5)
            stage = "In between Severe & Profound Hearing Loss";
        else if (num >= 5)
            stage = "Profound deafness";

        return stage;
    }

    LineChart lineChart;
    LineDataSet dataset;
    Spinner one, gen, ps, esh, smh, blp;
    int g, o, p, e, a;
    double h;
    TextView accuracytext;
    EditText hearingLevel, age;
    Button btn;
    SeekBar seek;
    int left, right;
    String gender = "", noiseEx = "", physicalStat = "", enviroSmoke = "", ageval, hlevel, smoking = "", bloodp = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adultanalysis);
        getSupportActionBar().hide();
        InputStream is = this.getResources().openRawResource(R.raw.adult);
        BufferedReader input = new BufferedReader(new InputStreamReader(is), 1024 * 8);

        initializechildData();
        initWeights();
        initData();
        lineChart = (LineChart) findViewById(R.id.chart);
        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<String>();

        left = Integer.parseInt(getIntent().getStringExtra("left"));
        right = Integer.parseInt(getIntent().getStringExtra("right"));
        accuracytext = (TextView) findViewById(R.id.textView23);
        accuracytext.setText("");
        for (int j = 0; j <= numEpochs; j++)//numEpochs=500
        {

            for (int i = 0; i < numPatterns; i++)//numPatterns=4
            {

                //select a pattern at random
                patNum = (int) ((Math.random() * numPatterns) - 0.001);

                //calculate the current network output
                //and error for this pattern
                calcNet();

                //change network weights
                WeightChangesHO();
                WeightChangesIH();
            }

            //display the overall network error
            //after each epoch
            calcOverallError();
            entries.add(new Entry((float) RMSerror, j + 1));
            dataset = new LineDataSet(entries, "RMS Error per iteration");

            labels.add(Integer.toString(j));

            System.out.println("epoch = " + j + "  RMS Error = " + RMSerror);

        }

        LineData data = new LineData(labels, dataset);
        dataset.setColors(ColorTemplate.COLORFUL_COLORS); //
        dataset.setDrawCubic(true);
        dataset.setDrawFilled(true);

        lineChart.setData(data);
        lineChart.animateY(5000);
        //training has finished
        //display the results


        //set spinner
        gen = (Spinner) findViewById(R.id.spinner);
        ps = (Spinner) findViewById(R.id.spinner2);
        esh = (Spinner) findViewById(R.id.spinner3);
        one = (Spinner) findViewById(R.id.spinner4);
        smh = (Spinner) findViewById(R.id.spinner6);
        blp = (Spinner) findViewById(R.id.spinner5);
        btn = (Button) findViewById(R.id.button);


        gen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gender = gen.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ps.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                physicalStat = ps.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        esh.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                enviroSmoke = esh.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        one.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                noiseEx = one.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        smh.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                smoking = smh.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        blp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bloodp = blp.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        final int rightinput[][] = new int[1][10];
        final int inputArray[][] = new int[1][10];
        btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        for (int i = 0; i < 10; i++) {
                            inputArray[0][i] = 0;
                            rightinput[0][i] = 0;
                        }
                        inputArray[0][9] = 1;
                        rightinput[0][9] = 1;
                        inputArray[0][1] = getONE();
                        rightinput[0][1] = inputArray[0][1];
                        inputArray[0][2] = getGender();
                        rightinput[0][2] = inputArray[0][2];
                        inputArray[0][3] = getPS();
                        rightinput[0][3] = inputArray[0][3];
                        inputArray[0][0] = getSH();
                        rightinput[0][0] = inputArray[0][0];
                        inputArray[0][4] = getESH();
                        rightinput[0][4] = inputArray[0][4];
                        inputArray[0][5] = getBP();
                        rightinput[0][5] = inputArray[0][5];
                        System.out.println(getGender() + "   " + getPS() + "   " + getESH() + "   " + getONE());
                        System.out.println(h);

                        switch (hearingLevel(left)) {
                            case 0:
                                inputArray[0][6] = 0;
                                inputArray[0][7] = 0;
                                inputArray[0][8] = 0;
                                break;
                            case 1:
                                inputArray[0][6] = 0;
                                inputArray[0][7] = 0;
                                inputArray[0][8] = 1;
                                break;
                            case 2:
                                inputArray[0][6] = 0;
                                inputArray[0][7] = 1;
                                inputArray[0][8] = 0;
                                break;
                            case 3:
                                inputArray[0][6] = 0;
                                inputArray[0][7] = 1;
                                inputArray[0][8] = 1;
                                break;
                            case 4:
                                inputArray[0][6] = 1;
                                inputArray[0][7] = 0;
                                inputArray[0][8] = 0;
                                break;
                        }
                        switch (hearingLevel(right)) {
                            case 0:
                                inputArray[0][6] = 0;
                                inputArray[0][7] = 0;
                                inputArray[0][8] = 0;
                                break;
                            case 1:
                                inputArray[0][6] = 0;
                                inputArray[0][7] = 0;
                                inputArray[0][8] = 1;
                                break;
                            case 2:
                                inputArray[0][6] = 0;
                                inputArray[0][7] = 1;
                                inputArray[0][8] = 0;
                                break;
                            case 3:
                                inputArray[0][6] = 0;
                                inputArray[0][7] = 1;
                                inputArray[0][8] = 1;
                                break;
                            case 4:
                                inputArray[0][6] = 1;
                                inputArray[0][7] = 0;
                                inputArray[0][8] = 0;
                                break;
                        }
                        for (int i = 0; i < 7; i++)
                            System.out.print(inputArray[0][i]);

                        displayResults(inputArray, rightinput);

                    }
                }
        );


    }

    /**
     * Get thegender option value from the combo box
     *
     * @return
     */
    public int getGender() {
        int x = 0;
        System.out.println(gender);
        if (gender.equals("Female"))
            x = 1;
        return x;
    }

    /**
     * Get the physical status value from the combo box
     *
     * @return
     */
    public int getPS() {
        int x = 0;
        System.out.println(physicalStat);
        if (physicalStat.equals("Sad"))
            x = 1;
        return x;
    }

    /**
     * Get the enviromental smoking habits value from the combo box
     *
     * @return
     */
    public int getESH() {
        int x = 0;
        System.out.println(enviroSmoke);
        if (enviroSmoke.equals("Yes"))
            x = 1;
        return x;
    }

    /**
     * Get the occupaptional noise exposure value from the combo box.
     *
     * @return
     */
    public int getONE() {
        int x = 0;
        System.out.println(noiseEx);
        if (noiseEx.equals("Yes"))
            x = 1;
        return x;
    }

    /**
     * Get the smoking habits value from the combo box.
     *
     * @return
     */
    public int getSH() {
        int x = 0;
        System.out.println(smoking);
        if (smoking.equals("Yes"))
            x = 1;
        return x;
    }

    /**
     * Get the blood pressure value from the combo box.
     *
     * @return
     */
    public int getBP() {
        int x = 0;
        System.out.println(bloodp);
        if (bloodp.equals("Low"))
            x = 1;
        else
            x = 0;
        return x;
    }

    /**
     * Use to depict the hearing stage of a normal healthy person based on the decibel levels.
     *
     * @param hear
     * @return
     */
    public int hearingLevel(int hear) {
        int x = 0;
        if (0 < hear && hear < 25) {
            x = 0;
        } else if (26 < hear && hear < 40) {
            x = 1;
        } else if (41 < hear && hear < 70) {
            x = 2;
        } else if (71 < hear && hear < 90) {
            x = 3;
        } else {
            x = 4;
        }
        return x;
    }

}
