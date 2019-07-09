package com.example.instagram;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.instagram.model.Post;

public class PostDetailsActivity extends AppCompatActivity {

    private Post post;
    Context context;

    private TextView tvUsername;
    private TextView tvDescription;
    private ImageView ivImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tvUsername = findViewById(R.id.tvUsername);
        tvDescription = findViewById(R.id.tvDescription);
        ivImage = findViewById(R.id.ivImage);

        context = getParent();

        /*
        // Set the handle text
        tvUsername.setText(post.getUser().getUsername());

        // Get the image and load it (if possible)
        ParseFile image = post.getImage();
        if (image != null) {
            Glide.with(context).load(image.getUrl()).into(ivImage);
        }

        // Set description text
        tvDescription.setText(post.getDescription());
        */

    }
}
