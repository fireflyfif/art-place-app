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
import com.example.android.artplace.model.Artists.Artist;
import com.example.android.artplace.model.Artists.EmbeddedArtists;
import com.example.android.artplace.model.ArtsyResponse;
import com.example.android.artplace.model.Artworks.ArtistsLink;
import com.example.android.artplace.model.Artworks.Artwork;
import com.example.android.artplace.model.Artworks.EmbeddedArtworks;
import com.example.android.artplace.model.ImageLinks;
import com.example.android.artplace.model.Links;
import com.example.android.artplace.model.Next;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Singleton pattern for the Repository class,
// best explained here: https://medium.com/exploring-code/how-to-make-the-perfect-singleton-de6b951dfdb0
public class ArtsyRepository {

    private static final String TAG = ArtsyRepository.class.getSimpleName();

    // With volatile variable all the write will happen on volatile sInstance
    // before any read of sInstance variable
    private static volatile ArtsyRepository INSTANCE;


    // Private constructor of the Repository class
    private ArtsyRepository() {

        // Prevent from the reflection api
        if (INSTANCE != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public static ArtsyRepository getInstance() {
        // Double check locking pattern
        if (INSTANCE == null) {

            // if there is no instance available, create a new one
            synchronized (ArtsyRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ArtsyRepository();
                }
            }
        }

        return INSTANCE;
    }

    public LiveData<List<Artist>> getArtist(String artworkId) {
        return loadArtist(artworkId);
    }

    public LiveData<List<Artist>> getArtistLink(String artistUrl) {
        return loadArtistLink(artistUrl);
    }

    private LiveData<List<Artist>> loadArtistLink(String artistUrl) {

        MutableLiveData<List<Artist>> artistLiveData = new MutableLiveData<>();

        ArtPlaceApp.getInstance().getArtsyApi().getArtistLink(artistUrl).enqueue(new Callback<ArtsyResponse>() {
            ArtsyResponse artsyResponse = new ArtsyResponse();
            List<Artwork> artworkList = new ArrayList<>();
            Artwork currentArtwork = new Artwork();
            ImageLinks imageLinks = new ImageLinks();
            EmbeddedArtists embeddedArtists = new EmbeddedArtists();
            List<Artist> artistList = new ArrayList<>();
            ArtistsLink artistsLink = new ArtistsLink();
            String artistUrlString;

            @Override
            public void onResponse(@NonNull Call<ArtsyResponse> call, @NonNull Response<ArtsyResponse> response) {
                if (response.isSuccessful()) {

                    artsyResponse = response.body();

                    if (artsyResponse != null) {

                        EmbeddedArtworks embeddedArtworks = artsyResponse.getEmbeddedArtworks();
                        artworkList = embeddedArtworks.getArtworks();

                        imageLinks = currentArtwork.getLinks();

                        if (artistsLink != null) {

                            if (imageLinks.getArtists() != null) {
                                artistsLink = imageLinks.getArtists();
                                artistUrlString = artistsLink.getHref();

                                Log.d(TAG, "Link to artist: " + artistUrlString);
                            }
                        }


                        if (embeddedArtists != null) {
                            artistList = embeddedArtists.getArtists();

                            artistLiveData.setValue(artistList);
                        }
                    }

                    Log.d(TAG, "Loaded successfully! " + response.code());

                } else {

                    artistLiveData.setValue(null);
                    Log.d(TAG, "Loaded NOT successfully! " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArtsyResponse> call, @NonNull Throwable t) {
                Log.d(TAG, "OnFailure! " + t.getMessage());
            }
        });

        return artistLiveData;
    }

    /**
     * Fetch data from the Artists endpoint with selected artwork ID
     *
     * @param artworkId is the current artwork ID
     */
    private LiveData<List<Artist>> loadArtist(String artworkId) {

        MutableLiveData<List<Artist>> artistLiveData = new MutableLiveData<>();

        // No need of a networkIO Executors here, as Retrofit is doing its call asynchronously
        // Get the Instance of the ArtPlaceApp to create the Retrofit call
        ArtPlaceApp.getInstance().getArtsyApi().getArtist(artworkId).enqueue(new Callback<EmbeddedArtists>() {
            EmbeddedArtists embeddedArtists = new EmbeddedArtists();
            List<Artist> artistList = new ArrayList<>();

            @Override
            public void onResponse(@NonNull Call<EmbeddedArtists> call, @NonNull Response<EmbeddedArtists> response) {
                if (response.isSuccessful()) {
                    embeddedArtists = response.body();

                    if (embeddedArtists != null) {
                        artistList = embeddedArtists.getArtists();

                        artistLiveData.setValue(artistList);
                    }
                    Log.d(TAG, "Loaded successfully! " + response.code());

                } else {
                    artistLiveData.setValue(null);
                    Log.d(TAG, "Loaded NOT successfully! " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<EmbeddedArtists> call, @NonNull Throwable t) {
                Log.d(TAG, "OnFailure! " + t.getMessage());
            }
        });

        // Return LiveData<Artist>
        return artistLiveData;
    }
}
