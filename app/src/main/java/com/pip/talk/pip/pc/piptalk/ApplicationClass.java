package com.pip.talk.pip.pc.piptalk;

import android.app.Application;
import android.content.Intent;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.pip.talk.pip.pc.piptalk.fcm.MyFirebaseInstanceIDService;
import com.pip.talk.pip.pc.piptalk.fcm.MyFirebaseMessagingService;


public class ApplicationClass extends Application {


    public static final String TAG = ApplicationClass.class.getSimpleName();


    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    Intent i= null;
    Intent i2= null;


    private static ApplicationClass mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        i = new Intent(this,MyFirebaseInstanceIDService.class);
        i2 = new Intent(this, MyFirebaseMessagingService.class);
        startService(i);
        startService(i2);
        mInstance = this;

        registerActivityLifecycleCallbacks(new CheckAppVisibility());

    }



    public static synchronized ApplicationClass getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());

        }
        return mRequestQueue;
    }



    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }



}
