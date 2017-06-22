package com.pip.talk.pip.pc.piptalk.Signup;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.pip.talk.pip.pc.piptalk.ApplicationClass;
import com.pip.talk.pip.pc.piptalk.MultipartRequest;
import com.pip.talk.pip.pc.piptalk.MyEditText;
import com.pip.talk.pip.pc.piptalk.R;
import com.pip.talk.pip.pc.piptalk.SpinnerAdapter;
import com.pip.talk.pip.pc.piptalk.User_Model.CircleTransform;
import com.pip.talk.pip.pc.piptalk.User_Model.UserModel;
import com.pip.talk.pip.pc.piptalk.User_Model.UserPreference;
import com.pip.talk.pip.pc.piptalk.fcm.MyFirebaseInstanceIDService;
import com.pip.talk.pip.pc.piptalk.home.MainActivity;
import com.pip.talk.pip.pc.piptalk.xmpp.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;


public class SignUpFragment extends Fragment {

    //deceleration of needed views and variables
    View view;
    ImageButton signUpBtn;
    SpinnerAdapter spinnerAdapter1;
    SpinnerAdapter spinnerAdapter2;
    ArrayList<String> languageList;
    ArrayList<String> genderList;
    MyEditText passEditTxt;
    MyEditText birth_dateEditTxt;
    MyEditText userEditTxt;
    Spinner genderSpinner;
    Spinner langSpinner;
    private ImageButton userImage;
    private Typeface ralewayLogo;
    // private TextView mLogol;
    private final String TAG = "TAG";
    private final static int RESULT_LOAD_IMAGE=1;
    private final static int RESULT_TAKE_IMAGE=2;
    private String ppPath="";
    PasswordValidator passwordValidator = new PasswordValidator();
    final Drawable errorIcon = null;
    UserModel model;

    private boolean doesUsernameExist = false;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //select from gallery
        if (requestCode==RESULT_LOAD_IMAGE&&resultCode==getActivity().RESULT_OK &&data!=null){
            //get image path
            Uri imageUri=data.getData();
            String [] imagePathColumn={MediaStore.Images.Media.DATA};
            Log.v("URI :",imageUri+"");
            Log.v("IMAGE PATH COLUMN :",imagePathColumn+"");

            Cursor cursor =getActivity().getContentResolver().query(imageUri,imagePathColumn,null,null,null);
            cursor.moveToFirst();
            int columnIndex=cursor.getColumnIndex(imagePathColumn[0]);
            String imagePath =cursor.getString(columnIndex);
            Log.v("IMAGE PATH :",imagePath);
            cursor.close();
            ppPath=imagePath;
            // userImage.setImageBitmap(BitmapFactory.decodeFile(imagePath));


        }

        //take a photo by camera
        if (requestCode==RESULT_TAKE_IMAGE&&resultCode==getActivity().RESULT_OK&&data!=null){
            Bitmap bitmap = (Bitmap)data.getExtras().get("data");
            String partFilename = currentDateFormat();
            storeCameraPhotoInSDCard(bitmap, partFilename);
            String storeFilename = "photo_" + partFilename + ".png";
            Log.e("picPAth", Environment.getDataDirectory()+"/" + storeFilename);
            ppPath=Environment.getExternalStorageDirectory()+"/" + storeFilename;
        }

    }

    private String currentDateFormat(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
        String  currentTimeStamp = dateFormat.format(new Date());
        return currentTimeStamp;
    }

    private void storeCameraPhotoInSDCard(Bitmap bitmap, String currentDate){
        File outputFile = new File(Environment.getExternalStorageDirectory(), "photo_" + currentDate + ".jpg");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


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

        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_siginup, container, false);

        model=new UserModel();
        //define fragment views
        signUpBtn=(ImageButton) view.findViewById(R.id.signup_btn);
        birth_dateEditTxt=(MyEditText)view.findViewById(R.id.signup_dateTxt);
        passEditTxt=(MyEditText) view.findViewById(R.id.signup_passTxt);
        userEditTxt=(MyEditText) view.findViewById(R.id.signup_userTxt);
        langSpinner=(Spinner) view.findViewById(R.id.signup_language_spinner_GONE);
        genderSpinner=(Spinner) view.findViewById(R.id.signup_gender_spinner_GONE);
        userImage = (ImageButton) view.findViewById(R.id.user_image);
        //mLogol = (TextView)view.findViewById(R.id.signup_logo);
        languageList=new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.language_options)));
        genderList=new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.gender_options)));

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //get static data for spinner
      //  getSpinnersStaticData();


        spinnerAdapter1 = new SpinnerAdapter(getActivity(),R.layout.spinner_rows,languageList);
        spinnerAdapter2 = new SpinnerAdapter(getActivity(),R.layout.spinner_rows,genderList);
        langSpinner.setAdapter(spinnerAdapter1);
        genderSpinner.setAdapter(spinnerAdapter2);


