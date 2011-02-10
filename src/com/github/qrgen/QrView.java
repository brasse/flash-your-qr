package com.github.qrgen;

import com.google.zxing.qrcode.encoder.ByteMatrix;

import android.content.Context;
import android.graphics.Canvas;
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
        QrDraw.draw(canvas, code, getPaddingLeft(), getPaddingRight(),
                    getPaddingTop(), getPaddingBottom());
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
