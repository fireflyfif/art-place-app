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

package dev.iotarho.artplace.app.ui.mainactivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.google.android.material.appbar.AppBarLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import dev.iotarho.artplace.app.R;
import dev.iotarho.artplace.app.callbacks.OnRefreshListener;
import dev.iotarho.artplace.app.ui.artworks.ArtworksFragment;
import dev.iotarho.artplace.app.ui.favorites.FavoritesFragment;
import dev.iotarho.artplace.app.ui.mainactivity.adapters.BottomNavAdapter;
import dev.iotarho.artplace.app.ui.mainactivity.adapters.MainViewPager;
import dev.iotarho.artplace.app.ui.searchresults.SearchFragment;

public class MainActivity extends AppCompatActivity implements OnRefreshListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String THEME_PREFERENCE_KEY = "theme_prefs";
    private static final String POSITION_KEY = "position";

    @BindView(R.id.appbar_main)
    AppBarLayout appBarLayout;
    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.bottom_navigation)
    AHBottomNavigation bottomNavigation;
    @BindView(R.id.view_pager_content)
    MainViewPager viewPager;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private BottomNavAdapter mPagerAdapter;
    private SharedPreferences mPreferences;
    private boolean mIsDayMode;
    private String mTitle;
    private int mPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        setupViewPager();
        setupBottomNavStyle();

        // Add items to the Bottom Navigation
        addBottomNavigationItems();
        bottomNavigation.setCurrentItem(mPosition);

        bottomNavigation.setOnTabSelectedListener((position, wasSelected) -> {

            bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
            viewPager.setCurrentItem(position);

            mPosition = position;
            // Set the title on the toolbar according to
            // the position of the clicked Fragment
            setToolbarTitle(position);

            return true;
        });

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mIsDayMode = mPreferences.getBoolean(THEME_PREFERENCE_KEY, false);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        int currentNightMode = newConfig.uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                // Night mode is not active, we're using the light theme
                Log.d(TAG, "mode is light");
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                // Night mode is active, we're using dark theme
                Log.d(TAG, "mode is dark");
                break;
        }
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

    private void addBottomNavigationItems() {

        AHBottomNavigationItem artworksItem = new AHBottomNavigationItem(
                getString(R.string.title_artworks), R.drawable.ic_frame);
        AHBottomNavigationItem artistsItem = new AHBottomNavigationItem(
                getString(R.string.search), R.drawable.ic_search_24dp);
        AHBottomNavigationItem favoritesItem = new AHBottomNavigationItem(
                getString(R.string.title_favorites), R.drawable.ic_favorite_24dp);

        bottomNavigation.addItem(artworksItem);
        bottomNavigation.addItem(artistsItem);
        bottomNavigation.addItem(favoritesItem);
    }

    //TODO: Make these three methods into one
    private Fragment createArtworksFragment() {
        Fragment artworksFragment = new ArtworksFragment();
        // Set arguments here
        Bundle bundle = new Bundle();
        artworksFragment.setArguments(bundle);
        return artworksFragment;
    }

    private FavoritesFragment createFavFragment() {
        return new FavoritesFragment();
    }

    private SearchFragment createSearchFragment() {
        return new SearchFragment();
    }

    private void setupBottomNavStyle() {
        bottomNavigation.setDefaultBackgroundColor(fetchColor(R.color.colorPrimary));
        bottomNavigation.setAccentColor(fetchColor(R.color.colorAccent));
        bottomNavigation.setInactiveColor(fetchColor(R.color.colorIconsInactive));

        bottomNavigation.setColoredModeColors(fetchColor(R.color.colorPrimary),
                fetchColor(R.color.colorAccent));

        bottomNavigation.setColored(false);

        // Hide the navigation when the user scroll the Rv
        //bottomNavigation.setBehaviorTranslationEnabled(true);
        bottomNavigation.setTranslucentNavigationEnabled(true);
    }

    private void setupViewPager() {
        viewPager.setPagingEnabled(false);
        // Set the offset to 3 so that the Pager Adapter keeps in memory
        // all 3 Fragments and doesn't load it
        viewPager.setOffscreenPageLimit(3);
        mPagerAdapter = new BottomNavAdapter(getSupportFragmentManager());

        mPagerAdapter.addFragments(createArtworksFragment());
        mPagerAdapter.addFragments(createSearchFragment());
        mPagerAdapter.addFragments(createFavFragment());

        viewPager.setAdapter(mPagerAdapter);
    }

    @Override
    public void onRefreshConnection() {
        Log.d(TAG, "onRefreshConnection is now triggered");
        setAppBarVisible();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Show the AppBarLayout every time after resume
        setAppBarVisible();
    }

    private void setAppBarVisible() {
        // Set the AppBarLayout to expanded
        appBarLayout.setExpanded(true, true);
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
}
