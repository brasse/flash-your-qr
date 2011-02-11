/*
 * Copyright 2011 Mattias Svala
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
