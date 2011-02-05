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

    @Override
    public void onStart() {
        super.onStart();
        try {
            QRCode code = new QRCode();
            String url = "http://slashdot.com";
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                Log.i(TAG, "found extras");
                url = extras.getString("qrdata");
            } else {
                Log.i(TAG, "no extras");
            }
            Encoder.encode(url, ErrorCorrectionLevel.L, code);    
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
