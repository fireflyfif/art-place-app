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

package dev.iotarho.artplace.app.ui.artworks;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import dev.iotarho.artplace.app.AppExecutors;
import dev.iotarho.artplace.app.model.artworks.Artwork;
import dev.iotarho.artplace.app.repository.ArtsyRepository;
import dev.iotarho.artplace.app.ui.artworks.datasource.ArtworkDataSource;
import dev.iotarho.artplace.app.ui.artworks.datasource.ArtworkDataSourceFactory;
import dev.iotarho.artplace.app.utils.NetworkState;

import static dev.iotarho.artplace.app.utils.Constants.ArtworksFragment.INITIAL_SIZE_HINT;
import static dev.iotarho.artplace.app.utils.Constants.ArtworksFragment.PAGE_SIZE;
import static dev.iotarho.artplace.app.utils.Constants.ArtworksFragment.PREFETCH_DISTANCE_HINT;

public class ArtworksViewModel extends ViewModel {

    private LiveData<NetworkState> networkState;
    private LiveData<NetworkState> initialLoading;

    private LiveData<PagedList<Artwork>> artworksPagedListLiveData;
    private LiveData<Artwork> artworkLiveData;

    private ArtworkDataSourceFactory artworkDataSourceFactory;

    private ArtsyRepository repo;

    public ArtworksViewModel(ArtsyRepository repo) {
        this.repo = repo;
        init();
    }

    /**
     Method for initializing the DataSourceFactory and for building the LiveData
    */
    private void init() {

        // Get an instance of the DataSourceFactory class
        artworkDataSourceFactory = new ArtworkDataSourceFactory(repo);

        // Initialize the network state liveData
        networkState = Transformations.switchMap(artworkDataSourceFactory.getArtworksDataSourceLiveData(), ArtworkDataSource::getNetworkState);

        // Initialize the Loading state liveData
        initialLoading = Transformations.switchMap(artworkDataSourceFactory.getArtworksDataSourceLiveData(), ArtworkDataSource::getInitialLoading);

        // Configure the PagedList.Config
        PagedList.Config pagedListConfig = new PagedList.Config.Builder()
                        .setEnablePlaceholders(false)
                        .setInitialLoadSizeHint(INITIAL_SIZE_HINT)
                        // If not set, defaults to page size.
                        //A value of 0 indicates that no list items
                        // will be loaded until they are specifically requested
                        .setPrefetchDistance(PREFETCH_DISTANCE_HINT)
                        .setPageSize(PAGE_SIZE)
                        .build();

        artworksPagedListLiveData = new LivePagedListBuilder<>(artworkDataSourceFactory, pagedListConfig)
                //.setInitialLoadKey()
                .setFetchExecutor(AppExecutors.getInstance().networkIO())
                .build();
    }

    public LiveData<NetworkState> getNetworkState() {
        return networkState;
    }

    public LiveData<NetworkState> getInitialLoading() {
        return initialLoading;
    }

    public LiveData<PagedList<Artwork>> getArtworkLiveData() {
        return artworksPagedListLiveData;
    }

    public LiveData<Artwork> getArtworkFromLink() {
        return artworkLiveData;
    }

    public void initArtworkData(String artworkLink) {
        if (artworkLiveData != null) {
            return;
        }
        artworkLiveData = repo.getArtworkFromLink(artworkLink);
    }

    public LiveData<PagedList<Artwork>> refreshArtworkLiveData() {
        PagedList.Config pagedListConfig = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(INITIAL_SIZE_HINT)
                // If not set, defaults to page size.
                //A value of 0 indicates that no list items
                // will be loaded until they are specifically requested
                .setPrefetchDistance(PREFETCH_DISTANCE_HINT)
                .setPageSize(PAGE_SIZE)
                .build();


        artworksPagedListLiveData = new LivePagedListBuilder<>(artworkDataSourceFactory, pagedListConfig)
                .setFetchExecutor(AppExecutors.getInstance().networkIO())
                .build();

        return artworksPagedListLiveData;
    }

}
