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

package com.example.android.artplace.ui.ArtworksMainActivity;

import android.arch.lifecycle.Observer;
import android.arch.paging.PagedList;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.artplace.ArtPlaceApp;
import com.example.android.artplace.R;
import com.example.android.artplace.ui.ArtworkDetailActivity;
import com.example.android.artplace.ui.ArtworksMainActivity.adapters.ArtworkListAdapter;
import com.example.android.artplace.model.remote.Artworks.Artwork;
import com.example.android.artplace.callbacks.OnArtworkClickListener;
import com.example.android.artplace.callbacks.OnRefreshListener;
import com.example.android.artplace.utils.ConnectivityUtils;
import com.example.android.artplace.utils.NetworkState;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements OnArtworkClickListener, OnRefreshListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String ARTWORK_PARCEL_KEY = "artwork_key";

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.artworks_rv)
    RecyclerView artworksRv;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.error_message)
    TextView errorMessage;

    private ArtworkListAdapter mPagedListAdapter;
    private ArtworksViewModel mViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

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
                    mPagedListAdapter.submitList(artworks); // the paged list of artworks is 0, but it's working
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
    public void onArtworkClick(Artwork artwork) {
        Toast.makeText(this, "Clicked artwork with id " + artwork.getId(), Toast.LENGTH_SHORT).show();

        Bundle bundle = new Bundle();
        bundle.putParcelable(ARTWORK_PARCEL_KEY, artwork);

        Intent intent = new Intent(MainActivity.this, ArtworkDetailActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onRefreshConnection() {

        new RetrieveNetworkConnectivity().execute();

    }

    private synchronized void refreshArtworks() {

        // Setup the RecyclerView
        setRecyclerView();

        // Call submitList() method of the PagedListAdapter when a new page is available
        mViewModel.getArtworkLiveData().observe(this, new Observer<PagedList<Artwork>>() {
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


    /**
     * AsyncTask that is used for requesting a network connection upon a Refresh button click from the user
     *
     * Note: It's not ideal in this case, because it's being destroyed with the Lifecylce of the Activity,
     * but it demonstrate a use case of AsyncTask that is needed for passing the Rubrics fro Capstone Stage 2
     */
    private class RetrieveNetworkConnectivity extends AsyncTask<String, Void, Boolean> {

        //private WeakReference<MainActivity> mAppReference;
        //private CoordinatorLayout mCoordinatorLayout;

        private Exception mException;

        public RetrieveNetworkConnectivity() {
            //mAppReference = new WeakReference<>(context);
            //mCoordinatorLayout = coordinatorLayout;
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            try {
                boolean isConnected = ConnectivityUtils.isConnected();
                if (isConnected) {

                    Snackbar.make(coordinatorLayout, "All good with your Network connection!",
                            Snackbar.LENGTH_LONG).show();

                    // Refresh the list of artworks here
                    // TODO: Problem: get's only the initial Load
                    refreshArtworks();

                    return true;
                } else {
                    Snackbar.make(coordinatorLayout, "No Network connection!",
                            Snackbar.LENGTH_LONG).show();

                    return false;
                }
            } catch (Exception e) {
                this.mException = e;
                return null;
            }

        }
    }
}
