package com.enighma.timesheeter;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.ObjectInputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by Enighma on 2013-07-12.
 */
public class HistoryBrowserActivity extends Activity {

    private static final String LOG_TAG = HistoryBrowserActivity.class.getName();
    private ListView mHistoryList;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_history_browser);
    }

    @Override
    protected void onStart() {
        super.onStart();


        final String[] filesNames = fileList();
//        String[] strings = fileList();
//        for(String f : strings){
//            Log.d(LOG_TAG, "files: " + f);
//        }

        mHistoryList = (ListView) findViewById(R.id.history_list);
        mHistoryList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, filesNames));


        mHistoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String filename = filesNames[position];
                Log.d(LOG_TAG, "selected filename: " + filename);

                try {


                    ObjectInputStream ois = new ObjectInputStream(openFileInput(filename));

                    ArrayList<?> object = (ArrayList<?>) ois.readObject();
                    for(Object o : object){

                        final Class<?> aClass = o.getClass();
                        final Field name = aClass.getDeclaredField("name");
                        final Field start = aClass.getDeclaredField("mStart");
                        final Field end = aClass.getDeclaredField("mEnd");
                        start.setAccessible(true);
                        end.setAccessible(true);



                        final String n = (String) name.get(o);
                        long s = start.getLong(o);
                        long e = end.getLong(o);

                        long dt = e - s;

                        float dth = TimeUnit.MILLISECONDS.toMinutes(dt) / 60f;
                        System.out.println(n + " dt: " + dth);

                        System.out.println();
//                        for(Field f : fields){
//                            f.setAccessible(true);
//                            Class<?> targetType = f.getType();
//                            Object objectValue = targetType.newInstance();
//                            Object value = f.get(objectValue);
//
//                        }





//                        final Field[] fields = o.getClass().getFields();
//                        final Field[] declaredFields = o.getClass().getDeclaredFields();
//                        final Class<?> aClass = o.getClass();
//
                        System.out.println();

                    }
//                    long start = ois.readLong();
//                    long end = ois.readLong();

//                    Log.d(LOG_TAG, "title: " + object);

//                    ArrayList<com.enighma.timesheeter.model.Day> days = (ArrayList<com.enighma.timesheeter.model.Day>) ois.readObject();

//                    printDays(days);

                    ois.close();
                } catch (Exception e) {
                    Log.e(LOG_TAG, "Error", e);
                }


            }
        });
    }


    public static class Day {
        String name;
        long mStart;
        long mEnd;
    }

    public void printDays(ArrayList<com.enighma.timesheeter.model.Day> days) {

        for(com.enighma.timesheeter.model.Day d : days){
//            Log.d(LOG_TAG,"Day: " + d.name + "diff: " + TimeUnit.MILLISECONDS.toHours(d.getDiff()));
        }

    }
}
