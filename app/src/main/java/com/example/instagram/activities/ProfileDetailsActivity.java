package com.example.instagram.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.instagram.adapter.ProfileAdapter;
import com.example.instagram.R;
import com.example.instagram.model.EndlessRecyclerViewScrollListener;
import com.example.instagram.model.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProfileDetailsActivity extends AppCompatActivity {

    public static final String TAG = "ProfileDetailsActivity";

    ParseUser user;

    // Layout fields
    private ImageView ivProfileImage;
    private TextView tvName;
    private TextView tvTitle;

    // Data and layout manager
    protected List<Post> mPosts;
    protected LinearLayoutManager layoutManager;

    // adapter
    private ProfileAdapter adapter;

    // RecyclerView
    protected RecyclerView rvPosts;

    // Swipe container for swipe to refresh functionality
    protected SwipeRefreshLayout swipeContainer;

    // Store a member variable for the listener
    protected EndlessRecyclerViewScrollListener scrollListener;

    // For indeterminate progress bar
    protected ProgressBar pb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details);

        rvPosts = findViewById(R.id.rvPosts);

        Intent intent = getIntent();
        // CHANGE LATER
        user = (ParseUser) intent.getParcelableExtra("user_profile");

        // Create the data source
        mPosts = new ArrayList<>();
        // Create the adapter
        adapter = new ProfileAdapter(this, mPosts);
        // Set the adapter on the RecyclerView
        rvPosts.setAdapter(adapter);
        // Set the layout manager on the RecyclerView
        // First param is number of columns and second param is orientation i.e Vertical or Horizontal
        layoutManager = new GridLayoutManager(this, 3);
        rvPosts.setLayoutManager(layoutManager);

        setupSwipeRefreshing();

        // For enabling endless scrolling
        enableEndlessScrolling();

        // Adds the scroll listener to RecyclerView
        rvPosts.addOnScrollListener(scrollListener);

        setProfileInfo();

        loadTopPosts(new Date(0));
    }

    private void setProfileInfo() {
        // Setup layout
        ivProfileImage = findViewById(R.id.ivProfileImage);
        tvName = findViewById(R.id.tvName);
        tvTitle = findViewById(R.id.tvTitle);

        ParseFile profileImage = user.getParseFile(Post.KEY_PROFILE_IMAGE);
        if (profileImage != null) {
            Glide.with(this)
                    .load(profileImage.getUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(ivProfileImage);
        }
        else {
            Glide.with(this)
                    .load(R.drawable.default_avatar)
                    .apply(RequestOptions.circleCropTransform())
                    .into(ivProfileImage);
        }
        String string = user.getString(Post.KEY_NAME);
        tvName.setText(string);

        tvTitle.setText(user.getUsername());

        pb = (ProgressBar) findViewById(R.id.pbLoading);
    }

    // Code to setup endless scrolling
    protected void enableEndlessScrolling() {
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadTopPosts(getMaxDate());
            }
        };
    }

    // Handle logic for Swipe to Refresh.
    protected void setupSwipeRefreshing() {
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchHomeAsync(0);
            }
        });
        // Configure the refreshing colors (Instagram colors!)
        swipeContainer.setColorSchemeResources(android.R.color.holo_purple,
                android.R.color.holo_blue_bright,
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light);
    }

    // Refresh the home screen, and load top posts.
    protected void fetchHomeAsync(int page) {
        adapter.clear();
        loadTopPosts(new Date(0));
        swipeContainer.setRefreshing(false);
    }

    // Load top 20 posts from this user
    protected void loadTopPosts(final Date maxDate) {

        // Show progress bar
        pb.setVisibility(ProgressBar.VISIBLE);

        final Post.Query postsQuery = new Post.Query();

        // Only query posts by the ser
        postsQuery.getTop()
                .withUser()
                .whereEqualTo(Post.KEY_USER, user);

        // If app is just opened, get newest 20 posts
        // Else query for older posts
        if (maxDate.equals(new Date(0))) {
            adapter.clear();
            postsQuery.getTop()
                    .withUser()
                    .whereEqualTo(Post.KEY_USER, user);
        } else {
            postsQuery.getNext(maxDate)
                    .getTop()
                    .withUser()
                    .whereEqualTo(Post.KEY_USER, user);
        }

        postsQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e == null) {

                    // if opening app, clear out old items
                    if(maxDate.equals(new Date(0))) {
                        adapter.clear();
                    }

                    mPosts.addAll(posts);
                    adapter.notifyDataSetChanged();

                    // For logging purposes
                    for (int i = 0; i < posts.size(); i++) {
                        Log.d(TAG, "Post[" + i + "] = "
                                + posts.get(i).getDescription()
                                + "\nusername = " + posts.get(i).getUser().getUsername());
                    }

                    // Hide progress bar
                    pb.setVisibility(ProgressBar.INVISIBLE);

                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    // Get maximum Date to find next post to load.
    protected Date getMaxDate() {
        int size = mPosts.size();
        if (size == 0) {
            return new Date(0);
        } else {
            Post oldest = mPosts.get(mPosts.size() - 1);
            return oldest.getCreatedAt();
        }
    }
}
