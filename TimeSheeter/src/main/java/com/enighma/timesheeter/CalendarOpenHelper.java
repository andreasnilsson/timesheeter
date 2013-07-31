package com.enighma.timesheeter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.CalendarContract;

import com.enighma.timesheeter.model.Day;

import org.jetbrains.annotations.NotNull;

/**
 * For saving links between the calendar events created from this app
 */
public class CalendarOpenHelper extends SQLiteOpenHelper {
    private static final String LOG_TAG = CalendarOpenHelper.class.getName();

    private static final int DATABASE_VERSION       = 1;
    private static final String DATABASE_NAME       = "timesheeter";

    // Tables
    private static final String TABLE_EVENT         = "day";

    // Columns
    private static final String KEY_ROW_ID = "rowid";
    private static final String KEY_EVENT_ID        = "event_id";
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
                + KEY_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_EVENT_ID          + " INTEGER UNIQUE,"
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

    /**
     * Adds day to calendar and internal sqllite db
     * should be called from the bg.. TODO All here from the bg..
     * @param day
     * @param calendarId
     */
    public CalendarDay createDay(@NotNull Day day, int calendarId){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CALENDAR_ID,     calendarId);
        values.put(KEY_TIMESTAMP_START, day.getStart());
        values.put(KEY_TIMESTAMP_END,   day.getEnd());
        values.put(KEY_PAUSES,          day.getPauses());
        values.put(KEY_WEEK_NO,         day.getWeekNo());

        // TODO
        // Inserting Row
        assert db != null;
        long rowId = db.insert(TABLE_EVENT, null, values);
        db.close(); // Closing database connection

        // sync day to calendar

        return new CalendarDay(rowId, calendarId, day);
    }

    public void addDay(CalendarDay day){
        createDay(day, day.getCalenderId());
    }

    public int getNoDays(){
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_EVENT;

        final Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor.getCount();

    }

    public void deleteDay(long timestamp){
        // TODO
    }

    public void deleteDay(Day day){
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
        // generate eventId's here
        // TODO
        // Always use the google calenders data

        // for each day
//        ContentResolver cr = context.getContentResolver();
//
//        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, generateContentValues());
//        long id = Long.parseLong(uri.getLastPathSegment());
//
//        CalendarOpenHelper eventHelper = new CalendarOpenHelper(context);
//        eventHelper.createDay(this, id);
//
//        Log.d(LOG_TAG, "Saved to calendar");


    }

    public CalendarDay getToday() {
        final boolean doCloseCursor = true;
        long timestamp = 0; // TODO fix today

        long rowId = getRowId(timestamp);

        if(rowId != -1) {
            // we have no day
            SQLiteDatabase db = getReadableDatabase();

            String[] columns = {
                    KEY_ROW_ID,
                    KEY_CALENDAR_ID,
                    KEY_TIMESTAMP_END,
                    KEY_TIMESTAMP_START,
                    KEY_PAUSES}; // all rows
            String selection = KEY_ROW_ID + " == "+  rowId;
            String[] args = null;
            Cursor query = db.query(TABLE_EVENT, columns, selection, args, null, null, null);


            return getCalendarDayFromCursor(query, doCloseCursor);
        }

        return null;
    }

    private @NotNull CalendarDay getCalendarDayFromCursor(@NotNull Cursor cursor,
                                                          boolean closeCursor) {
        long rowId = -1;
        int calendarId= -1;
        long start= -1;
        long end= -1;
        long pauses= -1;

        String[] columnNames = cursor.getColumnNames();
        for(String n : columnNames){
            if (KEY_TIMESTAMP_END.equals(n)) {
                end = cursor.getLong(cursor.getColumnIndexOrThrow(n));
            }else if (KEY_TIMESTAMP_START.equals(n)) {
                start  = cursor.getLong(cursor.getColumnIndexOrThrow(n));
            }else if(KEY_ROW_ID.equals(n)) {
                rowId  = cursor.getLong(cursor.getColumnIndexOrThrow(n));
            }else if(KEY_CALENDAR_ID.equals(n)) {
                calendarId  = cursor.getInt(cursor.getColumnIndexOrThrow(n));
            }else if(KEY_PAUSES.equals(n)) {
                pauses = cursor.getLong(cursor.getColumnIndexOrThrow(n));
            }
        }

        // TODO assert they are not null

        if(closeCursor) cursor.close();

        return new CalendarDay(rowId, calendarId, new Day(start, end, pauses));
    }

    /**
     *
     * @param timestamp
     * @return The rowid or -1 if not found.
     */
    private long getRowId(long timestamp) {
        long rowId = -1;

        SQLiteDatabase db = getReadableDatabase();

        String[] columns = {KEY_ROW_ID}; // all rows
        String selection = KEY_TIMESTAMP_START + " <  " + timestamp +
                " && " + KEY_TIMESTAMP_END + " >  " + timestamp;

        String[] args = null;
        Cursor query = db.query(TABLE_EVENT, columns, selection, args, null, null, null);

        // return the first entry
        query.moveToNext();

        int columnIndex = query.getColumnIndexOrThrow(KEY_ROW_ID);
        if(columnIndex != -1){
            rowId = query.getLong(columnIndex);
        }

        query.close();

        return rowId;
    }

    public CalendarDay getDay(long timestamp){
        return null;
    }

    public void updateDay(CalendarDay today) {
        // Find day and update row
    }

    public static class CalendarDay extends Day {
        private static final String LOG_TAG = CalendarDay.class.getName();

        private static String GLOBAL_TITLE       = "Andreas";
        private static String GLOBAL_DESCRIPTION = "Working day";
        private static String TIMEZONE           = "Europe/Stockholm";
        private final long mRowId;

        private int mCalendarId;


        public CalendarDay(long rowId, int calendarId, Day day) {
            super(day);

            mRowId = rowId;
            mCalendarId = calendarId;
        }

        public int getCalenderId() {
            return mCalendarId;
        }

        public ContentValues generateContentValues(){
            int localTitle = getWeekDay();
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Events.DTSTART, mStart);
            values.put(CalendarContract.Events.DTEND, mEnd);
            values.put(CalendarContract.Events.TITLE, GLOBAL_TITLE + " - " + localTitle);
            values.put(CalendarContract.Events.DESCRIPTION, GLOBAL_DESCRIPTION);
            values.put(CalendarContract.Events.CALENDAR_ID, mCalendarId);
            values.put(CalendarContract.Events.EVENT_TIMEZONE, TIMEZONE);

            return values;
        }

//        // TODO should be done in bg
//        public void saveToCalendar(Context context) {
//
//        }
    }
}
