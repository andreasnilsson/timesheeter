package com.enighma.timesheeter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.enighma.timesheeter.model.CalendarDay;
import com.enighma.timesheeter.model.Day;

/**
 * For saving links between the calendar events created from this app
 */
public class CalendarOpenHelper extends SQLiteOpenHelper {
    private static final String LOG_TAG = CalendarOpenHelper.class.getName();

    private static final int DATABASE_VERSION       = 1;
    private static final String DATABASE_NAME       = "timsheeter";

    // Tables
    private static final String TABLE_EVENT         = "day";

    // Columns
    private static final String KEY_ID              = "id";
    private static final String KEY_CALENDAR_ID     = "calendar_id";
    private static final String KEY_TIMESTAMP_START = "start";
    private static final String KEY_TIMESTAMP_END   = "end";
    private static final String KEY_PAUSES          = "pauses";
    private static final String KEY_WEEK_NO         = "week_no";

    public CalendarOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO testing, remove

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENT);

        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_EVENT + "("
                + KEY_ID                + " INTEGER PRIMARY KEY,"
                + KEY_CALENDAR_ID       + " INTEGER,"
                + KEY_TIMESTAMP_START   + " INTEGER,"
                + KEY_TIMESTAMP_END     + " INTEGER,"
                + KEY_PAUSES            + " INTEGER,"
                + KEY_WEEK_NO           + " INTEGER"
                + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENT);

        // Create tables again
        onCreate(db);
    }

    public void addDay(int calendarId, long id, Day day){
        Log.d(LOG_TAG, "adding event with id: " + id);
        // TODO do in bg..
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, id);
        values.put(KEY_CALENDAR_ID, calendarId);
        values.put(KEY_TIMESTAMP_START, day.getStart());
        values.put(KEY_TIMESTAMP_END, day.getEnd());
        values.put(KEY_PAUSES, day.getPauses());
        values.put(KEY_WEEK_NO, day.getWeekNo());

        // TODO
        // Inserting Row
        db.insert(TABLE_EVENT, null, values);
        db.close(); // Closing database connection
    }

    public void addCalendarDay(long eventID, CalendarDay day){
        addDay(day.getCalenderId(), eventID, day);
    }



    public int getNoDays(){
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_EVENT;

        final Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor.getCount();

    }

    public void deleteDay(int eventId){
        // TODO
    }

    public void deleteAll(){
        // TODO
        // get all ids from local db

        // delete form calender using content resolver

    }

    /**
     * Synchronizes the apps saved time slots with the
     *
     * @param fullSync If {@code true} not only events but their metadata will be synced.
     */
    public void syncWithCalendar(boolean fullSync){
        // TODO
        // Always use the google calenders data


    }
}
