package com.example.test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

/**
 * Created by USER on 3/12/2016.
 * Use to handle the visualization process.
 *
 * @author R.K.Opatha (IT13510572)
 * @see android.app.ActionBar
 */
public class CDrawer extends SurfaceView implements Callback {
    private Context mContext;
    private CDrawThread mDrawThread;
    private SurfaceHolder mHolder;

    private Boolean isCreated = false;

    /**
     * Use to initialize the Drawing pane of the visualization pane.
     *
     * @param paramContext
     * @param paramAttributeSet
     */
    public CDrawer(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        System.out.println("CDrawer()");
        mHolder = getHolder();
        mContext = paramContext;
        mHolder.addCallback(this);
        mDrawThread = new CDrawThread(mHolder, paramContext, new Handler() {
            public void handleMessage(Message paramMessage) {
            }
        });
        mDrawThread.setName("" + System.currentTimeMillis());
        setFocusable(true);
    }

    /**
     * To get the value of GetDead2 variable
     *
     * @return
     */
    public Boolean GetDead2() {
        return mDrawThread.GetDead2();
    }

    /**
     * To restart the drawing thread.
     *
     * @param paramBoolean
     */
    public void Restart(Boolean paramBoolean) {
        if (isCreated) {
            if (mDrawThread.GetDead2().booleanValue()) {
                mDrawThread.SetDead2(Boolean.valueOf(false));
                if ((!paramBoolean.booleanValue()) || (!mDrawThread.GetDead().booleanValue()))
                    mHolder = getHolder();
                mHolder.addCallback(this);
                System.out.println("Restart drawthread");
                mDrawThread = new CDrawThread(mHolder, mContext, new Handler() {
                    public void handleMessage(Message paramMessage) {
                    }
                });
                mDrawThread.setName("" + System.currentTimeMillis());
                mDrawThread.start();
                return;
            }
            Boolean No1, No2 = true;
            while (true) {
                while (No2 = true) {
                    try {
                        Thread.sleep(1000L);
                        System.out.println("Just chilling in Restart");
                        No2 = false;
                        mDrawThread.SetDead2(Boolean.valueOf(true));
                    } catch (InterruptedException localInterruptedException) {
                        localInterruptedException.printStackTrace();
                    }
                    return;
                }

                if (!mDrawThread.GetDead().booleanValue())
                    continue;
                mHolder = getHolder();
                mHolder.addCallback(this);
                System.out.println("Restart drawthread");
                mDrawThread = new CDrawThread(mHolder, mContext, new Handler() {
                    public void handleMessage(Message paramMessage) {
                    }
                });
                mDrawThread.setName("" + System.currentTimeMillis());
                mDrawThread.start();
                return;
            }
        }
    }

    /**
     * To set the value of mb_Run in the draw thread
     *
     * @param paramBoolean
     */
    public void SetRun(Boolean paramBoolean) {
        mDrawThread.setRun(paramBoolean);
    }

    /**
     * To get the currently using draw thread
     *
     * @return
     */
    public CDrawThread getThread() {
        return mDrawThread;
    }

    /**
     * Using the drawing thread, surface holder setting the surface
     * size for the recorded voice frequency drawing purpose.
     *
     * @param paramSurfaceHolder
     * @param paramInt1
     * @param paramInt2
     * @param paramInt3
     */
    public void surfaceChanged(SurfaceHolder paramSurfaceHolder, int paramInt1, int paramInt2, int paramInt3) {
        mDrawThread.setSurfaceSize(paramInt2, paramInt3);
    }

    /**
     * To create  the surface using the surface holder
     * as given.
     *
     * @param paramSurfaceHolder
     */
    public void surfaceCreated(SurfaceHolder paramSurfaceHolder) {
        System.out.println("surfaceCreated");
        if (mDrawThread.getRun().booleanValue()) {
            System.out.println("11111");
            isCreated = true;
            mDrawThread.start();
        }
        while (true) {
            System.out.println("22222");
            Restart(Boolean.valueOf(false));
            return;
        }
    }

    /**
     * To destroy the surface which has being created/ or given.
     *
     * @param paramSurfaceHolder
     */
    public void surfaceDestroyed(SurfaceHolder paramSurfaceHolder) {
        int i = 1;
        while (true) {
            if (i == 0)
                return;
            try {
                mDrawThread.join();
                i = 0;
            } catch (InterruptedException localInterruptedException) {
            }
        }
    }

    /**
     * The Drawer Thread, subclass to cDrawer class
     * We want to keep most of this process in a background thread,
     * so the UI don't hang
     *
     * @author R.K.Opatha(IT13510572)
     */
    class CDrawThread extends Thread {
        private Paint mBackPaint;
        private Bitmap mBackgroundImage;
        private short[] mBuffer;
        private int mCanvasHeight = 1;
        private int mCanvasWidth = 1;
        private Paint mLinePaint;
        private int mPaintCounter = 0;
        private SurfaceHolder mSurfaceHolder;
        private Boolean m_bDead = Boolean.valueOf(false);
        private Boolean m_bDead2 = Boolean.valueOf(true);
        private Boolean m_bRun = Boolean.valueOf(true);
        private Boolean m_bSleep = Boolean.valueOf(false);
        private int m_iScaler = 8;
        private int counter = 0;

