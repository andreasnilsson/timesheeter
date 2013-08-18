package com.enighma.timesheeter.service;

import android.app.IntentService;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.enighma.timesheeter.CalendarOpenHelper;
import com.enighma.timesheeter.Config;
import com.enighma.timesheeter.model.CalendarEvent;

import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class TimeSheetService extends IntentService {
    // defined ations.
    public static final String ACTION_NEW_EVENT = "com.enighma.timesheeter.action.NEW_EVENT";

    public static final String EXTRA_TIMESTAMP = "extra_timestamp";
    public static final String EXTRA_CALENDAR_ID = "extra_calendar_id";

    private final TimeSheetBinder mBinder;
    private CalendarOpenHelper mCalendarOpenHelper;

    private static Set<DataSetObserver> mObservers  = new HashSet<DataSetObserver>();

    public TimeSheetService() {
        super("TimeSheetService");
        mBinder = new TimeSheetBinder();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final String action = intent.getAction();

        if(ACTION_NEW_EVENT.equals(action)){
            final long timestamp = intent.getLongExtra(EXTRA_TIMESTAMP, Long.MIN_VALUE);
            int calendarId = intent.getIntExtra(EXTRA_TIMESTAMP, -1);

            if(timestamp != Long.MIN_VALUE) {
                if(calendarId == -1) {
                    // use default calendar
                    calendarId = 0;
                }

                createNewEvent(calendarId, timestamp);
            }
        }
    }

    public void createNewEvent(int calendarId, long timestamp) {
        final CalendarEvent calendarEvent = new CalendarEvent(calendarId, timestamp);
        mCalendarOpenHelper.store(calendarEvent);
        notifyObservers();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mCalendarOpenHelper = new CalendarOpenHelper(getApplicationContext());

        // TODO
        // check if we have a day today
        // else create one..
    }

    public void addTimeSheetObserver(DataSetObserver observer) {
        mObservers.add(observer);
    }

    public void removeDataSetObserver(DataSetObserver observer){
        mObservers.remove(observer);
    }

    // TODO better naming
    private void notifyObservers() {
        Log.d(Config.LOG_TAG, "Notifying observers");
        for(DataSetObserver o : mObservers){
            o.onChanged();
        }
    }

    public int getNoSavedDays() {
        return mCalendarOpenHelper.getNoDays();
    }

    public class TimeSheetBinder extends Binder {
        public TimeSheetService getService() {
            return TimeSheetService.this;
        }
    }

}
