package com.ucsandroid.profitable;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.rey.material.widget.EditText;
import com.ucsandroid.profitable.supportclasses.RegistrationIntentService;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivityLogin extends AppCompatActivity implements View.OnClickListener {

    private String mToken = "";
    private String userIID = "";
    private CardView loginButton;
    private TextView forgotButton;
    private EditText usernameField, restaurantField, pinField;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

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


        if (checkPlayServices()) {
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }

        checkUserLoggedIn();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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

        //userIID = InstanceID.getInstance(this).getId();


        System.out.println("userIID = " + userIID);
        System.out.println("mToken = " + mToken);


        //todo: check if error getting iid

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

    private void doVolleyLogin(String username, String restId, String pin) {

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
                loginSuccessListener, loginErrorListener);

        // Access the RequestQueue through your singleton class.
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

                    //update userIID in server
                    if (!userIID.equalsIgnoreCase("")) {
                        updateUserIID(employeeId, userIID);
                    }
                    //Send retrieved data to saver method
                    saveLoginInfo(userData);
                } else {
                    //TODO:Results Error ((ActivityOrderView)getActivity()).showErrorSnackbar(theResponse.getString("message"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };


    Response.ErrorListener loginErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            System.out.println("Volley error: " + error);///TODO Connect/server error
        }
    };


    //UPDATE DEVICEID METHODS//

    private void updateUserIID(String employeeId, String userIID) {

        Uri.Builder builder = Uri.parse("http://52.38.148.241:8080").buildUpon();
        builder.appendPath("com.ucsandroid.profitable")
                .appendPath("rest")
                .appendPath("employee")
                .appendPath("device")
                .appendQueryParameter("emp_id", employeeId)
                .appendQueryParameter("device_id", userIID);

        String myUrl = builder.build().toString();

        System.out.println("update DeviceId:" + myUrl);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.PUT,
                myUrl,
                (JSONObject) null,
                deviceSuccessListener, deviceErrorListener);

        // Access the RequestQueue through your singleton class.
        Singleton.getInstance().addToRequestQueue(jsObjRequest);
    }

    private Response.Listener deviceSuccessListener = new Response.Listener() {
        @Override
        public void onResponse(Object response) {

            try {
                JSONObject theResponse = new JSONObject(response.toString());
                if (theResponse.getBoolean("success") && theResponse.has("result")) {
                    doLogin();
                } else {
                    showDeviceErrorDialog();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };


    Response.ErrorListener deviceErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            showDeviceErrorDialog();
        }
    };

    private void showDeviceErrorDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Error setting device ID, you may not receive push notifications.")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //continue
                        dialog.dismiss();
                        doLogin();
                    }
                })
                .setNeutralButton("Retry", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //retry
                        dialog.dismiss();
                        //userIID = InstanceID.getInstance(ActivityLogin.this).getId();

                        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(ActivityLogin.this);
                        String empoyeeID = settings.getString(getString(R.string.employee_id), "");
                        updateUserIID(empoyeeID, userIID);

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        dialog.dismiss();
                    }
                });
        // Create the AlertDialog object and return it
        builder.create();

    }


    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "ActivityLogin Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.ucsandroid.profitable/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "ActivityLogin Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.ucsandroid.profitable/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}