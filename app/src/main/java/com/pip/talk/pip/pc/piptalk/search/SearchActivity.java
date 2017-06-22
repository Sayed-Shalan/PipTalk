package com.pip.talk.pip.pc.piptalk.search;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.MatrixCursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.pip.talk.pip.pc.piptalk.ApplicationClass;
import com.pip.talk.pip.pc.piptalk.R;
import com.pip.talk.pip.pc.piptalk.User_Model.UserModel;
import com.pip.talk.pip.pc.piptalk.User_Model.UserPreference;
import com.pip.talk.pip.pc.piptalk.profile.ProfileActivity;
import com.pip.talk.pip.pc.piptalk.profile.ViewUserProfileActivity;
import com.pip.talk.pip.pc.piptalk.xmpp.bean.CcsOutMessage;
import com.pip.talk.pip.pc.piptalk.xmpp.server.CcsClient;
import com.pip.talk.pip.pc.piptalk.xmpp.server.MessageHelper;
import com.pip.talk.pip.pc.piptalk.xmpp.util.Util;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



public class SearchActivity extends AppCompatActivity {

    private ListView listView;
    private String query ;

    private SuggestCursorAdapter adapter;

    private SearchView searchView = null;
    private final String TAG = "TAG";
    private Context globalContext;
    private ArrayList<UserModel> publicAl = new ArrayList<>();
    public String userID ="";

    private TextView username2search;
    private ImageView pic2search;


    private FragmentManager fragmentManager =getSupportFragmentManager();

    private final String URL= "http://"+Util.WAMP_SERVER_DOMAIN+"/piptalk/search.php";



    private final  String[] columns = new String[] { "_id", "text" };


    public SearchActivity() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_searchActivity);
        setSupportActionBar(toolbar);


        listView = (ListView) findViewById(R.id.search_list_view);


        globalContext = this ;

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            Slide slide = new Slide(Gravity.BOTTOM);
            slide.addTarget(R.id.search_list_view); // change this
            slide.setInterpolator(AnimationUtils.loadInterpolator(this,android.R.interpolator.linear_out_slow_in));
            slide.setDuration(300);
            getWindow().setEnterTransition(slide);
        }


        if(savedInstanceState == null){
            if(publicAl.size()==0){
                Log.e("BACK"," creating the fragment ");
                Fragment fragment = SearchFragment.instantiate(this, SearchFragment.class.getName());
                fragmentManager.beginTransaction().add(R.id.hold_container, fragment).commit();
            }

        }


        handleIntent(getIntent());

    }



    private void findUserID(final String usernameK , final View view){

        Log.e("T","inside findUserID");

        getTokenFromResponse(Request.Method.POST, usernameK, new getTokenInterface() {
            @Override
            public void onSuccess(String result) {
                try {
                    findToken(result,usernameK,view);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } );
    }


    private void findToken(String response, String _username,View view) throws JSONException {
        JSONObject responseObject = new JSONObject(response);
        String check=responseObject.getString("response");
        if (check.equals("success")){
            Log.e("T","response is success");

            JSONArray messageArray=responseObject.getJSONArray("user");
            JSONObject object = messageArray.getJSONObject(0);
            userID = object.getString("id");

            Intent pro = new Intent(globalContext, ViewUserProfileActivity.class);

            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
                Log.e("T","inside IF");

                username2search = (TextView) view.findViewById(R.id.foundContactName);
                pic2search = (ImageView) view.findViewById(R.id.foundContactImage);
                Pair<View, String> p1 = Pair.create((View) username2search, "ProfilePicture");
                Pair<View, String> p2 = Pair.create((View)pic2search, "Username");
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, p1, p2);
                // open chat and send data
                pro.putExtra("username",_username);
                pro.putExtra("profile_pic", userID);
                pro.addCategory("userData");
                Log.e("T","before calling start activity");
                ActivityCompat.startActivityForResult(this, pro, 0, options.toBundle());

            }else {
                Log.e("T","inside IF");

                pro.putExtra("username",_username);
                pro.putExtra("profile_pic", userID);
                pro.addCategory("userData");
                Log.e("T","before calling start activity");

                ActivityCompat.startActivityForResult(this, pro, 0,null);
            }


        }else{
            Log.e("T","response is fail");
        }
    }


    private interface getTokenInterface{
        void onSuccess(String result);
    }

    private void getTokenFromResponse(int method, final String username,  final getTokenInterface callback) {
        String url = "http://"+Util.WAMP_SERVER_DOMAIN+"/piptalk/search.php";
        Log.e("T","inside getTokenFromResponse");

        StringRequest loginRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("T","inside onResponse :"+response);
                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("T","inside onErrorResponse :"+error);

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("username",username);
                return params;
            }
        };
        ApplicationClass.getInstance().addToRequestQueue(loginRequest);

    }


    @Override
    protected void onResume() {
        super.onResume();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String username = publicAl.get(i).getUser_name();

                if (username.equals(new UserPreference(globalContext).getUser().getUser_name())){
                    startActivity(new Intent(globalContext, ProfileActivity.class));
                }else {
                    findUserID(username,view); // finds the data and sends it!
                }


//                ConnectTask connectTask = new ConnectTask();
//                connectTask.execute(i);

            }
        });

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);

    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_activity, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setIconified(false);
        searchView.setFocusable(true);
        searchView.requestFocusFromTouch();
        searchView.setQueryHint("Search by username");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        MatrixCursor cursor = new MatrixCursor(columns);

        adapter = new SuggestCursorAdapter(globalContext,cursor,new ArrayList());
        listView.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {   /// important
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.e(TAG,"on Query Text Submit");

                if(!query.equals("")){
                    myWebSevice(URL,query);
                }


                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.e(TAG,"on Query Text change");

                if(!newText.equals("")){
                    myWebSevice(URL,newText);
                }

                return false;
            }
        });





        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG,"set On Search ClickListener");
            }
        });
        return true;
    }




