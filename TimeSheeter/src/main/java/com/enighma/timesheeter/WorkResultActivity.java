package com.enighma.timesheeter;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.format.Time;
import android.util.SparseLongArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.enighma.timesheeter.service.TimeSheetService;
import com.enighma.timesheeter.util.ActivityHelper;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/** Created by 23060684 on 8/27/13. */
public class WorkResultActivity extends Activity {
    private static final int NO_DAYS_PER_WEEK = 7;
    private TimeSheetService mService;

    private boolean mBound = false;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = ((TimeSheetService.TimeSheetBinder) service).getService();
            mBound = true;

            // TODO get data from service
//            List<Long> events = mService.getEvents(mSelectedWeekNumber);
            List<Long> events = new ArrayList<Long>();

            // TODO update UI with data
            updateUI(events);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };
    private EventAdapter mEventAdapter;

    public class EventAdapter extends BaseAdapter {
        private SparseLongArray mDayWorkHoursMap;

        public EventAdapter(List<Long> events) {
            mDayWorkHoursMap = transform(events);
        }

        private SparseLongArray transform(List<Long> eventList) {
            SparseLongArray retList = new SparseLongArray(NO_DAYS_PER_WEEK);
            Time time = new Time();

            for (int i = 1; i < eventList.size(); i += 2) {
                long start = eventList.get(i - 1);
                long end = eventList.get(i);
                long dt = end - start;

                time.set(start);

                Long timeWorked = retList.get(time.weekDay);
                timeWorked += dt;
                retList.put(time.weekDay, timeWorked);
            }

            return retList;
        }

        @Override
        public int getCount() {
            return mDayWorkHoursMap.size();
        }

        @Override
        public Object getItem(int position) {
            return mDayWorkHoursMap.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Nullable
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = WorkResultActivity.this.getLayoutInflater();

            View view = convertView == null
                    ? inflater.inflate(R.layout.list_item, parent, false)
                    : convertView;

            // set text etc for the view..

            return view;
        }

        public void setEvents(List<Long> events) {
            mDayWorkHoursMap = transform(events);
            notifyDataSetChanged();
        }
    }

    private static final String EXTRA_WEEK_NUMBER = "extra_week_number";
    private int mSelectedWeekNumber;
    private ActivityHelper mAH;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAH = new ActivityHelper(this);


        setContentView(R.layout.activity_work_results);

        mSelectedWeekNumber = getIntent().getIntExtra(EXTRA_WEEK_NUMBER, getCurrentWeekNumber());
    }

    @Override
    protected void onStart() {
        super.onStart();

        // bind to service
        doBindService();
    }

    private int getCurrentWeekNumber() {
        Time time = new Time();
        time.setToNow();

        return time.getWeekNumber();
    }

    private void doBindService() {
        Intent intent = new Intent(this, TimeSheetService.class);
        bindService(intent, mConnection, BIND_AUTO_CREATE);
    }

    private void generateTestData() {
        List<Long> timestamps = new ArrayList<Long>();

        long t1 = System.currentTimeMillis();
        long t2 = t1 + TimeUnit.HOURS.toMillis(7);
        timestamps.add(t1);
        timestamps.add(t2);

//        HashMap<Integer, Long> dayWorkHoursMap = new HashMap<Integer, Long>();

        SparseLongArray dayWorkHoursMap = new SparseLongArray(NO_DAYS_PER_WEEK);

        // start determines day it will be logged at

        // Simple case we only have two timestamps

        // Do below per day

        // make sure to handle the case timestamps < 2

        // limitation last time event cannot be check in

        Time time = new Time();

        for (int i = 1; i < timestamps.size(); i += 2) {
            long start = timestamps.get(i - 1);
            long end = timestamps.get(i);
            long dt = end - start;

            time.set(start);

            Long timeWorked = dayWorkHoursMap.get(time.weekDay);
            timeWorked += dt;
            dayWorkHoursMap.put(time.weekDay, timeWorked);
        }
    }

    public void updateUI(List<Long> events) {
        if(mEventAdapter == null) {
            mEventAdapter = new EventAdapter(events);
            ListView list = mAH.getView(R.id.list);
            list.setAdapter(mEventAdapter);
        } else {
            mEventAdapter.setEvents(events);
        }
    }
}