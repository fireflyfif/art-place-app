package dev.iotarho.artplace.app.ui.searchresults.datasource

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import dev.iotarho.artplace.app.model.artists.Artist
import dev.iotarho.artplace.app.repository.ArtsyRepository

class TrendyDataSourceFactory(private val repository: ArtsyRepository, private val offset: Int) : DataSource.Factory<Long, Artist>() {
    private val trendyDataSourceMutable: MutableLiveData<TrendyArtistsDataSource> = MutableLiveData()
    val trendyDataSource: LiveData<TrendyArtistsDataSource> = trendyDataSourceMutable

    override fun create(): DataSource<Long, Artist?> {
        val trendyArtistsDataSource = TrendyArtistsDataSource(repository, offset)
        trendyDataSourceMutable.postValue(trendyArtistsDataSource)
        return trendyArtistsDataSource
    }

}