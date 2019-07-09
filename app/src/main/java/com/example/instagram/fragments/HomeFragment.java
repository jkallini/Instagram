package com.example.instagram.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.instagram.PostAdapter;
import com.example.instagram.R;
import com.example.instagram.model.Post;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private final String TAG = "HomeFragment";
    private RecyclerView rvPosts;
    private PostAdapter adapter;
    private List<Post> mPosts;

    // swipe container for swipe to refresh functionality
    private SwipeRefreshLayout swipeContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rvPosts = view.findViewById(R.id.rvPosts);

        // Create the data source
        mPosts = new ArrayList<>();
        // Create the adapter
        adapter = new PostAdapter(getContext(), mPosts);
        // Set the adapter on the RecyclerView
        rvPosts.setAdapter(adapter);
        // Set the layout manager on the RecyclerView
        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));

        setupSwipeRefreshing(view);

        loadTopPosts();
    }

    // Handle logic for Swipe to Refresh.
    private void setupSwipeRefreshing(@NonNull View view) {
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
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
    public void fetchHomeAsync(int page) {
        adapter.clear();
        loadTopPosts();
        swipeContainer.setRefreshing(false);
    }


    // Load the top 20 Instagram posts.
    private void loadTopPosts() {

        final Post.Query postsQuery = new Post.Query();
        postsQuery.getTop().withUser();

        // Get all Instagram posts
        postsQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e == null) {
                    mPosts.addAll(posts);
                    adapter.notifyDataSetChanged();

                    for (int i = 0; i < posts.size(); i++) {
                        Log.d(TAG, "Post[" + i + "] = "
                                + posts.get(i).getDescription()
                                + "\nusername = " + posts.get(i).getUser().getUsername());
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}
