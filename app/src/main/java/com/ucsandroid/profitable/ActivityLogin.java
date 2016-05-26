package com.ucsandroid.profitable;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;

import com.rey.material.widget.EditText;
import com.rey.material.widget.TextView;

public class ActivityLogin extends AppCompatActivity implements View.OnClickListener {

    private CardView loginButton;
    private TextView forgotButton;
    private EditText usernameField, restaurantField, pinField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = (CardView) findViewById(R.id.login_card);
        loginButton.setOnClickListener(this);

        //Input Textfields
        usernameField = (EditText) findViewById(R.id.username_textfield);
        restaurantField = (EditText) findViewById(R.id.restaurant_id_textfield);
        pinField = (EditText) findViewById(R.id.pin_textfield);

        forgotButton = (TextView) findViewById(R.id.forgot_login);
        forgotButton.setOnClickListener(this);


    }

    //Function to jump to the tableViewActivity page after log-in is succcessful
    private void doLogin()
    {
        Intent tableViewActivity = new Intent(ActivityLogin.this, ActivityTableView.class);
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

            //Check editFields for errors - refactor perhaps
            if(usernameField.getText().toString().equalsIgnoreCase("")){
                usernameField.setError("Username is required");
            }
            else{
                usernameField.clearError();
            }

            if(restaurantField.getText().toString().equalsIgnoreCase("")){
                restaurantField.setError("Restaurant Id is required");
            }
            else{
                restaurantField.clearError();
            }

            if(pinField.getText().toString().equalsIgnoreCase("")){
                pinField.setError("Pin is required");
            }
            else{
                pinField.clearError();
            }

        }
        else if(v == forgotButton){

        }



    }



}