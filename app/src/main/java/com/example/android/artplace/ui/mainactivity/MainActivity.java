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

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.artplace.ArtPlaceApp;
import com.example.android.artplace.R;
import com.example.android.artplace.callbacks.OnRefreshListener;
import com.example.android.artplace.ui.mainactivity.adapters.BottomNavAdapter;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
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
    private BottomNavAdapter mPagerAdapter;
    private SharedPreferences mPreferences;
    private boolean mIsDayMode;
    private String mTitle;
    private int mPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set the theme before creating the View
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        if (savedInstanceState != null) {
            // Get the position of the selected Fragment
            mPosition = savedInstanceState.getInt(POSITION_KEY);
        }

        Log.d(TAG, "onCreate: position: " + mPosition);
        // Set the title on the toolbar according to
        // the position of the clicked Fragment
        setToolbarTitle(mPosition);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(mTitle);
        }

        // Setup the Navigation with just two lines
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        // Setup Bottom navigation with Navigation Controller
        NavigationUI.setupWithNavController(bottomNavigation, navController);

        // Obtain the shared Tracker instance.
        // source: https://developers.google.com/analytics/devguides/collection/android/v4/
        ArtPlaceApp application = (ArtPlaceApp) getApplication();
        mTracker = application.getDefaultTracker();

        //setupViewPager();
        //setupBottomNavStyle();

        // Add items to the Bottom Navigation
        //addBottomNavigationItems();
        //bottomNavigation.getSelectedItemId(R.id.artworks_destination);

        //bottomNavigation.setCurrentItem(mPosition);

        /*bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.artworks_destination:
                        toolbar.setTitle("Artworks");
                        break;
                    case R.id.search_destination:
                        toolbar.setTitle("Search");
                        break;
                    case R.id.favorites_destination:
                        toolbar.setTitle("Favorites");
                        break;
                }

                return true;
            }
        });*/

        /*bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {

                bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
                //viewPager.setCurrentItem(position);

                mPosition = position;
                // Set the title on the toolbar according to
                // the position of the clicked Fragment
                setToolbarTitle(position);

                return true;
            }
        });*/

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mIsDayMode = mPreferences.getBoolean(THEME_PREFERENCE_KEY, false);
    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the position of the selected Fragment
        outState.putInt(POSITION_KEY, mPosition);
    }

    /**
     * Method for setting the title on the Toolbar for each Fragment
     *
     * @param position of the current Fragment
     */
    private void setToolbarTitle(int position) {
        switch (position) {
            case 0:
                mTitle = getString(R.string.title_artworks);
                toolbar.setTitle(mTitle);
                break;
            case 1:
                mTitle = getString(R.string.title_search);
                toolbar.setTitle(mTitle);
                break;
            case 2:
                mTitle = getString(R.string.title_favorites);
                toolbar.setTitle(mTitle);
                break;
            default:
                break;
        }
    }


    /*private void setupBottomNavStyle() {
        bottomNavigation.setDefaultBackgroundColor(fetchColor(R.color.colorPrimary));
        bottomNavigation.setAccentColor(fetchColor(R.color.colorAccent));
        bottomNavigation.setInactiveColor(fetchColor(R.color.colorIconsInactive));

        bottomNavigation.setColoredModeColors(fetchColor(R.color.colorPrimary),
                fetchColor(R.color.colorAccent));

        bottomNavigation.setColored(false);

        // Hide the navigation when the user scroll the Rv
        //bottomNavigation.setBehaviorTranslationEnabled(true);
        bottomNavigation.setTranslucentNavigationEnabled(true);
    }*/

    /*private void setupViewPager() {
        viewPager.setPagingEnabled(false);
        // Set the offset to 3 so that the Pager Adapter keeps in memory
        // all 3 Fragments and doesn't load it
        viewPager.setOffscreenPageLimit(3);
        mPagerAdapter = new BottomNavAdapter(getSupportFragmentManager());

        mPagerAdapter.addFragments(createArtworksFragment());
        mPagerAdapter.addFragments(createSearchFragment());
        mPagerAdapter.addFragments(createFavFragment());

        viewPager.setAdapter(mPagerAdapter);
    }*/

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
        //getMenuInflater().inflate(R.menu.main_menu, menu);
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

    private void savePrefs(boolean state) {
        mPreferences = getApplicationContext()
                .getSharedPreferences(THEME_PREFERENCE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(THEME_PREFERENCE_KEY, state);
        editor.apply();
    }

    /**
     * Simple method for fetching colors
     * source: https://android.jlelse.eu/ultimate-guide-to-bottom-navigation-on-android-75e4efb8105f
     *
     * @param color to fetch
     * @return int color value
     */
    private int fetchColor(@ColorRes int color) {
        return ContextCompat.getColor(this, color);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return super.onSupportNavigateUp();
    }
}