//    private void setupSearchView() {
//        mSearchView.setIconifiedByDefault(true);
//        mSearchView.setOnQueryTextListener((SearchView.OnQueryTextListener) this);
//        mSearchView.setQueryHint("Search Here");
//    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        this.finish();
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case   R.id.action_settings:

                return true;
            case R.id.action_search:
                //  Toast.makeText(this,"text is :"+query,Toast.LENGTH_LONG).show();
                return true;
            default:
//                Toast.makeText(this,"text is :"+item.getTitle() + id ,Toast.LENGTH_LONG).show();

                return super.onOptionsItemSelected(item);
        }

    }

    private interface VolleyCallback{
        void onSuccess(String result);
    }

    public void getStringFromResponse(int method, String url, final String data, final VolleyCallback callback) {

        Log.e("QQ","  data is : "+ data);

        StringRequest stringRequest = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

//                     showJSON(response); // the problem is here!!!!!!!!!!
                Log.e("RES"," "+response);
                callback.onSuccess(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("RES","inside ERROR and the error is :"+ error);

            }}){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("username",data);
                return params;
            }};

        ApplicationClass.getInstance().addToRequestQueue(stringRequest);

    }


    public void myWebSevice(String url, String data){

        getStringFromResponse(Request.Method.POST, url, data, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                showJSON(result);


            }
        });

    }


    private void showJSON(String response){


        ArrayList<UserModel> listModel = new ArrayList<>();
        String checkResponse ="";
        String checkUser ="";

        try {
            JSONObject responseObject = new JSONObject(response);
            checkResponse=responseObject.getString("response");
            checkUser = responseObject.getString("user");

        } catch (JSONException e) {
            e.printStackTrace();

            Log.e(TAG,"inside first catch");
        }

        if (checkResponse.equals("success")&&checkUser.length()>0){
            try {
                Log.e(TAG,"inside second try");
                JSONObject jsonObject = new JSONObject(response);
                int result = jsonObject.getJSONArray("user").length();
                JSONArray array = jsonObject.getJSONArray("user");
//                    JSONObject userObject = result.getJSONObject(1);
                if(result>0){
                    for (int i =0 ; i<result ;i++){
                        UserModel model =new UserModel();
                        model.setUser_name(String.valueOf(array.getJSONObject(i).get("username")));
                        model.setId(array.getJSONObject(i).getInt("id"));
                        model.setImage(array.getJSONObject(i).getString("has_image"));
                        listModel.add(model);
                        //Log.e(TAG,"loop "+i+"user is : >> "+username.get(i));
                    }
                    Log.e("QQ","inside showJSON and it's sent!!");
                    populateData(listModel);
                }
                else {
                    UserModel model =new UserModel();
                    model.setUser_name("No results");
                    model.setId(0);
                    model.setImage("0");
                    listModel.add(model);
                    listModel.add(model);
                    populateData(listModel);
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {


        }
    }


    private void  populateData(ArrayList<UserModel> dataArrayL){

        Object[] temp = new Object[] { 0, "default" };
        MatrixCursor cursor = new MatrixCursor(columns);

        for (int i = 0; i < dataArrayL.size(); i++) {
            temp[0] = i;
            temp[1] = dataArrayL.get(i);
            Log.e("TT","data of ("+i+") "+dataArrayL.get(i));
            cursor.addRow(temp);
        }




        adapter.changeData(dataArrayL);
        adapter.changeCursor(cursor);

        publicAl = dataArrayL;

        if (publicAl.size()>0){
            Fragment currentFragment = fragmentManager.findFragmentById(R.id.hold_container);
            if(currentFragment!= null && currentFragment.isAdded()){
                fragmentManager.beginTransaction()
                        .remove(currentFragment)
                        .commit();
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }

        }


//        if(publicAl.size()==0){
//            showAndHideFrigament("show");
//        }else {
//            showAndHideFrigament("hide");
//        }
        //   searchView.setSuggestionsAdapter(adapter);

    }


    public  class ConnectTask extends AsyncTask<Integer,Void,Void> {

        @Override
        protected Void doInBackground(Integer... Params) {

            String senderId = "995953923725";

            CcsClient ccsClient = CcsClient.prepareClient(senderId, Util.FCM_SERVER_API_KEY, true);

            Log.e("SEND"," Ccs is instantiated prepare clinent method is invoked ");


            try {
                Log.e("SEND","about to step into connect() method");
                ccsClient.connect();
                Log.e("SEND"," connection is built ");
            } catch (XMPPException e) {
                Log.e("SEND"," ccs.connect() (catsh1) : "+e);
                e.printStackTrace();
            } catch (SmackException e) {
                Log.e("SEND"," ccs.connect() (catsh2) : "+e);
                e.printStackTrace();
            } catch (IOException e) {
                Log.e("SEND"," ccs.connect() (catsh3) : "+e);
                e.printStackTrace();
            }

            String messageId = Util.getUniqueMessageId();
            Map<String, String> dataPayload = new HashMap<String, String>();
            dataPayload.put(Util.PAYLOAD_ATTRIBUTE_MESSAGE, "   Hi mama :D   ");
            dataPayload.put(Util.PAYLOAD_ATTRIBUTE_RECIPIENT,publicAl.get(Params[0]).getUser_name());
            Log.e("SEND"," before Ccs message out call");
            CcsOutMessage message = new CcsOutMessage(userID, messageId, dataPayload);
            Log.e("SEND"," after Ccs message out call");
            Log.e("SEND"," before MessageHelper call");
            String jsonRequest = MessageHelper.createJsonOutMessage(message);
            Log.e("SEND"," after MessageHelper call, json request is : "+jsonRequest);

            ccsClient.send(jsonRequest);

            return null;
        }
    }


}


