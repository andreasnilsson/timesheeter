package com.enighma.timesheeter;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.enighma.timesheeter.service.TimeSheetService;
import com.enighma.timesheeter.util.ViewHelper;

import static com.enighma.timesheeter.Config.LOG_TAG;

// TODO description
public class DataViewerActivity extends Activity {
    private ViewHelper mViewHelper;

    TimeSheetService mTimeSheetService;

    private boolean mBound;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBound = true;
            TimeSheetService.TimeSheetBinder binder = (TimeSheetService.TimeSheetBinder) service;
            mTimeSheetService = binder.getService();

            mTimeSheetService.addTimeSheetObserver(mDataSetObserver);
            updateUI();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;

        }
    };
    private DataSetObserver mDataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            updateUI();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_data_viewer);

        mViewHelper = new ViewHelper(this);

        setupOnClickListeners();

    }

    private void setupOnClickListeners() {
        Button generateButton = mViewHelper.getView(R.id.button);
        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBound) {
                    mTimeSheetService.createNewEvent(timestamp);
                } else {
                    Log.d(LOG_TAG, "Service not bound");
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // FIXME bind onstart or oncreate?
        // bind to service
        Intent intent = new Intent(this, TimeSheetService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    protected void onPause() {
        super.onPause();
//        mCalendarOpenHelper.removeDataSetObserver(mDataSetObserver);
    }

    public void updateUI(){
        // load data
        int nDays = mTimeSheetService.getNoSavedDays();

        mViewHelper.setText(R.id.calendarItemsTextView, "Calendar Items: " + nDays);
        Log.d(LOG_TAG, "Calendar items: " + nDays);

    }
}
