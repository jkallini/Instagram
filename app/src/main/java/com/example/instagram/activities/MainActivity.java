package com.example.instagram.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.instagram.R;
import com.example.instagram.fragments.ComposeFragment;
import com.example.instagram.fragments.HomeFragment;
import com.example.instagram.fragments.ProfileFragment;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    FragmentManager fragmentManager;

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
                                new String[]{HomeFragment.TAG, ComposeFragment.TAG, ProfileFragment.TAG});
                        break;
                    case R.id.action_compose:
                        setFragment(new ComposeFragment(),
                                new String[]{ComposeFragment.TAG, HomeFragment.TAG, ProfileFragment.TAG});
                        break;
                    case R.id.action_profile:
                        setFragment(new ProfileFragment(),
                                new String[]{ProfileFragment.TAG, HomeFragment.TAG, ComposeFragment.TAG});
                        break;
                    default:
                        setFragment(new HomeFragment(),
                                new String[]{HomeFragment.TAG, ComposeFragment.TAG, ProfileFragment.TAG});
                        break;
                }
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.action_home);
    }

    // Show/add the given fragment with tag stored in tags[0], and hide fragments with all other tags.
    public void setFragment(Fragment fragment, String[] tags) {

        if(fragmentManager.findFragmentByTag(tags[0]) != null) {
            // if the fragment exists, show it.
            fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag(tags[0])).commit();
        } else {
            // if the fragment does not exist, add it to fragment manager.
            // always add a new fragment if it is a PostDetailsFragment
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.flContainer, fragment, tags[0]).commit();
        }

        for (int i = 1; i < tags.length; i++) {
            if(fragmentManager.findFragmentByTag(tags[i]) != null) {
                //if the other fragment is visible, hide it.
                fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag(tags[i])).commit();
            }
        }
    }

    // Logout the current user.
    public void logout() {
        ParseUser.logOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
