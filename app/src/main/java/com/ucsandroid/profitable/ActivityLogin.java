package com.ucsandroid.profitable;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.iid.InstanceID;
import com.rey.material.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class ActivityLogin extends AppCompatActivity implements View.OnClickListener {

    private CardView loginButton;
    private TextView forgotButton;
    private EditText usernameField, restaurantField, pinField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (!Singleton.hasBeenInitialized()) {
            Singleton.initialize(this);
        }

        loginButton = (CardView) findViewById(R.id.login_card);
        loginButton.setOnClickListener(this);

        //Input Textfields
        usernameField = (EditText) findViewById(R.id.username_textfield);
        restaurantField = (EditText) findViewById(R.id.restaurant_id_textfield);
        pinField = (EditText) findViewById(R.id.pin_textfield);

        forgotButton = (TextView) findViewById(R.id.forgot_login);
        forgotButton.setOnClickListener(this);


        String iid = InstanceID.getInstance(this).getId();
        System.out.println("iid: "+iid);

        checkUserLoggedIn();

    }

    private void checkUserLoggedIn() {

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(ActivityLogin.this);
        if(settings.contains(getResources().getString(R.string.user_name)) &&
                settings.contains(getResources().getString(R.string.rest_id))){
            //User has logged in before
            doLogin();
        }
    }

    private void saveLoginInfo(JSONObject userData) throws JSONException {

        String employeeId = userData.getInt("employeeId")+"";
        String restId = userData.getInt("restaurantId")+"";
        String employeeType = userData.getString("employeeType");
        String accountName = userData.getString("accountName");
        String firstName = userData.getString("firstName");
        String lastName = userData.getString("lastName");


        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(ActivityLogin.this);
        SharedPreferences.Editor edit = settings.edit();
        edit.putString(getString(R.string.user_name), accountName);
        edit.putString(getString(R.string.rest_id), restId);

        edit.putString(getString(R.string.employee_id), employeeId);
        edit.putString(getString(R.string.employee_type), employeeType);

        edit.putString(getString(R.string.first_name), firstName);
        edit.putString(getString(R.string.last_name), lastName);

        edit.apply();

        doLogin();
    }

    //Function to jump to the tableViewActivity page after log-in is succcessful
    private void doLogin()
    {
        Intent tableViewActivity = new Intent(ActivityLogin.this, ActivityLocationView.class);
        startActivity(tableViewActivity);
        finish();
    }

    //Function to jump to the reset page if you forgot pass/username
    private void resetPage() {
        Intent forgotLoginActivity = new Intent(ActivityLogin.this, RetrieveLoginInfo.class);
        startActivity(forgotLoginActivity);
        finish();
    }

    @Override
    public void onClick(View v) {

        if(v == loginButton){

            boolean doLogin = true;
            //Check editFields for errors - refactor perhaps
            if(usernameField.getText().toString().equalsIgnoreCase("")){    //Check username field
                doLogin = false;
                usernameField.setError("Username is required");
            }
            else{
                usernameField.clearError();
            }

            if(restaurantField.getText().toString().equalsIgnoreCase("")){  //Check restaurant field
                doLogin = false;
                restaurantField.setError("Restaurant Id is required");
            }
            else{
                restaurantField.clearError();
            }

            if(pinField.getText().toString().equalsIgnoreCase("")){         //Check pin field
                doLogin = false;
                pinField.setError("Pin is required");
            }
            else{
                pinField.clearError();
            }

            if(doLogin){            //Continue to volley if nothing errors out
                doVolleyLogin(usernameField.getText().toString(), restaurantField.getText().toString(), pinField.getText().toString());
            }

        }
        else if(v == forgotButton){
            //todo
        }

    }

    private void doVolleyLogin(String username, String restId, String pin){

        //http://52.38.148.241:8080/com.ucsandroid.profitable/rest/employee/login?rest_id=1&account_name=bigX&account_pass=password
        Uri.Builder builder = Uri.parse("http://52.38.148.241:8080").buildUpon();
        builder.appendPath("com.ucsandroid.profitable")
                .appendPath("rest")
                .appendPath("employee")
                .appendPath("login")
                .appendQueryParameter("rest_id", restId)
                .appendQueryParameter("account_name", username)
                .appendQueryParameter("account_pass", pin);

        String myUrl = builder.build().toString();

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST,
                myUrl,
                (JSONObject) null,
                successListener, errorListener);

        // Access the RequestQueue through your singleton class.
        Singleton.getInstance().addToRequestQueue(jsObjRequest);

    }


    private Response.Listener successListener = new Response.Listener() {
        @Override
        public void onResponse(Object response) {

            try {
                JSONObject theResponse = new JSONObject(response.toString());

                if(theResponse.getBoolean("success") && theResponse.has("result")){

                    JSONObject userData = theResponse.getJSONObject("result");


                    //Send retrieved data to saver method
                    saveLoginInfo(userData);

                }
                else{
                    //TODO:Results Error ((ActivityOrderView)getActivity()).showErrorSnackbar(theResponse.getString("message"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            System.out.println("Volley error: " + error);///TODO Connect/server error
        }
    };


}