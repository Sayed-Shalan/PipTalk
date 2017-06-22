package com.pip.talk.pip.pc.piptalk.profile;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.pip.talk.pip.pc.piptalk.ApplicationClass;
import com.pip.talk.pip.pc.piptalk.R;
import com.pip.talk.pip.pc.piptalk.User_Model.UserPreference;
import com.pip.talk.pip.pc.piptalk.chat.ChatActivity;
import com.pip.talk.pip.pc.piptalk.home.MainActivity;
import com.pip.talk.pip.pc.piptalk.offlineDB.UserOfflineDBProvider;
import com.pip.talk.pip.pc.piptalk.search.SearchActivity;
import com.pip.talk.pip.pc.piptalk.xmpp.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class ViewUserProfileActivity extends AppCompatActivity {

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Typeface ralewaySemiBold;
    private Typeface ralewayRegular;
    private TextView status;
    private TextView statusTime;
    private TextView language;
    private TextView languageHeader;
    private TextView dateOfBirth;
    private TextView date_note;
    private TextView phoneNum;
    private TextView phoneNum_note;
    private TextView other;
    private ImageView imgpp;

    private Button startChat;
    private String username_;
    private String user_has_image;

    private String profile_pic;
    private Context globalContext;
    private Activity globalActivity;
    private String userToken="";




    private final String USER__ID = "user_id";
    private final String USERNAME = "username";
    private final String USER_TOKEN = "user_token";
    private final String NATIVE_LANGUAGE = "native_language";
    private final String GENDER = "gender";
    private final String HAS_IMAGE = "has_image";
    private final String STATUS = "status";
    private final String PHONE_NUMBER = "phone_number";
    private final String DOB = "date_of_birth";

    private String hasImage;
    private String gender;
    private String dob;
    private String phone_num;

    private String callingActivity="";
    private int contact_id;
    String ImgURL="http://"+ Util.WAMP_SERVER_DOMAIN+"/piptalk/images/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.view_profile_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        collapsingToolbarLayout =  (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout_view);
        status = (TextView) findViewById(R.id.view_user_status);
        statusTime = (TextView) findViewById(R.id.view_status_time);
        language = (TextView) findViewById(R.id.view_language);
        languageHeader = (TextView) findViewById(R.id.view_header_language);
        startChat= (Button) findViewById(R.id.startChatBtn) ;
        dateOfBirth = (TextView) findViewById(R.id.profile_birth_date_view);
        imgpp=(ImageView) findViewById(R.id.imageView_ViewUser);
        date_note = (TextView) findViewById(R.id.date_note_view);
        phoneNum = (TextView) findViewById(R.id.profile_phone_number_view);
        phoneNum_note =(TextView) findViewById(R.id.phoneNum_note_view);
        other = (TextView) findViewById(R.id.other);

        globalContext =this;
        globalActivity = this;
        ralewayRegular = Typeface.createFromAsset(this.getAssets(),"Raleway-Regular.ttf");
        ralewaySemiBold = Typeface.createFromAsset(this.getAssets(),"Raleway-SemiBold.ttf");

        status.setTypeface(ralewayRegular);
        statusTime.setTypeface(ralewayRegular);
        language.setTypeface(ralewayRegular);
        languageHeader.setTypeface(ralewayRegular);
        dateOfBirth.setTypeface(ralewayRegular);
        date_note.setTypeface(ralewayRegular);
        phoneNum.setTypeface(ralewayRegular);
        phoneNum_note.setTypeface(ralewayRegular);

        callingActivity = getCallingActivity().getClassName();

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            if(callingActivity.equals("com.pip.talk.pip.pc.piptalk.search.SearchActivity")){
                ViewCompat.setTransitionName(findViewById(R.id.app_bar_layout_view),"viewProfilePic");
                collapsingToolbarLayout.setTransitionName("viewProfileUsername");

            }else if(callingActivity.equals("com.pip.talk.pip.pc.piptalk.chat.ChatActivity")){
//                collapsingToolbarLayout.setTransitionName("Username");
//                ViewCompat.setTransitionName(findViewById(R.id.app_bar_layout_view),"ProfilePicture");
            }
        }

        Log.e("22","inside oncreate ");

        if (getCallingActivity() != null) {

            Intent intent = getIntent();

            if(callingActivity.equals("com.pip.talk.pip.pc.piptalk.search.SearchActivity")){
                startChat.setVisibility(View.VISIBLE);
                if(intent.hasCategory("userData")) {
                    Bundle bundle = intent.getExtras();
                    username_ = bundle.getString("username");
                    profile_pic = bundle.getString("profile_pic");
                    contact_id= Integer.valueOf(profile_pic) ;
                }

            }else if(callingActivity.equals("com.pip.talk.pip.pc.piptalk.chat.ChatActivity")){
                startChat.setVisibility(View.GONE);
                if(intent.hasCategory("user_id_AND_name_from_chatActivity")) {
                    contact_id = intent.getIntExtra("user_id",0);
                    username_ = intent.getStringExtra("contact_name");
                    user_has_image = intent.getStringExtra("user_has_image");
                }
            } else if (callingActivity.equals("com.pip.talk.pip.pc.piptalk.home.MainActivity")){
                Log.e("22","inside IF ");

                startChat.setVisibility(View.GONE);
                if (intent.hasCategory("fromMain_viewPro")){
                    Log.e("22","inside category if ");
                    profile_pic = intent.getStringExtra("profile_pic");
                    contact_id = Integer.valueOf(intent.getStringExtra("user_id"));
                    Log.e("22","contact id :"+contact_id);
                    username_ = intent.getStringExtra("username");
                }

            }


        }

        Log.e("W"," looking for data of id"+profile_pic);

        collapsingToolbarLayout.setTitle(username_);
        collapsingToolbarLayout.setCollapsedTitleTypeface(ralewaySemiBold);
        collapsingToolbarLayout.setExpandedTitleTypeface(ralewayRegular);

        getDatafromOfflineDB(); // displaying content from offline database

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                if (callingActivity.equals("com.pip.talk.pip.pc.piptalk.search.SearchActivity")){
                    startActivity(new Intent(this, SearchActivity.class));
                    this.finish();
                }else if (callingActivity.equals("com.pip.talk.pip.pc.piptalk.chat.ChatActivity")){

                    Intent intent = new Intent(this,ChatActivity.class);
                    intent.putExtra("username",username_);
                    intent.putExtra("contact_id",contact_id);
                    intent.putExtra("user_has_image",user_has_image);
                    intent.addCategory("fromViewProfile");
                    ActivityCompat.startActivityForResult(this,intent,0,null);
                    this.finish();

                }else if (callingActivity.equals("com.pip.talk.pip.pc.piptalk.home.MainActivity")){
                    startActivity(new Intent(this, MainActivity.class));
                    this.finish();
                }
                break;
        }
        return true;
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.e("SSS"," inside onStart ");


        if(isNetworkAvailable()){
            getData(String.valueOf(contact_id));  // get data and update offline database
            if(callingActivity.equals("com.pip.talk.pip.pc.piptalk.search.SearchActivity")){
                getUserToken(profile_pic); // get token
            }
        }

    }


    private void getDatafromOfflineDB(){
        String[] PROJECTION = {USER__ID,USERNAME,USER_TOKEN,NATIVE_LANGUAGE,GENDER,HAS_IMAGE,STATUS,PHONE_NUMBER,DOB};
        //  int userID = getActivity().getSharedPreferences("user", 1).getInt("user_id", 0);
        String selection = "(user_id="+contact_id+" AND username='" +username_+"')";

        Cursor c = getContentResolver().query(UserOfflineDBProvider.CONTENT_URI, PROJECTION, selection, null,null);
        if (!c.moveToFirst()) {
            Toast.makeText(this, "Chat is empty", Toast.LENGTH_LONG).show();
        } else {
            //    mGridData_from_Database.clear();
            status.setText(c.getString(c.getColumnIndex(STATUS)));
            language.setText(c.getString(c.getColumnIndex(NATIVE_LANGUAGE)));
            dateOfBirth.setText(c.getString(c.getColumnIndex(DOB)));
            phoneNum.setText(c.getString(c.getColumnIndex(PHONE_NUMBER)));

            trim();

            c.close();
        }
    }

    private void trim(){
        if(!(dateOfBirth.getText().length()>0)){
            dateOfBirth.setVisibility(View.GONE);
            date_note.setVisibility(View.GONE);
        }
        if(!(phoneNum.getText().length()>0)){
            phoneNum.setVisibility(View.GONE);
            phoneNum_note.setVisibility(View.GONE);
        }
        if (!(dateOfBirth.getText().length()>0) && !(phoneNum.getText().length()>0)){
            other.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


        startChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent pro = new Intent(globalContext, ChatActivity.class);
                pro.putExtra("username",username_);
                pro.putExtra("user_id",profile_pic);
                pro.putExtra("user_token",userToken);
                pro.putExtra("user_has_image",hasImage);
                pro.addCategory("from_view_profile");
                ActivityCompat.startActivityForResult(globalActivity,pro,0,null);
                Log.e("qq","data added");

                ContentValues values = new ContentValues();
                values.put(USER__ID, contact_id);
                values.put(USERNAME, username_);
                values.put(USER_TOKEN, userToken);
                values.put(NATIVE_LANGUAGE, language.getText().toString());
                values.put(GENDER, gender);
                values.put(HAS_IMAGE, hasImage);
                values.put(STATUS, status.getText().toString());
                values.put(PHONE_NUMBER, phone_num);
                values.put(DOB, dob);
                getContentResolver().insert(
                        UserOfflineDBProvider.CONTENT_URI, values);

            }
        });





    }

    private void getData (String id){

        Log.e("SSS"," inside getData");

        getStringFromResponse(Request.Method.POST, id, new CheckCallback() {
            @Override
            public void onSuccess(String result) {

                try {
                    Log.e("SSS"," inside onSuccess : "+result);

                    isMember(result);
                } catch (JSONException e) {
                    Log.e("SSS"," inside catch : "+e);
                    e.printStackTrace();
                }

            }
        });

    }

    private void isMember(String response) throws JSONException {

        JSONObject responseObject = new JSONObject(response);
        String check=responseObject.getString("response");

        Log.e("22"," inside isMember : "+check);


        if (check.equals("success")){

            JSONObject userObject=responseObject.getJSONObject("user");

            String statusX = userObject.getString("status");
            String languageX = userObject.getString("native_language");
            hasImage = userObject.getString("has_image");
            gender = userObject.getString("gender");
            dob = userObject.getString("date_of_birth");
            phone_num=userObject.getString("phone_number");
            Log.e("SSS","status from online "+statusX);
            status.setText(statusX);
            language.setText(languageX);
            dateOfBirth.setText(dob);
            phoneNum.setText(phone_num);
            if (hasImage.equals("0")){
                Glide.with(ViewUserProfileActivity.this)
                        .load(Uri.parse(ImgURL+"0.png"))
                        .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                        .into(imgpp);
            }else {
                Glide.with(ViewUserProfileActivity.this)
                        .load(Uri.parse(ImgURL+String.valueOf(contact_id)+".png"))
                        .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                        .into(imgpp);
            }

            trim();

            if(callingActivity.equals("com.pip.talk.pip.pc.piptalk.chat.ChatActivity")){
                ContentValues values = new ContentValues();          // sending updated data to update offline database
                values.put(STATUS,statusX);
                values.put(NATIVE_LANGUAGE,languageX);
                values.put(HAS_IMAGE,hasImage);

                String where = "( user_id="+contact_id+" AND username='"+username_+"')";
                int c = getContentResolver().update(UserOfflineDBProvider.CONTENT_URI, values, where,null);

                if (c>0) {
                    Toast.makeText(this, "user data updated", Toast.LENGTH_LONG).show();
                    getDatafromOfflineDB(); // display updated data from offline database
                } else {
                    Toast.makeText(this, "user data not updated", Toast.LENGTH_LONG).show();
                }
            }

        }else{
            Log.e("SSS","not success");
        }
    }

    private interface CheckCallback{
        void onSuccess(String result);
    }

    public void getStringFromResponse(int method, final String id , final ViewUserProfileActivity.CheckCallback callback) {

        String url = "http://"+ Util.WAMP_SERVER_DOMAIN+"/piptalk/getUserDetails.php";
        StringRequest loginRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("SSS"," inside onResponse :"+response);
                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("SSS"," inside onErrorResponse :"+error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("user_id",id);
                return params;
            }
        };
        ApplicationClass.getInstance().addToRequestQueue(loginRequest);

    }


    private void getUserToken (String id){

        Log.e("W"," inside getData");

        getTokenFromResponse(Request.Method.POST, id, new CheckCallback2(){
            @Override
            public void onSuccess(String result) {
                try {
                    parseResponse(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }
    private void parseResponse (String response) throws JSONException {

        JSONObject responseObject = new JSONObject(response);
        String check=responseObject.getString("response");

        Log.e("T","Parse response is  : "+response);

        if (check.equals("success")){

            JSONObject userObject=responseObject.getJSONObject("user");
//            String hasImage = userObject.getString("has_image");
//            String DOB = userObject.getString("date_of_birth");       extra data

            userToken = userObject.getString("user_token");

            Log.e("T","token is : "+userToken);

        }else{
            Log.e("T","not success");
        }
    }
    private interface CheckCallback2{
        void onSuccess(String result);
    }

    public void getTokenFromResponse(int method, final String id , final ViewUserProfileActivity.CheckCallback2 callback) {

        String url = "http://"+ Util.WAMP_SERVER_DOMAIN+"/piptalk/getTokenFromUid.php";
        StringRequest loginRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("W"," inside get token , onresponse :"+response);
                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("T"," inside onErrorResponse :"+error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("user_id",id);
                return params;
            }
        };
        ApplicationClass.getInstance().addToRequestQueue(loginRequest);

    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
