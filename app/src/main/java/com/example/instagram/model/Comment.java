package com.example.instagram.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@ParseClassName("Comment")
public class Comment extends ParseObject {

    public static final String KEY_USER = "user";
    public static final String KEY_TEXT = "text";
    public static final String KEY_POST_ID = "postId";
    public static final String KEY_CREATED_AT = "createdAt";

    // Get the user who made this Comment.
    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    // Sets the user of this Comment to the parameter ParseUser user.
    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    // Returns the text of this Comment as a String.
    public String getCommentText() {
        String text = getString(KEY_TEXT);
        if (text.compareTo("") == 0) text = ". . .";
        return text;
    }

    // Sets the text of this Comment to the parameter String description.
    public void setCommentText(String text) {
        put(KEY_TEXT, text);
    }

    // Returns the String id of the Post that this Comment is for.
    public String getPostId() {
        return getString(KEY_POST_ID);
    }

    // Sets the String id of the Post that this Comment is for.
    public void setPostId(String postId) {
        put(KEY_POST_ID, postId);
    }

    // parse Json string into a relative timestamp
    public String getRelativeTimeAgo() {
        String instagramFormat = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sf = new SimpleDateFormat(instagramFormat, Locale.ENGLISH);
        sf.setLenient(true);

        try {
            Date systemDate = sf.parse(sf.format(this.getCreatedAt()));

            Date userDate = new Date();
            double diff = Math.floor((userDate.getTime() - systemDate.getTime()) / 1000);
            if (diff <= 1) {
                return "just now";
            }
            if (diff < 20) {
                return diff + "s";
            }
            if (diff < 40) {
                return "30s";
            }
            if (diff < 60) {
                return "45s";
            }
            if (diff <= 90) {
                return "1m";
            }
            if (diff <= 3540) {
                return Math.round(diff / 60) + "m";
            }
            if (diff <= 5400) {
                return "1h";
            }
            if (diff <= 86400) {
                return Math.round(diff / 3600) + "h";
            }
            if (diff <= 129600) {
                return "1d";
            }
            if (diff < 604800) {
                return Math.round(diff / 86400) + "d";
            }
            if (diff <= 777600) {
                return "1w";
            }
            return Math.round(diff / 604800) + "w";
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static class Query extends ParseQuery<Comment> {
        public Query() {
            super(Comment.class);
        }

        // Limit the number of posts to only the top 20.
        public Comment.Query getOrdered() {
            // Allow reverse-chronological ordering
            orderByDescending(KEY_CREATED_AT);
            return this;
        }

        // Include the user.
        public Comment.Query withUser() {
            include(KEY_USER);
            return this;
        }

        // Get comments on Post with postId.
        public Comment.Query forPost(String postId) {
            whereEqualTo(KEY_POST_ID, postId);
            return this;
        }
    }
}
