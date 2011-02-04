package com.github.qrgen;

import com.google.zxing.qrcode.encoder.ByteMatrix;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class QrView extends View {

    private static final String TAG = "qrgen";

    private ByteMatrix code;

    public QrView(Context ctx) {
        super(ctx);
    }

    public QrView(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public void setQrCode(ByteMatrix code) {
        this.code = code;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRGB(255, 255, 255);

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);

        Rect clip = canvas.getClipBounds();
        int codeWidth = code.getWidth();
        int codeHeight = code.getHeight();
        int width = clip.width() - getPaddingLeft() - getPaddingRight();
        int height = clip.height() - getPaddingTop() - getPaddingBottom();
        float cellWidth = width / (float)codeWidth;
        float cellHeight = height / (float)codeHeight;
        Log.i(TAG, "cw: " + cellWidth + " ch: " + cellHeight);
        int x0 = getPaddingLeft();
        int y0 = getPaddingTop();

        RectF cell = new RectF(x0, y0, x0 + cellWidth, y0 + cellHeight);
        for (int x = 0; x < codeWidth; ++x) {
            for (int y = 0; y < codeHeight; ++y) {
                if (code.get(x, y) == 1) {
                    cell.offsetTo(x0 + x * cellWidth, y0 + y * cellHeight);
                    canvas.drawRect(cell, paint);
                }
            }
        }
        canvas.drawRect(cell, paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.i(TAG, "min w " +  getSuggestedMinimumWidth());
        Log.i(TAG, "min h " +  getSuggestedMinimumHeight());
        Log.i(TAG, "padding " + getPaddingTop());
        setMeasuredDimension(measureX(widthMeasureSpec),
                             measureX(heightMeasureSpec));
    }

    private int measureX(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        Log.i(TAG, MeasureSpec.toString(measureSpec));
        int x = 100;
        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
        } else {
            result = x;
            if (specMode == MeasureSpec.AT_MOST) {
                // Respect AT_MOST value if that was what is called for by measureSpec
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

}
