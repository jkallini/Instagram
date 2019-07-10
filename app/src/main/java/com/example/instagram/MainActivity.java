package com.example.instagram;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.instagram.fragments.ComposeFragment;
import com.example.instagram.fragments.HomeFragment;
import com.example.instagram.fragments.ProfileFragment;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FragmentManager fragmentManager = getSupportFragmentManager();

        // Setup toolbar and bottom navigation view
        Toolbar toolbar = findViewById(R.id.home_bar);
        setSupportActionBar(toolbar);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        setFragment(fragmentManager, new HomeFragment(),
                                HomeFragment.TAG, ComposeFragment.TAG, ProfileFragment.TAG);
                        break;
                    case R.id.action_compose:
                        setFragment(fragmentManager, new ComposeFragment(),
                                ComposeFragment.TAG, HomeFragment.TAG, ProfileFragment.TAG);
                        break;
                    case R.id.action_profile:
                        setFragment(fragmentManager, new ProfileFragment(),
                                ProfileFragment.TAG, HomeFragment.TAG, ComposeFragment.TAG);
                        break;
                    default:
                        setFragment(fragmentManager, new HomeFragment(),
                                HomeFragment.TAG, ComposeFragment.TAG, ProfileFragment.TAG);
                        break;
                }
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.action_home);
    }

    // Show/add the given fragment with tag 1, and hide any fragments with tags 2 or 3.
    private void setFragment(FragmentManager fragmentManager, Fragment fragment, String tag1, String tag2, String tag3) {
        if(fragmentManager.findFragmentByTag(tag1) != null) {
            //if the fragment exists, show it.
            fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag(tag1)).commit();
        } else {
            //if the fragment does not exist, add it to fragment manager.
            fragmentManager.beginTransaction().add(R.id.flContainer, fragment, tag1).commit();
        }
        if(fragmentManager.findFragmentByTag(tag2) != null){
            //if the other fragment is visible, hide it.
            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag(tag2)).commit();
        }
        if(fragmentManager.findFragmentByTag(tag3) != null){
            //if the other fragment is visible, hide it.
            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag(tag3)).commit();
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
        // Logout the user
        ParseUser.logOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
        return super.onOptionsItemSelected(item);
    }
}
