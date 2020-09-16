package dev.iotarho.artplace.app.ui.artworks;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import dev.iotarho.artplace.app.AppExecutors;
import dev.iotarho.artplace.app.model.artists.Artist;
import dev.iotarho.artplace.app.model.artworks.Artwork;
import dev.iotarho.artplace.app.repository.ArtsyRepository;
import dev.iotarho.artplace.app.ui.searchresults.datasource.TrendyArtistsDataSource;
import dev.iotarho.artplace.app.ui.searchresults.datasource.TrendyDataSourceFactory;
import dev.iotarho.artplace.app.utils.NetworkState;

import static dev.iotarho.artplace.app.utils.Constants.ArtworksFragment.INITIAL_SIZE_HINT;
import static dev.iotarho.artplace.app.utils.Constants.ArtworksFragment.PAGE_SIZE;
import static dev.iotarho.artplace.app.utils.Constants.ArtworksFragment.PREFETCH_DISTANCE_HINT;

public class TrendyArtistsViewModel extends ViewModel {

    private LiveData<NetworkState> networkState;
    private LiveData<NetworkState> initialLoading;
    private LiveData<PagedList<Artist>> artistPagedListLiveData;

    private ArtsyRepository repo;
    private TrendyDataSourceFactory trendyDataSourceFactory;

    public TrendyArtistsViewModel(ArtsyRepository repo) {
        this.repo = repo;
        init();
    }

    private void init() {

        // Get an instance of the DataSourceFactory class
        trendyDataSourceFactory = new TrendyDataSourceFactory(1, repo);

        // Initialize the network state liveData
        networkState = Transformations.switchMap(trendyDataSourceFactory.getTrendyDataSource(), TrendyArtistsDataSource::getNetworkState);

        // Initialize the Loading state liveData
        initialLoading = Transformations.switchMap(trendyDataSourceFactory.getTrendyDataSource(), TrendyArtistsDataSource::getInitialLoading);

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

        artistPagedListLiveData = new LivePagedListBuilder<>(trendyDataSourceFactory, pagedListConfig)
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

    public LiveData<PagedList<Artist>> getTrendyArtistLiveData() {
        return artistPagedListLiveData;
    }

}
