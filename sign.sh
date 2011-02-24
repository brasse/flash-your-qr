#!/bin/bash

APK=QrGen
jarsigner -verbose -keystore android-release-key.keystore \
    -signedjar bin/${APK}-unaligned.apk bin/${APK}-unsigned.apk thekey
zipalign -v 4 bin/${APK}-unaligned.apk bin/${APK}.apk
