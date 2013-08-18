package com.enighma.timesheeter;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.enighma.timesheeter.service.TimeSheetService;
import com.enighma.timesheeter.util.ActivityHelper;
import com.enighma.timesheeter.util.Logg;

import static android.provider.CalendarContract.Calendars;

public class CreateEventActivity extends Activity {
    Logg LOG = new Logg(CreateEventActivity.class.getName());

    private ActivityHelper mAH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        mAH = new ActivityHelper(this);
        registerListeners();
    }

    private void registerListeners() {
        Button createEventButton = mAH.getView(R.id.createEventButton);
        // disable button and enable once connected to service

        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createEvent();
            }
        });
    }



    private void createEvent() {
        final Intent intent = new Intent(this, TimeSheetService.class);
        intent.setAction(TimeSheetService.ACTION_NEW_EVENT);
        intent.putExtra(TimeSheetService.EXTRA_TIMESTAMP, System.currentTimeMillis());
        intent.putExtra(TimeSheetService.EXTRA_CALENDAR_ID, 1);

        startService(intent);
    }


    @Override
    protected void onStart() {
        super.onStart();

        // pritn available calendars

        String[] projection =
                new String[]{
                        Calendars._ID,
                        Calendars.NAME,
                        Calendars.ACCOUNT_NAME,
                        Calendars.ACCOUNT_TYPE};
        Cursor calCursor =
                getContentResolver().
                        query(Calendars.CONTENT_URI,
                                projection,
                                Calendars.VISIBLE + " = 1",
                                null,
                                Calendars._ID + " ASC");

        //  TODO make sure there are calendars available..

        LOG.d("found calendars: " + calCursor.getCount());


        if (calCursor.moveToFirst()) {
            do {
                long id = calCursor.getLong(0);
                String displayName = calCursor.getString(1);
                LOG.d("calendar info:");
                LOG.d("id: " + id + " name " + displayName);
                // ...
            } while (calCursor.moveToNext());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_event, menu);
        return true;
    }
    
}
