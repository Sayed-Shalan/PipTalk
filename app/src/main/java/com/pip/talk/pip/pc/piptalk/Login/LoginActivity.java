package com.pip.talk.pip.pc.piptalk.Login;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.pip.talk.pip.pc.piptalk.R;


public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        if (savedInstanceState==null){
//            Log.e("SEND","inside login Activity extras"+ savedInstanceState.toString());
//        }


        //call method to add login Fragment here
        bindLoginFragment();
    }

    //create method to add login fragment
    private  void bindLoginFragment()
    {
        getSupportFragmentManager().
                beginTransaction().
                replace(R.id.login_fragment_container,new LoginFragment()).
                commit();
    }
}
