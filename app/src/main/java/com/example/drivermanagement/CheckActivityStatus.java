package com.example.drivermanagement;

import android.app.Activity;

public class CheckActivityStatus {

    public static Activity mCurrentActivity = null;

    public static void SetActivity(Activity activity){ mCurrentActivity = activity;}

    public static void ClearActivity(Activity activity)
    {
        if(mCurrentActivity.equals(activity))
            mCurrentActivity = null;
    }
    public static Activity GetCurrentActivity(){return mCurrentActivity;}
}
