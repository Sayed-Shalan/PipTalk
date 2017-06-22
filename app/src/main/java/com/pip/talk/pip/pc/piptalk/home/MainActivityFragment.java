package com.pip.talk.pip.pc.piptalk.home;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.pip.talk.pip.pc.piptalk.ApplicationClass;
import com.pip.talk.pip.pc.piptalk.R;
import com.pip.talk.pip.pc.piptalk.User_Model.UserPreference;
import com.pip.talk.pip.pc.piptalk.chat.ChatActivity;
import com.pip.talk.pip.pc.piptalk.chat.ChatMessage;
import com.pip.talk.pip.pc.piptalk.offlineDB.ChatOfflineDBProvider;
import com.pip.talk.pip.pc.piptalk.profile.ViewUserProfileActivity;
import com.pip.talk.pip.pc.piptalk.xmpp.util.Util;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MainActivityFragment extends Fragment {

    private ListView contactsList;
    private TextView username;
    private ImageView profilePicture;
    private ArrayList<ChatMessage> messagesList;
    private ArrayList<ChatMessage> messagesListDB;
    private ArrayList<ChatMessage> contactsArraylist;

    private ContactsListAdapter adapter;

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



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        contactsList = (ListView) rootView.findViewById(R.id.contactsList);
        messagesList=new ArrayList<>();
        adapter = new ContactsListAdapter(getActivity(),0, messagesList);
        contactsList.setAdapter(adapter);


        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
                new IntentFilter("new_messages_num"));


        // check if there is previous chats in database
        // if there is, bring it, else display fragment

      /*  if (messagesList.size()<1){
           // Toast.makeText(getActivity(),"mafish data y 3m sayed",Toast.LENGTH_LONG).show();
            ChatMessage chatMessage=new ChatMessage("Mona","555555","Ahmed","55","hey zamalkawy","5",true);
            messagesList.add(chatMessage);
            contactsList = (ListView) rootView.findViewById(R.id.contactsList);
            ContactsListAdapter adapter = new ContactsListAdapter(getActivity(), R.layout.contact_list_item, messagesList);
            contactsList.setAdapter(adapter);
        }else {*/

        //}
        return rootView;

    }
    @Override
    public void onStart() {
        super.onStart();

        adapter.clear();
        if(isNetworkAvailable()){
            Log.e("II","there's network, grabing data from cloud" );
            getUserPreviousContacts();
        }else{

            Toast.makeText(getActivity(),"No internet connection",Toast.LENGTH_SHORT).show();

            getContactsFromOfflineDB();
        }

    }

    @Override
    public void onResume() {
        super.onResume();


        //action for short click
        contactsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), "opening chat page..", Toast.LENGTH_SHORT).show();

                username = (TextView) view.findViewById(R.id.contactName);
                profilePicture = (ImageView) view.findViewById(R.id.contactProfileImage);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Intent chatActivity = new Intent(getActivity(), ChatActivity.class);
                    chatActivity.putExtra("contact_pp",adapter.getItem(position).getContactpp());
                    if (adapter.getItem(position).isMine()){
                        //   chatActivity.putExtra("contact_name",adapter.getItem(position).getmReceiverUserName());
                        chatActivity.putExtra("username",adapter.getItem(position).getmReceiverUserName());
                        chatActivity.putExtra("user_token",adapter.getItem(position).getmReceiverUserToken());
                        chatActivity.putExtra("user_id",adapter.getItem(position).getReceiver_id());
                    }else {
                        // chatActivity.putExtra("contact_name",adapter.getItem(position).getmSenderUserName());
                        chatActivity.putExtra("username",adapter.getItem(position).getmSenderUserName());
                        chatActivity.putExtra("user_token",adapter.getItem(position).getmSenderUserToken());
                        chatActivity.putExtra("user_id",adapter.getItem(position).getSender_id());
                    }
                    Pair<View, String> p1 = Pair.create((View) profilePicture, "ProfilePicture");
                    Pair<View, String> p2 = Pair.create((View) username, "Username");
                    chatActivity.addCategory("from_main");
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), p1, p2);
                    startActivity(chatActivity, options.toBundle());

                } else {
                    Intent chatActivity = new Intent(getActivity(), ChatActivity.class);
                    chatActivity.putExtra("contact_pp",adapter.getItem(position).getContactpp()); //
                    if (adapter.getItem(position).isMine()){
                        // chatActivity.putExtra("contact_name",adapter.getItem(position).getmReceiverUserName());
                        chatActivity.putExtra("username",adapter.getItem(position).getmReceiverUserName());
                        chatActivity.putExtra("user_token",adapter.getItem(position).getmReceiverUserToken());
                        chatActivity.putExtra("user_id",adapter.getItem(position).getReceiver_id());
                    }else {
                        // chatActivity.putExtra("contact_name",adapter.getItem(position).getmSenderUserName());
                        chatActivity.putExtra("username",adapter.getItem(position).getmSenderUserName());
                        chatActivity.putExtra("user_token",adapter.getItem(position).getmSenderUserToken());
                        chatActivity.putExtra("user_id",adapter.getItem(position).getSender_id());
                    }
                    chatActivity.addCategory("from_main");
                    ActivityCompat.startActivityForResult(getActivity(),chatActivity,0,null);
                }

            }
        });

        //action for long click
        contactsList.setLongClickable(true);
        contactsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
                final CharSequence [] items= {"Delete user","View user profile","Cancel"};
                final AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());
                if (adapter.getItem(position).isMine()){
                    builder.setTitle(adapter.getItem(position).getmReceiverUserName());
                }else{
                    builder.setTitle(adapter.getItem(position).getmSenderUserName());
                }
                builder.setItems(items,new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (items[i].equals("Delete user")){
                            //remove from cloud
                            deleteChatBetweenUsers(adapter.getItem(position).getSender_id(),adapter.getItem(position).getReceiver_id());
                            deleteFromOfflineDB(adapter.getItem(position).getSender_id(),adapter.getItem(position).getReceiver_id());
                            //delete chat from offline database
                            //method here ----------------------------------------------------------------------------------------------

                            //delete from listview
                            adapter.remove(adapter.getItem(position));

                        }else if(items[i].equals("View user profile")){

                            Intent intent=new Intent(getActivity(), ViewUserProfileActivity.class);
                            Toast.makeText(getActivity(),"lol",Toast.LENGTH_SHORT).show();
                            if (adapter.getItem(position).isMine()){
                                intent.putExtra("username",adapter.getItem(position).getmReceiverUserName());
                                intent.putExtra("profile_pic",String.valueOf(adapter.getItem(position).getReceiver_id()));
                                intent.putExtra("user_id",String.valueOf(adapter.getItem(position).getReceiver_id()));
                                Log.e("22","contact id before sending 1 :"+String.valueOf(adapter.getItem(position).getReceiver_id()));
                            }else {
                                intent.putExtra("username",adapter.getItem(position).getmSenderUserName());
                                intent.putExtra("profile_pic",String.valueOf(adapter.getItem(position).getSender_id()));
                                intent.putExtra("user_id",String.valueOf(adapter.getItem(position).getSender_id()));
                                Log.e("22","contact id before sending 2 :"+String.valueOf(adapter.getItem(position).getSender_id()));
                            }
                            intent.addCategory("fromMain_viewPro");
                            ActivityCompat.startActivityForResult(getActivity(),intent,0,null);
                            getActivity().finish();
                        }else if (items[i].equals("Cancel")){
                            dialogInterface.dismiss();
                        }
                    }
                });
                builder.show();

                return true;
            }
        });

    }


    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        super.onPause();
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() { /// expected errors for offline database
        @Override
        public void onReceive(Context context, Intent intent) { // message coming while app is in foreground

            // Get extra data included in the Intent
            ArrayList<ChatMessage> arrayList =new ArrayList<>();
            Log.e("RR","inside BroadCast receiver chatFragment");
            String message = intent.getStringExtra("message");
            String message_id = intent.getStringExtra("message_id");

           int pos = 0;
            for (int i = 0 ; i<adapter.getCount(); i++){
                if (adapter.getItem(i).getmSenderUserName().equals(intent.getStringExtra("sender"))){
                    pos = i;
                }
            }

            adapter.getItem(pos).setmSenderUserName(intent.getStringExtra("sender"));
            adapter.getItem(pos).setmSenderUserToken(messagesList.get(pos).getmSenderUserToken());
            adapter.getItem(pos).setmReceiverUserName(messagesList.get(pos).getmReceiverUserName());
            adapter.getItem(pos).setmReceiverUserToken(messagesList.get(pos).getmReceiverUserToken());
            adapter.getItem(pos).setMessageBody(message);
            adapter.getItem(pos).setMsgId(message_id);
            adapter.getItem(pos).setMine(false);

            int dayOrNight = Integer.valueOf(intent.getStringExtra("time").substring(0,2));
            if(dayOrNight >12){
                dayOrNight = dayOrNight -12 ;
                adapter.getItem(pos).setTime(dayOrNight+":"+intent.getStringExtra("time").substring(3,5)+" pm");
            }else {
                adapter.getItem(pos).setTime(intent.getStringExtra("time").substring(0,5)+" am");
            }

            adapter.getItem(pos).setDate(intent.getStringExtra("date"));
            adapter.getItem(pos).setContactpp(messagesList.get(pos).getContactpp());
            adapter.getItem(pos).setFromOffline(false);
            adapter.notifyData(pos);

            //set message being received
            //  setMessageReceived(message_id);

        }

    };



    private void deleteFromOfflineDB(int sender_id,int receiver_id){
        ContentResolver cr = getActivity().getContentResolver();
        String where = "( sender_id= "+sender_id+" AND receiver_id= "+receiver_id+") "  +" OR (receiver_id= "+sender_id+ " AND sender_id= "+receiver_id+" )";
        cr.delete(ChatOfflineDBProvider.CONTENT_URI, where, null);
        Toast.makeText(getActivity(),
                "User has been deleted", Toast.LENGTH_SHORT).show();
    }
    //delete chat between two users from cloud
    private void deleteChatBetweenUsers(final int id1, final int id2){
        String URL ="http://"+ Util.WAMP_SERVER_DOMAIN+"/piptalk/delete_chat_between_two_users.php";
        StringRequest deleteChatRequest=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
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
                params.put("user1_id",String.valueOf(id1));
                params.put("user2_id",String.valueOf(id2));
                return params;
            }
        };
        ApplicationClass.getInstance().addToRequestQueue(deleteChatRequest);
    }

    //check internet connectivity
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        Log.e("II",""+activeNetworkInfo != null?"true":"  ");
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void getContactsFromOfflineDB() {

        messagesListDB = new ArrayList<ChatMessage>();
        contactsArraylist = new ArrayList<ChatMessage>();
        ArrayList<String> messageIDList = new ArrayList<>();
        String contactName = "";
        String[] PROJECTION = {SENDER_NAME, RECEIVER_NAME, MESSAGE_BODY,MESSAGE_LANG_CODE, MESSAGE_ID, TIME, DATE, SENDER_ID,
                RECEIVER_ID, RECEIVER_HAS_IMAGE, SENDER_HAS_IMAGE};


        int userID = getActivity().getSharedPreferences("user", 1).getInt("user_id", 0);
        ArrayList<String> isdisplayed = new ArrayList<>();

        String selection = "sender_id=" + userID + " OR receiver_id=" + userID;
        String orderBy = "date DESC,time DESC";

        Cursor c = getActivity().
                getContentResolver().
                query(ChatOfflineDBProvider.CONTENT_URI, PROJECTION, selection, null, orderBy);

        if (!c.moveToFirst()) {
            Toast.makeText(getActivity(), "No Contacts", Toast.LENGTH_LONG).show();
        } else {
            //   mGridData_from_Database.clear();
            do {
                Log.e("II", "cursor count is : " + c.getCount());

                boolean isMine = false;
                boolean user_2_exists;

                if (new UserPreference(getActivity()).
                        getUser().getUser_name()
                        .equals(c.getString(c.getColumnIndex(ChatOfflineDBProvider.SENDER_NAME)))) {
                    isMine = true;
                }

                if (isMine) {
                    user_2_exists = checkUser2(isdisplayed, c.getString(c.getColumnIndex(RECEIVER_NAME)));
                    contactName = c.getString(c.getColumnIndex(RECEIVER_NAME)); // sender is my user name, i need the other name
                } else {
                    user_2_exists = checkUser2(isdisplayed, c.getString(c.getColumnIndex(SENDER_NAME)));
                    contactName = c.getString(c.getColumnIndex(SENDER_NAME)); // receiver is my user name, i need the other name
                }

                // perform query to get the last message made with that contact
                // and send them both to the adapter

                String messageSelection = "sender_name='"+contactName+"' OR receiver_name='"+contactName+"'";
                String messageOrderBy = "date DESC, time DESC LIMIT 1";

                Cursor messageCursor = getActivity().
                        getContentResolver().
                        query(ChatOfflineDBProvider.CONTENT_URI, PROJECTION, messageSelection, null, messageOrderBy);

                if (!messageCursor.moveToFirst()) {
                    Toast.makeText(getActivity(), "No Contacts", Toast.LENGTH_LONG).show();
                } else {

                    String mID= messageCursor.getString(messageCursor.getColumnIndex(MESSAGE_ID));

                    ChatMessage chatMessage1 = new ChatMessage(
                            messageCursor.getString(messageCursor.getColumnIndex(MESSAGE_BODY)), mID,
                            messageCursor.getString(messageCursor.getColumnIndex(TIME)),
                            messageCursor.getString(messageCursor.getColumnIndex(DATE)),
                            messageCursor.getInt(messageCursor.getColumnIndex(SENDER_ID)),
                            messageCursor.getInt(messageCursor.getColumnIndex(RECEIVER_ID)));
                    chatMessage1.setUserContact(contactName);

                    String senderName = messageCursor.getString(messageCursor.getColumnIndex(SENDER_NAME));

                    if(new UserPreference(getActivity()).getUser().getUser_name().equals(senderName)){
                        chatMessage1.setMine(true);
                        chatMessage1.setContactpp(messageCursor.getString(messageCursor.getColumnIndex(RECEIVER_HAS_IMAGE)));

                    }else{
                        chatMessage1.setMine(false);
                        chatMessage1.setContactpp(messageCursor.getString(messageCursor.getColumnIndex(SENDER_HAS_IMAGE)));
                    }

                    chatMessage1.setMsg_lang_code(messageCursor.getString(messageCursor.getColumnIndex(MESSAGE_LANG_CODE)));
                    chatMessage1.setFromOffline(true);
                    chatMessage1.setmSenderUserName(senderName);
                    chatMessage1.setmReceiverUserName(messageCursor.getString(messageCursor.getColumnIndex(RECEIVER_NAME)));
                    chatMessage1.setReceriver_has_image(messageCursor.getString(messageCursor.getColumnIndex(RECEIVER_HAS_IMAGE)));
                    chatMessage1.setSender_has_image(messageCursor.getString(messageCursor.getColumnIndex(SENDER_HAS_IMAGE)));

                    if(contactsArraylist.size()==0){
                        contactsArraylist.add(chatMessage1);
                        messageIDList.add(mID);
                    }


                    for(int k =0; k<messageIDList.size();k++){
                        if(messageIDList.size()!=0){
                            if(!contains(messageIDList,mID)){
                                contactsArraylist.add(chatMessage1);
                                messageIDList.add(mID);
                            }
                        }else{
                            contactsArraylist.add(chatMessage1);
                            messageIDList.add(mID);
                        }
                    }

                }
            } while (c.moveToNext());

            adapter.sendMessageToAdapter(contactsArraylist);
        }
    }

    boolean contains(ArrayList<String> list, String name) {
        for (String item : list) {
            if (item.equals(name)) {
                return true;
            }
        }
        return false;
    }
    private void getUserPreviousContacts(){
        String URL="http://"+ Util.WAMP_SERVER_DOMAIN+"/piptalk/get_previous_contacts.php";
        StringRequest previousContactsRequest=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("OO","onResponse , response: " +response);
                try {
                    formateUserMessages(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("OO","onResponse , response: " +e.getCause() +" === "+ e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                Log.e("OO"," id to query with is : " + new UserPreference(getActivity()).getUser().getId());
                params.put("id",String.valueOf(new UserPreference(getActivity()).getUser().getId()));
                return params;
            }
        };
        ApplicationClass.getInstance().addToRequestQueue(previousContactsRequest);
    }
    private void formateUserMessages(String data) throws JSONException {

        JSONObject dataObject = new JSONObject(data);

        if (dataObject.getString("response").equals("success")){

            JSONArray messagesArray=dataObject.getJSONArray("user_messages");

            Log.e("OO","messageArray is :"+messagesArray);

            if(messagesArray.length()>0){
                ArrayList<String> isdisplayed=new ArrayList<>();

                for(int i=0;i<messagesArray.length();i++){
                    boolean user_2_exists;
                    boolean isMine;
                    JSONObject messageObject= messagesArray.getJSONObject(i);

                    if (new UserPreference(getActivity()).getUser().
                            getUser_name().equals(messageObject.getString("sender_name"))){
                        isMine=true;
                    }else {
                        isMine = false;
                    }


                    if (isMine){
                        user_2_exists=checkUser2(isdisplayed,messageObject.getString("receiver_name"));
                    }else {
                        user_2_exists=checkUser2(isdisplayed,messageObject.getString("sender_name"));
                    }


                    if(!user_2_exists) {

                            if (isMine) {
                                 ChatMessage chatMessageItem = new ChatMessage(messageObject.getString("sender_name"),
                                        messageObject.getString("sender_token"),
                                        messageObject.getString("receiver_name"),
                                        messageObject.getString("receiver_token"),
                                        messageObject.getString("message_body")
                                        ,messageObject.getString("message_id"),
                                        true);

                                int dayOrNight = Integer.valueOf(messageObject.getString("time").substring(0,2));
                                if(dayOrNight >12){
                                    dayOrNight = dayOrNight -12 ;
                                    chatMessageItem.setTime(dayOrNight+":"+messageObject.getString("time").substring(3,5)+" pm");
                                }else {
                                    chatMessageItem.setTime(messageObject.getString("time").substring(0,5)+" am");
                                }

                                chatMessageItem.setDate(messageObject.getString("date"));
                                chatMessageItem.setSender_id(messageObject.getInt("sender_id"));
                                chatMessageItem.setReceiver_id(messageObject.getInt("receiver_id"));
                                chatMessageItem.setFromOffline(false);

                                ////////////
                                isdisplayed.add(messageObject.getString("receiver_name"));
                                chatMessageItem.setContactpp(messageObject.getString("receiver_has_image"));
                                chatMessageItem.setUserContact(messageObject.getString("receiver_name"));
                                messagesList.add(chatMessageItem);
                                adapter.sendMessageToAdapter(messagesList);
                            } else {
                                isdisplayed.add(messageObject.getString("sender_name"));
                                translateMessageBody(
                                        messageObject.getString("msg_lang_code"),
                                        messageObject.getString("message_body"),
                                        messageObject);
                            }
                            //chatMessage.setContactpp(MainActivityFragment.contactPP);
                            // Toast.makeText(getActivity(),chatMessage.getMessageBody(),Toast.LENGTH_LONG).show();

                    }
                }



            }

        }

    }

    private void translateMessageBody(String lang_code,String message_body,JSONObject messageObject ) throws JSONException {

//            ArrayList<String> languageOptinsList;
//            ArrayList<String> languageValuesOfOptions;
            TranslationTask translationTask = new TranslationTask();
           // languageOptinsList=new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.language_options)));
        //languageValuesOfOptions=new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.languages_values)));
          //  int langValIndex=languageValuesOfOptions.indexOf(lang_code);
            translationTask.execute(lang_code,
                    PreferenceManager.getDefaultSharedPreferences(getActivity())
                            .getString(getString(R.string.pref_language_key),
                                    getString(R.string.pref_language_default_value)),
                    message_body,
                    messageObject.getString("sender_name"),
                    messageObject.getString("sender_token"),
                    messageObject.getString("receiver_name"),
                    messageObject.getString("receiver_token")
                    , messageObject.getString("message_id"),
                    messageObject.getString("time"),
                    messageObject.getString("date"),
                    String.valueOf(messageObject.getInt("sender_id")),
                    String.valueOf(messageObject.getInt("receiver_id")),
                    messageObject.getString("sender_has_image")
                    );

    }
    private boolean checkUser2(ArrayList<String> list,String friend){
        for (int i=0;i<list.size();i++){
            if (list.get(i).equals(friend)){
                return true;
            }
        }
        return false;
    }


    public class TranslationTask extends AsyncTask<String,Void,String[]>
    {

        @Override
        protected void onPostExecute(String[] aVoid) {
            super.onPostExecute(aVoid);

            if (aVoid.length==0){
                Log.e("Main Translate","length=0");

            }else {

                if (aVoid[0].length()!=0) {
                    // Toast.makeText(getApplicationContext(),aVoid,Toast.LENGTH_SHORT).show();
                    String MyActuallyTranslate = "";

                    for (int i = 13; i < aVoid[0].length() - 5; i++) {
                        MyActuallyTranslate = MyActuallyTranslate + aVoid[0].charAt(i);
                    }

                    Log.e("OO"," inside onPostExecute ,  text  after trimming is : " + MyActuallyTranslate);

                    ChatMessage chatMessageItem = new ChatMessage(aVoid[1],
                            aVoid[2],
                            aVoid[3],
                            aVoid[4],
                            MyActuallyTranslate
                            , aVoid[5],
                            false);

                    int dayOrNight = Integer.valueOf(aVoid[6].substring(0,2));
                    if(dayOrNight >12){
                        dayOrNight = dayOrNight -12 ;
                        chatMessageItem.setTime(dayOrNight+":"+aVoid[6].substring(3,5)+" pm");
                    }else {
                        chatMessageItem.setTime(aVoid[6].substring(0,5)+" am");
                    }
                    chatMessageItem.setDate(aVoid[7]);
                    chatMessageItem.setSender_id(Integer.parseInt(aVoid[8]));
                    chatMessageItem.setReceiver_id(Integer.parseInt(aVoid[9]));
                    chatMessageItem.setFromOffline(false);
                    chatMessageItem.setContactpp(aVoid[10]);
                    chatMessageItem.setUserContact(aVoid[1]);
                    messagesList.add(chatMessageItem);
                    adapter.sendMessageToAdapter(messagesList);
                }
            }
        }

        @Override
        protected String[] doInBackground(String... strings) {


            Log.e("OO"," inside doInBackground ");

            //connect to api services
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String translateResponseStr = null;
            String urlString ="http://api.microsofttranslator.com/V2/Ajax.svc/Translate?oncomplete=mycallback&appId=68D088969D79A8B23AF8585CC83EBA2A05A97651&from="+strings[0]+"&to="+strings[1]+"&text="+strings[2];
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
                }

                Log.e("OO"," inside doInBackground ,  text  before trimming is : " + translateResponseStr);

                String [] data={translateResponseStr ,strings[3],strings[4],strings[5],strings[6],strings[7],strings[8],
                strings[9],strings[10],strings[11],strings[12]};
                return data;
            }
        }
    }
}
