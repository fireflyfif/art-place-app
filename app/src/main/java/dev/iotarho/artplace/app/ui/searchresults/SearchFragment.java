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
import dev.iotarho.artplace.app.callbacks.SnackMessageListener;
import dev.iotarho.artplace.app.model.search.Result;
import dev.iotarho.artplace.app.ui.searchdetail.SearchDetailActivity;
import dev.iotarho.artplace.app.ui.searchresults.adapter.SearchListAdapter;
import dev.iotarho.artplace.app.utils.Injection;
import dev.iotarho.artplace.app.utils.NetworkState;
import dev.iotarho.artplace.app.utils.PreferenceUtils;
import dev.iotarho.artplace.app.utils.RetrieveNetworkConnectivity;
import dev.iotarho.artplace.app.utils.Utils;

import static dev.iotarho.artplace.app.ui.mainactivity.MainActivity.SEARCH_WORD_EXTRA;

public class SearchFragment extends Fragment implements
        OnResultClickListener,
        SnackMessageListener,
        SwipeRefreshLayout.OnRefreshListener,
        OnRefreshListener {

    private static final String TAG = SearchFragment.class.getSimpleName();

    private static final String SEARCH_QUERY_SAVE_STATE = "search_state";
    private static final String SEARCH_TYPE_SAVE_STATE = "search_type";
    private static final String ITEM_CHECKED_SAVE_STATE = "search_type";
    private static final String RESULT_PARCEL_KEY = "results_key";

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.search_rv)
    RecyclerView searchResultsRv;
    @BindView(R.id.progress_bar_search)
    ProgressBar progressBar;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    private SearchFragmentViewModelFactory searchFragmentViewModelFactory;
    private SearchFragmentViewModel searchFragmentViewModel;
    private SearchListAdapter mSearchAdapter;
    private String queryString;
    private String searchTypeString;
    private boolean isMenuItemChecked;

    private PreferenceUtils prefUtils;

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    public static SearchFragment newInstanceWithExtra(String query) {
        SearchFragment searchFragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(SEARCH_WORD_EXTRA, query);
        searchFragment.setArguments(args);
        return new SearchFragment();
    }

    // Required empty public constructor
    public SearchFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Add a menu to the current Fragment
        setHasOptionsMenu(true);

        if (savedInstanceState != null) {
            queryString = savedInstanceState.getString(SEARCH_QUERY_SAVE_STATE);
            searchTypeString = savedInstanceState.getString(SEARCH_TYPE_SAVE_STATE);
            isMenuItemChecked = savedInstanceState.getBoolean(ITEM_CHECKED_SAVE_STATE);
        }

        prefUtils = PreferenceUtils.getInstance();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(SEARCH_QUERY_SAVE_STATE, queryString);
        outState.putString(SEARCH_TYPE_SAVE_STATE, searchTypeString);
        outState.putBoolean(ITEM_CHECKED_SAVE_STATE, isMenuItemChecked);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        ButterKnife.bind(this, rootView);

        queryString = prefUtils.getSearchQuery();
        Log.d(TAG, "queryString: " + queryString);
        searchFragmentViewModelFactory = Injection.provideSearchViewModelFactory();

        // Initialize the ViewModel
        searchFragmentViewModel = new ViewModelProvider(getViewModelStore(), searchFragmentViewModelFactory).get(SearchFragmentViewModel.class);

        // Get the search word from the intent
        Bundle appData = getArguments(); //getIntent().getBundleExtra(SearchManager.APP_DATA);
        if (appData != null) {
            queryString = appData.getString(SEARCH_WORD_EXTRA);
            Log.d(TAG, "onCreateView, queryString: " + queryString);
        }

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        swipeRefreshLayout.setOnRefreshListener(this);

        setupUi(); // Set the UI onViewCreated to ensure that the view is created
    }

    private void setupUi() {
        // TODO: Should I set the value of the query here too?
        searchFragmentViewModel.getQueryLiveData().observe(getViewLifecycleOwner(), query -> {
            Log.d(TAG, "temp, query is = " + query);
        });
        searchFragmentViewModel.getPagedList().observe(getViewLifecycleOwner(), results -> {
            Log.d(TAG, "temp, getPagedList() is = " + results.size());
            mSearchAdapter.submitList(results); // submit the list to the PagedListAdapter
            observeNetworkState();
            observeLoadingState();
        });

        // Setup the RecyclerView first
        setupRecyclerView();
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

    private void observeLoadingState() {
        searchFragmentViewModel.getInitialLoading().observe(getViewLifecycleOwner(), networkState -> {
            if (networkState != null) {
                progressBar.setVisibility(View.VISIBLE);

                if (networkState.getStatus() == NetworkState.Status.SUCCESS
                        && networkState.getStatus() == NetworkState.Status.NO_RESULT) {
                    Log.d(TAG, "Network Status: " + networkState.getStatus());
                    progressBar.setVisibility(View.INVISIBLE);
                    Snackbar.make(coordinatorLayout, "Please search for another word.",
                            Snackbar.LENGTH_LONG).show();
                }

                if (networkState.getStatus() == NetworkState.Status.SUCCESS) {
                    Log.d(TAG, "Network Status: " + networkState.getStatus());
                    progressBar.setVisibility(View.INVISIBLE);
                }

                if (networkState.getStatus() == NetworkState.Status.FAILED) {
                    Log.d(TAG, "Network Status: " + networkState.getStatus());
                    progressBar.setVisibility(View.INVISIBLE);
                    Snackbar.make(coordinatorLayout, R.string.snackbar_no_network_connection,
                            Snackbar.LENGTH_LONG).show();
                }

                if (networkState.getStatus() == NetworkState.Status.NO_RESULT) {
                    Log.d(TAG, "Network Status: " + networkState.getStatus());
                    progressBar.setVisibility(View.INVISIBLE);
                    // TODO: Show an empty image
                    Snackbar.make(coordinatorLayout, "No results, sorry",
                            Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void observeNetworkState() {
        searchFragmentViewModel.getNetworkState().observe(getViewLifecycleOwner(), networkState -> {
            if (networkState != null) {
                mSearchAdapter.setNetworkState(networkState);
            }
        });
    }

    private synchronized void requestNewCall(String queryWord, String searchType) {
        // Setup the RecyclerView first
        setupRecyclerView();

        Log.d(TAG, "requestNewCall: queryWord: " + queryWord + " searchType: " + searchType);
        searchFragmentViewModel.getPagedList()
                .observe(getViewLifecycleOwner(), results -> mSearchAdapter.submitList(results));
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        int index = 0;

        if (!isMenuItemChecked) {
            return;
        }

      /*  MenuItem item = menu.getItem(R.id.search_type_group);
//        MenuItem item = menu.findItem(id);
        switch (item.getItemId()) {
            case R.id.action_type_artist:
                checkStateOfItem(menu, R.id.action_type_artist);
                break;
            case R.id.action_type_artwork:
                checkStateOfItem(menu, R.id.action_type_artwork);
                break;
            case R.id.action_type_gene:
                checkStateOfItem(menu, R.id.action_type_gene);
                break;
            case R.id.action_type_show:
                checkStateOfItem(menu, R.id.action_type_show);
                break;
            default:
                checkStateOfItem(menu, R.id.action_type_none);
                break;
        }*/
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.search_menu, menu);

        queryString = prefUtils.getSearchQuery();
        Log.d(TAG, "onCreateOptionsMenu: Query word " + queryString);

        // Set the SearchView
        SearchManager searchManager = (SearchManager) requireActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        if (searchManager == null) {
            return;
        }
        searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().getComponentName()));
        searchView.setSubmitButtonEnabled(false);
        if (!Utils.isNullOrEmpty(queryString)) {
            Log.d(TAG, "queryString: " + queryString);
            searchView.onActionViewExpanded();
            searchView.setQuery(queryString, true);
            searchFragmentViewModel.setQuery(queryString);
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "onQueryTextSubmit, query: " + query);
                searchFragmentViewModel.setQuery(query); // Set the new value to the mutable data TODO: Do I need this?
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "onQueryTextChange, newText: " + newText);
                if (newText.length() > 2) {
                    searchFragmentViewModel.setQuery(newText); // Set the new value to the mutable data
                    prefUtils.saveSearchQuery(newText); // Save the search query into SharedPreference
                    requestNewCall(newText, searchTypeString);
                }
                return false;
            }
        });

        searchView.clearFocus();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_search:
                return true;

            case R.id.action_type_artist:
                setItemState(item, "artist");
                Log.d(TAG, "Type word: " + searchTypeString);
                return true;

            case R.id.action_type_artwork:
                setItemState(item, "artwork");
                Log.d(TAG, "Type word: " + searchTypeString);
                return true;

            case R.id.action_type_gene:
                setItemState(item, "gene");
                Log.d(TAG, "Type word: " + searchTypeString);
                return true;

            case R.id.action_type_show:
                setItemState(item, "show");
                Log.d(TAG, "Type word: " + searchTypeString);
                return true;

            case R.id.action_type_none:
                setItemState(item, null);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setItemState(MenuItem item, String searchType) {
        if (item.isChecked()) {
            item.setChecked(false);
            isMenuItemChecked = false;
        } else {
            item.setChecked(true);
            isMenuItemChecked = true;
            searchTypeString = searchType;
            queryString = prefUtils.getSearchQuery();
            Log.d(TAG, "setItemState, queryString: " + queryString);
            searchFragmentViewModel.setType(searchType);
            requestNewCall(queryString, searchType);
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
        requestNewCall(prefUtils.getSearchQuery(), searchTypeString);
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onRefreshConnection() {
        new RetrieveNetworkConnectivity(this).execute();
    }

    @Override
    public void showSnackMessage(String resultMessage) {
        Snackbar.make(coordinatorLayout, resultMessage, Snackbar.LENGTH_LONG).show();
        requestNewCall(queryString, searchTypeString);
    }
}
