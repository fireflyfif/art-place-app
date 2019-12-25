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

package app.app.android.artplace.ui.searchresults;

import android.app.SearchManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import androidx.appcompat.widget.SearchView;
import android.widget.TextView;

import dev.iotarho.artplace.app.R;

import app.app.android.artplace.utils.TokenManager;
import app.app.android.artplace.callbacks.OnResultClickListener;
import app.app.android.artplace.model.search.Result;
import app.app.android.artplace.ui.searchdetail.SearchDetailActivity;
import app.app.android.artplace.ui.searchresults.adapter.SearchListAdapter;
import app.app.android.artplace.utils.Injection;
import app.app.android.artplace.utils.NetworkState;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;

public class SearchFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener, OnResultClickListener {

    private static final String TAG = SearchFragment.class.getSimpleName();
    private static final String ARG_SEARCH_TITLE = "search_title";

    private static final String PREFERENCE_SEARCH_KEY = "search_prefs";
    private static final String PREFERENCE_SEARCH_WORD= "search_word";

    private static final String SEARCH_QUERY_SAVE_STATE = "search_state";
    private static final String SEARCH_TYPE_SAVE_STATE = "search_type";
    private static final String RESULT_PARCEL_KEY = "results_key";

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.search_rv)
    RecyclerView searchResultsRv;
    @BindView(R.id.progress_bar_search)
    ProgressBar progressBar;
    @BindView(R.id.error_message_search)
    TextView errorMessage;

    private SearchFragmentViewModel mViewModel;
    private SearchListAdapter mSearchAdapter;
    private SearchView mSearchView;
    private String mQueryWordString;
    private SearchFragmentViewModelFactory mViewModelFactory;
    private String mSearchType;
    private TokenManager mTokenManager;

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
            mSearchType = savedInstanceState.getString(SEARCH_TYPE_SAVE_STATE);
        }

        // Initialize the TokenManager
        mTokenManager = TokenManager.getInstance(getActivity());

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
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

        mViewModelFactory = Injection.provideSearchViewModelFactory(mTokenManager, mQueryWordString, mSearchType);

        // Set the UI
        setupUi();

        return rootView;
    }

    private void setupRecyclerView() {
        int columnCount = getResources().getInteger(R.integer.list_column_count);

        StaggeredGridLayoutManager staggeredGridLayoutManager =
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);

        searchResultsRv.setLayoutManager(staggeredGridLayoutManager);

        mSearchAdapter = new SearchListAdapter(getContext(), this);
    }

    private void setupUi() {
        // Setup the RecyclerView first
        setupRecyclerView();

        // Initialize the ViewModel
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(SearchFragmentViewModel.class);

        mViewModel.getSearchResultsLiveData().observe(this, new Observer<PagedList<Result>>() {

            @Override
            public void onChanged(@Nullable PagedList<Result> results) {
                if (results != null) {
                    // Submit the list to the PagedListAdapter
                    mSearchAdapter.submitList(results);
                }
            }
        });

        mViewModel.getNetworkState().observe(this, new Observer<NetworkState>() {
            @Override
            public void onChanged(@Nullable NetworkState networkState) {
                if (networkState != null) {
                    mSearchAdapter.setNetworkState(networkState);
                }
            }
        });

        mViewModel.getInitialLoading().observe(this, new Observer<NetworkState>() {
            @Override
            public void onChanged(@Nullable NetworkState networkState) {
                if (networkState != null) {
                    progressBar.setVisibility(View.VISIBLE);
                    errorMessage.setText(R.string.loading_results_message);
                    errorMessage.setVisibility(View.VISIBLE);

                    if (networkState.getStatus() == NetworkState.Status.SUCCESS
                            && networkState.getStatus() == NetworkState.Status.NO_RESULT) {
                        Log.d(TAG, "Network Status: " + networkState.getStatus());
                        progressBar.setVisibility(View.GONE);
                        errorMessage.setText(R.string.no_data_found);
                        errorMessage.setVisibility(View.VISIBLE);
                        Snackbar.make(coordinatorLayout, "Please search for another word.",
                                Snackbar.LENGTH_LONG).show();
                    }

                    if (networkState.getStatus() == NetworkState.Status.SUCCESS) {
                        Log.d(TAG, "Network Status: " + networkState.getStatus());
                        progressBar.setVisibility(View.INVISIBLE);
                        errorMessage.setVisibility(View.INVISIBLE);
                    }

                    if (networkState.getStatus() == NetworkState.Status.FAILED) {
                        Log.d(TAG, "Network Status: " + networkState.getStatus());
                        progressBar.setVisibility(View.GONE);
                        // TODO: Hide this message when no connection but some cache results still visible
                        errorMessage.setVisibility(View.VISIBLE);
                        Snackbar.make(coordinatorLayout, R.string.snackbar_no_network_connection,
                                Snackbar.LENGTH_LONG).show();
                    }
                }
            }
        });

        // Set the Adapter on the RecyclerView
        searchResultsRv.setAdapter(mSearchAdapter);
    }

    public synchronized void requestNewCall(String queryWord, String searchType) {

        // Setup the RecyclerView first
        setupRecyclerView();

        // TODO: Generate a method for getting the query word from shared preferences
        mSharedPreferences = getContext().getSharedPreferences(PREFERENCE_SEARCH_KEY, MODE_PRIVATE);
        mQueryWordString = mSharedPreferences.getString(PREFERENCE_SEARCH_WORD, "");

        if (queryWord == null || queryWord.isEmpty()) {
            queryWord = "Andy Warhol";
        }

        mQueryWordString = queryWord;
        mSearchType = searchType;

        Log.d(TAG, "requestNewCall: Query word: " + mQueryWordString + "\nBut the reference queryWord is: "
                + queryWord);
        Log.d(TAG, "requestNewCall: Type word: " + mSearchType + "\nBut the reference for search type is: "
                + searchType);

        // Initialize the ViewModel
        mViewModel = ViewModelProviders.of(this, mViewModelFactory)
                .get(SearchFragmentViewModel.class);

        mViewModel.refreshSearchLiveData(queryWord, searchType)
                .observe(this, new Observer<PagedList<Result>>() {

            @Override
            public void onChanged(@Nullable PagedList<Result> results) {
                if (results != null) {
                    Log.d(TAG, "Size of the result: " + results.size());
                    // Submit the list to the PagedListAdapter
                    mSearchAdapter.submitList(results);
                }
            }
        });

        // Set the Adapter on the RecyclerView
        searchResultsRv.setAdapter(mSearchAdapter);
    }

    private void saveToSharedPreference(String searchQuery) {
        mSharedPreferences = getActivity()
                .getSharedPreferences(PREFERENCE_SEARCH_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(PREFERENCE_SEARCH_WORD, searchQuery);
        // Use apply() instead of commit(), because it is being saved on the background
        editor.apply();
        Log.d(TAG, "Saved into shared prefs");
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.action_search).setVisible(true);
        Log.d(TAG, "onPrepareOptionsMenu called");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.search_menu, menu);

        // TODO: Generate a method for getting the query word from shared preferences
        mSharedPreferences = getContext().getSharedPreferences(PREFERENCE_SEARCH_KEY, MODE_PRIVATE);
        mQueryWordString = mSharedPreferences.getString(PREFERENCE_SEARCH_WORD, "");
        Log.d(TAG, "onCreateOptionsMenu: Query word " + mQueryWordString);

        // Make the icon with a dynamic tint
        // source: https://stackoverflow.com/a/29916353/8132331
        Drawable drawable = menu.findItem(R.id.action_search).getIcon();
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(getActivity(), R.color.colorText));
        menu.findItem(R.id.action_search).setIcon(drawable);

        // Set the SearchView
        SearchManager searchManager =
                (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        if (mSearchView != null && !mQueryWordString.isEmpty()) {
            mSearchView.onActionViewExpanded();
            mSearchView.setQuery(mQueryWordString, true);
            mSearchView.clearFocus();
        }

        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        // Set the Submit Button
        mSearchView.setSubmitButtonEnabled(false);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                query = String.valueOf(mSearchView.getQuery());
                // Save the search query into SharedPreference
                saveToSharedPreference(query);
                requestNewCall(query, mSearchType);
                mQueryWordString = query;
                Log.d(TAG, "SearchFragment: onQueryTextSubmit called, query word: " + query);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "SearchFragment: onQueryTextChange called");

                if (newText.length() > 2) {
                    requestNewCall(newText, mSearchType);
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
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equals(PREFERENCE_SEARCH_KEY)) {
            mSharedPreferences = getActivity()
                    .getSharedPreferences(PREFERENCE_SEARCH_KEY, MODE_PRIVATE);

            if (mSharedPreferences.contains(PREFERENCE_SEARCH_WORD)) {
                mQueryWordString = mSharedPreferences.getString(PREFERENCE_SEARCH_WORD, "");

                Log.d(TAG, "onSharedPreferenceChanged: Saved search query: " + mQueryWordString);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "SearchFragment: onPause called" );
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "SearchFragment: onResume called" );
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onResultClick(Result result) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(RESULT_PARCEL_KEY, result);

        Intent intent = new Intent(getActivity(), SearchDetailActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
