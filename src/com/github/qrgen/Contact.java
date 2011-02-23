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

import android.content.Context;
import android.net.Uri;
import android.provider.ContactsContract.Contacts;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.IOException;

public class Contact {
    private static final String TAG = "qrgen";

    public static String getVCard(Context context, String lookupKey)
        throws FileNotFoundException, IOException {
        Uri vCardUri =
                Uri.withAppendedPath(Contacts.CONTENT_VCARD_URI, lookupKey);
        InputStreamReader in = new InputStreamReader(
                context.getContentResolver()
                .openAssetFileDescriptor(vCardUri, "r").createInputStream());
        StringBuilder sb = new StringBuilder();
        char[] buffer = new char[1000];
        int n;
        do {
            n = in.read(buffer, 0, buffer.length);
            if (n > 0) {
                sb.append(buffer, 0, n);
            }
        } while (n >= 0);
        return sb.toString();
    }
}
