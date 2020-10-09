package com.huawei.scankit.java.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;
import com.huawei.scankit.java.R;

public class BardCodeView extends View {

    private Rect[] borderRectangles;
    private Paint strokePaint;

    public BardCodeView(Context context) {
        super(context);
        initPaint();
    }

    public BardCodeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    private void initPaint() {
        strokePaint = new Paint();
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setColor(getResources().getColor(R.color.colorAccent));
        strokePaint.setStrokeWidth(2);
    }

    public void setBorderRectangles(Rect[] rectangles) {
        borderRectangles = rectangles;
        invalidate();
    }

    public void clear() {
        borderRectangles = new Rect[]{};
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (borderRectangles == null) {
            return;
        }

        for (Rect rect: borderRectangles) {
            canvas.drawRect(rect, strokePaint);
        }
    }
}
