package com.example.instagram.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.instagram.ProfileAdapter;
import com.example.instagram.R;
import com.example.instagram.model.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProfileFragment extends HomeFragment {

    public static final String TAG = "ProfileFragment";

    private TextView tvUsername;
    private ImageView ivProfileImage;
    private TextView tvName;
    private ProfileAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rvPosts = view.findViewById(R.id.rvPosts);
        setProfileInfo(view);

        // Create the data source
        mPosts = new ArrayList<>();
        // Create the adapter
        adapter = new ProfileAdapter(getContext(), mPosts);
        // Set the adapter on the RecyclerView
        rvPosts.setAdapter(adapter);
        // Set the layout manager on the RecyclerView

        // First param is number of columns and second param is orientation i.e Vertical or Horizontal
        layoutManager =
                new GridLayoutManager(getContext(), 3);
        // Attach the layout manager to the recycler view
        rvPosts.setLayoutManager(layoutManager);

        setupSwipeRefreshing(view);

        // For enabling endless scrolling
        enableEndlessScrolling();

        // Adds the scroll listener to RecyclerView
        rvPosts.addOnScrollListener(scrollListener);

        loadTopPosts(new Date(0));
    }

    private void setProfileInfo(@NonNull View view) {
        // Setup layout
        tvUsername = view.findViewById(R.id.tvUsername);
        ivProfileImage = view.findViewById(R.id.ivProfileImage);
        tvName = view.findViewById(R.id.tvName);

        ParseUser user = ParseUser.getCurrentUser();
        tvUsername.setText(user.getUsername());
        ParseFile profileImage = user.getParseFile(Post.KEY_PROFILE_IMAGE);
        if (profileImage != null) {
            Glide.with(getContext())
                    .load(profileImage.getUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(ivProfileImage);
        }
        else {
            Glide.with(getContext())
                    .load(R.drawable.default_avatar)
                    .apply(RequestOptions.circleCropTransform())
                    .into(ivProfileImage);
        }
        String string = user.getString(Post.KEY_NAME);
        tvName.setText(string);

        pb = (ProgressBar) view.findViewById(R.id.pbLoading);
    }

    // Refresh the home screen, and load top posts.
    protected void fetchHomeAsync(int page) {
        adapter.clear();
        loadTopPosts(new Date(0));
        swipeContainer.setRefreshing(false);
    }

    @Override
    protected void loadTopPosts(final Date maxDate) {

        // Show progress bar
        pb.setVisibility(ProgressBar.VISIBLE);

        final Post.Query postsQuery = new Post.Query();

        // Only query posts by the current user
        postsQuery.getTop()
                .withUser()
                .whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());

        // If app is just opened, get newest 20 posts
        // Else query for older posts
        if (maxDate.equals(new Date(0))) {
            adapter.clear();
            postsQuery.getTop()
                    .withUser()
                    .whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
        } else {
            postsQuery.getNext(maxDate)
                    .getTop()
                    .withUser()
                    .whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
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
}
