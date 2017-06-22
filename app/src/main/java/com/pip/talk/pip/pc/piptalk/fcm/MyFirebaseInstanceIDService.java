package com.pip.talk.pip.pc.piptalk.fcm;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import static android.R.attr.id;

/**
 * Created by pc on 12/4/2016.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
  public static  String TOKEN = "";
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("FCM", " on create token: " + FirebaseInstanceId.getInstance().getToken());
        TOKEN = FirebaseInstanceId.getInstance().getToken();
    }


//    public static String getSenderID(){
//
//        return FirebaseInstanceId.getInstance().getToken();
//    }


    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        TOKEN = FirebaseInstanceId.getInstance().getToken();
        Log.e("FCM", "Refreshed token: " + id);
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.


       // sendRegistrationToServer(refreshedToken);
    }


}
