package com.pip.talk.pip.pc.piptalk.Settings;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.pip.talk.pip.pc.piptalk.ApplicationClass;
import com.pip.talk.pip.pc.piptalk.R;
import com.pip.talk.pip.pc.piptalk.User_Model.UserPreference;
import com.pip.talk.pip.pc.piptalk.xmpp.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        addPreferencesFromResource(R.xml.pref_setting);

        //set Activity theme
        setTheme(R.style.settings_theme);

        //bind preference summary
        bindPreferenceSummary(findPreference(getString(R.string.pref_language_key)));
    }

    private void bindPreferenceSummary(Preference preference) {
        preference.setOnPreferenceChangeListener(this);
        onPreferenceChange(preference, PreferenceManager.getDefaultSharedPreferences(preference.getContext())
        .getString(preference.getKey(),""));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {

        String prefVal=o.toString();
        if(preference instanceof ListPreference)
        {
            ListPreference listPreference=(ListPreference)preference;
            int prefIndex=listPreference.findIndexOfValue(prefVal);
            if (prefIndex>=0)
            {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
            //update on cloud
            updateNativeLanguage();
        }
        else {
            preference.setSummary(prefVal);
        }



        return true;
    }

    private void updateNativeLanguage(){
        String URL="http://"+ Util.WAMP_SERVER_DOMAIN+"/piptalk/update_language.php";
        StringRequest updateLangRequest=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error in update"," language in Setting activity"+error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("user_id",String.valueOf(new UserPreference(getApplicationContext()).getUser().getId()));

                // update setting
                ArrayList<String> languageOptinsList;
                ArrayList<String> languageValuesOfOptions;
                languageOptinsList=new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.language_options)));
                languageValuesOfOptions=new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.languages_values)));
                int langOptionIndex=languageValuesOfOptions.indexOf(PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                        .getString(getString(R.string.pref_language_key), getString(R.string.pref_language_default_value)));


                params.put("lang",languageOptinsList.get(langOptionIndex));

                return params;
            }
        };
        ApplicationClass.getInstance().addToRequestQueue(updateLangRequest);
    }
}
