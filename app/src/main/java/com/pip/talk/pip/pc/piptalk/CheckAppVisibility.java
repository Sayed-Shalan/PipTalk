package com.pip.talk.pip.pc.piptalk;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by pc on 12/13/2016.
 */

public class CheckAppVisibility implements Application.ActivityLifecycleCallbacks {
    // I use four separate variables here. You can, of course, just use two and
    // increment/decrement them instead of using four and incrementing them all.
    private static int resumed;
    private static int paused;
    private static int started;
    private static int stopped;
    private static String resumedActivity;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Log.e("AC","activity " + activity.getLocalClassName()+ " is creacted " );
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Log.e("AC","activity " + activity.getLocalClassName()+ " is destroyed " );

    }

    @Override
    public void onActivityResumed(Activity activity) {
        ++resumed;
        Log.e("AC","activity " + activity.getLocalClassName()+ " is resumed " );
        resumedActivity = activity.getLocalClassName();
    }

    @Override
    public void onActivityPaused(Activity activity) {
        ++paused;
        Log.e("AC","activity " + activity.getLocalClassName()+ " is paused " );
        android.util.Log.w("test", "application is in foreground: " + (resumed > paused));
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
        ++started;
        Log.e("AC","activity " + activity.getLocalClassName()+ " is started " );

    }

    @Override
    public void onActivityStopped(Activity activity) {
        ++stopped;
        Log.e("AC","activity " + activity.getLocalClassName()+ " is stopped " );
        android.util.Log.w("test", "application is visible: " + (started > stopped));
    }

    public static boolean isChatActivityResumed() {
        if(resumedActivity.equals("chat.ChatActivity")){
            return true;
        }else {
            return false;
        }
    }

    public static boolean isMainActivityResumed() {
        if(resumedActivity.equals("home.MainActivity")){
            return true;
        }else {
            return false;
        }
    }

    public static boolean isApplicationVisible() {
        return started > stopped;
    }

    public static boolean isApplicationInForeground() {
        return resumed > paused;
    }


}