        /**
         * Instance the Thread
         * All the parameters i handled by the cDrawer class
         *
         * @param paramContext
         * @param paramHandler
         * @param arg4
         */
        public CDrawThread(SurfaceHolder paramContext, Context paramHandler, Handler arg4) {
            mSurfaceHolder = paramContext;
            mLinePaint = new Paint();
            mLinePaint.setAntiAlias(true);
            mLinePaint.setARGB(255, 255, 0, 0);
            mLinePaint = new Paint();
            mLinePaint.setAntiAlias(true);
            mLinePaint.setARGB(255, 255, 0, 255);
            mBackPaint = new Paint();
            mBackPaint.setAntiAlias(true);
            mBackPaint.setARGB(255, 0, 0, 0);
            mBuffer = new short[2048];
            mBackgroundImage = Bitmap.createBitmap(1, 1, Config.ARGB_8888);

        }

        /**
         * To get the m_bDead variable as given.
         *
         * @return
         */
        public Boolean GetDead() {
            return m_bDead;
        }

        /**
         * To get the m_bDead2 variable as given.
         *
         * @return
         */
        public Boolean GetDead2() {
            return m_bDead2;
        }

        /**
         * To get the m_bSleep variable as given.
         *
         * @return
         */
        public Boolean GetSleep() {
            return m_bSleep;
        }

        /**
         * To set the m_bDead2 variable with the variable given.
         *
         * @param paramBoolean
         */
        public void SetDead2(Boolean paramBoolean) {
            m_bDead2 = paramBoolean;
        }

        /**
         * To set the m_bSleep variable with the variable given.
         *
         * @param paramBoolean
         */
        public void SetSleeping(Boolean paramBoolean) {
            m_bSleep = paramBoolean;
        }

        /**
         * To draw the recorded voice in a visualization way
         * in the pre-created canvas and surface. The drawing
         * method of the frequency based on the audiorecorder
         * mediarecorder option class.Passing down the parameter
         * canvas as an input for this function.
         *
         * @param paramCanvas
         */
        public void doDraw(Canvas paramCanvas) {
            if (mCanvasHeight == 1)
                mCanvasHeight = paramCanvas.getHeight();
            paramCanvas.drawPaint(mBackPaint);
            int height = paramCanvas.getHeight();
            int BuffIndex = (mBuffer.length / 2 - paramCanvas.getWidth()) / 2;
            int width = paramCanvas.getWidth();
            int mBuffIndex = BuffIndex;
            int scale = height / m_iScaler;
            int StratX = 0;
            if (StratX >= width) {
                paramCanvas.save();
                return;
            }
            int cu1 = 0;
            while (StratX < width - 1) {
                int StartBaseY = mBuffer[(mBuffIndex - 1)] / scale;
                int StopBaseY = mBuffer[mBuffIndex] / scale;
                if (StartBaseY > height / 2) {
                    StartBaseY = 2 + height / 2;
                    int checkSize = height / 2;
                    if (StopBaseY <= checkSize)
                        return;
                    StopBaseY = 2 + height / 2;
                }
                int StartY = StartBaseY + height / 2;
                int StopY = StopBaseY + height / 2;
                paramCanvas.drawLine(StratX, StartY, StratX + 1, StopY, mLinePaint);
                cu1++;
                mBuffIndex++;
                StratX++;
                int checkSize_again = -1 * (height / 2);
                if (StopBaseY >= checkSize_again)
                    continue;
                StopBaseY = -2 + -1 * (height / 2);
            }
        }

        /**
         * To return the value of the m_bRun variable
         *
         * @return
         */
        public Boolean getRun() {
            return m_bRun;
        }

        /**
         * To draw the surface, surface holder, canvas
         * and continue drawing recorded sound in a
         * visualization pane
         */
        public void run() {
            while (true) {
                if (!m_bRun.booleanValue()) {
                    m_bDead = Boolean.valueOf(true);
                    m_bDead2 = Boolean.valueOf(true);
                    System.out.println("Goodbye Drawthread");
                    return;
                }
                Canvas localCanvas = null;
                try {
                    localCanvas = mSurfaceHolder.lockCanvas(null);
                    synchronized (mSurfaceHolder) {
                        if (localCanvas != null)
                            doDraw(localCanvas);
                    }
                } finally {
                    if (localCanvas != null)
                        mSurfaceHolder.unlockCanvasAndPost(localCanvas);
                }
            }
        }

        /**
         * To set the buffer using the paramArrayOfShort
         * synchronized with the mBuffer variable.
         *
         * @param paramArrayOfShort
         */
        public void setBuffer(short[] paramArrayOfShort) {
            synchronized (mBuffer) {
                mBuffer = paramArrayOfShort;
                return;
            }
        }

        /**
         * To set the m_bRun variable value.
         *
         * @param paramBoolean
         */
        public void setRun(Boolean paramBoolean) {
            m_bRun = paramBoolean;
        }

        /**
         * To set the surface height and width including a background image.
         *
         * @param paramInt1
         * @param paramInt2
         */
        public void setSurfaceSize(int paramInt1, int paramInt2) {
            synchronized (mSurfaceHolder) {
                mCanvasWidth = paramInt1;
                mCanvasHeight = paramInt2;
                mBackgroundImage = Bitmap.createScaledBitmap(mBackgroundImage, paramInt1, paramInt2, true);
                return;
            }
        }
    }
}