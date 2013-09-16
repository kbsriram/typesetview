#!/bin/bash

$HOME/android-sdk-macosx/platform-tools/adb shell am instrument -w -e class org.kbsriram.android.typesetview.${1} org.kbsriram.android.example.tests/android.test.InstrumentationTestRunner
