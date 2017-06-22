package com.pip.talk.pip.pc.piptalk.home;

import android.app.ActivityOptions;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.pip.talk.pip.pc.piptalk.ApplicationClass;
import com.pip.talk.pip.pc.piptalk.Change_Password.ChangePasswordActivity;
import com.pip.talk.pip.pc.piptalk.Login.LoginActivity;
import com.pip.talk.pip.pc.piptalk.R;
import com.pip.talk.pip.pc.piptalk.Settings.SettingsActivity;
import com.pip.talk.pip.pc.piptalk.User_Model.CircleTransform;
import com.pip.talk.pip.pc.piptalk.User_Model.UserPreference;
import com.pip.talk.pip.pc.piptalk.offlineDB.UserOfflineDBProvider;
import com.pip.talk.pip.pc.piptalk.profile.ProfileActivity;
import com.pip.talk.pip.pc.piptalk.search.SearchActivity;
import com.pip.talk.pip.pc.piptalk.xmpp.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    public static   String  MainUsername="";
    private Typeface titleTF;
    private TextView title;
    public static  String globalCheck = "";
    private Context globalContext;
    public static int User_id;
    ImageView ppImg;
    String ImgURL="http://"+ Util.WAMP_SERVER_DOMAIN+"/piptalk/images/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        globalContext = this;
      //  Toast.makeText(globalContext,"pref"+String.valueOf(new UserPreference(globalContext).getUser().getId()),Toast.LENGTH_SHORT).show();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ppImg=(ImageView) findViewById(R.id.mainActivity_pp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //    getState(); // for myFirebaseMessagingService
        titleTF = Typeface.createFromAsset(getAssets(),"Raleway-ExtraBoldItalic.ttf");
        title = (TextView) findViewById(R.id.toolbar_title);
        title.setTypeface(titleTF);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

//        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
//            Slide slide = new Slide(Gravity.TOP);
//            slide.addTarget(R.id.home_container); // change this
//            slide.setInterpolator(AnimationUtils.loadInterpolator(this,android.R.interpolator.linear_out_slow_in));
//            slide.setDuration(3000);
//            getWindow().setEnterTransition(slide);
//        }

        Intent intent = getIntent();

        if(intent.hasCategory("from_sign_up")){ // from sign up
            Bundle bundle = intent.getExtras();
            if (!bundle.isEmpty()) {
                MainUsername = bundle.getString("user_name");
               // Toast.makeText(globalContext,"pref"+String.valueOf(new UserPreference(globalContext).getUser().getId()),Toast.LENGTH_SHORT).show();
                //get user id because user come from signup
                getUserId();
                // we send use data to offline database inside this method too
               // Toast.makeText(globalContext,"pref"+String.valueOf(new UserPreference(globalContext).getUser().getId()),Toast.LENGTH_SHORT).show();
               // Toast.makeText(globalContext,"static"+String.valueOf(User_id),Toast.LENGTH_SHORT).show();
                //check if user has an image or not
            /*    if (!bundle.getString("has_image").equals("")){
                    //upload image to server by user id.png
                    uploadImageToServer(new File(bundle.getString("has_image")));
                }*/

            }
        }else if(intent.hasCategory("from_log_in")) { // from login
            Toast.makeText(globalContext,String.valueOf(new UserPreference(globalContext).getUser().getId()),Toast.LENGTH_SHORT).show();
            Bundle bundle = intent.getExtras();
            if (!bundle.isEmpty()) {
                MainUsername = bundle.getString("user_name");
                //  WebService(MainUsername, MainUserToken);
            }
        } else {
            MainUsername = new UserPreference(this).getUsername();
        }


        if(savedInstanceState == null){
            getSupportFragmentManager().
                    beginTransaction().
                    replace(R.id.home_container,new MainActivityFragment()).
                    commit();

        }

        new UserPreference(globalContext).updateUserToken();
    }
