package com.pip.talk.pip.pc.piptalk.chat;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.pip.talk.pip.pc.piptalk.ApplicationClass;
import com.pip.talk.pip.pc.piptalk.R;
import com.pip.talk.pip.pc.piptalk.User_Model.UserPreference;
import com.pip.talk.pip.pc.piptalk.offlineDB.ChatOfflineDBProvider;
import com.pip.talk.pip.pc.piptalk.xmpp.bean.CcsOutMessage;
import com.pip.talk.pip.pc.piptalk.xmpp.server.CcsClient;
import com.pip.talk.pip.pc.piptalk.xmpp.server.MessageHelper;
import com.pip.talk.pip.pc.piptalk.xmpp.util.Util;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;


public class ChatFragment extends Fragment {

    public static final String MESSAGE_ID = "message_id";
    public static final String SENDER_ID = "sender_id";
    public static final String SENDER_NAME = "sender_name";
    public static final String SENDER_HAS_IMAGE = "sender_has_image";
    public static final String RECEIVER_ID = "receiver_id";
    public static final String RECEIVER_NAME = "receiver_name";
    public static final String RECEIVER_HAS_IMAGE = "receiver_has_image";
    public static final String MESSAGE_BODY = "message_body";
    public static final String MESSAGE_LANG_CODE = "msg_lang_code";
    public static final String TIME = "time";
    public static final String DATE = "date";
    public  static String translatedText="";


    private final String USER_HAS_IMAGE = "has_image";

    private final String NATIVE_LANGUAGE = "native_language";
    private final String GENDER = "gender";
    private final String HAS_IMAGE = "has_image";
    private final String STATUS = "status";

    private  ListView msgListView;
    private EditText msg_editText;
    private ImageButton sendButton;
    private ImageButton recordButton;

    private int PRIVATE_MODE=1;
    private String PREF_NAME="user";
    private ArrayList<ChatMessage> chatArrList;
    private SendChatAdapter chatAdapter;
    //private static ReceiveChatAdapter receiveChatAdapter;
    //private static SendChatAdapter updateAdapter;
    private static String usernameOfDestination="";
    private static String tokenOfDestination="";
    private String mMainUserName="";
    private String mUserLangaue="";
    private String mMainUser_id="";
    private String mMainUserToken="";
    private String mMainUser_has_image="";
    private String NotificationMessage="";
    private String comingNotificationSenderHasImage ="";
    private String comingNotificationSenderName ="";
    private int user_id;
    private String user_has_image="";
    private  Activity activity;
    private String localTime="";
    private String fDate ="";
    private ArrayList<String> timeNDdate= new ArrayList<String>();
    private String writtenMessage="";



    public ChatFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.chat_fragment, container, false);

        msgListView = (ListView) rootView.findViewById(R.id.msgListView);
        msg_editText = (EditText) rootView.findViewById(R.id.messageEditText);
        msgListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        msgListView.setStackFromBottom(true);

        sendButton = (ImageButton) rootView.findViewById(R.id.sendMessageButton);
        recordButton = (ImageButton) rootView.findViewById(R.id.recordMessageButton);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // ----Set autoscroll of listview when a new message arrives----//
