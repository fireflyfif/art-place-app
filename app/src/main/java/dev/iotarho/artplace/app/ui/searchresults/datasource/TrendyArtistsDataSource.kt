package dev.iotarho.artplace.app.ui.searchresults.datasource

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import dev.iotarho.artplace.app.model.artists.Artist
import dev.iotarho.artplace.app.model.artists.ArtistWrapperResponse
import dev.iotarho.artplace.app.repository.ArtsyRepository
import dev.iotarho.artplace.app.utils.Constants
import dev.iotarho.artplace.app.utils.NetworkState
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TrendyArtistsDataSource(private val repository: ArtsyRepository, private val offset: Int) : PageKeyedDataSource<Long, Artist?>() {

    val networkState: MutableLiveData<NetworkState> = MutableLiveData()
    val initialLoading: MutableLiveData<NetworkState> = MutableLiveData()
    private var nextUrl: String? = null

    companion object {
        private val LOG_TAG = TrendyArtistsDataSource::class.java.simpleName
    }


    override fun loadInitial(params: LoadInitialParams<Long>, callback: LoadInitialCallback<Long, Artist?>) {
        // Update NetworkState
        initialLoading.postValue(NetworkState.LOADING)
        networkState.postValue(NetworkState.LOADING)
        repository.artsyApi.getTrendingArtists(true, "-trending", offset, Constants.ArtworksFragment.INITIAL_SIZE_HINT, Constants.ArtworksFragment.PAGE_SIZE)
                .enqueue(object : Callback<ArtistWrapperResponse?> {
                    override fun onResponse(call: Call<ArtistWrapperResponse?>, response: Response<ArtistWrapperResponse?>) {
                        if (response.isSuccessful) {
                            val wrapperResponse = response.body()
                            if (wrapperResponse != null) {
                                val embedded = wrapperResponse.embeddedArtist
                                nextUrl = getNextPage(wrapperResponse)
                                Log.d(LOG_TAG, "temp, loadInitial nextUrl: $nextUrl")
                                val artists = embedded.artists
                                callback.onResult(artists, null, 1L)
                                networkState.postValue(NetworkState.LOADED)
                                initialLoading.postValue(NetworkState.LOADED)
                            }
                        }
                    }

                    override fun onFailure(call: Call<ArtistWrapperResponse?>, t: Throwable) {
                        initialLoading.postValue(NetworkState.LOADED)
                        networkState.postValue(NetworkState.LOADED)
                        Log.e(LOG_TAG, "Response code from initial load, onFailure: " + t.message)
                    }
                })
    }

    private fun getNextPage(artistWrapperResponse: ArtistWrapperResponse): String? {
        val pageLinks = artistWrapperResponse.links
        if (pageLinks != null) {
            val next = pageLinks.next
            if (next != null) {
                return next.href
            }
        }
        return null
    }

    override fun loadBefore(params: LoadParams<Long>, callback: LoadCallback<Long, Artist?>) {
        // Ignore this, because we don't need to load anything before the initial load of data
    }

    override fun loadAfter(params: LoadParams<Long>, callback: LoadCallback<Long, Artist?>) {
        repository.artsyApi.getNextLinkForArtists(nextUrl).enqueue(object : Callback<ArtistWrapperResponse?> {
            override fun onResponse(call: Call<ArtistWrapperResponse?>, response: Response<ArtistWrapperResponse?>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        val embeddedArtist = body.embeddedArtist
                        nextUrl = getNextPage(body)
                        Log.d(LOG_TAG, "temp, loadAfter nextUrl: $nextUrl")
                        val nextKey: Long = if (params.key.toInt() == embeddedArtist.artists.size) {
                            0
                        } else {
                            params.key + 100
                        }
                        val artists = embeddedArtist.artists
                        callback.onResult(artists, nextKey)
                        networkState.postValue(NetworkState.LOADED)
                        initialLoading.postValue(NetworkState.LOADED)
                    }
                }
                Log.d(LOG_TAG, "temp, nextUrl: $nextUrl")
            }

            override fun onFailure(call: Call<ArtistWrapperResponse?>, t: Throwable) {
                initialLoading.postValue(NetworkState.LOADED)
                networkState.postValue(NetworkState.LOADED)
                Log.e(LOG_TAG, "Response code from loadAfter, onFailure: " + t.message)
            }
        })
    }
}