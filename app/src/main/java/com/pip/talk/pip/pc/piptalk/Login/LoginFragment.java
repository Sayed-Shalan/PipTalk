package com.pip.talk.pip.pc.piptalk.Login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.pip.talk.pip.pc.piptalk.ApplicationClass;
import com.pip.talk.pip.pc.piptalk.MyEditText;
import com.pip.talk.pip.pc.piptalk.R;
import com.pip.talk.pip.pc.piptalk.Signup.SignUpActivity;
import com.pip.talk.pip.pc.piptalk.User_Model.UserModel;
import com.pip.talk.pip.pc.piptalk.User_Model.UserPreference;
import com.pip.talk.pip.pc.piptalk.home.MainActivity;
import com.pip.talk.pip.pc.piptalk.xmpp.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;


public class LoginFragment extends Fragment {

    //deceleration of needed views and classes
    View view;
    private Button signupBtn;
    private ImageButton loginBtn;
    private MyEditText usernameEditTxt;
    private MyEditText passEditTxt;
    private Typeface ralewayTxt;
    private Typeface ralewayBtn;
    private Typeface ralewayLogo;
    private TextView mLogo;


    //method for check if the device is connected to internet or no
    public boolean isOnline()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //check if user already logged in
        int id=new UserPreference(getActivity()).getUser().getId();
        if(id!=0){
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_login, container, false);



        //define needed views
        signupBtn =(Button)view.findViewById(R.id.siginup_btn);
        loginBtn =(ImageButton) view.findViewById(R.id.login_btn);
        usernameEditTxt=(MyEditText) view.findViewById(R.id.login_username_txt);
        passEditTxt=(MyEditText) view.findViewById(R.id.login_password_txt);
       // mLogo= (TextView)view.findViewById(R.id.login_logo) ;

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        usernameEditTxt.setTypeface(ralewayTxt);
        passEditTxt.setTypeface(ralewayTxt);
        //loginBtn.setTypeface(ralewayBtn);
        signupBtn.setTypeface(ralewayBtn);
       // mLogo.setTypeface(ralewayLogo);

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        ralewayTxt = Typeface.createFromAsset(getContext().getAssets(),"Raleway-Regular.ttf");
        ralewayBtn = Typeface.createFromAsset(getContext().getAssets(),"Raleway-SemiBold.ttf");
       // ralewayLogo = Typeface.createFromAsset(getContext().getAssets(),"Raleway-ExtraBoldItalic.ttf");


    }

    @Override
    public void onResume() {
        super.onResume();

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SignUpActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                if (!isOnline()){
                    Toast.makeText(getActivity(),"No internet connection",Toast.LENGTH_SHORT).show();
                }
               else if(usernameEditTxt.getText().toString().equals("")||passEditTxt.getText().toString().equals("")){
                    Snackbar.make(view, "Invalid Entry", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }else {

                    String hashedPass =  getSha1Hex(passEditTxt.getText().toString());
                    String[] userData={usernameEditTxt.getText().toString(),hashedPass};
                    FetchUserTask fetchUserTask = new FetchUserTask();
                    fetchUserTask.execute(userData);
                }

            }
        });

    }


    @Nullable
    public static String getSha1Hex(String clearString)
    {
        try
        {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            messageDigest.update(clearString.getBytes("UTF-8"));
            byte[] bytes = messageDigest.digest();
            StringBuilder buffer = new StringBuilder();
            for (byte b : bytes)
            {
                buffer.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }
            return buffer.toString();
        }
        catch (Exception ignored)
        {
            ignored.printStackTrace();
            return null;
        }
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////
    private class FetchUserTask extends AsyncTask<String,Void,Void>{



        @Override
        protected Void doInBackground(final String... Params) {

            String url= "http://"+ Util.WAMP_SERVER_DOMAIN+"/piptalk/login.php";
            StringRequest loginRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.e("CH","inside onResponse : "+ response);
                        isMember(response);
                    } catch (JSONException e) {
                        Log.e("CH","inside catch : "+ e.getMessage()+ "response is : "+response) ;
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("CH ","Error " +error);

                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params=new HashMap<>();
                    params.put("username",Params[0]);
                    params.put("pass",Params[1]);
                    return params;
                }
            };
            int socketTimeout = 30000; // 30 seconds. You can change it
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

            loginRequest.setRetryPolicy(policy);
            ApplicationClass.getInstance().addToRequestQueue(loginRequest);
            return null;
        }

        private void isMember(String response) throws JSONException {
            JSONObject responseObject = new JSONObject(response);
            Log.e("CH","inside isMember : "+ responseObject);
            String check=responseObject.getString("response");
            if (check.equals("success")){
                Toast.makeText(getActivity(),"Loading....",Toast.LENGTH_SHORT).show();
                JSONObject userObject=responseObject.getJSONObject("user");
                Log.e("CH","inside isMember, success : "+ userObject);
                UserModel model = new UserModel();
                model.setId(userObject.getInt("id"));
                model.setUser_name(userObject.getString("user_name"));
                model.setPassword(userObject.getString("user_password"));
                model.setUserToken(userObject.getString("user_token"));
                model.setUserState(userObject.getString("user_state"));
                model.setNativeLanguage(userObject.getString("native_language"));

                // update setting
                ArrayList<String> languageOptinsList;
                ArrayList<String> languageValuesOfOptions;
                languageOptinsList=new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.language_options)));
                languageValuesOfOptions=new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.languages_values)));
                int langValIndex=languageOptinsList.indexOf(userObject.getString("native_language"));
                PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString(getString(R.string.pref_language_key),
                        languageValuesOfOptions.get(langValIndex)).commit();

                model.setStatus(userObject.getString("status"));
                model.setGender(userObject.getString("gender"));
                model.setImage(userObject.getString("has_image"));
                model.setDateOfBirth(userObject.getString("date_of_birth"));
                model.setPhoneNumber(userObject.getString("phone_number"));
                new UserPreference(getActivity()).storeUser(model);
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra("id",userObject.getInt("id"));
                intent.putExtra("user_name",userObject.getString("user_name"));
                intent.addCategory("from_log_in");
                startActivity(intent);
                //insert in log file
                storeUserInLogFile(userObject.getInt("id"));
            }else{
                Toast.makeText(getActivity(),"Wrong username or password",Toast.LENGTH_SHORT).show();
                passEditTxt.setText(null);
                usernameEditTxt.setText(null);
            }
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    private void storeUserInLogFile(final int user_id) {
        String URL ="http://"+Util.WAMP_SERVER_DOMAIN+"/piptalk/insert_user_in_log_file.php";
        StringRequest logFileRequest=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
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


                //get current date
                Date cDate = new Date();
                String fDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);

                //get current time
                Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+1:00"));
                Date currentLocalTime = cal.getTime();
                DateFormat date = new SimpleDateFormat("HH:mm:ss a");
                date.setTimeZone(TimeZone.getTimeZone("GMT+1:00"));
                String localTime = date.format(currentLocalTime);

                params.put("user_id",String.valueOf(user_id));
                params.put("time",localTime);
                params.put("date",fDate);
                return params;
            }
        };
        logFileRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        ApplicationClass.getInstance().addToRequestQueue(logFileRequest);
    }
}
