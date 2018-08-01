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

package com.example.android.artplace.ui.artworksMainActivity;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;

import com.example.android.artplace.AppExecutors;
import com.example.android.artplace.ArtPlaceApp;
import com.example.android.artplace.datasource.ArtworkDataSourceFactory;
import com.example.android.artplace.datasource.ArtworkDataSource;
import com.example.android.artplace.model.Artworks.Artwork;
import com.example.android.artplace.utils.NetworkState;

public class ArtworksViewModel extends ViewModel {

    private LiveData<NetworkState> mNetworkState;
    private LiveData<NetworkState> mInitialLoading;

    public LiveData<PagedList<Artwork>> mArtworkLiveData;
    private ArtworkDataSourceFactory mArtworkDataSourceFactory;
    private ArtPlaceApp mAppController;

    private static final int PAGE_SIZE = 30;


    public ArtworksViewModel(@NonNull ArtPlaceApp appController) {
        // This will produce an error, because the ViewModel constructor has no zero argument constructor
        mAppController = appController;

        init();
    }

    /*
    Method for initializing the DataSourceFactory and for building the LiveData
     */
    private void init() {

        // Get an instance of the DataSourceFactory class
        mArtworkDataSourceFactory = new ArtworkDataSourceFactory(mAppController);

        // Initialize the network state liveData
        mNetworkState = Transformations.switchMap(mArtworkDataSourceFactory.getArtworksDataSourceLiveData(),
                new Function<ArtworkDataSource, LiveData<NetworkState>>() {
            @Override
            public LiveData<NetworkState> apply(ArtworkDataSource input) {
                return input.getNetworkState();
            }
        });

        // Initialize the Loading state liveData
        mInitialLoading = Transformations.switchMap(mArtworkDataSourceFactory.getArtworksDataSourceLiveData(),
                new Function<ArtworkDataSource, LiveData<NetworkState>>() {
                    @Override
                    public LiveData<NetworkState> apply(ArtworkDataSource input) {
                        return input.getInitialLoading();
                    }
                });

        PagedList.Config pagedListConfig = new PagedList.Config.Builder()
                        .setEnablePlaceholders(true)
                        .setInitialLoadSizeHint(PAGE_SIZE)
                        // If not set, defaults to page size.
                        //A value of 0 indicates that no list items
                        // will be loaded until they are specifically requested
                        .setPrefetchDistance(10 * 2)
                        .setPageSize(PAGE_SIZE)
                        .build();


        mArtworkLiveData = new LivePagedListBuilder<>(mArtworkDataSourceFactory, pagedListConfig)
                //.setInitialLoadKey()
                .setFetchExecutor(AppExecutors.getInstance().networkIO())
                .build();
    }

    public LiveData<NetworkState> getNetworkState() {
        return mNetworkState;
    }

    public LiveData<NetworkState> getInitialLoading() {
        return mNetworkState;
    }

    public LiveData<PagedList<Artwork>> getArtworkLiveData() {
        return mArtworkLiveData;
    }
}