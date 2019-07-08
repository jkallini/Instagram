package com.example.instagram;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.instagram.model.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private Button cam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.home_bar);
        setSupportActionBar(toolbar);

        loadTopPosts();

        cam = findViewById(R.id.btnCamera);
        cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Launch ComposeActvitiy
                Intent intent = new Intent(HomeActivity.this, ComposeActivity.class);
                HomeActivity.this.startActivityForResult(intent, ComposeActivity.REQUEST_CODE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate home_menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Logout the user
        ParseUser.logOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
        return super.onOptionsItemSelected(item);
    }

    // Load the top 20 Instagram posts.
    private void loadTopPosts() {

        final Post.Query postsQuery = new Post.Query();
        postsQuery.getTop().withUser();

        // Get all Instagram posts
        postsQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        Log.d("HomeActivity", "Post[" + i + "] = "
                                + objects.get(i).getDescription()
                                + "\nusername = " + objects.get(i).getUser().getUsername());
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });

    }
}
