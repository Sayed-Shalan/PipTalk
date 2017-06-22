package com.pip.talk.pip.pc.piptalk.Change_Password;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.pip.talk.pip.pc.piptalk.ApplicationClass;
import com.pip.talk.pip.pc.piptalk.MyEditText;
import com.pip.talk.pip.pc.piptalk.R;
import com.pip.talk.pip.pc.piptalk.Signup.PasswordValidator;
import com.pip.talk.pip.pc.piptalk.User_Model.UserPreference;
import com.pip.talk.pip.pc.piptalk.profile.ProfileActivity;
import com.pip.talk.pip.pc.piptalk.xmpp.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;


public class ChangePasswordFragment extends Fragment {

    View view;
    private MyEditText oldPassTxt;
    private MyEditText newPassTxt1;
    private MyEditText newPassTxt2;
    private ImageButton confirmBtn;
    private  Typeface typeface;
    private TextView noteTxt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_change_password, container, false);
        //get Views
        oldPassTxt=(MyEditText) view.findViewById(R.id.changePassOldTxt);
        newPassTxt1=(MyEditText) view.findViewById(R.id.changePassNewTxt);
        newPassTxt2=(MyEditText) view.findViewById(R.id.changePassNew2Txt);
        confirmBtn=(ImageButton) view.findViewById(R.id.changePassConfirmBtn);
        noteTxt = (TextView) view.findViewById(R.id.passwordNote);


        typeface = Typeface.createFromAsset(getActivity().getAssets(),"Raleway-Regular.ttf");

        oldPassTxt.setTypeface(typeface);
        newPassTxt1.setTypeface(typeface);
        newPassTxt2.setTypeface(typeface);
        noteTxt.setTypeface(typeface);



        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(oldPassTxt.equals("")||newPassTxt1.equals("")||newPassTxt2.equals("")){
                    Toast.makeText(getActivity(),"Invalid entry",Toast.LENGTH_SHORT).show();
                }else {

                    final PasswordValidator passwordValidator = new PasswordValidator();
                    if(passwordValidator.validate(newPassTxt1.getText().toString())){ // valid

                        if(newPassTxt1.getText().toString().equals(newPassTxt2.getText().toString())){
                            String newHashedPass=getSha1Hex(newPassTxt1.getText().toString());
                            if(newHashedPass.equals(new UserPreference(getActivity()).getUser().getPassword())){
                              updatePassOnCloud(newHashedPass);
                            }else {
                                noteTxt.setText("Wrong old password");
                                newPassTxt1.setText(null);
                                newPassTxt2.setText(null);
                                oldPassTxt.setText(null);
                            }

                        }else {
                            noteTxt.setText("New password in the two fields isn't the same");
                            newPassTxt1.setText(null);
                            newPassTxt2.setText(null);
                           // oldPassTxt.setText(null);
                        }

                    } else { // invalid password


                        noteTxt.setText("Invalid password,must contain at least an uppercase character,a symbol,and a digit");
                        newPassTxt1.setText(null);
                        newPassTxt2.setText(null);
                        // oldPassTxt.setText(null);
                    }

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



    private void updatePassOnCloud(final String newPass) {
        String URL="http://"+Util.WAMP_SERVER_DOMAIN+"/piptalk/update_password.php";
        StringRequest updatePassRequest=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    FormateChangePassResponse(response);
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
                params.put("user_id",String.valueOf(new UserPreference(getActivity()).getUser().getId()));
                params.put("password",newPass);
                return params;
            }
        };
        ApplicationClass.getInstance().addToRequestQueue(updatePassRequest);
    }

    public void FormateChangePassResponse(String response) throws JSONException {
        JSONObject data=new JSONObject(response);
        if (data.getString("response").equals("success")){
            Toast.makeText(getContext(),"Password is updated successfully",Toast.LENGTH_LONG).show();

        }else {
            Toast.makeText(getContext(),"Can't update password ,Please try later",Toast.LENGTH_LONG).show();
        }
        Intent intent=new Intent(getActivity(), ProfileActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

}