//        if(savedInstanceState!=null){
//            msg_editText.setText(savedInstanceState.get("message").toString());
//        }

        activity = getActivity();
        chatArrList = new ArrayList<>();
        chatAdapter= new SendChatAdapter(getActivity(), 0);
        //receiveChatAdapter = new ReceiveChatAdapter(getActivity(), commingList);
        msgListView.setAdapter(chatAdapter);


        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
                new IntentFilter("update_coming_message"));

        Bundle bundle = getArguments();

        if(bundle!=null){
            usernameOfDestination = bundle.getString("username");
            // Toast.makeText(getActivity(),usernameOfDestination,Toast.LENGTH_SHORT).show();
            tokenOfDestination = bundle.getString("user_token");
            user_has_image = bundle.getString("user_has_image");

            if(bundle.containsKey("user_id_intger")){ // user id aka profile picture name
                user_id =  bundle.getInt("user_id_intger");
            }else if(bundle.containsKey("user_id_string")) {
                user_id = Integer.parseInt(bundle.getString("user_id_string"));
            }

            if(bundle.containsKey("notification_message") && bundle.containsKey("notification_sender") ){ // for notification data
                Log.e("TOKEN","inside contains key");
                user_id = Integer.valueOf(bundle.getString("notification_sender_id"));
//                NotificationMessage =  bundle.getString("notification_message");
                findUserToken(user_id);
                comingNotificationSenderHasImage = bundle.getString("notification_sender_has_image");
                comingNotificationSenderName = bundle.getString("notification_sender");
                usernameOfDestination = comingNotificationSenderName;
                user_has_image = comingNotificationSenderHasImage;
                //  display(bundle.getString("notification_message"),bundle.getString("notification_message_id"));
            }

            if (bundle.containsKey("contact_id")){
                user_id = Integer.valueOf(bundle.getString("contact_id"));
            }

        }


        mMainUserName =new UserPreference(getActivity()).getUser().getUser_name();
        mMainUser_id = String.valueOf(new UserPreference(getActivity()).getUser().getId());
        mMainUser_has_image = new UserPreference(getActivity()).getUser().getImage();
        mMainUserToken = new UserPreference(getActivity()).getUser().getUserToken();
        mUserLangaue = new UserPreference(getActivity()).getUser().getNativeLanguage();

        return rootView;
    }

