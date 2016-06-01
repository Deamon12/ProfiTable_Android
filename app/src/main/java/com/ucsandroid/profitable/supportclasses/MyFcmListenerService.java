package com.ucsandroid.profitable.supportclasses;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ucsandroid.profitable.Singleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class MyFcmListenerService extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(RemoteMessage message){
        //String from = message.getFrom();
        Map data = message.getData();

        //System.out.println("Recieved FCM message: "+message);
        //System.out.println("from: "+from);
        System.out.println("Recieved FCM message: "+data);

        JSONObject response = new JSONObject(data);
        int notificationType = -1;

        int locationId = -1;
        String locationStatus = "";


        try {
            notificationType = response.getInt("type");
            locationId = response.getInt("locationId");
            locationStatus = response.getString("locationStatus");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        if(notificationType == 1){

            sendUpdateLocationUI(locationStatus, locationId);

        }
        else if(notificationType == 2){

            //kitchen

        }
        else if(notificationType == 3){

            //food/order status

        }





    }

    private void sendUpdateLocationUI(String locationStatus, int locationId){
        Intent updateIntent = new Intent("update-location");
        updateIntent.putExtra("locationStatus", locationStatus);
        updateIntent.putExtra("locationId", locationId);

        LocalBroadcastManager.getInstance(this).sendBroadcast(updateIntent);
    }

}