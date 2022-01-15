package dev.iotarho.artplace.app.ui.artworks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import dev.iotarho.artplace.app.AppExecutors
import dev.iotarho.artplace.app.model.artists.Artist
import dev.iotarho.artplace.app.repository.ArtsyRepository
import dev.iotarho.artplace.app.ui.searchresults.datasource.TrendyArtistsDataSource
import dev.iotarho.artplace.app.ui.searchresults.datasource.TrendyDataSourceFactory
import dev.iotarho.artplace.app.utils.Constants
import dev.iotarho.artplace.app.utils.NetworkState
import dev.iotarho.artplace.app.utils.Utils

class TrendyArtistsViewModel(repo: ArtsyRepository) : ViewModel() {

    private var trendyDataSourceFactory: TrendyDataSourceFactory = TrendyDataSourceFactory(repo, Utils.randomOffset())

    // Initialize the network state liveData
    val networkState: LiveData<NetworkState> = Transformations.switchMap(trendyDataSourceFactory.trendyDataSource) { obj: TrendyArtistsDataSource -> obj.networkState }
    // Initialize the Loading state liveData
    val initialLoading: LiveData<NetworkState> = Transformations.switchMap(trendyDataSourceFactory.trendyDataSource) { obj: TrendyArtistsDataSource -> obj.initialLoading }

    var trendyArtistLiveData: LiveData<PagedList<Artist?>?> = MutableLiveData()

    init {
        // Configure the PagedList.Config
        val pagedListConfig = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(Constants.ArtworksFragment.INITIAL_SIZE_HINT) // If not set, defaults to page size.
                //A value of 0 indicates that no list items
                // will be loaded until they are specifically requested
                .setPrefetchDistance(Constants.ArtworksFragment.PREFETCH_DISTANCE_HINT)
                .setPageSize(Constants.ArtworksFragment.PAGE_SIZE)
                .build()
        trendyArtistLiveData = LivePagedListBuilder(trendyDataSourceFactory, pagedListConfig)
                .setFetchExecutor(AppExecutors.getInstance().networkIO())
                .build()
    }

    fun refreshTrendyArtistsLiveData(): LiveData<PagedList<Artist?>?> {
        val pagedListConfig = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(Constants.ArtworksFragment.INITIAL_SIZE_HINT) // If not set, defaults to page size.
                //A value of 0 indicates that no list items
                // will be loaded until they are specifically requested
                .setPrefetchDistance(Constants.ArtworksFragment.PREFETCH_DISTANCE_HINT)
                .setPageSize(Constants.ArtworksFragment.PAGE_SIZE)
                .build()
        trendyArtistLiveData = LivePagedListBuilder(trendyDataSourceFactory, pagedListConfig)
                .setFetchExecutor(AppExecutors.getInstance().networkIO())
                .build()
        return trendyArtistLiveData
    }
}