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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
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
import android.view.SubMenu;
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

public class SearchFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = SearchFragment.class.getSimpleName();
    private static final String PREFERENCE_SEARCH_NAME = "search_prefs";
    private static final String PREFERENCE_SEARCH_KEY = "search_key";
    private static final String SEARCH_QUERY_SAVE_STATE = "search_state";

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

    private SharedPreferences mSharedPreferences;


    // Required empty public constructor
    public SearchFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Add a menu to the current Fragment
        setHasOptionsMenu(true);

        if (savedInstanceState != null) {
            mQueryWordString = savedInstanceState.getString(SEARCH_QUERY_SAVE_STATE);
        }

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(SEARCH_QUERY_SAVE_STATE, mQueryWordString);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        ButterKnife.bind(this, rootView);

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

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause called" );
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called" );
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    public synchronized void requestNewCall(String queryWord, String typeWord) {

        // Setup the RecyclerView first
        setupRecyclerView();

        mSharedPreferences = getContext().getSharedPreferences(PREFERENCE_SEARCH_NAME, Context.MODE_PRIVATE);
        queryWord = mSharedPreferences.getString(PREFERENCE_SEARCH_KEY, null);

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

    private void saveToSharedPreference(String searchQuery) {
        mSharedPreferences = getActivity().getApplicationContext()
                .getSharedPreferences(PREFERENCE_SEARCH_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(PREFERENCE_SEARCH_KEY, searchQuery);
        // Use apply() instead of commit(), because it is being saved on the background
        editor.apply();
        Log.d(TAG, "Saved into shared prefs");
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
                // Save the search query into SharedPreference
                saveToSharedPreference(query);
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

            case R.id.action_type_article:
                item.setChecked(true);
                // Set the type to article
                mTypeString = "article";

                requestNewCall(mQueryWordString, mTypeString);

                Log.d(TAG, "Type word: " + mTypeString);
                return true;

            case R.id.action_type_artist:
                item.setChecked(true);
                // Set the type to artist
                mTypeString = "artist";

                requestNewCall(mQueryWordString, mTypeString);

                Log.d(TAG, "Type word: " + mTypeString);
                return true;

            case R.id.action_type_artwork:
                item.setChecked(true);
                // Set the type to artwork
                mTypeString = "artwork";

                requestNewCall(mQueryWordString, mTypeString);

                Log.d(TAG, "Type word: " + mTypeString);
                return true;

            case R.id.action_type_gene:

                item.setChecked(true);
                // Set the type to gene
                mTypeString = "gene";

                requestNewCall(mQueryWordString, mTypeString);

                Log.d(TAG, "Type word: " + mTypeString);
                return true;

            case R.id.action_type_show:
                item.setChecked(true);
                // Set the type to show
                mTypeString = "show";

                requestNewCall(mQueryWordString, mTypeString);

                Log.d(TAG, "Type word: " + mTypeString);
                return true;

            case R.id.action_type_none:
                item.setChecked(true);
                requestNewCall(mQueryWordString, null);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equals(PREFERENCE_SEARCH_KEY)) {
            mSharedPreferences = getActivity()
                    .getSharedPreferences(PREFERENCE_SEARCH_NAME, Context.MODE_PRIVATE);

            if (mSharedPreferences.contains(PREFERENCE_SEARCH_KEY)) {
                mQueryWordString = mSharedPreferences.getString(PREFERENCE_SEARCH_KEY, null);

                Log.d(TAG, "onSharedPreferenceChanged: Saved search query: " + mQueryWordString);
            }
        }

    }
}
