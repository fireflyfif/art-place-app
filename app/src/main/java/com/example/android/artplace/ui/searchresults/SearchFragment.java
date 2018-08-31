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

        mViewModelFactory = new SearchFragmentViewModelFactory(ArtPlaceApp.getInstance(), mQueryWordString);

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

        // Set the Adapter on the RecyclerView
        searchResultsRv.setAdapter(mSearchAdapter);

    }

    public synchronized void requestNewCall(String queryWord) {

        // Setup the RecyclerView first
        setupRecyclerView();

        if (queryWord == null) {
            queryWord = "Andy Warhol";
        }

        Log.d(TAG, "requestNewCall: Query word: " + queryWord);

        mViewModelFactory = new SearchFragmentViewModelFactory(ArtPlaceApp.getInstance(), queryWord);

        // Initialize the ViewModel
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(SearchFragmentViewModel.class);

        mViewModel.refreshSearchLiveData(ArtPlaceApp.getInstance(), queryWord).observe(this, new Observer<PagedList<Result>>() {

            @Override
            public void onChanged(@Nullable PagedList<Result> results) {
                if (results != null) {
                    // Submit the list to the PagedListAdapter
                    mSearchAdapter.submitList(results);
                }
            }
        });

        // Set the Adapter on the RecyclerView
        searchResultsRv.setAdapter(mSearchAdapter);
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

                requestNewCall(query);
                Log.d(TAG, "SearchFragment: onQueryTextSubmit called, query word: " + query);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "SearchFragment: onQueryTextChange called");

                if (newText.length() > 0) {

                    requestNewCall(newText);
                }

                //Toast.makeText(getContext(), "Search art word here", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
