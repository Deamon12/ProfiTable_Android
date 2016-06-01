package com.ucsandroid.profitable.supportclasses;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

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



        }





    }



}