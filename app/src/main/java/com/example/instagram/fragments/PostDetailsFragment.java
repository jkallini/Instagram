package com.example.instagram.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.instagram.R;
import com.example.instagram.model.Post;

public class PostDetailsFragment extends Fragment {

    public static final String TAG = "PostDetailsFragment";

    // Layout fields
    private TextView tvUsername;

    // This post
    private Post post;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        post = getArguments().getParcelable("new_post");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_details, container, false);

        tvUsername = view.findViewById(R.id.tvUsername);

        tvUsername.setText(post.getUser().getUsername());
        return view;
    }
}
