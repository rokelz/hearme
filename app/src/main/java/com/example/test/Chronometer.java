package com.example.test;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.logging.Logger;

/**
 * Created by USER on 5/18/2016.
 *
 * @author R.K.Opatha (IT13510572)
 *         Use to handle the chronometer which is being used
 *         for testing purpose.
 *         Use to implement a customize view.
 * @see android.app.ActionBar
 */
public class Chronometer extends TextView {
    @SuppressWarnings("unused")
    private static final String TAG = "Chronometer";
    private final static Logger LOGGER = Logger.getLogger(Chronometer.class.getName());

    /**
     * Creating an interface to apply multiple implementation
     * of the internal functions/methods.
     */
    public interface OnChronometerTickListener {

        void onChronometerTick(Chronometer chronometer);
    }

    private long mBase;
    private boolean mVisible;
    private boolean mStarted;
    private boolean mRunning;
    private OnChronometerTickListener mOnChronometerTickListener;

    private static final int TICK_WHAT = 2;

    /**
     * Create a defined context calling the super context
     * including additional features.
     *
     * @param context
     */
    public Chronometer(Context context) {
        this(context, null, 0);
    }

    /**
     * Create a chronometer based on the context given and the
     * attributes listed.
     *
     * @param context
     * @param attrs
     */
    public Chronometer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Create a chronometer based on the context given and the
     * attributes listed.
     *
     * @param context
     * @param attrs
     * @param defStyle
     */
    public Chronometer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    /**
     * Returns milliseconds since boot, including time spent in sleep.
     * and then use to update the text.
     */
    private void init() {
        mBase = SystemClock.elapsedRealtime();
        updateText(mBase);
    }

    /**
     * Setting the mBase value as given.
     *
     * @param base
     */
    public void setBase(long base) {
        mBase = base;
        dispatchChronometerTick();
        updateText(SystemClock.elapsedRealtime());
    }

    /**
     * Get the mBase value.
     *
     * @return
     */
    public long getBase() {
        return mBase;
    }

    /**
     * To set the ChronometerTickListener as value given.
     *
     * @param listener
     */
    public void setOnChronometerTickListener(OnChronometerTickListener listener) {
        mOnChronometerTickListener = listener;
    }

    /**
     * To get the ChronometerTickListener given.
     *
     * @return
     */
    public OnChronometerTickListener getOnChronometerTickListener() {
        return mOnChronometerTickListener;
    }

    /**
     * To start the chronometer.
     */
    public void start() {
        mStarted = true;
        updateRunning();
    }

    /**
     * To check whether the clock is running or not.
     *
     * @return
     */
    public boolean isRunning() {
        return mRunning;
    }

    /**
     * To check whether the clock is started or not.
     *
     * @return
     */
    public boolean isStarted() {
        return mStarted;
    }

    /**
     * Update the mStarted variable to false and
     * Update the running.
     */
    public void stop() {
        mStarted = false;
        updateRunning();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mVisible = false;
        updateRunning();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        mVisible = visibility == VISIBLE;
        updateRunning();
    }

    /**
     * Synchronizingly update the text using the value given.
     *
     * @param now
     */
    private synchronized void updateText(long now) {
        long timeElapsed = now - mBase;

        DecimalFormat df = new DecimalFormat("00");

        int hours = (int) (timeElapsed / (3600 * 1000));
        int remaining = (int) (timeElapsed % (3600 * 1000));

        int minutes = (int) (remaining / (60 * 1000));
        remaining = (int) (remaining % (60 * 1000));

        int seconds = (int) (remaining / 1000);
        remaining = (int) (remaining % (1000));

        int milliseconds = (int) (((int) timeElapsed % 1000) / 100);
        // TODO: show to two digits int milliseconds = (int) (remaining / 1000);

        String text = "";

        if (hours > 0) {
            text += df.format(hours) + ":";
            text += df.format(minutes) + ".";
            text += df.format(seconds);
        } else {
            text += df.format(minutes) + ":";
            text += df.format(seconds) + ".";
            text += new DecimalFormat("0").format(milliseconds);
        }

        setText(text);
    }

    /**
     * To Update the text with system clock elapsed time.
     */
    private void updateRunning() {
        boolean running = mVisible && mStarted;
        if (running != mRunning) {
            if (running) {
                updateText(SystemClock.elapsedRealtime());
                dispatchChronometerTick();
                mHandler.sendMessageDelayed(
                        Message.obtain(mHandler, TICK_WHAT), 100);
            } else {
                mHandler.removeMessages(TICK_WHAT);
            }
            mRunning = running;
        }
    }

    /**
     * To handle the message is its running. Update the text fields, dispatch the listener
     * and send the delayed message.
     */
    private Handler mHandler = new Handler() {
        public void handleMessage(Message m) {
            if (mRunning) {
                updateText(SystemClock.elapsedRealtime());
                dispatchChronometerTick();
                sendMessageDelayed(Message.obtain(this, TICK_WHAT), 100);
            }
        }
    };

    /**
     * To check whether the ChronometerTickListener is null or not.
     */
    void dispatchChronometerTick() {
        if (mOnChronometerTickListener != null) {
            mOnChronometerTickListener.onChronometerTick(this);
        }
    }
}