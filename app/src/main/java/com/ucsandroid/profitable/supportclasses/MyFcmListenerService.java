package com.ucsandroid.profitable.supportclasses;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class MyFcmListenerService extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(RemoteMessage message){

        Map data = message.getData();


        JSONObject response = new JSONObject(data);

        int notificationType = -1;


        try {
            notificationType = response.getInt("type");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        if(notificationType == 1){
            System.out.println("Recieved FCM location update: "+data);
            //location status
            int locationId = -1;
            String locationStatus = "";
            try {
                locationId = response.getInt("locationId");
                locationStatus = response.getString("locationStatus");
                sendUpdateLocationUI(locationStatus, locationId);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
        else if(notificationType == 2){
            System.out.println("Recieved FCM kitchen update: "+data);
            //kitchen - an order has been added to the queue
            sendUpdateKitchenUI();

        }
        else if(notificationType == 3){
            System.out.println("Recieved FCM order status update: "+data);
            //food/order status
            sendUpdateOrderStatus();
        }



    }

    private void sendUpdateLocationUI(String locationStatus, int locationId){
        Intent updateIntent = new Intent("update-location");
        updateIntent.putExtra("locationStatus", locationStatus);
        updateIntent.putExtra("locationId", locationId);

        LocalBroadcastManager.getInstance(this).sendBroadcast(updateIntent);
    }

    private void sendUpdateKitchenUI(){ //todo make reciver in kitchen
        Intent updateIntent = new Intent("update-kitchen");
        LocalBroadcastManager.getInstance(this).sendBroadcast(updateIntent);
    }

    private void sendUpdateOrderStatus(){ //todo make recivers in location frags
        Intent updateIntent = new Intent("update-order-status");
        LocalBroadcastManager.getInstance(this).sendBroadcast(updateIntent);
    }

}