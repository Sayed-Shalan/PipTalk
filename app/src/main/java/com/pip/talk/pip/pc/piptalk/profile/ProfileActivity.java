package com.pip.talk.pip.pc.piptalk.profile;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.pip.talk.pip.pc.piptalk.ApplicationClass;
import com.pip.talk.pip.pc.piptalk.MultipartRequest;
import com.pip.talk.pip.pc.piptalk.R;
import com.pip.talk.pip.pc.piptalk.User_Model.UserPreference;
import com.pip.talk.pip.pc.piptalk.xmpp.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class ProfileActivity extends AppCompatActivity {


    private CollapsingToolbarLayout collapsingToolbarLayout;

    private Typeface ralewaySemiBold;
    private Typeface ralewayRegular;
    private TextView status;
    private TextView statusTime;
    private TextView language;
    private TextView languageHeader;
    private TextView birthdate_Txt;
    private TextView phoneNumTxt;
    private TextView phoneNum_note;
    private TextView date_note;

    private  TextView other;
    private Context context;

    private FloatingActionButton fab_changePp;
    private ImageView ppImg;
    private int REQUEST_LOAD_IMAGE=1;
    private int REQUEST_TAKE_IMAGE=0;
    private LinearLayout LinearCardV;
    String ImgURL="http://"+ Util.WAMP_SERVER_DOMAIN+"/piptalk/images/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.profile_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        context = this;


        collapsingToolbarLayout =  (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        status = (TextView) findViewById(R.id.user_status);
        statusTime = (TextView) findViewById(R.id.status_note);
        language = (TextView) findViewById(R.id.language);
        birthdate_Txt=(TextView)findViewById(R.id.profile_birth_date);
        phoneNumTxt=(TextView)findViewById(R.id.profile_phone_number);
        languageHeader = (TextView) findViewById(R.id.language_note);
        fab_changePp=(FloatingActionButton)findViewById(R.id.fab_change_image);
        ppImg=(ImageView)findViewById(R.id.ppimageView);
        LinearCardV = (LinearLayout) findViewById(R.id.linear_card) ;
        phoneNum_note = (TextView) findViewById(R.id.phoneNum_note);
        date_note = (TextView) findViewById(R.id.date_note);
        other = (TextView) findViewById(R.id.other_o);

        ralewayRegular = Typeface.createFromAsset(this.getAssets(),"Raleway-Regular.ttf");
        ralewaySemiBold = Typeface.createFromAsset(this.getAssets(),"Raleway-SemiBold.ttf");

        status.setTypeface(ralewayRegular);
        statusTime.setTypeface(ralewayRegular);
        language.setTypeface(ralewayRegular);
        languageHeader.setTypeface(ralewayRegular);
        phoneNumTxt.setTypeface(ralewayRegular);
        birthdate_Txt.setTypeface(ralewayRegular);
        phoneNum_note.setTypeface(ralewayRegular);
        date_note.setTypeface(ralewayRegular);

        collapsingToolbarLayout.setTitle(new UserPreference(getApplicationContext()).getUser().getUser_name());
        collapsingToolbarLayout.setCollapsedTitleTypeface(ralewaySemiBold);
        collapsingToolbarLayout.setExpandedTitleTypeface(ralewayRegular);

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
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    //update has_image
    private void setHasImageOnCloud(){
        String URL="http://"+ Util.WAMP_SERVER_DOMAIN+"/piptalk/set_has_image.php";
        StringRequest updatePpRequest=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject data=new JSONObject(response);
                    if (data.getString("response").equals("Done")){
                        //done
                       }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params=new HashMap<>();
                params.put("user_id",String.valueOf(new UserPreference(getApplicationContext()).getUser().getId()));
                return params;
            }
        };
        ApplicationClass.getInstance().addToRequestQueue(updatePpRequest);
    }


    //Method to upload image on server
    private void uploadImageToServer(File file){
        String URL ="http://"+ Util.WAMP_SERVER_DOMAIN+"/piptalk/preUpload.php";
        HashMap<String,String> params=new HashMap<>();
        params.put("FileNme",String.valueOf(new UserPreference(getApplicationContext()).getUser().getId())+".png");
        MultipartRequest multipartRequest = new MultipartRequest(URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Toast.makeText(getApplicationContext(),"Your profile picture is updated successfuly",Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        },file,params);
        Volley.newRequestQueue(this).add(multipartRequest);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //select image from gallery
        if (requestCode==REQUEST_LOAD_IMAGE&&resultCode==this.RESULT_OK &&data!=null){
            if (isOnline()){
                //get image path
                Uri imageUri=data.getData();
                String [] imagePathColumn={MediaStore.Images.Media.DATA};

                Cursor cursor=getContentResolver().query(imageUri,imagePathColumn,null,null,null);
                cursor.moveToFirst();
                int columnIndex=cursor.getColumnIndex(imagePathColumn[0]);
                String imagePath=cursor.getString(columnIndex);
                cursor.close();

                //set image to its View --can be deleted
                Bitmap bm = BitmapFactory.decodeFile(imagePath);
                ppImg.setImageBitmap(bm);




                //upload user profile picture on the server
                uploadImageToServer(new File(imagePath));

                if (new UserPreference(this).getUser().getImage().equals("0")){

                    ApplicationClass.getInstance().getRequestQueue().getCache().remove(ImgURL+"0.png");

                    //update User Profile Picture on Cloud
                    setHasImageOnCloud();

                    //update User photo in UserPreferences
                    new UserPreference(getApplicationContext()).updateProfilePicture("1"); //means user has a profile image
                }else {
                    ApplicationClass.getInstance().getRequestQueue().getCache().remove(ImgURL+String.valueOf(new UserPreference(getApplicationContext())
                            .getUser().getId())+".png");
                }
            }else {
                Toast.makeText(this,"Can't upload this image,Check internet",Toast.LENGTH_SHORT).show();
            }
        }

        //take photo from camera
     else  if(requestCode==REQUEST_TAKE_IMAGE&&resultCode==this.RESULT_OK&&data!=null) {

            if (isOnline()){
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                String partFilename = currentDateFormat(); //get current date in milliseconds as photo name
                storeCameraPhotoInSDCard(bitmap, partFilename); //store photo in sd card
                String storeFilename = "photo_" + partFilename + ".png";

                // Log.e("picPAth", Environment.getDataDirectory()+"/" + storeFilename);
                String ppPath = Environment.getExternalStorageDirectory() + "/" + storeFilename;

                //set image to its View --can be deleted
                Bitmap bm = BitmapFactory.decodeFile(ppPath);
                ppImg.setImageBitmap(bm);

                //upload user profile picture on the server
                uploadImageToServer(new File(ppPath));
                if (new UserPreference(this).getUser().getImage().equals("0")){

                    ApplicationClass.getInstance().getRequestQueue().getCache().remove(ImgURL+"0.png");

                    //update User Profile Picture on Cloud
                    setHasImageOnCloud();

                    //update User photo in UserPreferences
                    new UserPreference(getApplicationContext()).updateProfilePicture("1"); //means user has a profile image


                }else {
                    ApplicationClass.getInstance().getRequestQueue().getCache().remove(ImgURL+String.valueOf(new UserPreference(getApplicationContext())
                            .getUser().getId())+".png");
                }
            }else {
                Toast.makeText(this,"Can't upload this image,Check internet",Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(this,"Data is null",Toast.LENGTH_SHORT).show();
        }


    }


    @Override
    protected void onStart() {
        super.onStart();
        status.setText(new UserPreference(this).getUser().getStatus());
        language.setText(new UserPreference(this).getUser().getNativeLanguage());
        birthdate_Txt.setText(new UserPreference(this).getUser().getDateOfBirth());
        phoneNumTxt.setText(new UserPreference(this).getUser().getPhoneNumber());
    }

    private  void trim(){
        if(!(phoneNumTxt.getText().length()>0)){
            phoneNumTxt.setVisibility(View.GONE);
            phoneNum_note.setVisibility(View.GONE);
        }

        if (!(birthdate_Txt.getText().length()>0)){
            birthdate_Txt.setVisibility(View.GONE);
            date_note.setVisibility(View.GONE);
        }

        if (!(birthdate_Txt.getText().length()>0) && !(phoneNumTxt.getText().length()>0)){
            other.setVisibility(View.GONE);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();

        trim();

        Log.e("TT"," getImage() contains "+ new UserPreference(context).getUser().getImage());

        if (new UserPreference(context).getUser().getImage().equals("1")){
            Glide.with(ProfileActivity.this)
                    .load(Uri.parse(ImgURL+String.valueOf(new UserPreference(getApplicationContext())
                            .getUser().getId())+".png"))
                    .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                    .into(ppImg);
          /*  Glide.with(getApplicationContext())
                    .load(ImgURL+String.valueOf(new UserPreference(getApplicationContext())
                            .getUser().getId())+".png").fitCenter().into(ppImg);*/
        }else {

            Glide.with(ProfileActivity.this)
                    .load(Uri.parse(ImgURL+"0.png"))
                    .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                    .into(ppImg);

          /*  Glide.with(getApplicationContext())
                    .load(ImgURL+"0.png").fitCenter().into(ppImg);*/
        }

        LinearCardV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(ProfileActivity.this,EditProfileActivity.class);
                startActivity(intent);

            }
        });

        //Change Profile Picture when click on fab Btn
        fab_changePp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final CharSequence [] items= {"Take a photo","Choose from gallery","Cancel"};
                final AlertDialog.Builder builder= new AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle("Choose a photo :");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (items[i].equals("Choose from gallery")){

                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent,REQUEST_LOAD_IMAGE);
                        }else if (items[i].equals("Take a photo")){

                            Intent photoCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(photoCaptureIntent, REQUEST_TAKE_IMAGE);
                        }else if (items[i].equals("Cancel")){
                            dialogInterface.dismiss();

                        }
                    }
                });
                builder.show();
            }
        });



    }




}
