package com.enighma.timesheeter.model;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.CalendarContract;

import com.enighma.timesheeter.util.TimeUtil;

import java.util.concurrent.TimeUnit;

public class CalendarEvent {

    public static final String TIMEZONE = "Europe/Stockholm";
    private static final String GLOBAL_TITLE = "Andreas Work Timestamp";

    private Long mCalendarId;
    private Long mEventId;

    private Long mProjectId;

    private long mTimestamp;

    /**
     * id from db
     */
    private long id;

    public CalendarEvent(long calendarId, long timestamp) {
        mTimestamp = timestamp;
        // TODO remove hardcoding..
        mCalendarId = 0L;
    }


    // TODO should only be done after it has been stored in the db.. FORCE this behavior
    public Long sync(Context context) {

        ContentResolver cr = context.getContentResolver();
        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, generateContentValues());
        if(uri != null) mEventId = Long.parseLong(uri.getLastPathSegment());

        return mEventId;

    }

    public boolean isSynchronized() {
        return mEventId != null;
    }

    public ContentValues generateContentValues(){
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, mTimestamp);
        values.put(CalendarContract.Events.DTEND, mTimestamp + TimeUnit.MINUTES.toMillis(30) );
        values.put(CalendarContract.Events.TITLE, GLOBAL_TITLE);
//        values.put(CalendarContract.Events.DESCRIPTION, GLOBAL_DESCRIPTION);
        values.put(CalendarContract.Events.CALENDAR_ID, mCalendarId);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, TIMEZONE);

        return values;
    }

    public int getWeekNo() {
        return TimeUtil.getWeekNumber(mTimestamp);
    }

    public Long getCalendarId() {
        return mCalendarId;
    }

    public Long getEventId() {
        return mEventId;
    }

    public Long getProjectId() {
        return mProjectId;
    }

    public long getTimestamp() {
        return mTimestamp;
    }

    public long getId() {
        return id;
    }
}
