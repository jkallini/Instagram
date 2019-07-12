package com.example.instagram;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.instagram.model.Post;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

public class PostDetailsActivity extends AppCompatActivity {

    private TextView tvUsername;
    private TextView tvDescription;
    private ImageView ivImage;
    private ImageView ivProfileImage;
    private TextView tvTimeStamp;
    private TextView tvTitle;
    private TextView tvLikeCount;
    private ImageView ivLike;

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
        tvLikeCount = findViewById(R.id.tvLikeCount);
        ivLike = findViewById(R.id.ivLike);

        String postId = getIntent().getStringExtra(Post.class.getSimpleName());
        final ParseUser user = getIntent().getParcelableExtra("post's user");

        Post.Query query = new Post.Query();

        query.withUser().getInBackground(postId, new GetCallback<Post>() {
            @Override
            public void done(final Post post, ParseException e) {
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

                setButton(ivLike, post.isLiked(),
                        R.drawable.ufi_heart, R.drawable.ufi_heart_active, R.color.red_5);
                int likeCount = post.getLikeCount();
                if (likeCount == 1) tvLikeCount.setText(String.format("%d like", post.getLikeCount()));
                else tvLikeCount.setText(String.format("%d likes", post.getLikeCount()));

                ivLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isLiked = post.isLiked();
                        if (!isLiked) {
                            post.likePost(ParseUser.getCurrentUser());
                        } else {
                            post.unlikePost(ParseUser.getCurrentUser());
                        }
                        post.saveInBackground();
                        setButton(ivLike, !isLiked,
                                R.drawable.ufi_heart, R.drawable.ufi_heart_active, R.color.red_5);
                        int likeCount = post.getLikeCount();
                        if (likeCount == 1) tvLikeCount.setText(String.format("%d like", post.getLikeCount()));
                        else tvLikeCount.setText(String.format("%d likes", post.getLikeCount()));
                    }
                });
            }
        });

        // Allow users to be clickable
        tvUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostDetailsActivity.this, ProfileDetailsActivity.class);
                intent.putExtra("user_profile", user);
                startActivity(intent);
            }
        });
        ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostDetailsActivity.this, ProfileDetailsActivity.class);
                intent.putExtra("user_profile", user);
                startActivity(intent);
            }
        });
    }

    // sets the color of a button, depending on whether it is active
    private void setButton(ImageView iv, boolean isActive, int strokeResId, int fillResId, int activeColor) {
        iv.setImageResource(isActive ? fillResId : strokeResId);
        iv.setColorFilter(ContextCompat.getColor(getApplicationContext(), isActive ? activeColor : R.color.black));
    }
}
