package com.ucsandroid.profitable;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.iid.FirebaseInstanceId;
import com.rey.material.widget.EditText;
import com.ucsandroid.profitable.supportclasses.RegistrationIntentService;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivityLogin extends AppCompatActivity implements View.OnClickListener {

    private CoordinatorLayout mCoordinator;
    private String mToken = "";
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

        mCoordinator = (CoordinatorLayout) findViewById(R.id.the_coordinator);
        loginButton = (CardView) findViewById(R.id.login_card);
        loginButton.setOnClickListener(this);

        //Input Textfields
        usernameField = (EditText) findViewById(R.id.username_textfield);
        restaurantField = (EditText) findViewById(R.id.restaurant_id_textfield);
        pinField = (EditText) findViewById(R.id.pin_textfield);

        forgotButton = (TextView) findViewById(R.id.forgot_login);
        forgotButton.setOnClickListener(this);


        if (checkPlayServices()) {
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }

        checkUserLoggedIn();

        //GoogleApiClient client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, 0)
                        .show();
            } else {
                Log.i("profit", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private void checkUserLoggedIn() {

        FirebaseInstanceId instanceID = FirebaseInstanceId.getInstance();
        mToken = instanceID.getToken();

        System.out.println("mToken = " + mToken);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(ActivityLogin.this);
        if (settings.contains(getResources().getString(R.string.user_name)) &&
                settings.contains(getResources().getString(R.string.rest_id))) {

            //User has logged in before
            doLogin();
        }
    }

    private void saveLoginInfo(JSONObject userData) throws JSONException {

        String employeeId = userData.getInt("employeeId") + "";
        String restId = userData.getInt("restaurantId") + "";
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
    private void doLogin() {
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

        if (v == loginButton) {

            boolean doLogin = true;
            //Check editFields for errors - refactor perhaps
            if (usernameField.getText().toString().equalsIgnoreCase("")) {    //Check username field
                doLogin = false;
                usernameField.setError("Username is required");
            } else {
                usernameField.clearError();
            }

            if (restaurantField.getText().toString().equalsIgnoreCase("")) {  //Check restaurant field
                doLogin = false;
                restaurantField.setError("Restaurant Id is required");
            } else {
                restaurantField.clearError();
            }

            if (pinField.getText().toString().equalsIgnoreCase("")) {         //Check pin field
                doLogin = false;
                pinField.setError("Pin is required");
            } else {
                pinField.clearError();
            }

            if (doLogin) {            //Continue to volley if nothing errors out
                doVolleyLogin(usernameField.getText().toString(), restaurantField.getText().toString(), pinField.getText().toString());
            }

        } else if (v == forgotButton) {
            //todo
        }

    }



    // ----- Volley Calls & responses------//

    private void doVolleyLogin(String username, String restId, String pin) {

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
                loginSuccessListener,
                errorListener);


        Singleton.getInstance().addToRequestQueue(jsObjRequest);
    }


    private Response.Listener loginSuccessListener = new Response.Listener() {
        @Override
        public void onResponse(Object response) {

            try {
                JSONObject theResponse = new JSONObject(response.toString());
                if (theResponse.getBoolean("success") && theResponse.has("result")) {

                    JSONObject userData = theResponse.getJSONObject("result");
                    String employeeId = userData.getInt("employeeId") + "";

                    //update userToken in server
                    if (mToken != null && !mToken.equalsIgnoreCase("")) {
                        updateUserToken(employeeId, mToken);
                    }
                    //Send retrieved data to saver method
                    saveLoginInfo(userData);
                } else {
                    showErrorSnackbar("Unable to login with those credentials");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };


    //Update Token on server
    private void updateUserToken(String employeeId, String userToken) {

        Uri.Builder builder = Uri.parse("http://52.38.148.241:8080").buildUpon();
        builder.appendPath("com.ucsandroid.profitable")
                .appendPath("rest")
                .appendPath("employee")
                .appendPath("device")
                .appendQueryParameter("emp_id", employeeId)
                .appendQueryParameter("device_id", userToken);

        String myUrl = builder.build().toString();

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.PUT,
                myUrl,
                (JSONObject) null,
                deviceSuccessListener,
                errorListener);

        Singleton.getInstance().addToRequestQueue(jsObjRequest);
    }

    private Response.Listener deviceSuccessListener = new Response.Listener() {
        @Override
        public void onResponse(Object response) {

            try {
                JSONObject theResponse = new JSONObject(response.toString());
                if (theResponse.getBoolean("success") && theResponse.has("result")) {
                } else {
                    showErrorSnackbar("Error updating deviceID");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            showErrorSnackbar("Connection Error");
        }
    };


    public void showErrorSnackbar(String message){
        Snackbar snackbar = Snackbar
                .make(mCoordinator, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

}