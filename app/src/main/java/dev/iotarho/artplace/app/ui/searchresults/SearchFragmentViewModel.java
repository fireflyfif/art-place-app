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

import android.util.Log;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import dev.iotarho.artplace.app.AppExecutors;
import dev.iotarho.artplace.app.model.search.Result;
import dev.iotarho.artplace.app.repository.ArtsyRepository;
import dev.iotarho.artplace.app.ui.searchresults.datasource.SearchDataSource;
import dev.iotarho.artplace.app.ui.searchresults.datasource.SearchDataSourceFactory;
import dev.iotarho.artplace.app.utils.NetworkState;

public class SearchFragmentViewModel extends ViewModel {

    private static final String TAG = SearchFragmentViewModel.class.getSimpleName();

    private static final int PAGE_SIZE = 10;
    private static final int INITIAL_SIZE_HINT = 10;
    private static final int PREFETCH_DISTANCE_HINT = 10;

    private SearchDataSourceFactory mSearchDataSourceFactory;

    private LiveData<NetworkState> mNetworkState;
    private LiveData<NetworkState> mInitialLoading;

    private LiveData<PagedList<Result>> mResultPagedList;
    private String mQueryWord;
    private String mTypeWord;
    private ArtsyRepository mRepo;


    public SearchFragmentViewModel(String queryWord, String typeWord) {
        mQueryWord = queryWord;
        mTypeWord = typeWord;
        mRepo = ArtsyRepository.getInstance();

        init();
    }

    private void init() {

        // Get an instance of the DataSourceFactory class
        mSearchDataSourceFactory = new SearchDataSourceFactory(mRepo, mQueryWord, mTypeWord);

        // Initialize the network state liveData
        mNetworkState = Transformations.switchMap(mSearchDataSourceFactory.getSearchDataSourceMutableLiveData(),
                (Function<SearchDataSource, LiveData<NetworkState>>) input -> input.getNetworkState());

        // Initialize the Loading state liveData
        mInitialLoading = Transformations.switchMap(mSearchDataSourceFactory.getSearchDataSourceMutableLiveData(),
                (Function<SearchDataSource, LiveData<NetworkState>>) input -> input.getLoadingState());


        // Configure the PagedList.Config
        PagedList.Config pagedListConfig = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(INITIAL_SIZE_HINT)
                .setPrefetchDistance(PREFETCH_DISTANCE_HINT)
                .setPageSize(PAGE_SIZE)
                .build();

        mResultPagedList = new LivePagedListBuilder<>(mSearchDataSourceFactory, pagedListConfig)
                .setFetchExecutor(AppExecutors.getInstance().networkIO())
                .build();
    }

    public LiveData<NetworkState> getNetworkState() {
        return mNetworkState;
    }

    public LiveData<NetworkState> getInitialLoading() {
        return mInitialLoading;
    }

    public LiveData<PagedList<Result>> getSearchResultsLiveData() {
        return mResultPagedList;
    }

    public LiveData<PagedList<Result>> refreshSearchLiveData(String queryWord, String typeWord) {
        // Get an instance of the DataSourceFactory class
        mSearchDataSourceFactory = new SearchDataSourceFactory(mRepo, queryWord, typeWord);

        // Configure the PagedList.Config
        PagedList.Config pagedListConfig = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(INITIAL_SIZE_HINT)
                .setPrefetchDistance(PREFETCH_DISTANCE_HINT)
                .setPageSize(PAGE_SIZE)
                .build();

        Log.d(TAG, "SearchFragmentViewModel: PagedListConfig: " + pagedListConfig);

        mResultPagedList = new LivePagedListBuilder<>(mSearchDataSourceFactory, pagedListConfig)
                .setFetchExecutor(AppExecutors.getInstance().networkIO())
                .build();
        return mResultPagedList;
    }
}