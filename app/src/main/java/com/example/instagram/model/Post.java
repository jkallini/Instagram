package com.example.instagram.model;

import android.text.format.DateUtils;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

@ParseClassName("Post")
public class Post extends ParseObject {

    public static final String KEY_DESCRIPTION = "description";    // Key of description of Post
    public static final String KEY_IMAGE = "image";                // Key of image within Post
    public static final String KEY_USER = "user";                  // Key of user who made Post
    public static final String KEY_CREATED_AT = "createdAt";       // Key of post's creation time
    public static final String KEY_PROFILE_IMAGE = "profileImage"; // Key of user's profile image
    public static final String KEY_NAME = "name";                  // Key of user's name
    public static final String KEY_LIKES = "likes";                // Key of users who liked this post

    // Returns the description of this Post as a String.
    public String getDescription() {
        String description = getString(KEY_DESCRIPTION);
        if (description.compareTo("") == 0) description = ". . .";
        return description;
    }

    // Sets the description of this Post to the parameter String description.
    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    // Returns the image of this post as a ParseFile.
    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    // Sets the image of this post to the parameter ParseFile image.
    public void setImage(ParseFile image) {
        put(KEY_IMAGE, image);
    }

    // Returns the user of this post as a ParseUser.
    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    // Sets the user of this post to the parameter ParseUser user.
    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    // Returns the array of users who liked this post.
    public JSONArray getLikes() {
        return getJSONArray(KEY_LIKES);
    }

    // Returns the number of likes on this post.
    public int getLikeCount() {
        if (getLikes() != null) {
            return getLikes().length();
        }
        else return 0;
    }

    // Add a like to this post.
    public void likePost(ParseUser user) { add(KEY_LIKES, user); }

    // Remove a like from this post.
    public void unlikePost(ParseUser user) {
        ArrayList<ParseUser> a = new ArrayList<ParseUser>();
        a.add(user);
        removeAll(KEY_LIKES, a);
    }

    // Check if this post has been liked.
    public boolean isLiked() {
        JSONArray a = getLikes();
        if(a != null) {
            for (int i = 0; i < a.length(); i++) {
                try {
                    if (a.getJSONObject(i).getString("objectId").equals(ParseUser.getCurrentUser().getObjectId())) {
                        return true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    // Get the relative timestamp of this post.
    public String getRelativeTimeAgo() {
        String instagramFormat = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sf = new SimpleDateFormat(instagramFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(sf.format(this.getCreatedAt())).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    public static class Query extends ParseQuery<Post> {
        public Query() {
            super(Post.class);
        }

        // Limit the number of posts to only the top 20.
        public Query getTop() {
            setLimit(20);
            // Allow reverse-chronological ordering
            orderByDescending(KEY_CREATED_AT);
            return this;
        }

        // Include the user.
        public Query withUser() {
            include(KEY_USER);
            return this;
        }

        // Get post that is older than the maxDate.
        public Query getNext(Date maxDate) {
            whereLessThan(KEY_CREATED_AT, maxDate);
            return this;
        }
    }
}
