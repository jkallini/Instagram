package com.example.instagram;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.button.MaterialButton;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    // Login fields and button
    private EditText etUsernameEntry;
    private EditText etPasswordEntry;
    private MaterialButton mbLogin;
    private MaterialButton mbSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Find references to the views
        etUsernameEntry = findViewById(R.id.etUsernameEntry);
        etPasswordEntry = findViewById(R.id.etPasswordEntry);
        mbLogin = findViewById(R.id.mbLogin);
        mbSignUp = findViewById(R.id.mbSignUp);

        // Set Sign Up text
        setSignUpText();

        // Set listener for Login button
        mbLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = etUsernameEntry.getText().toString();
                final String password = etPasswordEntry.getText().toString();
                login(username, password);
            }
        });

        // Set listener for Sign Up button
        mbSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    // Set Sign Up button text (for making "Sign up" bold)
    private void setSignUpText() {
        String tempString="Don\'t have an account? Sign up.";
        Button button=(Button)findViewById(R.id.mbSignUp);
        SpannableString spanString = new SpannableString(tempString);
        spanString.setSpan(new StyleSpan(Typeface.BOLD), 23, spanString.length(), 0);
        button.setText(spanString);
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
