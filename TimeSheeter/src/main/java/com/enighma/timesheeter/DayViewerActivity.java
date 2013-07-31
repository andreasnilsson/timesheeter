package com.enighma.timesheeter;

import android.app.Activity;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.enighma.timesheeter.model.Day;

import static com.enighma.timesheeter.CalendarOpenHelper.*;

/**
 * Created by Enighma on 2013-07-12.
 */
public class DayViewerActivity extends Activity {

    private static final String LOG_TAG = DayViewerActivity.class.getName();
    public static String FILE_NAME = "temp_day";

    private TimePicker mEndTimePicker;
    private TimePicker mStartTimePicker;
    private TextView mDayTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_day_viewer);

        // Setup pickers
        mStartTimePicker = (TimePicker) findViewById(R.id.startTimePicker);
        mEndTimePicker = (TimePicker) findViewById(R.id.endTimePicker);
        mStartTimePicker.setIs24HourView(true);
        mEndTimePicker.setIs24HourView(true);


        mDayTextView = (TextView) findViewById(R.id.dayTextView);

        // Setup click listeners
        findViewById(R.id.saveButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO

            }
        });
        findViewById(R.id.loadButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
            }
        });
        findViewById(R.id.saveToCalendarButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create test data
                final CalendarDay day = createTestDay();

                // save calendar day
//                day.saveToCalendar(getApplicationContext());
            }
        });
    }

    private CalendarDay createTestDay() {
        int calID = 1;

        Time time = new Time();
        time.setToNow();

        long start = time.toMillis(true);

        time.hour += 2;

        long end = time.toMillis(true);

//        return new CalendarDay(calID, start, end, 0L);
        // TODO
        return null;

    }

    private void showText(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    private Day getDayFromViews() {
        long start = getTimeFromTimePicker(mStartTimePicker);
        long end = getTimeFromTimePicker(mEndTimePicker);
        long pauses = 0L;

        return new Day(start, end, pauses);
    }

    private long getTimeFromTimePicker(TimePicker startTimePicker) {
        Time t = new Time();
        t.setToNow();
        t.minute = mStartTimePicker.getCurrentMinute();
        t.hour = mStartTimePicker.getCurrentHour();

        return t.toMillis(true);
    }


    public void updateViews(final Day day) {

        Time startTime = new Time();
        startTime.set(day.getStart());

        Time endTime = new Time();
        startTime.set(day.getEnd());


        mStartTimePicker.setCurrentHour(startTime.hour);
        mStartTimePicker.setCurrentMinute(startTime.minute);

        mEndTimePicker.setCurrentHour(endTime.hour);
        mEndTimePicker.setCurrentMinute(endTime.minute);

        mDayTextView.setText("week day: " + startTime.weekDay);
    }
}
