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
package dev.iotarho.artplace.app.ui.artworks.datasource

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import dev.iotarho.artplace.app.model.artworks.Artwork
import dev.iotarho.artplace.app.model.artworks.ArtworkWrapperResponse
import dev.iotarho.artplace.app.repository.ArtsyRepository
import dev.iotarho.artplace.app.utils.NetworkState
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ArtworkDataSource(private val repository: ArtsyRepository) : PageKeyedDataSource<Long, Artwork?>() {

    private val networkStateMutable: MutableLiveData<NetworkState> = MutableLiveData()
    val networkState: LiveData<NetworkState> = networkStateMutable

    private val initLoadingMutable: MutableLiveData<NetworkState> = MutableLiveData()
    val initialLoading: LiveData<NetworkState> = initLoadingMutable
    private var mNextUrl: String? = null


    companion object {
        private val TAG = ArtworkDataSource::class.java.simpleName
    }


    override fun loadInitial(params: LoadInitialParams<Long>, callback: LoadInitialCallback<Long, Artwork?>) {
        // Update NetworkState
        initLoadingMutable.postValue(NetworkState.LOADING)
        networkStateMutable.postValue(NetworkState.LOADING)
        repository.artsyApi.getArtworksData(params.requestedLoadSize).enqueue(object : Callback<ArtworkWrapperResponse?> {
            override fun onResponse(call: Call<ArtworkWrapperResponse?>, response: Response<ArtworkWrapperResponse?>) {
                when {
                    response.isSuccessful -> {
                        val artworkWrapperResponse = response.body()
                        if (artworkWrapperResponse != null) {
                            val embeddedArtworks = artworkWrapperResponse.embeddedArtworks
                            mNextUrl = getNextPage(artworkWrapperResponse)
                            val artworkList = embeddedArtworks.artworks
                            callback.onResult(artworkList, null, 2L)
                            Log.d(TAG, "List of Artworks loadInitial : " + artworkList.size)
                            networkStateMutable.postValue(NetworkState.LOADED)
                            initLoadingMutable.postValue(NetworkState.LOADED)
                        }
                        Log.d(TAG, "Response code from initial load, onSuccess: " + response.code())
                    }
                    response.code() == 401 -> {
                        Log.d(TAG, "Error from the server message " + response.message())
                    }
                    response.code() == 504 -> { // todo: handle the timeout properly
                        initLoadingMutable.postValue(NetworkState(NetworkState.Status.FAILED))
                        networkStateMutable.postValue(NetworkState(NetworkState.Status.FAILED))
                        Log.d(TAG, "Response code from initial load: " + response.code())
                    }
                    else -> {
                        initLoadingMutable.postValue(NetworkState(NetworkState.Status.FAILED))
                        networkStateMutable.postValue(NetworkState(NetworkState.Status.FAILED))
                        Log.d(TAG, "Response code from initial load: " + response.code())
                    }
                }
            }

            override fun onFailure(call: Call<ArtworkWrapperResponse?>, t: Throwable) {
                networkStateMutable.postValue(NetworkState(NetworkState.Status.FAILED))
                Log.d(TAG, "Response code from initial load, onFailure: " + t.message)
            }
        })
    }

    private fun getNextPage(artworkWrapperResponse: ArtworkWrapperResponse): String? {
        val pageLinks = artworkWrapperResponse.pageLinks
        if (pageLinks != null) {
            val next = pageLinks.next
            if (next != null) {
                return next.href
            }
            Log.d(TAG, "loadInitial: Next page link: $mNextUrl")
        }
        return null
    }

    override fun loadBefore(params: LoadParams<Long>, callback: LoadCallback<Long, Artwork?>) {
        // Ignore this, because we don't need to load anything before the initial load of data
    }

    override fun loadAfter(params: LoadParams<Long>, callback: LoadCallback<Long, Artwork?>) {
        Log.i(TAG, "Loading: " + params.key + " Count: " + params.requestedLoadSize)

        // Set Network State to Loading
        networkStateMutable.postValue(NetworkState.LOADING)
        repository.artsyApi.getNextLink(mNextUrl, params.requestedLoadSize).enqueue(object : Callback<ArtworkWrapperResponse?> {
            override fun onResponse(call: Call<ArtworkWrapperResponse?>, response: Response<ArtworkWrapperResponse?>) {
                if (response.isSuccessful) {
                    val artworkWrapperResponse = response.body()
                    if (artworkWrapperResponse != null) {
                        val embeddedArtworks = artworkWrapperResponse.embeddedArtworks
                        mNextUrl = getNextPage(artworkWrapperResponse)
                        var artworkList: List<Artwork?> = ArrayList()
                        if (embeddedArtworks.artworks != null) {
                            //long nextKey = params.key == 100 ? null: params.key + 30;
                            val nextKey: Long = if (params.key.toInt() == embeddedArtworks.artworks.size) {
                                0
                            } else {
                                params.key + 100
                            }
                            Log.d(TAG, "Next key : $nextKey")
                            artworkList = embeddedArtworks.artworks
                            callback.onResult(artworkList, nextKey)
                            Log.d(TAG, "List of Artworks loadAfter : " + artworkList.size)
                        }
                        Log.d(TAG, "List of Artworks loadInitial : " + artworkList.size)
                        networkStateMutable.postValue(NetworkState.LOADED)
                        initLoadingMutable.postValue(NetworkState.LOADED)
                    }
                }
            }

            override fun onFailure(call: Call<ArtworkWrapperResponse?>, t: Throwable) {
                networkStateMutable.postValue(NetworkState(NetworkState.Status.FAILED))
                Log.d(TAG, "Response code from initial load, onFailure: " + t.message)
            }
        })
    }
}