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

package dev.iotarho.artplace.app.ui.artworks;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.snackbar.Snackbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import dev.iotarho.artplace.app.R;
import dev.iotarho.artplace.app.callbacks.OnArtworkClickListener;
import dev.iotarho.artplace.app.callbacks.OnRefreshListener;
import dev.iotarho.artplace.app.callbacks.SnackMessageListener;
import dev.iotarho.artplace.app.model.artworks.Artwork;
import dev.iotarho.artplace.app.ui.artworkdetail.ArtworkDetailActivity;
import dev.iotarho.artplace.app.ui.artworks.adapter.ArtworkListAdapter;
import dev.iotarho.artplace.app.utils.Injection;
import dev.iotarho.artplace.app.utils.NetworkState;
import dev.iotarho.artplace.app.utils.RetrieveNetworkConnectivity;
import dev.iotarho.artplace.app.utils.TokenManager;


public class ArtworksFragment extends Fragment implements OnArtworkClickListener, OnRefreshListener,
        SnackMessageListener {

    private static final String TAG = ArtworksFragment.class.getSimpleName();
    private static final String ARG_ARTWORKS_TITLE = "artworks_title";
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
    private ArtworksFragmentViewModelFactory mFactory;

    private TokenManager mTokenManager;
    private SharedPreferences mPreferences;

    private PagedList<Artwork> mArtworksList;

    private SearchView mSearchView;


    public ArtworksFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the TokenManager
        mTokenManager = TokenManager.getInstance(getActivity());

        // Add a menu to the current Fragment
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_artworks, container, false);

        ButterKnife.bind(this, rootView);

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

        // Initialize the ViewModelFactory
        mFactory = Injection.provideArtworksViewModelFactory(mTokenManager);

        // Initialize the ViewModel
        mViewModel = ViewModelProviders.of(this, mFactory).get(ArtworksViewModel.class);

        // Call submitList() method of the PagedListAdapter when a new page is available
        mViewModel.getArtworkLiveData().observe(this, new Observer<PagedList<Artwork>>() {
            @Override
            public void onChanged(@Nullable PagedList<Artwork> artworks) {
                if (artworks != null) {
                    // When a new page is available, call submitList() method of the PagedListAdapter
                    mPagedListAdapter.submitList(artworks);
                    mPagedListAdapter.swapCatalogue(artworks);

                    mArtworksList = artworks;
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

        // Initialize the ViewModelFactory
        mFactory = Injection.provideArtworksViewModelFactory(mTokenManager);

        // Initialize the ViewModel
        mViewModel = ViewModelProviders.of(this, mFactory).get(ArtworksViewModel.class);

        mViewModel.refreshArtworkLiveData().observe(this, artworks -> {
            if (artworks != null) {
                mPagedListAdapter.submitList(null);
                // When a new page is available, call submitList() method of the PagedListAdapter
                mPagedListAdapter.submitList(artworks);
                mArtworksList = artworks;
            }
        });

        // Setup the Adapter on the RecyclerView
        artworksRv.setAdapter(mPagedListAdapter);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Log.d(TAG, "ArtworksFragment: onAttach called");
    }

    @Override
    public void onDetach() {
        super.onDetach();

        Log.d(TAG, "ArtworksFragment: onDetach called");
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "ArtworksFragment: onResume called");
    }

    @Override
    public void onArtworkClick(Artwork artwork, int position) {

        Bundle bundle = new Bundle();
        bundle.putParcelable(ARTWORK_PARCEL_KEY, artwork);
        Log.d(TAG, "Get current list: " + getAdapter().getCurrentList().size());

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

    private ArtworkListAdapter getAdapter() {
        return mPagedListAdapter;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.artworks_menu, menu);
        // Make the icon with a dynamic tint
        // source: https://stackoverflow.com/a/29916353/8132331
        Drawable drawable = menu.findItem(R.id.action_refresh).getIcon();
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(getActivity(), R.color.colorText));
        menu.findItem(R.id.action_refresh).setIcon(drawable);

        // Set the Search View
        SearchManager searchManager =
                (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        mSearchView.setSubmitButtonEnabled(false);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO:
                //query = String.valueOf(mSearchView.getQuery());
                if (getAdapter() != null) {
                    getAdapter().getFilter().filter(query);
                    mPagedListAdapter.swapCatalogue(mArtworksList);

                    Log.d(TAG, "Current query: " + query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (getAdapter() != null) {
                    getAdapter().getFilter().filter(newText);
                    Log.d(TAG, "Current query: " + newText);
                }
                return false;
            }
        });
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