//private void display(String msg,String msgID ){
//    chatArrList.add(new ChatMessage("","", "","",msg,msgID,false));
//    chatAdapter.SendMessageToAdapter(chatArrList);
//}

    private void findUserToken(final int _id ){

        Log.e("T","inside findUserID");

        getTokenFromResponse(Request.Method.POST, _id, new getTokenInterface() {
            @Override
            public void onSuccess(String result) {
                try {
                    findToken(result,_id);
                } catch (JSONException e) {
                    Log.e("TTT"," failed message : " + e);
                    e.printStackTrace();
                }
            }
        } );
    }


    private void findToken(String response,int _id) throws JSONException {
        JSONObject responseObject = new JSONObject(response);
        Log.e("TTT","response object : " + responseObject);

        String check=responseObject.getString("response");
        Log.e("TTT","check : " + check);

        if (check.equals("success")){
            Log.e("TTT","response is success");
            JSONObject token=responseObject.getJSONObject("user");
            Log.e("TTT"," user array is :" +token);
            tokenOfDestination=  token.getString("user_token");
            Log.e("TTT","token is : " + tokenOfDestination);
        }

    }


    private interface getTokenInterface{
        void onSuccess(String result);
    }

    private void getTokenFromResponse(int method, final int _id,  final getTokenInterface callback) {
        String url = "http://"+Util.WAMP_SERVER_DOMAIN+"/piptalk/getTokenFromUid.php";
        Log.e("T","inside getTokenFromResponse");

        StringRequest loginRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("TTT","inside onResponse :"+response);
                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TTT","inside onErrorResponse :"+error);

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("user_id",String.valueOf(_id));
                return params;
            }
        };
        ApplicationClass.getInstance().addToRequestQueue(loginRequest);

    }


    @Override
    public void onStart() {
        super.onStart();

        //get old chat between the users
        chatAdapter.clear();

        Log.e("II","inside onStart");

        if(isNetworkAvailable()){
            if(chatAdapter.getCount()==0)  {
                Log.e("RR","inside onStart, getting old chat");
                getOldChat(user_id);
                //getLastMessageId ();
            }
        }else {
            getOldChatfromOfflineDB();
        }

    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        Log.e("II",""+activeNetworkInfo != null?"true":"  ");
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void getOldChat(final int contact_userid) {
        String URL="http://"+ Util.WAMP_SERVER_DOMAIN+"/piptalk/get_chat_between_two_users.php";
        StringRequest oldChatRequest=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.v("TT","Success is "+ response);
                    formateOldChat(response);
                } catch (JSONException e) {
                    Log.e("TT","Error is :" + e.getMessage());
                    Log.e("TT","Error is :" + e.getCause());
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TT","Error 2 is :" + error);

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String,String> params=new HashMap<>();
                params.put("user1_id",String.valueOf(new UserPreference(getActivity()).getUser().getId()));
                params.put("user2_id",String.valueOf(contact_userid));
                return params;
            }
        };
        ApplicationClass.getInstance().addToRequestQueue(oldChatRequest);
    }

    private void formateOldChat(String response) throws JSONException {
        ArrayList<ChatMessage> messagesList = new ArrayList<>();

        int count = 0;
        ChatMessage chatMessage ;
        JSONObject data=new JSONObject(response);
        Log.e("TT","inside formatting old chat , response is  "+response);

        if(data.getString("response").equals("success")) {

            TranslationTask translationTask = new TranslationTask();

            JSONArray messageArray=data.getJSONArray("user_messages");
            boolean isMine;
            if (messageArray.length()>0) {
                for (int i = 0; i < messageArray.length(); i++) {
                    chatMessage = new ChatMessage();
                    JSONObject message = messageArray.getJSONObject(i);
                    if (new UserPreference(getActivity()).getUser().getId() == message.getInt("sender_id")) {
                        isMine = true;
                    } else {
                        isMine = false;
                    }

                    chatMessage.setMessageBody(message.getString("message_body"));
                    chatMessage.setMsgId(message.getString("message_id"));
                    chatMessage.setMine(isMine);
                    int dayOrNight = Integer.valueOf(message.getString("time").substring(0, 2));
                    if (dayOrNight > 12) {
                        dayOrNight = dayOrNight - 12;
                        chatMessage.setTime(dayOrNight + ":" + message.getString("time").substring(3, 5) + " pm");
                    } else {
                        chatMessage.setTime(message.getString("time").substring(0, 5) + " am");
                    }
                    chatMessage.setMsg_lang_code(message.getString("msg_lang_code"));
                    chatMessage.setSender_id(message.getInt("sender_id"));
                    chatMessage.setReceiver_id(message.getInt("receiver_id"));
                    chatMessage.setDate(message.getString("date"));
                    chatMessage.setReceriver_has_image(message.getString("receiver_id"));
                    chatMessage.setSender_has_image(message.getString("sender_id"));
                    messagesList.add(chatMessage);
                    count = i;
                }
                count++;


                if (count > 50) {
                    clearChat(new UserPreference(getActivity()).getUser().getId(),
                            user_id,
                            Integer.valueOf(messagesList.get(count - 51).getMsgId()));
                }


                translationTask.execute(messagesList);
            }
        }else {
            Log.e("TT","not success");
        }

    }


    private void clearChat(final int user1_id , final int user2_id, final  int divider_message){

        Log.e("T","inside findUserID");

        clearWebService(Request.Method.POST, user1_id,user2_id,divider_message, new clearInerface() {
            @Override
            public void onSuccess(String result) {
                try {
                    checkSuccess(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } );
    }

    private void checkSuccess(String response) throws JSONException {
        JSONObject responseObject = new JSONObject(response);
        String check=responseObject.getString("response");

        if (check.equals("success")){
            Log.e("IDID"," delete is success ");
        }else{
            Log.e("T","response is fail");
        }
    }

    private interface clearInerface{
        void onSuccess(String result);
    }

    private void clearWebService(int method, final int user1_id , final int user2_id,final  int divider_message , final clearInerface callback) {
        String url = "http://"+Util.WAMP_SERVER_DOMAIN+"/piptalk/clear_chat.php";
        Log.e("T","inside getTokenFromResponse");

        StringRequest loginRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("IDID"," response is  " + response);
                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("T","inside onErrorResponse :"+error);

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("user1_id",String.valueOf(user1_id));
                params.put("user2_id",String.valueOf(user2_id));
                params.put("divider",String.valueOf(divider_message));

                return params;
            }
        };
        ApplicationClass.getInstance().addToRequestQueue(loginRequest);

    }

//    private void translateMessageBody(ArrayList<ChatMessage> arrayList)  {
//
////        if(msg_lang_code.equals(PreferenceManager.getDefaultSharedPreferences(getActivity())
////                .getString(getString(R.string.pref_language_key),
////                        getString(R.string.pref_language_default_value)))){
////
////        }
////            ChatMessage chatMessage =new ChatMessage("","", "", "",
////                    message.getString("message_body")
////                    ,message.getString("message_id"),
////                    isMine);
////
////            int dayOrNight = Integer.valueOf(message.getString("time").substring(0,2));
////            if(dayOrNight >12){
////                dayOrNight = dayOrNight -12 ;
////                chatMessage.setTime(dayOrNight+":"+message.getString("time").substring(3,5)+" pm");
////            }else {
////                chatMessage.setTime(message.getString("time").substring(0,5)+" am");
////            }
////
////            chatMessage.setMsg_lang_code(msg_lang_code);
////            chatMessage.setSender_id(message.getInt("sender_id"));
////            chatMessage.setReceiver_id(message.getInt("receiver_id"));
////            chatMessage.setDate(message.getString("date"));
////            chatMessage.setReceriver_has_image(message.getString("receiver_id"));
////            chatMessage.setSender_has_image(message.getString("sender_id"));
////         //   chatArrList.add(chatMessage);
////            chatAdapter.addMessageToAdapter(chatMessage);
//
////
////            TranslationTask translationTask = new TranslationTask();
////
////            translationTask.execute(msg_lang_code, //0
////                    PreferenceManager.getDefaultSharedPreferences(getActivity())
////                            .getString(getString(R.string.pref_language_key),
////                                    getString(R.string.pref_language_default_value)), // 1
////                    message_body, // 2
////                    message.getString("message_id"), // 3
////                    message.getString("time"), // 4
////                    message.getString("date"), // 5
////                    String.valueOf(isMine), // 6
////                    msg_lang_code, // 7
////                    String.valueOf(message.getInt("sender_id")), // 8
////                    String.valueOf(message.getInt("receiver_id")), // 9
////                    message.getString("receiver_id"), // 10
////                    message.getString("sender_id") // 11
////
////                    );
//
//
//
//
//    }

    private void getOldChatfromOfflineDB(){

        ArrayList<ChatMessage> chatList = new ArrayList<>();
        ChatMessage chatMessage;

        String[] PROJECTION = {SENDER_NAME, RECEIVER_NAME, MESSAGE_BODY, MESSAGE_LANG_CODE,MESSAGE_ID, TIME, DATE, SENDER_ID,
                RECEIVER_ID, RECEIVER_HAS_IMAGE, SENDER_HAS_IMAGE};
        int userID = getActivity().getSharedPreferences("user", 1).getInt("user_id", 0);
        String selection = "(sender_id="+userID+" AND receiver_id=" +user_id+") OR ( sender_id="+user_id+" AND receiver_id="+userID+")";
        String orderBy = "message_id ASC";

        Cursor c = getActivity().
                getContentResolver().query(ChatOfflineDBProvider.CONTENT_URI, PROJECTION, selection, null,orderBy);
        Log.e("LEN","cursor count : "+c.getCount());
        boolean first = c.moveToFirst();
        if (!first) {
            Toast.makeText(getActivity(), "Chat is empty", Toast.LENGTH_LONG).show();
        } else {
            //    mGridData_from_Database.clear();
            do {
                Log.e("LEN","pos is : "+c.getPosition());
                boolean isMine = false;
                if (new UserPreference(getActivity()).getUser().getUser_name()
                        .equals(c.getString(c.getColumnIndex(SENDER_NAME)))){
                    isMine=true;
                }
                chatMessage =new ChatMessage(
                        c.getString(c.getColumnIndex(SENDER_NAME)),
                        c.getString(c.getColumnIndex(RECEIVER_NAME)),
                        c.getString(c.getColumnIndex(MESSAGE_BODY)),
                        c.getString(c.getColumnIndex(MESSAGE_ID)),isMine);

                chatMessage.setMsg_lang_code(c.getString(c.getColumnIndex(MESSAGE_LANG_CODE)));
                chatMessage.setSender_id(c.getInt(c.getColumnIndex(SENDER_ID)));
                chatMessage.setReceiver_id(c.getInt(c.getColumnIndex(RECEIVER_ID)));
                chatMessage.setTime(c.getString(c.getColumnIndex(TIME)).substring(0,5));
                chatMessage.setDate(c.getString(c.getColumnIndex(DATE)));
                chatMessage.setSender_has_image(c.getString(c.getColumnIndex(SENDER_HAS_IMAGE)));
                chatMessage.setReceriver_has_image(c.getString(c.getColumnIndex(RECEIVER_HAS_IMAGE)));

                chatList.add(chatMessage);

            } while (c.moveToNext());

            c.close();
            Log.e("LEN","chat list size : "+chatList.size());
        }
        chatAdapter.SendMessageToAdapter(chatList);

    }

//    private void updateMessageFromNotification(String message){ /// expected errors coz of oflline database part <<<<<<
//
//           // String messageId = Util.getUniqueMessageId();
//
//            chatArrList.add(new ChatMessage("","", "","",message,NotificationMessageID,false)); /// expected error coz of offline db
/////  // part
//            chatAdapter.SendMessageToAdapter(chatArrList); // display it
//      //  sendMessageSeenToCloudDB(NotificationMessageID);
//
//    }


    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        super.onPause();
    }



    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() { /// expected errors for offline database
        @Override
        public void onReceive(Context context, Intent intent) { // message coming while app is in foreground

            // Get extra data included in the Intent
            Log.e("RR","inside BroadCast receiver chatFragment");
            String message = intent.getStringExtra("message");
            String message_id = intent.getStringExtra("message_id");

            ChatMessage msg =new ChatMessage(intent.getStringExtra("sender"),
                    tokenOfDestination, mMainUserName,mMainUserToken,
                    message,message_id,false);
            msg.setTime(intent.getStringExtra("time"));
            msg.setDate(intent.getStringExtra("date"));
            chatAdapter.addMessageToAdapter(msg);

            //set message being received
            //  setMessageReceived(message_id);

        }

    };


    private void setMessageReceived(final String message_id){
        String URL="http://"+Util.WAMP_SERVER_DOMAIN+"/piptalk/setMessageReceived.php";
        StringRequest setMessageRecvRequest=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params=new HashMap<>();
                params.put("message_id",message_id);
                return params;
            }
        };
        ApplicationClass.getInstance().addToRequestQueue(setMessageRecvRequest);
    }


    @Override
    public void onResume() {
        super.onResume();

        msg_editText.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if(!s.equals(""))
                {
                    sendButton.setVisibility(View.VISIBLE);
                    recordButton.setVisibility(View.GONE);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {

                if(s.length() > 0){
                    sendButton.setVisibility(View.VISIBLE);
                    recordButton.setVisibility(View.GONE);
                }else{
                    sendButton.setVisibility(View.GONE);
                    recordButton.setVisibility(View.VISIBLE);
                }
            }
        });


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                writtenMessage = msg_editText.getText().toString();

                msg_editText.setText("");

                timeNDdate = getTimeAndDate(); // gets time and date

                ChatMessage chatMessage = new ChatMessage(mMainUserName,
                        usernameOfDestination,
                        writtenMessage,
                        "",
                        true);

                int dayOrNight = Integer.valueOf(timeNDdate.get(0).substring(0,2));
                if(dayOrNight >12){
                    dayOrNight = dayOrNight -12 ;
                    chatMessage.setTime(dayOrNight+":"+timeNDdate.get(0).substring(3,5)+" pm");
                }else {
                    chatMessage.setTime(timeNDdate.get(0).substring(0,5)+" am");
                }
                chatMessage.setDate(timeNDdate.get(1)); // date

             //   chatArrList.add(chatMessage);
                chatAdapter.addMessageToAdapter(chatMessage);  // sending the message to the adapter to be displayed

                sendMessage( // send to cloud ? send to the other person + to offline : display "couldn't send"
                        writtenMessage,
                        timeNDdate.get(0), // time
                        timeNDdate.get(1), // date
                        new UserPreference(getActivity()).getUser().getId(),
                        user_id,chatMessage);

            }
        });

        msgListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                View v  =  getViewByPosition(position,msgListView);
