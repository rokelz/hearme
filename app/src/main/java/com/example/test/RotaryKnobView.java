package com.example.test;

import android.content.Context;
import android.graphics.Canvas;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by USER on 5/10/2016.
 */
public class RotaryKnobView extends ImageView {

    AudioManager audioManager;
    int ax;
    int cx;
    private float angle = 0f;
    private float theta_old = 0f;
    MediaPlayer mp1;

    private RotaryKnobListener listener;

    public interface RotaryKnobListener {
        public void onKnobChanged(int arg);
    }

    public void setKnobListener(RotaryKnobListener l) {
        listener = l;
    }

    public RotaryKnobView(Context context) {
        super(context);
        initialize();
        audioManager = (AudioManager) getContext().getSystemService(context.AUDIO_SERVICE);
        ax = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
        cx = audioManager.getStreamVolume(AudioManager.STREAM_RING);
    }

    public RotaryKnobView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public RotaryKnobView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    private float getTheta(float x, float y) {
        float sx = x - (getWidth() / 2.0f);
        float sy = y - (getHeight() / 2.0f);

        float length = (float) Math.sqrt(sx * sx + sy * sy);
        float nx = sx / length;
        float ny = sy / length;
        float theta = (float) Math.atan2(ny, nx);

        final float rad2deg = (float) (360.0 / Math.PI);
        float thetaDeg = theta * rad2deg;
        System.out.println(thetaDeg);
        if (thetaDeg < 0) {
            thetaDeg = thetaDeg * (-1);
        }
        System.out.println(thetaDeg);
        return thetaDeg;
    }

    public float getInvTheta(float x, float y) {
        float theta = getTheta(x, y);
        return (1 / theta);
    }


    public void initialize() {
        this.setImageResource(R.drawable.jog);
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getX(0);
                float y = event.getY(0);
                float theta = getTheta(x, y);
                //increaseVolume(theta);
                //mp1.stop();
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_POINTER_DOWN:
                        theta_old = theta;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        invalidate();
                        float delta_theta = theta - theta_old;
                        theta_old = theta;
                        int direction = (delta_theta > 0) ? 1 : -1;
                        angle = theta - 180;
                        notifyListener(direction, theta);
                        break;
                }

                return true;
            }
        });
    }

    public float getAngle() {
        return angle;
    }

    private void notifyListener(int arg, float theta) {
        if (null != listener) {
            listener.onKnobChanged(arg);
        }

    }

    protected void onDraw(Canvas c) {
        c.rotate(angle, getWidth() / 2, getHeight() / 2);
        super.onDraw(c);
    }

    public void increaseVolume(float delta) {
        mp1 = MediaPlayer.create(getContext(), R.raw.tone);
        float maxVolume = 1.0f;
        float volume = (delta / 360.0f) * 128;
        System.out.println(delta);
        System.out.println("*******************************");
        System.out.println(volume);
        float log1 = (float) (Math.log(volume) / Math.log(maxVolume));
        System.out.println("-------------------------------------");

        System.out.println(log1);
        if (Float.isInfinite(log1)) {
            float count = 100 * .01f;
            log1 = count;
        }
        //mp1.setLooping(true);
        mp1.setVolume(log1, log1);
        mp1.start();
        mp1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
                mp1.release();
                mp1.stop();
            }
        });


    }
}