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
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.android.artplace.AppExecutors;
import com.example.android.artplace.ArtPlaceApp;
import com.example.android.artplace.datasource.ArtworkDataSourceFactory;
import com.example.android.artplace.model.Artists.Artist;
import com.example.android.artplace.model.Artists.EmbeddedArtists;
import com.example.android.artplace.remote.ArtsyApiInterface;
import com.example.android.artplace.remote.ArtsyApiManager;
import com.example.android.artplace.utils.NetworkState;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArtsyRepository {

    private static final String TAG = ArtsyRepository.class.getSimpleName();

    private static ArtsyRepository INSTANCE;
    private static ArtsyApiInterface sApiInterface;
    private ArtPlaceApp mAppController;

    private LiveData<NetworkState> mNetworkState;
    private LiveData<NetworkState> mInitialLoading;


    // Constructor of the Repository class
    public ArtsyRepository(ArtPlaceApp appController) {
        mAppController = appController;
    }

    public static ArtsyRepository getInstance() {
        if (INSTANCE == null) {
            //INSTANCE = new ArtsyRepository(sApiInterface);
        }
        return INSTANCE;
    }

    /*
    Method that makes the call to the API
     */
    private static ArtsyApiInterface makeApiCall() {
        return ArtsyApiManager.create();
    }

    public LiveData<List<Artist>> getArtist(String artworkId) {
        return loadArtist(artworkId);
    }

    /**
     * Fetch data from the Artists endpoint with selected artwork ID
     *
     * @param artworkId is the current artwork ID
     */
    private LiveData<List<Artist>> loadArtist(String artworkId) {

        MutableLiveData<List<Artist>> artistLiveData = new MutableLiveData<>();

        AppExecutors.networkIO().execute(() -> {
            mAppController.getArtsyApi().getArtist(artworkId).enqueue(new Callback<EmbeddedArtists>() {
                EmbeddedArtists embeddedArtists = new EmbeddedArtists();

                @Override
                public void onResponse(Call<EmbeddedArtists> call, Response<EmbeddedArtists> response) {
                    if (response.isSuccessful()) {
                        embeddedArtists = response.body();

                        if (embeddedArtists != null) {
                            //artistList = artists.getArtists();
                            artistLiveData.setValue(embeddedArtists.getArtists());
                        }
                        Log.d(TAG, "Loaded successfully! " + response.code());

                    } else {
                        artistLiveData.setValue(null);
                        Log.d(TAG, "Loaded NOT successfully! " + response.code());
                    }
                }
                @Override
                public void onFailure(Call<EmbeddedArtists> call, Throwable t) {
                    Log.d(TAG, "OnFailure! " + t.getMessage());
                }
            });

        });

        // TODO: Make it return LiveData<Artist>
        return artistLiveData;
    }
}
