package com.enighma.timesheeter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity {
    private static final String LOG_TAG = MainActivity.class.getName();
    private static final String FILE_NAME_PREFIX = "week_";

    private Handler mHandler = new Handler();
    private DayPageAdapter mDayPageAdapter;
    private ViewPager viewPager;
    private List<Day> mDays;
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            update();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onResume() {
        super.onResume();

        // do in BG
        loadData();

        final Button checkInButton = (Button) findViewById(R.id.checkInButton);

        checkInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkIn();


                updateCheckInButton();


                update();
            }
        });

        // Setup view pager

        setupViewPager();

        updateCheckInButton();

        // show current diff from today's check-in time to now
        mHandler.post(mRunnable);

    }

    private void updateCheckInButton() {
        // TODO
        final Button checkInButton = (Button) findViewById(R.id.checkInButton);

        Day today = getToday();
        if (today.isCheckedIn()) {
            if (today.isCheckedOut()) {
                checkInButton.setText("Done!");
            } else {
                checkInButton.setText("Check Out");
            }
        } else {
            checkInButton.setText("Check In");
        }
    }

    private void setupViewPager() {
        mDayPageAdapter = new DayPageAdapter(this, mDays);
        mDayPageAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                saveData();
            }
        });
        //TODO viewpager for days and show one in focus..

        viewPager = (ViewPager) findViewById(R.id.dayViewPager);
        viewPager.setAdapter(mDayPageAdapter);
        viewPager.setCurrentItem(getCurrentTime().weekDay, true);
        viewPager.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
                updateCheckInButton();

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    private Time getCurrentTime() {
        Time t = new Time();
        t.set(System.currentTimeMillis());
        return t;
    }


    public void update() {

        final TextView timeView = (TextView) findViewById(R.id.current_dt);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Day today = getToday();

                if (today.isCheckedIn() && !today.isCheckedOut()) {

                    final long seconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - today.getStart()) % 60;
                    final long minutes = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - today.getStart()) % 60;
                    final long hours   = TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis() - today.getStart()) % 24;


                    String s = seconds < 10 ? "0" + seconds : "" + seconds;
                    String m = minutes < 10 ? "0" + minutes : "" + minutes;
                    String h = hours < 10 ? "0" + hours : "" + hours;

                    String text = h + ":" + m + ":" + s;

                    timeView.setVisibility(View.VISIBLE);
                    timeView.setText(text);

                    mHandler.postDelayed(mRunnable, 1000);
                } else {
                    timeView.setVisibility(View.GONE);
                }
            }
        });


    }

    private List<Day> createDays() {

        ArrayList<Day> days = new ArrayList<Day>();
        String[] strings = getResources().getStringArray(R.array.week_days);


        for (String d : strings) {
            Day day = new Day(d);
            days.add(day);
        }

        return days;
    }

    private void checkIn() {
        Day day = getToday();

        if (!day.isCheckedIn()) {
            day.checkIn();
        } else {
            day.checkOut();
        }

        viewPager.getAdapter().notifyDataSetChanged();

        saveData();
    }

    private void saveData() {
        FileOutputStream os = null;
        try {
            os = openFileOutput(getFileName(), Context.MODE_PRIVATE);

            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(mDays);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (os != null) {
            try {
                os.close();
            } catch (IOException ignored) { }
        }
    }


    private void loadData() {

        FileInputStream is = null;
        try {
            is = openFileInput(getFileName());
            ObjectInputStream ois = new ObjectInputStream(is);

            mDays = (ArrayList<Day>) ois.readObject();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (is != null) {
            try {
                is.close();
            } catch (IOException ignored) {
            }
        }


        // If we have no days to load then lets create them
        if (mDays == null) {
            mDays = createDays();
        }

        // TODO when loaded update UI so it is async
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public String getFileName() {
        Time t = new Time();
        t.setToNow();

        return FILE_NAME_PREFIX + t.getWeekNumber();

    }

    public Day getToday() {
        if (mDays != null) {
            Time time = new Time();
            time.setToNow();
            return mDays.get(time.weekDay);
        }

        return null;
    }


    private class DayPageAdapter extends PagerAdapter {

        private final Context mContext;
        private List<Day> mDays;
        private final LayoutInflater mLayoutInflater;
        private float mWidth = 1.0f;// / 3.0f;

        private DayPageAdapter(Context context, List<Day> days) {
            mContext = context;
            mDays = days;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        }


        @Override
        public float getPageWidth(int position) {
            return mWidth;
        }


        @Override
        public int getCount() {
            return mDays.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = mLayoutInflater.inflate(R.layout.day_layout, null);

            final Day day = mDays.get(position);

            TextView dayTV = (TextView) view.findViewById(R.id.dayTextView);
            dayTV.setText(day.name);


            Button start = (Button) view.findViewById(R.id.start);
            Button end = (Button) view.findViewById(R.id.end);
            TextView diff = (TextView) view.findViewById(R.id.diff);

            start.setText(formatTime(day.getStart()));
            end.setText(formatTime(day.mEnd));

            float hours = TimeUnit.MILLISECONDS.toMinutes(day.getDiff()) / 60f;


            if (hours > 0) {
                diff.setText(Float.toString(hours) + " h");
            } else {
                diff.setText("-");
            }


            container.addView(view);
            start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Time currentTime = getCurrentTime();
                    if(day.getStart() != 0){
                        currentTime.set(day.getStart());
                    }

                    final TimePicker tp = new TimePicker(mContext);
                    tp.setIs24HourView(true);
                    tp.setCurrentHour(currentTime.hour);
                    tp.setCurrentMinute(currentTime.minute);

                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setView(tp);
                    builder.setTitle("Select time");
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {

                            Time t = new Time();
                            t.setToNow();

                            t.hour = tp.getCurrentHour();
                            t.minute = tp.getCurrentMinute();

                            day.setStart(t.toMillis(true));

                            notifyDataSetChanged();
                        }
                    });

                    builder.show();

                }
            });

            end.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Time currentTime = getCurrentTime();

                    if (day.getStart() != 0) {
                        currentTime.set(day.getEnd());
                    }

                    final TimePicker tp = new TimePicker(mContext);
                    tp.setIs24HourView(true);
                    tp.setCurrentHour(currentTime.hour);
                    tp.setCurrentMinute(currentTime.minute);

                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setView(tp);
                    builder.setTitle("Select time");
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            Time t = new Time();
                            t.setToNow();

                            t.hour = tp.getCurrentHour();
                            t.minute = tp.getCurrentMinute();

                            day.setEnd(t.toMillis(true));

                            notifyDataSetChanged();
                        }
                    });

                    builder.show();
                }
            });


            return view;
        }

        private CharSequence formatTime(long time) {
            return time == 0 ? "--:--" : DateFormat.getTimeFormat(mContext).format(new Date(time));
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

    public static class Day implements Serializable {


        // TODO expand with arbitrary check in/out states

        public final String name;
        /**
         * Default mStart is 0
         */
        private long mStart;
        /**
         * Default mEnd is 0
         */
        private long mEnd;


        public Day(String name) {
            this.name = name;
            mStart = 0;
            mEnd = 0;
        }

        public void checkIn() {
            mStart = System.currentTimeMillis();
        }

        public void checkOut() {
            mEnd = System.currentTimeMillis();
        }

        /**
         * If no mStart time has been set, i.e. mStart = 0
         * it is considered not the be checked in.
         *
         * @return {@code true} if checked in.
         */
        public boolean isCheckedIn() {
            return mStart != 0;
        }

        public boolean isCheckedOut() {
            return mEnd != 0;
        }


        /**
         * If there is an mEnd time it returns the delta.
         *
         * @return
         */
        public long getDiff() {
            return isCheckedOut() ? mEnd - mStart : 0;
        }

        public long getStart() {
            return mStart;
        }

        public void setStart(long start) {
            mStart = start;
        }

        public void setEnd(long end) {
            mEnd = end;
        }

        public long getEnd() {
            return mEnd;
        }
    }
}