//



    @Override
    protected void onStart() {
        super.onStart();

        //update user token
        updateUserToken();
    }

    private void sendUserDataToOfflineDB(){

        UserPreference userPreference = new UserPreference(globalContext);
        ContentValues values = new ContentValues();

        Log.e("TTT","user id before sending to db"+ User_id);
        values.put(UserOfflineDBProvider.USER__ID,
                User_id);

        values.put(UserOfflineDBProvider.USERNAME,
                MainUsername);

        values.put(UserOfflineDBProvider.USER_TOKEN,
                userPreference.getToken());

        values.put(UserOfflineDBProvider.NATIVE_LANGUAGE,
                userPreference.getNLanguage());

        values.put(UserOfflineDBProvider.GENDER,
                userPreference.getGender());

        values.put(UserOfflineDBProvider.HAS_IMAGE,
                userPreference.getHasImage());

        values.put(UserOfflineDBProvider.STATUS,
                userPreference.getStatus());

        values.put(UserOfflineDBProvider.PHONE_NUMBER,
                userPreference.getPhoneNumber());

        values.put(UserOfflineDBProvider.DOB,
                userPreference.getDOB());

        getContentResolver().insert(
                UserOfflineDBProvider.CONTENT_URI, values);

        Toast.makeText(this,
                "Added", Toast.LENGTH_SHORT).show();
    }


    private interface VolleyCallback {

        void onSuccess(String result);
    }

    private void getUserId() {
        getUserIDfromResponse(Request.Method.POST,  new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                findID(result);
            }
        } );
    }

    private void findID(String result){
        JSONObject data= null;
        try {
            data = new JSONObject(result);
            if (data.getString("response").equals("Done")){
                //  new UserPreference(globalContext).updateUserId(data.getInt("id"));
                //Toast.makeText(globalContext,String.valueOf(data.getInt("id")),Toast.LENGTH_SHORT).show();
                //  Toast.makeText(globalContext,"pref"+String.valueOf(new UserPreference(globalContext).getUser().getId()),Toast.LENGTH_SHORT).show();
                MainActivity.User_id = data.getInt("id");
                sendUserDataToOfflineDB();

            }else {
                Log.v("Error","can't get id ");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getUserIDfromResponse(int post, final VolleyCallback callback){
        final String URL="http://"+ Util.WAMP_SERVER_DOMAIN+"/piptalk/get_user_id.php";
        Log.e("TTT","INSIDE GetUserID() ");
        StringRequest idRequest=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params=new HashMap<>();
                params.put("username",new UserPreference(getApplicationContext()).getUser().getUser_name());
                return params;
            }
        };
        ApplicationClass.getInstance().addToRequestQueue(idRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (new UserPreference(this).getUser().getImage().equals("0")){
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.unknown_male); // problem here,incoming different sizes won't work
            Bitmap resized = Bitmap.createScaledBitmap(bm,(int)(bm.getWidth()*0.3), (int)(bm.getHeight()*0.3), true);
            RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(getResources(),resized);
            drawable.setCircular(true);
            ppImg.setImageDrawable(drawable);
        }else {
            Glide.with(this)
                    .load(ImgURL +String.valueOf(new UserPreference(getApplicationContext()).getUser().getId())+".png") // add your image url
                    .transform(new CircleTransform(this)) // applying the image transformer
                    .into(ppImg);
        }
    }


    //updateUserToken
    private void updateUserToken(){
        String URL="http://"+ Util.WAMP_SERVER_DOMAIN+"/piptalk/update_user_token.php";
        StringRequest updateTokenrequest=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.v("update token correctly",response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("user_token",FirebaseInstanceId.getInstance().getToken());
                params.put("user_id",String.valueOf(new UserPreference(getApplicationContext()).getUser().getId()));
                return params;
            }
        };

        ApplicationClass.getInstance().addToRequestQueue(updateTokenrequest);
    }



    private void CheckActivity (){

        getStringFromResponse(Request.Method.POST,new CheckCallback() {
            @Override
            public void onSuccess(String result) {

                try {
                    isMember(result);
                } catch (JSONException e) {
                    Log.e("PP"," is memeber (catsh) : "+e);
                    e.printStackTrace();
                }

            }
        });

    }

    public void getStringFromResponse(int method, final CheckCallback callback) {

        String url = "http://"+ Util.WAMP_SERVER_DOMAIN+"/piptalk/check_activity.php";
        StringRequest loginRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.v("SEND",response);
                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("Response ","Error");

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("user_id",String.valueOf(new UserPreference(globalContext).getUserId()));
                return params;
            }
        };
        ApplicationClass.getInstance().addToRequestQueue(loginRequest);

    }

    private void isMember(String response) throws JSONException {

        JSONObject responseObject = new JSONObject(response);
        String check=responseObject.getString("response");

        if (check.equals("success")){
            JSONObject userObject=responseObject.getJSONObject("user");
            String STATE =userObject.getString("user_state");
            globalCheck= STATE;
        }
    }

    private interface CheckCallback{
        void onSuccess(String result);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case   R.id.action_settings:

                Intent i = new Intent(this,SettingsActivity.class);
                startActivity(i);
                return true;

            case R.id.action_search:

                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
                    Intent intent = new Intent(this,SearchActivity.class);
                    Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
                    this.startActivity(intent,bundle);
                }else {
                    Intent intent = new Intent(this,SearchActivity.class);
                    startActivity(intent);
                }
                return true;

            case R.id.action_profile :
                Intent intent = new Intent(this,ProfileActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_logout :
                new UserPreference(getApplicationContext()).deleteUser();
                Intent in = new Intent(this,LoginActivity.class);
                startActivity(in);
                finish();
                return true;
            case R.id.action_changePassword:
                Intent intent1=new Intent(MainActivity.this, ChangePasswordActivity.class);
                startActivity(intent1);
                finish();
                return true;

            case R.id.action_contact_us :

                return true;


            case R.id.action_help :

                return true;
            default:

                return super.onOptionsItemSelected(item);
        }

    }




}
