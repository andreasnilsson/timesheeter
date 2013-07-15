package com.enighma.timesheeter.model;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.enighma.timesheeter.CalendarOpenHelper;

import static android.provider.CalendarContract.Events;

/**
 * Created by Andreas Nilsson on 2013-07-14.
 */
public class CalendarDay extends Day {
    private static final String LOG_TAG = CalendarDay.class.getName();

    private static String GLOBAL_TITLE       = "Andreas";
    private static String GLOBAL_DESCRIPTION = "Working day";
    private static String TIMEZONE           = "Europe/Stockholm";

    private int mCalendarId;


    public CalendarDay(int calendarId, long start, long end, long pauses) {
        super(start, end, pauses);
        mCalendarId = calendarId;
    }

    public int getCalenderId() {
        return mCalendarId;
    }

    public ContentValues generateContentValues(){
        int localTitle = getWeekDay();
        ContentValues values = new ContentValues();
        values.put(Events.DTSTART, mStart);
        values.put(Events.DTEND, mEnd);
        values.put(Events.TITLE, GLOBAL_TITLE + " - " + localTitle);
        values.put(Events.DESCRIPTION, GLOBAL_DESCRIPTION);
        values.put(Events.CALENDAR_ID, mCalendarId);
        values.put(Events.EVENT_TIMEZONE, TIMEZONE);

        return values;
    }

    // TODO should be done in bg
    public void saveToCalendar(Context context) {
        ContentResolver cr = context.getContentResolver();

        Uri uri = cr.insert(Events.CONTENT_URI, generateContentValues());
        long eventID = Long.parseLong(uri.getLastPathSegment());

        CalendarOpenHelper eventHelper = new CalendarOpenHelper(context);
        eventHelper.addCalendarDay(eventID, this);

        Log.d(LOG_TAG, "Saved to calendar");
    }
}
