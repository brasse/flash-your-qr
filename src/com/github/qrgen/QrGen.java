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

import com.google.zxing.WriterException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.Encoder;
import com.google.zxing.qrcode.encoder.QRCode;

import android.app.Activity;
import android.util.Log;
import android.os.Bundle;

public class QrGen extends Activity
{
    private static final String TAG = "qrgen";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    private String getQrData(Bundle extras) {
        String source = extras.getString("source");
        String data = extras.getString("qrdata");
        if (WidgetConfigure.SOURCE_MANUAL.equals(source)) {
            return data;
        } else {
            assert WidgetConfigure.SOURCE_CONTACT.equals(source);
            try {
                return Contact.getVCard(this, data);
            } catch (Exception fe) {
                return "vCard failed";
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            QRCode code = new QRCode();
            String qrData = "http://slashdot.com";
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                Log.i(TAG, "found extras");
                // qrData = extras.getString("qrdata");
                qrData = getQrData(extras);
            } else {
                Log.i(TAG, "no extras");
            }
            Encoder.encode(qrData, ErrorCorrectionLevel.L, code);
            QrView qrView = (QrView)findViewById(R.id.qr);
            if (qrView == null) {
                Log.i(TAG, "view is null");
            }
            if (code == null) {
                Log.i(TAG, "code is null");
            }
            qrView.setQrCode(code.getMatrix());
        } catch (WriterException e) {
            Log.e(TAG, "Failed to generate QR.", e);
        }
    }
}
