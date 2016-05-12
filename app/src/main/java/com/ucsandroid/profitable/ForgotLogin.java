package com.ucsandroid.profitable;

/**
 * Created by Shamim on 5/10/2016.
 *
 * ForgotLogin Activity that user should be directed to if they choose the "Forgot"
 * option when logging into ProfiTable. This activity should give options for what they
 * have forgotten (username, PIN, or restaurant ID). From here you can give them the option
 * to receive their missing info via email (whatever is on file).
 *
 * TODO: Begin actual implementation, just a placeholder for now
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class ForgotLogin extends AppCompatActivity implements View.OnClickListener {

    private Button loginOkButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_login);

        //Create the buttons for choices on the page
        loginOkButton = (Button) findViewById(R.id.login_ok_button);
        loginOkButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        //If you click on the button, then call the function
        if(v == loginOkButton){
            doLogin();
        }
    }

    private void doLogin() {
        Intent tableViewActivity = new Intent(ForgotLogin.this, ActivityTableView.class);

        //Open up a new activity
        startActivity(tableViewActivity);
        finish();
    }
}
