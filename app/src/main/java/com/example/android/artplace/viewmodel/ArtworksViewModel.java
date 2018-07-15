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

package com.example.android.artplace.viewmodel;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;

import com.example.android.artplace.ArtPlaceApp;
import com.example.android.artplace.datasource.ArtworkDataSourceFactory;
import com.example.android.artplace.datasource.ArtworkDataSource;
import com.example.android.artplace.model.Artwork;
import com.example.android.artplace.utils.NetworkState;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ArtworksViewModel extends ViewModel {

    private Executor mExecutor;
    private LiveData<NetworkState> mNetworkState;
    public LiveData<PagedList<Artwork>> mArtworkLiveData;
    private ArtworkDataSourceFactory mArtworkDataSourceFactory;
    //private ArtsyApiInterface mApiService;
    private ArtPlaceApp mAppController;


    public ArtworksViewModel(@NonNull ArtPlaceApp appController) {
        // This might produce an error, because the ViewModel constructor has no zero argument constructor
        mAppController = appController;
        // TODO: Do I need this reference here?
        //mArtworkDataSourceFactory = artworkDataSourceFactory;

        init();
    }

    private void init() {

        // Initialize an executor class
        mExecutor = Executors.newFixedThreadPool(5);

        // Get an instance of the DataSourceFactory class
        mArtworkDataSourceFactory = new ArtworkDataSourceFactory(mAppController);

        // Initialize the network state liveData
        mNetworkState = Transformations.switchMap(mArtworkDataSourceFactory.getArtworksDataSourceLiveData(), new Function<ArtworkDataSource, LiveData<NetworkState>>() {
            @Override
            public LiveData<NetworkState> apply(ArtworkDataSource input) {
                return input.getNetworkState();
            }
        });

        PagedList.Config pagedListConfig = new PagedList.Config.Builder()
                        .setEnablePlaceholders(true)
                        .setInitialLoadSizeHint(10)
                        // If not set, defaults to page size.
                        //A value of 0 indicates that no list items
                        // will be loaded until they are specifically requested
                        .setPrefetchDistance(10)
                        .setPageSize(10)
                        .build();


        mArtworkLiveData = new LivePagedListBuilder<>(mArtworkDataSourceFactory, pagedListConfig) // liveData is null??
                .setFetchExecutor(mExecutor)
                .build();
    }

    public LiveData<NetworkState> getNetworkState() {
        return mNetworkState;
    }

    public LiveData<PagedList<Artwork>> getArtworkLiveData() {
        return mArtworkLiveData;
    }
}
