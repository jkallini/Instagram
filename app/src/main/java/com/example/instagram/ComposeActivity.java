package com.example.instagram;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class ComposeActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 0; // request code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.compose_bar);
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Logout the user
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate compose_menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.compose_menu, menu);

        // Make "Share" blue
        int positionOfMenuItem = 0;
        MenuItem item = menu.getItem(positionOfMenuItem);
        SpannableString s = new SpannableString("Share");
        s.setSpan(new ForegroundColorSpan(Color.parseColor("#009EFF")), 0, s.length(), 0);
        s.setSpan(new StyleSpan(Typeface.BOLD), 0, s.length(), 0);
        item.setTitle(s);

        return super.onCreateOptionsMenu(menu);
    }
}
