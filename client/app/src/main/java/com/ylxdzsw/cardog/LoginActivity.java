package com.ylxdzsw.cardog;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;


public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText username = (EditText) findViewById(R.id.input_username);
        EditText password = (EditText) findViewById(R.id.input_password);
        Button submit = (Button) findViewById(R.id.submit_login);

        submit.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("username", username.getText().toString());
            intent.putExtra("password", password.getText().toString());

            startActivity(intent);
        });
    }

}

