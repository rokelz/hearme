package com.example.test;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class FeatureExtractor extends AsyncTask<String, Void, Boolean> {

    public final static int MFCC_COUNT = 13;
    public final static String TAG = "FeatureExtractor";

    private static ProgressDialog cProgressRecorder;
    private static Context cContext;

    public static double[][] MFCC = null;
    public static double[][] DeltaDelta = null;

    public static int frameExtracted = 0;
    public static int frameRemoved = 0;
    public static int frameCount = 0;
    protected void onPreExecute()
    {
        frameExtracted = 0;
        cContext = Training.context;

        cProgressRecorder = new ProgressDialog(cContext);
        cProgressRecorder.setProgressNumberFormat(null);
        cProgressRecorder.setMax(100);

        cProgressRecorder = ProgressDialog.show(cContext, "Removing invalid frames...", "Energy based removal");
    }

    @Override
    protected Boolean doInBackground(String... params)
    {

        try
        {
            // params[0] = name of the audio file

            //params[0] = MainActivity.PATH + "/" + "aceEma0.wav";
            Timer t = new Timer();
            t.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    publishProgress();
                }
            }, 50, 50);

            frameRemoved = 0;
            MFCC = extractMFCC(params[0]);
            DeltaDelta = DD.computeDD(MFCC, 2); // 2 is the precision

            // If recognition mode is on:
            if (!Training.isTraining)
            {
                // Delete wav file
                File file = new File(params[0]);
                file.delete();
                // Send SVM intent so that SVM recognition may start
                Intent intent = new Intent("com.example.test.SVM_EXTRACT");
                cContext.sendBroadcast(intent);
            }
            else
            // if training mode is on:
            {
                /**
                 * Save to file
                 * save with the same filename of the .wav with a different extension:
                 * ff = feature file
                 */
                String outputFileName = params[0].replace(Training.AUDIO_EXT, Training.FEATURE_EXT);
                writeFeatureFile(outputFileName, MFCC, DeltaDelta);
            }
        }
        catch(Exception e)
        {
            Log.e(TAG, e.getMessage());
            return(Boolean.FALSE);
        }

        return(Boolean.TRUE);
    }

    @Override
    protected void onProgressUpdate(Void... out)
    {
        if(frameExtracted > 0)
        {
            int frameCount = Framer.getFrames().length;
            //int perc = (int)Math.floor(((float)frameExtracted / (float)frameCount * 100.0));
            cProgressRecorder.setTitle("Extracting features...");
            cProgressRecorder.setMessage(frameExtracted + "/" + frameCount);
        }
        else
        {
            cProgressRecorder.setMessage("Frames removed " + frameRemoved + "/" + frameCount);
        }
    }

    @Override
    protected void onPostExecute(Boolean cv)
    {
        cProgressRecorder.dismiss();
    }


    public static void writeFeatureFile(String fileName, double[][] MFCC, double[][] DeltaDelta)
    {
        // Stampo su file testuale i vettori
        try
        {
            FileWriter writer = new FileWriter(fileName, true);
            /*
            * output:
            * %MFCC                             %DD
            * MFCC0     MFCC1   MFCC2   ...     DD0     DD1
            * frame1
            * frame2
            * .
            * .
            * .
            */
            for(int f = 0; f < Framer.getFrames().length; f++)
            {
                String line = "";

                for (int k = 0; k < MFCC_COUNT; k++)
                    line += (k + 1) + ":" + Double.toString(MFCC[f][k]) + " ";


                for (int k = 0; k < DD.DD_COUNT; k++)
                    line += (k + MFCC_COUNT + 1) + ":" + Double.toString(DeltaDelta[f][k]) + " ";

                line = line.substring(0, line.length() - 1) + "\r\n";

                writer.write(line);
            }
            writer.close();
        }
        catch (IOException e)
        {
            Log.e(TAG, e.getMessage());
        }
    }

    public static double [][] extractMFCC(String audioFilename) throws java.lang.Exception
    {
        frameExtracted = 0;

        Framer.readFromFile(audioFilename);
        final Frame[] frames = Framer.getFrames();


        double[][] mfcc = new double[frames.length][]; // No need to create rows (FEThread already does it)


        int numCores = Training.numCores;


        Thread[] threads = new Thread[numCores];
        for (int C = 0; C < numCores; C++)
        {
            threads[C] = new Thread(new FEThread(C, numCores, frames, mfcc));
            threads[C].start();
        }
        for (int C = 0; C < numCores; C++)
            threads[C].join();

        return mfcc;
    }
}
