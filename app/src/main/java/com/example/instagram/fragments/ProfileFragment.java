package com.example.instagram.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.example.instagram.MainActivity;
import com.example.instagram.ProfileAdapter;
import com.example.instagram.R;
import com.example.instagram.model.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProfileFragment extends HomeFragment {

    public static final String TAG = "ProfileFragment";

    // PICK_PHOTO_CODE is a constant integer
    public final static int PICK_PHOTO_CODE = 1046;

    private TextView tvUsername;
    private ImageView ivProfileImage;
    private TextView tvName;
    private TextView tvChangeProfPhoto;
    private TextView tvLogout;
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
        tvChangeProfPhoto = view.findViewById(R.id.tvChangeProfPhoto);
        tvLogout = view.findViewById(R.id.tvLogout);

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

        tvChangeProfPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeProfilePic();
            }
        });

        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).logout();
            }
        });

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

    // Trigger gallery selection for a photo.
    public void changeProfilePic() {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, PICK_PHOTO_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            Uri photoUri = data.getData();
            uploadProfileImage(photoUri);
        }
    }

    // Upload the photo URI to Parse server.
    private void uploadProfileImage(Uri photoUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), photoUri);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] image = stream.toByteArray();
            final ParseFile parseFile = new ParseFile("profpic.jpg", image);

            parseFile.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    Log.d("MAIN", "SAVE IN BACKGROUND CALLED");
                    ParseUser user = ParseUser.getCurrentUser();
                    user.put(Post.KEY_PROFILE_IMAGE, parseFile);
                    user.saveInBackground();

                    // Refresh to load new profile images
                    getFragmentManager().beginTransaction()
                            .replace(R.id.flContainer, new ProfileFragment(), ProfileFragment.TAG)
                            .commit();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
