package com.example.test;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import java.util.logging.Logger;

/**
 * @author R.K.Opatha (IT13510572)
 *         Use to handle the circular decibel meter
 * @see android.os.IInterface
 */
public class Speedometer extends View implements SpeedChangeListener {
    private static final String TAG = Speedometer.class.getSimpleName();
    public static final float DEFAULT_MAX_SPEED = 110; // Assuming this is km/h and you drive a super-car
    private final static Logger LOGGER = Logger.getLogger(SingleConversationActivity.class.getName());
    // Speedometer internal state
    private float mMaxSpeed;
    private float mCurrentSpeed;

    // Scale drawing tools
    private Paint onMarkPaint;
    private Paint offMarkPaint;
    private Paint scalePaint;
    private Paint readingPaint;
    private Path onPath;
    private Path offPath;
    final RectF oval = new RectF();

    // Drawing colors
    private int ON_COLOR = Color.argb(255, 0xff, 0xA5, 0x00);
    private int OFF_COLOR = Color.argb(255, 0x3e, 0x3e, 0x3e);
    private int SCALE_COLOR = Color.argb(255, 255, 255, 255);
    private float SCALE_SIZE = 12f;
    private float READING_SIZE = 60f;

    // Scale configuration
    private float centerX;
    private float centerY;
    private float radius;

    /**
     * Use to call the super context
     *
     * @param context
     */
    public Speedometer(Context context) {
        super(context);
    }

