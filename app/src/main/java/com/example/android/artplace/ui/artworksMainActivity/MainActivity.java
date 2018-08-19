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

package com.example.android.artplace.ui.artworksMainActivity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.example.android.artplace.ArtPlaceApp;
import com.example.android.artplace.ArtworksFragment;
import com.example.android.artplace.R;
import com.example.android.artplace.callbacks.SnackMessageListener;
import com.example.android.artplace.ui.ArtworkDetailActivity;
import com.example.android.artplace.ui.artworksMainActivity.adapter.ArtworkListAdapter;
import com.example.android.artplace.model.Artworks.Artwork;
import com.example.android.artplace.callbacks.OnArtworkClickListener;
import com.example.android.artplace.callbacks.OnRefreshListener;
import com.example.android.artplace.ui.favoriteArtworks.FavArtworksActivity;
import com.example.android.artplace.utils.ConnectivityUtils;
import com.example.android.artplace.utils.NetworkState;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements OnArtworkClickListener, OnRefreshListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String ARTWORK_PARCEL_KEY = "artwork_key";

    @BindView(R.id.appbar_main)
    AppBarLayout appBarLayout;
    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.bottom_navigation)
    AHBottomNavigation bottomNavigation;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private Tracker mTracker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        // Obtain the shared Tracker instance.
        // source: https://developers.google.com/analytics/devguides/collection/android/v4/
        ArtPlaceApp application = (ArtPlaceApp) getApplication();
        mTracker = application.getDefaultTracker();

        // Add items to the Bottom Navigation
        addBottomNavigationItems();
        // Setting the very 1st item as home screen.
        bottomNavigation.setCurrentItem(0);

        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {


                return false;
            }
        });
    }

    private void addBottomNavigationItems() {

        AHBottomNavigationItem artworksItem = new AHBottomNavigationItem("Artworks", R.drawable.ic_launch_24dp);
        AHBottomNavigationItem artistsItem = new AHBottomNavigationItem("Artists", R.drawable.ic_clear_24dp);
        AHBottomNavigationItem favoritesItem = new AHBottomNavigationItem("Favorites", R.drawable.ic_favorite_24dp);

        bottomNavigation.addItem(artworksItem);
        bottomNavigation.addItem(artistsItem);
        bottomNavigation.addItem(favoritesItem);
    }

    private ArtworksFragment createFragment() {
        ArtworksFragment artworksFragment = new ArtworksFragment();
        //artworksFragment.setArguments();
        return artworksFragment;
    }

    private void setupBottomNavStyle() {
        bottomNavigation.setDefaultBackgroundColor(fetchColor(R.color.colorPrimary));
        bottomNavigation.setAccentColor(fetchColor(R.color.colorAccent));
        bottomNavigation.setInactiveColor(fetchColor(R.color.colorBottomNavigationInactive));

        bottomNavigation.setColoredModeColors(Color.WHITE,
                fetchColor(R.color.colorBottomNavigationInactiveColored));

        bottomNavigation.setColored(true);

        // Hide the navigation when the user scroll the Rv
        bottomNavigation.setBehaviorTranslationEnabled(true);
    }


    @Override
    public void onArtworkClick(Artwork artwork) {

        Bundle bundle = new Bundle();
        bundle.putParcelable(ARTWORK_PARCEL_KEY, artwork);

        Intent intent = new Intent(MainActivity.this, ArtworkDetailActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onRefreshConnection() {
        Log.d(TAG, "onRefreshConnection is now triggered");
        setAppBarVisible();
        //new RetrieveNetworkConnectivity(this, this).execute();
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume is called now!");

        /*if (recyclerViewState != null) {
            artworksRv.getLayoutManager().onRestoreInstanceState(recyclerViewState);
        }*/

        mTracker.setScreenName(getString(R.string.analytics_artwork_screenname));
        // Send initial screen screen view hit.
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        // Show the AppBarLayout every time after resume
        setAppBarVisible();

        // Refresh list if there is an internet connection
        //refreshArtworks();
    }

    private void setAppBarVisible() {
        // Set the AppBarLayout to expanded
        appBarLayout.setExpanded(true, true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:

                return false;

            case R.id.action_favorites:
                Intent intent = new Intent(MainActivity.this, FavArtworksActivity.class);
                // TODO: Put as an extra key from this parent activity so that the next activity knows where to go back!!!
                startActivity(intent);
                return true;

            case R.id.action_refresh:
                // Refresh the list if there is connectivity
                //refreshArtworks();

                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
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
