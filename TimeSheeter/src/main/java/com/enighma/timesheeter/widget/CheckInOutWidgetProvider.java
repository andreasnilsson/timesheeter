package com.enighma.timesheeter.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.enighma.timesheeter.MainActivity;
import com.enighma.timesheeter.R;

import static com.enighma.timesheeter.Config.LOG_TAG;

/**
 * Created by 23060684 on 7/24/13.
 */
public class CheckInOutWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(LOG_TAG, "on widget update");

        for(int id : appWidgetIds){
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_checkinout);
            views = setCheckInOutPendingIntent(context, views);
            appWidgetManager.updateAppWidget(id, views);
        }
    }

    private RemoteViews setCheckInOutPendingIntent(Context context, RemoteViews remoteViews) {
        // Create an Intent to launch ExampleActivity
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        remoteViews.setOnClickPendingIntent(R.id.toggle_work_status_button, pendingIntent);

        return remoteViews;
    }
}
