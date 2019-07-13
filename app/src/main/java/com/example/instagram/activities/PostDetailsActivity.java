package com.example.instagram.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.instagram.adapter.CommentAdapter;
import com.example.instagram.R;
import com.example.instagram.model.Comment;
import com.example.instagram.model.Post;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class PostDetailsActivity extends AppCompatActivity {

    private static final String TAG = "PostDetailsActivity";

    // Fields for details
    private TextView tvUsername;
    private TextView tvDescription;
    private ImageView ivImage;
    private ImageView ivProfileImage;
    private TextView tvTimeStamp;
    private TextView tvTitle;
    private TextView tvLikeCount;
    private ImageView ivLike;

    // Fields for comments
    private RecyclerView rvComments;
    private CommentAdapter adapter;
    private List<Comment> comments;
    private EditText etCommentBox;
    private ImageView ivCommentSend;
    private String postId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        // Set views for details
        tvUsername = findViewById(R.id.tvUsername);
        tvDescription = findViewById(R.id.tvDescription);
        ivImage = findViewById(R.id.ivImage);
        ivProfileImage = findViewById(R.id.ivProfileImage);
        tvTimeStamp = findViewById(R.id.tvTimeStamp);
        tvTitle = findViewById(R.id.tvTitle);
        tvLikeCount = findViewById(R.id.tvLikeCount);
        ivLike = findViewById(R.id.ivLike);

        // Set fields for comments
        rvComments = findViewById(R.id.rvComments);
        comments = new ArrayList<>();
        adapter = new CommentAdapter(this, comments);
        rvComments.setAdapter(adapter);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvComments.setLayoutManager(linearLayoutManager);

        etCommentBox = findViewById(R.id.etCommentBox);
        ivCommentSend = findViewById(R.id.ivCommentSend);

        postId = getIntent().getStringExtra(Post.class.getSimpleName());
        final ParseUser user = getIntent().getParcelableExtra("post's user");

        setPostDetails(postId);
        setProfileListeners(user);
        setCommentSendListener();
        populateDetails();
    }

    // Query for comments on this post, and add them to list.
    private void populateDetails() {
        Comment.Query query = new Comment.Query();
        query.getOrdered().withUser().forPost(postId);
        query.findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> objects, ParseException e) {
                if (e == null) {
                    comments.addAll(objects);
                    adapter.notifyItemInserted(0);
                    rvComments.scrollToPosition(0);
                }
                else {
                    Log.e(TAG, "Failed querying for comments");
                }
            }
        });
    }

    // Send a comment when the comment button is pressed.
    private void setCommentSendListener() {
        ivCommentSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Comment comment = new Comment();
                comment.setCommentText(etCommentBox.getText().toString());
                comment.setPostId(postId);
                comment.setUser(ParseUser.getCurrentUser());
                comment.saveInBackground();
                etCommentBox.setText("");
                adapter.clear();
                populateDetails();
            }
        });
    }

    private void setProfileListeners(final ParseUser user) {
        // Allow users to be clickable
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostDetailsActivity.this, ProfileDetailsActivity.class);
                intent.putExtra("user_profile", user);
                startActivity(intent);
            }
        };
        tvUsername.setOnClickListener(listener);
        ivProfileImage.setOnClickListener(listener);
    }

    private void setPostDetails(String postId) {
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
                setLikeText(post, tvLikeCount);

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
                        setLikeText(post, tvLikeCount);
                    }
                });
            }
        });
    }


    private void setLikeText(Post post, TextView view) {
        int likeCount = post.getLikeCount();
        if (likeCount == 1) view.setText(String.format("%d like", post.getLikeCount()));
        else view.setText(String.format("%d likes", post.getLikeCount()));
    }

    // sets the color of a button, depending on whether it is active
    private void setButton(ImageView iv, boolean isActive, int strokeResId, int fillResId, int activeColor) {
        iv.setImageResource(isActive ? fillResId : strokeResId);
        iv.setColorFilter(ContextCompat.getColor(getApplicationContext(), isActive ? activeColor : R.color.black));
    }
}
