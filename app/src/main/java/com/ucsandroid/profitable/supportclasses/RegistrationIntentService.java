package com.ucsandroid.profitable.supportclasses;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.ucsandroid.profitable.ActivityLogin;
import com.ucsandroid.profitable.R;
import com.ucsandroid.profitable.Singleton;

import org.json.JSONException;
import org.json.JSONObject;

public class RegistrationIntentService extends IntentService {

    // abbreviated tag name
    private static final String TAG = "RegIntentService";

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        // Make a call to Instance API
        FirebaseInstanceId instanceID = FirebaseInstanceId.getInstance();
        //String senderId = getResources().getString(R.string.FCM_defaultSenderId);

        // request token that will be used by the server to send push notifications
        String token = instanceID.getToken();
        //Log.d(TAG, "FCM Registration Token: " + token);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        if (settings.contains(getResources().getString(R.string.user_name)) &&
                settings.contains(getResources().getString(R.string.rest_id))) {

            //User has logged in before
            String userId = settings.getString(getResources().getString(R.string.user_name), "");
            updateUserToken(userId, token);
        }



    }

    //UPDATE DEVICEID METHOD//
    private void updateUserToken(String employeeId, String userToken) {

        Uri.Builder builder = Uri.parse("http://52.38.148.241:8080").buildUpon();
        builder.appendPath("com.ucsandroid.profitable")
                .appendPath("rest")
                .appendPath("employee")
                .appendPath("device")
                .appendQueryParameter("emp_id", employeeId)
                .appendQueryParameter("device_id", userToken);

        String myUrl = builder.build().toString();

        //System.out.println("update DeviceId:" + myUrl);

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
                    System.out.println("Updated deviceToken");
                } else {
                    System.out.println("Error updating deviceToken");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };


    Response.ErrorListener deviceErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            //showDeviceErrorDialog();
            System.out.println("ErrorListener updating deviceToken");
        }
    };



}