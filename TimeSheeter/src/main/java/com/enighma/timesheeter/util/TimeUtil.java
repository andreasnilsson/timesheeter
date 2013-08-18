package com.enighma.timesheeter.util;


import android.text.format.Time;

public class TimeUtil {

    /**
     *
     * @return Current time in ms since EPOCH
     */
    public static Long getNow() {
        Time time = new Time();
        time.setToNow();

        // TODO ignore dst???
        return time.toMillis(true);
    }

    public static int getWeekNumber(long timestamp) {
        Time time = new Time();
        time.set(timestamp);
        return time.getWeekNumber();
    }
}
