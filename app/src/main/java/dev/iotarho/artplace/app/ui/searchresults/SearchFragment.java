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
import androidx.recyclerview.widget.GridLayoutManager;
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
import static dev.iotarho.artplace.app.utils.Constants.SearchFragment.ARTIST_TYPE;
import static dev.iotarho.artplace.app.utils.Constants.SearchFragment.ARTWORK_TYPE;
import static dev.iotarho.artplace.app.utils.Constants.SearchFragment.GENE_TYPE;
import static dev.iotarho.artplace.app.utils.Constants.SearchFragment.ITEM_CHECKED_SAVE_STATE;
import static dev.iotarho.artplace.app.utils.Constants.SearchFragment.RESULT_PARCEL_KEY;
import static dev.iotarho.artplace.app.utils.Constants.SearchFragment.SEARCH_QUERY_SAVE_STATE;
import static dev.iotarho.artplace.app.utils.Constants.SearchFragment.SEARCH_TYPE_SAVE_STATE;
import static dev.iotarho.artplace.app.utils.Constants.SearchFragment.SHOW_TYPE;

public class SearchFragment extends Fragment implements
        OnResultClickListener,
        SnackMessageListener,
        SwipeRefreshLayout.OnRefreshListener,
        OnRefreshListener {

    private static final String TAG = SearchFragment.class.getSimpleName();

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.search_rv)
    RecyclerView searchResultsRv;
    @BindView(R.id.progress_bar_search)
    ProgressBar progressBar;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.empty_screen)
    View emptyScreen;

    private SearchFragmentViewModel searchFragmentViewModel;
    private SearchListAdapter searchListAdapter;
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

        setHasOptionsMenu(true); // Add a menu to the current Fragment

        if (savedInstanceState != null) {
            queryString = savedInstanceState.getString(SEARCH_QUERY_SAVE_STATE);
            searchTypeString = savedInstanceState.getString(SEARCH_TYPE_SAVE_STATE);
            isMenuItemChecked = savedInstanceState.getBoolean(ITEM_CHECKED_SAVE_STATE);
            Log.d(TAG, "savedInstanceState, queryString: " + queryString + " searchTypeString: " + searchTypeString);
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
        Log.d(TAG, "queryString from prefs: " + queryString);
        SearchFragmentViewModelFactory searchFragmentViewModelFactory = Injection.provideSearchViewModelFactory();

        // Initialize the ViewModel
        searchFragmentViewModel = new ViewModelProvider(getViewModelStore(), searchFragmentViewModelFactory).get(SearchFragmentViewModel.class);

        swipeRefreshLayout.setOnRefreshListener(this);

        setupUi(); // Set the UI onViewCreated to ensure that the view is created
        // Get the search word from the intent
        Bundle appData = getArguments(); //getIntent().getBundleExtra(SearchManager.APP_DATA);
        if (appData != null) {
            queryString = appData.getString(SEARCH_WORD_EXTRA);
            Log.d(TAG, "onCreateView, queryString: " + queryString);
        }

        return rootView;
    }

    /*@Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        swipeRefreshLayout.setOnRefreshListener(this);

        setupUi(); // Set the UI onViewCreated to ensure that the view is created
    }*/

    private void setupUi() {
        searchFragmentViewModel.getPagedList().observe(getViewLifecycleOwner(), results -> {
            Log.d(TAG, "temp, results: " + results);
            searchListAdapter.submitList(results); // submit the list to the PagedListAdapter
            observeNetworkState();
            observeLoadingState();
        });

        searchFragmentViewModel.getQueryLiveData().observe(getViewLifecycleOwner(), query -> {
            Log.d(TAG, "temp, query: " + query);
        });

        searchFragmentViewModel.getTypeLiveData().observe(getViewLifecycleOwner(), type -> {
            Log.d(TAG, "temp, type: " + type);
        });

        // Setup the RecyclerView first
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        int columnCount = getResources().getInteger(R.integer.list_column_count);
        // Unable to use StaggeredGridLayoutManager because of a known bug:
        // https://gist.github.com/fireflyfif/401e669697cb2736ff7b3ffe7dfcb76e
        StaggeredGridLayoutManager staggeredGridLayoutManager =
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), columnCount);

        searchResultsRv.setLayoutManager(gridLayoutManager);
        searchListAdapter = new SearchListAdapter(this, this);
        // Set the Adapter on the RecyclerView
        searchResultsRv.setAdapter(searchListAdapter);
    }

    private void observeLoadingState() {
        searchFragmentViewModel.getInitialLoading().observe(getViewLifecycleOwner(), networkState -> {
            if (networkState == null) {
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            switch (networkState.getStatus()) {
                case SUCCESS:
                    progressBar.setVisibility(View.GONE);
                    emptyScreen.setVisibility(View.GONE);
                    if (networkState.getStatus() == NetworkState.Status.NO_RESULT) {
                        emptyScreen.setVisibility(View.VISIBLE); // Show an empty image
                    }
                    break;
                case FAILED:
                    progressBar.setVisibility(View.GONE);
                    emptyScreen.setVisibility(View.GONE);
                    Snackbar.make(coordinatorLayout, R.string.snackbar_no_network_connection, Snackbar.LENGTH_LONG).show();
                    break;
                case NO_RESULT:
                    progressBar.setVisibility(View.GONE);
                    emptyScreen.setVisibility(View.VISIBLE); // Show an empty image
                    break;
                default:
                    progressBar.setVisibility(View.GONE);
                    emptyScreen.setVisibility(View.GONE);
                    break;
            }
        });
    }

    private void observeNetworkState() {
        searchFragmentViewModel.getNetworkState().observe(getViewLifecycleOwner(), networkState -> {
            if (networkState != null) {
                searchListAdapter.setNetworkState(networkState);
            }
        });
    }

    private synchronized void requestNewCall() {
        // Setup the RecyclerView first
        setupRecyclerView();
        searchFragmentViewModel.getPagedList().observe(getViewLifecycleOwner(), results -> {
            Log.d(TAG, "temp, requestNewCall, results: " + results);
            searchListAdapter.submitList(results);
            observeNetworkState();
            observeLoadingState();
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.search_menu, menu);

        queryString = prefUtils.getSearchQuery();

        // Set the SearchView
        SearchManager searchManager = (SearchManager) requireActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setIconifiedByDefault(false); // Do not iconify the widget, expand it by default

        if (searchManager == null) {
            return;
        }
        searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().getComponentName()));
        searchView.setSubmitButtonEnabled(false);
        if (!Utils.isNullOrEmpty(queryString)) {
            searchView.onActionViewExpanded();
            searchView.setQuery(queryString, true);
            searchFragmentViewModel.setQuery(queryString);
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchFragmentViewModel.setQuery(query); // Set the new value to the mutable data
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 2) {
                    searchFragmentViewModel.setQuery(newText); // Set the new value to the mutable data
                    prefUtils.saveSearchQuery(newText); // Save the search query into SharedPreference
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
                setItemState(item, ARTIST_TYPE);
                return true;

            case R.id.action_type_artwork:
                setItemState(item, ARTWORK_TYPE);
                return true;

            case R.id.action_type_gene:
                setItemState(item, GENE_TYPE);
                return true;

            case R.id.action_type_show:
                setItemState(item, SHOW_TYPE);
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
            searchFragmentViewModel.setType(searchType);
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
        requestNewCall();
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
        requestNewCall();
    }
}
