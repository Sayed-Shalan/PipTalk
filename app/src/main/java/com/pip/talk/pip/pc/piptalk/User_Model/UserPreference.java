package com.pip.talk.pip.pc.piptalk.User_Model;


import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.iid.FirebaseInstanceId;

public class UserPreference {
    private int PRIVATE_MODE=1;
    private String PREF_NAME="user";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private Context context;
    private String KEY_UER_ID="user_id";
    private String KEY_UER_IMAGE="user_image";
    private String KEY_UER_NAME="user_name";
    private String KEY_USER_NATIVE_LANGUAGE="user_native_language";
    private String KEY_UER_TOKEN="user_token";
    private String KEY_UER_STATE="user_state";
    private String KEY_UER_PASS="user_state";
    private String KEY_UER_GENDER="user_gender";
    private String KEY_UER_STATUS="user_status";
    private String KEY_UER_PHONE="user_phone";
    private String KEY_UER_BIRTH_DATE="user_birth_date";

    public UserPreference(Context context){
        this.context=context;
        sharedPreferences=context.getSharedPreferences(PREF_NAME,PRIVATE_MODE);
        editor=sharedPreferences.edit();
    }

    public UserPreference(){
        sharedPreferences=context.getSharedPreferences(PREF_NAME,PRIVATE_MODE);
        editor=sharedPreferences.edit();
    }




    public void storeUser(UserModel model){
        editor.putInt(KEY_UER_ID,model.getId());
        editor.putString(KEY_UER_BIRTH_DATE,model.getDateOfBirth());
        editor.putString(KEY_UER_GENDER,model.getGender());
        editor.putString(KEY_UER_IMAGE,model.getImage());
        editor.putString(KEY_UER_PASS,model.getPassword());
        editor.putString(KEY_UER_NAME,model.getUser_name());
        editor.putString(KEY_UER_STATE,model.getUserState());
        editor.putString(KEY_UER_STATUS,model.getStatus());
        editor.putString(KEY_UER_TOKEN,model.getUserToken());
        editor.putString(KEY_UER_PHONE,model.getPhoneNumber());
        editor.putString(KEY_USER_NATIVE_LANGUAGE,model.getNativeLanguage());
        editor.commit();
    }

    public UserModel getUser(){
        UserModel model =new UserModel();
        model.setId(sharedPreferences.getInt(KEY_UER_ID,0));
        model.setUser_name(sharedPreferences.getString(KEY_UER_NAME,null));
        model.setUserToken(sharedPreferences.getString(KEY_UER_TOKEN,null));
        model.setUserState(sharedPreferences.getString(KEY_UER_STATE,null));
        model.setPassword(sharedPreferences.getString(KEY_UER_PASS,null));
        model.setGender(sharedPreferences.getString(KEY_UER_GENDER,null));
        model.setStatus(sharedPreferences.getString(KEY_UER_STATUS,null));
        model.setDateOfBirth(sharedPreferences.getString(KEY_UER_BIRTH_DATE,null));
        model.setImage(sharedPreferences.getString(KEY_UER_IMAGE,null));
        model.setPhoneNumber(sharedPreferences.getString(KEY_UER_PHONE,null));
        model.setNativeLanguage(sharedPreferences.getString(KEY_USER_NATIVE_LANGUAGE,null));

        return model;
    }

    public void setKEY_USER_NATIVE_LANGUAGE(String KEY_USER_NATIVE_LANGUAGE) {
        editor.putString(this.KEY_USER_NATIVE_LANGUAGE,KEY_USER_NATIVE_LANGUAGE);
        editor.commit();

    }

    public void setKEY_UER_GENDER(String KEY_UER_GENDER) {
        editor.putString(this.KEY_UER_GENDER,KEY_UER_GENDER);
        editor.commit();


    }

    public void setKEY_UER_STATUS(String KEY_UER_STATUS) {
        editor.putString(this.KEY_UER_STATUS,KEY_UER_STATUS);
        editor.commit();


    }

    public void setKEY_UER_PHONE(String KEY_UER_PHONE) {
        editor.putString(this.KEY_UER_PHONE,KEY_UER_PHONE);
        editor.commit();
    }

    public void setKEY_UER_BIRTH_DATE(String KEY_UER_BIRTH_DATE) {
        editor.putString(this.KEY_UER_BIRTH_DATE,KEY_UER_BIRTH_DATE);
        editor.commit();

    }

    public String getUsername(){
        return sharedPreferences.getString(KEY_UER_NAME,"");
    }
    public void deleteUser(){
        editor.clear();
        editor.commit();
    }

    public void updateUserToken(){
        editor.putString(KEY_UER_TOKEN,FirebaseInstanceId.getInstance().getToken());
        editor.commit();
    }

    public  String  getToken(){
        return sharedPreferences.getString(KEY_UER_TOKEN,"");
    }
    public  String  userLanguage(){
        return sharedPreferences.getString(KEY_USER_NATIVE_LANGUAGE,"");
    }
    public int getUserId(){
        return sharedPreferences.getInt(KEY_UER_ID,0);
    }
    public  String  getGender(){
        return sharedPreferences.getString(KEY_UER_GENDER,"");
    } public  String  getNLanguage(){
        return sharedPreferences.getString(KEY_USER_NATIVE_LANGUAGE,"");
    } public  String  getHasImage(){
        return sharedPreferences.getString(KEY_UER_IMAGE,"");
    } public  String  getStatus(){
        return sharedPreferences.getString(KEY_UER_STATUS,"");
    }
    public  String  getPhoneNumber(){
        return sharedPreferences.getString(KEY_UER_PHONE,"");
    } public  String  getDOB(){
        return sharedPreferences.getString(KEY_UER_BIRTH_DATE,"");
    }
    public void updateProfilePicture(String pPname){
        editor.putString(KEY_UER_IMAGE,pPname);
        editor.commit();
    }

    public void updateUserData(UserModel model){
        editor.putString(KEY_UER_BIRTH_DATE,model.getDateOfBirth());
        editor.putString(KEY_UER_GENDER,model.getGender());
        editor.putString(KEY_UER_STATUS,model.getStatus());
        editor.putString(KEY_UER_PHONE,model.getPhoneNumber());
        editor.putString(KEY_USER_NATIVE_LANGUAGE,model.getNativeLanguage());

        editor.commit();
    }

    public void updateUserId(int id){
        editor.putInt(KEY_UER_ID,id);
        editor.commit();
    }
}
