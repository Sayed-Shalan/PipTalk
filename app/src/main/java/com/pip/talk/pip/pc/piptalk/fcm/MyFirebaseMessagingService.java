package com.pip.talk.pip.pc.piptalk.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.pip.talk.pip.pc.piptalk.ApplicationClass;
import com.pip.talk.pip.pc.piptalk.CheckAppVisibility;
import com.pip.talk.pip.pc.piptalk.R;
import com.pip.talk.pip.pc.piptalk.chat.ChatActivity;
import com.pip.talk.pip.pc.piptalk.home.MainActivity;
import com.pip.talk.pip.pc.piptalk.offlineDB.ChatOfflineDBProvider;
import com.pip.talk.pip.pc.piptalk.xmpp.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static Context context;

    String message= "";

    Map<String, String> map = null;
    String sharedPrefUsername="";

    public MyFirebaseMessagingService() {
        super();
        Log.e("CHAT", " >>> MyFirebaseMessagingService()  <<<" );
    }
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        context=this;
         map = remoteMessage.getData();
         message= map.get("message");

        if ( map.get(Util.PAYLOAD_ATTRIBUTE_RECIPIENT).equals(MainActivity.MainUsername)) {

            // if the two users have the same language
            if (PreferenceManager.getDefaultSharedPreferences(this).
                    getString(getString(R.string.pref_language_key),getString(R.string.pref_language_default_value))
                    .equals(map.get(Util.PAYLOAD_ATTRIBUTE_LANGUAGE_CODE))){ // same language
                updateUI(message);

            }else { // different language .. translate then update UI
                translateMessage(message);
            }


        }else {
            Log.e("CHAT", "inside  onMessageReceived(), problem in being online or active ");
        }



    }


    private void updateUI(String message){
        Log.e("CHAT", "inside  onMessageReceived(), user is online and active");

        if(CheckAppVisibility.isApplicationInForeground()){ //  application is sleeping

            if (CheckAppVisibility.isChatActivityResumed()){ // chat activity in foreground

                if (ChatActivity.contact_username.equals(map.get(Util.PAYLOAD_ATTRIBUTE_SENDER))){ // the same person i am talking to

                    Intent intent = new Intent("update_coming_message");
                    intent.putExtra("message", message);
                    intent.putExtra("message_id", map.get("message_id"));
                    intent.putExtra("sender",map.get(Util.PAYLOAD_ATTRIBUTE_SENDER));
                    intent.putExtra("time",map.get(Util.PAYLOAD_ATTRIBUTE_TIME));
                    intent.putExtra("date",map.get(Util.PAYLOAD_ATTRIBUTE_DATE));
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

                }else { // different person
                    buildNotification(message);
                }

            }else { // chat activity in background

                if (CheckAppVisibility.isMainActivityResumed()){

                    Intent intent = new Intent("new_messages_num");
                    intent.putExtra("message", message);
                    intent.putExtra("message_id", map.get("message_id"));
                    intent.putExtra("sender",map.get(Util.PAYLOAD_ATTRIBUTE_SENDER));
                    intent.putExtra("time",map.get(Util.PAYLOAD_ATTRIBUTE_TIME));
                    intent.putExtra("date",map.get(Util.PAYLOAD_ATTRIBUTE_DATE));
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                }else{
                    buildNotification(message);
                }

            }


        }else{
            buildNotification(message);
        }


        sendToOfflineDB(Integer.valueOf(map.get(Util.PAYLOAD_ATTRIBUTE_SENDER_ID)),
                map.get(Util.PAYLOAD_ATTRIBUTE_SENDER),
                map.get(Util.PAYLOAD_ATTRIBUTE_SENDER_HAS_IMAGE),
                Integer.valueOf(map.get(Util.PAYLOAD_ATTRIBUTE_RECEIVER_ID)),
                map.get(Util.PAYLOAD_ATTRIBUTE_RECIPIENT),
                map.get(Util.PAYLOAD_ATTRIBUTE_RECEIVER_HAS_IMAGE),
                message,
                map.get(Util.PAYLOAD_ATTRIBUTE_TIME),
                map.get(Util.PAYLOAD_ATTRIBUTE_DATE),
                "1", "1");

    }

    private void translateMessage( String message){

        TranslationTask translationTask=new TranslationTask();
        translationTask.execute(map.get(Util.PAYLOAD_ATTRIBUTE_LANGUAGE_CODE), // 0
                PreferenceManager.getDefaultSharedPreferences(this)
                        .getString(getString(R.string.pref_language_key),
                                getString(R.string.pref_language_default_value)) // 1
//                map.get("message"), //2
//                map.get("recipient"), // 3
//                map.get("message_id"), // 4
//                map.get(Util.PAYLOAD_ATTRIBUTE_SENDER), //5
//                map.get(Util.PAYLOAD_ATTRIBUTE_TIME), //6
//                map.get(Util.PAYLOAD_ATTRIBUTE_DATE), // 7
//                map.get(Util.PAYLOAD_ATTRIBUTE_SENDER_ID), // 8
//                map.get(Util.PAYLOAD_ATTRIBUTE_RECEIVER_HAS_IMAGE), // 9
//                map.get(Util.PAYLOAD_ATTRIBUTE_SENDER_HAS_IMAGE) // 10
        );
    }


    private void buildNotification (String message){

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.calendar_icon)
                        .setContentTitle(map.get(Util.PAYLOAD_ATTRIBUTE_SENDER))
                        .setAutoCancel(true)
                        .setContentText(message)
                        .setSound(soundUri);

// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, ChatActivity.class);
        resultIntent.putExtra("notification_message",message);
        resultIntent.putExtra("notification_sender",map.get("sender"));
        resultIntent.putExtra("notification_message_id",map.get("message_id"));
        resultIntent.putExtra("notification_sender_id",map.get(Util.PAYLOAD_ATTRIBUTE_SENDER_ID));
        resultIntent.putExtra("notification_receiver_has_image",map.get(Util.PAYLOAD_ATTRIBUTE_RECEIVER_HAS_IMAGE));
        resultIntent.putExtra("notification_receiver_name",map.get(Util.PAYLOAD_ATTRIBUTE_RECIPIENT));
        resultIntent.putExtra("notification_sender_has_image",map.get(Util.PAYLOAD_ATTRIBUTE_SENDER_HAS_IMAGE));
//                resultIntent.putExtra("notification_receriver_token",map.get(Util.P))
        resultIntent.addCategory("NOTIFICATION");
        resultIntent.addFlags(FLAG_ACTIVITY_SINGLE_TOP);
// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(ChatActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(1, mBuilder.build());


    }
    private void sendMessageReceivedToCloudDB (final String messageID, final String msg){ /// not working perfectly yet
        String url= "http://"+ Util.WAMP_SERVER_DOMAIN+"/piptalk/setMessageReceived.php";
        StringRequest signUpRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject data = new JSONObject(response);
                    String check=data.getString("response");
                    if (check.equals("Done")){
                        Log.e("OOOO"," message received is 1 (Success) ");
                    }else {
                        Log.e("OOOO"," resoponse is  " + check);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("OOOO"," response fail : "+error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("message_id",messageID);
                params.put("message_body",msg);
                return params;
            }
        };

        ApplicationClass.getInstance().addToRequestQueue(signUpRequest);
    }

    private void sendToOfflineDB(int senderID, String sender, String sender_has_image,
                                 int receiverID, String receiver, String user_has_image,
                                 String msg, String time, String date,
                                 String sent, String received){
        ContentValues values = new ContentValues();
        values.put(ChatOfflineDBProvider.SENDER_ID,
                senderID);
        values.put(ChatOfflineDBProvider.SENDER_NAME, sender);
        values.put(ChatOfflineDBProvider.SENDER_HAS_IMAGE,sender_has_image);
        values.put(ChatOfflineDBProvider.RECEIVER_ID,
                receiverID);
        values.put(ChatOfflineDBProvider.RECEIVER_NAME,receiver); // receiver username
        values.put(ChatOfflineDBProvider.RECEIVER_HAS_IMAGE,user_has_image); // user has image
        values.put(ChatOfflineDBProvider.MESSAGE_BODY,
                msg);

        values.put(ChatOfflineDBProvider.MESSAGE_LANG_CODE,
                PreferenceManager.getDefaultSharedPreferences(context).
                        getString(getString(R.string.pref_language_key), ""));

        values.put(ChatOfflineDBProvider.TIME,
                time);
        values.put(ChatOfflineDBProvider.DATE,
                date);
        values.put(ChatOfflineDBProvider.M_SENT,
                sent);
        values.put(ChatOfflineDBProvider.M_RECEIVED,
                received);
        getContentResolver().insert(
                ChatOfflineDBProvider.CONTENT_URI, values);
    }

    public class TranslationTask extends AsyncTask<String,Void,Void >
    {

        Context context =MyFirebaseMessagingService.context;

//        @Override
//        protected void onPostExecute(String  aVoid) {
//            super.onPostExecute(aVoid);
//            if(aVoid.isEmpty()) {
//                Log.e("Chat Translate"," empty ");
//            } else {
//
//
//                // Toast.makeText(getApplicationContext(),aVoid,Toast.LENGTH_SHORT).show();
//                if (aVoid.length()!=0) {
//                    String MyActuallyTranslate = "";
//                    for (int i = 13; i < aVoid.length() - 5; i++) {
//                        MyActuallyTranslate = MyActuallyTranslate + aVoid.charAt(i);
//                    }
//
//                    //start receive message after translated
//
//
//                    if ( map.get("recipient").equals(MainActivity.MainUsername)) {
//
//                        Log.e("CHAT", "inside  onMessageReceived(), user is online and active");
//
//                        if(CheckAppVisibility.isApplicationInForeground()){
//
//                            Intent intent = new Intent("update_coming_message");
//                            // You can also include some extra data.
//                            Log.e("WWW","onMessageReceived(), message received : "+MyActuallyTranslate);
//                            intent.putExtra("message",MyActuallyTranslate);
//                            intent.putExtra("message_id",map.get("message_id"));
//                            intent.putExtra("sender",map.get(Util.PAYLOAD_ATTRIBUTE_SENDER));
//                            intent.putExtra("time",map.get(Util.PAYLOAD_ATTRIBUTE_TIME));
//                            intent.putExtra("date",map.get(Util.PAYLOAD_ATTRIBUTE_DATE));
//                            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
//
//                            //      sendMessageReceivedToCloudDB(messageID,message);
//
//                        }else{
//                            //Define sound URI
//
//                            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//                            NotificationCompat.Builder mBuilder =
//                                    new NotificationCompat.Builder(context)
//                                            .setSmallIcon(R.drawable.logo)
//                                            .setContentTitle(map.get(Util.PAYLOAD_ATTRIBUTE_SENDER))
//                                            .setAutoCancel(true)
//                                            .setContentText(MyActuallyTranslate)
//                                            .setSound(soundUri);
//
//// Creates an explicit intent for an Activity in your app
//                            Intent resultIntent = new Intent(context, ChatActivity.class);
//                            resultIntent.putExtra("notification_message",MyActuallyTranslate);
//                            resultIntent.putExtra("notification_sender",map.get(Util.PAYLOAD_ATTRIBUTE_SENDER));
//                            resultIntent.putExtra("notification_message_id",map.get("message_id"));
//                            resultIntent.putExtra("notification_sender_id",map.get(Util.PAYLOAD_ATTRIBUTE_SENDER_ID));
//                            resultIntent.putExtra("notification_receiver_has_image",map.get(Util.PAYLOAD_ATTRIBUTE_RECEIVER_HAS_IMAGE));
//                            resultIntent.putExtra("notification_receiver_name",map.get("recipient"));
//                            resultIntent.putExtra("notification_sender_has_image",map.get(Util.PAYLOAD_ATTRIBUTE_SENDER_HAS_IMAGE));
////                resultIntent.putExtra("notification_receriver_token",map.get(Util.P))
//                            resultIntent.addCategory("NOTIFICATION");
//                            resultIntent.addFlags(FLAG_ACTIVITY_SINGLE_TOP);
//
//// The stack builder object will contain an artificial back stack for the
//// started Activity.
//// This ensures that navigating backward from the Activity leads out of
//// your application to the Home screen.
//                            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
//// Adds the back stack for the Intent (but not the Intent itself)
//                            stackBuilder.addParentStack(ChatActivity.class);
//// Adds the Intent that starts the Activity to the top of the stack
//                            stackBuilder.addNextIntent(resultIntent);
//                            PendingIntent resultPendingIntent =
//                                    stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//                            mBuilder.setContentIntent(resultPendingIntent);
//                            NotificationManager mNotificationManager =
//                                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//// mId allows you to update the notification later on.
//                            mNotificationManager.notify(1, mBuilder.build());
//
//                            //   sendMessageReceivedToCloudDB(messageID,message);
//                        }
//
//
//                        Log.e("QQ","message received int sender id :" +map.get(Util.PAYLOAD_ATTRIBUTE_SENDER_ID));
//                        Log.e("QQ","message received int receiver id :" +map.get(Util.PAYLOAD_ATTRIBUTE_RECEIVER_ID));
//
//                      /*  sendToOfflineDB(Integer.valueOf(map.get(Util.PAYLOAD_ATTRIBUTE_SENDER_ID)),
//                                map.get(Util.PAYLOAD_ATTRIBUTE_SENDER), map.get(Util.PAYLOAD_ATTRIBUTE_SENDER_HAS_IMAGE),
//                                Integer.valueOf(map.get(Util.PAYLOAD_ATTRIBUTE_RECEIVER_ID)), map.get(Util.PAYLOAD_ATTRIBUTE_RECIPIENT),
//                                map.get(Util.PAYLOAD_ATTRIBUTE_RECEIVER_HAS_IMAGE),map.get(Util.PAYLOAD_ATTRIBUTE_MESSAGE),
//                                map.get(Util.PAYLOAD_ATTRIBUTE_TIME), map.get(Util.PAYLOAD_ATTRIBUTE_DATE),
//                                "1", "1");
//
//*/
//
//                    }else {
//                        Log.e("CHAT", "inside  onMessageReceived(), problem in being online or active ");
//                    }
//
//
//                    //end receive
//
//
//                }
//            }
//        }

        @Override
        protected Void  doInBackground(String... strings) {


            //connect to api services
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String translateResponseStr = null;

            //decode it before translation if it is arabic
            if (strings[0].equals("ar")) {
                try {
                    message = new String(message.getBytes(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    Log.e("utf8", "conversion", e);
                }
            }

            String urlString ="http://api.microsofttranslator.com/V2/Ajax.svc/Translate?oncomplete=mycallback&appId=68D088969D79A8B23AF8585CC83EBA2A05A97651&from="+strings[0]+"&to="+strings[1]+"&text="+message;
            urlString=urlString.replaceAll(" ","%20");



            try {
                URL url=new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                // read data from stream
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                /////////////////////////////////////////////
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "/n");
                }
                if (buffer.length() <= 0) {
                    Log.v("Response", "Error to connect");
                    return null;

                }
                translateResponseStr = buffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }

                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }}
                String  data=translateResponseStr ;
                    //    strings[3],strings[4],strings[5],strings[6],strings[7],strings[8],strings[9],strings[10]

            if (data.length()>0) {
                String translatedMsg = "";
                for (int i = 13; i < data.length() - 5; i++) {
                    translatedMsg = translatedMsg + data.charAt(i);
                }



                    updateUI(translatedMsg);




            }
            return null;
        }
    }

}
