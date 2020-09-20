package dev.iotarho.artplace.app.ui.searchresults.datasource;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PageKeyedDataSource;

import java.util.List;

import dev.iotarho.artplace.app.model.Next;
import dev.iotarho.artplace.app.model.PageLinks;
import dev.iotarho.artplace.app.model.artists.Artist;
import dev.iotarho.artplace.app.model.artists.ArtistWrapperResponse;
import dev.iotarho.artplace.app.model.artists.EmbeddedArtists;
import dev.iotarho.artplace.app.repository.ArtsyRepository;
import dev.iotarho.artplace.app.utils.NetworkState;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static dev.iotarho.artplace.app.utils.Constants.ArtworksFragment.INITIAL_SIZE_HINT;
import static dev.iotarho.artplace.app.utils.Constants.ArtworksFragment.PAGE_SIZE;

public class TrendyArtistsDataSource extends PageKeyedDataSource<Long, Artist> {

    private static final String LOG_TAG = TrendyArtistsDataSource.class.getSimpleName();
    private final ArtsyRepository repository;
    private final MutableLiveData<NetworkState> networkState;
    private final MutableLiveData<NetworkState> initialLoading;
    private String nextUrl;
    private int offset;

    public TrendyArtistsDataSource(ArtsyRepository repository, int offset) {
        this.repository = repository;
        this.offset = offset;
        networkState = new MutableLiveData<>();
        initialLoading = new MutableLiveData<>();
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Long> params, @NonNull LoadInitialCallback<Long, Artist> callback) {
        // Update NetworkState
        initialLoading.postValue(NetworkState.LOADING);
        networkState.postValue(NetworkState.LOADING);

        repository.getArtsyApi().getTrendingArtists(true, "-trending", offset, INITIAL_SIZE_HINT, PAGE_SIZE)
                .enqueue(new Callback<ArtistWrapperResponse>() {
                    @Override
                    public void onResponse(Call<ArtistWrapperResponse> call, Response<ArtistWrapperResponse> response) {
                        if (response.isSuccessful()) {
                            ArtistWrapperResponse wrapperResponse = response.body();
                            if (wrapperResponse != null) {
                                EmbeddedArtists embedded = wrapperResponse.getEmbeddedArtist();
                                nextUrl = getNextPage(wrapperResponse);
                                Log.d(LOG_TAG, "temp, loadInitial nextUrl: " + nextUrl);
                                List<Artist> artists = embedded.getArtists();
                                callback.onResult(artists, null, 1L);

                                networkState.postValue(NetworkState.LOADED);
                                initialLoading.postValue(NetworkState.LOADED);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ArtistWrapperResponse> call, Throwable t) {
                        initialLoading.postValue(NetworkState.LOADED);
                        networkState.postValue(NetworkState.LOADED);
                        Log.e(LOG_TAG, "Response code from initial load, onFailure: " + t.getMessage());
                    }
                });
    }

    private String getNextPage(ArtistWrapperResponse artistWrapperResponse) {
        PageLinks pageLinks = artistWrapperResponse.getLinks();
        if (pageLinks != null) {
            Next next = pageLinks.getNext();
            if (next != null) {
                return next.getHref();
            }
        }
        return null;
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Long> params, @NonNull LoadCallback<Long, Artist> callback) {
        // Ignore this, because we don't need to load anything before the initial load of data
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Long> params, @NonNull LoadCallback<Long, Artist> callback) {
        repository.getArtsyApi().getNextLinkForArtists(nextUrl).enqueue(new Callback<ArtistWrapperResponse>() {
            @Override
            public void onResponse(Call<ArtistWrapperResponse> call, Response<ArtistWrapperResponse> response) {
                if (response.isSuccessful()) {
                    ArtistWrapperResponse body = response.body();
                    if (body != null) {
                        EmbeddedArtists embeddedArtist = body.getEmbeddedArtist();
                        nextUrl = getNextPage(body);
                        Log.d(LOG_TAG, "temp, loadAfter nextUrl: " + nextUrl);
                        long nextKey;
                        if (params.key == embeddedArtist.getArtists().size()) {
                            nextKey = 0;
                        } else {
                            nextKey = params.key + 100;
                        }

                        List<Artist> artists = embeddedArtist.getArtists();
                        callback.onResult(artists, nextKey);

                        networkState.postValue(NetworkState.LOADED);
                        initialLoading.postValue(NetworkState.LOADED);
                    }
                }
                Log.d(LOG_TAG, "temp, nextUrl: " + nextUrl);
            }

            @Override
            public void onFailure(Call<ArtistWrapperResponse> call, Throwable t) {
                initialLoading.postValue(NetworkState.LOADED);
                networkState.postValue(NetworkState.LOADED);
                Log.e(LOG_TAG, "Response code from loadAfter, onFailure: " + t.getMessage());
            }
        });
    }

    public LiveData<NetworkState> getNetworkState() {
        return networkState;
    }

    public LiveData<NetworkState> getInitialLoading() {
        return initialLoading;
    }
}
