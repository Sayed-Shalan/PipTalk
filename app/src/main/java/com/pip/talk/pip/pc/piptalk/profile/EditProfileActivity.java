package com.pip.talk.pip.pc.piptalk.profile;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.pip.talk.pip.pc.piptalk.ApplicationClass;
import com.pip.talk.pip.pc.piptalk.MyEditText;
import com.pip.talk.pip.pc.piptalk.R;
import com.pip.talk.pip.pc.piptalk.SpinnerAdapter;
import com.pip.talk.pip.pc.piptalk.User_Model.UserPreference;
import com.pip.talk.pip.pc.piptalk.xmpp.util.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;



public class EditProfileActivity extends AppCompatActivity {

    private MyEditText statusTxt;
    private MyEditText phoneTxt;
    private MyEditText dateTxt;
    private Spinner genderSpinner;
    private Spinner languageSpinner;
    ArrayList<String> languageList;
    SpinnerAdapter spinnerAdapter1;
    SpinnerAdapter spinnerAdapter2;
    ArrayList<String> genderList;
    private ImageButton saveBtn;
    private Context context;
    private Typeface typeface;
   private   ArrayList<String> languageOptinsList;
   private ArrayList<String> languageValuesOfOptions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_edit_profile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        context = this;
        statusTxt=(MyEditText) findViewById(R.id.editStatusTxt);
        phoneTxt=(MyEditText) findViewById(R.id.editphoneTxt);
        dateTxt=(MyEditText) findViewById(R.id.editBirthDate);
        genderSpinner=(Spinner)findViewById(R.id.EditProfile_gender_spinner);
        languageSpinner=(Spinner)findViewById(R.id.EditProfile_language_spinner);
        saveBtn=(ImageButton) findViewById(R.id.editprofile_saveBtn);

        typeface = Typeface.createFromAsset(this.getAssets(),"Raleway-Regular.ttf");

        statusTxt.setTypeface(typeface);
        phoneTxt.setTypeface(typeface);
       // dateTxt.setTypeface(typeface);

        languageList=new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.language_options)));
        genderList=new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.gender_options)));
        spinnerAdapter1 = new SpinnerAdapter(this,R.layout.spinner_rows,languageList);
        spinnerAdapter2 = new SpinnerAdapter(this,R.layout.spinner_rows,genderList);
        languageSpinner.setAdapter(spinnerAdapter1);
        genderSpinner.setAdapter(spinnerAdapter2);
        languageOptinsList=new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.language_options)));
        languageValuesOfOptions=new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.languages_values)));
       // languageSpinner.setSelection(languageOptinsList.indexOf(languageSpinner.getSelectedItem().toString()));

    }


    @Override
    protected void onResume() {
        super.onResume();

        //open datePicker
        dateTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // create calndar object
                final Calendar myCalendar = Calendar.getInstance();
                final DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        String myFormat = "dd/MMM/yyyy"; //In which you need put here
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);
                        dateTxt.setText(sdf.format(myCalendar.getTime()));

                    }

                };
                new DatePickerDialog(EditProfileActivity.this, datePickerListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });



        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //update data on Preference

                UserPreference userPreference = new UserPreference(context);
                if (phoneTxt.getText().length()>0 ){
                   userPreference.setKEY_UER_PHONE(phoneTxt.getText().toString());
                }
                if (dateTxt.getText().length()>0 ){

                    userPreference.setKEY_UER_BIRTH_DATE(dateTxt.getText().toString());
                }
                if (statusTxt.getText().length()>0 ){
                    userPreference.setKEY_UER_STATUS(statusTxt.getText().toString());
                }

                userPreference.setKEY_UER_GENDER(genderSpinner.getSelectedItem().toString());

                // update setting

                int langValIndex=languageOptinsList.indexOf(languageSpinner.getSelectedItem().toString());

                PreferenceManager.getDefaultSharedPreferences(context).
                        edit().putString(getString(R.string.pref_language_key),
                        languageValuesOfOptions.get(langValIndex)).apply();

                userPreference.setKEY_USER_NATIVE_LANGUAGE(languageSpinner.getSelectedItem().toString());

                //update data on cloud
                updateUserData();

                Intent intent=new Intent(EditProfileActivity.this,ProfileActivity.class);
                startActivity(intent);

            }
        });


    }

    private void updateUserData() {
        String URL="http://"+ Util.WAMP_SERVER_DOMAIN+"/piptalk/update_profile.php";
        StringRequest updateDataRequest=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("status",statusTxt.getText().toString());
                params.put("lang",languageSpinner.getSelectedItem().toString());
                params.put("gender",genderSpinner.getSelectedItem().toString());
                params.put("phone",phoneTxt.getText().toString());
                params.put("birth_date",dateTxt.getText().toString());
                params.put("user_id",String.valueOf(new UserPreference(getApplicationContext()).getUser().getId()));
                return params;
            }
        };
        ApplicationClass.getInstance().addToRequestQueue(updateDataRequest);
    }
}
