package com.ylxdzsw.cardog;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;


public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button today     = (Button) findViewById(R.id.submit_today);
        Button lastmonth = (Button) findViewById(R.id.submit_lastmonth);

        today    .setOnClickListener(v -> send(new Intent(this, TodayActivity.class)));
        lastmonth.setOnClickListener(v -> send(new Intent(this, LastmonthActivity.class)));
    }

    private void send(Intent intent) {
        EditText username = (EditText) findViewById(R.id.input_username);
        EditText password = (EditText) findViewById(R.id.input_password);

        intent.putExtra("username", username.getText().toString());
        intent.putExtra("password", password.getText().toString());

        startActivity(intent);
    }
}

