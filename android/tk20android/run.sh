#!/bin/sh
./gradlew assembleDebug
adb install -r mobile/build/outputs/apk/mobile-debug.apk
