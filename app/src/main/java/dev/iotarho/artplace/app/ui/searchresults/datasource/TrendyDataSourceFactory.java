package dev.iotarho.artplace.app.ui.searchresults.datasource;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

import dev.iotarho.artplace.app.model.artists.Artist;
import dev.iotarho.artplace.app.repository.ArtsyRepository;

public class TrendyDataSourceFactory extends DataSource.Factory<Long, Artist> {

    private final MutableLiveData<TrendyArtistsDataSource> trendyDataSource;
//    private final String searchKey;
    private final ArtsyRepository repository;

    public TrendyDataSourceFactory(String searchKey, ArtsyRepository repository) {
//        this.searchKey = searchKey;
        this.repository = repository;
        trendyDataSource = new MutableLiveData<>();
    }

    @NonNull
    @Override
    public DataSource<Long, Artist> create() {
        TrendyArtistsDataSource trendyArtistsDataSource = new TrendyArtistsDataSource(repository);
        trendyDataSource.postValue(trendyArtistsDataSource);
        return trendyArtistsDataSource;
    }
}
