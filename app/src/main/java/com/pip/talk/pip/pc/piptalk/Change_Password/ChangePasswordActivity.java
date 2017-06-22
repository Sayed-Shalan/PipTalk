package com.pip.talk.pip.pc.piptalk.Change_Password;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.pip.talk.pip.pc.piptalk.R;

public class ChangePasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_ChangePassActivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (savedInstanceState==null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.changePasswordContainer,new ChangePasswordFragment(),null)
                    .commit();
        }

    }


}
