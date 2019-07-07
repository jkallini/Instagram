package com.example.instagram;

import android.content.Intent;
import android.support.design.button.MaterialButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    // Login fields and button
    private EditText etUsernameEntry;
    private EditText etPasswordEntry;
    private MaterialButton mbLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Find references to the views
        etUsernameEntry = findViewById(R.id.etUsernameEntry);
        etPasswordEntry = findViewById(R.id.etPasswordEntry);
        mbLogin = findViewById(R.id.mbLogin);

        // Set listener for login button
        mbLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = etUsernameEntry.getText().toString();
                final String password = etPasswordEntry.getText().toString();
                login(username, password);
            }
        });
    }

    // Login the user with the given username and password
    private void login(String username, String password) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    Log.d("LoginActivity", "Login successful!");

                    // Launch HomeActivity
                    final Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.e("LoginActivity", "Login failed.");
                    e.printStackTrace();
                }
            }
        });
    }
}
