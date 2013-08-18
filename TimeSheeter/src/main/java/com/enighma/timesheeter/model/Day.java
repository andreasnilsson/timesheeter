package com.enighma.timesheeter.model;

import android.text.format.Time;

import com.enighma.timesheeter.util.TimeUtil;


public class Day {

    /**
     * Start timestamp for a day
     */
    public Long mStart;

    /**
     * End timestamp for a day.
     */
    public Long mEnd;

    /**
     * The duration of mPauses in a day
     */
    public Long mPauses;


    public Day(Long start, Long end, Long pauses) {
        mStart = start;
        mEnd = end;
        mPauses = pauses;
    }

    public Day(Day day) {
        this.mStart = day.mStart;
        this.mEnd = day.mEnd;
        this.mPauses = day.mPauses;
    }

    public Day() {
        // Day without any initialized attribues.. that is not eched in or anything
    }

    public Long getStart() {
        return mStart;
    }

    public Long getEnd() {
        return mEnd;
    }

    public void checkIn() {
        mStart = TimeUtil.getNow();
    }

    public void checkOut() {
        mEnd = TimeUtil.getNow();
    }

    public void setPausesDuration(long duration) {
        mPauses = duration;
    }

    public Long getTotalWorkingTime() {
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

    public void setStart(Long start) {
        mStart = start;
    }
}
