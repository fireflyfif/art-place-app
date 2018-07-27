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

package com.example.android.artplace.datasource;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PageKeyedDataSource;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.android.artplace.ArtPlaceApp;
import com.example.android.artplace.model.Artworks.Artwork;
import com.example.android.artplace.model.Artworks.EmbeddedArtworks;
import com.example.android.artplace.utils.NetworkState;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArtworkDataSource extends PageKeyedDataSource<Long, Artwork> {

    private static final String TAG = ArtworkDataSource.class.getSimpleName();

    private ArtPlaceApp mAppController;

    private final MutableLiveData<NetworkState> mNetworkState;
    private final MutableLiveData<NetworkState> mInitialLoading;


    public ArtworkDataSource(ArtPlaceApp appController) {
        mAppController = appController;

        mNetworkState = new MutableLiveData();
        mInitialLoading = new MutableLiveData();
    }

    public MutableLiveData getNetworkState() {
        return mNetworkState;
    }

    public MutableLiveData getInitialLoading() {
        return mInitialLoading;
    }


    @Override
    public void loadInitial(@NonNull LoadInitialParams<Long> params, @NonNull LoadInitialCallback<Long, Artwork> callback) {

        // Update NetworkState
        mInitialLoading.postValue(NetworkState.LOADING);
        mNetworkState.postValue(NetworkState.LOADING);

        // Make the Retrofit call to the API
        mAppController.getArtsyApi().getEmbedded(params.requestedLoadSize).enqueue(new Callback<EmbeddedArtworks>() {
            EmbeddedArtworks embeddedArtworks = new EmbeddedArtworks();
            List<Artwork> artworkList = new ArrayList<>();

            @Override
            public void onResponse(@NonNull Call<EmbeddedArtworks> call, @NonNull Response<EmbeddedArtworks> response) {
                if (response.isSuccessful()) {

                    embeddedArtworks = response.body();
                    if (embeddedArtworks != null) {
                        callback.onResult(artworkList = embeddedArtworks.getArtworks(), null, 2L);

                        Log.d(TAG, "List of Artworks loadInitial : " + artworkList.size());

                        mNetworkState.postValue(NetworkState.LOADED);
                        mInitialLoading.postValue(NetworkState.LOADED);
                    }

                    Log.d(TAG, "Response code from initial load, onSuccess: " + response.code());
                } else {

                    mInitialLoading.postValue(new NetworkState(NetworkState.Status.FAILED));
                    mNetworkState.postValue(new NetworkState(NetworkState.Status.FAILED));

                    Log.d(TAG, "Response code from initial load: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<EmbeddedArtworks> call, @NonNull Throwable t) {

                mNetworkState.postValue(new NetworkState(NetworkState.Status.FAILED));
                Log.d(TAG, "Response code from initial load, onFailure: " + t.getMessage());
            }
        });

    }

    @Override
    public void loadBefore(@NonNull LoadParams<Long> params, @NonNull LoadCallback<Long, Artwork> callback) {

        // Ignore this, because we don't need to load anything before the initial load of data
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Long> params, @NonNull LoadCallback<Long, Artwork> callback) {

        Log.i(TAG, "Loading: " + params.key + " Count: " + params.requestedLoadSize);

        // Set Network State to Loading
        mNetworkState.postValue(NetworkState.LOADING);

        // Make the Retrofit call to the API
        mAppController.getArtsyApi().getEmbedded(params.requestedLoadSize).enqueue(new Callback<EmbeddedArtworks>() {
            EmbeddedArtworks embeddedArtworks = new EmbeddedArtworks();
            List<Artwork> artworkList = new ArrayList<>();

            @Override
            public void onResponse(@NonNull Call<EmbeddedArtworks> call, @NonNull Response<EmbeddedArtworks> response) {
                if (response.isSuccessful()) {
                    if (embeddedArtworks != null) {
                        embeddedArtworks = response.body();

                        if (embeddedArtworks.getArtworks() != null) {
                            //long nextKey = params.key == 100 ? null: params.key + 30;
                            long nextKey;
                            // TODO: Getting repeated artworks???
                            if (params.key == embeddedArtworks.getArtworks().size()) {
                                nextKey = 0;
                            } else {
                                nextKey = params.key + 1;
                            }

                            Log.d(TAG, "Next key : " + nextKey);
                            callback.onResult(artworkList = embeddedArtworks.getArtworks(), nextKey);

                            Log.d(TAG, "List of Artworks loadAfter : " + artworkList.size());
                        }

                        mNetworkState.postValue(NetworkState.LOADED);
                    }

                    Log.d(TAG, "Response code from initial load, onSuccess: " + response.code());
                } else {

                    mNetworkState.postValue(new NetworkState(NetworkState.Status.FAILED));

                    Log.d(TAG, "Response code from initial load: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<EmbeddedArtworks> call, Throwable t) {

                mNetworkState.postValue(new NetworkState(NetworkState.Status.FAILED));
                Log.d(TAG, "Response code from initial load, onFailure: " + t.getMessage());
            }
        });

    }
}
