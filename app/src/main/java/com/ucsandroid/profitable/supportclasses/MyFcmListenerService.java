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
        int type = -1;

        try {
            type = response.getInt("type");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        if(type == 1){

            sendUpdateLocationUI();

        }
        else if(type == 2){

            //kitchen

        }
        else if(type == 3){

            //food/order status

        }





    }

    private void sendUpdateLocationUI(){
        Intent updateIntent = new Intent("update-location");
        LocalBroadcastManager.getInstance(this).sendBroadcast(updateIntent);
    }

}