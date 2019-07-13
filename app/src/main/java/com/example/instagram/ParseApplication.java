package com.example.instagram;

import android.app.Application;

import com.example.instagram.model.Comment;
import com.example.instagram.model.Post;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(Comment.class);
        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId("instagram-for-fbu")
                .clientKey("julies-easy-instagram-made-at-fbu")
                .server("https://jkallini-instagram.herokuapp.com/parse")
                .build();

        Parse.initialize(configuration);
    }
}