    /**
     * USe contruct the speedometer
     *
     * @param context
     * @param attrs
     */
    public Speedometer(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.Speedometer,
                0, 0);
        try {
            LOGGER.info("Initializing the speedometer");
            mMaxSpeed = a.getFloat(R.styleable.Speedometer_maxSpeed, DEFAULT_MAX_SPEED);
            mCurrentSpeed = a.getFloat(R.styleable.Speedometer_currentSpeed, 0);
            ON_COLOR = a.getColor(R.styleable.Speedometer_onColor, ON_COLOR);
            OFF_COLOR = a.getColor(R.styleable.Speedometer_offColor, OFF_COLOR);
            SCALE_COLOR = a.getColor(R.styleable.Speedometer_scaleColor, SCALE_COLOR);
            SCALE_SIZE = a.getDimension(R.styleable.Speedometer_scaleTextSize, SCALE_SIZE);
            READING_SIZE = a.getDimension(R.styleable.Speedometer_readingTextSize, READING_SIZE);
        } catch (Exception e) {
            LOGGER.warning("Initializing the speedometer went wrong");
        } finally {
            a.recycle();
        }
        initDrawingTools();
    }

    /**
     * Use to set the background styles in the speedometer
     */
    private void initDrawingTools() {
        onMarkPaint = new Paint();
        onMarkPaint.setStyle(Paint.Style.STROKE);
        onMarkPaint.setColor(ON_COLOR);
        onMarkPaint.setStrokeWidth(35f);
        onMarkPaint.setShadowLayer(5f, 0f, 0f, ON_COLOR);
        onMarkPaint.setAntiAlias(true);

        offMarkPaint = new Paint(onMarkPaint);
        offMarkPaint.setColor(OFF_COLOR);
        offMarkPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        offMarkPaint.setShadowLayer(0f, 0f, 0f, OFF_COLOR);

        scalePaint = new Paint(offMarkPaint);
        scalePaint.setStrokeWidth(1f);
        scalePaint.setTextSize(SCALE_SIZE);
        scalePaint.setShadowLayer(5f, 0f, 0f, Color.RED);
        scalePaint.setColor(SCALE_COLOR);

        readingPaint = new Paint(scalePaint);
        readingPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        offMarkPaint.setShadowLayer(3f, 0f, 0f, Color.WHITE);
        readingPaint.setTextSize(26f);
        readingPaint.setTypeface(Typeface.SANS_SERIF);
        readingPaint.setColor(Color.WHITE);

        onPath = new Path();
        offPath = new Path();
    }

    /**
     * Use to get the current position in the speedometer
     *
     * @return
     */
    public float getCurrentSpeed() {
        return mCurrentSpeed;
    }

    /**
     * Setting the position in the speedometer
     *
     * @param mCurrentSpeed
     */
    public void setCurrentSpeed(float mCurrentSpeed) {
        if (mCurrentSpeed > this.mMaxSpeed)
            this.mCurrentSpeed = mMaxSpeed;
        else if (mCurrentSpeed < 55)
            this.mCurrentSpeed = 55;
        else
            this.mCurrentSpeed = mCurrentSpeed;
    }

    /**
     * Use to draw the speedometer background differentiating the points.
     *
     * @param width
     * @param height
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {

        // Setting up the oval area in which the arc will be drawn
        if (width > height) {
            radius = height / 4;
        } else {
            radius = width / 4;
        }
        oval.set(centerX - radius,
                centerY - radius,
                centerX + radius,
                centerY + radius);
    }

    /**
     * Use to define the measurements of the speedometer.
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		Log.d(TAG, "Width spec: " + MeasureSpec.toString(widthMeasureSpec));
//		Log.d(TAG, "Height spec: " + MeasureSpec.toString(heightMeasureSpec));

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int chosenWidth = chooseDimension(widthMode, widthSize);
        int chosenHeight = chooseDimension(heightMode, heightSize);

        int chosenDimension = Math.min(chosenWidth, chosenHeight);
        centerX = chosenDimension / 2;
        centerY = chosenDimension / 2;
        setMeasuredDimension(chosenDimension, chosenDimension);
    }

    /**
     * USe to choose the dimension
     *
     * @param mode
     * @param size
     * @return
     */
    private int chooseDimension(int mode, int size) {
        if (mode == MeasureSpec.AT_MOST || mode == MeasureSpec.EXACTLY) {
            return size;
        } else { // (mode == MeasureSpec.UNSPECIFIED)
            return getPreferredSize();
        }
    }

    /**
     * USe to define the size, incase it is not predefined.
     *
     * @return
     */
    private int getPreferredSize() {
        return 110;
    }

    /**
     * Draw the canvas including the speedometer.
     *
     * @param canvas
     */
    @Override
    public void onDraw(Canvas canvas) {
        drawScaleBackground(canvas);
        drawScale(canvas);
        drawLegend(canvas);
        drawReading(canvas);
    }

    /**
     * Draws the segments in their OFF state
     *
     * @param canvas
     */
    private void drawScaleBackground(Canvas canvas) {
        offPath.reset();
        for (int i = -180; i < 0; i += 4) {
            offPath.addArc(oval, i, 2f);
        }
        canvas.drawPath(offPath, offMarkPaint);
    }

    /**
     * Draw scaling points
     *
     * @param canvas
     */
    private void drawScale(Canvas canvas) {
        onPath.reset();
        System.out.println(mMaxSpeed + "       " + mCurrentSpeed);
        float sbc = ((mMaxSpeed - mCurrentSpeed) / 55) * (-180);
        System.out.println(sbc);
        int subs = (int) sbc;

        System.out.println(subs);
        for (int i = -180; i < subs; i += 5) {
            onPath.addArc(oval, i, 2f);
        }
        canvas.drawPath(onPath, onMarkPaint);
    }

    /**
     * Draw the boundary labels of the speedometer.
     *
     * @param canvas
     */
    private void drawLegend(Canvas canvas) {
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.rotate(-180, centerX, centerY);
        Path circle = new Path();
        double halfCircumference = radius * Math.PI;
        int l = 0;
        double increments = 11;
        String hearLevelArray[] = {"Calm Breath", "Calm Breath", "Whisper",
                "Conversations", "Rain fall", "TV Sound", "Vacume Cleaner",
                "Busy Traffic", "Lawnmower", "Jet ski", "Power saw"};
        String levelss[] = {"10", "20", "30", "40", "50", "60", "70", "80", "90", "100", "110"};
        for (int i = 0; i <= this.mMaxSpeed; i += increments) {
            l = (i) / 11;
            circle.addCircle(centerX, centerY, radius, Path.Direction.CW);
            canvas.drawTextOnPath(levelss[l],
                    circle,
                    (float) (i * halfCircumference / this.mMaxSpeed),
                    -30f,
                    scalePaint);

        }

        canvas.restore();
    }

    /**
     * Use to draw the reading with the hearing level,
     * displaying the environmental noise levels.
     *
     * @param canvas
     */
    private void drawReading(Canvas canvas) {
        Path path = new Path();
        String hearLevelArray[] = {"Calm Breath", "Calm Breath", "Whisper",
                "Conversations", "Rain fall", "TV Sound", "Vacume Cleaner",
                "Busy Traffic", "Lawnmower", "Jet ski", "Power saw"};
        String levelss[] = {"10", "20", "30", "40", "50", "60", "70", "80", "90", "100", "110"};
        int indexArr = 0;
        for (int i = 0; i < levelss.length; i++) {
            if (Integer.parseInt(levelss[i]) == (int) this.mCurrentSpeed) {
                indexArr = i;
            }
        }
        String message = hearLevelArray[indexArr];
        float[] widths = new float[message.length()];
        readingPaint.getTextWidths(message, widths);
        float advance = 0;
        for (double width : widths)
            advance += width;
        path.moveTo(centerX - advance / 2, centerY);
        path.lineTo(centerX + advance / 2, centerY);
        canvas.drawTextOnPath(message, path, 0f, 0f, readingPaint);
    }

    /**
     * Use to mention the speed change option
     *
     * @param newSpeedValue
     */
    @Override
    public void onSpeedChanged(int newSpeedValue) {
        int[] decibels = {55, 60, 65, 70, 75, 80, 85, 90, 95, 100, 110};

        this.setCurrentSpeed(decibels[newSpeedValue]);
        this.invalidate();
    }
}
