package com.pip.talk.pip.pc.piptalk.fcm;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.pip.talk.pip.pc.piptalk.ApplicationClass;
import com.pip.talk.pip.pc.piptalk.home.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pc on 12/9/2016.
 */

public class CheckUserActivity {

    String state = "";
    String check = "";


    public CheckUserActivity(){

    }


     public String getState(final String username, final String token){

//         Log.e("SEND"," return of webService is :"+check);

         return "";
    }



    private String isMember(String response) throws JSONException {

        Log.e("SEND"," inside isMember() response is : "+response);

        JSONObject responseObject = new JSONObject(response);

        Log.e("SEND"," inside isMember() response object is : "+responseObject);

        String check=responseObject.getString("response");

        Log.e("SEND"," inside isMember() check is : "+check);

        if (check.equals("success")){
            Log.e("SEND"," inside isMember() SUCCESS! : "+check);

            JSONObject userObject=responseObject.getJSONObject("user");

            Log.e("SEND"," inside isMember() userObject is : "+userObject);

            String STATE =userObject.getString("user_state");

            Log.e("SEND"," inside isMember() STATE is : "+STATE);
            MainActivity.globalCheck= STATE;
            return STATE;
        }else{
            Log.e("SEND"," inside isMember() FAIL! : "+check);

            return "";
        }
}



    public void getStringFromResponse(int method, final String username , final String token, final VolleyCallback2 callback) {

       String url = "http://192.168.136.1/piptalk/check_activity.php";
        StringRequest loginRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.v("SEND",response);
                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("Response ","Error");

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("username",username);
                params.put("token",token);
                return params;
            }
        };
        ApplicationClass.getInstance().addToRequestQueue(loginRequest);

    }



    public interface VolleyCallback2{
        void onSuccess(String result);
    }



//    private void showJSON(String response){
//
//
//        ArrayList<String> username = new ArrayList<String>();
//        String checkResponse ="";
//        String checkUser ="";
//
//        try {
//            JSONObject responseObject = new JSONObject(response);
//            checkResponse=responseObject.getString("response");
//            checkUser = responseObject.getString("user");
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//
//            Log.e(TAG,"inside first catch");
//        }
//
//        if (checkResponse.equals("success")&&checkUser.length()>0){
//            try {
//                Log.e(TAG,"inside second try");
//                JSONObject jsonObject = new JSONObject(response);
//                int result = jsonObject.getJSONArray("user").length();
//                JSONArray array = jsonObject.getJSONArray("user");
////                    JSONObject userObject = result.getJSONObject(1);
//                if(result>0){
//                    for (int i =0 ; i<result ;i++){
//                        username.add(String.valueOf(array.getJSONObject(i).get("username")));
//                        Log.e(TAG,"loop "+i+"user is : >> "+username.get(i));
//                    }
//                    Log.e("QQ","inside showJSON and it's sent!!");
//                    populateData(username);
//                }
//                else {
//
//                    username.add("No results");
//                    populateData(username);
//                }
//
//
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        else {
//
//
//        }
//    }

}
