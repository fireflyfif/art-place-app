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

package com.example.android.artplace.repository;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.android.artplace.AppExecutors;
import com.example.android.artplace.datasource.ArtworkDataSourceFactory;
import com.example.android.artplace.model.Artworks.Embedded;
import com.example.android.artplace.remote.ArtsyApiInterface;
import com.example.android.artplace.remote.ArtsyApiManager;
import com.example.android.artplace.utils.NetworkState;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArtsyRepository {

    private static final String TAG = ArtsyRepository.class.getSimpleName();

    private static ArtsyRepository INSTANCE;
    private static ArtsyApiInterface sApiInterface;
    private ArtworkDataSourceFactory mDataSourceFactory;

    private LiveData<NetworkState> mNetworkState;
    private LiveData<NetworkState> mInitialLoading;


    // Constructor of the Repository class
    public ArtsyRepository(ArtsyApiInterface apiInterface) {
        sApiInterface = apiInterface;
    }

    public static ArtsyRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ArtsyRepository(sApiInterface);
        }
        return INSTANCE;
    }

    /*
    Method that makes the call to the API
     */
    private static ArtsyApiInterface makeApiCall() {
        return ArtsyApiManager.create();
    }

    /**
     * Fetch data from the Artists endpoint with selected artwork ID
     *
     * @param artworkId is the current artwork ID
     */
    private void loadArtist(String artworkId) {
        AppExecutors.networkIO().execute(() -> {
            sApiInterface.getArtist(artworkId).enqueue(new Callback<Embedded>() {
                @Override
                public void onResponse(@NonNull Call<Embedded> call, @NonNull Response<Embedded> response) {
                    if (response.isSuccessful()) {
                        Artwork
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Embedded> call, @NonNull Throwable t) {

                }
            });

        });
    }

}
