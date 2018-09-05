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

package com.example.android.artplace.ui.searchresults;

import android.app.SearchManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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
import android.support.v7.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.artplace.ArtPlaceApp;
import com.example.android.artplace.R;
import com.example.android.artplace.model.search.Result;
import com.example.android.artplace.ui.searchresults.adapter.SearchListAdapter;
import com.example.android.artplace.utils.NetworkState;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchFragment extends Fragment {

    private static final String TAG = SearchFragment.class.getSimpleName();

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.search_rv)
    RecyclerView searchResultsRv;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.error_message)
    TextView errorMessage;

    private SearchFragmentViewModel mViewModel;
    private SearchListAdapter mSearchAdapter;
    private SearchView mSearchView;
    private String mQueryWordString;
    private SearchFragmentViewModelFactory mViewModelFactory;
    private String mTypeString;

    private boolean isTypeArtist = false;
    private boolean isTypeArtwork = false;
    private boolean isTypeGene = false;
    private boolean isTypeShow = false;

    // Required empty public constructor
    public SearchFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        ButterKnife.bind(this, rootView);

        // Add a menu to the current Fragment
        setHasOptionsMenu(true);
        // Set the UI
        setupUi();

        return rootView;
    }

    private void setupRecyclerView() {
        StaggeredGridLayoutManager staggeredGridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        searchResultsRv.setLayoutManager(staggeredGridLayoutManager);

        mSearchAdapter = new SearchListAdapter(getContext());
    }

    private void setupUi() {
        // Setup the RecyclerView first
        setupRecyclerView();

        if (mQueryWordString == null) {
            mQueryWordString = "Andy Warhol";
        }

        Log.d(TAG, "setupUi: Query word: " + mQueryWordString);
        Log.d(TAG, "setupUi: Type word: " + mTypeString);

        mViewModelFactory = new SearchFragmentViewModelFactory(ArtPlaceApp.getInstance(), mQueryWordString, mTypeString);

        // Initialize the ViewModel
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(SearchFragmentViewModel.class);

        mViewModel.getSearchResultsLiveData().observe(this, new Observer<PagedList<Result>>() {

            @Override
            public void onChanged(@Nullable PagedList<Result> results) {

                if (results != null) {
                    Log.d(TAG, "Size of the result: " + results.size());
                    // Submit the list to the PagedListAdapter
                    mSearchAdapter.submitList(results);
                } else {
                    // TODO: Show message for no data found
                    errorMessage.setText("No data found for the current word.");
                    errorMessage.setVisibility(View.VISIBLE);
                }
            }
        });

        mViewModel.getNetworkState().observe(this, new Observer<NetworkState>() {
            @Override
            public void onChanged(@Nullable NetworkState networkState) {
                mSearchAdapter.setNetworkState(networkState);
            }
        });

        mViewModel.getInitialLoading().observe(this, new Observer<NetworkState>() {
            @Override
            public void onChanged(@Nullable NetworkState networkState) {
                if (networkState != null && networkState.getStatus() == NetworkState.Status.SUCCESS) {
                    progressBar.setVisibility(View.INVISIBLE);
                    errorMessage.setVisibility(View.INVISIBLE);
                } else if (networkState != null && networkState.getStatus() == NetworkState.Status.FAILED) {
                    progressBar.setVisibility(View.GONE);
                    // TODO: Hide this message when no connection but some cache results still visible
                    errorMessage.setVisibility(View.VISIBLE);
                    Snackbar.make(coordinatorLayout, R.string.snackbar_no_network_connection,
                            Snackbar.LENGTH_LONG).show();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    errorMessage.setVisibility(View.GONE);
                }
            }
        });

        // Set the Adapter on the RecyclerView
        searchResultsRv.setAdapter(mSearchAdapter);

    }

    public synchronized void requestNewCall(String queryWord, String typeWord) {

        // Setup the RecyclerView first
        setupRecyclerView();

        if (queryWord == null) {
            queryWord = "Andy Warhol";
        }

        Log.d(TAG, "requestNewCall: Query word: " + queryWord);
        Log.d(TAG, "requestNewCall: Type word: " + typeWord);

        mViewModelFactory = new SearchFragmentViewModelFactory(ArtPlaceApp.getInstance(), queryWord, typeWord);

        // Initialize the ViewModel
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(SearchFragmentViewModel.class);

        mViewModel.refreshSearchLiveData(ArtPlaceApp.getInstance(), queryWord, typeWord).observe(this, new Observer<PagedList<Result>>() {

            @Override
            public void onChanged(@Nullable PagedList<Result> results) {
                if (results != null) {
                    Log.d(TAG, "Size of the result: " + results.size());
                    // Submit the list to the PagedListAdapter
                    mSearchAdapter.submitList(results);
                } else {
                    // TODO: Show message for no data found
                    errorMessage.setText("No data found for the current word.");
                    errorMessage.setVisibility(View.VISIBLE);
                }
            }
        });

        // Set the Adapter on the RecyclerView
        searchResultsRv.setAdapter(mSearchAdapter);
    }

    private void updateType() {
        if (isTypeArtist) {
            mTypeString += "artist";
        } else if (isTypeArtwork) {
            mTypeString += "artwork";
        } else if (isTypeGene) {
            mTypeString += "gene";
        } else if (isTypeShow) {
            mTypeString += "show";
        } else {
            mTypeString = "";
        }
        Log.d(TAG, "updateType: Type word: " + mTypeString);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        Log.d(TAG, "onPrepareOptionsMenu called");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.search_menu, menu);

        // Set the SearchView
        SearchManager searchManager =
                (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        // Set the Submit Button
        mSearchView.setSubmitButtonEnabled(false);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                query = String.valueOf(mSearchView.getQuery());

                requestNewCall(query, mTypeString);
                Log.d(TAG, "SearchFragment: onQueryTextSubmit called, query word: " + query);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "SearchFragment: onQueryTextChange called");

                if (newText.length() > 0) {

                    requestNewCall(newText, mTypeString);
                }

                //Toast.makeText(getContext(), "Search art word here", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_search:
                return true;
            case R.id.action_type_artist:
                if (item.isChecked()) {
                    item.setChecked(false);
                    isTypeArtist = false;
                    // Set the type to artist, nothing happens here

                } else {
                    item.setChecked(true);
                    isTypeArtist = true;
                }

                updateType();
                requestNewCall(mQueryWordString, mTypeString);
                return true;
            case R.id.action_type_artwork:
                if (item.isChecked()) {
                    // Set the type to artwork
                    item.setChecked(false);
                    isTypeArtwork = false;

                    mTypeString = String.valueOf(item.getTitle());
                } else {
                    item.setChecked(true);
                    isTypeArtwork = true;
                }

                updateType();
                requestNewCall(mQueryWordString, mTypeString);
                return true;
            case R.id.action_type_profile:
                if (item.isChecked()) {
                    // Set the type to profile
                    item.setChecked(false);

                } else {
                    item.setChecked(true);
                }

                updateType();
                requestNewCall(mQueryWordString, mTypeString);
                return true;
            case R.id.action_type_gene:
                if (item.isChecked()) {
                    // Set the type to gene
                    item.setChecked(false);
                    isTypeGene = false;
                    mTypeString = String.valueOf(item.getTitle());

                    Log.d(TAG, "Selected menu isTypeGene: " + isTypeGene);
                } else {
                    item.setChecked(true);
                    isTypeGene = true;
                }

                updateType();
                requestNewCall(mQueryWordString, mTypeString);
                return true;
            case R.id.action_type_show:
                if (item.isChecked()) {
                    // Set the type to show
                    item.setChecked(false);
                    isTypeShow = false;
                    mTypeString = String.valueOf(item.getTitle());
                } else {
                    item.setChecked(true);
                    isTypeShow = true;
                }

                updateType();
                requestNewCall(mQueryWordString, mTypeString);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }
}
