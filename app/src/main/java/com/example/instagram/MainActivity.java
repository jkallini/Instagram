package com.example.instagram;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.instagram.fragments.ComposeFragment;
import com.example.instagram.fragments.HomeFragment;
import com.example.instagram.fragments.PostDetailsFragment;
import com.example.instagram.fragments.ProfileFragment;
import com.example.instagram.model.Post;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    FragmentManager fragmentManager;

    // PICK_PHOTO_CODE is a constant integer
    public final static int PICK_PHOTO_CODE = 1046;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();

        // Setup toolbar and bottom navigation view
        Toolbar toolbar = findViewById(R.id.home_bar);
        setSupportActionBar(toolbar);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        setFragment(new HomeFragment(),
                                new String[]{HomeFragment.TAG, ComposeFragment.TAG, ProfileFragment.TAG, PostDetailsFragment.TAG});
                        break;
                    case R.id.action_compose:
                        setFragment(new ComposeFragment(),
                                new String[]{ComposeFragment.TAG, HomeFragment.TAG, ProfileFragment.TAG, PostDetailsFragment.TAG});
                        break;
                    case R.id.action_profile:
                        setFragment(new ProfileFragment(),
                                new String[]{ProfileFragment.TAG, HomeFragment.TAG, ComposeFragment.TAG, PostDetailsFragment.TAG});
                        break;
                    default:
                        setFragment(new HomeFragment(),
                                new String[]{HomeFragment.TAG, ComposeFragment.TAG, ProfileFragment.TAG, PostDetailsFragment.TAG});
                        break;
                }
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.action_home);
    }

    // Show/add the given fragment with tag stored in tags[0], and hide fragments with all other tags.
    public void setFragment(Fragment fragment, String[] tags) {

        if(fragmentManager.findFragmentByTag(tags[0]) != null && tags[0] != PostDetailsFragment.TAG) {
            // if the fragment exists, show it.
            fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag(tags[0])).commit();
        } else {
            // if the fragment does not exist, add it to fragment manager.
            // always add a new fragment if it is a PostDetailsFragment
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            // Add sliding animation to PostDetailsFragment
            if (tags[0] == PostDetailsFragment.TAG) {
                transaction.setCustomAnimations(android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right);
            }
            transaction.add(R.id.flContainer, fragment, tags[0]).commit();
        }

        for (int i = 1; i < tags.length; i++) {
            if(fragmentManager.findFragmentByTag(tags[i]) != null) {
                //if the other fragment is visible, hide it.
                fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag(tags[i])).commit();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate home_menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // case statements
            case R.id.action_change_profile_pic:
                changeProfilePic();
                break;
            default :
                logout();
        }
        return super.onOptionsItemSelected(item);
    }

    // Trigger gallery selection for a photo.
    public void changeProfilePic() {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, PICK_PHOTO_CODE);
        }
    }

    // Logout the current user.
    private void logout() {
        ParseUser.logOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
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
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
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
                    fragmentManager.beginTransaction()
                            .replace(R.id.flContainer, new ProfileFragment(), ProfileFragment.TAG)
                            .commit();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
