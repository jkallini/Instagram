package com.example.instagram;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.button.MaterialButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignupActivity extends AppCompatActivity {

    // Login fields and button
    private EditText etUsernameEntry;
    private EditText etEmailEntry;
    private EditText etPasswordEntry;
    private EditText etPasswordConfirm;
    private MaterialButton mbSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Find references to the views
        etUsernameEntry = findViewById(R.id.etUsernameEntry);
        etEmailEntry = findViewById(R.id.etEmailEntry);
        etPasswordEntry = findViewById(R.id.etPasswordEntry);
        etPasswordConfirm = findViewById(R.id.etPasswordConfirm);
        mbSignUp = findViewById(R.id.mbLogin);

        // Set listener for Login button
        mbSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = etUsernameEntry.getText().toString();
                final String email = etEmailEntry.getText().toString();
                final String password = etPasswordEntry.getText().toString();
                final String confirm = etPasswordConfirm.getText().toString();

                // Check that the passwords match
                if (confirm.compareTo(password) != 0) {
                    Toast.makeText(SignupActivity.this, "passwords do not match", Toast.LENGTH_LONG).show();
                } else {
                    signUp(username, email, password);
                }
            }
        });
    }

    // Sign up the user with the given username, email, and password.
    private void signUp(String username, String email, String password) {

        // Create the ParseUser
        ParseUser user = new ParseUser();

        // Set core properties
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);

        // Invoke signUpInBackground
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Hooray! Let them use the app now.
                    Log.d("SignupActivity", "Sign up successful!");

                    // Launch HomeActivity (and finish Login and Signup activities)
                    final Intent intent = new Intent(SignupActivity.this, HomeActivity.class);
                    startActivity(intent);
                    LoginActivity.loginActivity.finish();
                    finish();
                } else {
                    Log.e("SignupActivity", "Sign up failed.");
                    e.printStackTrace();
                }
            }
        });
    }
}
