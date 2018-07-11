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

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;

import com.example.android.artplace.datasource.ArtworkDataFactory;
import com.example.android.artplace.model.Artwork;
import com.example.android.artplace.model.Embedded;
import com.example.android.artplace.remote.MainApplication;
import com.example.android.artplace.utils.NetworkState;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class EmbeddedViewModel extends ViewModel {

    private Executor mExecutor;
    private LiveData<NetworkState> mNetworkState;
    // TODO: Decide if I should wrap the Artwork or the Embedded model with the PagedList???
    private LiveData<PagedList<Artwork>> mArtworkLiveData;

    private MainApplication mMainApplication;

    public EmbeddedViewModel(@NonNull MainApplication mainApplication) {
        this.mMainApplication = mainApplication;
        init();
    }

    private void init() {

        // Initialize an executor class
        mExecutor = Executors.newFixedThreadPool(5);

        // Get an instance of the DataSourceFactory class
        ArtworkDataFactory artworkDataFactory = new ArtworkDataFactory(mMainApplication);
        // Initialize the network state liveData
        // TODO: Finish it!
        //mNetworkState = Transformations.switchMap(artworkDataFactory.getMutableLiveData(),
                //dataSource)

        PagedList.Config pagedListConfig =
                (new PagedList.Config.Builder())
                        .setEnablePlaceholders(false)
                        .setInitialLoadSizeHint(10)
                        // If not set, defaults to page size.
                        //A value of 0 indicates that no list items
                        // will be loaded until they are specifically requested
                        .setPrefetchDistance(10)
                        .setPageSize(1)
                        .build();

        // TODO: Resolve the lint warning!
        mArtworkLiveData = (new LivePagedListBuilder(artworkDataFactory, pagedListConfig))
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
