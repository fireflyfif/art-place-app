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

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.android.artplace.model.Artwork;
import com.example.android.artplace.model.Embedded;
import com.example.android.artplace.ArtPlaceApp;
import com.example.android.artplace.remote.ArtsyApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArtsyRepository {

    private static final String TAG = ArtsyRepository.class.getSimpleName();

    private static ArtsyRepository INSTANCE;
    private ArtsyApiInterface mApiInterface;


    // Constructor of the Repository class
    public ArtsyRepository() {
        mApiInterface = makeApiCall();
    }

    public static ArtsyRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ArtsyRepository();
        }

        return INSTANCE;
    }

    /*
    Method that makes the call to the API
     */
    private static ArtsyApiInterface makeApiCall() {
        return ArtPlaceApp.create();
    }

    public void getArtworks(int itemSize) {

        mApiInterface.getEmbedded(itemSize).enqueue(new Callback<Embedded>() {
            Embedded embedded = new Embedded();
            List<Artwork> artworkList = new ArrayList<>();

            @Override
            public void onResponse(@NonNull Call<Embedded> call, @NonNull Response<Embedded> response) {
                if (response.isSuccessful()) {

                    embedded = response.body();
                    if (embedded != null) {
                        artworkList = embedded.getArtworks();

                        Log.d(TAG, "List of Artworks: " + artworkList.size());
                    }

                    Log.d(TAG, "Response code from initial load, onSuccess: " + response.code());
                } else {
                    Log.d(TAG, "Response code from initial load: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Embedded> call, @NonNull Throwable t) {

                Log.d(TAG, "Response code from initial load, onFailure: " + t.getMessage());
            }
        });

    }
}
