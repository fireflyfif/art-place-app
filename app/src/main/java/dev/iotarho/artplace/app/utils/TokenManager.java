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

package dev.iotarho.artplace.app.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;

import dev.iotarho.artplace.app.BuildConfig;
import dev.iotarho.artplace.app.callbacks.FetchTokenCallback;
import dev.iotarho.artplace.app.model.token.TypeToken;
import dev.iotarho.artplace.app.remote.ArtsyApiManager;
import dev.iotarho.artplace.app.remote.authentication.TokenService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Helper class for saving the token into SharedPreferences
 */
public class TokenManager {

    private static final String TAG = TokenManager.class.getSimpleName();

    private PreferenceUtils mPreferenceUtils;
    private static TokenManager INSTANCE;

    private TokenManager() {
        mPreferenceUtils = PreferenceUtils.getInstance();
    }

    public static synchronized TokenManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TokenManager();
        }
        return INSTANCE;
    }

    public String fetchTokenFirstTime() {
        Response<TypeToken> response;
        String firstTimeToken = null;
        String tokenExpirationDate = null;

        synchronized (this) {
            try {
                response = ArtsyApiManager
                        .createService(TokenService.class)
                        .refreshToken(BuildConfig.CLIENT_ID, BuildConfig.CLIENT_SECRET)
                        .execute();
                if (response.isSuccessful()) {
                    TypeToken typeToken = response.body();
                    firstTimeToken = typeToken.getToken();
                    tokenExpirationDate = typeToken.getExpiresAt();

                    Log.d(TAG, "saving token into preferences: " + typeToken.getToken());
                    mPreferenceUtils.saveToken(typeToken);
                }
            } catch (IOException e) {
                Log.e(TAG, "error fetching the token: " + e.getMessage());
            }
        }
        Log.d(TAG, "First time token fetched: " + firstTimeToken + " Expired at: " + tokenExpirationDate);
        return firstTimeToken;
    }

    public void fetchToken(FetchTokenCallback callback) {
        synchronized (this) {
            ArtsyApiManager.createService(TokenService.class).refreshToken(BuildConfig.CLIENT_ID, BuildConfig.CLIENT_SECRET)
                    .enqueue(new Callback<TypeToken>() {
                        @Override
                        public void onResponse(@NonNull Call<TypeToken> call, @NonNull Response<TypeToken> response) {
                            if (response.isSuccessful()) {
                                TypeToken tokenObject = response.body();
                                if (tokenObject != null) {
                                    String newToken = tokenObject.getToken();

                                    // Get the value of the token as a reference in the callback
                                    // source: https://stackoverflow.com/a/44881355/8132331
                                    if (callback != null) {
                                        callback.onSuccess(tokenObject);
                                        Log.d(TAG, "Token successfully fetched: " + newToken);
                                    }
                                    // Check the token
                                    Log.d(TAG, "Token saved into SharedPrefs: " + newToken);
                                    // Save the token into SharedPreferences
                                    mPreferenceUtils.saveToken(tokenObject);
                                }
                            } else {
                                Log.e(TAG, "Error when fetching the toke: %s" + response.message());
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<TypeToken> call, @NonNull Throwable t) {
                            Log.e(TAG, "Error when fetching the toke");
                            if (callback != null) {
                                Log.e(TAG, "error while fetching the token: " + t.getMessage());
                                callback.onError(t);
                            }
                        }
                    });
        }
    }
}

