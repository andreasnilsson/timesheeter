package com.enighma.timesheeter.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.enighma.timesheeter.CalendarOpenHelper;

import org.jetbrains.annotations.Nullable;

import static com.enighma.timesheeter.Config.LOG_TAG;

/**
 * Created by Enighma on 2013-07-25.
 */
public class TimeSheetService extends IntentService {
    // defined ations.
    public static final String ACTION_CHECK_IN = "com.enighma.timesheeter.action.CHECK_IN";
    public static final String ACTION_CHECK_OUT = "com.enighma.timesheeter.action.CHECK_OUT";

    public static final String EXTRA_TIMESTAMP = "timesheeter.timestamp";



    private final TimeSheetBinder mBinder;
    private CalendarOpenHelper mCalendarOpenHelper;

    public TimeSheetService() {
        super("TimeSheetService");
        mBinder = new TimeSheetBinder();
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public TimeSheetService(String name) {
        super(name);
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

        if(ACTION_CHECK_IN.equals(action)){
            intent.getLongExtra(EXTRA_TIMESTAMP, Long.MIN_VALUE);
            checkIn();
        } else if(ACTION_CHECK_OUT.equals(action)){
            checkOut();
        }
    }

    public void checkOut() {

    }

    public void checkIn() {
        Log.d(LOG_TAG, "Service >>> check in");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mCalendarOpenHelper = new CalendarOpenHelper(getApplicationContext());
    }

    public class TimeSheetBinder extends Binder {
        public TimeSheetService getService() {
            return TimeSheetService.this;
        }
    }
}
