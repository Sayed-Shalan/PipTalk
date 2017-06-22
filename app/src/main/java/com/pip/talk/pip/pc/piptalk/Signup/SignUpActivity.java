package com.pip.talk.pip.pc.piptalk.Signup;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.pip.talk.pip.pc.piptalk.R;


public class SignUpActivity extends AppCompatActivity {

    public static Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_SiginupActivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        context=this;

        //call method to add sign up Fragment here
        bindSignUpFragment();
    }


    //create method to add sign up fragment
    private  void bindSignUpFragment()
    {
        getSupportFragmentManager().
                beginTransaction().
                replace(R.id.signup_fragment_container,new SignUpFragment()).
                commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);
    }
}
