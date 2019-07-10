package com.example.instagram;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
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
        ViewHolder holder = new ViewHolder(view, mCommunicator);
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
        private TextView tvTimestamp;
        private ImageView ivProfileImage;

        // Fragment communicator
        FragmentCommunicator mCommunicator;

        public ViewHolder(@NonNull View itemView, FragmentCommunicator communicator) {
            super(itemView);

            tvUsername = itemView.findViewById(R.id.tvUsername);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);

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
            tvTimestamp.setText(post.getRelativeTimeAgo());
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
}
