package com.enighma.timesheeter.util;

import android.util.Log;

import com.enighma.timesheeter.Config;

public class Logg {
    public static final Logg LOGG = new Logg(Config.LOG_TAG);
    private String mLogTag;

    public Logg(String logTag) {
        mLogTag = logTag;
    }

    public void d(String message) {
        Log.d(mLogTag, message);
    }
}
