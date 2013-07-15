package com.enighma.timesheeter.model;

import android.text.format.Time;


/**
 * All the data is wrapped in the {@link DayDAO}
 * Separates data in DAO and logic in here
 * <p/>
 * Created by Enighma on 2013-07-12.
 */
public class Day {

    /**
     * Start timestamp for a day
     */
    public Long mStart = 0L;

    /**
     * End timestamp for a day.
     */
    public Long mEnd = 0L;

    /**
     * The duration of mPauses in a day
     */
    public Long mPauses = 0L;


    public Day(long start, long end, long pauses) {
        mStart = start;
        mEnd = end;
        mPauses = pauses;
    }

    public long getStart() {
        return mStart;
    }

    public long getEnd() {
        return mEnd;
    }

    public void checkIn() {
        mStart = getNow();
    }

    public void checkOut() {
        mEnd = getNow();
    }

    public void setPausesDuration(long duration) {
        mPauses = duration;
    }

    public long getTotalWorkingTime() {
        return mEnd - mStart - mPauses;
    }

    boolean isCheckedIn() {
        return mStart != 0;
    }

    boolean isCheckedOut() {
        return mEnd != 0;
    }

    /**
     * @return {@code true} if checked in and out for the day
     */
    public boolean isFinished() {
        return isCheckedIn() && isCheckedOut();
    }

    public static long getNow() {
        Time t = new Time();
        t.setToNow();
        return t.toMillis(true);
    }

    public Long getPauses() {
        return mPauses;
    }

    public int getWeekNo() {
        Time t = new Time();
        t.set(mStart);

        return t.getWeekNumber();
    }


    public int getWeekDay() {
        Time t = new Time();
        t.set(mStart);
        return t.weekDay;
    }
}