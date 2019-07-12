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
import android.widget.ProgressBar;

import com.example.instagram.MainActivity;
import com.example.instagram.PostAdapter;
import com.example.instagram.PostAdapter.FragmentCommunicator;
import com.example.instagram.R;
import com.example.instagram.model.EndlessRecyclerViewScrollListener;
import com.example.instagram.model.Post;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeFragment extends Fragment {

    public static final String TAG = "HomeFragment";
    protected RecyclerView rvPosts;
    private PostAdapter adapter;
    protected List<Post> mPosts;
    protected LinearLayoutManager layoutManager;

    // Swipe container for swipe to refresh functionality
    protected SwipeRefreshLayout swipeContainer;

    // Store a member variable for the listener
    protected EndlessRecyclerViewScrollListener scrollListener;

    // For indeterminate progress bar
    protected ProgressBar pb;

    // Fragment communicator to send posts to PostDetailsFragment
    final FragmentCommunicator communicator = new FragmentCommunicator() {

        @Override
        public void sendPostToDetails(Post post) {

            // Make a new fragment and send Post in a bundle
            PostDetailsFragment fragment = new PostDetailsFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable("new_post", post);
            fragment.setArguments(bundle);

            ((MainActivity) getActivity()).setFragment(fragment,
                    new String[]{PostDetailsFragment.TAG, HomeFragment.TAG, ComposeFragment.TAG, ProfileFragment.TAG});

        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rvPosts = view.findViewById(R.id.rvPosts);

        pb = (ProgressBar) view.findViewById(R.id.pbLoading);

        // Create the data source
        mPosts = new ArrayList<>();
        // Create the adapter
        adapter = new PostAdapter(getContext(), mPosts, communicator);
        // Set the adapter on the RecyclerView
        rvPosts.setAdapter(adapter);
        // Set the layout manager on the RecyclerView
        layoutManager = new LinearLayoutManager(getContext());
        rvPosts.setLayoutManager(layoutManager);

        setupSwipeRefreshing(view);

        // For enabling endless scrolling
        enableEndlessScrolling();

        // Adds the scroll listener to RecyclerView
        rvPosts.addOnScrollListener(scrollListener);

        loadTopPosts(new Date(0));
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
    protected void setupSwipeRefreshing(@NonNull View view) {
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
    protected void fetchHomeAsync(int page) {
        adapter.clear();
        loadTopPosts(new Date(0));
        swipeContainer.setRefreshing(false);
    }


    // Load the top 20 Instagram posts.
    protected void loadTopPosts(final Date maxDate) {

        // Show progress bar
        pb.setVisibility(ProgressBar.VISIBLE);

        final Post.Query postsQuery = new Post.Query();

        // If app is just opened, get newest 20 posts
        // Else query for older posts
        if (maxDate.equals(new Date(0))) {
            adapter.clear();
            postsQuery.getTop().withUser();
        } else {
            postsQuery.getNext(maxDate).getTop().withUser();
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
