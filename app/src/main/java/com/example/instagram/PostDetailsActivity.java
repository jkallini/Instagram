package com.example.instagram;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.instagram.model.Post;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;

public class PostDetailsActivity extends AppCompatActivity {

    //private Post post;
    //Context context;

    private TextView tvUsername;
    private TextView tvDescription;
    private ImageView ivImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        tvUsername = findViewById(R.id.tvUsername);
        tvDescription = findViewById(R.id.tvDescription);
        ivImage = findViewById(R.id.ivImage);

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
                        Glide.with(getApplicationContext()).load(image.getUrl()).into(ivImage);
                    }

                    // Set description text
                    tvDescription.setText(post.getDescription());
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}
