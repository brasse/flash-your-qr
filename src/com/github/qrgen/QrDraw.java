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

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

public class QrDraw {
    private static final String TAG = "qrgen";

    public static void draw(Canvas canvas, ByteMatrix code,
                            int paddingLeft, int paddingRight,
                            int paddingTop, int paddingBottom) {
        canvas.drawColor(Color.WHITE);

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);

        Rect clip = canvas.getClipBounds();
        int codeWidth = code.getWidth();
        int codeHeight = code.getHeight();
        int width = clip.width() - paddingLeft - paddingRight;
        int height = clip.height() - paddingTop - paddingBottom;
        float cellWidth = width / (float)codeWidth;
        float cellHeight = height / (float)codeHeight;
        Log.i(TAG, "cw: " + cellWidth + " ch: " + cellHeight);
        int x0 = paddingLeft;
        int y0 = paddingTop;

        RectF cell = new RectF(x0, y0, x0 + cellWidth, y0 + cellHeight);
        for (int x = 0; x < codeWidth; ++x) {
            for (int y = 0; y < codeHeight; ++y) {
                if (code.get(x, y) == 1) {
                    cell.offsetTo(x0 + x * cellWidth, y0 + y * cellHeight);
                    canvas.drawRect(cell, paint);
                }
            }
        }
    }
}