/*
        langSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                langTxt.setText(langSpinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
               // langTxt.setText(languageList.get(1));
            }
        });

        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                genderTxt.setText(genderSpinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //genderTxt.setText(genderList.get(1));

            }
        });
*/


        // mLogol.setTypeface(ralewayLogo);

        //add action for fab button
        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence [] items= {"Take a photo","Choose from gallery","Cancel"};
                final AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());
                builder.setTitle("Choose a photo :");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (items[i].equals("Choose from gallery")){

                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent,RESULT_LOAD_IMAGE);
                        }else if (items[i].equals("Take a photo")){

                            Intent photoCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(photoCaptureIntent, RESULT_TAKE_IMAGE);
                        }else if (items[i].equals("Cancel")){
                            dialogInterface.dismiss();

                        }
                    }
                });
                builder.show();
            }
        });

        return view;
    }



    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //ralewayLogo = Typeface.createFromAsset(getContext().getAssets(),"Raleway-ExtraBoldItalic.ttf");

    }


    //  Method returns if the username exists or not
    private void isUsernameExists(final String current_user){
        if(!isOnline()){
            Toast.makeText(getActivity(),"No internet connection",Toast.LENGTH_LONG).show();
        }else {
            String URL = "http://" + Util.WAMP_SERVER_DOMAIN + "/piptalk/username_if_exist.php";
            StringRequest checkUsername = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject data = new JSONObject(response);
                        if (data.getString("response").equals("exist")) {
                            //   signUpBtn.setEnabled(false);
                            doesUsernameExist = true;

                        } else {
                            doesUsernameExist = false;
                            //     signUpBtn.setEnabled(true);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("username", current_user);
                    return params;
                }
            };
            ApplicationClass.getInstance().addToRequestQueue(checkUsername);
        }
    }




    @Override
    public void onResume() {
        super.onResume();







        if (ppPath.equals("") || ppPath.isEmpty()) {
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.take_photo_background);
            Bitmap resized = Bitmap.createScaledBitmap(bm,(int)(bm.getWidth()*0.3), (int)(bm.getHeight()*0.3), true);
            RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(getResources(),resized);
            drawable.setCircular(true);
            userImage.setImageDrawable(drawable);
        }else{
            Bitmap bm = BitmapFactory.decodeFile(ppPath);
            Bitmap resized = Bitmap.createScaledBitmap(bm,(int)(bm.getWidth()*0.3), (int)(bm.getHeight()*0.3), true);
            RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(getResources(),resized);
            drawable.setCircular(true);
            userImage.setImageDrawable(drawable);

        }


        //show Datepicker when click on birthdate editTxt

        birth_dateEditTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // create calndar object
               final Calendar myCalendar = Calendar.getInstance();
                final DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        String myFormat = "dd/MMM/yyyy"; //In which you need put here
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);
                        birth_dateEditTxt.setText(sdf.format(myCalendar.getTime()));

                    }

                };
                new DatePickerDialog(getActivity(), datePickerListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        final Drawable errorIcon = getResources().getDrawable(R.drawable.ic_warning_black_24dp);
        errorIcon.setBounds(new Rect(0, 0, errorIcon.getIntrinsicWidth(), errorIcon.getIntrinsicHeight()));


        userEditTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.e("TXT","    "+editable.toString().length());
                //   isUsernameExists(userEditTxt.getText().toString());
                if(editable.toString().length()>20){
                    userEditTxt.setError("Too long username", errorIcon);
                }
                else{
                    userEditTxt.setError(null);

                }

            }
        });


        userEditTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){

                    isUsernameExists(userEditTxt.getText().toString());

                    Log.e("T"," focus changed ");
                }
            }
        });

        passEditTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // code to execute when EditText loses focus
                    if(!passwordValidator.validate(passEditTxt.getText().toString())) { // doesn't match
                        passEditTxt.setError("Invalid", errorIcon);

                    } else { // matches
                        passEditTxt.setError(null);
                    }
                }
            }
        });



        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                String native_language=langSpinner.getSelectedItem().toString();
                String user_gender=genderSpinner.getSelectedItem().toString();

                if (!isOnline()){
                    Toast.makeText(getActivity(),"No internet connection",Toast.LENGTH_LONG).show();
                }else if ((passEditTxt.getText().toString().equals("")||userEditTxt.getText().toString().equals(""))){
                    // Toast.makeText(getActivity(),"Please fill all fields",Toast.LENGTH_LONG).show();
                    Snackbar.make(view, "Invalid entry", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }else{
                    //   if (passwordValidator.validate(passEditTxt.getText().toString())){ // valid

                    if(!doesUsernameExist){  // username isn't  exists


                        String[] data = new String[8];
                        String hashedPass = getSha1Hex(passEditTxt.getText().toString());
                        model.setPassword(hashedPass);

                        Log.e("PASS","hashed is :"+ hashedPass);

//                        FetchUserTask fetchUserTask=new FetchUserTask();

                        data[0] = userEditTxt.getText().toString();
                        model.setUser_name(userEditTxt.getText().toString());



                        data[1]= hashedPass;
                        model.setPassword(hashedPass);

                        data[2]= MyFirebaseInstanceIDService.TOKEN;
                        model.setUserToken(MyFirebaseInstanceIDService.TOKEN);

                        data[3] = user_gender;
                        model.setGender(user_gender);

                        data[4] = native_language;
                        model.setNativeLanguage(native_language);

                        // update setting preferences
                        ArrayList<String> languageOptinsList;
                        ArrayList<String> languageValuesOfOptions;
                        languageOptinsList=new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.language_options)));
                        languageValuesOfOptions=new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.languages_values)));
                        int langValIndex=languageOptinsList.indexOf(native_language);
                        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString(getString(R.string.pref_language_key),
                                languageValuesOfOptions.get(langValIndex)).apply();


                        data[5] = "active";

                        model.setUserState("active");

                        //will be edited
                        model.setPhoneNumber("");
                    /*    int month = datePicker.getMonth()+1;
                        model.setDateOfBirth(datePicker.getDayOfMonth()+"/"+month+"/"+datePicker.getYear());
                        Log.e("DD","date is : "+ datePicker.getDayOfMonth()+"/"+month+"/"+datePicker.getYear());*/
                        model.setStatus("Update status");

                        if(ppPath.equals("")) {
                            //upload profile image to server
                            //uploadImageToServer(new File(ppPath));
                            data[6]="0";
                            model.setImage("0");
                        }else {
                            data[6]="1";
                            model.setImage("1");

                        }

                        data[7] = birth_dateEditTxt.getText().toString();
                        model.setDateOfBirth(birth_dateEditTxt.getText().toString());

                        new UserPreference(getActivity()).storeUser(model);
                      //  fetchUserTask.execute(data);
                        insertUser(data);
                    }
                    else {
                        Snackbar.make(view, "Invalid username or password", Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                    }


                /*    }else { // invalid pass
                        Snackbar.make(view, "Invalid username or password", Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                    }*/


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


//    ////////////////////////////////////////////////////////////////////////////////////////////////
//    private class FetchUserTask extends AsyncTask<String,Void,Void>{
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//
//        }
//
////        @Override
////        protected Void doInBackground(final String... Params) {   /// the problem is here
////            String url= "http://"+Util.WAMP_SERVER_DOMAIN+"/piptalk/insert_user.php";
////            StringRequest signUpRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
////                @Override
////                public void onResponse(String response) {
////                    Log.v(TAG," from sign up >> "+ response);
////                    try {
////                        JSONObject data=new JSONObject(response);
////                        if (data.getString("response").equals("done")){
////
////                            storeUserInLogFile(data.getInt("user_id"));
////                            model.setId(data.getInt("user_id"));
////                            new UserPreference(getActivity()).storeUser(model);
////                            //new UserPreference(getActivity()).updateUserId(data.getInt("user_id"));
////                            // Toast.makeText(getActivity(),String.valueOf(data.getInt("user_id")),Toast.LENGTH_SHORT).show();
////                            if (!ppPath.equals("")) {
////                                //upload image to server by user id.png
////                                uploadImageToServer(new File(ppPath),data.getInt("user_id"));
////                            }
////                        }
////                    } catch (JSONException e) {
////                        e.printStackTrace();
////                    }
////                }
////            }, new Response.ErrorListener() {
////                @Override
////                public void onErrorResponse(VolleyError error) {
////
////                    Log.v(TAG,"  "+error);
////                }
////            }){
////                @Override
////                protected Map<String, String> getParams() throws AuthFailureError {
////                    Map<String,String> params = new HashMap<>();
////
////                    Log.e("tag",Params[0]);
////                    Log.e("tag",Params[1]);
////                    Log.e("tag",Params[2]);
////                    Log.e("tag",Params[3]);
////                    Log.e("tag",Params[4]);
////
////                    params.put("username",Params[0]);
////                    params.put("pass",Params[1]);
////                    params.put("token",Params[2]);
////                    params.put("gender",Params[3]);
////                    params.put("lang",Params[4]);
////                    params.put("user_state",Params[5]);
////                    params.put("has_image",Params[6]);
////                    return params;
////
////                }
////            };
////
////            ApplicationClass.getInstance().addToRequestQueue(signUpRequest);
////            return null;
////        }
//
//    }
//
//

    private void insertUser(final String[] arr ){

        Log.e("T","inside findUserID");

        insertUserResponse(Request.Method.POST, arr, new insertInterface() {
            @Override
            public void onSuccess(String result) {
                findUser(result);
            }
        });
    }

    private void findUser(String response){
        Log.e("TT"," inside find User, response is : "+ response);
        try {
            JSONObject data=new JSONObject(response);
            if (data.getString("response").equals("done")){

                storeUserInLogFile(data.getInt("user_id"));
                Log.e("TT"," inside try ");

                model.setId(data.getInt("user_id"));
                new UserPreference(getActivity()).storeUser(model);
                //new UserPreference(getActivity()).updateUserId(data.getInt("user_id"));
                // Toast.makeText(getActivity(),String.valueOf(data.getInt("user_id")),Toast.LENGTH_SHORT).show();
                if (!ppPath.equals("")) {
                    //upload image to server by user id.png
                    uploadImageToServer(new File(ppPath),data.getInt("user_id"));
                    Log.e("TT"," uploading image to server with path :"+ ppPath);
                }

                Toast.makeText(getActivity(),"Your account is created successfully",Toast.LENGTH_LONG).show();
                Intent intent=new Intent(getActivity(), MainActivity.class);
                intent.putExtra("user_name",userEditTxt.getText().toString());
                intent.putExtra("has_image",ppPath);
                intent.addCategory("from_sign_up");
                startActivity(intent);
                //shared preference part
                getActivity().finish();

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void insertUserResponse(int method, final String [] arr,  final insertInterface callback){

        String url= "http://"+Util.WAMP_SERVER_DOMAIN+"/piptalk/insert_user.php";
        StringRequest signUpRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("TT"," response is : "+response);

                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("TT"," error is : "+error);
                Log.e("TT"," error is : "+error.getCause());

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();

                Log.e("tag",arr[0]);
                Log.e("tag",arr[1]);
                Log.e("tag",arr[2]);
                Log.e("tag",arr[3]);
                Log.e("tag",arr[4]);
                Log.e("tag",arr[5]);
                Log.e("tag",arr[6]);

                params.put("username",arr[0]);
                params.put("pass",arr[1]);
                params.put("token",arr[2]);
                params.put("gender",arr[3]);
                params.put("lang",arr[4]);
                params.put("user_state",arr[5]);
                params.put("has_image",arr[6]);
                params.put("birthday",arr[7]);
                return params;

            }
        };

        ApplicationClass.getInstance().addToRequestQueue(signUpRequest);
    }

    private interface insertInterface{
        void onSuccess(String result);
    }

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
        ApplicationClass.getInstance().addToRequestQueue(logFileRequest);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
//Method to upload image on server
private void uploadImageToServer(File file ,int id){ // my problem is here <<
    String URL ="http://"+ Util.WAMP_SERVER_DOMAIN+"/piptalk/preUpload.php";
    HashMap<String,String> params=new HashMap<>();
    params.put("FileNme",String.valueOf(id)+".png");
    MultipartRequest multipartRequest = new MultipartRequest(URL, new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Log.e("TT"," inside uploading image is success  and response is :"+response);
        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("TT"," inside error sending image, error is : "+ error);
            Log.e("TT"," inside error sending image, error cause is : "+ error.getCause());
            Log.e("TT"," inside error sending image, error message is : "+ error.getMessage());

        }
    },file,params);
    Volley.newRequestQueue(getActivity()).add(multipartRequest);
}

}
