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

package dev.iotarho.artplace.app.ui.searchresults;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import dev.iotarho.artplace.app.R;
import dev.iotarho.artplace.app.callbacks.OnRefreshListener;
import dev.iotarho.artplace.app.callbacks.OnResultClickListener;
import dev.iotarho.artplace.app.model.search.Result;
import dev.iotarho.artplace.app.ui.searchdetail.SearchDetailActivity;
import dev.iotarho.artplace.app.ui.searchresults.adapter.SearchListAdapter;
import dev.iotarho.artplace.app.utils.Injection;
import dev.iotarho.artplace.app.utils.NetworkState;
import dev.iotarho.artplace.app.utils.PreferenceUtils;
import dev.iotarho.artplace.app.utils.Utils;

public class SearchFragment extends Fragment implements
        OnResultClickListener,
        SwipeRefreshLayout.OnRefreshListener, OnRefreshListener {

    private static final String TAG = SearchFragment.class.getSimpleName();

    private static final String SEARCH_QUERY_SAVE_STATE = "search_state";
    private static final String SEARCH_TYPE_SAVE_STATE = "search_type";
    private static final String RESULT_PARCEL_KEY = "results_key";

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.search_rv)
    RecyclerView searchResultsRv;
    @BindView(R.id.progress_bar_search)
    ProgressBar progressBar;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;


    private SearchFragmentViewModel mViewModel;
    private SearchListAdapter mSearchAdapter;
    private String mQueryWordString;
    private SearchFragmentViewModelFactory mViewModelFactory;
    private String mSearchType;

    private PreferenceUtils prefUtils;

    // Required empty public constructor
    public SearchFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Add a menu to the current Fragment
        setHasOptionsMenu(true);

        if (savedInstanceState != null) {
            mQueryWordString = savedInstanceState.getString(SEARCH_QUERY_SAVE_STATE);
            mSearchType = savedInstanceState.getString(SEARCH_TYPE_SAVE_STATE);
        }

        prefUtils = PreferenceUtils.getInstance();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(SEARCH_QUERY_SAVE_STATE, mQueryWordString);
        outState.putString(SEARCH_TYPE_SAVE_STATE, mSearchType);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        ButterKnife.bind(this, rootView);

        mQueryWordString = prefUtils.getSearchQuery();
        mViewModelFactory = Injection.provideSearchViewModelFactory(mQueryWordString, mSearchType);

        // Set the UI
        setupUi();

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        swipeRefreshLayout.setOnRefreshListener(this);
    }

    private void setupRecyclerView() {
        int columnCount = getResources().getInteger(R.integer.list_column_count);

        StaggeredGridLayoutManager staggeredGridLayoutManager =
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);

        searchResultsRv.setLayoutManager(staggeredGridLayoutManager);
        mSearchAdapter = new SearchListAdapter(this, this);
        // Set the Adapter on the RecyclerView
        searchResultsRv.setAdapter(mSearchAdapter);
    }

    private void setupUi() {
        // Initialize the ViewModel
        mViewModel = new ViewModelProvider(this, mViewModelFactory).get(SearchFragmentViewModel.class);

        mViewModel.getSearchResultsLiveData().observe(requireActivity(), results -> {
            if (results != null) {
                // Submit the list to the PagedListAdapter
                mSearchAdapter.submitList(results);
            }
        });

        mViewModel.getNetworkState().observe(requireActivity(), networkState -> {
            if (networkState != null) {
                mSearchAdapter.setNetworkState(networkState);
            }
        });

        mViewModel.getInitialLoading().observe(requireActivity(), networkState -> {
            if (networkState != null) {
                progressBar.setVisibility(View.VISIBLE);

                if (networkState.getStatus() == NetworkState.Status.SUCCESS
                        && networkState.getStatus() == NetworkState.Status.NO_RESULT) {
                    Log.d(TAG, "Network Status: " + networkState.getStatus());
                    progressBar.setVisibility(View.GONE);
                    Snackbar.make(coordinatorLayout, "Please search for another word.",
                            Snackbar.LENGTH_LONG).show();
                }

                if (networkState.getStatus() == NetworkState.Status.SUCCESS) {
                    Log.d(TAG, "Network Status: " + networkState.getStatus());
                    progressBar.setVisibility(View.INVISIBLE);
                }

                if (networkState.getStatus() == NetworkState.Status.FAILED) {
                    Log.d(TAG, "Network Status: " + networkState.getStatus());
                    progressBar.setVisibility(View.GONE);
                    Snackbar.make(coordinatorLayout, R.string.snackbar_no_network_connection,
                            Snackbar.LENGTH_LONG).show();
                }
            }
        });

        // Setup the RecyclerView first
        setupRecyclerView();
    }

    private synchronized void requestNewCall(String queryWord, String searchType) {
        // Setup the RecyclerView first
        setupRecyclerView();

        Log.d(TAG, "requestNewCall: queryWord: " + queryWord + " searchType: " + searchType);
        // Initialize the ViewModel
        mViewModel = new ViewModelProvider(this, mViewModelFactory)
                .get(SearchFragmentViewModel.class);

        mViewModel.refreshSearchLiveData(queryWord, searchType)
                .observe(this, results -> mSearchAdapter.submitList(results));
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.action_search).setVisible(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.search_menu, menu);

        // TODO: Generate a method for getting the query word from shared preferences
        mQueryWordString = prefUtils.getSearchQuery();
        Log.d(TAG, "onCreateOptionsMenu: Query word " + mQueryWordString);

        // Make the icon with a dynamic tint
        // source: https://stackoverflow.com/a/29916353/8132331
        Drawable drawable = menu.findItem(R.id.action_search).getIcon();
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(requireActivity(), R.color.color_on_surface));
        menu.findItem(R.id.action_search).setIcon(drawable);

        // Set the SearchView
        SearchManager searchManager = (SearchManager) requireActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        if (searchView == null || searchManager == null) {
            return;
        }

        if (!Utils.isNullOrEmpty(mQueryWordString)) {
            searchView.onActionViewExpanded();
            searchView.setQuery(mQueryWordString, true);
            searchView.clearFocus();
        }

        searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().getComponentName()));
        searchView.setSubmitButtonEnabled(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Save the search query into SharedPreference
                prefUtils.saveSearchQuery(query);
                requestNewCall(query, mSearchType);
                Log.d(TAG, "onQueryTextSubmit, query: " + query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "onQueryTextChange, newText: " + newText);
                if (newText.length() > 4) {
                    requestNewCall(newText, mSearchType);
                }

                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_search:
                return true;

            /*case R.id.action_type_article:
                item.setChecked(true);
                // Set the type to article
                mSearchType = "article";

                requestNewCall(mQueryWordString, mSearchType);

                Log.d(TAG, "Type word: " + mSearchType);
                return true;*/

            case R.id.action_type_artist:
                item.setChecked(true);
                // Set the type to artist
                mSearchType = "artist";

                requestNewCall(mQueryWordString, mSearchType);

                Log.d(TAG, "Type word: " + mSearchType);
                return true;

            case R.id.action_type_artwork:
                item.setChecked(true);
                // Set the type to artwork
                mSearchType = "artwork";

                requestNewCall(mQueryWordString, mSearchType);

                Log.d(TAG, "Type word: " + mSearchType);
                return true;

            case R.id.action_type_gene:

                item.setChecked(true);
                // Set the type to gene
                mSearchType = "gene";

                requestNewCall(mQueryWordString, mSearchType);

                Log.d(TAG, "Type word: " + mSearchType);
                return true;

            case R.id.action_type_show:
                item.setChecked(true);
                // Set the type to show
                mSearchType = "show";

                requestNewCall(mQueryWordString, mSearchType);

                Log.d(TAG, "Type word: " + mSearchType);
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
    public void onResultClick(Result result) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(RESULT_PARCEL_KEY, result);

        Intent intent = new Intent(getActivity(), SearchDetailActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        requestNewCall(prefUtils.getSearchQuery(), mSearchType);
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onRefreshConnection() {
        // TODO: implement how to behave on refresh
//        new RetrieveNetworkConnectivity(this, this).execute();
    }
}
