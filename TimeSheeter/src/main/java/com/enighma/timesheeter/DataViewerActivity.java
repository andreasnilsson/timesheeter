package com.enighma.timesheeter;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.enighma.timesheeter.service.TimeSheetService;
import com.enighma.timesheeter.util.ViewHelper;

import java.util.List;

import static com.enighma.timesheeter.Config.*;

// TODO description
public class DataViewerActivity extends Activity {
    private CalendarOpenHelper mCalendarOpenHelper;
    private ViewHelper mViewHelper;

    TimeSheetService mTimeSheetService;

    private boolean mBound;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBound = true;
            TimeSheetService.TimeSheetBinder binder = (TimeSheetService.TimeSheetBinder) service;
            mTimeSheetService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_data_viewer);

        mCalendarOpenHelper = new CalendarOpenHelper(this);
        mViewHelper = new ViewHelper(this);

        setupOnClickListeners();
    }

    private void setupOnClickListeners() {
        Button generateButton = mViewHelper.getView(R.id.button);
        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBound) {
                    mTimeSheetService.checkIn();
                } else {
                    Log.d(LOG_TAG, "Service not bound");
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // load data
        final List<CalendarOpenHelper.CalendarDay> allDays = mCalendarOpenHelper.getAllDays();

        mViewHelper.setText(R.id.calendarItemsTextView, "Calendar Items: " + allDays.size());
        Log.d(LOG_TAG, "Calendar items: " + allDays.size());

        // bind to service

        Intent intent = new Intent(this, TimeSheetService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }
}
