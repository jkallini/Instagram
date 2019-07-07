package com.example.instagram.model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("Post")
public class Post extends ParseObject {

    private static final String KEY_DESCRIPTION = "description"; // Key of description of Post
    private static final String KEY_IMAGE = "image";             // Key of image within Post
    private static final String KEY_USER = "user";               // Key of user who made Post

    // Returns the description of this Post as a String.
    public String getDescription() {
        return getString(KEY_DESCRIPTION);
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

    public static class Query extends ParseQuery<Post> {
        public Query() {
            super(Post.class);
        }

        // Limit the number of posts to only the top 20.
        public Query getTop() {
            setLimit(20);
            return this;
        }

        public Query withUser() {
            include("user");
            return this;
        }
    }
}
