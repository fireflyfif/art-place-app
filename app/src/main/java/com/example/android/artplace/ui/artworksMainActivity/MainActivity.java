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
import android.arch.paging.PagedList;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
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

import com.example.android.artplace.ArtPlaceApp;
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

public class MainActivity extends AppCompatActivity implements OnArtworkClickListener, OnRefreshListener, SnackMessageListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String ARTWORK_PARCEL_KEY = "artwork_key";
    private static final String SAVE_STATE_ARTWORK_KEY = "save_state_artwork";

    @BindView(R.id.appbar_main)
    AppBarLayout appBarLayout;
    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.artworks_rv)
    RecyclerView artworksRv;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.error_message)
    TextView errorMessage;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private ArtworkListAdapter mPagedListAdapter;
    private ArtworksViewModel mViewModel;
    private Tracker mTracker;

    private Parcelable recyclerViewState;


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

        // Initialize the ViewModel
        mViewModel = new ArtworksViewModel(ArtPlaceApp.create(this));
        //mViewModel = ViewModelProviders.of(this).get(ArtworksViewModel.class);

        // Setup the RecyclerView
        setRecyclerView();

        // Call submitList() method of the PagedListAdapter when a new page is available
        mViewModel.getArtworkLiveData().observe(this, new Observer<PagedList<Artwork>>() {
            @Override
            public void onChanged(@Nullable PagedList<Artwork> artworks) {
                if (artworks != null) {
                    // When a new page is available, call submitList() method of the PagedListAdapter
                    mPagedListAdapter.submitList(artworks);
                }
            }
        });

        // Call setNetworkState() method of the PagedListAdapter for setting the Network state
        mViewModel.getNetworkState().observe(this, new Observer<NetworkState>() {
            @Override
            public void onChanged(@Nullable NetworkState networkState) {
                mPagedListAdapter.setNetworkState(networkState);
            }
        });

        mViewModel.getInitialLoading().observe(this, new Observer<NetworkState>() {
            @Override
            public void onChanged(@Nullable NetworkState networkState) {
                // When the NetworkStatus is Successful
                // hide both the Progress Bar and the error message
                if (networkState != null &&
                        networkState.getStatus() == NetworkState.Status.SUCCESS) {
                    progressBar.setVisibility(View.INVISIBLE);
                    errorMessage.setVisibility(View.INVISIBLE);
                }
                // When the NetworkStatus is Failed
                // show the error message and hide the Progress Bar
                else if (networkState != null &&
                        networkState.getStatus() == NetworkState.Status.FAILED) {
                    progressBar.setVisibility(View.GONE);
                    // TODO: Hide this message when no connection but some cache results still visible
                    errorMessage.setVisibility(View.VISIBLE);
                    Snackbar.make(coordinatorLayout, "No Network connection. Please try again.",
                            Snackbar.LENGTH_LONG).show();
                }
                // When the NetworkStatus is Running/Loading
                // show the Loading Progress Bar and hide the error message
                else {
                    progressBar.setVisibility(View.VISIBLE);
                    errorMessage.setVisibility(View.GONE);
                }
            }
        });

        artworksRv.setAdapter(mPagedListAdapter);
    }

    private void setRecyclerView() {

        int columnCount = getResources().getInteger(R.integer.list_column_count);

        StaggeredGridLayoutManager staggeredGridLayoutManager =
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);

        artworksRv.setLayoutManager(staggeredGridLayoutManager);

        // Set the PagedListAdapter
        mPagedListAdapter = new ArtworkListAdapter(getApplicationContext(), this, this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        recyclerViewState = artworksRv.getLayoutManager().onSaveInstanceState();

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        artworksRv.getLayoutManager().onRestoreInstanceState(recyclerViewState);
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
        new RetrieveNetworkConnectivity(this, this).execute();

    }

    public synchronized void refreshArtworks() {

        // Setup the RecyclerView
        setRecyclerView();

        mViewModel.refreshArtworkLiveData().observe(this, new Observer<PagedList<Artwork>>() {
            @Override
            public void onChanged(@Nullable PagedList<Artwork> artworks) {
                if (artworks != null) {
                    mPagedListAdapter.submitList(null);
                    // When a new page is available, call submitList() method of the PagedListAdapter
                    mPagedListAdapter.submitList(artworks);
                }
            }
        });

        // Setup the Adapter on the RecyclerView
        artworksRv.setAdapter(mPagedListAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mTracker.setScreenName("Artworks-Gridview");
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
                refreshArtworks();

                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showSnackMessage(String resultMessage) {
        // Show the AppBar so that the Refresh icon be visible!!
        setAppBarVisible();
        Snackbar.make(coordinatorLayout, resultMessage, Snackbar.LENGTH_LONG).show();
        refreshArtworks();
    }


    /**
     * AsyncTask that is used for requesting a network connection upon a Refresh button click from the user
     *
     * Note: It's not ideal in this case, because it's being destroyed with the Lifecylce of the Activity,
     * but it demonstrate a use case of AsyncTask that is needed for passing the Rubrics fro Capstone Stage 2
     */
    private static class RetrieveNetworkConnectivity extends AsyncTask<String, Void, String> {

        private WeakReference<MainActivity> mContext;
        private SnackMessageListener mListener;

        boolean flag = false;
        private Exception mException;


        private RetrieveNetworkConnectivity(MainActivity context, SnackMessageListener snackMessageListener) {
            mContext = new WeakReference<>(context);
            mListener = snackMessageListener;
        }

        @Override
        protected String doInBackground(String... result) {

            String snackMessage;
            try {
                boolean isConnected = ConnectivityUtils.isConnected();
                if (isConnected) {
                    flag = true;
                    Log.d(TAG, "doInBackground, network=true ");

                    snackMessage = "All good with your Network connection!";

                    return snackMessage;
                } else {
                    flag = false;
                    Log.d(TAG, "doInBackground, network= false");

                    snackMessage = "No Network connection!";

                    return snackMessage;
                }
            } catch (Exception e) {
                this.mException = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (mListener != null) {
                if (flag) {
                    // Show a message to the user there is an internet connection
                    mListener.showSnackMessage(result);

                    Log.d(TAG, "onPostExecute called with connectivity ON");

                } else {
                    // Show a message to the user there is No internet connection
                    mListener.showSnackMessage(result);

                    Log.d(TAG, "onPostExecute called with NO connectivity");
                }
            }
        }
    }

}
