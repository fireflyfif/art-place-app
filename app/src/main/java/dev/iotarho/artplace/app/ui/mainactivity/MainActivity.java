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

import android.app.SearchManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;

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
import dev.iotarho.artplace.app.utils.PreferenceUtils;
import dev.iotarho.artplace.app.utils.ThemeUtils;

public class MainActivity extends AppCompatActivity implements OnRefreshListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String POSITION_KEY = "position";
    public static final String SEARCH_WORD_EXTRA = "search_word_from_intent";

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
    private String mTitle;
    private int mPosition;
    private String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.color_on_background));
        }
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        PreferenceUtils preferenceUtils = PreferenceUtils.getInstance();
        ThemeUtils.applyTheme(preferenceUtils.getThemeFromPrefs());

        if (savedInstanceState != null) {
            // Get the position of the selected Fragment
            mPosition = savedInstanceState.getInt(POSITION_KEY);
        }

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
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the position of the selected Fragment
        outState.putInt(POSITION_KEY, mPosition);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent); // intent saved by the activity is updated in case you call getIntent() in the future
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            Log.d(TAG, "handleIntent, queryString: " + query);
            SearchFragment.newInstanceWithExtra(query);
        }
    }

    @Override
    public boolean onSearchRequested() {
        Bundle appData = new Bundle();
        appData.putString("search_word_from_intent", "query");
        Log.d(TAG, "onSearchRequested called");
        startSearch(null, false, appData, false);
        return true;
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
//                toolbar.inflateMenu(R.menu.search_menu);
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
                getString(R.string.title_artworks), R.drawable.ic_red_dot);
        AHBottomNavigationItem artistsItem = new AHBottomNavigationItem(
                getString(R.string.search), R.drawable.ic_red_dot);
        AHBottomNavigationItem favoritesItem = new AHBottomNavigationItem(
                getString(R.string.title_favorites), R.drawable.ic_red_dot);

        bottomNavigation.addItem(artworksItem);
        bottomNavigation.addItem(artistsItem);
        bottomNavigation.addItem(favoritesItem);
    }

    private void setupBottomNavStyle() {
        // Set default background color for AHBottomNavigation
        bottomNavigation.setDefaultBackgroundColor(fetchColor(R.color.color_on_surface));

        // Change colors for AHBottomNavigation
        bottomNavigation.setAccentColor(fetchColor(R.color.color_primary));
        bottomNavigation.setInactiveColor(fetchColor(R.color.color_secondary));

        // Manage titles for AHBottomNavigation
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.SHOW_WHEN_ACTIVE);

        // Use colored navigation with circle reveal effect
        bottomNavigation.setColored(false);

        // Hide the navigation when the user scroll the Rv
        bottomNavigation.setBehaviorTranslationEnabled(true);
    }

    private void setupViewPager() {
        viewPager.setPagingEnabled(false);
        // Set the offset to 3 so that the Pager Adapter keeps in memory
        // all 3 Fragments and doesn't load it
        viewPager.setOffscreenPageLimit(3);
        mPagerAdapter = new BottomNavAdapter(getSupportFragmentManager());

        mPagerAdapter.addFragments(ArtworksFragment.newInstance());
        mPagerAdapter.addFragments(SearchFragment.newInstance());
        mPagerAdapter.addFragments(FavoritesFragment.newInstance());

        viewPager.setAdapter(mPagerAdapter);
    }

    @Override
    public void onRefreshConnection() {
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
