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

package com.example.android.artplace.ui.artworks;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.artplace.R;
import com.example.android.artplace.callbacks.OnArtworkClickListener;
import com.example.android.artplace.callbacks.OnRefreshListener;
import com.example.android.artplace.callbacks.SnackMessageListener;
import com.example.android.artplace.model.artworks.Artwork;
import com.example.android.artplace.ui.ArtworkDetailActivity;
import com.example.android.artplace.ui.artworks.adapter.ArtworkListAdapter;
import com.example.android.artplace.utils.NetworkState;
import com.example.android.artplace.utils.RetrieveNetworkConnectivity;
import com.google.android.gms.analytics.Tracker;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Use the {@link ArtworksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ArtworksFragment extends Fragment implements OnArtworkClickListener, OnRefreshListener, SnackMessageListener {

    private static final String TAG = ArtworksFragment.class.getSimpleName();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
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
    private Tracker mTracker;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public ArtworksFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ArtworksFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ArtworksFragment newInstance(String param1, String param2) {
        ArtworksFragment fragment = new ArtworksFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Add a menu to the current Fragment
        setHasOptionsMenu(true);

        getActivity().setTitle("Artworks");

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_artworks, container, false);

        ButterKnife.bind(this, rootView);

        // Initialize the ViewModel
        mViewModel = ViewModelProviders.of(this).get(ArtworksViewModel.class);

        // Set up the UI
        setupUi();

        return rootView;
    }

    private void setRecyclerView() {

        int columnCount = getResources().getInteger(R.integer.list_column_count);

        StaggeredGridLayoutManager staggeredGridLayoutManager =
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);

        artworksRv.setLayoutManager(staggeredGridLayoutManager);

        // Set the PagedListAdapter
        mPagedListAdapter = new ArtworkListAdapter(getContext(), this, this);
    }

    private void setupUi() {

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
                if (networkState != null) {
                    progressBar.setVisibility(View.VISIBLE);
                    errorMessage.setText(R.string.loading_results_message);
                    errorMessage.setVisibility(View.VISIBLE);
                    // When the NetworkStatus is Successful
                    // hide both the Progress Bar and the error message
                    if (networkState.getStatus() == NetworkState.Status.SUCCESS) {
                        progressBar.setVisibility(View.INVISIBLE);
                        errorMessage.setVisibility(View.INVISIBLE);
                    }
                    // When the NetworkStatus is Failed
                    // show the error message and hide the Progress Bar
                    if (networkState.getStatus() == NetworkState.Status.FAILED) {
                        progressBar.setVisibility(View.GONE);
                        // TODO: Hide this message when no connection but some cache results still visible
                        errorMessage.setText(getString(R.string.error_msg));
                        errorMessage.setVisibility(View.VISIBLE);
                        Snackbar.make(coordinatorLayout, R.string.snackbar_no_network_connection,
                                Snackbar.LENGTH_LONG).show();
                    }
                }
            }
        });

        artworksRv.setAdapter(mPagedListAdapter);
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
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onArtworkClick(Artwork artwork) {

        Bundle bundle = new Bundle();
        bundle.putParcelable(ARTWORK_PARCEL_KEY, artwork);

        Intent intent = new Intent(getContext(), ArtworkDetailActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);

    }

    @Override
    public void onRefreshConnection() {
        Log.d(TAG, "onRefreshConnection is now triggered");

        new RetrieveNetworkConnectivity(this, this).execute();
    }

    @Override
    public void showSnackMessage(String resultMessage) {

        // TODO: Show the AppBar so that the Refresh icon be visible!!
        Snackbar.make(coordinatorLayout, resultMessage, Snackbar.LENGTH_LONG).show();
        refreshArtworks();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.artworks_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_refresh:
                // Refresh Artworks
                refreshArtworks();
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
