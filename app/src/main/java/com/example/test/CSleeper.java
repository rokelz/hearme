package com.example.test;

/**
 * Created by USER on 3/12/2016.
 * This is the sleeper for the visualizer
 * This allows the sampler to collect data before running.
 *
 * @author R.K.Opatha (IT13510572)
 * @see android.app.ActionBar
 **/
public class CSleeper
        implements Runnable {
    private Boolean done = Boolean.valueOf(false);
    private MainActivity m_ma;
    private CSampler m_sampler;

    /**
     * To initiate the constructor.
     *
     * @param paramMainActivity
     * @param paramCSampler
     */
    public CSleeper(MainActivity paramMainActivity, CSampler paramCSampler) {
        m_ma = paramMainActivity;
        m_sampler = paramCSampler;
    }

    /**
     * To run the function.
     */
    public void run() {
        try {
            m_sampler.Init();
        } catch (Exception e) {
            return;
        }
        while (true)
            try {
                Thread.sleep(1000L);
                System.out.println("Tick");
                continue;
            } catch (InterruptedException localInterruptedException) {
                localInterruptedException.printStackTrace();
            }
    }
}