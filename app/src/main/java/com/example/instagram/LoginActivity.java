package com.example.instagram;

import android.support.design.button.MaterialButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsernameEntry;
    private EditText etPasswordEntry;
    private MaterialButton mbLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }
}
