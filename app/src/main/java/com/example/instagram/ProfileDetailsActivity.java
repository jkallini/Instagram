package com.example.instagram;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.instagram.model.Post;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

public class ProfileDetailsActivity extends AppCompatActivity {

    Context context;

    private TextView tvUsername;
    private TextView tvDescription;
    private ImageView ivImage;
    private ImageView ivProfileImage;
    private TextView tvTimeStamp;
    private TextView tvTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        tvUsername = findViewById(R.id.tvUsername);
        tvDescription = findViewById(R.id.tvDescription);
        ivImage = findViewById(R.id.ivImage);
        ivProfileImage = findViewById(R.id.ivProfileImage);
        tvTimeStamp = findViewById(R.id.tvTimeStamp);
        tvTitle = findViewById(R.id.tvTitle);

        String postId = getIntent().getStringExtra(Post.class.getSimpleName());

        Post.Query query = new Post.Query();

        query.withUser().getInBackground(postId, new GetCallback<Post>() {
            @Override
            public void done(Post post, ParseException e) {
                if (e == null) {
                    // Set the handle text
                    tvUsername.setText(post.getUser().getUsername());

                    // Get the image and load it (if possible)
                    ParseFile image = post.getImage();
                    if (image != null) {
                        Glide.with(getApplicationContext())
                                .load(image.getUrl())
                                .into(ivImage);
                    }

                    // Get the profile image and load it
                    ParseUser user = post.getUser();
                    ParseFile profileImage = user.getParseFile(Post.KEY_PROFILE_IMAGE);
                    if (profileImage != null) {
                        Glide.with(getApplicationContext())
                                .load(profileImage.getUrl())
                                .apply(RequestOptions.circleCropTransform())
                                .into(ivProfileImage);
                    }
                    else {
                        Glide.with(getApplicationContext())
                                .load(R.drawable.default_avatar)
                                .apply(RequestOptions.circleCropTransform())
                                .into(ivProfileImage);
                    }

                    // Set description text
                    tvDescription.setText(post.getDescription());

                    // Set relative timestamp
                    tvTimeStamp.setText(post.getRelativeTimeAgo());

                    // Set toolbar text
                    tvTitle.setText(tvUsername.getText().toString() + "\'s post");

                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}
