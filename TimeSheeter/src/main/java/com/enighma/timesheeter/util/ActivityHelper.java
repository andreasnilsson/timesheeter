package com.enighma.timesheeter.util;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Enighma on 2013-08-01.
 */
public class ActivityHelper {

    private final Activity mActivity;

    public ActivityHelper(Activity activity) {
        mActivity = activity;
    }

    public <T extends View> T getView(int resId){
        //noinspection unchecked
        return (T) mActivity.findViewById(resId);
    }

    /**
     * Sets the text of a text view with resource id {@code textViewResId}.
     *
     * @param textViewResId The resource id for the {@link TextView}.
     * @param string The string to set.
     */
    public void setText(int textViewResId, String string) {
        TextView tv = getView(textViewResId);
        tv.setText(string);
    }
}
