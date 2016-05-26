package com.ucsandroid.profitable;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.rey.material.widget.EditText;

public class ActivityLogin extends AppCompatActivity implements View.OnClickListener {

    private Button loginOkButton, loginForgotButton;
    private EditText username_textfield, restaurant_id_textfield, pin_textfield;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Buttons
        //loginOkButton = (Button) findViewById(R.id.login_ok_button);
        //loginForgotButton = (Button) findViewById(R.id.login_forgot_button);

        //Input Textfields
        username_textfield = (EditText) findViewById(R.id.username_textfield);
        restaurant_id_textfield = (EditText) findViewById(R.id.restaurant_id_textfield);
        pin_textfield = (EditText) findViewById(R.id.pin_textfield);

        /**
         Login and move on to the next page if you have the correct information,
         otherwise inform user and do not move on.
         TODO: Implement access to Database to check for correct login
         */
        /*
        loginOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(username_textfield.getText().toString().equals("admin") &&
                    restaurant_id_textfield.toString().equals("1") &&
                    pin_textfield.toString().equals("password")){
                        Toast.makeText(getApplicationContext(), "Logging In...", Toast.LENGTH_SHORT).show();

                        if(v == loginOkButton){
                            doLogin();
                        }
                }
                else{
                    Toast.makeText(getApplicationContext(), "Incorrect Login", Toast.LENGTH_SHORT).show();
                }
            }
        });*/
/*
        //If you forgot login info, press this button, then move on to ForgotLogin page
        loginForgotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if(v == loginForgotButton){
                resetPage();
                }
            }
        });*/

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

    }
}