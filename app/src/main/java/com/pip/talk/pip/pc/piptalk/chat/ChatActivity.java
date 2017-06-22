package com.pip.talk.pip.pc.piptalk.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pip.talk.pip.pc.piptalk.R;
import com.pip.talk.pip.pc.piptalk.User_Model.CircleTransform;
import com.pip.talk.pip.pc.piptalk.offlineDB.UserOfflineDBProvider;
import com.pip.talk.pip.pc.piptalk.profile.ViewUserProfileActivity;
import com.pip.talk.pip.pc.piptalk.xmpp.util.Util;


public class ChatActivity extends AppCompatActivity{


    private TextView username ;
    private ImageView imageView;
    private LinearLayout layout;
    private Bundle bundle;
    private Activity globalContext;
    public static String contact_username="";
    private   String contact_usertoken="";
    private   int contact_userid;
    private  String contact_userID_string;
    private String user_has_image="";
    private String callingActivity;

    private final String USER_HAS_IMAGE = "has_image";


    String URL="http://"+ Util.WAMP_SERVER_DOMAIN+"/piptalk/images/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_ChatActivity);
        setSupportActionBar(toolbar);

        username = (TextView) findViewById(R.id.chat_username);
        imageView = (ImageView) findViewById(R.id.chat_profile_pic);
        layout = (LinearLayout) findViewById(R.id.userAndPic);

        globalContext = this;
        Intent intent = getIntent();


        if(getCallingActivity()!=null){

            callingActivity = getCallingActivity().getClassName();

            if(callingActivity.equals("com.pip.talk.pip.pc.piptalk.profile.ViewUserProfileActivity")){
                if (intent.hasCategory("fromViewProfile")){
                    contact_username=intent.getStringExtra("username");
                    contact_userid =intent.getIntExtra("contact_id",0);
                    user_has_image = intent.getStringExtra("user_has_image");
                    username.setText(contact_username);
                }
            }

        }

        if(intent.hasCategory("from_main")){
            bundle=intent.getExtras();
            contact_username=bundle.getString("username");
            contact_usertoken=bundle.getString("user_token");
            contact_userid=bundle.getInt("user_id");
            user_has_image= bundle.getString("contact_pp");
            username.setText(contact_username);
            // token and sender has image
        }
        else if(intent.hasCategory("from_view_profile")){
            bundle=intent.getExtras();
            contact_username=bundle.getString("username");
            contact_usertoken=bundle.getString("user_token");
            contact_userID_string=bundle.getString("user_id");
            contact_userid=Integer.valueOf(contact_userID_string);
            user_has_image= bundle.getString("user_has_image");
            username.setText(contact_username);

            // token and sender has image
        }


        if(isNetworkAvailable()){

            if (user_has_image.equals("0")){
                Glide.with(this)
                        .load(URL+"0.png") // add your image url
                        .transform(new CircleTransform(this)) // applying the image transformer
                        .into(imageView);
            }else {
                Glide.with(this)
                        .load(URL+String.valueOf(contact_userid)+".png") // add your image url
                        .transform(new CircleTransform(this)) // applying the image transformer
                        .into(imageView);
            }
        }else {

            if(!getContactHasImageFromOfflineDB()){

                Glide.with(this)
                        .load(URL+"0.png") // add your image url
                        .transform(new CircleTransform(this)) // applying the image transformer
                        .into(imageView);

            }else {
                Glide.with(this)
                        .load(URL + String.valueOf(contact_userid) + ".png") // add your image url
                        .transform(new CircleTransform(this)) // applying the image transformer
                        .into(imageView);
            }

        }




           /* Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.unknown_male); // problem here,incoming different sizes won't work
            Bitmap resized = Bitmap.createScaledBitmap(bm,(int)(bm.getWidth()*0.09), (int)(bm.getHeight()*0.09), true);
            RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(getResources(),resized);
            drawable.setCircular(true);
            imageView.setImageDrawable(drawable);*/


        Bundle bundle = new Bundle();

        if (savedInstanceState == null) {
            if(intent.hasCategory("from_view_profile")|| intent.hasCategory("from_main")){
                bundle.putString("username",contact_username);
                bundle.putString("user_token",contact_usertoken);
                bundle.putString("user_has_image",user_has_image);

                if(intent.hasCategory("from_main")){
                    bundle.putInt("user_id_intger",contact_userid);
                }else{
                    bundle.putString("user_id_string",contact_userID_string);
                }

                ChatFragment fragment = new ChatFragment();
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction()
                        .replace(R.id.chat_container,fragment).commit();
            }else {
                bundle.putString("contact_id",String.valueOf(contact_userid));
                ChatFragment fragment = new ChatFragment();
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction()
                        .replace(R.id.chat_container,fragment).commit();
            }

        }
        onNewIntent(getIntent());

    }


    private boolean getContactHasImageFromOfflineDB(){
        String[] PROJECTION = {USER_HAS_IMAGE};
        String selection = "(user_id="+contact_userid+" AND username='"+contact_username+"')";
        String has="";
        Cursor c = getContentResolver().query(UserOfflineDBProvider.CONTENT_URI, PROJECTION, selection, null,null);

        if (!c.moveToFirst()) {
            Log.e("S","no response");
        } else {
            has =  c.getString(c.getColumnIndex(USER_HAS_IMAGE));
            c.close();
        }

        if(has.equals("0")){
            return false;
        }else {
            return true;
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        Log.e("II",""+activeNetworkInfo != null?"true":"  ");
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if(intent.hasCategory("NOTIFICATION")){
            Bundle extras = intent.getExtras();// message + message id + sender + sender id + receiver + receiver has image
            username.setText((CharSequence) extras.get("notification_sender"));
            Log.e("SEND", "inseide onNewInent");
            ChatFragment fragment = new ChatFragment();
            fragment.setArguments(extras);
            getFragmentManager().beginTransaction()
                    .add(R.id.chat_container, fragment).commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        return true; // fix this to have a menu
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

                return true;

            case R.id.action_search:

                return true;
            default:

                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ViewUserProfileActivity.class);
                intent.putExtra("user_id",contact_userid);
                intent.putExtra("contact_name",contact_username);
                intent.putExtra("user_has_image",user_has_image);
                intent.addCategory("user_id_AND_name_from_chatActivity");
                ActivityCompat.startActivityForResult(globalContext,intent,0,null);
            }
        });
    }

}