//                if (v != null){
//                    if(v.findViewById(R.id.alert_not_sent).getVisibility() == View.VISIBLE){
//                        timeNDdate  = getTimeAndDate();
//                        sendMessage( // RESEND MESSAGE to cloud ? send to the other person + to offline : display "couldn't send"
//                                writtenMessage,
//                                localTime,
//                                fDate,
//                                new UserPreference(getActivity()).getUser().getId(),
//                                user_id,new ChatMessage());
//                    }else {
//                        Toast.makeText(getActivity(),"Message is sent",Toast.LENGTH_SHORT).show();
//                    }
//                }
//
                return false;
            }
        });

    }


    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }
    private ArrayList<String> getTimeAndDate(){
        //get current date
        Date cDate = new Date();
        String fDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);
        //get current time
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+2:00"));
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("HH:mm:ss"); // changed
        date.setTimeZone(TimeZone.getTimeZone("GMT+2:00"));
        String localTime = date.format(currentLocalTime);
        //localTime = localTime.substring(0,5)+":00";  /// fix this for database time column

        ArrayList<String> array= new ArrayList<String>();
        array.add(localTime);
        array.add(fDate);
        return array;
    }

    private void sendMessage(final String writtenMessage, final String localTime,
                             final String fDate, final int sender_id, final int receiver_id, final ChatMessage chatMessage){

        String url= "http://"+Util.WAMP_SERVER_DOMAIN+"/piptalk/insert_into_main_chat.php";
        StringRequest signUpRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("RR"," response is : "+response);

                prepareSend(response,writtenMessage,
                        localTime,fDate,chatMessage);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("RR"," response fail : "+error);
                View v =  getViewByPosition(chatArrList.size(),msgListView);
                v.findViewById(R.id.alert_not_sent).setVisibility(View.VISIBLE);
                Toast.makeText(getActivity(),"Message couldn't be sent! long click to resend",Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();

                Log.e("RR"," MEESAGE : "+writtenMessage);
                Log.e("RR"," TIME : "+localTime);
                Log.e("RR"," DATE : "+fDate);
                Log.e("RR"," SENDER ID : "+sender_id);
                Log.e("RR"," RECEIVER ID : "+receiver_id);

                params.put("message_body",writtenMessage);
                params.put("language_code",PreferenceManager.getDefaultSharedPreferences(getActivity()).
                        getString(getString(R.string.pref_language_key), ""));
                params.put("time",localTime);
                params.put("date",fDate);
                params.put("sender_id",String.valueOf(sender_id));
                params.put("receiver_id",String.valueOf(receiver_id));
                params.put("sent","1");
                params.put("received","0");
                return params;
            }
        };

        ApplicationClass.getInstance().addToRequestQueue(signUpRequest);

    }


    private void sendMessageToOfflineDB(String senderID,
                                        String receiverID,
                                        String msg, String time,
                                        String date, String sent,
                                        String received){

//        // getting language code
//        ArrayList<String> languageOptinsList;
//        ArrayList<String> languageValuesOfOptions;
//        languageOptinsList=new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.language_options)));
//        languageValuesOfOptions=new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.languages_values)));
//        int langValIndex=languageOptinsList.indexOf(mUserLangaue);
//

        ContentValues values = new ContentValues();
        values.put(ChatOfflineDBProvider.SENDER_ID,
                senderID);
        values.put(ChatOfflineDBProvider.SENDER_NAME, getActivity(). //  sender username
                getSharedPreferences(PREF_NAME,PRIVATE_MODE).getString("user_name",""));

        values.put(ChatOfflineDBProvider.SENDER_HAS_IMAGE,getActivity(). // sender has image
                getSharedPreferences(PREF_NAME,PRIVATE_MODE).getString("user_image",""));

        values.put(ChatOfflineDBProvider.RECEIVER_ID,
                receiverID);
        values.put(ChatOfflineDBProvider.RECEIVER_NAME,usernameOfDestination); // receiver username

        values.put(ChatOfflineDBProvider.RECEIVER_HAS_IMAGE,user_has_image); // user has image


        values.put(ChatOfflineDBProvider.MESSAGE_BODY,
                msg);

        values.put(ChatOfflineDBProvider.MESSAGE_LANG_CODE,
                PreferenceManager.getDefaultSharedPreferences(getActivity()).
                        getString(getString(R.string.pref_language_key), ""));

        values.put(ChatOfflineDBProvider.TIME,
                time);
        values.put(ChatOfflineDBProvider.DATE,
                date);
        values.put(ChatOfflineDBProvider.M_SENT,
                sent);
        values.put(ChatOfflineDBProvider.M_RECEIVED,
                received);

        getActivity().getContentResolver().insert(
                ChatOfflineDBProvider.CONTENT_URI, values);

        Toast.makeText(getActivity(), "Added to db ", Toast.LENGTH_SHORT).show();

    }


    private void prepareSend(String response, String msg,
                             String time, String date, ChatMessage chatMessage){

        SendMessageTask send = new SendMessageTask();

        try {

            JSONObject data=new JSONObject(response);
            String check=data.getString("response");

            if (check.equals("Done")){


                if(chatMessage != null){
                    chatMessage.setMsgId(String.valueOf(data.getInt("message_id")));
                    Log.e("MSGID","  msg id :  "+data.getInt("message_id"));

                }

                /// sending message to the other person
                send.execute(mMainUserName, // 0
                        msg, //1
                        usernameOfDestination, // 2
                        tokenOfDestination, // 3
                        String.valueOf(data.getInt("message_id")), // 4
                        time.substring(0,5), // 5
                        date); //6


                sendMessageToOfflineDB(String.valueOf(new UserPreference(getActivity()).getUser().getId()),
                        String.valueOf(user_id),
                        msg ,
                        time,
                        date,
                        "1",
                        "0");

                // sender
                // sender id            (global)
                //sender has image       (global)
                // receiver
                // receiver token       (global)
                // receiver id         (global)
                // receiver has image (global)
                // msg
                // msg id
                // time
                // date
                //  setMessageBeingSent(data.getInt("message_id"));
            }else {
                Toast.makeText(getActivity(),"Message couldn't be sent ELSE",Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            Log.e("KKK"," error "+e.getMessage());
            Toast.makeText(getActivity(),"Message couldn't be sent JSONException",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    private void setMessageBeingSent(final int message_id){
        String URL="http://"+Util.WAMP_SERVER_DOMAIN+"/piptalk/setMessageSent.php";
        StringRequest setSentRequest=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.v("ResponseForSetMsgSent",response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params=new HashMap<>();
                params.put("message_id",String.valueOf(message_id));
                return params;
            }
        };

        ApplicationClass.getInstance().addToRequestQueue(setSentRequest);
    }


//    private void getLastMessageId (){
//
//        getStringFromResponse(Request.Method.POST,new MessageCallback() {
//            @Override
//            public void onSuccess(String result) {
//
//                try {
//                    isMember(result);
//                } catch (JSONException e) {
//                    Log.e("PP"," is memeber (catsh) : "+e);
//                    e.printStackTrace();
//                }
//
//            }
//        });
//
//    }

//    private void isMember(String response) throws JSONException {
//        JSONObject responseObject = new JSONObject(response);
//        String check=responseObject.getString("response");
//
//        if (check.equals("success")){
//            Log.e("II","message id is : "+responseObject.getString("message_id"));
//            message_ID =Integer.valueOf(responseObject.getString("message_id"));
//            Log.e("II","message id Before incrementing: "+message_ID);
//                    message_ID++;
//            Log.e("II","message id is : "+message_ID);
//        }
//    }


//    private interface MessageCallback{
//        void onSuccess(String result);
//    }

//    public void getStringFromResponse(int method, final MessageCallback callback) {
//
//        String url = "http://"+ Util.WAMP_SERVER_DOMAIN+"/piptalk/get_latest_message_id.php";
//        StringRequest loginRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                callback.onSuccess(response);
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.v("Response ","Error"+error);
//            }
//        }){
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String,String> params=new HashMap<>();
//                return params;
//            }
//        };
//        ApplicationClass.getInstance().addToRequestQueue(loginRequest);
//
//    }


    public  class SendMessageTask extends AsyncTask<String,Void,Void> {



//        @Override
//        protected void onPostExecute(ArrayList arrayList) {
//            super.onPostExecute(arrayList);
//
//        }

        @Override
        protected Void doInBackground(String... Params) {

            String senderId = "995953923725";

            CcsClient ccsClient = CcsClient.prepareClient(senderId, Util.FCM_SERVER_API_KEY, true);

            try {
                ccsClient.connect();
            } catch (XMPPException e) {
                e.printStackTrace();
            } catch (SmackException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Map<String, String> dataPayload = new HashMap<>();
            dataPayload.put(Util.PAYLOAD_ATTRIBUTE_SENDER,Params[0]);
            dataPayload.put(Util.PAYLOAD_ATTRIBUTE_MESSAGE, Params[1]);
            dataPayload.put(Util.PAYLOAD_ATTRIBUTE_RECIPIENT,Params[2]);
            dataPayload.put(Util.PAYLOAD_ATTRIBUTE_MESSAGE_ID,Params[4]);
            dataPayload.put(Util.PAYLOAD_ATTRIBUTE_TIME,Params[5]);
            dataPayload.put(Util.PAYLOAD_ATTRIBUTE_DATE,Params[6]);
            dataPayload.put(Util.PAYLOAD_ATTRIBUTE_RECEIVER_HAS_IMAGE,user_has_image);
            dataPayload.put(Util.PAYLOAD_ATTRIBUTE_RECEIVER_ID,String.valueOf(user_id));
            dataPayload.put(Util.PAYLOAD_ATTRIBUTE_SENDER_ID,mMainUser_id);
            dataPayload.put(Util.PAYLOAD_ATTRIBUTE_SENDER_HAS_IMAGE,mMainUser_has_image);
            dataPayload.put(Util.PAYLOAD_ATTRIBUTE_LANGUAGE_CODE,PreferenceManager.
                    getDefaultSharedPreferences(getActivity()).
                    getString(getString(R.string.pref_language_key), ""));

            // sender
            // sender id            (global)
            //sender has image       (global)
            // receiver
            // receiver token
            // receiver id         (global)
            // receiver has image (global)
            // msg
            // msg id
            // time
            // date
            String messageId = Params[4] ; // mesg id

            CcsOutMessage message = new CcsOutMessage(Params[3], messageId, dataPayload);  //// <<< params[3] is token of destination
            String jsonRequest = MessageHelper.createJsonOutMessage(message);

            ccsClient.send(jsonRequest);

            return null ;

//            data.add(0,Params[1]);  // MESSAGE
//            data.add(1,time); // TIME
//            data.add(2,Params[6]); // DATE
        }
    }

    public class TranslationTask extends AsyncTask<ArrayList<ChatMessage>,Void,ArrayList<ChatMessage>>
    {

        @Override
        protected void onPostExecute(ArrayList<ChatMessage> aVoid) {
            super.onPostExecute(aVoid);
            if(aVoid.size() == 0) {
                Log.e("Chat Translate","EMPTY");
            } else {
                    chatAdapter.SendMessageToAdapter(aVoid);
                }
        }


        @Override
        protected ArrayList<ChatMessage> doInBackground(ArrayList<ChatMessage>... strings) {


            ArrayList<ChatMessage> arrayList = new ArrayList<ChatMessage>();
            arrayList = strings[0];
            String myLanguageCode = PreferenceManager.getDefaultSharedPreferences(getActivity())
                    .getString(getString(R.string.pref_language_key),
                            getString(R.string.pref_language_default_value));

            for(int i = 0; i < arrayList.size(); i++) {
                if (!arrayList.get(i).getMsg_lang_code().equals(myLanguageCode)) {

                    Log.e("LL"," inside loop "+i);
                    Log.e("LL"," message before translation "+arrayList.get(i).getmMessageBody());


                    //connect to api services
                    HttpURLConnection urlConnection = null;
                    BufferedReader reader = null;
                    String translateResponseStr = null;
                    String urlString = "http://api.microsofttranslator.com/V2/Ajax.svc/Translate?oncomplete=mycallback&appId=68D088969D79A8B23AF8585CC83EBA2A05A97651&from=" + arrayList.get(i).getMsg_lang_code() + "&to=" + myLanguageCode + "&text=" + arrayList.get(i).getmMessageBody();
                    urlString = urlString.replaceAll(" ", "%20");

                    try {
                        URL url = new URL(urlString);
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
                    } finally {
                        if (urlConnection != null) {
                            urlConnection.disconnect();
                        }

                        if (reader != null) {
                            try {
                                reader.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    if (translateResponseStr.length() > 0) {
                        String MyActuallyTranslate = "";
                        for (int j = 13; j < translateResponseStr.length() - 5; j++) {
                            MyActuallyTranslate = MyActuallyTranslate + translateResponseStr.charAt(j);
                        }
                        Log.e("LL","message after translation "+MyActuallyTranslate);
                        arrayList.get(i).setMessageBody(MyActuallyTranslate);
                    }
                }
            }

            return arrayList;
        }
        }
    }


