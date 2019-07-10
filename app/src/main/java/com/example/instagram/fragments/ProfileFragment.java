package com.example.instagram.fragments;

import android.util.Log;
import android.widget.ProgressBar;

import com.example.instagram.model.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.Date;
import java.util.List;

public class ProfileFragment extends HomeFragment {

    public static final String TAG = "ProfileFragment";

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
