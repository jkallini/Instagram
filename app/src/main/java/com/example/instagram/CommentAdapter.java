package com.example.instagram;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.instagram.model.Comment;
import com.example.instagram.model.Post;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private Context context;        // Context
    private List<Comment> comments; // Data

    // Constructor
    public CommentAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        final ViewHolder holder = new ViewHolder(view);

        // Allow users to be clickable
        holder.tvUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                ParseUser user = comments.get(position).getUser();
                Intent intent = new Intent(context, ProfileDetailsActivity.class);
                intent.putExtra("user_profile", user);
                context.startActivity(intent);
            }
        });
        holder.ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                ParseUser user = comments.get(position).getUser();
                Intent intent = new Intent(context, ProfileDetailsActivity.class);
                intent.putExtra("user_profile", user);
                context.startActivity(intent);
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.ViewHolder holder, int position) {
        Comment comment = comments.get(position);

        // Set the handle text
        holder.tvUsername.setText(comment.getUser().getUsername());
        holder.tvTimeStamp.setText(comment.getRelativeTimeAgo());
        holder.tvCommentText.setText(comment.getCommentText());

        // Get the profile image and load it
        ParseFile profileImage = comment.getUser().getParseFile(Post.KEY_PROFILE_IMAGE);
        if (profileImage != null) {
            Glide.with(context)
                    .load(profileImage.getUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.ivProfileImage);
        }
        else {
            Glide.with(context)
                    .load(R.drawable.default_avatar)
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.ivProfileImage);
        }
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        // Comment ViewHolder fields
        TextView tvUsername;
        TextView tvCommentText;
        TextView tvTimeStamp;
        ImageView ivProfileImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvCommentText = itemView.findViewById(R.id.tvCommentText);
            tvTimeStamp = itemView.findViewById(R.id.tvTimeStamp);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
        }
    }

    // Clean all elements of the recycler
    public void clear() {
        comments.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Comment> list) {
        comments.addAll(list);
        notifyDataSetChanged();
    }
}
