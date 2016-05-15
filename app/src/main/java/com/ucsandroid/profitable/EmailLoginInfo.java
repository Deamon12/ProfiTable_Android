package com.ucsandroid.profitable;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

abstract class EmailLoginInfo extends AppCompatActivity implements View.OnClickListener {

    private Button sendButton, backToLoginButton;
    private EditText user_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email_login_info);

        //Buttons
        sendButton = (Button) findViewById(R.id.respass);
        backToLoginButton = (Button) findViewById(R.id.bktolog);

        //Input Textfields
        user_email = (EditText)findViewById(R.id.forpas);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == sendButton)
                {
                    //TODO implement the check against the address in the database
                    if (user_email.getText().toString().equals("EMAIL ADDRESS IN DATABASE")) {
                        Toast.makeText(getApplicationContext(), "Login Information Sent!", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "There is no such email registered.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //After the Login Information sent, then you just click the button to redirect to the main login page
        backToLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == backToLoginButton) {
                    loginAgain();
                }
            }
        });
    }

    //Function to jump to the main login page if you forgot pass/username
    private void loginAgain() {
        Intent loginAgain = new Intent(EmailLoginInfo.this, ActivityLogin.class);
        startActivity(loginAgain);
        finish();
    }
}