package it.jaschke.alexandria.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.Checkable;
import android.widget.CompoundButton;

import it.jaschke.alexandria.R;
import it.jaschke.alexandria.utils.ColorUtils;

/**
 * Created by Paulina on 2015-10-28.
 */
public class ColorToggleButton extends CompoundButton {

    private static final int STROKE_WIDTH_DIP = 4;
    private static final int STROKE_COLOR_TRANSLATION = -20;

    private static final int CHECK_MARK_COLOR = Color.WHITE;
    private static final int CHECK_MARK_WIDTH_DIP = 3;
    private static final int CHECK_MARK_PADDING_DIP = 8;

    private static final PointF A = new PointF(0.10f, 0.58f);
    private static final PointF B = new PointF(0.33f, 0.80f);
    private static final PointF C = new PointF(0.92f, 0.22f);

    private int circleColor = Color.parseColor("#666666");
    private int strokeColor = Color.parseColor("#525252");
    private boolean checked;

    private float strokeWidth;
    private float checkmarkWidth;
    private float checkmarkPadding;
    private Paint paint;

    public ColorToggleButton(Context context) {
        super(context);
        initHelpers();
        setClickable(true);
    }

    public ColorToggleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadAttrs(attrs);
        initHelpers();
        setClickable(true);
    }

    public ColorToggleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        loadAttrs(attrs);
        initHelpers();
        setClickable(true);
    }

    private void loadAttrs(AttributeSet attrs) {
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.ColorToggleButton, 0, 0);

        setCircleColor(array.getColor(R.styleable.ColorToggleButton_color, circleColor));

        array.recycle();
    }

    private void initHelpers() {
        strokeWidth = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, STROKE_WIDTH_DIP,
                getResources().getDisplayMetrics());

        checkmarkWidth = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, CHECK_MARK_WIDTH_DIP,
                getResources().getDisplayMetrics());

        checkmarkPadding = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, CHECK_MARK_PADDING_DIP,
                getResources().getDisplayMetrics());

        paint = new Paint();
        paint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (checked) {
            paint.setColor(circleColor);
            paint.setStyle(Paint.Style.FILL);
            drawMaxSizeCircle(canvas, paint);
        }

        paint.setColor(strokeColor);
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Paint.Style.STROKE);
        drawMaxSizeCircle(canvas, paint);

        if (checked) {
            paint.setStrokeWidth(checkmarkWidth);
            paint.setColor(CHECK_MARK_COLOR);
            drawCheckMark(canvas, paint);
        }
    }

    private void drawMaxSizeCircle(Canvas canvas, Paint paint) {
        float cx = (getRight() - getLeft()) / 2f;
        float cy = (getBottom() - getTop()) / 2f;
        canvas.drawCircle(cx, cy, Math.min(cx, cy) - strokeWidth / 2f, paint);
    }

    private void drawCheckMark(Canvas canvas, Paint paint) {
        float cx = (getRight() - getLeft()) / 2f;
        float cy = (getBottom() - getTop()) / 2f;
        float squareSize = Math.min(cx * 2f, cy * 2f) - 2 * checkmarkPadding;

        float offset = (float) (checkmarkWidth / (2f * Math.sqrt(2)));

        canvas.drawLine(
                cx - (0.5f - A.x) * squareSize - offset,
                cy - (0.5f - A.y) * squareSize - offset,
                cx - (0.5f - B.x) * squareSize + offset,
                cy - (0.5f - B.y) * squareSize + offset,
                paint);

        canvas.drawLine(
                cx - (0.5f - B.x) * squareSize - offset,
                cy - (0.5f - B.y) * squareSize + offset,
                cx - (0.5f - C.x) * squareSize + offset,
                cy - (0.5f - C.y) * squareSize - offset,
                paint);
    }


    @Override
    public void setChecked(boolean checked) {
        this.checked = checked;
        invalidate();
    }

    @Override
    public boolean isChecked() {
        return checked;
    }

    @Override
    public void toggle() {
        checked = !checked;
    }


    public void setCircleColor(@ColorInt int circleColor) {
        this.circleColor = circleColor;
        this.strokeColor = ColorUtils.getColorWithTranslateBrightness(
                circleColor, STROKE_COLOR_TRANSLATION);
    }

    public int getCircleColor() {
        return circleColor;
    }
}
