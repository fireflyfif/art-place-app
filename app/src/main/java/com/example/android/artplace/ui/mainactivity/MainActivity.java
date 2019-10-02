/*
 * PROJECT LICENSE
 * This project was submitted by Iva Ivanova as part of the Nanodegree at Udacity.
 *
 * According to Udacity Honor Code we agree that we will not plagiarize (a form of cheating) the work of others. :
 * Plagiarism at Udacity can range from submitting a project you didn’t create to copying code into a program without
 * citation. Any action in which you misleadingly claim an idea or piece of work as your own when it is not constitutes
 * plagiarism.
 * Read more here: https://udacity.zendesk.com/hc/en-us/articles/360001451091-What-is-plagiarism-
 *
 * MIT License
 *
 * Copyright (c) 2018 Iva Ivanova
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 *
 */

package com.example.android.artplace.ui.mainactivity;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.android.artplace.ArtPlaceApp;
import com.example.android.artplace.R;
import com.example.android.artplace.callbacks.OnRefreshListener;
import com.example.android.artplace.ui.artworks.ArtworksFragment;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements OnRefreshListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String THEME_PREFERENCE_KEY = "theme_prefs";
    private static final String POSITION_KEY = "position";

    @BindView(R.id.appbar_main)
    AppBarLayout appBarLayout;
    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigation;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private Tracker mTracker;
    private SharedPreferences mPreferences;
    private boolean mIsDayMode;
    private String mTitle;
    private int mPosition;

    private Fragment fragmentArtworks = new ArtworksFragment();
    private final FragmentManager mFragmentManager = getSupportFragmentManager();
    private FragmentTransaction mTransaction;
    private Fragment mFragments[] = new Fragment[3];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set the theme before creating the View
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        if (savedInstanceState != null) {
            // Get the position of the selected Fragment
            mPosition = savedInstanceState.getInt(POSITION_KEY);
        }
        Log.d(TAG, "onCreate: position: " + mPosition);
        // Set the title on the toolbar according to
        // the position of the clicked Fragment
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(mTitle);
        }

        // TODO: Leaving the Navigation component aside, because of recreation of fragments on every tab switched
        // Setup the Navigation with just two lines
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        // Setup Bottom navigation with Navigation Controller
        NavigationUI.setupWithNavController(bottomNavigation, navController);

        // Specify the top level navigation so that there is no Up Button on them
        // source: https://stackoverflow.com/a/53251574/8132331
        Set<Integer> topLevelDestinations = new HashSet<>();
        topLevelDestinations.add(R.id.artworks_destination);
        topLevelDestinations.add(R.id.search_destination);
        topLevelDestinations.add(R.id.favorites_destination);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(topLevelDestinations).build();
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);

        // Obtain the shared Tracker instance.
        // source: https://developers.google.com/analytics/devguides/collection/android/v4/
        ArtPlaceApp application = (ArtPlaceApp) getApplication();
        mTracker = application.getDefaultTracker();

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mIsDayMode = mPreferences.getBoolean(THEME_PREFERENCE_KEY, false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the position of the selected Fragment
        outState.putInt(POSITION_KEY, mPosition);
    }

    @Override
    public void onRefreshConnection() {
        Log.d(TAG, "onRefreshConnection is now triggered");
        setAppBarVisible();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume is called now!");

        mTracker.setScreenName(getString(R.string.analytics_artwork_screenname));
        // Send initial screen screen view hit.
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        // Show the AppBarLayout every time after resume
        setAppBarVisible();
    }

    private void setAppBarVisible() {
        // Set the AppBarLayout to expanded
        appBarLayout.setExpanded(true, true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                return false;

            case R.id.action_refresh:
                return false;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return super.onSupportNavigateUp();
    }
}
