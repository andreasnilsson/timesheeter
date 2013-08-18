package com.enighma.timesheeter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.CalendarContract;

import com.enighma.timesheeter.model.CalendarEvent;
import com.enighma.timesheeter.model.Day;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * For saving links between the calendar events created from this app
 */
public class CalendarOpenHelper extends SQLiteOpenHelper {
    private static final String LOG_TAG = CalendarOpenHelper.class.getName();

    private static final int DATABASE_VERSION       = 2;
    private static final String DATABASE_NAME       = "timesheeter";

    // Tables
    private static final String TABLE_TIMESTAMP     = "timestamp";

    // Columns
    private static final String KEY_ROW_ID          = "rowid";
    private static final String KEY_EVENT_ID        = "event_id";
    private static final String KEY_CALENDAR_ID     = "calendar_id";
    private static final String KEY_TIMESTAMP       = "timestamp";
    private static final String KEY_WEEK_NO         = "week_no";
    private Map<Long, CalendarEvent> mDayCache  = new HashMap<Long, CalendarEvent>();
    private Context mContext;

    public CalendarOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO testing, remove

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TIMESTAMP);

        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_TIMESTAMP + "("
                + KEY_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_EVENT_ID          + " INTEGER UNIQUE,"
                + KEY_CALENDAR_ID       + " INTEGER,"
                + KEY_TIMESTAMP         + " INTEGER,"
                + KEY_WEEK_NO           + " INTEGER"
                + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TIMESTAMP);

        // Create tables again
        onCreate(db);
    }

    /**
     * Adds day to calendar and internal sqllite db
     * should be called from the bg.. TODO All here from the bg..
     */
    public boolean store(@NotNull CalendarEvent timeStamp){
        SQLiteDatabase db = getWritableDatabase();

        // first sync with calendar to get eventId
        final Long eventId = timeStamp.sync(mContext);

        if(eventId != null && db != null) {
            ContentValues values = new ContentValues();
            values.put(KEY_CALENDAR_ID, timeStamp.getCalendarId());
            values.put(KEY_TIMESTAMP, timeStamp.getTimestamp());
            values.put(KEY_WEEK_NO, timeStamp.getWeekNo());
            values.put(KEY_EVENT_ID, eventId);

            // TODO
            // Inserting Row

            long rowId = db.insert(TABLE_TIMESTAMP, null, values);

            // cache day..

            mDayCache.put(rowId, timeStamp);

            db.close();

            return true;
        }

        return false;
    }

    public int getNoDays(){
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_TIMESTAMP;

        if(db != null) {
            return db.rawQuery(selectQuery, null).getCount();
        }

        return 0;
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
        // TODO
    }

//    private @NotNull CalendarDay getCalendarDayFromCursor(@NotNull Cursor cursor,
//                                                          boolean closeCursor) {
//        long rowId = -1;
//        int calendarId= -1;
//        long start= -1;
//        long end= -1;
//        long pauses= -1;
//
//        String[] columnNames = cursor.getColumnNames();
//        for(String n : columnNames){
//            if (KEY_TIMESTAMP.equals(n)) {
//                start  = cursor.getLong(cursor.getColumnIndexOrThrow(n));
//            }else if(KEY_ROW_ID.equals(n)) {
//                rowId  = cursor.getLong(cursor.getColumnIndexOrThrow(n));
//            }else if(KEY_CALENDAR_ID.equals(n)) {
//                calendarId  = cursor.getInt(cursor.getColumnIndexOrThrow(n));
//        }
//
//        // TODO assert they are not null
//
//        if(closeCursor) cursor.close();
//
//        return new CalendarDay(rowId, calendarId, new Day(start, end, pauses));
//    }

    /**
     *
     * @param timestamp
     * @return The rowid or -1 if not found.
     */
    private long getRowId(long timestamp) {
        long rowId = -1;

        SQLiteDatabase db = getReadableDatabase();

        if(db != null) {
            String[] columns = {KEY_ROW_ID}; // all rows
            String selection = KEY_TIMESTAMP + " = " + timestamp;
            Cursor query = db.query(TABLE_TIMESTAMP, columns, selection, null, null, null, null);

            // return the first entry
            query.moveToNext();

            int columnIndex = query.getColumnIndexOrThrow(KEY_ROW_ID);
            if(columnIndex != -1){
                rowId = query.getLong(columnIndex);
            }

            query.close();

        }


        return rowId;
    }

    public CalendarDay getDay(long timestamp){
        return null;
    }

    public void updateDay(CalendarDay today) {
        // Find day and update row
    }

//    public List<CalendarDay> getAllDays() {
//        final ArrayList<CalendarDay> allDays = new ArrayList<CalendarDay>();
//
//        SQLiteDatabase db = getReadableDatabase();
//        String selectQuery = "SELECT * FROM " + TABLE_TIMESTAMP;
//
//        final Cursor cursor = db.rawQuery(selectQuery, null);
//
//        while(cursor.moveToNext()) {
//            allDays.add(getTimeSlot(cursor, false));
//        }
//
//        cursor.close();
//
//        return allDays;
//    }

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
