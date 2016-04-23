package com.ucsandroid.profitable;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class ActivityLogin extends AppCompatActivity implements View.OnClickListener {

    private Button loginOkButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginOkButton = (Button) findViewById(R.id.login_ok_button);
        loginOkButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        if(v == loginOkButton){
            doLogin();
        }

    }

    private void doLogin() {
        Intent tableViewActivity = new Intent(ActivityLogin.this, ActivityTableView.class);
        startActivity(tableViewActivity);
        finish();
    }


}
