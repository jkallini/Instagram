package com.example.instagram;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.instagram.model.Post;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{

    private Context context;  // Context
    private List<Post> posts; // Data
    private FragmentCommunicator mCommunicator;

    // Constructor
    public PostAdapter(Context context, List<Post> posts, FragmentCommunicator communicator) {
        this.context = context;
        this.posts = posts;
        this.mCommunicator = communicator;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        final ViewHolder holder = new ViewHolder(view, mCommunicator);

        // Allow users to be clickable
        holder.tvUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                ParseUser user = posts.get(position).getUser();
                Intent intent = new Intent(context, ProfileDetailsActivity.class);
                intent.putExtra("user_profile", user);
                context.startActivity(intent);
            }
        });
        holder.ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                ParseUser user = posts.get(position).getUser();
                Intent intent = new Intent(context, ProfileDetailsActivity.class);
                intent.putExtra("user_profile", user);
                context.startActivity(intent);
            }
        });

        // Enable like button
        holder.ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Post post = posts.get(position);
                boolean isLiked = post.isLiked();
                if (!isLiked) {
                    post.likePost(ParseUser.getCurrentUser());
                } else {
                    post.unlikePost(ParseUser.getCurrentUser());
                }
                post.saveInBackground();
                setButton(holder.ivLike, !isLiked,
                        R.drawable.ufi_heart, R.drawable.ufi_heart_active, R.color.red_5);
                setLikeText(post, holder.tvLikeCount);
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    // Interface to communicate data from HomeFragment to PostDetailsFragment.
    public interface FragmentCommunicator {
        void sendPostToDetails(Post post);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Layout fields of item_post
        private TextView tvUsername;
        private ImageView ivImage;
        private TextView tvDescription;
        private TextView tvTimeStamp;
        private ImageView ivProfileImage;
        private ImageView ivLike;
        private TextView tvLikeCount;

        // Fragment communicator
        FragmentCommunicator mCommunicator;

        public ViewHolder(@NonNull View itemView, FragmentCommunicator communicator) {
            super(itemView);

            tvUsername = itemView.findViewById(R.id.tvUsername);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvTimeStamp = itemView.findViewById(R.id.tvTimeStamp);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);

            ivLike = itemView.findViewById(R.id.ivLike);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);

            mCommunicator = communicator;

            // Allow posts to be clickable
            itemView.setOnClickListener(this);

        }

        // Bind the view elements to the Post.
        public void bind(Post post) {

            // Set the handle text
            tvUsername.setText(post.getUser().getUsername());

            // Get the image and load it (if possible)
            ParseFile image = post.getImage();
            if (image != null) {
                Glide.with(context).load(image.getUrl()).into(ivImage);
            }

            // Get the profile image and load it
            ParseFile profileImage = post.getUser().getParseFile(Post.KEY_PROFILE_IMAGE);
            if (profileImage != null) {
                Glide.with(context)
                        .load(profileImage.getUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(ivProfileImage);
            }
            else {
                Glide.with(context)
                        .load(R.drawable.default_avatar)
                        .apply(RequestOptions.circleCropTransform())
                        .into(ivProfileImage);
            }

            // Set description text
            tvDescription.setText(post.getDescription());

            // Set timestamp text
            tvTimeStamp.setText(post.getRelativeTimeAgo());

            setButton(ivLike, post.isLiked(),
                    R.drawable.ufi_heart, R.drawable.ufi_heart_active, R.color.red_5);
            setLikeText(post, tvLikeCount);
        }

        @Override
        public void onClick(View v) {

            Log.d("Viewholder", "POST CLICKED");
            // Get item's position
            int position = getAdapterPosition();

            // Check that the position exists and launch new fragment
            if (position != RecyclerView.NO_POSITION) {

                Post post = posts.get(position);

                // CODE FOR USING DETAILS FRAGMENT
                /* mCommunicator.sendPostToDetails(post); */

                // CODE FOR USING DETAILS ACTIVITY
                Intent intent = new Intent(context, PostDetailsActivity.class);
                intent.putExtra(Post.class.getSimpleName(), post.getObjectId());
                intent.putExtra("post's user", post.getUser());
                context.startActivity(intent);
            }
        }
    }

    // Clean all elements of the recycler
    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Post> list) {
        posts.addAll(list);
        notifyDataSetChanged();
    }

    // sets the color of a button, depending on whether it is active
    private void setButton(ImageView iv, boolean isActive, int strokeResId, int fillResId, int activeColor) {
        iv.setImageResource(isActive ? fillResId : strokeResId);
        iv.setColorFilter(ContextCompat.getColor(context, isActive ? activeColor : R.color.black));
    }

    private void setLikeText(Post post, TextView view) {
        int likeCount = post.getLikeCount();
        if (likeCount == 1) view.setText(String.format("%d like", post.getLikeCount()));
        else view.setText(String.format("%d likes", post.getLikeCount()));
    }
}
