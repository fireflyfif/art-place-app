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

import com.example.android.artplace.BuildConfig;
import com.example.android.artplace.model.Artwork;
import com.example.android.artplace.model.Embedded;
import com.example.android.artplace.remote.ArtPlaceApp;
import com.example.android.artplace.remote.ArtsyApiManager;
import com.example.android.artplace.repository.ArtsyRepository;
import com.example.android.artplace.utils.NetworkState;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArtworkDataSource extends PageKeyedDataSource<Long, Artwork> {

    private static final String TAG = ArtworkDataSource.class.getSimpleName();

    //private ArtsyApiInterface mArtsyApi;

    //private ArtPlaceApp artPlaceApp;

    private ArtsyRepository mArtsyRepository;
    private ArtsyApiManager mApiManager;

    private MutableLiveData<NetworkState> mNetworkState;
    private MutableLiveData<NetworkState> mInitialLoading;

    private String mToken;
    //private int mItemSize;

    public ArtworkDataSource(ArtsyApiManager apiManager, String token) {
        mApiManager = apiManager;
        mArtsyRepository = ArtsyRepository.getInstance(apiManager);

        mNetworkState = new MutableLiveData();
        mInitialLoading = new MutableLiveData();

        mToken = token;
        //mItemSize = itemSize;
    }

    public MutableLiveData<NetworkState> getNetworkState() {
        return mNetworkState;
    }

    public MutableLiveData<NetworkState> getInitialLoading() {
        return mInitialLoading;
    }

    /**
     * This id field is unique for each artwork
     *
     * @param
     * @return
     *//*
    @NonNull
    @Override
    public Long getKey(@NonNull Artwork item) {
        return item.getId();
    }*/

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Long> params, @NonNull LoadInitialCallback<Long, Artwork> callback) {

        final Embedded embedded = new Embedded();
        final List<Artwork> artworkList = new ArrayList<>(); // size = 0 ?

        // Add NetworkState
        mInitialLoading.postValue(NetworkState.LOADING);
        mNetworkState.postValue(NetworkState.LOADING);

        mArtsyRepository.getArtworks(mToken, params.requestedLoadSize);

        if (embedded.getArtworks() != null) {
            artworkList.addAll(embedded.getArtworks());
            callback.onResult(artworkList, null, 2L);

            mInitialLoading.postValue(NetworkState.LOADED);
            mNetworkState.postValue(NetworkState.LOADED);
        } else {
            mInitialLoading.postValue(new NetworkState(NetworkState.Status.FAILED));
            mNetworkState.postValue(new NetworkState(NetworkState.Status.FAILED));
        }


        /*artPlaceApp.getArtsyApi().getEmbedded(BuildConfig.TOKEN, params.requestedLoadSize)
                .enqueue(new Callback<Embedded>() {
                    @Override
                    public void onResponse(@NonNull Call<Embedded> call, @NonNull Response<Embedded> response) {
                        if (response.isSuccessful()) {

                            if (embedded.getArtworks() != null) {
                                artworkList.addAll(embedded.getArtworks());

                                callback.onResult(response.body().getArtworks(), null, 2l);

                                mInitialLoading.postValue(NetworkState.LOADED);
                                mNetworkState.postValue(NetworkState.LOADED);
                            }

                            Log.d(TAG, "Response code from initial load, onSuccess: " + response.code());

                        } else {

                            mInitialLoading.postValue(new NetworkState(NetworkState.Status.FAILED, response.message()));
                            mNetworkState.postValue(new NetworkState(NetworkState.Status.FAILED, response.message()));

                            Log.d(TAG, "Response code from initial load: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<Embedded> call, Throwable t) {
                        mNetworkState.postValue(new NetworkState(NetworkState.Status.FAILED, t.getMessage()));

                        Log.d(TAG, "Response code from initial load, onFailure: " + t.getMessage());
                    }
                });*/
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Long> params, @NonNull LoadCallback<Long, Artwork> callback) {

        // Ignore this, because we don't need to load anything before the initial load of data
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Long> params, @NonNull LoadCallback<Long, Artwork> callback) {

        Log.i(TAG, "Loading " + params.key + "Count " + params.requestedLoadSize);

        final Embedded embedded = new Embedded();
        final List<Artwork> artworkList = new ArrayList<>();

        // Add Network State
        mNetworkState.postValue(NetworkState.LOADING);

        mArtsyRepository.getArtworks(mToken, params.requestedLoadSize);

        if (embedded.getArtworks() != null) {
            artworkList.addAll(embedded.getArtworks());

            long nextKey = (params.key == embedded.getArtworks().size()) ? null: params.key + 1;
            callback.onResult(artworkList, nextKey);

            mNetworkState.postValue(NetworkState.LOADED);
        } else {
            mNetworkState.postValue(new NetworkState(NetworkState.Status.FAILED));
        }



        /*artPlaceApp.getArtsyApi().getEmbedded(BuildConfig.TOKEN, params.requestedLoadSize)
                .enqueue(new Callback<Embedded>() {
                    @Override
                    public void onResponse(@NonNull Call<Embedded> call, @NonNull Response<Embedded> response) {
                        // TODO: To complete!
                        if (embedded.getArtworks() != null) {
                            artworkList.addAll(embedded.getArtworks());

                            long artworkId = (params.key == embedded.getArtworks().size()) ? null: params.key + 1;
                            callback.onResult(response.body().getArtworks(), artworkId);

                            mNetworkState.postValue(NetworkState.LOADED);
                        }

                    }

                    @Override
                    public void onFailure(Call<Embedded> call, Throwable t) {

                        mNetworkState.postValue(new NetworkState(NetworkState.Status.FAILED, t.getMessage()));

                        Log.d(TAG, "Response code: " + t.getMessage());
                    }
                });*/
    }
}
