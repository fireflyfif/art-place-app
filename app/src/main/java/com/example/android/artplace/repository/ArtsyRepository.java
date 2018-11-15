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

import com.example.android.artplace.ArtPlaceApp;
import com.example.android.artplace.model.artists.ArtistWrapperResponse;
import com.example.android.artplace.model.artists.Artist;
import com.example.android.artplace.model.artists.EmbeddedArtists;
import com.example.android.artplace.model.artworks.Artwork;
import com.example.android.artplace.model.artworks.ArtworkWrapperResponse;
import com.example.android.artplace.model.artworks.EmbeddedArtworks;
import com.example.android.artplace.model.token.TypeToken;
import com.example.android.artplace.utils.TokenManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.android.artplace.BuildConfig.CLIENT_ID;
import static com.example.android.artplace.BuildConfig.CLIENT_SECRET;

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

    private String getNewToken() {
        String token = null;
        
        ArtPlaceApp.getInstance().getToken().refreshToken(CLIENT_ID, CLIENT_SECRET)
                .enqueue(new Callback<TypeToken>() {
            @Override
            public void onResponse(@NonNull Call<TypeToken> call, @NonNull Response<TypeToken> response) {
                // Get the token here

            }

            @Override
            public void onFailure(@NonNull Call<TypeToken> call, @NonNull Throwable t) {

            }
        });
        
        return token;
    }


    /*
    Getter method to load the Similar Artworks
     */
    public LiveData<List<Artwork>> getSimilarArtFromLink(String similarArtUrl) {
        return loadSimilarArtworks(similarArtUrl);
    }

    /**
     * Method for accessing the Similar artworks endpoint from the Artsy API
     *
     * @param similarArtUrl is the Url to the Similar Artworks results
     * @return loaded results from the Artsy response
     */
    private LiveData<List<Artwork>> loadSimilarArtworks(String similarArtUrl) {

        MutableLiveData<List<Artwork>> similarArtData = new MutableLiveData<>();

        ArtPlaceApp.getInstance().getArtsyApi().getSimilarArtLink(similarArtUrl)
                .enqueue(new Callback<ArtworkWrapperResponse>() {

                    ArtworkWrapperResponse artworkWrapper = new ArtworkWrapperResponse();
                    EmbeddedArtworks embeddedArtworks = new EmbeddedArtworks();
                    List<Artwork> similarArtList = new ArrayList<>();

                    @Override
                    public void onResponse(@NonNull Call<ArtworkWrapperResponse> call,
                                           @NonNull Response<ArtworkWrapperResponse> response) {

                        if (response.isSuccessful()) {
                            artworkWrapper = response.body();
                            if (artworkWrapper != null) {
                                embeddedArtworks = artworkWrapper.getEmbeddedArtworks();

                                similarArtList = embeddedArtworks.getArtworks();
                                similarArtData.setValue(similarArtList);
                            }
                            Log.d(TAG, "Similar artworks loaded successfully! " + response.code());
                        } else {
                            similarArtData.setValue(null);
                            Log.d(TAG, "Similar artworks loaded NOT successfully! " + response.code());
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<ArtworkWrapperResponse> call, @NonNull Throwable t) {
                        Log.d(TAG, "OnFailure! " + t.getMessage());
                    }
                });

        return similarArtData;
    }

    public LiveData<List<Artist>> getArtistFromLink(String artistUrl) {
        return loadArtistFromLink(artistUrl);
    }

    private LiveData<List<Artist>> loadArtistFromLink(String artistUrl) {

        MutableLiveData<List<Artist>> artistLiveData = new MutableLiveData<>();

        ArtPlaceApp.getInstance().getArtsyApi().getArtistLink(artistUrl)
                .enqueue(new Callback<ArtistWrapperResponse>() {
                    ArtistWrapperResponse artistWrapperResponse = new ArtistWrapperResponse();
                    EmbeddedArtists embeddedArtists = new EmbeddedArtists();
                    List<Artist> artistList = new ArrayList<>();

                    @Override
                    public void onResponse(@NonNull Call<ArtistWrapperResponse> call,
                                           @NonNull Response<ArtistWrapperResponse> response) {
                        if (response.isSuccessful()) {

                            artistWrapperResponse = response.body();

                            if (artistWrapperResponse != null) {
                                embeddedArtists = artistWrapperResponse.getEmbeddedArtist();

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
                    public void onFailure(@NonNull Call<ArtistWrapperResponse> call, @NonNull Throwable t) {
                        Log.d(TAG, "OnFailure! " + t.getMessage());
                    }
                });

        return artistLiveData;
    }

}
