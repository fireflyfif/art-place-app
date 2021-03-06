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

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import java.util.Locale;

import dev.iotarho.artplace.app.AppExecutors;
import dev.iotarho.artplace.app.model.search.Result;
import dev.iotarho.artplace.app.repository.ArtsyRepository;
import dev.iotarho.artplace.app.ui.searchresults.datasource.SearchDataSource;
import dev.iotarho.artplace.app.ui.searchresults.datasource.SearchDataSourceFactory;
import dev.iotarho.artplace.app.utils.NetworkState;
import dev.iotarho.artplace.app.utils.Utils;

import static dev.iotarho.artplace.app.utils.Constants.SearchFragment.INITIAL_SIZE_HINT;
import static dev.iotarho.artplace.app.utils.Constants.SearchFragment.PAGE_SIZE;
import static dev.iotarho.artplace.app.utils.Constants.SearchFragment.PREFETCH_DISTANCE_HINT;

public class SearchFragmentViewModel extends ViewModel {

    private LiveData<NetworkState> networkLiveState;
    private LiveData<NetworkState> initialLiveLoadingState;
    private LiveData<PagedList<Result>> resultLivePagedList;

    private MutableLiveData<String> queryLiveData = new MutableLiveData<>(Utils.randomSearch()); // set the default query
    private MutableLiveData<String> typeLiveData = new MutableLiveData<>();
    private SearchResultLiveData searchResultLiveData = new SearchResultLiveData(queryLiveData, typeLiveData);

    private PagedList.Config pagedListConfig;
    private ArtsyRepository repo;
    private SearchDataSourceFactory dataSourceFactory;


    public SearchFragmentViewModel(ArtsyRepository artsyRepository) {
        repo = artsyRepository;
        init();
    }

    private void init() {
        // Configure the PagedList.Config
        pagedListConfig = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(INITIAL_SIZE_HINT)
                .setPrefetchDistance(PREFETCH_DISTANCE_HINT)
                .setPageSize(PAGE_SIZE)
                .build();

        resultLivePagedList = Transformations.switchMap(searchResultLiveData, input -> {
            // Get an instance of the DataSourceFactory class
            dataSourceFactory = new SearchDataSourceFactory(repo, input.first, input.second);
            // Initialize the network state liveData
            networkLiveState = Transformations.switchMap(dataSourceFactory.getSearchDataSourceMutableLiveData(), SearchDataSource::getNetworkState);
            // Initialize the Loading state liveData
            initialLiveLoadingState = Transformations.switchMap(dataSourceFactory.getSearchDataSourceMutableLiveData(), SearchDataSource::getLoadingState);

            resultLivePagedList = new LivePagedListBuilder<>(dataSourceFactory, pagedListConfig)
                    .setFetchExecutor(AppExecutors.getInstance().networkIO())
                    .build();
            return resultLivePagedList;
        });
    }

    public LiveData<PagedList<Result>> getPagedList() {
        return resultLivePagedList;
    }

    public LiveData<NetworkState> getNetworkState() {
        return networkLiveState;
    }

    public LiveData<NetworkState> getInitialLoading() {
        return initialLiveLoadingState;
    }

    public MutableLiveData<String> getQueryLiveData() {
        return queryLiveData;
    }

    public MutableLiveData<String> getTypeLiveData() {
        return typeLiveData;
    }

    public void setQuery(String originalInput) {
        String input = originalInput.toLowerCase(Locale.getDefault()).trim();
        if (input.equals(queryLiveData.getValue())) {
            return;
        }
        queryLiveData.setValue(input);
    }

    public void setType(String typeInput) {
        if (typeInput == null) {
            return;
        }
        String input = typeInput.toLowerCase(Locale.getDefault()).trim();
        if (input.equals(typeLiveData.getValue())) {
            return;
        }

        typeLiveData.setValue(input);
    }

    /**
     * Method used for requesting a new network call with the updated query
     * <p>
     * Note: This method might not be ideal, but it triggers a new call to get new data
     *
     * @return fresh new list of search result
     */
    public LiveData<PagedList<Result>> refreshSearchLiveData() {
        dataSourceFactory = new SearchDataSourceFactory(repo, queryLiveData.getValue(), typeLiveData.getValue());
        resultLivePagedList = new LivePagedListBuilder<>(dataSourceFactory, pagedListConfig)
                .setFetchExecutor(AppExecutors.getInstance().networkIO())
                .build();

        return resultLivePagedList;
    }
}
